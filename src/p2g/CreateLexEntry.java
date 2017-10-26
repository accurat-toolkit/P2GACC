package p2g;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

	/************************
	 create Lemma. 
	 receives: a termlist nodes of <originalstring, casing, lemma, POS>
	 does: - create entry, including
	 	   - create lemma
	 			- proper casing
	 			- proper inflection of parts
	 	   - create POS
	 	   - create lemmaseq, create POSseq
*/

class LexEntry {
	String canonicalform;
	String lemma;
	String tag;
	String gender;
	String entrytype;
	String headpos;
	String lemmasequence;
	String POSsequence;
}

public class CreateLexEntry {
	
	String TRENNER = "_";
	String SEP = "\t";
	String mSEP = ";";
	String mmSEP = ",";
	Casing ci;
	
	HashMap <String, String> hdeadjer;
	HashMap <String, String> hadjflex;
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxx   Constructor
	CreateLexEntry (String datapath, String language) throws IOException{
		
		ci = new Casing ();
		
	   	language = language.trim();
    	language = language.toLowerCase();
						
		if (!(datapath.endsWith("/"))) 
			datapath = datapath + "/";
					
		if (!(language.equals("en"))) {
			 hadjflex = new HashMap <String, String>();
			String aflexfile = datapath + "p2g-" + language + "-adjflex.txt";
			loadAdFlexfile (aflexfile);			
		}
			
	}
		
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	// createLexEntry: In: the termlattice, the list of node-indices in the lattice, preceded by the headpos
	
