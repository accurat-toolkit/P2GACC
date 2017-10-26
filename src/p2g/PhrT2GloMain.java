package p2g;


import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
// import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


class PhrT2GloMain {
	
	HashMap <String, Boolean> termhash;   // used to hash the terms, to avoid duplicates
	
	FilterInput fin;
	TermExtract tex;
	Stoplist stl;
	
	FilterExceptions slexc;
	FilterExceptions tlexc;
		
	SimpleTokeniser sltok;  // normaliser
	SimpleTokeniser tltok;  
	
	LexInfo sllex;		// lemmatiser and defaulter lexicon-info
	LexInfo tllex;
	
	MWFilter slmwf;	// Multiword Filter
	MWFilter tlmwf; 
	
	CreateLexEntry slcle;
	CreateLexEntry tlcle;

	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xx Konstruktor
	PhrT2GloMain () {}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxx loadResources
	public void loadResources (String slang, String tlang, String datapath) 
							  throws IOException {
				
		slang = slang.trim();
		slang = slang.toLowerCase();
		tlang = tlang.trim();
		tlang = tlang.toLowerCase();
				
		if (!(datapath.endsWith("/"))) 
			datapath = datapath + "/";
		
		//load resources:
		//  simpletokeniser needs:  normaliserfiles
		//  lemmatiser      needs:  BLF, DCP
		
		SimpleTokeniser sltok = new SimpleTokeniser (datapath, slang);   // normaliser
		this.sltok = sltok;
		SimpleTokeniser tltok = new SimpleTokeniser (datapath, tlang);
		this.tltok = tltok;
		
		LexInfo sllex = new LexInfo (datapath, slang);		// resources for lemmatiser and defaulter
		this.sllex = sllex;
		LexInfo tllex = new LexInfo (datapath, tlang);
		this.tllex = tllex;
				
		//  mwfilter needs: mwpatterns
		MWFilter slmwf = new MWFilter (datapath, slang);	// Multiword Filter
		this.slmwf = slmwf;
		MWFilter tlmwf = new MWFilter (datapath, tlang);
		this.tlmwf = tlmwf;
			
		//createlexEntry needs: Gender-Defaulter, Adj-Flexer, Exceptions
		CreateLexEntry slcle = new CreateLexEntry (datapath, slang);
		this.slcle = slcle;
		CreateLexEntry tlcle = new CreateLexEntry (datapath, tlang);
		this.tlcle = tlcle;
		
		FilterExceptions slexc = new FilterExceptions (slang);
		this.slexc = slexc;
		FilterExceptions tlexc = new FilterExceptions (tlang);
		this.tlexc = tlexc;
			
		// other objects
		TermExtract tex = new TermExtract (sltok, tltok, sllex, tllex,  
				slmwf, tlmwf,slexc, tlexc, slcle, tlcle);
		this.tex = tex;
		
		FilterInput fin = new FilterInput ();
		this.fin = fin;
		
		Stoplist stl = new Stoplist (datapath);
		this.stl = stl;
		

	}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	/** doExtraction
	// Main:
	// 	read a line
	// 	candidate = createBitext (line)     // different versions
	// 	lexentry = termExtract (candidate)
	// 	if not empty (lexentry)
	//  		filter for stopwords / known …
	//  	    	write if filter is passed
	 **/
	 

