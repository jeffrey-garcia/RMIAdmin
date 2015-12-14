package RMIAdmin;


import java.io.*;


public class RMIClient_Authenticator {
	private static RMIClient_CipherKey cKey = new RMIClient_CipherKey();
	private static String serverKey = "";
	private static String serverPassphrase = "";
	
	
	public static String getToken() {
		String token = "rmiAdmin";
		
		getServerKey();
		getServerPassphrase();
		
		if (serverKey.equals("")==false && serverPassphrase.equals("")==false) {
			cKey.changeKey(serverKey);
			token = cKey.decryptLine(serverPassphrase);
		}
		
		return token;
	}
	
	
	public static void getServerKey() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("serverKey.dat"));
			
			String tmp = "";
			if ((tmp = br.readLine()) != null) {
				serverKey = tmp;
			}
			
			br.close();
			
		} catch (Exception exc) {
			//nothing to do...
		}
	}
	
	
	public static String getServerPassphrase() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("serverPassphrase.dat"));
			
			String tmp = "";
			if ((tmp = br.readLine()) != null) {
				serverPassphrase = tmp;
			}
			
			br.close();
			
			return serverPassphrase;
			
		} catch (Exception exc) {
			return "";
		}
	}
}