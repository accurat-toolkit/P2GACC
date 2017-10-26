package p2g;


		/******************************************
		 * collects all methods needed to determine / create .... casing
		 * @author g.thurmair
		 *
		 */

import java.util.HashMap;

class Casing {
	
	HashMap <String, String> cpEnglish = new HashMap <String, String> ();
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//     Constructor
	Casing () {
		
		cpEnglish = loadCpEnglish ();
	}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxx determine Casing   == identisch mit updateTokenClass
	public String determineCasing (String instring) {
		
			Boolean Flag1UC = false;
			Boolean Flag2UC = false;
			Boolean Flag2LC = false;
			Boolean FlagDg = false;
//			Boolean FlagPc = false;
			
			if (instring.length() > 1) {
				
				// position 0
				Character c = instring.charAt(0);
				if (Character.isUpperCase(c)) { Flag1UC = true; }    // uc or cp
				else if (Character.isDigit(c)) { FlagDg = true; }
				else if (Character.isLowerCase(c)) { Flag2LC = true; }
			//	else FlagPc = true;
				
				// position 1ff.
				for (int i = 1; i < instring.length(); i++) {
					c = instring.charAt(i);
					if (Character.isUpperCase(c)) Flag2UC = true;
					else if (Character.isLowerCase(c)) Flag2LC = true;
					else if (Character.isDigit(c)) FlagDg = true;
			//		else FlagPc = true;
					// else: currency symbols
				}
		
				// Determine Casing
			//	if (FlagPc) return ("MX");	// worsens results: Abbs etc. dont get normalised any more
				if (Flag1UC) {
					if (Flag2UC) {
						if (Flag2LC) return ("mx");  // +- FlagDG
						else
							if (FlagDg) return ("mx");
							else return ("uc");
					}
					else {
						if (FlagDg) return ("mx");
						else return ("cp");
					}
				}
				else {
					if (Flag2LC) {
						if (Flag2UC) return ("mx");
						else {
							if (FlagDg) return ("mx");
							else return ("lc");			
						}
					}
					else {
						if (Flag2UC) return ("mx");
						else return ("dg");
					}
				}
			}
			else if (instring.length() == 1) {
				Character c = instring.charAt(0);
				if (Character.isUpperCase(c)) return ("uc");
				else if (Character.isDigit(c)) return ("dg");
				else return ("lc");
			}
			return ("lc");   //default
		}
/*	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxx not used
	public String getCiValue (String instring) {
		String ci = "";
		
		// characters
		if (isAllLower (instring))
			ci = "lc";
		else if (isCapitalised (instring))
			ci = "cp";
		else if (isAllUpper (instring))
			ci = "uc";
		
		else if (isDigit (instring))
			ci = "dg";
		else if (isPnct (instring))
			ci = "pc";
		
		else ci = "mx";
		
		return ci;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	//xxxx    setting ci values
	
	public Boolean isUpper (String tok) {		// capitalised or uppercase 
		Character m = tok.charAt(0);
		if (Character.isUpperCase(m)) { return true; }
		else { return false; }
	}
	
	public Boolean isAllLower (String tok) {
		for (int k = 0; k < tok.length(); k++) {
			Character m = tok.charAt(k);
			if (Character.isLowerCase(m)) { }
			else { return false; }
		}
		return true;
	}
	
	public Boolean isAllUpper (String tok) {
		if (tok.isEmpty()) return false;
		for (int k = 0; k < tok.length(); k++) {
			Character m = tok.charAt(k);
			if (Character.isUpperCase(m)) { }
			else { return false; }
		}
		return true;		
	}
	
	public Boolean isCapitalised (String tok) {
		Character m = tok.charAt(0);
		if (Character.isUpperCase(m)) {
			for (int k = 1; k < tok.length(); k++) {
				m = tok.charAt(k);
				if (Character.isLowerCase(m)) { }
				else { return false; }
			}
			return true;
		}
		else return false;
	}

	public Boolean isDigit (String tok) {
		if (tok.isEmpty()) return false;
		Boolean dgflag = true;
		for (int k = 0; k < tok.length(); k++) {
			Character m = tok.charAt(k);
			if (Character.isDigit(m)) { dgflag = true; }
			else { dgflag = false; break; }
		}
		return dgflag;
	}
	
	public Boolean isPnct (String tok) {
		if (tok.isEmpty()) return false;
		Boolean pcflag = true;
		for (int k = 0; k < tok.length(); k++) {
			Character c = tok.charAt(k);			// billig!!!
			if ((c == '.') || (c == ',') || (c == ';') || (c == '(') || (c == ')') ||
				(c == ':') || (c == '?') || (c == '!') || (c == '/') || (c == '"') ||
				(c == '\'') || (c == '[') || (c == ']') || (c == '{') || (c == '}')) {
				pcflag = true;
			}
			else {
				pcflag = false;
				break;
			}
		}
		return pcflag;
	}
*/		
	 //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	 //xxxxxxxxx  truecaseLemma
	 //xxxx converts a normalised lemma into a truecased one
		
