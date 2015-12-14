package RMIAdmin;


import java.rmi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;


public class RMIClient_AutoUpgrade {
	private String ip;
	private String port;
	private String shell;
	
	private RMI rmiTarget;
	
	private RMIClientGUI_Function rmiGUI;
	
	private String serverPassphrase = RMIClient_Authenticator.getServerPassphrase();
	
	
	public static void main (String [] args) {
		RMIClient_AutoUpgrade rmi_AU = new RMIClient_AutoUpgrade("127.0.0.1","1055","cmd.exe");
		rmi_AU.updateServer("RMIAdmin.jar");
	}
	
	
	public RMIClient_AutoUpgrade(String ip, String port, String shell) {
		this.ip = ip;
		this.port = port;
		this.shell = shell;
	}
	
	
	public RMIClient_AutoUpgrade(String ip, String port, String shell, RMIClientGUI_AutoUpgrade rmiGUI) {
		this.ip = ip;
		this.port = port;
		this.shell = shell;
		this.rmiGUI = rmiGUI;
	}
	
	
	public boolean updateServer(String file) {
		String remoteWorkingDir = "";
		String localWorkingDir = "";
		boolean completed = false;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			remoteWorkingDir = rmiTarget.getCurrPath(serverPassphrase);
			System.out.println("Remote Dir = " + remoteWorkingDir); //Debugger
			
			File f = new File(new File(file).getAbsolutePath());
			localWorkingDir = f.getParent();
			System.out.println("Local Dir = " + localWorkingDir);
			
			if (remoteWorkingDir.equals("") != true) {
				RMIClient_FileTransferLocal rmi_FT = new RMIClient_FileTransferLocal(ip,port,shell);
				rmi_FT.uploadFile_Bin(localWorkingDir + File.separator + file, remoteWorkingDir + rmi_FT.getFileSeparator() + file);
			}
			
			completed = true;
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
			remoteWorkingDir = "";
			
			completed =false;
			
		} finally {
			return completed;
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
}