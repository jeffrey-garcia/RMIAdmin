package RMIAdmin;


import java.rmi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;


public class RMIClient_ConnectionMonitor {
	private String ip;
	private String port;
	
	private RMI rmiTarget;
	
	private RMIClientGUI_Function rmiGUI;
	
	private String serverPassphrase = RMIClient_Authenticator.getServerPassphrase();
	
	
	/*
	public static void main (String [] args) {
		RMIClient_ConnectionMonitor rmi_CM = new RMIClient_ConnectionMonitor("127.0.0.1","1055");
		Object [] obj = rmi_CM.connect();
		System.out.println("Alive: " + obj [0]);
	}
	*/
	
	
	public RMIClient_ConnectionMonitor(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}
	
	
	public RMIClient_ConnectionMonitor(String ip, String port, RMIClientGUI_ConnectionMonitor rmiGUI) {
		this.ip = ip;
		this.port = port;
		this.rmiGUI = rmiGUI;
	}
	
	
	public Object [] connect() {
		Object [] obj = new Object [2];
		boolean isConnected = false;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			isConnected = rmiTarget.isConnected(serverPassphrase);
			
			obj [0] = isConnected + "";
			obj [1] = "Up and running";
			
		} catch (Exception exc) {
			//exc.printStackTrace();
			//grepDebug(exc);
			
			String error = exc.getMessage() + "\n";
			StackTraceElement[] err = exc.getStackTrace();
			
			for (int i=0; i<err.length; i++) {
				error += err [i] + "\n";
			}
			
			/*
			String breakTxt = "nested exception is: \n";
			if (error.indexOf(breakTxt) >= 0) {
				error = error.substring(error.indexOf(breakTxt)+breakTxt.length(), error.length());
			}
			*/
			
			//Remove all the tab space in front
			error = error.replaceAll("\t", "");
			
			isConnected = false;
			
			obj [0] = isConnected + "";
			obj [1] = error;
		}
		
		return obj;
	}
	
	
	public boolean restartRMI() {
		boolean status = false;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			status = rmiTarget.restartRMI(serverPassphrase);
			
		} catch (Exception exc) {
			grepDebug(exc);
			status = false;
			
		} finally {
			return status;
		}
	}
	
	
	public boolean shutdownRMI() {
		boolean status = false;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			rmiTarget.shutdownRMI(serverPassphrase);
			
			/**********************************************************
			 * Note that if we can retrieve this boolean value, 
			 * then it means that the remote RMI service has not been 
			 * successfully shutdown for some unknown reasons.
			 *********************************************************/
			status = false;
			
		} catch (UnmarshalException ue) {
			//only when this exception occur means that server
			status = true;
			
		} catch (Exception exc) {
			grepDebug(exc);
			status = false;
			
		} finally {
			return status;
		}
	}
	
	
	public void grepDebug(Exception exc) {
		if (rmiGUI != null) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiGUI.getDebugger().setDebugText(error);
			rmiGUI.warning(exc.getMessage());
		}
	}
	
	
	public void grepDebug(String text) {
		if (rmiGUI != null) {
			rmiGUI.getDebugger().setDebugText(text);
		}
	}
	
	
	public static void main(String [] args) {
		String ip = "";
		String port = "";
		String action = "";
		
		try {
			for (int i=0; i<args.length; i++) {
				if (args[i].equals("-i")) { 
					ip = args [i+1];
					
				} else if (args[i].equals("-p")) { 
					port = args [i+1];
					
				} else if (args[i].equals("-a")) { 
					action = args [i+1];
					
				} else if (args[i].equals("-h")) {
					helpMenu();
				}
			}
			
			if (ip.length() == 0) {
				errorParameter("Missing -i \"IP Address\" value");
				
			} else if (port.length() == 0) {
				errorParameter("Missing -p \"Port Number\" value");
				
			} else if (action.length() == 0) {
				errorParameter("Missing -a \"Action\" value, available options:\n" + 
								" connect \n" + 
								" restart \n" + 
								" shutdown \n");
			}
			
			//System.out.println(ip); //Debugger
			//System.out.println(port); //Debugger
			//System.out.println(action); //Debugger
			
			invokeFunction(ip, port, action);
			
		} catch (Exception exc) {
			//exc.printStackTrace(); //Debugger
			helpMenu();
		}
	}
	
	
	private static void errorParameter(String error) {
		System.out.println("SYNTAX ERROR");
		System.out.println("=============================");
		System.out.println(error);
		System.out.println();
		System.out.println("Use the -h option to view the help manual");
		System.out.println();
		System.out.println();
		
		System.exit(0);
	}
	
	
	private static void helpMenu() {
		System.out.println("NAME");
		System.out.println("=============================");
		System.out.println("Monitor link status of remote Computer via RMIAdmin");
		System.out.println();
		System.out.println();
		System.out.println("SYNOPSIS");
		System.out.println("=============================");
		System.out.println("java RMIClient_ConnectionMonitor -options \"values\"");
		System.out.println();
		System.out.println();
		System.out.println("DESCRIPTION");
		System.out.println("=============================");
		System.out.println("-i\tSpecify the \"IP Address\" of remote RMIServer e.g. \"203.186.94.62\"");
		System.out.println("-p\tSpecify the \"Port Number\" of remote RMIServer e.g. \"1091\"");
		System.out.println("-a\tSpecify the \"Action\" to be executed e.g. connect | restart | shutdown");
		System.out.println();
		System.out.println();
		System.out.println("REPORTING BUGS");
		System.out.println("=============================");
		System.out.println("Report bugs to <support@rmiAdmin.net>");
		System.out.println();
		System.out.println();
		System.out.println("COPYRIGHT");
		System.out.println("=============================");
		System.out.println("Copyright (c) 2005 rmiAdmin.net. All Rights Reserved.");
		System.out.println();
		System.out.println();
		System.out.println("SEE ALSO");
		System.out.println("=============================");
		System.out.println("The full documentation for RMIAdmin can be obtained from the official website:");
		System.out.println("http://www.rmiAdmin.net");
		System.out.println();
		System.out.println();
		
		System.exit(0);
	}
	
	
	private static void invokeFunction(String ip, String port, String action) {
		RMIClient_ConnectionMonitor cm;
		
		try {
			if (action.equals("connect")) {
				cm = new RMIClient_ConnectionMonitor(ip, port);
				Object [] obj = cm.connect();
				System.out.println("Alive: " + obj [0]);
				
			} else if (action.equals("restart")) {
				cm = new RMIClient_ConnectionMonitor(ip, port);
				boolean status = cm.restartRMI();
				System.out.println("Status: " + status);
				
			} else if (action.equals("shutdown")) {
				cm = new RMIClient_ConnectionMonitor(ip, port);
				boolean status = cm.shutdownRMI();
				System.out.println("Status: " + status);
				
			} else {
				errorParameter("Invalid Action: " + action);
			}
			
		} catch (Exception exc) {
			//exc.printStackTrace();
			helpMenu();
		}
	}
}