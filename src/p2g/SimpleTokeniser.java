package p2g;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**************************************
 * Linguistic Resources:
 * Normaliser-Lexikon: Hashmap string,string: key = wrong form, value = right form.
 *                     
 * Methods:
 * simpleTokeniser:  string to tokens
 * simpleNormaliser: string to normalised string
 * removePnct:       replaces most common punctuations by blanks  
 * lookupNormaliser: access to normlex
*/


public class SimpleTokeniser {

HashMap <String, String> normlex;

	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxx constructor: load resources
	public SimpleTokeniser (String datapath, String language) 
									throws IOException {
			
	language = language.trim();
	language = language.toLowerCase();
			
	if (!(datapath.endsWith("/"))) 
		datapath = datapath + "/";
	
	String normfile = datapath + "p2g-" + language + "-norm.txt";
			
	HashMap <String, String> normlex = new HashMap <String, String> ();
	normlex = loadNormLex (normlex, normfile);
	this.normlex = normlex;
	}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxx simple tokeniser
	//xxx in: text, out: tokenlist
	/*****************************
	 *  just looks for letters, digits, and -
	 *  all other characters are token boundaries
	 *  simple...
	 *  errors with quotes! john's etc.
	 */
	public ArrayList<String> simpleTokenise (String text) {
		
		ArrayList<String> tokens = new ArrayList <String>();
		
		String token = "";
		for (int pos = 0; pos < text.length(); pos++) {		
			Character c = text.charAt(pos);
			if ((Character.isLetterOrDigit(c)) || 
					(c.equals('-'))) {				// Vorsicht bei Baltics!!
				token = token + c;
			}
			else {
				if (!(token.isEmpty())) {
					tokens.add(token);
					token = new String ();
					token = "";
				}	
			}
		}
		// last token
		if (!(token.isEmpty()))
			tokens.add(token);
		
		return tokens;	
	}
	

	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxx simple normaliser
	public String simpleNormalise (String inword) {
		
		inword = inword.trim();
		inword = removePnct (inword);
		inword = inword.toLowerCase();
		inword = lookupNormaliser (inword);		
		return inword;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxx simple Pcnt-treatment
	public String removePnct (String inword) {
		
		inword = inword.replace(".", " ");
		inword = inword.replace(",", " ");  // nur trailing pnct soll weg!
		inword = inword.replace(")", " ");
		inword = inword.replace("(", " ");
		inword = inword.replace("\"", " ");
		inword = inword.replace("\'", " ");
		inword = inword.replace(";", " ");
		inword = inword.replace(":", " ");
		inword = inword.replace("!", " ");
		inword = inword.replace("?", " ");
		
		return inword;		
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxx simple Normaliser-treatment
	public String lookupNormaliser (String inword) {
		
		if (normlex.containsKey(inword)) {
			inword = normlex.get(inword);
		}
		return inword;
		
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxxxxxxxx load Normaliserlex
	public HashMap <String, String> loadNormLex (HashMap <String, String> normlex, String filename) 
											throws IOException {
		
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
				
			String a = "";
			while ((a = in.readLine()) != null) {
				
				String [] str = a.split("\t");
				if (str.length != 2) continue;
				normlex.put(str[0], str[1]);		// only 1 normstring survives			
			}

	
		return normlex;
	}
	

}
	