	public LexEntry createLexEntry (ArrayList<Node> termlist, String language, LexInfo lex, 
			HashMap <String,String> adjflex, FilterExceptions exc) {
		
		LexEntry entry = new LexEntry ();
		entry.lemma = getLemma (termlist, language, lex, adjflex, exc);
		entry.tag = getPOS (termlist);

		if (termlist.size() > 2)
			entry.entrytype = "mw";
		else
			entry.entrytype = "sw";
		if (entry.entrytype.equals("mw")) {
			entry.headpos = termlist.get(0).lemma;
			entry.lemmasequence = getLemmaSequence (termlist);
			entry.POSsequence = getPOSSequence (termlist);
		}
		
		// add gender for nouns
		if (entry.tag.equals("No")) {
			if (!(language.equals("en"))) {
				if (entry.entrytype.equals("sw"))
					entry.gender = getGender (entry.lemma, "1", lex);
				else
					entry.gender = getGender (entry.lemmasequence, entry.headpos, lex);
			}
			else
				entry.gender = "";
		}
		
		return entry;
	}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxxxxxxxxxx  getLemma
	/********************
	 * builds the Lemma form of a term: head-lemmatisation, truecasing, agreement
	 * builds the lemma string from all nodes of the termarray
	 */
	public String getLemma (ArrayList <Node> termarray, String language, LexInfo lex, 
								HashMap <String,String> adjflex, FilterExceptions exc) {
		
		String resultlemma = "";
		
		 // Singleword: truecase lemma, also using original casing (Obacht bei all-UC)
		 // Multiword: Head: lemmatise the head, create casing for the head
		 //            Modifiers: if ADJ: inflect the adj according to gd of noun
		 //						  else: take textform of modifier
		
		if (termarray.size() == 2) {  // singleword: truecase it
			resultlemma = ci.truecaseLemma (termarray.get(1).textform, termarray.get(1).lemma, 
										    termarray.get(1).casing, termarray.get(1).tag, language);
		}
		else {   // multiword
			
				// get head and its gender (for Adj-Agreement)
			Integer headpos = Integer.valueOf(termarray.get(0).lemma);   //headpos was no 0 in list 
			String headlemma = termarray.get(headpos).lemma;			
			headlemma = headlemma.toLowerCase();   
			String gender = lex.getGenderDefault(headlemma);
			
			for (int i = 1; i < termarray.size(); i++) {
				Node current = termarray.get(i);
				String currentlemma = "";
				if (i == headpos)  {  // truecase the head 
					currentlemma = ci.truecaseLemma (termarray.get(i).textform, termarray.get(i).lemma, 
													termarray.get(i).casing, termarray.get(i).tag, language);
				}
				else {    
						// modifier: take textform except for Adj and participles which must agree with the head 
					if (current.tag.startsWith("Ad")) { 
						if (i < headpos) {    // prenominal (DE and FR)
							currentlemma = inflectAdj (current.lemma, language, gender, adjflex);
						}
						else if ((i == headpos + 1) &&     // postnominal
								 (language.equals("fr") || language.equals("it") || language.equals("es") || language.equals("pt"))) {
							currentlemma = inflectAdj (current.lemma, language, gender, adjflex);
						}
						else currentlemma = current.textform;
						
					}
					else if ((current.tag.equals("VbFQ")) || (current.tag.equals("VbFH"))) {  // same for inflected participle forms
						if (i < headpos) {    // prenominal
							currentlemma = inflectParticiple (current.textform, language, gender, exc);
						}
						else if ((i == headpos + 1) &&     // postnominal
								 (language.equals("fr") || language.equals("it") || language.equals("es") || language.equals("pt"))) {
							currentlemma = inflectParticiple (current.textform, language, gender, exc);
						}
						else currentlemma = current.textform;
						
					}

					else		// other MW elements 
						currentlemma = current.textform;
					
					  // truecase non-heads
					currentlemma = ci.truecaseLemma (termarray.get(i).textform, currentlemma, 
							termarray.get(i).casing, termarray.get(i).tag, language);			
				}
				
					// append to the lemma string
				if (resultlemma.isEmpty())
					resultlemma = currentlemma;
				else
					resultlemma = resultlemma + " " + currentlemma;
				}
		}	// end multiword	
		return resultlemma;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxxx andere LexEntry-Felder: getPOS, getlemmaSequence, getPOSSequence
	/******************************
	 * the FIRST element of the termarray list is the head position
	 * the POS of the multiword is the POS of its head.
	 * POS is reduced to the BASIC tagset 
	 */
	public String getPOS (ArrayList <Node> termarray) { 
		Integer headpos = Integer.valueOf(termarray.get(0).lemma);
		String POS = "";
		if (termarray.get(headpos).tag.equals("Unk"))
			POS = "Unk";
		else
			POS = termarray.get(headpos).tag.substring(0, 2);  // basic tagset
		return POS;
	}
	
	/************************************
	 * the lemmasequence is a comma-separated list of all (lowercased) lemmata in the multiword expression
	 * lemmata are in nodes 2ff of the termarray
	 * @param termarray
	 * @return
	 */
	public String getLemmaSequence (ArrayList <Node> termarray) {
		String lemseq = "";
		for (int i = 1; i < termarray.size(); i++) {
			if (lemseq.isEmpty())
				lemseq = termarray.get(i).lemma.toLowerCase();   // in the SEQUENCE lammata are lowercase
			else
				lemseq = lemseq + mmSEP + termarray.get(i).lemma.toLowerCase();
		}
		return lemseq;
	}
	
	/**********************************
	 * the POS-sequence is a comma-separated list of all POS tags of the multiword expression
	 * POS are in nodes 2ff. of the term array. BASIC tagset
	 * @param termarray
	 * @return
	 */
	public String getPOSSequence (ArrayList <Node> termarray) {
		String posseq = "";
		for (int i = 1; i < termarray.size(); i++) {
			if (posseq.isEmpty())
				posseq = termarray.get(i).tag.substring(0,2);   // BTag
			else
				posseq = posseq + mmSEP + termarray.get(i).tag.substring(0,2);
		}
		return posseq;
	}

	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	/*************************
	 * returns the gender of the head of a lemma-sequence
	 */
	public String getGender (String lemmaseq, String headpos, LexInfo lex) {
		
		// compute instring
		Integer hpos = Integer.valueOf(headpos);
		String [] strings = lemmaseq.split(mmSEP);
		String candidate = strings[hpos - 1];
		
		String gender = lex.getGenderDefault(candidate);
		return gender;
		
	}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	/******************
	 * This is language-specific. Done for DE EN FR ES IT PT
	 * Inflects the Adjective by looking up the gender-related column in the hash table
	 * default treatment if no-found
	 */
	private String inflectAdj (String adjstring, String language, String gender, HashMap<String,String> hadjflex) {
		String outstring = "";
	
		if (language.equals("en"))
			return adjstring;
			
		// Inflection of Adjectives
		if (language.equals("de")) {
			if (hadjflex.containsKey(adjstring)) {
				String [] forms = hadjflex.get(adjstring).split("\t");
				if (gender.equals("neutr")) 
					outstring = forms[2];
				else if (gender.startsWith("fem")) 
					outstring = forms[1];
				else 
					outstring = forms[0];
				}
			else {  // default inflection
				if (gender.equals("neutr"))
					outstring = adjstring + "es";
				else if (gender.startsWith("fem")) 
					outstring = adjstring + "e";
				else
					outstring = adjstring + "er";
			}
		}
		else if (language.equals("fr") || language.equals("es") ||
				language.equals("it") || language.equals("pt")) {
			if (hadjflex.containsKey(adjstring)) {
				String [] forms = hadjflex.get(adjstring).split("\t");
		
				if (gender.equals("fem")) 
					outstring = forms[1];
				else 
					outstring = forms[0];
			}
			else outstring = adjstring;
		}
		else
			outstring = adjstring;  // unknown
			
		
		return outstring;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	/*************************
	 *  This is language-specific. Covered: EN DE FR IT ES PT
	 *  Inflection of participles is like adjectives, with some variants
	 *  instring is the TEXTFORM, not the lemma (the lemma is the verb... )
	 *  So it must first be DEflected and then INflected.
	 *  (Gender comes from the head noun)
	 */
	private String inflectParticiple (String instring, String language, String gender, FilterExceptions exc) {
		
		// inflection of participles
		String outstring = "";

		if (language.equals("en"))
			outstring = instring;
		
		else if (language.equals("de")) {
			// deflect lemma
			if (instring.endsWith("nden") || instring.endsWith("nder") || instring.endsWith("ndes") || instring.endsWith("ndem") ||
					instring.endsWith("ten")  || instring.endsWith("ter")  || instring.endsWith("tes")  || instring.endsWith("tem")  ||
					instring.endsWith("enen") || instring.endsWith("ener") || instring.endsWith("enes") || instring.endsWith("enem") ||
					instring.endsWith("anen") || instring.endsWith("aner") || instring.endsWith("anes") || instring.endsWith("anem")) {
				instring = instring.substring(0, instring.length() - 2);   // Endung weg
			}
			else if (instring.endsWith("nde") || instring.endsWith("te") || instring.endsWith("ane") || instring.endsWith("ene"))
				instring = instring.substring(0, instring.length() - 1);				
			
			// inflect lemma	
			if (gender.equals("neutr"))
				outstring = instring + "es";
			else if (gender.startsWith("fem")) 
				outstring = instring + "e";
			else
				outstring = instring + "er";
		}
		
		else if (language.equals("it")) {
			if (instring.endsWith("nti")) 
				outstring = instring.substring(0, instring.length() - 1) + "e";
			else if (instring.endsWith("se") || instring.endsWith("te")) 
				outstring = instring.substring(0, instring.length() - 1) + "a";
			else if (instring.endsWith("ei") || instring.endsWith("si") || instring.endsWith("ti")) 
				outstring = instring.substring(0, instring.length() - 1) + "o";		
			else outstring = instring;
		}
		
		else if (language.equals("es") || language.equals("pt")){
			if (instring.endsWith("as")) 
				outstring = instring.substring(0, instring.length() - 1) + "a";
			else if (instring.endsWith("os")) 
				outstring = instring.substring(0, instring.length() - 1) + "o";
			else outstring = instring;
		}
		
		else if (language.equals("fr")){
			if (instring.endsWith("ntes") || instring.endsWith("nts")) 
				outstring = instring.substring(0, instring.length() - 1);
			else if (instring.endsWith("ées") || instring.endsWith("és") || instring.endsWith("ies") ||
					instring.endsWith("ses") || instring.endsWith("tes") || instring.endsWith("ues") || instring.endsWith("ts"))
				outstring = instring.substring(0, instring.length() - 1);
			else if (instring.endsWith("is") || instring.endsWith("us")) {
				if (exc.isIrregularVerb(instring))
					outstring = instring;
				else 
					outstring = instring.substring(0, instring.length() - 1);
			}
			else outstring = instring;
		}
				
		return outstring; 
	}
		
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	/******** loadAdjFlexFile
	 *   File giving the inflections of Adjectives. 
	 *   Fileformat: lemma \t maskulin-singular \t feminin-singular (\t neuter-singular) (3 or 4 columns depending on language)
	 *   Method creates HashMap, key = Adj-lemma, Value = string (inflected forms, separated by \t 
	 */
	public void loadAdFlexfile (String aflexfile) throws IOException {
				
			BufferedReader in = new BufferedReader (new InputStreamReader 
					(new FileInputStream (new File (aflexfile)), "UTF8"));

			// BOM-treatment
			Integer firstCharOfStream = in.read();
			if(firstCharOfStream != 0xFEFF) { 
				// System.out.println ("xxxxxxxxxxx no BOM in " + aflexfile + "xxxxxxxxxxx"); 
				in.close();		// 1. char zurück
				in = new BufferedReader (new InputStreamReader 
						(new FileInputStream (new File (aflexfile)), "UTF8"));
			}
				
				// Format:  lemma \t  stamm zum Flexen  (sinister \t sinistr)
				int nrlines = 0;
				String a = "";
				while ((a = in.readLine()) != null) {
					
					String [] st = a.split("\t");
					if (st.length == 4)
						hadjflex.put(st[0], st[1] + "\t" + st[2] + "\t" + st[3]);  // languages with: mask fem neutr
					else 
						hadjflex.put(st[0], st[1] + "\t" + st[2]);					// languages with: mask fem
					nrlines++;	
				}
//				System.out.println(nrlines + " adj-flexentries entries read");
				in.close();
			

	} // end loadAdFlexFile
		
}

