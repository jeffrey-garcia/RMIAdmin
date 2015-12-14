package RMIAdmin;


import java.rmi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;


/**
 * The <code>RMIClient_CommandLine</code> class is a text-mode program, 
 * it allows the execution of Operating System's command at remote 
 * server via the RMI protocol.
 *
 * <p>
 *
 * Copyright (c) 2005, rmiAdmin.net. All Rights Reserved.
 *
 * <p>
 *
 * We grants you ("Licensee") a non-exclusive, royalty free, license to use, 
 * modify and redistribute this software in source and binary code form, 
 * provided that i) this copyright notice and license appear on all copies of 
 * the software; and ii) Licensee does not utilize the software in a manner 
 * which is disparaging to us. 
 *
 * <p>
 *
 * This software is provided "as is," without a warranty of any kind. all 
 * express or implied conditions, representations and warranties, including any 
 * implied warranty of merchantability, fitness for a particular purpose or 
 * non-infringement, are hereby excluded. we and its licensors shall not be 
 * liable for any damages suffered by licensee as a result of using, modifying 
 * or distributing the software or its derivatives. in no event shall we or its 
 * licensors be liable for any lost revenue, profit or data, or for direct, 
 * indirect, special, consequential, incidental or punitive damages, however 
 * caused and regardless of the theory of liability, arising out of the use of 
 * or inability to use software, even if we have been advised of the 
 * possibility of such damages.
 *
 * <p>
 *
 * @author Jeffrey Garcia
 * <p>
 *
 * @version 1.0 since Sept 2005
 * <p>
 *
 * @see	<p><a href="../api/RMIClient_CommandLine.html">RMIClient_CommandLine</a>
 * @see	<p><a href="../api/RMIClient_FileBrowser.html">RMIClient_FileBrowser</a>
 * @see	<p><a href="../api/RMI_FileTransfer.html">RAC_FileBrowser</a>
 */
public class RMIClient_CommandLine {
	/**
	 * A <b>private</b> String which holds the IP Address of the Remote Server 
	 */
	private String ip;
	/**
	 * A <b>private</b> String which holds the Port Number of the Remote Server
	 */
	private String port;
	/** A <b>private</b> String which holds the Operating System's Shell 
	 * of the Remote Server. <i>e.g. For Windows: cmd.exe, for Linux: 
	 * /bin/bash</i> */
	private String shell;
	/** 
	 * A <b>private</b> String which holds the Command to be executed at Remote Server 
	 */
	private String command;
	/**
	 * A <b>private</b> RMI object which holds the Remote Server Object 
	 */
	private RMI rmiTarget;
	/** A <b>private</b> String which holds the remote Thread Command Name
	 * tracking the execution status & progress */
	private String threadName;
	/** A <b>private</b> Object which is the GUI container passed in from 
	 * other invoking classes, for any necessary GUI activities */
	private RMIClientGUI_CommandLine rmiGUI_CLI;
	/** A <b>private</b> boolean which holds the command execution progress 
	 * at Remote Server */
	private boolean completed = false;
	
