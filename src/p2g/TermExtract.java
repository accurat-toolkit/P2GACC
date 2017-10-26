package p2g;


import java.util.ArrayList;
// import java.util.HashMap;

class Node {
	String textform;
	String casing;
	String lemma;
	String tag;	
}

class BiText {
	String sltext;
	String tltext;
}

class TermExtract {

	ArrayList <BiText> termcandidates;
	ArrayList <ArrayList <Node>> wordlatticeSL;
	ArrayList <ArrayList <Node>> wordlatticeTL;
	ArrayList <Node> termlistSL;
	ArrayList <Node> termlistTL;
		
	
	String slang; 
	String tlang; 
	String datapath;
	BiText candidate;
	
	String SEP = "\t";
	String mSEP = ";";
	String mmSEP = ",";
	String CR = "\r\n";
	
	
	SimpleTokeniser sltokeniser;  //  = new SimpleTokeniser (datapath, slang);
	SimpleTokeniser tltokeniser;  //  = new SimpleTokeniser (datapath, tlang);
	LexInfo sllex;				  //  = lexicon for lemmatisation and genderdefaulting
	LexInfo tllex;
	MWFilter slmwf;               //  = new MWFilter (datapath, slang);
	MWFilter tlmwf;               //  = new MWFilter (datapath, tlang);
	FilterExceptions slexc;
	FilterExceptions tlexc;

	CreateLexEntry slcle;
	CreateLexEntry tlcle;
	Casing ci;
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxxxxxxx Konstruktor: init alle Objekte
	TermExtract (SimpleTokeniser sltokeniser, SimpleTokeniser tltokeniser,
			LexInfo sllex, LexInfo tllex,  
			MWFilter slmwf, MWFilter tlmwf,
			FilterExceptions slexc, FilterExceptions tlexc, 
			CreateLexEntry slcle, CreateLexEntry tlcle) {
		
		ci = new Casing ();
		
		this.sltokeniser = sltokeniser;
		this.sllex = sllex;
		this.slmwf = slmwf;
		this.slcle = slcle;
		this.slexc = slexc;
		
		this.tltokeniser = tltokeniser;
		this.tllex = tllex;
		this.tlmwf = tlmwf;
		this.tlcle = tlcle;
		this.tlexc = tlexc;
	}
	
	
	
	/**xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxxx extractTerm
		termExtract: in = Bitext, out = String with LexEntry;
		sltermlattice = createTermlattice (sltext)
		sltermlist = Mwfilter (sltermlattice)     // also for singlewords
		if sltermlist is not empty
			tltermlattice = createTermlattice (tltext)
 			tltermlist = Mwfilter (tltermlattice)
 			if tltermlist is not empty	// treffer
				slentry = createLexEntry (sltermlist)
				tlentry = createLexEntry (tltermlist)
				return sl+tlEntry;
			else return emptystring
	*/
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxx   extractTerm
	public String extractTerm (BiText candidate, String slang, String tlang, Boolean verbose, 
							LexInfo sllex, LexInfo tllex, 
							FilterExceptions slexc, FilterExceptions tlexc,
							CreateLexEntry slcle, CreateLexEntry tlcle) {
		
		String returnstring = "";
		
		// create a wordlattice for the SL phrase
		wordlatticeSL = new ArrayList <ArrayList <Node>> ();
		wordlatticeSL = createWordLattice (sltokeniser, sllex, 
				 						   wordlatticeSL, slexc, candidate.sltext, slang);
		
		// apply linguistic filter on the SL side
		termlistSL = new ArrayList <Node> ();
		termlistSL = slmwf.runMWFilter(slmwf, wordlatticeSL);
		
		if (!(termlistSL.isEmpty())) {
				// create wordlattice and linguistic filter for the target side
			wordlatticeTL = new ArrayList <ArrayList <Node>> ();
			wordlatticeTL = createWordLattice (tltokeniser, tllex, 
											   wordlatticeTL, tlexc, candidate.tltext, tlang);
			termlistTL = new ArrayList <Node> ();
			termlistTL = tlmwf.runMWFilter(tlmwf, wordlatticeTL);
			if (!(termlistTL.isEmpty())) {
					// if both source and target side are OK: create lexicon entries
				LexEntry slentry = slcle.createLexEntry(termlistSL, slang, sllex, slcle.hadjflex, slexc);
				LexEntry tlentry = tlcle.createLexEntry(termlistTL, tlang, sllex, tlcle.hadjflex, tlexc);
				
				returnstring = entryToString (slentry, tlentry, verbose);
			}
		}
		return returnstring;
	}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxx entryToString
	/** provides the output strings of the entries
	// verbose TRUE = WITH annotations, FALSE = only SL/TL-Lemma/Tag
	 * */

