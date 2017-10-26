package p2g;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;	

public class MWFilter {
	
String TRENNER = "\t";


	/** MWFilter does:
	// 1. load the MW-Pattern-Hashfiles (for SL and TL). key = the pattern, value = headpos
	// 2. create the MW-Strings from the term lattice
	// 3. search the best (first-matching) MW-Pattern for the MW-Strings, return its headpos and linenr
	 * 4. deliver the node list of the 
	*/


	
	HashMap <String, Integer> mwpatterns = new HashMap<String,Integer>();
//	HashMap <String, Integer> tlmwpatterns = new HashMap<String,Integer>();
		
	ArrayList <String> mwstrings = new ArrayList <String> ();
	ArrayList<String> mwnodeidx = new ArrayList <String> ();
	
	ArrayList <ArrayList<Node>> termlattice; 
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxx Constructor: datapath, slang, tlang
	
    MWFilter (String datapath, String language) 
    								throws IOException {
		
    	language = language.trim();
    	language = language.toLowerCase();
						
		if (!(datapath.endsWith("/"))) 
			datapath = datapath + "/";
		
		String mwpfilename = datapath + "p2g-" + language + "-multiword.txt";
		mwpatterns = loadMWPatterns (mwpfilename);
		
    }
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//   runMWFilter 
	//   gets: the term lattice
	//   returns: an array of integers returned as string:
	//      first = headpos, second ff. = indices of the nodes in the lattice vector
	
