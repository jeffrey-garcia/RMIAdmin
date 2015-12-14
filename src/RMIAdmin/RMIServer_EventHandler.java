package RMIAdmin;


import java.io.*;
import java.util.*;
import java.text.*;


public class RMIServer_EventHandler {
	private String file;
	private FileReader fr;
	private FileWriter fw;
	private PrintWriter pw;
	private String event;
	
	private StackTraceElement ste [];
	
	
	public static void main(String [] args) {
		RMIServer_EventHandler rmiS_EH = new RMIServer_EventHandler("testing");
		rmiS_EH.getFile();
	}
	
	
	public RMIServer_EventHandler(String event) {
		this.file = "event.log";
		this.event = event;
	}
	
	
	public RMIServer_EventHandler(StackTraceElement ste []) {
		this.file = "event.log";
		this.ste = ste;
		this.event = "";
	}
	
	
	public void getFile() {
		try {
			fr = new FileReader(file);
			
			//goto write the Log
			this.writeLog();
			
		} catch (Exception exc) {
			System.out.print("Event log does not exist, creating log file...  ");
			
			//goto Create the Log
			this.createLog();
		}
	}
	
	
	private void createLog() {
		try {
			fw = new FileWriter(file, true);
			fw.close();
			
			System.out.print("Done.\n");
			//goto Write the log
			this.writeLog();
			
		} catch (Exception exc) {
			exc.printStackTrace();
			criticalError();
		}
	}
	
	
	private void writeLog() {
		try {
			System.out.print("writing log file...   ");
			
				fw = new FileWriter(file, true);
				pw = new PrintWriter(fw);
				pw.println("Event recorded at " + this.getDayTime());
				pw.println("*************************************************");
				
				if (event.equals("")==true) {
					for (int z=0; z<ste.length; z++) {
						//System.out.println(ste [z]); //Debugger
						pw.println(ste [z]);
					}
				} else {
					pw.println(event);
				}
				
				pw.println();
				pw.println();
				
				pw.close();
				fw.close();
			
			System.out.print("Done.\n");
			
		} catch (Exception exc) {
			exc.printStackTrace();
			criticalError();
		}
	}
	
	
	private String getDayTime() {
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String timestamp = formatter.format(today);
		
		return timestamp;
	}
	
	
	private void criticalError() {
		System.out.println("Critical error occur, fail writing to log file.");
		System.exit(0);
	}
	
}