	private String serverPassphrase = RMIClient_Authenticator.getServerPassphrase();
	
	
	/**
	 * Create an instance of RMIClient_CommandLine.
	 * <p>
	 * This is a default constructor, we must pass 
	 * in the:
	 * <ul>
	 *	<li>IP Address of Remote Server </li>
	 *	<li>Service Port Number of Remote Server </li>
	 *	<li>Operating System Shell of Remote Server </li>
	 *	<li>Command to be executed at Remote Server </li>
	 * </ul>
	 */
	public RMIClient_CommandLine(String ip, String port, String shell, String command) {
		this.ip = ip;
		this.port = port;
		this.shell = shell;
		this.command = command;
	}
	
	
	/**
	 * Create an instance of RMIClient_CommandLine.
	 * <p>
	 * This is another default constructor, we must 
	 * pass in the:
	 * <ul>
	 *	<li>IP Address of Remote Server </li>
	 *	<li>Service Port Number of Remote Server </li>
	 *	<li>Operating System Shell of Remote Server </li>
	 *	<li>Command to be executed at Remote Server </li>
	 *	<li>Object for GUI event handling </li>
	 * </ul>
	 */
	public RMIClient_CommandLine(String ip, String port, String shell, String command, RMIClientGUI_CommandLine rmiGUI_CLI) {
		this.ip = ip;
		this.port = port;
		this.shell = shell;
		this.command = command;
		
		this.rmiGUI_CLI = rmiGUI_CLI;
	}
	
	
	/**
	 * A <b>public</b> method to test the 
	 * connection between local client and 
	 * remote server.
	 * <p>
	 * This method will try to invoke a 
	 * method on the remote server, and 
	 * the exception will be caught to 
	 * indicate a failure in establishing 
	 * connection with remote server.
	 * <p>
	 * Note: Upon invoking the mothod on 
	 * remote server, exceptions may be 
	 * caught depending on the error, but 
	 * we've trapped all kind of exceptions 
	 * using the Exception class, and we 
	 * display the same error message for 
	 * all possible errors. If this method 
	 * is not invoked from the GUI, we will 
	 * quit the program immediately when 
	 * connection fails.
	 * <p>
	 * <b>Possible errors</b>:
	 * <ul>
	 *	<li>Remote host is physically down (server shutdown)</li>
	 *	<li>Remote service not available (RMI service is dead)</li>
	 *	<li>Network linkage failure (ISP routing error)</li>
	 *	<li>Local host network problem (ethernet hardware/config error)</li>
	 *	<li>Invalid RMI config (client programs does not match with server programs)</li>
	 * </ul>
	 * <p>
	 * <i>Error code for problem diagnostic will be available in next version.</i>
	 */
	public void connect() {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			rmiTarget.isConnected(serverPassphrase);
			
		} catch (Exception exc) {
			//exc.printStackTrace();
			System.out.println("Error connecting to remote host fail, possible reasons:");
			System.out.println("=======================================================");
			System.out.println("1. Remote host is physically down");
			System.out.println("2. Remote service not available");
			System.out.println("3. Network linkage failure");
			System.out.println("4. Local host network problem");
			System.out.println("5. Invalid RMI config [Incorrect Name Binding]");
			
			if (rmiGUI_CLI == null) System.exit(0);
		}
	}
	
	
	/**
	 * A <b>public</b> method to execute 
	 * command at remote server.
	 * <p>
	 * This method is the entry point of 
	 * remote command execution, as we will 
	 * passed the shell and command to remote 
	 * server where the thread name of the 
	 * processing thread will be returned. 
	 * This design assist us to trace the 
	 * execution status locally by means of 
	 * communicating the remote thread 
	 * periodically via its thread name.
	 * <p>
	 * Note: A local thread (threadRun) is 
	 * implemented in the design, which is 
	 * used to encapsulate the tracking 
	 * process. If any exception is caught, 
	 * the status will be immediately set 
	 * to true.
	 */
	public void rexec() {
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			//System.out.println("Invoking Remote Host at IP " + ip + " on port " + port + "."); //Debugger
			//System.out.println("Connection Status: " + rmiTarget.isConnected(serverPassphrase)); //Debugger
			//System.out.println("Remote Operating System: " + rmiTarget.getOS(serverPassphrase)); //Debugger
			
			grepDebug("Invoking Remote Host at IP " + ip + " on port " + port + ".");
			grepDebug("Connection Status: " + rmiTarget.isConnected(serverPassphrase));
			grepDebug("Remote Operating System: " + rmiTarget.getOS(serverPassphrase));
			
			threadName = rmiTarget.setCommand(serverPassphrase, shell, command);
			
			//System.out.println("Remote Thread Process Name: " + threadName); //Debugger
			grepDebug("Remote Thread Process Name: " + threadName);
			
			new threadRun().start();
			
		} catch (Exception exc) {
			completed = true;
			exc.printStackTrace();
			grepDebug(exc.getMessage());
			grepDebug(exc);
		}
	}
	
	
	/**
	 * A <b>public</b> method to terminate 
	 * an executing command at remote 
	 * server.
	 * <p>
	 * This method is used to terminate 
	 * the thread process on remote server 
	 * which the command process resident.
	 * <p>
	 * Note: A local thread (threadCancel 
	 * class) is implemented in the design, 
	 * which is used to encapsulate the 
	 * interruption process. 
	 */
	public void cancelExec() {
		new threadCancel().start();
	}
	
	
	/**
	 * A <b>public</b> method to get 
	 * the command execution status.
	 * server.
	 * <p>
	 * This method is used to retrieve 
	 * the completion status of the 
	 * thread process on remote server 
	 * where the executed command resident.
	 * <p>
	 * @return true if the remote thread process 
	 * is completed; false if the remote 
	 * thread is still in progress.
	 */
	public boolean getCompleteStatus() {
		return completed;
	}
	
	
	/**
	 * A <b>private</b> method to print 
	 * message to GUI.
	 * <p>
	 * This method is used to print 
	 * message to the GUI (JTextArea) 
	 * for error tracing.
	 * <p>
	 * Note:
	 * <p>
	 * @param text the message to be 
	 * displayed
	 */
	private void grepOutput(String text) {
		if (rmiGUI_CLI != null) {
			rmiGUI_CLI.jtaOutput.append(text);
			rmiGUI_CLI.jtaOutput.setCaretPosition(rmiGUI_CLI.jtaOutput.getDocument().getLength());
		}
	}
	
	
	/**
	 * A <b>private</b> method to print 
	 * any exception stack trace to GUI.
	 * <p>
	 * This method is used to print 
	 * the exception stack trace to the 
	 * GUI (JTextArea) for error tracing.
	 * <p>
	 * Note: If no GUI handler, this 
	 * part will be skipped.
	 * <p>
	 * @param exc the exception to be 
	 * displayed
	 */
	private void grepDebug(Exception exc) {
		if (rmiGUI_CLI != null) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiGUI_CLI.getDebugger().setDebugText(error);
			rmiGUI_CLI.warning(exc.getMessage());
		}
	}
	
	
	/**
	 * A <b>private</b> method to print 
	 * message to GUI.
	 * <p>
	 * This method is used to print 
	 * message to the GUI (JTextArea) 
	 * for error tracing.
	 * <p>
	 * Note: If no GUI handler, this 
	 * part will be skipped.
	 * <p>
	 * @param text the message to be 
	 * displayed
	 */
	private void grepDebug(String text) {
		if (rmiGUI_CLI != null) {
			rmiGUI_CLI.getDebugger().setDebugText(text);
		}
	}
	
	
	public class threadRun extends Thread {
		LinkedList llOutput = new LinkedList();
		
		public void run() {
			try {
				rmiTarget.runCommand(serverPassphrase, threadName);
				
				Thread.sleep(2000);  //wait 2 second until the thread started
				
				while (rmiTarget.checkCommandStatus(serverPassphrase, threadName) == false) {
					//System.out.println(threadName + " waiting for results..."); //Debugger
					grepDebug(threadName + " waiting for results...");
					
					llOutput = rmiTarget.getCommandResult(serverPassphrase, threadName);
					for (int i=0; i<llOutput.size(); i++) {
						System.out.println(llOutput.get(i)); //Debugger
						grepOutput(llOutput.get(i) + "\n");
					}
					
					Thread.sleep(2000); //wait for 2 second, then re-check
				}
				
				llOutput = rmiTarget.getCommandResult(serverPassphrase, threadName);
				for (int i=0; i<llOutput.size(); i++) {
					System.out.println(llOutput.get(i)); //Debugger
					grepOutput(llOutput.get(i) + "\n");
				}
				
			} catch (Exception exc) {
				exc.printStackTrace();
				grepDebug(exc);
				
			} finally {
				completed = true;
			}
		}
	}
	
	
	public class threadCancel extends Thread {
		public void run() {
			try {
				System.out.print("\nInterrupting Thread: " + threadName + "... ");
				grepDebug("\nInterrupting Thread: " + threadName + "... ");
				
				rmiTarget.cancelCommand(serverPassphrase, threadName);
				
				System.out.println("Done.");
				grepDebug("Done.\n");
				
			} catch (Exception exc) {
				exc.printStackTrace();
				grepDebug(exc);
				
			} finally {
				completed = true;
			}
		}
	}
	
	
	/**
	 * The entry point of this class
	 * <p>
	 * @param args An array of String containing 
	 * all the options and parameters is passed 
	 * in.
	 * <p>
	 * Note: If any -option arguments is 
	 * missing, the program will immediately 
	 * jump to the helpMenu method() which 
	 * display the complete usage guide, and 
	 * then quit automatically.<br>
	 * Else if the value following the -option 
	 * arguments is missing, the program will 
	 * immediately jump to the errorParameter
	 * (String error) method to show the correct 
	 * usage of that option, and quit automatically.
	 */
	public static void main(String [] args) {
		String ip = "";
		String port = "";
		String shell = "";
		String command = "";
		
		try {
			for (int i=0; i<args.length; i++) {
				if (args[i].equals("-i")) { 
					ip = args [i+1];
					
				} else if (args[i].equals("-p")) { 
					port = args [i+1];
					
				} else if (args[i].equals("-s")) { 
					shell = args [i+1];
					
				} else if (args[i].equals("-c")) { 
					command = args [i+1];
					
					/*******************************
					 * This part handle the problem 
					 * of accepting \" & \ & " as 
					 * parameters
					 ******************************/
					if (command.indexOf("\"") == command.length()-1) {
						command = command.substring(0, command.indexOf("\"")) + "\\";
						//System.out.println(command); //Debugger
					} else {
						//System.out.println(command); //Debugger
						//nothing to do...
					}
					
					command = "\"" + command + "\"";
					
				} else if (args[i].equals("-h")) {
					helpMenu();
				}
			}
			
			if (ip.length() == 0) {
				errorParameter("Missing -i \"IP Address\" value");
				
			} else if (port.length() == 0) {
				errorParameter("Missing -p \"Port Number\" value");
				
			} else if (shell.length() == 0) {
				errorParameter("Missing -s \"Shell\" value");
				
			} else if (command.length() == 0) {
				errorParameter("Missing -c \"Command\" value");
			}
			
			//System.out.println(ip); //Debugger
			//System.out.println(port); //Debugger
			//System.out.println(shell); //Debugger
			//System.out.println(command); //Debugger
			
			RMIClient_CommandLine cl;
			cl = new RMIClient_CommandLine(ip, port, shell, command);
			cl.connect();
			cl.rexec();
			
		} catch (Exception exc) {
			//exc.printStackTrace(); //Debugger
			helpMenu();
		}
	}
	
	
	/**
	 * A <b>private static</b> method 
	 * to print message on the console.
	 * <p>
	 * This method is used to print 
	 * message to the console, aims at 
	 * providing hints for user on the 
	 * correct usage relevant to the 
	 * specific option, and then the 
	 * program will exit itself.
	 * <p>
	 * @param error The String (hints) 
	 * to be displayed for users.
	 */
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
	
	
	/**
	 * A <b>private static</b> method 
	 * to print message on the console.
	 * <p>
	 * This method is used to print 
	 * the complete usage guide to 
	 * the console, aims at providing 
	 * a manual for user about the usage 
	 * of this program via command line, 
	 * and then the program will exit 
	 * itself.
	 */
	private static void helpMenu() {
		System.out.println("NAME");
		System.out.println("=============================");
		System.out.println("Execute Command on Remote Computer via RMIAdmin");
		System.out.println();
		System.out.println();
		System.out.println("SYNOPSIS");
		System.out.println("=============================");
		System.out.println("java RMIClient_CommandLine -options \"values\"");
		System.out.println();
		System.out.println();
		System.out.println("DESCRIPTION");
		System.out.println("=============================");
		System.out.println("-i\tSpecify the \"IP Address\" of remote RMIServer e.g. \"203.186.94.62\"");
		System.out.println("-p\tSpecify the \"Port Number\" of remote RMIServer e.g. \"1091\"");
		System.out.println("-s\tSpecify the \"Shell\" of remote RMIServer e.g. \"cmd.exe\" or \"/bin/bash\"");
		System.out.println("-c\tSpecify the \"Command\" to be executed e.g. \"dir c:\\ && copy *txt ..\\\"");
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
}