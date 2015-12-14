package RMIAdmin;


import java.rmi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;


public class RMIClient_FileTransfer {
	private String ip;
	private String port;
	private String shell;
	
	private RMI rmiTarget;
	
	private RMIClientGUI_Function rmiGUI;
	
	
	public RMIClient_FileTransfer(String ip, String port, String shell) {
		this.ip = ip;
		this.port = port;
		this.shell = shell;
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
						System.out.println(tmp);
						
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
								" upload \n" + 
								" download \n");
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
		System.out.println("\tupload \"Source PathName\" \"Destination Path\" -a");
		System.out.println("\tupload file or directory to Remote Server in ASCII mode");
		System.out.println();
		System.out.println("\tupload \"Source PathName\" \"Destination Path\" -b");
		System.out.println("\tupload file or directory to Remote Server in BINARY mode");
		System.out.println();
		System.out.println("\tupload \"Source PathName\" \"Destination Path\" -[a|b] -r");
		System.out.println("\toverwrite even if target exist");
		System.out.println();
		System.out.println("\tdownload \"Source PathName\" \"Destination Path\" -a");
		System.out.println("\tdownload file or directory from Remote Server in ASCII mode");
		System.out.println();
		System.out.println("\tdownload \"Source PathName\" \"Destination Path\" -b");
		System.out.println("\tdownload file or directory from Remote Server in BINARY mode");
		System.out.println();
		System.out.println("\tdownload \"Source PathName\" \"Destination Path\" -[a|b] -r");
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
		RMIClient_FileTransferLocal ftl;
		RMIClient_FileTransferRemote ftr;
		