	public String doExtraction (String infile, String outfile, String datapath,
							  String informat, String slang, String tlang,
							  String frqthreshold, Boolean verbose, Boolean hashtheterms,
							  String lexstoplist) 
							throws IOException {
		
		int nrlines = 0;
		int wrlines = 0;
		
		
		if (!lexstoplist.isEmpty()) {
			this.stl.loadStoplist(lexstoplist);
		}

		termhash = new HashMap <String, Boolean> ();
		
			BufferedReader in = new BufferedReader (new InputStreamReader 
					(new FileInputStream (new File (infile)), "UTF8"));

			// BOM-treatment
			Integer firstCharOfStream = in.read();
			if(firstCharOfStream != 0xFEFF) { 
				// System.out.println ("xxxxxxxxxxx no BOM in " + infile + "xxxxxxxxxxx"); 
				in.close();		// 1. char zurück
				in = new BufferedReader (new InputStreamReader 
						(new FileInputStream (new File (infile)), "UTF8"));
			}
	
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "UTF8"));
			// BOM treatment
			out.write(0xFEFF);
					
				// Main loop: Read PhraseTable records
			String inline = "";			
			while ((inline = in.readLine()) != null) {

				nrlines++;
				
				// Extraction of BiTexts
				BiText currentbitext = new BiText ();
				currentbitext = fin.createBitext(inline, informat, frqthreshold);
				
				// no bitext: continue
				if (currentbitext.sltext.isEmpty())
					continue;
				
				// otherwise: do Term Extraction from Bitext
				String lexentry = tex.extractTerm(currentbitext, slang, tlang, verbose, 
						                          sllex, tllex, slexc, tlexc, 
						                          slcle, tlcle);
				
				// if there is a term:  compare to stoplist
				// lexentry: if not verbose = sllemma \t slpos \t tllemma \t tlpos \t slfeatures \t tlfeatures \r\n
				//           if verbose     = sllemma \t slpos \t tllemma \t tlpos \r\n
				
				if (!(lexentry.isEmpty())) {
					
					if (!(lexstoplist.isEmpty())) {						
						String [] stopentry = lexentry.split("\t");
						stopentry[3] = stopentry[3].replace("\r\n", "");
							// stoplistformat = sllemma \t slpos \t tllemma \t tlpos
						String stopcompare = stopentry[0] + "\t" + stopentry[1] + "\t" + stopentry[2] + "\t" + stopentry[3];
						if (stl.isInStoplist(stopcompare))
							lexentry = "";
					}
					
					// hash the terms to avoid dublettes - or write verbose
					if (!(hashtheterms)) {		// output including inputline 
						out.newLine();
						out.write("******   " + inline);
						out.newLine();
						out.write(lexentry);
						out.newLine(); 
						out.flush();
						wrlines++;
					}
					else {
						if (!(termhash.containsKey(lexentry)))
							termhash.put(lexentry, true);
					}					
				}
			}  // end of main loop
			
			// Output termhash
			if (hashtheterms) {
				Set<String> allkeys = termhash.keySet();
				Iterator<String> it = allkeys.iterator();
			
				while (it.hasNext()) {
					String outf = it.next(); 								
					out.write(outf);
					out.newLine(); 
					out.flush();
					wrlines++;
				}
			}

			in.close();
			out.close();
		return (infile + ": " + nrlines + " read, " + wrlines + " written\n");
	}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public static void main (String [] args) {
	

		String infile = "";
		String outfile = "";
		String slang = "";			// one of: en fr es de it pt
		String tlang = "";			// one of: en fr es de it pt 
		String datapath = "";
		String informat = "";		// one of: phrasetable anymalign termlist pexacc
		String frqthreshold = ""; 	// this is a FREQUENCY for AnymAlign but a PROBABILITY for PhraseTables
		String lexstoplist = ""; 
		
		Boolean verbose;
		Boolean hashtheterms;


		// for args
		if (args.length < 7) {
			System.out.println ("Usage: 1 = infile, 2 = outfile, 3 = slang  4 = tlang " +
					                  " 5 = datapath 6 = informat, 7 = threshold, 8 = lexstoplist (optional)");
			System.exit(-1);
		}
		else {
			infile = args[0];
			outfile = args[1];
			slang = args[2];
			tlang = args[3];
			datapath = args[4];
			informat = args[5];
			frqthreshold = args[6];
			if (args.length == 8)
				lexstoplist = args[7];
		}

		
		verbose = false;
		hashtheterms = true;
		
		
/*
	// for direct
		slang = "de";
		tlang = "en";
		datapath = "f:/projekte/working/openp2g/data";
		
		informat = "phrasetable";     // phrasetable | anymalign | termlist"
		frqthreshold = "0.5";

		infile = "F:/temp/phrtestin.txt";
		outfile = "F:/temp/phrtestout.txt";
*/
		
/*		
		informat = "pexacc";
		frqthreshold = "0.1";
		infile = "f:/temp/auto-v2-pexout.txt";
		outfile = "f:/temp/auto-v2-pexterm-01.txt";
*/
/*		
		informat = "anymalign";
		slang = "de";
		tlang = "en";
		frqthreshold = "1";      // this is a frequency ...
		infile =  "f:/temp/p2g/automotive-anymaligniert-anfang.txt";
		outfile = "f:/temp/p2g/automotive-anymaligniert-anfang-out.txt";
*/
/*		informat = "termlist";   // sl - tl - frq
		frqthreshold = "1";      // this is a probability!
		infile = "f:/temp/p2g/anymalign-veras-arbeit.txt";
		outfile = "f:/temp/p2g/anymalign-veras-arbeit-out.txt";
*/		
		// Check parameters
		slang = slang.trim();
		slang = slang.toLowerCase();
		if (!(slang.equals("en") || slang.equals("fr") || slang.equals("es") || slang.equals("de")
				|| slang.equals("it") || slang.equals("pt"))) {
			System.out.println ("Unknown Source language: " + slang);
			System.exit(-1);
		}
		
		tlang = tlang.trim();
		tlang = tlang.toLowerCase();
		if (!(tlang.equals("en") || tlang.equals("fr") || tlang.equals("es") || tlang.equals("de")
				|| tlang.equals("it") || tlang.equals("pt"))) {
			System.out.println ("Unknown target language: " + tlang);
			System.exit(-1);
		}
		
		informat = informat.trim();
		informat = informat.toLowerCase();
		if (!(informat.equals("phrasetable") || informat.equals("anymalign") 
				|| informat.equals("pexacc") || informat.equals("termlist"))) {
			System.out.println ("Unknown input format: " + informat);
			System.exit(-1);
		}

//		System.out.println ("Time " + System.currentTimeMillis());
		
		try {
			PhrT2GloMain p2g = new PhrT2GloMain ();
			p2g.loadResources(slang, tlang, datapath);
		
			String finalmsg = p2g.doExtraction(infile, outfile, datapath, informat, slang, tlang, 
				         					frqthreshold, verbose, hashtheterms, lexstoplist);
 			System.out.println (finalmsg);
//			System.out.println ("Time " + System.currentTimeMillis());
		
		}  catch (IOException e) {
		System.err.println ("IO-Error: " + e);
		System.exit (-1); 
		} finally { }
	}	
	
}