package RMIAdmin;


import java.rmi.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.util.*;


public class RMIClient_FileTransferLocal {
	private String ip;
	private String port;
	private String shell;
	
	private RMI rmiTarget;
	
	private RMIClientGUI_Function rmiGUI;
	
	private String serverPassphrase = RMIClient_Authenticator.getServerPassphrase();
	
	
	public static void main (String [] args) {
		//RMIClient_FileTransfer rmi_FT = new RMIClient_FileTransfer("130.18.181.77","1099","cmd.exe");
		//rmi_FT.uploadFile_Bin("c:/ipscan.exe", "g:/ipscan.exe");
		
		RMIClient_FileTransferLocal rmi_FT = new RMIClient_FileTransferLocal("127.0.0.1","1099","cmd.exe");
		rmi_FT.uploadFile_Ascii("c:/test.java", "c:/Qoo/test.java");
	}
	
	
	public RMIClient_FileTransferLocal(String ip, String port, String shell) {
		this.ip = ip;
		this.port = port;
		this.shell = shell;
	}
	
	
	public RMIClient_FileTransferLocal(String ip, String port, String shell, RMIClientGUI_Function rmiGUI) {
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
		String [] driveInString = null;
		
		try {
			File [] drive = File.listRoots();
			driveInString = new String [drive.length];
			
			System.out.println("Available Drives in Local File System");
			System.out.println("--------------------------------------------------");
			for (int i=0; i<drive.length; i++) {
				driveInString [i] = drive [i] + "";
				System.out.println(drive [i]);
			}
			System.out.println("");
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return driveInString;
	}
	
	
	public String [] openRemoteFileSystem() {
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
				
				System.out.println("Files & Directories under selected path: " + location);
				System.out.println("-----------------------------------------------------------");
				for (int i=0; i<fTmp.length; i++) {
					System.out.println(i + ">" + fTmp [i]); //Debugger
					fList [i][0] = fTmp [i].getName();
					
					if (fTmp [i].isDirectory()==true) {
						fList [i][1] = "D";
					} else {
						fList [i][1] = "F";
					}
				}
				System.out.println("");
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
		}
		
		return fList;
	}
	
	
	public Vector getFileInfo(String location) {
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
	
	
	public Vector getRemoteFileInfo(String location) {
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
	
	
	public String createFile(String location, String type) {
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
		File file = null;
		String complete = "false";
		
		try {
			file = new File(location);
			
			/********************************
			 * JAVA cannot delete non-empty  
			 * folders, therefore we must 
			 * write a recursive algorithm.
			 *******************************/
			if (file.isDirectory()) {
				complete = deleteDir(file) + "";
			} else {
				complete = file.delete() + "";
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
	
	
	private String deleteDir(File dir) {
		String complete = "";
		File[] list = dir.listFiles();
		
		for(int i=0; i<list.length; i++) {
			if(list [i].isFile()) {
				complete = list [i].delete() + "";
			} else {
				if (list [i].listFiles().length > 0) {
					deleteDir(list [i]);
				} else {
					complete = list [i].delete() + "";
				}
			}
		}
		
		complete = dir.delete() + "";
		return complete;
	}
	
	
	public String renameFile(String srcLocation, String destLocation) {
		File srcFile = null;
		File destFile = null;
		String complete = "false";
		
		try {
			srcFile = new File(srcLocation);
			destFile = new File(destLocation);
			
			complete = srcFile.renameTo(destFile) + "";
			
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
	
	
	public String copyAndPaste(String src, String dest) {
		String complete = "false";
		
		try {
			File srcFile = new File(src);
			File destFile = new File(dest);
			
			if (srcFile.isDirectory() == true) {
				copyDir(srcFile, destFile);
				complete = "true";
			} else {
				copyFile(srcFile, destFile);
				complete = "true";
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
	
	
	public String cutAndPaste(String src, String dest) {
		String complete = "false";
		
		try {
			//Do the same operation as "Copy and Paste"
			File srcFile = new File(src);
			File destFile = new File(dest);
			
			if (srcFile.isDirectory() == true) {
				copyDir(srcFile, destFile);
			} else {
				copyFile(srcFile, destFile);
			}
			
			//Then delete the source location
			complete = deleteFile(src);
			
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
	
	
	private void copyDir(File srcDir, File destDir) throws Exception {
		destDir = new File(destDir.getPath() + File.separator + srcDir.getName());
		destDir.mkdir();
		
		//--- Reserve the last modified date ---
		long l = srcDir.lastModified();
		destDir.setLastModified(l);
		//--------------------------------------
		System.out.println("Create Directory " + destDir.getName());
		
		File[] list = srcDir.listFiles();
		
		for(int i=0; i<list.length; i++) {
			if(list [i].isFile()) {
				copyFile(list [i], destDir);
			} else {
				copyDir(list [i], destDir);
			}
		}
	}
	
	
	private void copyFile(File srcFile, File destFile) throws Exception {
		String fileName = srcFile.getName();
		
		byte[] byteArray = new byte[4096];
		int len = 0;
		
		FileInputStream input = new FileInputStream(srcFile);
		FileOutputStream output = new FileOutputStream(destFile + File.separator + srcFile.getName());
				
		while ((len=input.read(byteArray)) != -1) {
			output.write(byteArray, 0, len);
		}
		
		input.close();
		output.close();
		
		//--- Reserve the last modified date ---
		long l = srcFile.lastModified();
		File f = new File(destFile + File.separator + srcFile.getName());
		f.setLastModified(l);
		//--------------------------------------
	}
	
	
	public boolean checkDuplicateTarget(String srcFile, String destLocation) {
		boolean duplicate = false;
		System.out.println("****" + srcFile); //Debugger
		
		try {
			String [][] fList =  getFileList(destLocation);
			
			for (int i=0; i<fList.length; i++) {
				String destFile = fList [i][0];
				System.out.println("****" + destFile); //Debugger
				
				if (destFile.equals(srcFile)) {
					System.out.println(srcFile + " found in " + destLocation); //Debugger
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
	
	
	public boolean checkRemoteDuplicateTarget(String srcFile, String destLocation) {
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
	
	
	public int countObject(String src) {
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
	
	
	public int countRemoteObject(String srcFile) {
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
	
	
	private int countObjectInDir(File srcDir, int totalObject) throws Exception {
		File[] list = srcDir.listFiles();
		
		if (list != null) {
			for(int i=0; i<list.length; i++) {
				if(list [i].isFile()) {
					totalObject += 1;
					//System.out.println(list [i] + "***" + totalObject); //Debugger
					
				} else {
					totalObject = countObjectInDir(list [i], totalObject);
				}
			}
		}
		
		return totalObject;
	}
	
	
	public long getFileSize(String srcLocation) {
		long size = 0;
		File f = new File(srcLocation);
		
		if (f.canRead()) {
			if (f.isDirectory()) {
				File [] fileList = f.listFiles();
				
				for (int i=0; i<fileList.length; i++) {
					size += getFileSize(fileList [i]  + "");
				}
				
			} else {
				size = f.length();
			}
		}
		
		return size;
	}
	
	
	public String getRoundedFileSize(String srcLocation) {
		String unit = " Byte (s)";
		String sizeInString = "0";
		float divider = Float.parseFloat("1024");
		long sizeInLong = new File(srcLocation).length();
		float sizeInFloat = Float.parseFloat(sizeInLong + "");
		
		if (sizeInFloat > 1024) {
			sizeInFloat = sizeInFloat / divider;
			
			if (sizeInFloat > 1024) {
				sizeInFloat = sizeInFloat / divider;
				
				unit = " MB";
				sizeInString = sizeInFloat + "";
				
			} else {
				unit = " KB";
				sizeInString = sizeInFloat + "";
			}
			
		} else {
			unit = " Byte (s)";
			sizeInString = sizeInFloat + "";
		}
		
		return sizeInString + unit;
	}
	
	
	public String getRoundedFileSize(long sizeInLong) {
		String unit = " Byte (s)";
		String sizeInString = "0";
		float divider = Float.parseFloat("1024");
		float sizeInFloat = Float.parseFloat(sizeInLong + "");
		
		if (sizeInFloat > 1024) {
			sizeInFloat = sizeInFloat / divider;
			
			if (sizeInFloat > 1024) {
				sizeInFloat = sizeInFloat / divider;
				
				unit = " MB";
				sizeInString = sizeInFloat + "";
				
			} else {
				unit = " KB";
				sizeInString = sizeInFloat + "";
			}
			
		} else {
			unit = " Byte (s)";
			sizeInString = sizeInFloat + "";
		}
		
		return sizeInString + unit;
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
	
	
	public int uploadFile_Bin(String srcLocation, String destLocation) {
		int vID = -1;
		
		try {
			File srcFile = new File(srcLocation);
			
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			if (srcFile.isDirectory() == true) {
				rmiTarget.createFile(serverPassphrase, destLocation, "Directory");
				File[] list = srcFile.listFiles();
				
				/******************************************
				 * Recursive algorithm to help expanding
				 * every level of folders and traverse the
				 * files within.
				 *****************************************/
				for(int i=0; i<list.length; i++) {
					if(list [i].isFile()) {
						//System.out.println("Uploading File: " + list [i]); //Debugger
						int tempID = uploadFile_Bin(list [i].getPath(), destLocation + rmiTarget.getFileSeparator(serverPassphrase) + list [i].getName());
						if (tempID == -999) break;
						
					} else {
						//System.out.println("Uploading Folder: " + list [i]); //Debugger
						uploadFile_Bin(list [i].getPath(), destLocation + rmiTarget.getFileSeparator(serverPassphrase) + list [i].getName());
					}
				}
				
			} else {
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
				
				int bytesRead = 0;
				long fileSize = srcFile.length();
				int bufferSize = 4096;
				byte [] buffer = new byte [bufferSize];
				
				/***************************************
				 *
				 * Session is the value which 
				 * control the upload session,
				 * it let the server to distinguish
				 * whether the file upload is 
				 * completed.
				 *
				 * session = 0 ... upload begin
				 * session > 0 ... upload in progress
				 * session = -1 .. upload completed
				 *
				 **************************************/
				int session = 0;
				
				/*******************************************************
				 *
				 * vID is the position of the Remote File Transfer 
				 * Handler - RMIServer_FileTransfer, in the Vector
				 * of RMIServer.
				 *
				 * Such a design allow us to interact with single 
				 * RMIServer, but at the same time able to invoke 
				 * and references many instances of Remote File 
				 * Transfer Handler, see below for details: 
				 * 
				 *   RMIClientGUI_FileTransfer
				 *               |
				 *               |
				 *               V
				 *           RMIServer
				 *               |
				 *               |---------> RMIServer_FileTransfer
				 *               |
				 *               |---------> RMIServer_FileTransfer
				 *               |
				 *               |---------> RMIServer_FileTransfer
				 *
				 * vID = -1 ... new Upload Session
				 * vID >= 0 ... continue previous Upload Session
				 * vID = -999 ... Upload error occured at Server Side 
				 *
				 ******************************************************/
				vID = -1;
				
				/*******************************************************
				 *
				 * Algorithm Explanation for File Upload (Binary)
				 * 
				 * We must declare a byte [] array such that the 
				 * input stream could buffer certain amount of data.
				 * If we read the input stream byte-by-byte, the 
				 * efficiency will decrease and the running time 
				 * will be very slow.
				 *
				 * The default size of buffer is set to 4096. 
				 * If this value is set too large, then large 
				 * amount of data will be buffered to memory at
				 * a time, and this may lead to Out Of Memory.
				 * 
				 * However the buffering technique also introduce 
				 * another problem - within the buffer, we don't 
				 * know at which point indicate the end of file.
				 *
				 * Take example of a file with size 5000 bytes, so 
				 * the first reading of input stream will only buffer 
				 * 4096 bytes of data, and upon the second round, 
				 * 4 bytes of remaining data will be buffered. But 
				 * each reading action in fact will use the same 
				 * byte [] array to store any data regardless of 
				 * the data size (as long as the buffer could hold).
				 * 
				 * So if we just output the buffer straight forward, 
				 * the new size of the target now becomes 4096*2.
				 *
				 * As a result, we've to implement a checking 
				 * mechanism which is able to determine the end of 
				 * file anywhere within the buffer.
				 * 
				 ******************************************************/
				int counter = 0;
				while ((bytesRead=bis.read(buffer)) != -1) {
					long remainSize = fileSize-(bufferSize*counter);
					//System.out.println(remainSize); //Debugger
					
					if (remainSize >= bufferSize) { //Perform the checking
						//nothing to do.
						
					} else {
						byte [] newBuffer = new byte [(int)remainSize];
						for (int i=0; i<remainSize; i++) {
							newBuffer [i] = buffer [i];
						}
						buffer = newBuffer;
						
						break; //End of file, quit the loop immediately
					}
					
					if (vID == -999) {
						//Some error must exist, quit the loop immediately
						break;
						
					} else {
						vID = rmiTarget.uploadFile_Bin(serverPassphrase, destLocation, buffer, session, vID);
						counter ++;
					}
				}
				
				if (vID != -999) { //Do this part only when there is no error
					session = -1;
					
					//Determine if filesize is empty
					if (fileSize == 0) {
						buffer = new byte [0];
						vID = rmiTarget.uploadFile_Bin(serverPassphrase, destLocation, buffer, session, vID);
					} else {
						vID = rmiTarget.uploadFile_Bin(serverPassphrase, destLocation, buffer, session, vID);
					}
				}
				
				bis.close();
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
			
		} finally {
			if (vID==-999) {
				String errorText = "";
				errorText += "Error uploading file from " + srcLocation + 
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
				
			} else {
				System.out.println("Uploading from " + srcLocation + 
									" to " + destLocation + " completed");
			}
			
			return vID;
		}
	}
	
	
	public int uploadFile_Ascii(String srcLocation, String destLocation) {
		int vID = -1;
		
		try {
			File srcFile = new File(srcLocation);
			
			String app = "rmi://" + ip + ":" + port + "/" + RMIClient_Authenticator.getToken();
			rmiTarget = (RMI) Naming.lookup(app);
			
			if (srcFile.isDirectory() == true) {
				rmiTarget.createFile(serverPassphrase, destLocation, "Directory");
				File[] list = srcFile.listFiles();
				
				/******************************************
				 * Recursive algorithm to help expanding
				 * every level of folders and traverse the
				 * files within.
				 *****************************************/
				for(int i=0; i<list.length; i++) {
					if(list [i].isFile()) {
						//System.out.println("Uploading File: " + list [i]); //Debugger
						int tempID = uploadFile_Ascii(list [i].getPath(), destLocation + rmiTarget.getFileSeparator(serverPassphrase) + list [i].getName());
						if (tempID == -999) break;
						
					} else {
						//System.out.println("Uploading Folder: " + list [i]); //Debugger
						uploadFile_Ascii(list [i].getPath(), destLocation + rmiTarget.getFileSeparator(serverPassphrase) + list [i].getName());
					}
				}
				
			} else {
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
				
				int intRead;
				long fileSize = srcFile.length();
				int bufferSize = 4096;
				byte [] buffer = new byte [bufferSize];
				
				int session = 0;
				vID = -1;
				
				int counter = 0;
				while ((intRead=bis.read(buffer)) != -1) {
					long remainSize = fileSize-(bufferSize*counter);
					//System.out.println(remainSize); //Debugger
					
					if (remainSize >= bufferSize) { //Perform the checking
						//nothing to do.
						
					} else {
						byte [] newBuffer = new byte [(int)remainSize];
						for (int i=0; i<remainSize; i++) {
							newBuffer [i] = buffer [i];
						}
						buffer = newBuffer;
						
						break; //End of file, quit the loop immediately
					}
					
					if (vID == -999) {
						//Some error must exist, quit the loop immediately
						break;
						
					} else {
						vID = rmiTarget.uploadFile_Ascii(serverPassphrase, destLocation, buffer, session, vID);
						counter ++;
					}
				}
				
				if (vID != -999) { //Do this part only when there is no error
					session = -1;
					
					//Determine if filesize is empty
					if (fileSize == 0) {
						buffer = new byte [0];
						vID = rmiTarget.uploadFile_Ascii(serverPassphrase, destLocation, buffer, session, vID);
					} else {
						vID = rmiTarget.uploadFile_Ascii(serverPassphrase, destLocation, buffer, session, vID);
					}
				}
				
				bis.close();
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			grepDebug(exc);
			
		} finally {
			if (vID == -999) {
				String errorText = "";
				errorText += "Error uploading file from " + srcLocation + 
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
				System.out.println("Uploading from " + srcLocation + 
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
				createFile(location, "Directory");
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