	public ArrayList <Node> runMWFilter (MWFilter mwf, ArrayList<ArrayList<Node>> termlattice) {
		
		this.termlattice = termlattice;
		
		this.mwstrings = createMWStrings (termlattice);	
			// side effect: mwnodeidx is filled with the nodeidx, parallal array, same index

		ArrayList <Node> termlist = new ArrayList <Node> ();
		
			// all mwcandidates are looked up, return = either the index of the one taken, or -1
		Integer lookupresult = lookupMWString (this.mwstrings);
		if (lookupresult == -1) {
			return termlist;   // no match on mwpattern: empty list
		}
		
		else {
				// return list of node-numbers, preceded by the headpos
			Integer headpos = lookupresult % 1000;
			int indexline = lookupresult / 1000;
			String nodeidxes = mwnodeidx.get(indexline);
			nodeidxes = headpos.toString() + TRENNER + nodeidxes; 
			
			termlist = createTermList (termlattice, nodeidxes);
			return termlist;
		}		
	}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	// create List of MW-Strings from Term Lattice
	// in: the lattice Array<Array>Node>>, out: Array<String> with MW-Strings
	// run recursively through the lattice
	public ArrayList<String> createMWStrings (ArrayList<ArrayList<Node>> termlattice) {
		
		this.termlattice = termlattice;
		mwstrings = new ArrayList <String> ();
		mwnodeidx = new ArrayList <String> ();
		
		doNextElement ("", "", 0);
		
		return mwstrings;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	// recursive collection
		private void doNextElement (String current, String currentidx, int col) {
			String out = "";
			String thisidx = "";
			
			if (col < termlattice.size()) {
				for (Integer row = 0; row < termlattice.get(col).size(); row++) {
					if (current.isEmpty()) {
						out = mapTags(termlattice.get(col).get(row).tag);
						thisidx = row.toString();
					}
					else { 
						out = current + TRENNER + mapTags(termlattice.get(col).get(row).tag);
						thisidx = currentidx + TRENNER + row;
					}
					if (col < termlattice.size() - 1) {
						doNextElement (out, thisidx, col + 1);
					}
					else {
						mwstrings.add(out);
						mwnodeidx.add(thisidx);
					}
				
				}
			}		
		}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxxxxx  LookupMWStrings
	//   tries to find the mwstrings in the hashtable
	// returns: line which matched (for later entry creation), headpos	
	// returned in 1 integer. line * 1000 + headpos	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		

		private Integer lookupMWString (ArrayList <String> mwstrings) {
			
			Integer headpos = -1;    // stays negative if no mwstring matches a pattern

			for (int i = 0; i < mwstrings.size(); i++) {				
				if (mwpatterns.containsKey(mwstrings.get(i))) {
						// first headpos, then row
					headpos = i * 1000 + Integer.valueOf(mwpatterns.get(mwstrings.get(i)));
					return headpos;
				}
			}
			return headpos;
		}
		

		
		//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		//xxxxx create term list
		// first node = headposition, next = the disambiguated lemmata, as found on the best path through the lattice
		private ArrayList <Node> createTermList (ArrayList<ArrayList<Node>> termlattice, String nodenrs) {
			ArrayList <Node> terms = new ArrayList <Node> ();
			String [] ix = nodenrs.split(TRENNER);
			
			// artificial headpos node
			Integer headpos = Integer.valueOf(ix[0]);
			Node n = new Node ();
			n.casing = "";
			n.lemma = headpos.toString();
			n.tag = "";
			n.textform = "";
			terms.add(n);
			
			// rest of the nodes
			for (int k = 1; k < ix.length; k++) {
				Integer index = Integer.valueOf(ix[k]);
				terms.add(termlattice.get(k-1).get(index));
			}			
			return terms;
		}
		
		//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		//       mapTags
		/********************************
		 * intended to map the tagset of the lemmatiser to the tagset of the MW patterns
		 * not used right now
		 */
		private String mapTags (String stag) {
			  
			  String mwtag = "";
			  mwtag = stag;
/*			  if ((stag.equals("AdAt")) || (stag.equals("AdNp"))) mwtag = "Ad";
			  else if (stag.equals("ApPr")) mwtag = "Ap";
			  else if (stag.equals("ApPD")) mwtag = "ApPD";
			  else if (stag.equals("CoCo")) mwtag = "Co";
			  else if ((stag.equals("DtAD")) || (stag.equals("DtAI"))) mwtag = "Dt";
			  else if (stag.equals("DtAF")) mwtag = "Dt";
			  else if (stag.equals("No")) mwtag = "NoC";								  // BTag!
			  else if ((stag.equals("NoCo")) || (stag.equals("NoCN"))) mwtag = "NoC";
			  else if (stag.startsWith("NoP")) mwtag = "NoP";
			  else if ((stag.equals("VbFH")) || (stag.equals("VbFQ"))) mwtag = "VbP";		// nur de: inflected
			  else if ((stag.equals("VbFG")) || (stag.equals("VbFP"))) mwtag = "VbP";		// en: basic
			  else if (stag.equals("Unk")) mwtag = "Unk";
			  else mwtag = "OTHER";   // alle anderen
*/			  
			  return mwtag;
		  }
		  

		//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
			//xxxxxxxxxx read-MWPatterns: reads a hashfile
		    /*******************************
		     *  MW Patterns are lists of POS sequences, tab-delimited,
		     *  with the position of the head as first element
		     *  They are read in a HashMap, key is the pattern, value is the headposition 
		     */
			 
			private HashMap<String, Integer> loadMWPatterns (String mwfile) 
															throws IOException {
				
				HashMap <String, Integer> hmw = new HashMap<String,Integer>();
					
				
				BufferedReader in = new BufferedReader (new InputStreamReader 
					(new FileInputStream (new File (mwfile)), "UTF8"));

				// BOM-treatment
				Integer firstCharOfStream = in.read();
				if(firstCharOfStream != 0xFEFF) { 
					// System.out.println ("xxxxxxxxxxx no BOM in " + mwfile + "xxxxxxxxxxx"); 
					in.close();		// 1. char zurück
					in = new BufferedReader (new InputStreamReader 
							(new FileInputStream (new File (mwfile)), "UTF8"));
				}
					
					int nrlines = 0;
					String a = "";
					while ((a = in.readLine()) != null) {
						String [] sa = a.split("\t");
							//  sa[0] = headpos; sa[1ff.] = mwpattern
						String headpos = sa[0];
						String pattern = sa[1];
						for (int k=2; k < sa.length; k++)
							pattern = pattern + TRENNER + sa[k];
						if (hmw.containsKey(pattern)) {}
						else {
							Integer m = 0;
							m = Integer.valueOf(headpos);
							hmw.put(pattern, m);
						}				
						nrlines++;	
					}
			//		System.out.println(nrlines + " mwpatterns read");
					in.close();		
				return hmw;
			}
			
}