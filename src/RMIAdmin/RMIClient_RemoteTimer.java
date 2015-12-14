package RMIAdmin;


import java.rmi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.text.*;
import java.util.*;


public class RMIClient_RemoteTimer {
	private String ip;
	private String port;
	
	private RMI rmiTarget;
	
	private RMIClientGUI_Function rmiGUI;
	
	private String serverPassphrase = RMIClient_Authenticator.getServerPassphrase();
	
	private long time;
	
	
	public static void main (String [] args) {
		RMIClient_RemoteTimer rmi_RT = new RMIClient_RemoteTimer("127.0.0.1","1099");
		long time = rmi_RT.getTimer();
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String temp = formatter.format(new Date(time));
		
		System.out.println("Remote System Time: " + temp); //Debugger
	}
	
	
	public RMIClient_RemoteTimer(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}
	
	
	public RMIClient_RemoteTimer(String ip, String port, RMIClientGUI_RemoteTimer rmiGUI) {
		this.ip = ip;
		this.port = port;
		this.rmiGUI = rmiGUI;
	}
	
	
	public long getTimer() {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			time = rmiTarget.getTimer(serverPassphrase);
			
		} catch (Exception exc) {
			grepDebug(exc);
			
			//Set the time to nothing
			time = 0;
		}
		
		return time;
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