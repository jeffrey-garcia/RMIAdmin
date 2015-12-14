package RMIAdmin;


import java.rmi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;


public class RMIClient_FileBrowser {
	private String ip;
	private String port;
	private String shell;
	
	private RMI rmiTarget;
	
	private RMIClientGUI_Function rmiGUI;
	
	private String serverPassphrase = RMIClient_Authenticator.getServerPassphrase();
	
	
	public RMIClient_FileBrowser(String ip, String port, String shell) {
		this.ip = ip;
		this.port = port;
		this.shell = shell;
	}
	
	
	public RMIClient_FileBrowser(String ip, String port, String shell, RMIClientGUI_Function rmiGUI) {
		this.ip = ip;
		this.port = port;
		this.shell = shell;
		
		this.rmiGUI = rmiGUI;
	}
	
	
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
			System.out.println("5. Invalid RMI config");
			
			if (rmiGUI == null) System.exit(0);
		}
	}
	
	
	public String [] openFileSystem() {
		String [] drive = null;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			//System.out.println("Invoking Remote Host at IP " + ip + " on port " + port + "."); //Debugger
			//System.out.println("Connection Status: " + rmiTarget.isConnected(serverPassphrase)); //Debugger
			//System.out.println("Remote Operating System: " + rmiTarget.getOS(serverPassphrase)); //Debugger
			
			grepDebug("Invoking Remote Host at IP " + ip + " on port " + port + ".");
			grepDebug("Connection Status: " + rmiTarget.isConnected(serverPassphrase));
			grepDebug("Remote Operating System: " + rmiTarget.getOS(serverPassphrase));
			
			drive = rmiTarget.openFileSystem(serverPassphrase);
			
			if (rmiGUI == null) {
				if (drive != null) {
					for (int i=0; i<drive.length; i++) {
						System.out.println(drive [i]);
					}
				} else {
					System.out.println("Operation failed, possible reasons::");
					System.out.println("====================================");
					System.out.println("1. Remote Drive not ready (e.g. Floppy/CD-Rom)");
					System.out.println("2. Drive is accessible (e.g. Root/Administrator)");
					System.out.println("3. Drive has insufficient disk space");
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return drive;
	}
	
	
	public String [][] getFileList(String location) {
		String [][] fList = null;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			fList = rmiTarget.getFileList(serverPassphrase, location);
			
			if (rmiGUI == null) {
				if (fList != null) {
					for (int i=0; i<fList.length; i++) {
						for (int j=0; j<fList[i].length; j++) {
							if (j==0) {
								System.out.print(fList[i][j] + "   ");
							} else if (j==1) {
								System.out.println("[" + fList[i][j] + "]");
							}
						}
					}
					
				} else {
					System.out.println("Operation failed, possible reasons::");
					System.out.println("====================================");
					System.out.println("1. Remote Drive not ready (e.g. Floppy/CD-Rom)");
					System.out.println("2. Drive is accessible (e.g. Root/Administrator)");
					System.out.println("3. Target is a file, not directory");
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return fList;
	}
	
	
	public Vector getFileInfo(String location) {
		Vector vFileInfo = null;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			vFileInfo = rmiTarget.getFileInfo(serverPassphrase, location);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return vFileInfo;
	}
	
	
	private void showFileInfo(String location) {
		Vector vFileInfo = null;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			vFileInfo = rmiTarget.getFileInfo(serverPassphrase, location);
			
			if (rmiGUI == null) {
				if (vFileInfo != null) {
					System.out.println("File Name: " + vFileInfo.elementAt(0));
					System.out.println("Canonical Path: " + vFileInfo.elementAt(1));
					System.out.println("Path: " + vFileInfo.elementAt(2));
					System.out.println("Parent Path: " + vFileInfo.elementAt(3));
					if ((vFileInfo.elementAt(4)+"").equals("true")) {
						System.out.println("Type: Directory");
					} else {
						System.out.println("Type: File");
					}
					if ((vFileInfo.elementAt(6)+"").equals("true")) {
						System.out.println("Hidden: Yes");
					} else {
						System.out.println("Hidden: False");
					}
					if ((vFileInfo.elementAt(7)+"").equals("true")) {
						System.out.println("Readable: Yes");
					} else {
						System.out.println("Readable: False");
					}
					if ((vFileInfo.elementAt(8)+"").equals("true")) {
						System.out.println("Writable: Yes");
					} else {
						System.out.println("Writable: False");
					}
					System.out.println("Last Modified: " + new Date(Long.parseLong(vFileInfo.elementAt(9)+"")));
					System.out.println("Size: " + getRoundedFileSize((new Long(getFileSize(location)+"").longValue())));
					System.out.println("");
					
				} else {
					System.out.println("Operation failed, possible reasons::");
					System.out.println("====================================");
					System.out.println("1. Remote Drive not ready (e.g. Floppy/CD-Rom)");
					System.out.println("2. Drive is accessible (e.g. Root/Administrator)");
					System.out.println("3. Target does not exist");
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
	}
	
	
	public String createFile(String location, String type) {
		String complete = "false";
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			complete = rmiTarget.createFile(serverPassphrase, location, type);
			
			if (rmiGUI == null) {
				if (complete.equals("true")) {
					System.out.println("Creation of [" + type + "] \"" + location + "\" success");
				} else {
					System.out.println("Creation of [" + type + "] \"" + location + "\" failed, possible reasons:");
					System.out.println("===================================================");
					System.out.println("1. Remote Drive not ready (e.g. Floppy/CD-Rom)");
					System.out.println("2. Drive is accessible (e.g. Root/Administrator)");
					System.out.println("3. Target already exist");
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return complete;
	}
	
	
	public String deleteFile(String location) {
		String complete = "false";
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			complete = rmiTarget.deleteFile(serverPassphrase, location);
			
			if (rmiGUI == null) {
				if (complete.equals("true")) {
					System.out.println("Deletion \"" + location + "\" success");
				} else {
					System.out.println("Deletion \"" + location + "\" failed, possible reasons:");
					System.out.println("=======================================================");
					System.out.println("1. Remote Drive not ready (e.g. Floppy/CD-Rom)");
					System.out.println("2. Drive is accessible (e.g. Root/Administrator)");
					System.out.println("3. Target does not exist");
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return complete;
	}
	
	
	public String renameFile(String srcLocation, String destLocation) {
		String complete = "false";
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			complete = rmiTarget.renameFile(serverPassphrase, srcLocation, destLocation);
			
			if (rmiGUI == null) {
				if (complete.equals("true")) {
					System.out.println("Renaming \"" + srcLocation + 
										"\" to \"" + destLocation + 
										"\" success");
				} else {
					System.out.println("Renaming \"" + srcLocation + 
										"\" to \"" + destLocation + 
										"\" failed, possible reasons:");
					System.out.println("=======================================================");
					System.out.println("1. Remote Drive not ready (e.g. Floppy/CD-Rom)");
					System.out.println("2. Drive is accessible (e.g. Root/Administrator)");
					System.out.println("3. Source does not exist");
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return complete;
	}
	
	
	public String copyAndPaste(String srcLocation, String destLocation) {
		String complete = "false";
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			complete = rmiTarget.copyAndPaste(serverPassphrase, srcLocation, destLocation);
			
			if (rmiGUI == null) {
				if (complete.equals("true")) {
					System.out.println("Copying \"" + srcLocation + 
										"\" to \"" + destLocation + 
										"\" success");
				} else {
					System.out.println("Copying \"" + srcLocation + 
										"\" to \"" + destLocation + 
										"\" failed, possible reasons:");
					System.out.println("=======================================================");
					System.out.println("1. Remote Drive not ready (e.g. Floppy/CD-Rom)");
					System.out.println("2. Drive is accessible (e.g. Root/Administrator)");
					System.out.println("3. Source does not exist");
					System.out.println("4. Target path does not exist");
					System.out.println("5. Insufficient disk space");
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return complete;
	}
	
	
	public String cutAndPaste(String srcLocation, String destLocation) {
		String complete = "false";
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			complete = rmiTarget.cutAndPaste(serverPassphrase, srcLocation, destLocation);
			
			if (rmiGUI == null) {
				if (complete.equals("true")) {
					System.out.println("Moving \"" + srcLocation + 
										"\" to \"" + destLocation + 
										"\" success");
				} else {
					System.out.println("Moving \"" + srcLocation + 
										"\" to \"" + destLocation + 
										"\" failed, possible reasons:");
					System.out.println("=======================================================");
					System.out.println("1. Remote Drive not ready (e.g. Floppy/CD-Rom)");
					System.out.println("2. Drive is accessible (e.g. Root/Administrator)");
					System.out.println("3. Source does not exist");
					System.out.println("4. Target path does not exist");
					System.out.println("5. Insufficient disk space");
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return complete;
	}
	
	
	public boolean checkDuplicateTarget(String srcFile, String destLocation) {
		boolean duplicate = false;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			duplicate = rmiTarget.checkDuplicateTarget(serverPassphrase, srcFile, destLocation);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return duplicate;
	}
	
	
	public int countObject(String srcFile) {
		int totalObject = 0;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			totalObject = rmiTarget.countObject(serverPassphrase, srcFile);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return totalObject;
	}
	
	
	public long getFileSize(String srcLocation) {
		long size = 0;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			size = rmiTarget.getFileSize(serverPassphrase, srcLocation);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return size;
	}
	
	
	public String getRoundedFileSize(String srcLocation) {
		String size = "0 Bytes(s)";
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			size = rmiTarget.getRoundedFileSize(serverPassphrase, srcLocation);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return size;
	}
	
	
	public String getRoundedFileSize(long sizeInLong) {
		String size = "0 Bytes(s)";
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			size = rmiTarget.getRoundedFileSize(serverPassphrase, sizeInLong);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return size;
	}
	
	
	public String getFileSeparator() {
		String separator = File.separator;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			separator = rmiTarget.getFileSeparator(serverPassphrase);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return separator;
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
	
	
	public static void main(String [] args) {
		String ip = "";
		String port = "";
		String shell = "";
		String option = "";
		Vector vParam = new Vector();
		
		try {
			for (int i=0; i<args.length; i++) {
				if (args[i].equals("-i")) { 
					ip = args [i+1];
					
				} else if (args[i].equals("-p")) { 
					port = args [i+1];
					
				} else if (args[i].equals("-s")) { 
					shell = args [i+1];
							
				} else if (args[i].equals("-h")) {
					helpMenu();
					
				} else if (args[i].equals("-o")) {
					option = args [i+1];
					option = option.toLowerCase();
					vParam.removeAllElements();
					
					for (int j=i+2; j<args.length; j++) {
						String tmp = args [j];
						
						/*******************************
						 * This part handle the problem 
						 * of accepting \" & \ & " as 
						 * parameters
						 ******************************/
						if (tmp.indexOf("\"") == tmp.length()-1) {
							tmp = tmp.substring(0, tmp.indexOf("\"")) + "\\";
							//System.out.println(tmp); //Debugger
						} else {
							//System.out.println(tmp); //Debugger
							//nothing to do...
						}
						//System.out.println(tmp); //Debugger
						
						if (tmp.equals("-i") || 
							tmp.equals("-p") || 
							tmp.equals("-h") || 
							tmp.equals("-s") || 
							tmp.equals("-o")
						) {break;}
						
						vParam.addElement(tmp);
					}
				}
			}
			
			if (ip.length() == 0) {
				errorParameter("Missing -i \"IP Address\" value, use the -h option to view the help manual.");
				
			} else if (port.length() == 0) {
				errorParameter("Missing -p \"Port Number\" value, use the -h option to view the help manual.");
				
			} else if (shell.length() == 0) {
				errorParameter("Missing -s \"Shell\" value, use the -h option to view the help manual.");
				
			} else if (option.length() == 0) {
				errorParameter("Missing -o \"option\" value, available options:\n" + 
								" listRoots \n" + 
								" listFiles \n" + 
								" getFileInfo \n" + 
								" create \n" + 
								" delete \n" + 
								" rename \n" + 
								" copy \n" + 
								" move");
			}
			
			//System.out.println(ip); //Debugger
			//System.out.println(port); //Debugger
			//System.out.println(shell); //Debugger
			//System.out.println(option); //Debugger
			//System.out.println(vParam); //Debugger
			//System.out.println(); //Debugger
			
			invokeFunction(ip, port, shell, option, vParam);
			
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
		System.out.println("Perform File Operation on Remote Computer via RMIAdmin");
		System.out.println();
		System.out.println();
		System.out.println("SYNOPSIS");
		System.out.println("=============================");
		System.out.println("java RMIClient_FileBrowser -options \"values\"");
		System.out.println();
		System.out.println();
		System.out.println("DESCRIPTION");
		System.out.println("=============================");
		System.out.println("-i\tSpecify the \"IP Address\" of remote RMIServer e.g. \"203.186.94.62\"");
		System.out.println();
		System.out.println("-p\tSpecify the \"Port Number\" of remote RMIServer e.g. \"1091\"");
		System.out.println();
		System.out.println("-s\tSpecify the \"Shell\" of remote RMIServer e.g. \"cmd.exe\" or \"/bin/bash\"");
		System.out.println();
		System.out.println("-o\tSpecify the \"Operation\" of file on remote RMIServer");
		System.out.println("\t");
		System.out.println("\tlistRoots");
		System.out.println("\tlist the available drives on Remote Server");
		System.out.println();
		System.out.println("\tlistFiles \"Target PathName\"");
		System.out.println("\tlist all files or directories of the specified path on Remote Server");
		System.out.println();
		System.out.println("\tgetFileInfo \"Target PathName\"");
		System.out.println("\tget all properties of the file or directory specified on Remote Server");
		System.out.println();
		System.out.println("\tcreate \"Target PathName\" \"File Type [Directory|File]\"");
		System.out.println("\tcreate file or directory on Remote Server");
		System.out.println();
		System.out.println("\tdelete \"Target PathName\"");
		System.out.println("\tdelete file or directory on Remote Server");
		System.out.println();
		System.out.println("\trename \"Original PathName\" \"Modified PathName\"");
		System.out.println("\trename file or directory on Remote Server");
		System.out.println();
		System.out.println("\tcopy \"Source PathName\" \"Destination Path\"");
		System.out.println("\tcopy file or directory on Remote Server");
		System.out.println();
		System.out.println("\tcopy \"Source PathName\" \"Destination Path\" -r");
		System.out.println("\toverwrite even if target exist");
		System.out.println();
		System.out.println("\tmove \"Source PathName\" \"Destination Path\"");
		System.out.println("\tmove file or directory on Remote Server");
		System.out.println();
		System.out.println("\tmove \"Source PathName\" \"Destination Path\" -r");
		System.out.println("\toverwrite even if target exist");
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
	
	
	private static void invokeFunction(String ip, String port, String shell, String option, Vector vParam) {
		RMIClient_FileBrowser fb;
		
		try {
			if (option.equals("listroots")) {
				fb = new RMIClient_FileBrowser(ip, port, shell);
				fb.connect();
				fb.openFileSystem();
				
			} else if (option.equals("listfiles")) {
				if (vParam.size() < 1)
				errorParameter("Please specify the target location e.g. -o listFiles \"PathName\"");
				
				fb = new RMIClient_FileBrowser(ip, port, shell);
				fb.connect();
				fb.getFileList(vParam.elementAt(0) + "");
				
			} else if (option.equals("getfileinfo")) {
				if (vParam.size() < 1)
				errorParameter("Please specify the target location e.g. -o getFileInfo \"PathName\"");
				
				fb = new RMIClient_FileBrowser(ip, port, shell);
				fb.connect();
				fb.showFileInfo(vParam.elementAt(0) + "");
				
			} else if (option.equals("create")) {
				if (vParam.size() < 1)
				errorParameter("Please specify the target location e.g. -o create \"PathName\" Directory|File");
				
				if (vParam.size() < 2)
				errorParameter("Please specify the file type e.g. -o create \"PathName\" Directory|File");
				
				if ((vParam.elementAt(1)+"").equals("Directory")==false && 
					(vParam.elementAt(1)+"").equals("File")==false)
				errorParameter("Incorrect file type specified e.g. -o create \"PathName\" Directory|File");
				
				fb = new RMIClient_FileBrowser(ip, port, shell);
				fb.connect();
				fb.createFile(vParam.elementAt(0)+"", vParam.elementAt(1)+"");
				
			} else if (option.equals("delete")) {
				if (vParam.size() < 1)
				errorParameter("Please specify the target location e.g. -o delete \"PathName\"");
				
				fb = new RMIClient_FileBrowser(ip, port, shell);
				fb.connect();
				fb.deleteFile(vParam.elementAt(0) + "");
				
			} else if (option.equals("rename")) {
				if (vParam.size() < 1)
				errorParameter("Please specify the source name e.g. -o rename \"Source PathName\" \"Destination PathName\"");
				
				if (vParam.size() < 2)
				errorParameter("Please specify the target name e.g. -o rename \"Source PathName\" \"Destination PathName\"");
				
				fb = new RMIClient_FileBrowser(ip, port, shell);
				fb.connect();
				fb.renameFile(vParam.elementAt(0)+"", vParam.elementAt(1)+"");
				
			} else if (option.equals("copy")) {
				String replace = "n";
				
				if (vParam.size() < 1)
				errorParameter("Please specify the source location e.g. -o copy \"Source PathName\" \"Destination Path\"");
				
				if (vParam.size() < 2)
				errorParameter("Please specify the target location e.g. -o copy \"Source PathName\" \"Destination Path\"");
				
				if (vParam.size() > 2) {
					for (int i=0; i<vParam.size(); i++) {
						replace = vParam.elementAt(i) + "";
						replace = replace.toLowerCase();
						
						if (replace.equals("-r")) {
							replace = "y";
							break;
						}
					}
				}
				
				fb = new RMIClient_FileBrowser(ip, port, shell);
				fb.connect();
				
				Vector vFileInfo = fb.getFileInfo(vParam.elementAt(0)+"");
				String fileName = vFileInfo.elementAt(0) + "";
				String separator = fb.getFileSeparator();
				
				if (fb.checkDuplicateTarget(fileName, vParam.elementAt(1)+"") == true) {
					if (replace.equals("y")) {
						fb.copyAndPaste(vParam.elementAt(0)+"", vParam.elementAt(1)+"");
					} else {
						System.out.print("Duplicate object exist, overwrite? [y|n]: ");
						replace = new BufferedReader(new InputStreamReader(System.in)).readLine();
						replace = replace.toLowerCase();
						
						if (replace.indexOf("y")==0) {
							fb.copyAndPaste(vParam.elementAt(0)+"", vParam.elementAt(1)+"");
						} else {
							System.out.println("Copy aborted");
						}
					}
					
				} else {
					fb.copyAndPaste(vParam.elementAt(0)+"", vParam.elementAt(1)+"");
				}
				
			} else if (option.equals("move")) {
				String replace = "n";
				
				if (vParam.size() < 1)
				errorParameter("Please specify the source location e.g. -o move \"Source PathName\" \"Destination Path\"");
				
				if (vParam.size() < 2)
				errorParameter("Please specify the target location e.g. -o move \"Source PathName\" \"Destination Path\"");
				
				if (vParam.size() > 2) {
					for (int i=0; i<vParam.size(); i++) {
						replace = vParam.elementAt(i) + "";
						replace = replace.toLowerCase();
						
						if (replace.equals("-r")) {
							replace = "y";
							break;
						}
					}
				}
				
				fb = new RMIClient_FileBrowser(ip, port, shell);
				fb.connect();
				
				Vector vFileInfo = fb.getFileInfo(vParam.elementAt(0)+"");
				String fileName = vFileInfo.elementAt(0) + "";
				String separator = fb.getFileSeparator();
				
				if (fb.checkDuplicateTarget(fileName, vParam.elementAt(1)+"") == true) {
					if (replace.equals("y")) {
						fb.cutAndPaste(vParam.elementAt(0)+"", vParam.elementAt(1)+"");
					} else {
						System.out.print("Duplicate object exist, overwrite? [y|n]: ");
						replace = new BufferedReader(new InputStreamReader(System.in)).readLine();
						replace = replace.toLowerCase();
						
						if (replace.indexOf("y")==0) {
							fb.cutAndPaste(vParam.elementAt(0)+"", vParam.elementAt(1)+"");
						} else {
							System.out.println("Move aborted");
						}
					}
					
				} else {
					fb.cutAndPaste(vParam.elementAt(0)+"", vParam.elementAt(1)+"");
				}
				
			} else {
				errorParameter("Invalid Option: " + option);
			}
			
		} catch (Exception exc) {
			//exc.printStackTrace();
			helpMenu();
		}
	}
}