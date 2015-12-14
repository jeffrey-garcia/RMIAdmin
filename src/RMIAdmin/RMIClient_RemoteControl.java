package RMIAdmin;


import java.rmi.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.color.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;

import java.io.*;
import java.net.*;


public class RMIClient_RemoteControl {
	private String ip;
	private String port;
	
	private RMI rmiTarget;
	
	private RMIClientGUI_Function rmiGUI;
	
	private String serverPassphrase = RMIClient_Authenticator.getServerPassphrase();
	
	
	public static void main (String [] args) {
		RMIClient_RemoteControl rmi_RC = new RMIClient_RemoteControl("127.0.0.1","1099");
	}
	
	
	public RMIClient_RemoteControl(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}
	
	
	public RMIClient_RemoteControl(String ip, String port, RMIClientGUI_RemoteControl rmiGUI) {
		this.ip = ip;
		this.port = port;
		this.rmiGUI = rmiGUI;
	}
	
	
	public byte [] captureScreen(Rectangle screenRect) {
		byte [] data = null;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			data = rmiTarget.captureScreen(serverPassphrase, screenRect);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
		
		return data;
	}
	
	
	public void keyPress(int keycode) {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			rmiTarget.keyPress(serverPassphrase, keycode);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
	}
	
	
	public void keyRelease(int keycode) {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			rmiTarget.keyRelease(serverPassphrase, keycode);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
	}
	
	
	public void mouseMove(int x, int y) {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			rmiTarget.mouseMove(serverPassphrase, x, y);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
	}
	
	
	public void mousePress(int buttons) {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			rmiTarget.mousePress(serverPassphrase, buttons);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
	}
	
	
	public void mouseRelease(int buttons) {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			rmiTarget.mouseRelease(serverPassphrase, buttons);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
	}
	
	
	public void mouseWheel(int wheelAmt) {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			rmiTarget.mouseWheel(serverPassphrase, wheelAmt);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
	}
	
	
	public Dimension getScreenSize() {
		Dimension d = null;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			d = rmiTarget.getScreenSize(serverPassphrase);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
		
		return d;
	}
	
	
	public boolean openRC() {
		boolean done = false;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			done = rmiTarget.openRC(serverPassphrase);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
		
		return done;
	}
	
	
	public boolean releaseRC() {
		boolean done = false;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			done = rmiTarget.releaseRC(serverPassphrase);
			
		} catch (Exception exc) {
			grepDebug(exc);
		}
		
		return done;
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