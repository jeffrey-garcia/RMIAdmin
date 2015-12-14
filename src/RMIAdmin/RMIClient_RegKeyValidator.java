package RMIAdmin;


import java.io.*;
import java.text.*;
import java.util.*;


/************************************************
 * Such a protection (using Reg Key) is useless.
 * Since we've no way to trace if user has 
 * modified the system date.
 *
 * Therefore user could cheat the validation 
 * easily by adjusting the system date.
 *
 * A much better an wiser methodology to 
 * encourage purchase of the software is to 
 * provide a limited functionality when in 
 * evaluation mode.
 *
 ***********************************************/
public class RMIClient_RegKeyValidator {
	private final String passphraseFile = "passphrase.dat";
	private final String regStateFile = "licensed.dat";
	private String passPhrase = null;
	private String registration = null;
	private RMIClient_CipherKey cKey;
	
	private RMIClientGUI rmiGUI;
	
	private boolean isExpired = false;
	
	//Default Encryption Key = I LOVE JAVA
	//Original Encryption Key = UHBQA ZJMDT QQCYS FEZVN
	//Original Content in license.dat = N2m{,f0sp%!V+pt
	//PassPhrase = I LOVE PROGRAMMING JAVA
	//Encrypted PassPhrase = ^(N`W`:Z_S[mR^Pbab&O[lO
	
	public static void main(String [] args) {
		try {
			RMIClient_RegKeyValidator regV = new RMIClient_RegKeyValidator();
			
			if (regV.isRegister() == true) {
				System.out.println("Already Registered.");
				
			} else {
				System.out.println("Not Yet Registered.");
				System.out.print("Please enter key: ");
				String key = new BufferedReader(new InputStreamReader(System.in)).readLine();
				
				regV.setRegKey(key);
				
				if (regV.register() == true)  {
					System.out.println("Registration Success.");
				} else {
					System.out.println("Registration Fail.");
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	
	public RMIClient_RegKeyValidator() {
		cKey = new RMIClient_CipherKey();
	}
	
	
	public RMIClient_RegKeyValidator(RMIClientGUI rmiGUI) {
		cKey = new RMIClient_CipherKey();
		this.rmiGUI = rmiGUI;
	}
	
	
	public boolean isRegister() {
		/******************************************
		 * Note that the encryption key has not 
		 * yet been defined by user, therefore the 
		 * default key will be used for all the 
		 * decrytion in this method
		 *****************************************/
		
		boolean isRegistered = false;
		String validText = "Evaluate since: ";
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(regStateFile)));
			String tmp;
			
			if ((tmp = br.readLine()) != null) {
				br.readLine();
			} else {
				tmp = "";
			}
			
			//System.out.println("Encrypted Registration State   " + tmp); //Debugger
			br.close();
			
			registration = cKey.decryptLine(tmp);
			//System.out.println("Decrypted Registration State   " + registration); //Debugger
			
			if (registration.indexOf("Registered on ") == 0) {
				isRegistered = true;
				
			} else if (registration.indexOf(validText) == 0) {
				String startEvalDate = registration.substring(validText.length(), registration.length());
				
				if (checkExpiration(startEvalDate) == true) {
					//double protection if software expired
					System.out.println("No more grace period, evaluation expired."); //debugger
					
					if (rmiGUI != null) rmiGUI.warning("No more grace period, evaluation expired.");
					isExpired = true;
				}
				
			} else if (registration.equals("Evaluation Mode")) {
				beginEvaluation();
			
			} else if (registration.indexOf("Expired on ") == 0) {
				//double protection if software expired,prevent user modify date
				System.out.println("No more grace period, evaluation expired."); //debugger
				
				if (rmiGUI != null) rmiGUI.warning("No more grace period, evaluation expired.");
				isExpired = true;
				
			} else {
				System.out.println(regStateFile + " is invalid/corrupted.");
				if (rmiGUI != null) rmiGUI.warning(regStateFile + " is invalid/corrupted.");
				
				//Content is invalid, quit the entire system
				System.exit(0);
			}
			
		} catch (Exception exc) {
			exc.printStackTrace(); //Debugger
			
			if (rmiGUI != null) rmiGUI.warning(exc.getMessage());
			
			//Critical error occured, quit the entire system
			System.exit(0);
			
		} finally {
			return isRegistered;
		}
	}
	
	
	public void setRegKey(String key) {
		if (key.equals("") == true) {
			//Make sure the defined key is not empty, put some rubbish
			key = "SOME RUBBISH";
		}
		
		if (cKey.changeKey(key) == true) {
			System.out.println("Key " + key + " accepted."); //Debugger
		} else {
			System.out.println("Incorrect key. Default key will be used"); //Debugger
		}
	}
	
	
	public boolean register() {
		boolean regResult = false;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(passphraseFile)));
			String tmp;
			
