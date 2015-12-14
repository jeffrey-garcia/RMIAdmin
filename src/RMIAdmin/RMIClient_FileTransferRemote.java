package RMIAdmin;


import java.rmi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;


public class RMIClient_FileTransferRemote {
	private String ip;
	private String port;
	private String shell;
	
	private RMI rmiTarget;
	
	private RMIClientGUI_Function rmiGUI;
	
	private String serverPassphrase = RMIClient_Authenticator.getServerPassphrase();
	
	
	public static void main (String [] args) {
		//RMIClient_FileTransfer rmi_FT = new RMIClient_FileTransfer("130.18.181.77","1099","cmd.exe");
		//rmi_FT.uploadFile_Bin("c:/ipscan.exe", "g:/ipscan.exe");
		
		RMIClient_FileTransferRemote rmi_FT = new RMIClient_FileTransferRemote("127.0.0.1","1099","cmd.exe");
		rmi_FT.downloadFile_Ascii("c:/testConsumeMemory.java", "c:/Qoo/testConsumeMemory.java");
		
		//RMIClient_FileTransferRemote rmi_FT2 = new RMIClient_FileTransferRemote("127.0.0.1","1099","cmd.exe");
		//rmi_FT.downloadFile_Bin("c:/tivoli.zip", "c:/Qoo/tivoli.zip");
	}
	
	
	public RMIClient_FileTransferRemote(String ip, String port, String shell) {
		this.ip = ip;
		this.port = port;
		this.shell = shell;
	}
	
	
	public RMIClient_FileTransferRemote(String ip, String port, String shell, RMIClientGUI_Function rmiGUI) {
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
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return fList;
	}
	
	
	public String [][] getLocalFileList(String location) {
		File fLocation = null;
		File [] fTmp = null;
		String [][] fList = null;
		
		try {
			fLocation = new File (location);
			fTmp = fLocation.listFiles();
			
			//====== Make sure Drive is ready ======
			if (fTmp != null) {
				//System.out.println(fTmp.length); //Debugger
				fList = new String [fTmp.length][2];
				
				//System.out.println("Files & Directories under selected path: " + location);
				//System.out.println("-----------------------------------------------------------");
				for (int i=0; i<fTmp.length; i++) {
					//System.out.println(i + ">" + fTmp [i]); //Debugger
					fList [i][0] = fTmp [i].getName();
					
					if (fTmp [i].isDirectory()==true) {
						fList [i][1] = "D";
					} else {
						fList [i][1] = "F";
					}
				}
				//System.out.println("");
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
	
	
	public Vector getLocalFileInfo(String location) {
		File file = null;
		Vector vFileInfo = null;
		
		try {
			file = new File (location);
			
			System.out.println("Get detail information for file: " + location);
			System.out.println("-----------------------------------------------------------");
			System.out.println(file.getName());
			System.out.println(file.getCanonicalPath());
			System.out.println(file.getPath());
			System.out.println(file.getParent());
			System.out.println(file.isDirectory());
			System.out.println(file.isFile());
			System.out.println(file.isHidden());
			System.out.println(file.canRead());
			System.out.println(file.canWrite());
			System.out.println(file.lastModified());
			System.out.println(file.length());
			System.out.println("");
			
			vFileInfo = new Vector();
			vFileInfo.addElement(file.getName() + "");
			vFileInfo.addElement(file.getCanonicalPath() + "");
			vFileInfo.addElement(file.getPath() + "");
			vFileInfo.addElement(file.getParent() + "");
			vFileInfo.addElement(file.isDirectory() + "");
			vFileInfo.addElement(file.isFile() + "");
			vFileInfo.addElement(file.isHidden() + "");
			vFileInfo.addElement(file.canRead() + "");
			vFileInfo.addElement(file.canWrite() + "");
			vFileInfo.addElement(file.lastModified() + "");
			vFileInfo.addElement(file.length() + "");
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return vFileInfo;
	}
	
	
	public String createFile(String location, String type) {
		String complete = "false";
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			complete = rmiTarget.createFile(serverPassphrase, location, type);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return complete;
	}
	
	
	public String createLocalFile(String location, String type) {
		File file = null;
		String complete = "false";
		
		try {
			file = new File(location);
			
			if (type.equals("Directory")) {
				complete = file.mkdir() + "";
			} else {
				complete = file.createNewFile() + "";
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
			
			complete = "";
			StackTraceElement [] ste = exc.getStackTrace();
			for (int i=0; i<ste.length; i++) {
				complete += ste [i] + "\n";
			}
		}
		
		return complete;
	}
	
	
	public String deleteFile(String location) {
		String complete = "false";
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			complete = rmiTarget.deleteFile(serverPassphrase, location);
			
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
	
	
	public boolean checkLocalDuplicateTarget(String srcFile, String destLocation) {
		boolean duplicate = false;
		//System.out.println("****" + srcFile); //Debugger
		
		try {
			String [][] fList =  getLocalFileList(destLocation);
			
			for (int i=0; i<fList.length; i++) {
				String destFile = fList [i][0];
				//System.out.println("****" + destFile); //Debugger
				
				if (destFile.equals(srcFile)) {
					//System.out.println(srcFile + " found in " + destLocation); //Debugger
					duplicate = true;
					break;
				}
			}
			
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
	
	
	public int countLocalObject(String src) {
		int totalObject = 0;
		
		try {
			File srcFile = new File(src);
			
			if (srcFile.isDirectory() == true) {
				totalObject = countObjectInDir(srcFile, totalObject);
				
			} else {
				/*******************************
				 * If source object is a file,
				 * return totalObject as size 
				 * of file
				 ******************************/
				totalObject = new Long(srcFile.length()).intValue();
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return totalObject;
	}
	
	
	private int countObjectInDir(File srcDir, int totalObject) throws Exception {
		File[] list = srcDir.listFiles();
		
		for(int i=0; i<list.length; i++) {
			if(list [i].isFile()) {
				totalObject += 1;
				//System.out.println(list [i] + "***" + totalObject); //Debugger
				
			} else {
				totalObject = countObjectInDir(list [i], totalObject);
			}
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
	
	
	public String getLocalFileSeparator() {
		String separator = File.separator;
		return separator;
	}
	
	
	public boolean testPathExist(String location) {
		boolean exist = false;
		
		try {
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			exist = rmiTarget.testPathExist(serverPassphrase, location);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return exist;
	}
	
	
	public boolean testLocalPathExist(String location) {
		boolean exist = false;
		File f = new File(location);
		
		if (f.getParent() == null) {
			//Maybe a drive
			if (f.exists()) {
				//System.out.println("File already exist!"); //Debugger
				exist = true;
			} else {
				//System.out.println("File not exist!"); //Debugger
				exist = false;
			}
			
		} else {
			f = new File(f.getParent());
			
			if (f.exists()) {
				//System.out.println("File already exist!"); //Debugger
				exist = true;
			} else {
				//System.out.println("File not exist!"); //Debugger
				exist = false;
			}
		}
		
		return exist;
	}
	
	
	public int downloadFile_Bin(String srcLocation, String destLocation) {
		int vID = -1;
		
		try {
			File srcFile = new File(srcLocation);
			
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			/*************************************
			 * This part get the information of
			 * the remote file.
			 ************************************/
			Vector vFileInfo = getFileInfo(srcLocation);
			boolean isDirectory = new Boolean(vFileInfo.elementAt(4)+"").booleanValue();
			
			if (isDirectory == true) {
				createLocalFile(destLocation, "Directory");
				String [][] list = rmiTarget.getFileList(serverPassphrase, srcLocation);
				
				/******************************************
				 * Recursive algorithm to help expanding
				 * every level of folders and traverse the
				 * files within.
				 *****************************************/
				for(int i=0; i<list.length; i++) {
					if(list [i][1].equals("F")) {
						//System.out.println("Downloading File: " + list [i][0]); //Debugger
						int tempID = downloadFile_Bin(srcLocation + getLocalFileSeparator() + list [i][0], destLocation + getLocalFileSeparator() + list [i][0]);
						if (tempID == -999) break;
						
					} else {
						//System.out.println("Downloading Folder: " + list [i][0]); //Debugger
						downloadFile_Bin(srcLocation + getLocalFileSeparator() + list [i][0], destLocation + getLocalFileSeparator() + list [i][0]);
					}
				}
				
			} else {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destLocation)));
				
				int intRead;
				long fileSize = new Long(vFileInfo.elementAt(10) + "").longValue();
				long remainSize = (long)0;
				int bufferSize = 4096;
				byte [] buffer = new byte [bufferSize];
				Object [] obj;
				
				int session = 0;
				vID = -1;
				
				int counter = 0;
				while (true) {
					remainSize = fileSize-(bufferSize*counter);
					//System.out.println(remainSize); //Debugger
					
					if (remainSize >= bufferSize) { //Perform the checking
						//nothing to do.
						
					} else {
						buffer = new byte [(int)remainSize];
						
						break; //End of file, quit the loop immediately
					}
					
					if (vID == -999) {
						//Some error must exist, quit the loop immediately
						break;
						
					} else {
						obj = rmiTarget.downloadFile_Bin(serverPassphrase, srcLocation, buffer, session, vID);
						vID = ((Integer) obj [0]).intValue();
						buffer = (byte []) obj [1];
						bos.write(buffer,0,buffer.length);
						
						counter ++;
					}
				}
				
				if (vID != -999) { //Do this part only when there is no error
					session = -1;
					obj = rmiTarget.downloadFile_Bin(serverPassphrase, srcLocation, buffer, session, vID);
					vID = ((Integer) obj [0]).intValue();
					buffer = (byte []) obj [1];
					bos.write(buffer,0,buffer.length);
				}
				
				bos.flush();
				bos.close();
			}
			
		} catch (Exception exc) {
			//exc.printStackTrace();
			grepDebug(exc);
			vID = -999;
			
		} finally {
			if (vID == -999) {
				String errorText = "";
				errorText += "Error downloading file from " + srcLocation + 
										" to " + destLocation + ",\n";
				errorText += "possible reasions:\n";
				errorText += "=======================================================\n";
				errorText += "1. Remote Drive not ready (e.g. Floppy/CD-Rom)\n";
				errorText += "2. Drive is accessible (e.g. Root/Administrator)\n";
				errorText += "3. Source does not exist\n";
				errorText += "4. Target path does not exist\n";
				errorText += "5. Insufficient disk space\n";
				
				if (rmiGUI == null) {
					System.out.println(errorText);
					System.exit(0);
					
				} else {
					rmiGUI.warning(errorText);
					rmiGUI.getDebugger().setDebugText(errorText);
				}
				
			} else if (vID >= 0) {
				System.out.println("Downloading from " + srcLocation + 
									" to " + destLocation + " completed");
			}
			
			return vID;
		}
	}
	
	
	public int downloadFile_Ascii(String srcLocation, String destLocation) {
		int vID = -1;
		
		try {
			File srcFile = new File(srcLocation);
			
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			/*************************************
			 * This part get the information of
			 * the remote file.
			 ************************************/
			Vector vFileInfo = getFileInfo(srcLocation);
			boolean isDirectory = new Boolean(vFileInfo.elementAt(4)+"").booleanValue();
			
			if (isDirectory == true) {
				createLocalFile(destLocation, "Directory");
				String [][] list = rmiTarget.getFileList(serverPassphrase, srcLocation);
				
				/******************************************
				 * Recursive algorithm to help expanding
				 * every level of folders and traverse the
				 * files within.
				 *****************************************/
				for(int i=0; i<list.length; i++) {
					if(list [i][1].equals("F")) {
						//System.out.println("Downloading File: " + list [i][0]); //Debugger
						int tempID = downloadFile_Ascii(srcLocation + getFileSeparator() + list [i][0], destLocation + getLocalFileSeparator() + list [i][0]);
						if (tempID == -999) break;
						
					} else {
						//System.out.println("Downloading Folder: " + list [i][0]); //Debugger
						downloadFile_Ascii(srcLocation + getFileSeparator() + list [i][0], destLocation + getLocalFileSeparator() + list [i][0]);
					}
				}
				System.out.println("Loop quit");
				
			} else {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destLocation)));
				
				int intRead;
				long fileSize = new Long(vFileInfo.elementAt(10) + "").longValue();
				long remainSize = (long)0;
				int bufferSize = 4096;
				byte [] buffer = new byte [bufferSize];
				Object [] obj;
				
				int session = 0;
				vID = -1;
				
				int counter = 0;
				while (true) {
					remainSize = fileSize-(bufferSize*counter);
					//System.out.println(remainSize); //Debugger
					
					if (remainSize >= bufferSize) { //Perform the checking
						//nothing to do.
						
					} else {
						buffer = new byte [(int)remainSize];
						
						break; //End of file, quit the loop immediately
					}
					
					if (vID == -999) {
						//Some error must exist, quit the loop immediately
						break;
						
					} else {
						obj = rmiTarget.downloadFile_Ascii(serverPassphrase, srcLocation, buffer, session, vID);
						vID = ((Integer) obj [0]).intValue();
						buffer = (byte []) obj [1];
						bos.write(buffer,0,buffer.length);
						
						counter ++;
					}
				}
				
				if (vID != -999) { //Do this part only when there is no error
					session = -1;
					obj = rmiTarget.downloadFile_Ascii(serverPassphrase, srcLocation, buffer, session, vID);
					vID = ((Integer) obj [0]).intValue();
					buffer = (byte []) obj [1];
					bos.write(buffer,0,buffer.length);
				}
				
				bos.flush();
				bos.close();
			}
			
		} catch (Exception exc) {
			//exc.printStackTrace();
			grepDebug(exc);
			vID = -999;
			
		} finally {
			if (vID == -999) {
				String errorText = "";
				errorText += "Error downloading file from " + srcLocation + 
										" to " + destLocation + ",\n";
				errorText += "possible reasions:\n";
				errorText += "=======================================================\n";
				errorText += "1. Remote Drive not ready (e.g. Floppy/CD-Rom)\n";
				errorText += "2. Drive is accessible (e.g. Root/Administrator)\n";
				errorText += "3. Source does not exist\n";
				errorText += "4. Target path does not exist\n";
				errorText += "5. Insufficient disk space\n";
				
				if (rmiGUI == null) {
					System.out.println(errorText);
					System.exit(0);
					
				} else {
					rmiGUI.warning(errorText);
					rmiGUI.getDebugger().setDebugText(errorText);
				}
				
			} else if (vID >= 0) {
				System.out.println("Downloading from " + srcLocation + 
									" to " + destLocation + " completed");
			}
			
			return vID;
		}
	}
	
	
	public boolean removeTempFile(String location) {
		boolean completed = false;
		
		try {
			File srcFile = new File(location);
			completed = srcFile.delete();
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return completed;
	}
	
	
	public boolean validateTempSpace() {
		boolean exist = false;
		
		try {
			String location = System.getProperty("user.home") + File.separator + "RMIAdmin";
			File srcFile = new File(location);
			exist = srcFile.exists();
			
			if (exist == false) {
				createLocalFile(location, "Directory");
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return exist;
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