	private String entryToString (LexEntry slentry, LexEntry tlentry, Boolean verbose) {
		String returnstring = "";
		
		returnstring = slentry.lemma + SEP + slentry.tag + SEP + 
					   tlentry.lemma + SEP + tlentry.tag;
		
		if (verbose) {
			// then write source and target side features as 'feature=value;'
			String slfeatures = CR + "POS=" + slentry.tag + mSEP + "etyp=" + slentry.entrytype + mSEP;
			if (slentry.tag.startsWith("No") && (!(slentry.gender.isEmpty())))
				slfeatures = slfeatures  + "GD=" + slentry.gender + mSEP;
			if (slentry.entrytype.equals("mw")) {
				slfeatures = slfeatures + "headpos=" + slentry.headpos + mSEP;
				slfeatures = slfeatures + "lemmalist=" + slentry.lemmasequence + mSEP;
				slfeatures = slfeatures + "taglist=" + slentry.POSsequence + mSEP;
			}
			String tlfeatures = CR + "POS=" + tlentry.tag + mSEP + "etyp=" + tlentry.entrytype + mSEP;
			if (tlentry.tag.startsWith("No") && (!(tlentry.gender.isEmpty())))
				tlfeatures = tlfeatures  + "GD=" + tlentry.gender + mSEP;
			if (tlentry.entrytype.equals("mw")) {
				tlfeatures = tlfeatures + "headpos=" + tlentry.headpos + mSEP;
				tlfeatures = tlfeatures + "lemmalist=" + tlentry.lemmasequence + mSEP;
				tlfeatures = tlfeatures + "taglist=" + tlentry.POSsequence + mSEP;
			}
			returnstring = returnstring + SEP + slfeatures + SEP + tlfeatures + CR;
		}
		
		return returnstring;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxxxxxx createWordlattice
	/*******************************
	 *  tokenises and normalises the instring
	 *  replaces each word of the instring by an ArrayList of <lemma, POS>, 
	 *  by calling a lemmatiser. 
	 *  A bit of filtering of the lemmatiser output, to remove irregular / disturbing hypotheses
	 */
	private ArrayList <ArrayList <Node>> createWordLattice (SimpleTokeniser tok, LexInfo lex , 
															ArrayList <ArrayList <Node>> wordlattice,
															FilterExceptions exc,
															String instring, String language) {
			
		// tokenise string
		ArrayList <String> tokens = new ArrayList <String> ();
		tokens = tok.simpleTokenise (instring);
		
		for (int i = 0; i < tokens.size(); i++) {
			
			// lemmatise each token
			Node currentnode = new Node ();   
			currentnode.textform = tokens.get(i);
			currentnode.casing = ci.determineCasing(currentnode.textform);
			currentnode.lemma = tok.simpleNormalise(currentnode.textform) ;    // lemma = preliminary, = normalised textform here!
			
			// lemmatise String. in = node, out = LIST of nodes			
			ArrayList <Node> nodelist = new ArrayList <Node> ();
			nodelist = lex.getLexEntry(currentnode);

			
			// two filters on the lemmatisatzion output:
			
			// 1. remove noun candidates in German which are lowercase
			if (language.equals("de")) {
				for (int k = nodelist.size() - 1; k >= 0; k--) {
					if ((nodelist.get(k).casing.equals("lc")) &&
						(nodelist.get(k).tag.startsWith("No"))) {
						nodelist.remove(k);
					}
				}
			}
			
			// 2. remove nouns or adjectives which are homographs to function words, according 
			// to the exception lists
			for (int j = nodelist.size() -1; j >= 0; j--) {
				Node n = nodelist.get(j);
				if ((n.tag.equals("AdAt") && (exc.isAdjHomo(n.textform)))
					|| (n.tag.equals("NoCo") && (exc.isNounHomo(n.textform))))
						nodelist.remove(j);
			}
						
			// add result to lattice
			wordlattice.add(nodelist);
		}	
		return wordlattice;
	}
}