		public String truecaseLemma (String original, String lemma, String casing, String pos, String language) {
			String truecasedform = "";
			
					// complex case
			if ((lemma.contains("-") && lemma.length() > 1)) {		// truecase only last part!, und: not with single hyph
				
				if (original.startsWith("-")) 
					original = original.substring(1, original.length());
				else if (original.endsWith("-")) 
					original = original.substring(0, original.length() - 1);
				
				if (lemma.startsWith("-")) 
					lemma = lemma.substring(1, lemma.length());
				else if (lemma.endsWith("-")) 
					lemma = lemma.substring(0, lemma.length() - 1);
				
				String [] part = lemma.split("-");
				String [] originalpart = original.split("-");		// compare to lastpart of ORIGINAL
				
				if (part.length == originalpart.length) {
					for (int i = 0; i < part.length; i++) {
						if (i == part.length - 1)
							part [part.length - 1] = truecaseLemma (originalpart[part.length - 1], part [part.length - 1], 
																	casing, pos, language);
						else 							
							if (!(pos.startsWith("No")))		// hyph bei No ALLE cp: Tret-Heiz-Verfahren
								pos = "Unk";					// sonst nicht! Amber-gestützt
							part[i] = truecaseLemma (originalpart[i], part [i], casing, pos, language);
					}
					truecasedform = part[0];
					for (int j = 1; j < part.length; j++) {
						truecasedform = truecasedform + "-" + part[j];
					}
				}
				else   // VERSCHIEDEN viele Hyphens ...
					truecasedform = capitalise (lemma);
			}
			
			else {		// casing for single parts
				String currentcasing = determineCasing (original);
				
						// all languages, all POS
				if (currentcasing.equals("uc")) {
					truecasedform = toUpper (lemma);
				}
				else if ((currentcasing.equals("dg")) || (currentcasing.equals("pc"))) {
					truecasedform = lemma;
				}
				else if (currentcasing.equals("mx")) {
					truecasedform = truecaseMX (original, lemma);
				}
				else if (currentcasing.equals("cp")) {
					if (language.equals("de")) {
						if ((pos.startsWith("No")) || (pos.startsWith("Unk"))) {
							truecasedform = capitalise (lemma);
						}
						else {
							truecasedform = lemma.toLowerCase();
						}
					}
					else {     // all other languages
						if ((pos.startsWith("NoP")) || pos.startsWith("Unk") || pos.startsWith("AdNa")) {
							truecasedform = capitalise (lemma);
						}
						else {
							truecasedform = lemma.toLowerCase();
						}
					}
				}
				else {   // if currentcasing.equals("lc")
						// capitalise proper nouns or DE nouns
					if ((pos.startsWith("NoP")) ||
							((pos.startsWith("No")) && (language.equals("de")))) {
						truecasedform = capitalise (lemma);
					}
					else {
						truecasedform = lemma.toLowerCase();
					}
				}				
			}
			
			if (language.equals("en")) {
				truecasedform = capitaliseEnNoun (truecasedform);
			}
			return truecasedform;
		}
		