			if ((tmp = br.readLine()) != null) {
				br.readLine();
			} else {
				tmp = "";
			}
			
			if (tmp.equals("^(N`W`:Z_S[mR^Pbab&O[lO") == false) {
				System.out.println(passphraseFile + " is invalid/corrupted.");
				if (rmiGUI != null) rmiGUI.warning(passphraseFile + " is invalid/corrupted.");
				
				//Content is invalid, quit the entire system
				System.exit(0);
			}
			
			System.out.println("Encrypted PassPhrase   " + tmp); //Debugger
			br.close();
			
			passPhrase = cKey.decryptLine(tmp);
			System.out.println("Decrypted PassPhrase   " + passPhrase); //Debugger
			
			if (passPhrase.equals("I LOVE PROGRAMMING JAVA") == true) {
				regResult = true;
				approveRegistration();
				
			} else {
				regResult = false;
			}
			
		} catch (Exception exc) {
			exc.printStackTrace(); //Debugger
			regResult = false;
			
			if (rmiGUI != null) rmiGUI.warning(exc.getMessage());
			
			//Critical error that we must qui the entire system
			System.exit(0);
			
		} finally {
			return regResult;
		}
	}
	
	
	private boolean checkExpiration(String startEvalDate) {
		boolean isExpired = true;
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		GregorianCalendar startDate; //First Evaluation's Date
		GregorianCalendar currDate; //Today's Date
		
		try {
			startDate = new GregorianCalendar();
			startDate.setTime(formatter.parse(startEvalDate));
			
			currDate =  new GregorianCalendar();
			currDate.setTime(formatter.parse(formatter.format(new Date())));
			
			if (currDate.before(startDate)) {
				System.out.println("Current Date " + 
									(currDate.get(currDate.MONTH)+1) + "/" +
									currDate.get(currDate.DAY_OF_MONTH) + "/" + 
									currDate.get(currDate.YEAR) + 
									" is before Start Evaluation Date " + 
									(startDate.get(startDate.MONTH)+1) + "/" +
									startDate.get(startDate.DAY_OF_MONTH) + "/" + 
									startDate.get(startDate.YEAR)
								   );
				
				System.out.println(regStateFile + " is invalid/corrupted."); //Debugger
				if (rmiGUI != null) rmiGUI.warning(regStateFile + " is invalid/corrupted.");
				
				//Date Time is invalid, quit the entire system
				System.exit(0);
				
			} else {
				int gracePeriod = 0;
				while (startDate.getTime().compareTo(currDate.getTime()) < 0) {
					startDate.add(Calendar.DAY_OF_MONTH, 1);
					gracePeriod ++;
				}
				
				if (gracePeriod > 30) {
					isExpired = true;
					System.out.println("No more grace period, evaluation expired."); //Debugger
					if (rmiGUI != null) rmiGUI.warning("No more grace period, evaluation expired.\n");
					
					//Set the software to be expired
					expireEvaluation();
					
				} else {
					System.out.println("Grace period: " + (30-gracePeriod) + " day(s) remaining."); //Debugger
					if (rmiGUI != null) rmiGUI.showInfo("Grace period: " + (30-gracePeriod) + " day(s) remaining for evaluation.");
					
					isExpired = false;
				}
			}
			
		} catch (Exception exc) {
			//exc.printStackTrace(); //Debugger
			isExpired = true;
			
			//Date Time Parsing Error, quit the entire system
			System.out.println(regStateFile + " is invalid/corrupted."); //debugger
			if (rmiGUI != null) rmiGUI.warning(regStateFile + " is invalid/corrupted.");
			
			//quit the entire system
			System.exit(0);
			
		} finally {
			return isExpired;
		}
	}
	
	
	private void beginEvaluation() {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		GregorianCalendar currDate; //Today's Date
		
		try {
			currDate =  new GregorianCalendar();
			currDate.setTime(formatter.parse(formatter.format(new Date())));
			
			String today = 	(currDate.get(currDate.MONTH) + 1) + "/" +
							currDate.get(currDate.DAY_OF_MONTH) + "/" + 
							currDate.get(currDate.YEAR);
			
			FileWriter fw = new FileWriter(regStateFile, false);
			fw.write(cKey.encryptLine("Evaluate since: " + today));
			fw.close();
			
		} catch (Exception exc) {
			//exc.printStackTrace(); //Debugger
			
			if (rmiGUI != null) rmiGUI.warning(exc.getMessage());
			
			//Critical error, quit the entire system
			System.exit(0);
			
		} finally {
			//nothing to do
		}
	}
	
	
	private void approveRegistration() {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		GregorianCalendar currDate; //Today's Date
		
		try {
			currDate =  new GregorianCalendar();
			currDate.setTime(formatter.parse(formatter.format(new Date())));
			
			String today = 	(currDate.get(currDate.MONTH) + 1) + "/" +
							currDate.get(currDate.DAY_OF_MONTH) + "/" + 
							currDate.get(currDate.YEAR);
			
			FileWriter fw = new FileWriter(regStateFile, false);
			
			//Reset the Reg Key to default
			setRegKey("default");
			
			//System.out.println("Registered on " + today); //Debugger
			//System.out.println(cKey.encryptLine("Registered on " + today)); //Debugger
			
			fw.write(cKey.encryptLine("Registered on " + today));
			fw.close();
			
		} catch (Exception exc) {
			//exc.printStackTrace(); //Debugger
			
			if (rmiGUI != null) rmiGUI.warning(exc.getMessage());
			
			//Critical error occur, quit entire system
			System.exit(0);
			
		} finally {
			//nothing to do
		}
	}
	
	
	private void expireEvaluation() {
		//Set the expire state on the class variable
		isExpired = true;
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		GregorianCalendar currDate; //Today's Date
		
		try {
			currDate =  new GregorianCalendar();
			currDate.setTime(formatter.parse(formatter.format(new Date())));
			
			String today = 	(currDate.get(currDate.MONTH) + 1) + "/" +
							currDate.get(currDate.DAY_OF_MONTH) + "/" + 
							currDate.get(currDate.YEAR);
			
			FileWriter fw = new FileWriter(regStateFile, false);
			
			//Reset the Reg Key to default
			setRegKey("default");
			
			System.out.println("Expired on " + today);
			System.out.println(cKey.encryptLine("Expired on " + today));
			
			fw.write(cKey.encryptLine("Expired on " + today));
			fw.close();
			
		} catch (Exception exc) {
			//exc.printStackTrace(); //Debugger
			
			if (rmiGUI != null) rmiGUI.warning(exc.getMessage());
			
			//Critical error occur, quit entire system
			System.exit(0);
			
		} finally {
			//nothing to do
		}
	}
	
	
	public boolean isExpired() {
		return isExpired;
	}
}