		try {
			if (option.equals("upload")) {
				String replace = "n";
				String mode = "ascii";
				
				if (vParam.size() < 1)
				errorParameter("Please specify the source location on local e.g. -o upload \"Source PathName\" \"Destination Path\"");
				
				if (vParam.size() < 2)
				errorParameter("Please specify the target location on remote server e.g. -o upload \"Source PathName\" \"Destination Path\"");
				
				if (vParam.size() < 3)
				errorParameter("Please specify the transfer mode [Ascii|Binary] \n" + 
								"e.g. -o upload \"Source PathName\" \"Destination Path\" -a|-b");
				
				if (vParam.size() > 2) {
					for (int i=0; i<vParam.size(); i++) {
						mode = vParam.elementAt(i) + "";
						mode = mode.toLowerCase();
						
						if (mode.equals("-a")) {
							mode = "ascii";
							break;
						} else if (mode.equals("-b")) {
							mode = "binary";
							break;
						}
					}
				}
				
				if (vParam.size() > 3) {
					for (int i=0; i<vParam.size(); i++) {
						replace = vParam.elementAt(i) + "";
						replace = replace.toLowerCase();
						
						if (replace.equals("-r")) {
							replace = "y";
							break;
						}
					}
				}
				
				ftl = new RMIClient_FileTransferLocal(ip, port, shell);
				ftl.connect();
				
				String srcPath = vParam.elementAt(0)+"";
				String destPath = vParam.elementAt(1)+"";
				String srcFile = new File(srcPath).getName();
				String separator = ftl.getFileSeparator();
				
				if (ftl.testPathExist(destPath) == true) {
					if (new File(srcPath).exists()) {
						if (ftl.checkRemoteDuplicateTarget(srcFile, vParam.elementAt(1)+"") == false) {
							if (mode.equals("ascii")) {
								ftl.uploadFile_Ascii(srcPath, destPath + separator + srcFile);
							} else if (mode.equals("binary")) {
								ftl.uploadFile_Bin(srcPath, destPath + separator + srcFile);
							} else {
								errorParameter("Please specify a valid transfer mode [Ascii|Binary] \n" + 
												"e.g. -o upload \"Source PathName\" \"Destination Path\" -a|-b");
							}
							
						} else {
							if (replace.equals("y")) {
								if (mode.equals("ascii")) {
									ftl.uploadFile_Ascii(srcPath, destPath + separator + srcFile);
								} else if (mode.equals("binary")) {
									ftl.uploadFile_Bin(srcPath, destPath + separator + srcFile);
								} else {
									errorParameter("Please specify a valid transfer mode [Ascii|Binary] \n" + 
													"e.g. -o upload \"Source PathName\" \"Destination Path\" -a|-b");
								}
								
							} else {
								System.out.print("Duplicate object exist, overwrite? [y|n]: ");
								replace = new BufferedReader(new InputStreamReader(System.in)).readLine();
								replace = replace.toLowerCase();
								
								if (replace.indexOf("y")==0) {
									if (mode.equals("ascii")) {
										ftl.uploadFile_Ascii(srcPath, destPath + separator + srcFile);
									} else if (mode.equals("binary")) {
										ftl.uploadFile_Bin(srcPath, destPath + separator + srcFile);
									} else {
										errorParameter("Please specify a valid transfer mode [Ascii|Binary] \n" + 
														"e.g. -o upload \"Source PathName\" \"Destination Path\" -a|-b");
									}
									
								} else {
									System.out.println("Upload aborted");
								}
							}
						}
						
					} else {
						System.out.println("Local file " + srcPath + " does not exist. Upload failed.");
						System.exit(0);
					}
					
				} else {
					System.out.println("Remote path " + destPath + " does not exist. Upload failed.");
					System.exit(0);
				}
			
			} else if (option.equals("download")) {
				String replace = "n";
				String mode = "ascii";
				
				if (vParam.size() < 1)
				errorParameter("Please specify the source location on remote server e.g. -o download \"Source PathName\" \"Destination Path\"");
				
				if (vParam.size() < 2)
				errorParameter("Please specify the target location on local e.g. -o download \"Source PathName\" \"Destination Path\"");
				
				if (vParam.size() < 3)
				errorParameter("Please specify the transfer mode [Ascii|Binary] \n" + 
								"e.g. -o download \"Source PathName\" \"Destination Path\" -a|-b");
				
				if (vParam.size() > 2) {
					for (int i=0; i<vParam.size(); i++) {
						mode = vParam.elementAt(i) + "";
						mode = mode.toLowerCase();
						
						if (mode.equals("-a")) {
							mode = "ascii";
							break;
						} else if (mode.equals("-b")) {
							mode = "binary";
							break;
						}
					}
				}
				
				if (vParam.size() > 3) {
					for (int i=0; i<vParam.size(); i++) {
						replace = vParam.elementAt(i) + "";
						replace = replace.toLowerCase();
						
						if (replace.equals("-r")) {
							replace = "y";
							break;
						}
					}
				}
				
				ftr = new RMIClient_FileTransferRemote(ip, port, shell);
				ftr.connect();
				
				String srcPath = vParam.elementAt(0) + "";
				String destPath = vParam.elementAt(1) + "";
				Vector vFileInfo = ftr.getFileInfo(vParam.elementAt(0)+"");
				String fileName = vFileInfo.elementAt(0) + "";
				String separator = ftr.getLocalFileSeparator();
				
				if (ftr.testPathExist(srcPath) == true) {
					if (new File(destPath).exists()) {
						if (ftr.checkLocalDuplicateTarget(fileName, destPath) == false) {
							if (mode.equals("ascii")) {
								ftr.downloadFile_Ascii(srcPath, destPath + separator + fileName);
							} else if (mode.equals("binary")) {
								ftr.downloadFile_Bin(srcPath, destPath + separator + fileName);
							} else {
								errorParameter("Please specify a valid transfer mode [Ascii|Binary] \n" + 
												"e.g. -o download \"Source PathName\" \"Destination Path\" -a|-b");
							}
							
						} else {
							if (replace.equals("y")) {
								if (mode.equals("ascii")) {
									ftr.downloadFile_Ascii(srcPath, destPath + separator + fileName);
								} else if (mode.equals("binary")) {
									ftr.downloadFile_Bin(srcPath, destPath + separator + fileName);
								} else {
									errorParameter("Please specify a valid transfer mode [Ascii|Binary] \n" + 
													"e.g. -o download \"Source PathName\" \"Destination Path\" -a|-b");
								}
								
							} else {
								System.out.print("Duplicate object exist, overwrite? [y|n]: ");
								replace = new BufferedReader(new InputStreamReader(System.in)).readLine();
								replace = replace.toLowerCase();
								
								if (replace.indexOf("y")==0) {
									if (mode.equals("ascii")) {
										ftr.downloadFile_Ascii(srcPath, destPath + separator + fileName);
									} else if (mode.equals("binary")) {
										ftr.downloadFile_Bin(srcPath, destPath + separator + fileName);
									} else {
										errorParameter("Please specify a valid transfer mode [Ascii|Binary] \n" + 
														"e.g. -o download \"Source PathName\" \"Destination Path\" -a|-b");
									}
									
								} else {
									System.out.println("Upload aborted");
								}
							}
						}
						
					} else {
						System.out.println("Local path " + destPath + " does not exist. Download failed.");
						System.exit(0);
					}
					
				} else {
					System.out.println("Remote file " + srcPath + " does not exist. Download failed.");
					System.exit(0);
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