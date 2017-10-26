package p2g;


/***********************************
 * Stoplist implements the Lexicon Filter
 * deletes an entry from the termlist if it is in the stoplist.
 * Stoplist must be provided by the users.
 * !!!! Check if it  works with LEXentries AND with TERMentries !!! 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashMap;



public class Stoplist {
	
	String datapath = "";
	HashMap <String, Boolean> hstop;
	
	//xxxxxxxxxxxxx Constructor xxxxxxxxxxxxxxxxxxxxx
	Stoplist (String datapath) {
	
	if (!(datapath.endsWith("/"))) 
		datapath = datapath + "/";
	
	this.datapath = datapath;
	
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public void loadStoplist (String stoplistname) 
							throws IOException{
		
		this.hstop = new HashMap <String, Boolean> ();
		
		String stoplistfile = this.datapath + stoplistname;

		BufferedReader in = new BufferedReader (new InputStreamReader 
				(new FileInputStream (new File (stoplistfile)), "UTF8"));
		// BOM-treatment
		Integer firstCharOfStream = in.read();
		if(firstCharOfStream != 0xFEFF) { 
			// System.out.println ("xxxxxxxxxxx no BOM in " + stoplistfile + "xxxxxxxxxxx"); 
			in.close();		// 1. char zurück
			in = new BufferedReader (new InputStreamReader 
					(new FileInputStream (new File (stoplistfile)), "UTF8"));
		}
		
			String inline = "";
			
			while ((inline = in.readLine()) != null) {
								
				if (!(this.hstop.containsKey(inline)))
					this.hstop.put(inline, true);
				
			}						
	}
	
	//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	public Boolean isInStoplist (String inrecord) {
		
		if (this.hstop.containsKey(inrecord))
			return true;
		
		return false;
		
	}
}