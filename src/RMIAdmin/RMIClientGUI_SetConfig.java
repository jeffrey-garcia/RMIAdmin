package RMIAdmin;


import java.io.*;
import java.util.*;


public class RMIClientGUI_SetConfig {
	private RMIClientGUI rmiGUI = null;
	private RMIClientGUI_Debugger rmiDebug;
	
	private String file = "RMIAdmin.cfg";
	private String msg = "";
	
	
	public static void main(String [] args) {
		//RMIClientGUI_SetConfig c = new RMIClientGUI_SetConfig();
		//c.set("test","456");
		//c.get("test");
	}
	
	
	public RMIClientGUI_SetConfig() {
		getUserDir();
		checkFile();
	}
	
	
	public RMIClientGUI_SetConfig(RMIClientGUI_Debugger rmiDebug) {
		this.rmiGUI = (RMIClientGUI)rmiDebug.getTopLevelAncestor();
		this.rmiDebug = rmiDebug;
		
		getUserDir();
		checkFile();
	}
	
	
	private void getUserDir() {
		try {
			file = System.getProperty("user.home") + File.separator + file;
			
		} catch (java.security.AccessControlException ace) {
			StackTraceElement[] error = ace.getStackTrace();
			
			rmiDebug.setDebugText(error);
			
			rmiGUI.warning("Missing .java.policy in HOME folder.\n\n" + 
								"For Windows: C:\\Document and Settings\\<i>Your Windows Logon Name</i>\n" + 
								"For Linux: /home/<i>Your Logon Name</i>\n\n" + 
								"Please download the file from <u>http://www.rmiAdmin.net/deploy/.java.policy</u>\n\n"
							); //Debugger
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
		
	}
	
	
	private void checkFile() {
		boolean exist = false;
		
		try {
			FileReader fr = new FileReader(file);
			fr.close();
			exist = true;
			
			rmiDebug.setDebugText("Config file exist.");
			
		} catch (FileNotFoundException fnfe) {
			exist = false;
			
			rmiDebug.setDebugText("Config file does not exist.");
			
		} catch (Exception exc) {
			exc.printStackTrace();
			msg += exc.getMessage() + "\n";
		}
		
		if (exist == false) {
			try {
				rmiDebug.setDebugText("Creating file ...");
				
				FileWriter fw = new FileWriter(file, false);
				fw.close();
				
				rmiDebug.setDebugText("Done.");
				
			} catch (Exception exc) {
				exc.printStackTrace();
				msg += exc.getMessage() + "\n";
			}
		}
	}
	
	
	public void set(String item, String value) {
		boolean found = false;
		
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String temp = "";
			Vector v = new Vector();
			while ((temp = br.readLine()) != null) {
				if (temp.indexOf(item) == 0) {
					temp = temp.substring(0, temp.indexOf("#")+1);
					temp = temp + value;
					
					found = true;
				}
				
				v.addElement(temp);
			}
			
			br.close();
			fr.close();
			
			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);
			
			for (int i=0; i<v.size(); i++) {
				pw.println(v.elementAt(i) + "");
			}
			
			if (found == false) {
				pw.println(item + "#" + value);
			}
			
			pw.close();
			fw.close();
			
			rmiDebug.setDebugText("Update completed.");
			
		} catch (Exception exc) {
			exc.printStackTrace();
			msg += exc.getMessage() + "\n";
		}
	}
	
	
	public String get(String item) {
		String value = "";
		
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String temp = "";
			while ((temp = br.readLine()) != null) {
				if (temp.indexOf(item) == 0) {
					value = temp.substring(temp.indexOf("#")+1, temp.length());
				}
			}
			
			br.close();
			fr.close();
			
		} catch (Exception exc) {
			exc.printStackTrace();
			msg += exc.getMessage() + "\n";
		}
		
		rmiDebug.setDebugText("Returned value = " + value);
		return value;
	}
	
	
	public String getMessage() {
		return msg;
	}
}