		//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		//xxxxx capitalise
		public String capitalise (String lemma) {
			
			if (lemma.isEmpty())
				return lemma;
			
			StringBuilder s = new StringBuilder();
			
			Character c = lemma.charAt(0);
			s = s.append(Character.toUpperCase(c));
			
			for (int j = 1; j < lemma.length(); j++) {
				s = s.append(lemma.charAt(j));
			}
			lemma = s.toString();
			
			return lemma;
		}
		
		//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		//xxxxx toUpper
		public String toUpper (String lemma) {
			
			StringBuilder s = new StringBuilder();
						
			for (int j = 0; j < lemma.length(); j++) {
				Character c = lemma.charAt(j);
				s = s.append(Character.toUpperCase(c));
			}
			
			lemma = s.toString();
			
			return lemma;
		}
		
		 //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		 //xxxxxxxxx truecaseMX xxxxx macht das Originalspelling bei UCs und MXs wieder her
		 
		 String truecaseMX (String originalstring, String normalstring) {
			 
			 // casing des originalstring auf den normalstring übertragen so weit das geht
			 StringBuilder canonstring = new StringBuilder();
			 
			 // kürzere Länge nehmen
			 int laenge = 0;
			 Character c;
			 if (normalstring.length() < originalstring.length())
				 laenge = normalstring.length();
			 else laenge = originalstring.length();
			 
			 for (int i = 0; i < laenge; i++) {

				 if (normalstring.charAt(i) == originalstring.charAt(i)) {
					 canonstring = canonstring.append(originalstring.charAt(i));
				 }
				 else {
					 c = normalstring.charAt(i);
					 if (originalstring.charAt(i) == Character.toUpperCase(c)) {
						 canonstring = canonstring.append(originalstring.charAt(i));
					 }
					 else canonstring.append(normalstring.charAt(i));
				 }
			 }
			 if (laenge < normalstring.length()) {
				 for (int j = laenge; j < normalstring.length(); j++)
					 canonstring.append(normalstring.charAt(j));
			 }
						 
			return canonstring.toString();
		 }
		 
		 //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		 private String capitaliseEnNoun (String instring) {
			 
			 if (cpEnglish.containsKey(instring))
				 return (cpEnglish.get(instring));
			 else 
				 return instring;
		 }
		 
		 //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		 //xxx all English Nouns which must be capitalised
		 // hashtable, key = alllower, value = capitalised
		 // in case of lexicon gaps ...
		 
		 private HashMap <String, String> loadCpEnglish () {
			 
			 cpEnglish.put("monday", "Monday");			cpEnglish.put("january", "January");	cpEnglish.put("september", "September");
			 cpEnglish.put("tuesday", "Tuesday");		cpEnglish.put("february", "February");	cpEnglish.put("october", "October");
			 cpEnglish.put("wednesday", "Wednesday");	cpEnglish.put("march", "March");		cpEnglish.put("november", "November");
			 cpEnglish.put("thursday", "Thursday");		cpEnglish.put("april", "April");		cpEnglish.put("december", "December");
			 cpEnglish.put("friday", "Friday");			cpEnglish.put("may", "May");			cpEnglish.put("european", "European");
			 cpEnglish.put("saturday", "Saturday");		cpEnglish.put("june", "June");
			 cpEnglish.put("sunday", "Sunday");			cpEnglish.put("july", "July");
			 cpEnglish.put("god", "God");				cpEnglish.put("august", "August");
			 
			 cpEnglish.put("european", "European");		cpEnglish.put("swiss", "Swiss");		cpEnglish.put("spanish", "Spanish");
			 cpEnglish.put("swedish", "Swedish");		cpEnglish.put("german", "German");		cpEnglish.put("french", "French");
			 cpEnglish.put("italian", "Italian");		cpEnglish.put("polish", "Polish");		cpEnglish.put("russian", "Russian");
			 cpEnglish.put("turkish", "Turkish");		cpEnglish.put("american", "American");	cpEnglish.put("chinese", "Chinese");
			 cpEnglish.put("japanese", "Japanese");		cpEnglish.put("korean", "Korean");		cpEnglish.put("indian", "Indian");
			 cpEnglish.put("african", "African");		cpEnglish.put("arabic", "Arabic");		cpEnglish.put("brasilian", "Brasilian");
			 return cpEnglish;
		 }
}
