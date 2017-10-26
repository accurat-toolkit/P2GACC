package p2g;

import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;


public class LexInfo {
	
	/******
	 * hfwd stores the full word dictionary, hdefgd stores the gender defaulter 
	 */
	HashMap <String, ArrayList<String>> hfwd;   // key textform, value = ArrayList of (lemma \t pos) 
	HashMap <String, String> hdefgd; 			// key = ending, value = gender
	int MAX_ENDING = 9;							// max. length of ending strings for defaulter
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxxxxx Constructor
	LexInfo (String datapath, String language) throws IOException {
		
	 	language = language.trim();
    	language = language.toLowerCase();
						
		if (!(datapath.endsWith("/"))) 
			datapath = datapath + "/";
		
		// initialise full word dictinoary
		hfwd = new HashMap <String, ArrayList<String>> ();
		String lemmalexicon = datapath + "p2g-" + language + "-lemma.txt";
		loadLemmaLexicon (lemmalexicon);
		
		// initialise gender-defaulter
		if (!(language.equals("en"))) {
			hdefgd = new HashMap <String, String> ();
			String gddefaulter = datapath + "p2g-" + language + "-gender.txt";
			loadGDDefault (gddefaulter);
		}
	}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//  getLexEntry
	//xxxx in: a node of <origstring, casing, Lemma, POS> with lemma! = normalisedstring, and POS = empty
	//xxxx out a node LIST with one reading each node
	public ArrayList <Node> getLexEntry (Node current) {
		
		ArrayList <Node> resultlist = new ArrayList <Node> ();
		
		// digits are not lemmatised
		if (current.casing.equals("dg")) {
				current.tag = "Nm";
				resultlist.add(current);
		}
		
		else {
			String textform = current.lemma;		// lemma has the normalised string!
			
			ArrayList <String> lexinfo = new ArrayList<String>(); 
			if (hfwd.containsKey(textform))
				lexinfo = hfwd.get(textform);
			
			if (lexinfo.isEmpty()) {	// no-found: unknown
				current.tag = "Unk";
				current.lemma = current.textform;
				resultlist.add(current);
			}
			else {		
				for (int i = 0; i < lexinfo.size(); i++) {
					Node n = new Node ();
					n.textform = current.textform;
					n.casing = current.casing;
					String reading = lexinfo.get(i);   // reading = lemma \t pos
					String [] st = reading.split("\t");  
					n.lemma = st[0];
					n.tag = st[1];
					resultlist.add(n);
				}
			}
		}
		return resultlist;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public String getGenderDefault (String instring) {
		
		// gender of LONGEST string is taken first
		
		String gender = "";
		
		instring = instring.trim();
				
		// shorten longer strings
		int max = MAX_ENDING;
		if (instring.length() <= MAX_ENDING) max = instring.length();
		
		// from longer to shorter strings
		int ende = instring.length();
		for (int k = max; k > 0; k--) {
			String such = instring.substring(ende - k, ende);
			if (hdefgd.containsKey(such)) {
				gender = hdefgd.get(such);
				return gender;
			}
		}		
		return "unk";
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	private void loadLemmaLexicon (String filename) throws IOException {
		
		BufferedReader in = new BufferedReader (new InputStreamReader 
			(new FileInputStream (new File (filename)), "UTF8"));

		// BOM-treatment
		Integer firstCharOfStream = in.read();
		if(firstCharOfStream != 0xFEFF) { 
			// System.out.println ("xxxxxxxxxxx no BOM in " + filename + "xxxxxxxxxxx"); 
			in.close();		// 1. char zurück
			in = new BufferedReader (new InputStreamReader 
					(new FileInputStream (new File (filename)), "UTF8"));
		}
			
			int nrlines = 0;
			String a = "";
			while ((a = in.readLine()) != null) {
				String [] sa = a.split("\t");
				if (!(sa.length == 3))
					continue;
				String hfwdvalue = sa[1] + "\t" + sa[2];
				String hfwdkey = sa[0];
				ArrayList <String> vals = new ArrayList <String> ();
				if (hfwd.containsKey(hfwdkey)) 
					vals = hfwd.get(hfwdkey);					
				vals.add(hfwdvalue);
				hfwd.put(hfwdkey, vals);
				
				nrlines++;	
			}
	//		System.out.println(nrlines + " mwpatterns read");
			in.close();		
}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	private void loadGDDefault (String filename) throws IOException {
		
			BufferedReader in = new BufferedReader (new InputStreamReader 
				(new FileInputStream (new File (filename)), "UTF8"));

			// BOM-treatment
			Integer firstCharOfStream = in.read();
			if(firstCharOfStream != 0xFEFF) { 
				// System.out.println ("xxxxxxxxxxx no BOM in " + filename + "xxxxxxxxxxx"); 
				in.close();		// 1. char zurück
				in = new BufferedReader (new InputStreamReader 
						(new FileInputStream (new File (filename)), "UTF8"));
			}
				
				int nrlines = 0;
				String a = "";
				while ((a = in.readLine()) != null) {
					String [] sa = a.split("\t");
					if (!(sa.length == 2))
						continue;
					if (!(hdefgd.containsKey(sa[0]))) {
						hdefgd.put(sa[0], sa[1]);
					}
					nrlines++;	
				}
		//		System.out.println(nrlines + " mwpatterns read");
				in.close();		
	}
		
	
}