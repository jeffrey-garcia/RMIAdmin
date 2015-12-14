package RMIAdmin;


import java.net.*;
import javax.help.*;
import javax.help.search.*;


public class RMIClientGUI_HelpSet {
	private static HelpSet hs = null;
	private static ClassLoader cl;
	private static Object obj = new Object();
	
	
	public static HelpSet getHelpSet(String helpsetfile) {
		cl = obj.getClass().getClassLoader();
		
		try {
			URL hsURL = HelpSet.findHelpSet(cl, helpsetfile);
			hs = new HelpSet(null, hsURL);
		} catch(Exception ee) {
			System.out.println("HelpSet: "+ ee.getMessage());
			System.out.println("HelpSet: "+ helpsetfile + " not found");
		}
		
		return hs;
	}
}