package p2g;


import java.util.HashMap;

class FilterExceptions {
	
	HashMap <String, String> hexceptions; 
	
	//xxxxxxxxxxxx  Konstruktor xxxxxxxxxxxx
	FilterExceptions (String language) {
		hexceptions = new HashMap <String, String> ();
		hexceptions = loadHomographs (language, hexceptions);
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public Boolean isNounHomo (String candidate) {
		
		if (hexceptions.containsKey(candidate)) {
			String val = hexceptions.get(candidate);
			if (val.equals("hon"))
				return true;
		}
		return false;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public Boolean isAdjHomo (String candidate) {
		if (hexceptions.containsKey(candidate)) {
			String val = hexceptions.get(candidate);
			if (val.equals("hoa"))
				return true;
		}
		return false;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public Boolean isIrregularVerb (String candidate) {
		if (hexceptions.containsKey(candidate)) {
			String val = hexceptions.get(candidate);
			if (val.equals("irr"))
				return true;
		}
		return false;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public HashMap <String, String> loadHomographs (String language, HashMap <String, String> hexceptions) {
		
		if (language.equals ("de")) {
			
			// Nomina
			hexceptions.put ("helle", "hon");			hexceptions.put ("wegen", "hon");			hexceptions.put ("laut", "hon");
			hexceptions.put ("falls", "hon");			hexceptions.put ("statt", "hon");			hexceptions.put ("anfangs", "hon");
			hexceptions.put ("jenseits", "hon");		hexceptions.put ("eingangs", "hon");		hexceptions.put ("contra", "hon");
			hexceptions.put ("alias", "hon");			hexceptions.put ("sonder", "hon");			hexceptions.put ("minus", "hon");
			hexceptions.put ("letztere", "hon");		hexceptions.put ("letzteren", "hon");		hexceptions.put ("letzterem", "hon");
			hexceptions.put ("letzterer", "hon");		hexceptions.put ("letzteres", "hon");		hexceptions.put ("einer", "hon");
			hexceptions.put ("nichts", "hon");			hexceptions.put ("ist", "hon");				hexceptions.put ("würden", "hon");
			hexceptions.put ("hast", "hon");			hexceptions.put ("habe", "hon");			hexceptions.put ("waren", "hon");
			hexceptions.put ("würde", "hon");			hexceptions.put ("gewesen", "hon");			hexceptions.put ("hintern", "hon");
			hexceptions.put ("anfang", "hon");			hexceptions.put ("angesichts", "hon");		hexceptions.put ("anl.", "hon");
			hexceptions.put ("ausgangs", "hon");		hexceptions.put ("behufs", "hon");			hexceptions.put ("betreffs", "hon");
			hexceptions.put ("gen", "hon");				hexceptions.put ("kontra", "hon");			hexceptions.put ("kraft", "hon");
			hexceptions.put ("mangels", "hon");			hexceptions.put ("mithilfe", "hon");		hexceptions.put ("mitte", "hon");
			hexceptions.put ("mittels", "hon");			hexceptions.put ("namens", "hon");			hexceptions.put ("pro", "hon");
			hexceptions.put ("samt", "hon");			hexceptions.put ("zwecks", "hon");			hexceptions.put ("beides", "hon");
			
			// Adjektive
			hexceptions.put ("lauter", "hoa");			hexceptions.put ("exclusive", "hoa");		hexceptions.put ("inclusive", "hoa");
			hexceptions.put ("exklusive", "hoa");		hexceptions.put ("inklusive", "hoa");		hexceptions.put ("teilweise", "hoa");
			hexceptions.put ("ferner", "hoa");			hexceptions.put ("später", "hoa");			hexceptions.put ("lieber", "hoa");		
		}
		
		else if (language.equals ("en")) {
			
			// Nomina
			hexceptions.put ("left", "hon");			hexceptions.put ("still", "hon");			hexceptions.put ("round", "hon");
			hexceptions.put ("out", "hon");				hexceptions.put ("bound", "hon");			hexceptions.put ("near", "hon");
			hexceptions.put ("how", "hon");
			hexceptions.put ("over", "hon");			hexceptions.put ("till", "hon");			hexceptions.put ("while", "hon");
			hexceptions.put ("will", "hon");			hexceptions.put ("can", "hon");				hexceptions.put ("may", "hon");
			hexceptions.put ("outside", "hon");			hexceptions.put ("inside", "hon");
			hexceptions.put ("plus", "hon");			hexceptions.put ("opposite", "hon");		hexceptions.put ("beyond", "hon");
			hexceptions.put ("mine", "hon");			hexceptions.put ("do", "hon");				hexceptions.put ("minus", "hon");
			hexceptions.put ("ones", "hon");			hexceptions.put ("yes", "hon");
			hexceptions.put ("are", "hon");				hexceptions.put ("does", "hon");			hexceptions.put ("being", "hon");
			hexceptions.put ("might", "hon");			hexceptions.put ("must", "hon");
			/*
			hexceptions.put ("one", "hon"); 			hexceptions.put ("times", "hon");			hexceptions.put ("forward", "hon");
			hexceptions.put ("half", "hon");
			*/
			// Adjektive
			hexceptions.put ("less", "hoa");			hexceptions.put ("more", "hoa");			
			hexceptions.put ("worst", "hoa");			
			hexceptions.put ("up", "hoa");				hexceptions.put ("off", "hoa");				hexceptions.put ("just", "hoa");
			hexceptions.put ("much", "hoa");			
			hexceptions.put ("such", "hoa");			hexceptions.put ("most", "hoa");			hexceptions.put ("least", "hoa");
			hexceptions.put ("alone", "hoa");			hexceptions.put ("on", "hoa");				
			hexceptions.put ("thru", "hoa");			
			hexceptions.put ("very", "hoa");			hexceptions.put ("now", "hoa");			
			hexceptions.put ("many", "hoa");			hexceptions.put ("few", "hoa");				hexceptions.put ("fewer", "hoa");
			hexceptions.put ("fewest", "hoa");	
			/*
			hexceptions.put ("same", "hoa");			hexceptions.put ("other", "hoa");			hexceptions.put ("several", "hoa");
			hexceptions.put ("next", "hoa");			hexceptions.put ("lower", "hoa");			hexceptions.put ("little", "hoa");
			hexceptions.put ("long", "hoa");			hexceptions.put ("only", "hoa");			hexceptions.put ("through", "hoa");
			hexceptions.put ("pending", "hoa");			hexceptions.put ("even", "hoa");			hexceptions.put ("above", "hoa");
			*/
		}
		
		else if (language.equals("fr")) {
			
			// Nomina
			hexceptions.put ("plus", "hon");			hexceptions.put ("devant", "hon");			hexceptions.put ("fait", "hon");
			hexceptions.put ("bien", "hon");			hexceptions.put ("moins", "hon");			hexceptions.put ("avant", "hon");
			hexceptions.put ("sommes", "hon");			hexceptions.put ("vers", "hon");			hexceptions.put ("pour", "hon");
			hexceptions.put ("car", "hon");				hexceptions.put ("autres", "hon");			hexceptions.put ("allée", "hon");
			hexceptions.put ("allées", "hon");			hexceptions.put ("as", "hon");				hexceptions.put ("avions", "hon");
			hexceptions.put ("fût", "hon");				hexceptions.put ("est", "hon");				hexceptions.put ("étais", "hon");
			hexceptions.put ("aller", "hon");			hexceptions.put ("avoir", "hon");			hexceptions.put ("être", "hon");
			hexceptions.put ("été", "hon");				hexceptions.put ("étés", "hon");			hexceptions.put ("devoir", "hon");
			hexceptions.put ("pouvoir", "hon");			hexceptions.put ("savoir", "hon");			hexceptions.put ("vouloir", "hon");
			hexceptions.put ("sous", "hon");			hexceptions.put ("pis", "hon");				hexceptions.put ("envers", "hon");
			hexceptions.put ("pas", "hon");				hexceptions.put ("personne", "hon");		hexceptions.put ("rien", "hon");
			hexceptions.put ("or", "hon");				hexceptions.put ("et", "hon");				hexceptions.put ("son", "hon");
			hexceptions.put ("ton", "hon");
			
			// Adjektive
			hexceptions.put ("crus", "hoa");			hexceptions.put ("dus", "hoa");				hexceptions.put ("voulus", "hoa");
			hexceptions.put ("aucune", "hoa");			hexceptions.put ("aucun", "hoa");			hexceptions.put ("sauf", "hoa");
			hexceptions.put ("sur", "hoa");				hexceptions.put ("allés", "hoa");			hexceptions.put ("allé", "hoa");
			hexceptions.put ("eu", "hoa");				hexceptions.put ("eue", "hoa");				hexceptions.put ("eues", "hoa");
			hexceptions.put ("étées", "hoa");			hexceptions.put ("étée", "hoa");			hexceptions.put ("pu", "hoa");
			hexceptions.put ("su", "hoa");				hexceptions.put ("values", "hoa");			hexceptions.put ("value", "hoa");
			hexceptions.put ("valu", "hoa");
			
			// Ausnahmen bei irregular Participles
			// singularia ending in -s
			hexceptions.put("occis", "irr");		hexceptions.put("circoncis", "irr");	hexceptions.put("mis", "irr");		
			hexceptions.put("admis", "irr");		hexceptions.put("réadmis", "irr");		hexceptions.put("émis", "irr");
			hexceptions.put("démis", "irr");		hexceptions.put("remis", "irr");		hexceptions.put("commis", "irr");		
			hexceptions.put("omis", "irr");			hexceptions.put("promis", "irr");		hexceptions.put("compromis", "irr");
			hexceptions.put("permis", "irr");		hexceptions.put("transmis", "irr");		hexceptions.put("retransmis", "irr");		
			hexceptions.put("soumis", "irr");		hexceptions.put("pris", "irr");			hexceptions.put("repris", "irr");
			hexceptions.put("entrepris", "irr");	hexceptions.put("compris", "irr");		hexceptions.put("appris", "irr");		
			hexceptions.put("réappris", "irr");		hexceptions.put("désapris", "irr");		hexceptions.put("surpris", "irr");
			hexceptions.put("sursis", "irr");		hexceptions.put("acquis", "irr");		hexceptions.put("requis", "irr");		
			hexceptions.put("conquis", "irr");		hexceptions.put("reconquis", "irr");	hexceptions.put("occlus", "irr");
			hexceptions.put("inclus", "irr");		hexceptions.put("absous", "irr");		hexceptions.put("dissous", "irr");					
		}
		
		else if (language.equals ("es")) {
			
			// Nomina
			hexceptions.put ("bien", "hon");		hexceptions.put ("bajo", "hon");		hexceptions.put ("una", "hon");
			hexceptions.put ("uno", "hon");			hexceptions.put ("queridos", "hon");	hexceptions.put ("querido", "hon");
			hexceptions.put ("todo", "hon");		hexceptions.put ("cuanto", "hon");		hexceptions.put ("son", "hon");
			hexceptions.put ("hayas", "hon");		hexceptions.put ("haya", "hon");		hexceptions.put ("haz", "hon");
			hexceptions.put ("haces", "hon");		hexceptions.put ("hechos", "hon");		hexceptions.put ("hecho", "hon");
			hexceptions.put ("suyos", "hon");		hexceptions.put ("era", "hon");			hexceptions.put ("sed", "hon");
			hexceptions.put ("eras", "hon");		hexceptions.put ("ser", "hon");			hexceptions.put ("haber", "hon");
			hexceptions.put ("debes", "hon");		hexceptions.put ("debe", "hon");		hexceptions.put ("deber", "hon");
			hexceptions.put ("saber", "hon");		hexceptions.put ("poder", "hon");		hexceptions.put ("cuantos", "hon");
			hexceptions.put ("este", "hon");		hexceptions.put ("mi", "hon");
			
			// Adjektive
			hexceptions.put ("más", "hoa");			hexceptions.put ("menos", "hoa");		hexceptions.put ("conforme", "hoa");
			hexceptions.put ("mía", "hoa");			hexceptions.put ("mío", "hoa");			hexceptions.put ("mías", "hoa");
			hexceptions.put ("suya", "hoa");		hexceptions.put ("suyo", "hoa");		hexceptions.put ("suyas", "hoa");
			hexceptions.put ("tuyas", "hoa");		hexceptions.put ("tuya", "hoa");		hexceptions.put ("tuyo", "hoa");
			hexceptions.put ("incluido", "hoa");	hexceptions.put ("habido", "hoa");		hexceptions.put ("habida", "hoa");
			hexceptions.put ("habidos", "hoa");		hexceptions.put ("habidas", "hoa");		hexceptions.put ("sidas", "hoa");
			hexceptions.put ("sidos", "hoa");		hexceptions.put ("sida", "hoa");		hexceptions.put ("sido", "hoa");			
		}
		
		else if (language.equals ("it")) {
			
			// Nomina
			hexceptions.put ("affine", "hon");		hexceptions.put ("agli", "hon");		hexceptions.put ("ai", "hon");
			hexceptions.put ("alla", "hon");		hexceptions.put ("alle", "hon");		hexceptions.put ("altrui", "hon");
			hexceptions.put ("andante", "hon");		hexceptions.put ("andare", "hon");		hexceptions.put ("andata", "hon");
			hexceptions.put ("avere", "hon");		hexceptions.put ("basso", "hon");		hexceptions.put ("checche", "hon");
			hexceptions.put ("chi", "hon");			hexceptions.put ("colla", "hon");		hexceptions.put ("collo", "hon");
			hexceptions.put ("come", "hon");		hexceptions.put ("contro", "hon");		hexceptions.put ("cosa", "hon");
			hexceptions.put ("dei", "hon");			hexceptions.put ("dentro", "hon");		hexceptions.put ("dietro", "hon");
			hexceptions.put ("dovere", "hon");		hexceptions.put ("dunque", "hon");		hexceptions.put ("essere", "hon");
			hexceptions.put ("ex", "hon");			hexceptions.put ("fa", "hon");			hexceptions.put ("finche", "hon");
			hexceptions.put ("fosse", "hon");		hexceptions.put ("fossi", "hon");		hexceptions.put ("fra", "hon");
			hexceptions.put ("fra'", "hon");		hexceptions.put ("fummo", "hon");		hexceptions.put ("giacche", "hon");
			hexceptions.put ("guai", "hon");		hexceptions.put ("lui", "hon");			hexceptions.put ("mediante", "hon");
			hexceptions.put ("mentre", "hon");		hexceptions.put ("mi", "hon");			hexceptions.put ("mica", "hon");
			hexceptions.put ("molto", "hon");		hexceptions.put ("nei", "hon");			hexceptions.put ("ni", "hon");
			hexceptions.put ("niente", "hon");		hexceptions.put ("no", "hon");			hexceptions.put ("ora", "hon");
			hexceptions.put ("pelle", "hon");		hexceptions.put ("perche", "hon");		hexceptions.put ("poche", "hon");
			hexceptions.put ("potere", "hon");		hexceptions.put ("prima", "hon");		hexceptions.put ("pro", "hon");
			hexceptions.put ("quale", "hon");		hexceptions.put ("quali", "hon");		hexceptions.put ("quando", "hon");
			hexceptions.put ("quanti", "hon");		hexceptions.put ("quanto", "hon");		hexceptions.put ("sapere", "hon");
			hexceptions.put ("saputi", "hon");		hexceptions.put ("saputo", "hon");		hexceptions.put ("secondo", "hon");
			hexceptions.put ("si", "hon");			hexceptions.put ("solo", "hon");		hexceptions.put ("sono", "hon");
			hexceptions.put ("sopra", "hon");		hexceptions.put ("sotto", "hon");		hexceptions.put ("state", "hon");
			hexceptions.put ("stati", "hon");		hexceptions.put ("sulla", "hon");		hexceptions.put ("sulle", "hon");
			hexceptions.put ("sì", "hon");			hexceptions.put ("te", "hon");			hexceptions.put ("tramite", "hon");
			hexceptions.put ("vai", "hon");			hexceptions.put ("vanno", "hon");		hexceptions.put ("verso", "hon");
			hexceptions.put ("vi", "hon");			hexceptions.put ("via", "hon");			hexceptions.put ("vista", "hon");
			hexceptions.put ("viste", "hon");		hexceptions.put ("volere", "hon");

			// Adjektive
			hexceptions.put ("alcuna", "hoa");		hexceptions.put ("alcune", "hoa");		hexceptions.put ("alcuni", "hoa");
			hexceptions.put ("alcuno", "hoa");		hexceptions.put ("alquanto", "hoa");	hexceptions.put ("andanti", "hoa");
			hexceptions.put ("avente", "hoa");		hexceptions.put ("avverso", "hoa");		hexceptions.put ("benvenuta", "hoa");
			hexceptions.put ("benvenute", "hoa");	hexceptions.put ("benvenuti", "hoa");	hexceptions.put ("benvenuto", "hoa");
			hexceptions.put ("certuna", "hoa");		hexceptions.put ("certune", "hoa");		hexceptions.put ("certuni", "hoa");
			hexceptions.put ("certuno", "hoa");		hexceptions.put ("che", "hoa");			hexceptions.put ("ciascuna", "hoa");
			hexceptions.put ("ciascuno", "hoa");	hexceptions.put ("codesta", "hoa");		hexceptions.put ("codesto", "hoa");
			hexceptions.put ("comprese", "hoa");	hexceptions.put ("compresi", "hoa");	hexceptions.put ("compreso", "hoa");
			hexceptions.put ("concernente", "hoa");	hexceptions.put ("durante", "hoa");		hexceptions.put ("eccetto", "hoa");
			hexceptions.put ("entrambi", "hoa");	hexceptions.put ("essa", "hoa");		hexceptions.put ("esse", "hoa");
			hexceptions.put ("essi", "hoa");		hexceptions.put ("esso", "hoa");		hexceptions.put ("extra", "hoa");
			hexceptions.put ("in", "hoa");			hexceptions.put ("innanzi", "hoa");		hexceptions.put ("lungo", "hoa");
			hexceptions.put ("mia", "hoa");			hexceptions.put ("mie", "hoa");			hexceptions.put ("miei", "hoa");
			hexceptions.put ("molta", "hoa");		hexceptions.put ("molte", "hoa");		hexceptions.put ("molti", "hoa");
			hexceptions.put ("nessuna", "hoa");		hexceptions.put ("nessuno", "hoa");		hexceptions.put ("nullo", "hoa");
			hexceptions.put ("oltre", "hoa");		hexceptions.put ("ovvia", "hoa");		hexceptions.put ("parecchi", "hoa");
			hexceptions.put ("parecchie", "hoa");	hexceptions.put ("pochi", "hoa");		hexceptions.put ("presso", "hoa");
			hexceptions.put ("pronto", "hoa");		hexceptions.put ("pure", "hoa");		hexceptions.put ("qual", "hoa");
			hexceptions.put ("qualche", "hoa");		hexceptions.put ("qualunque", "hoa");	hexceptions.put ("quant'", "hoa");
			hexceptions.put ("quanta", "hoa");		hexceptions.put ("quante", "hoa");		hexceptions.put ("quegli", "hoa");
			hexceptions.put ("quei", "hoa");		hexceptions.put ("quell'", "hoa");		hexceptions.put ("quella", "hoa");
			hexceptions.put ("quelle", "hoa");		hexceptions.put ("quello", "hoa");		hexceptions.put ("quest'", "hoa");
			hexceptions.put ("questa", "hoa");		hexceptions.put ("queste", "hoa");		hexceptions.put ("questi", "hoa");
			hexceptions.put ("questo", "hoa");		hexceptions.put ("salvo", "hoa");		hexceptions.put ("scandente", "hoa");
			hexceptions.put ("spettante", "hoa");	hexceptions.put ("tantino", "hoa");		hexceptions.put ("tutte", "hoa");
			hexceptions.put ("tutti", "hoa");		hexceptions.put ("tutto", "hoa");			
		}
		
		else if (language.equals("pt")) {
			
			// Nomina
			hexceptions.put ("algo", "hon");		hexceptions.put ("alguma", "hon");		hexceptions.put ("algumas", "hon");
			hexceptions.put ("aquele", "hon");		hexceptions.put ("aqueles", "hon");		hexceptions.put ("caso", "hon");
			hexceptions.put ("consoante", "hon");	hexceptions.put ("contra", "hon");		hexceptions.put ("cuja", "hon");
			hexceptions.put ("cujas", "hon");		hexceptions.put ("cujo", "hon");		hexceptions.put ("cujos", "hon");
			hexceptions.put ("entre", "hon");		hexceptions.put ("entretanto", "hon");	hexceptions.put ("era", "hon");
			hexceptions.put ("eras", "hon");		hexceptions.put ("essa", "hon");		hexceptions.put ("essas", "hon");
			hexceptions.put ("esse", "hon");		hexceptions.put ("esses", "hon");		hexceptions.put ("fez", "hon");
			hexceptions.put ("fora", "hon");		hexceptions.put ("foras", "hon");		hexceptions.put ("logo", "hon");
			hexceptions.put ("mais", "hon");		hexceptions.put ("mal", "hon");			hexceptions.put ("malgrado", "hon");
			hexceptions.put ("mas", "hon");			hexceptions.put ("mesmo", "hon");		hexceptions.put ("meu", "hon");
			hexceptions.put ("meus", "hon");		hexceptions.put ("nós", "hon");			hexceptions.put ("o", "hon");
			hexceptions.put ("os", "hon");			hexceptions.put ("poder", "hon");		hexceptions.put ("poderes", "hon");
			hexceptions.put ("saber", "hon");		hexceptions.put ("saberes", "hon");		hexceptions.put ("ser", "hon");
			hexceptions.put ("seres", "hon");		hexceptions.put ("serão", "hon");		hexceptions.put ("seu", "hon");
			hexceptions.put ("sua", "hon");			hexceptions.put ("suas", "hon");		hexceptions.put ("são", "hon");
			hexceptions.put ("tanto", "hon");		hexceptions.put ("tantos", "hon");		hexceptions.put ("termos", "hon");
			hexceptions.put ("tinha", "hon");		hexceptions.put ("tinhas", "hon");		hexceptions.put ("todo", "hon");
			hexceptions.put ("todos", "hon");		hexceptions.put ("um", "hon");			hexceptions.put ("uns", "hon");
			
			// Adjektive
			hexceptions.put ("ambas", "hoa");		hexceptions.put ("ambos", "hoa");		hexceptions.put ("este", "hoa");
			hexceptions.put ("estes", "hoa");		hexceptions.put ("estáveis", "hoa");	hexceptions.put ("muita", "hoa");
			hexceptions.put ("muitas", "hoa");		hexceptions.put ("muito", "hoa");		hexceptions.put ("muitos", "hoa");
			hexceptions.put ("qualquer", "hoa");	hexceptions.put ("todas", "hoa");		
		}	
		return hexceptions;
	}  // end loadHomographs
}