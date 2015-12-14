package RMIAdmin;


import java.rmi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class RMIClient_MemoryMonitor {
	private String ip;
	private String port;
	
	private RMI rmiTarget;
	
	private RMIClientGUI_Function rmiGUI;
	
	private String serverPassphrase = RMIClient_Authenticator.getServerPassphrase();
	
	private long [] memory = new long [3];
	private String [][] systemInfo = null;
	
	
	public static void main (String [] args) {
		RMIClient_MemoryMonitor rmi_MM = new RMIClient_MemoryMonitor("127.0.0.1","1099");
		long [] memory = rmi_MM.getMemory();
		
		for (int i=0; i<memory.length; i++) {
			switch (i) {
				case (0): 
					System.out.println("Free VM Memory: " + memory [0]);
					break;
				case (1): 
					System.out.println("Total VM Memory: " + memory [1]);
					break;
				case (2): 
					System.out.println("MAX VM Memory: " + memory [2]);
					break;
			}
		}
	}
	
	
	public RMIClient_MemoryMonitor(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}
	
	
	public RMIClient_MemoryMonitor(String ip, String port, RMIClientGUI_MemoryMonitor rmiGUI) {
		this.ip = ip;
		this.port = port;
		this.rmiGUI = rmiGUI;
	}
	
	
	public long [] getMemory() {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			memory = rmiTarget.getMemory(serverPassphrase);
			
		} catch (Exception exc) {
			grepDebug(exc);
			
			//set values to 0 to the memory array, indicating error has occured.
			for (int i=0; i<memory.length; i++) {
				memory [i] = 0;
			}
		}
		
		return memory;
	}
	
	
	public void collectGarbage() {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			rmiTarget.collectGarbage(serverPassphrase);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
	}
	
	
	public String [][] getSystemInfo() {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			systemInfo = rmiTarget.getSystemInfo(serverPassphrase);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
		
		return systemInfo;
	}
	
	
	private void grepDebug(Exception exc) {
		if (rmiGUI != null) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiGUI.getDebugger().setDebugText(error);
			rmiGUI.warning(exc.getMessage());
		}
	}
	
	
	private void grepDebug(String text) {
		if (rmiGUI != null) {
			rmiGUI.getDebugger().setDebugText(text);
		}
	}
}