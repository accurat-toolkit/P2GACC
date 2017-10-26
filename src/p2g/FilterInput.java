package p2g;


class FilterInput {
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public BiText createBitext (String inrecord, String informat, String frqthreshold) {
		
		BiText currentbitext = new BiText ();
		
		// Extraktion der BiTexts
		if (informat.equals("phrasetable")) {
			currentbitext = createBitextFromPhraseTable(inrecord, frqthreshold);
		}
		else if (informat.equals("anymalign")) {
			currentbitext = createBitextFromAnymAlign(inrecord, frqthreshold);
		}
		else if (informat.equals("termlist")) {
				currentbitext = createBitextFromTermList (inrecord, frqthreshold);
		}
		else if (informat.equals("pexacc")) {
			currentbitext = createBitextFromPexacc (inrecord, frqthreshold);
	}
		else {}
			//leer
		
		return currentbitext;
	}
	
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public BiText createBitextFromPhraseTable (String inrecord, String frqthreshold) {
		
		/*** Format of PhraseTable: 4 fields, separated by '|||' )
		 *   0 = source-term
		 *   1 = target-term
		 *   2 = array[5] of probabilities, separated by ' '. relevant: 2-1 = p(e|f), 2-3 = p(f|e). Here only 3 is taken: (s2[3]); 
		 *   3 = (empty?)
		 *   4 = frequency. Entry taken if sl AND tl frequency > 1
		 */
		
		BiText returnbitext = new BiText ();
		returnbitext.sltext = "";
		returnbitext.tltext = "";
				
//		inrecord = inrecord.replace("|||", "\t");
//		String [] s1 = inrecord.split("\t");
		String [] s1 = inrecord.split ("\\|\\|\\|");
		if (s1.length < 5) 			// wrong input format
			return returnbitext;
		
		String slterm = s1[0].trim();
		String tlterm = s1[1].trim();
		
			// Probabilities: second field of phrase table
		Double threshold = Double.valueOf(frqthreshold);
		
		String probs = s1[2];
		String [] s2 = probs.split(" ");
		if (s2.length < 4) 
			return returnbitext;		
		Double currentprob = Double.valueOf(s2[3]);
		
		if (currentprob < threshold)
			return returnbitext;   // empty ...

			// Frequency: last field of phrase table
		String f = s1[4].trim();
		String [] fa = f.split(" ");
		if (fa.length > 2)
			return returnbitext;
		if ((Double.valueOf(fa[0]) > 1) &&
			(Double.valueOf(fa[1]) > 1)) {
			returnbitext.sltext = slterm;
			returnbitext.tltext = tlterm;
		}
		return returnbitext;
		
		// return has content if p(tl|sl) > thershold && frq(sl) > 1 && frq(tl) > 1
		// else is empty

	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public BiText createBitextFromAnymAlign (String inrecord, String frqthreshold) {
		
		/**************
		 * Format of AnymAlign: 5 columns, TAB-separated
		 * 0 = Slterm, 1 = tlterm, 2 = hyphen, 3 = 2 doubles with scores, blank-separated, 4 = # frequency 
		 */
		
		BiText returnbitext = new BiText ();
		returnbitext.sltext = "";
		returnbitext.tltext = "";
		
		String [] infields = inrecord.split("\t");
		if (infields.length < 3)
			return returnbitext;
		
		String slterm = infields[0];
		String tlterm = infields[1];

//		       hyphen = infields[2];
//		String scores = infields[3];		
		String infrq = infields[4];			
		Double threshold = Double.valueOf(frqthreshold);
		
		Double frq = Double.valueOf(infrq);
		if (frq > threshold) {
			returnbitext.sltext = slterm;
			returnbitext.tltext = tlterm;
		}
		
		return returnbitext;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public BiText createBitextFromPexacc (String inrecord, String frqthreshold) {
		
		/**************
		 * Format: 3 columns, TAB-separated
		 * 0 = Slterm, 1 = tlterm, 2 = double with confidence 
		 */
		
		BiText returnbitext = new BiText ();
		returnbitext.sltext = "";
		returnbitext.tltext = "";
		
		String [] infields = inrecord.split("\t");
		if (infields.length < 3)
			return returnbitext;
		
		String slterm = infields[0];
		String tlterm = infields[1];
		String infrq = infields[2];
		infrq = infrq.trim();
		
		Double threshold = Double.valueOf(frqthreshold);
			
		// translation frequency
		Double frq = Double.valueOf(infrq);
		if (frq > threshold) {
			returnbitext.sltext = slterm;
			returnbitext.tltext = tlterm;
		}
		
		return returnbitext;
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public BiText createBitextFromTermList (String inrecord, String frqthreshold) {
		
		/**************
		 * Format: 3 columns, TAB-separated
		 * 0 = Slterm, 1 = tlterm, 2 frq
		 */
		
		BiText returnbitext = new BiText ();
		returnbitext.sltext = "";
		returnbitext.tltext = "";
		
		String [] infields = inrecord.split("\t");
		if (infields.length < 3)
			return returnbitext;
		
		String slterm = infields[0];
		String tlterm = infields[1];
		String infrq = infields [2];
		
		Integer frq = Integer.valueOf(infrq);
		Integer threshold = Integer.valueOf(frqthreshold);
		
		if (frq > threshold)  {
			returnbitext.sltext = slterm;
			returnbitext.tltext = tlterm;
		}
		
		return returnbitext;
	}
}