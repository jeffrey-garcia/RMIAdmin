package RMIAdmin;


import java.rmi.*;
import java.rmi.server.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.imageio.*;

import java.io.*;
import java.util.*;


public class RMIServer extends UnicastRemoteObject implements RMI {
	private String serverKey = null;
	private String serverPassPhrase = "";
	private boolean auth_ON = false;
	
	private String port = "";
	private String rmiBindName = "";
	private String app = "";
	private RMIServer ptr = null;
	private Vector vRMIS_FT = new Vector();
	
	private int rcSession = 1;
	private Robot robot = null;
	
	
	public RMIServer() throws RemoteException {
		super();
	}
	
	
	private void trapError(Exception exc) {
		//Print Exception Message to Logfile
		(new RMIServer_EventHandler(exc.getMessage())).getFile();
		
		//Print Stack Trace to Logfile
		(new RMIServer_EventHandler(exc.getStackTrace())).getFile();
	}
	
	
	public boolean isConnected(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		return true;
	}
	
	
	public String getOS(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		String os = System.getProperty("os.name") + "," + System.getProperty("os.version");
		return os;
	}
	
	
	public boolean restartRMI(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		boolean doneRestart = false;
		
		try {
			System.out.println("\n\n\n");
			System.out.println("RMI Server will now be restarted.");
			System.out.println("**********************************************");
			
			System.out.print("Unbinding application from RMI Registry...");
			UnicastRemoteObject.unexportObject(ptr, true);
			Naming.unbind(app);
			System.out.println(" Done.");
			
			Thread []  threads = new Thread [Thread.activeCount() * 2];
			int enumerated = Thread.enumerate(threads);
			
			for(int i=0; i<enumerated; i++) {
				//System.out.println("Active Thread ID " + i + " = " + threads[i].getName()); //Debugger
				String threadClass = threads[i].getClass().getName() + "";
				
				if (threadClass.indexOf("RMIAdmin.RMIServer_CommandLine$") >= 0) {
					System.out.print("Destroying obsoleted thread: " + threadClass + "... "); //Debugger
					threads[i].destroy();
					
					System.out.println("Done."); //Debugger
				}
			}
			
			System.out.print("Setting Port No. to " + port + "...   ");
			//registry = java.rmi.registry.LocateRegistry.createRegistry(Integer.parseInt(port));
			System.out.println("Done.");
			
			System.out.print("Setting RMI Application Name [" + rmiBindName + "]...   ");
			String app = "rmi://127.0.0.1:" + port + "/" + rmiBindName;
			System.out.println("Done .");
			
			System.out.print("Creating new instance for RAdminServer...   ");
			RMIServer server = new RMIServer();
			System.out.println("Done.");
			
			System.out.print("Rebinding application to RMI Registry...   ");
			Naming.rebind(app, server);
			System.out.println("Done.");
			
			System.out.print("Testing Runtime for Command Line...");
			if (server.checkCLready(key) == true) {
				System.out.println("Done.");
			} else {
				System.out.println("Fail.");
			}
			
			System.out.print("Validating File System...   ");
			if (server.checkFSready(key) == true) {
				System.out.println("Done.");
			} else {
				System.out.println("Fail.");
			}
			
			System.out.print("Constructing a Robot Object for Native System's Screen...");
			server.robot = new Robot();
			System.out.println("Done.");
			
			System.out.print("Invoke Housekeeping... ");
			server.ptr = server;
			server.port = port;
			server.app = app;
			server.rmiBindName = rmiBindName;
			
			RMIServer temp = this;
			temp = null;
			
			System.runFinalization();
			System.gc();
			System.out.println("Done.");
			//=============================================
			
			doneRestart = true;
			
		} catch (Exception exc) {
			trapError(exc);
			doneRestart = false;
			
		} finally {
			return doneRestart;
		}
	}
	
	
	public void shutdownRMI(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		exitVM();
	}
	
	
	private void exitVM() {
		System.exit(0);
	}
	
	
	public boolean checkCLready(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		Runtime R;
		boolean ready = false;
		
		try {
			R = Runtime.getRuntime();
			ready = true;
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return ready;
	}
	
	
	public String setCommand(String key, String shell, String command) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_CommandLine rmiS_CL = new RMIServer_CommandLine();
		String threadName = rmiS_CL.setCommand(shell, command);
		
		return threadName;
	}
	
	
	public void runCommand(String key, String threadName) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_CommandLine rmiS_CL;
		
		try {
			rmiS_CL = new RMIServer_CommandLine();
			rmiS_CL.runCommand(threadName);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public void cancelCommand(String key, String threadName) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_CommandLine rmiS_CL;
		
		try {
			rmiS_CL = new RMIServer_CommandLine();
			rmiS_CL.cancelCommand(threadName);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public boolean checkCommandStatus(String key, String threadName) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_CommandLine rmiS_CL;
		boolean completed = false;
		
		try {
			rmiS_CL = new RMIServer_CommandLine();
			completed = rmiS_CL.checkCommandStatus(threadName);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return completed;
	}
	
	
	public LinkedList getCommandResult(String key, String threadName) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_CommandLine rmiS_CL;
		LinkedList llOutput = new LinkedList();
		
		try {
			rmiS_CL = new RMIServer_CommandLine();
			llOutput = rmiS_CL.getCommandResult(threadName);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return llOutput;
	}
	
	
	public boolean checkFSready(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		boolean ready = false;
		
		try {
			File.listRoots();
			ready = true;
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return ready;
	}
	
	
	public String [] openFileSystem(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		File [] drive = null;
		String [] drive2 = null;
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			drive = rmiS_FB.getDrives();
			drive2 = new String [drive.length];
			
			/******************************************
			 * Convert the File array to String array,
			 * because this may produce error across 
			 * different OS.
			 *****************************************/
			for (int i=0; i<drive.length; i++) {
				drive2 [i] = drive [i] + "";
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return drive2;
	}
	
	
	public String [][] getFileList(String key, String location) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		String [][] fList = null;
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			fList = rmiS_FB.getFiles(location);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return fList;
	}
	
	
	public Vector getFileInfo(String key, String location) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		Vector vFileInfo = null;
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			vFileInfo = rmiS_FB.getFileInfo(location);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return vFileInfo;
	}
	
	
	public String createFile(String key, String location, String type) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		String complete = "false";
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			complete = rmiS_FB.createFile(location, type);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return complete;
	}
	
	
	public String deleteFile(String key, String location) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		String complete = "false";
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			complete = rmiS_FB.deleteFile(location);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return complete;
	}
	
	
	public String renameFile(String key, String srcLocation, String destLocation) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		String complete = "false";
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			complete = rmiS_FB.renameFile(srcLocation, destLocation);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return complete;
	}
	
	
	public String copyAndPaste(String key, String srcLocation, String destLocation) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		String complete = "false";
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			complete = rmiS_FB.copyAndPaste(srcLocation, destLocation);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return complete;
	}
	
	
	public String cutAndPaste(String key, String srcLocation, String destLocation) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		String complete = "false";
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			complete = rmiS_FB.cutAndPaste(srcLocation, destLocation);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return complete;
	}
	
	
	public boolean checkDuplicateTarget(String key, String srcFile, String destLocation) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		boolean duplicate = false;
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			duplicate = rmiS_FB.checkDuplicateTarget(srcFile, destLocation);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return duplicate;
	}
	
	
	public int countObject(String key, String srcLocation) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		int totalObject = 0;
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			totalObject = rmiS_FB.countObject(srcLocation);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return totalObject;
	}
	
	
	public long getFileSize(String key, String srcLocation) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		long size = 0;
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			size = rmiS_FB.getFileSize(srcLocation);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return size;
	}
	
	
	public String getRoundedFileSize(String key, String srcLocation) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		String size = "0 Byte(s)";
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			size = rmiS_FB.getRoundedFileSize(srcLocation);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return size;
	}
	
	
	public String getRoundedFileSize(String key, long sizeInLong) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileBrowser rmiS_FB;
		String size = "0 Byte(s)";
		
		try {
			rmiS_FB = new RMIServer_FileBrowser();
			size = rmiS_FB.getRoundedFileSize(sizeInLong);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return size;
	}
	
	
	public String getFileSeparator(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		return File.separator;
	}
	
	
	public boolean testPathExist(String key, String location) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileTransfer rmiS_FT;
		boolean exist = false;
		
		try {
			rmiS_FT = new RMIServer_FileTransfer();
			exist = rmiS_FT.testPathExist(location);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return exist;
	}
	
	
	public int uploadFile_Bin(String key, String destLocation, byte [] buffer, int session, int vID) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileTransfer rmiS_FT;
		
		try {
			if (vID == -1) {
				//new upload session
				rmiS_FT = new RMIServer_FileTransfer();
				String error = rmiS_FT.openUploadStream(destLocation, "bin");
				
				if (error.length() > 0) {
					//Error occurred
					vID = -999;
				} else {
					//add the new File Transfer handler to Vector
					vRMIS_FT.add(rmiS_FT);
					vID = vRMIS_FT.size() - 1;
				}
				
			} else {
				//continue upload session
				if (vID != -999) {
					rmiS_FT = (RMIServer_FileTransfer)vRMIS_FT.elementAt(vID);
				} else {
					rmiS_FT = null;
				}
			}
			
			if (vID != -999) {
				if (session >= 0) {
					//upload in progress
					String error = rmiS_FT.uploadFile_Bin(destLocation, buffer);
					
					if (error.length() > 0) {
						vID = -999;
					}
					
				} else {
					//upload the remaining segment of file
					String error = rmiS_FT.uploadFile_Bin(destLocation, buffer);
					
					//upload has finished
					rmiS_FT.closeUploadStream(destLocation, "bin");
					
					//This part is for housekeeping, hopefully to release some memory
					rmiS_FT = null;
					vRMIS_FT.setElementAt(null, vID);
					
					if (error.length() > 0) {
						vID = -999;
					}
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return vID;
	}
	
	
	public int uploadFile_Ascii(String key, String destLocation, byte [] buffer, int session, int vID) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileTransfer rmiS_FT;
		
		try {
			if (vID == -1) {
				//new upload session
				rmiS_FT = new RMIServer_FileTransfer();
				String error = rmiS_FT.openUploadStream(destLocation, "ascii");
				
				if (error.length() > 0) {
					//Error occurred
					vID = -999;
				} else {
					//add the new File Transfer handler to Vector
					vRMIS_FT.add(rmiS_FT);
					vID = vRMIS_FT.size() - 1;
				}
				
			} else {
				//continue upload session
				if (vID != -999) {
					rmiS_FT = (RMIServer_FileTransfer)vRMIS_FT.elementAt(vID);
				} else {
					rmiS_FT = null;
				}
			}
			
			if (vID != -999) {
				if (session >= 0) {
					//upload in progress
					String error = rmiS_FT.uploadFile_Ascii(destLocation, buffer);
					
					if (error.length() > 0) {
						vID = -999;
					}
					
				} else {
					//upload the remaining segment of file
					String error = rmiS_FT.uploadFile_Ascii(destLocation, buffer);
					
					//upload has finished
					rmiS_FT.closeUploadStream(destLocation, "ascii");
					
					//This part is for housekeeping, hopefully to release memory
					rmiS_FT = null;
					vRMIS_FT.setElementAt(null, vID);
					
					if (error.length() > 0) {
						vID = -999;
					}
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return vID;
	}
	
	
	public Object [] downloadFile_Bin(String key, String srcLocation, byte [] buffer, int session, int vID) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileTransfer rmiS_FT;
		Object [] obj = new Object [2];
		
		try {
			if (vID == -1) {
				//new Download session
				rmiS_FT = new RMIServer_FileTransfer();
				String error = rmiS_FT.openDownloadStream(srcLocation, "bin");
				
				if (error.length() > 0) {
					//Error occurred
					vID = -999;
					
				} else {
					//add the new File Transfer handler to Vector
					vRMIS_FT.add(rmiS_FT);
					vID = vRMIS_FT.size() - 1;
				}
				
			} else {
				//continue download session
				if (vID != -999) {
					rmiS_FT = (RMIServer_FileTransfer)vRMIS_FT.elementAt(vID);
					
				} else {
					rmiS_FT = null;
				}
			}
			
			if (vID != -999) {
				if (session >= 0) {
					//download in progress
					buffer = rmiS_FT.downloadFile_Bin(srcLocation, buffer);
					
				} else {
					//download the remaining segment of file
					buffer = rmiS_FT.downloadFile_Bin(srcLocation, buffer);
					
					//upload has finished
					rmiS_FT.closeDownloadStream(srcLocation);
					
					//This part is for housekeeping, hopefully to release memory
					rmiS_FT = null;
					vRMIS_FT.setElementAt(null, vID);
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		obj [0] = new Integer(vID);
		obj [1] = buffer;
		
		return obj;
	}
	
	
	public Object [] downloadFile_Ascii(String key, String srcLocation, byte [] buffer, int session, int vID) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		RMIServer_FileTransfer rmiS_FT;
		Object [] obj = new Object [2];
		
		try {
			if (vID == -1) {
				//new Download session
				rmiS_FT = new RMIServer_FileTransfer();
				String error = rmiS_FT.openDownloadStream(srcLocation, "ascii");
				
				if (error.length() > 0) {
					//Error occurred
					vID = -999;
					
				} else {
					//add the new File Transfer handler to Vector
					vRMIS_FT.add(rmiS_FT);
					vID = vRMIS_FT.size() - 1;
				}
				
			} else {
				//continue download session
				if (vID != -999) {
					rmiS_FT = (RMIServer_FileTransfer)vRMIS_FT.elementAt(vID);
					
				} else {
					rmiS_FT = null;
				}
			}
			
			if (vID != -999) {
				if (session >= 0) {
					//download in progress
					buffer = rmiS_FT.downloadFile_Ascii(srcLocation, buffer);
					
				} else {
					//download the remaining segment of file
					buffer = rmiS_FT.downloadFile_Ascii(srcLocation, buffer);
					
					//upload has finished
					rmiS_FT.closeDownloadStream(srcLocation);
					
					//This part is for housekeeping, hopefully to release memory
					rmiS_FT = null;
					vRMIS_FT.setElementAt(null, vID);
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		obj [0] = new Integer(vID);
		obj [1] = buffer;
		
		return obj;
	}
	
	
	public void collectGarbage(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		System.gc();
	}
	
	
	public long [] getMemory(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		long [] memory = new long [3];
		
		try {
			Runtime R = Runtime.getRuntime();
			long freeMemory = R.freeMemory();
			long totalMemory = R.totalMemory();
			long maxMemory = R.maxMemory();
			
			memory [0] = freeMemory;
			memory [1] = totalMemory;
			memory [2] = maxMemory;
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return memory;
	}
	
	
	public String [][] getSystemInfo(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		String [][] systemInfo = null;
		
		try {
			systemInfo = new String [11][2];
			
			systemInfo [0][0] = "Java Runtime Environment version";
			systemInfo [0][1] = System.getProperty("java.version");
			
			systemInfo [1][0] = "Java Runtime Environment vendor";
			systemInfo [1][1] = System.getProperty("java.vendor");
			
			systemInfo [2][0] = "Java Virtual Machine implementation name";
			systemInfo [2][1] = System.getProperty("java.vm.name");
			
			systemInfo [3][0] = "Java Virtual Machine implementation version";
			systemInfo [3][1] = System.getProperty("java.vm.version");
			
			systemInfo [4][0] = "Java Virtual Machine implementation vendor";
			systemInfo [4][1] = System.getProperty("java.vm.vendor");
			
			systemInfo [5][0] = "Name of JIT compiler to use";
			systemInfo [5][1] = System.getProperty("java.compiler");
			
			systemInfo [6][0] = "Operating system name";
			systemInfo [6][1] = System.getProperty("os.name");
			
			systemInfo [7][0] = "Operating system version";
			systemInfo [7][1] = System.getProperty("os.version");
			
			systemInfo [8][0] = "Current user's account name";
			systemInfo [8][1] = System.getProperty("user.name");
			
			systemInfo [9][0] = "Current user's home directory";
			systemInfo [9][1] = System.getProperty("user.home");
			
			systemInfo [10][0] = "Current user's working directory";
			systemInfo [10][1] = System.getProperty("user.dir");
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return systemInfo;
	}
	
	
	public byte [] captureScreen(String key, Rectangle screenRect) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		if (robot == null) throw new RemoteException("<br>Remote System GUI not available.<br>" + 
														"Make sure the RMI Service is started under a Window session.<br>" + 
														"(e.g. X11 Window Server)");
		
		RMIServer_RemoteControl rmiS_RC;
		byte [] data = null; 
		
		try {
			rmiS_RC = new RMIServer_RemoteControl(robot);
			data = rmiS_RC.captureScreen(screenRect);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return data;
	}
	
	
	public void keyPress(String key, int keycode) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		if (robot == null) throw new RemoteException("<br>Remote System GUI not available.<br>" + 
														"Make sure the RMI Service is started under a Window session.<br>" + 
														"(e.g. X11 Window Server)");
		
		RMIServer_RemoteControl rmiS_RC;
		
		try {
			rmiS_RC = new RMIServer_RemoteControl(robot);
			rmiS_RC.keyPress(keycode);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public void keyRelease(String key, int keycode) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		if (robot == null) throw new RemoteException("<br>Remote System GUI not available.<br>" + 
														"Make sure the RMI Service is started under a Window session.<br>" + 
														"(e.g. X11 Window Server)");
		
		RMIServer_RemoteControl rmiS_RC;
		
		try {
			rmiS_RC = new RMIServer_RemoteControl(robot);
			rmiS_RC.keyRelease(keycode);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public void mouseMove(String key, int x, int y) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		if (robot == null) throw new RemoteException("<br>Remote System GUI not available.<br>" + 
														"Make sure the RMI Service is started under a Window session.<br>" + 
														"(e.g. X11 Window Server)");
		
		RMIServer_RemoteControl rmiS_RC;
		
		try {
			rmiS_RC = new RMIServer_RemoteControl(robot);
			rmiS_RC.mouseMove(x, y);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public void mousePress(String key, int buttons) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		if (robot == null) throw new RemoteException("<br>Remote System GUI not available.<br>" + 
														"Make sure the RMI Service is started under a Window session.<br>" + 
														"(e.g. X11 Window Server)");
		
		RMIServer_RemoteControl rmiS_RC;
		
		try {
			rmiS_RC = new RMIServer_RemoteControl(robot);
			rmiS_RC.mousePress(buttons);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public void mouseRelease(String key, int buttons) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		if (robot == null) throw new RemoteException("<br>Remote System GUI not available.<br>" + 
														"Make sure the RMI Service is started under a Window session.<br>" + 
														"(e.g. X11 Window Server)");
		
		RMIServer_RemoteControl rmiS_RC;
		
		try {
			rmiS_RC = new RMIServer_RemoteControl(robot);
			rmiS_RC.mouseRelease(buttons);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public void mouseWheel(String key, int wheelAmt) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		if (robot == null) throw new RemoteException("<br>Remote System GUI not available.<br>" + 
														"Make sure the RMI Service is started under a Window session.<br>" + 
														"(e.g. X11 Window Server)");
		
		RMIServer_RemoteControl rmiS_RC;
		
		try {
			rmiS_RC = new RMIServer_RemoteControl(robot);
			rmiS_RC.mouseWheel(wheelAmt);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public Dimension getScreenSize(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		if (robot == null) throw new RemoteException("<br>Remote System GUI not available.<br>" + 
														"Make sure the RMI Service is started under a Window session.<br>" + 
														"(e.g. X11 Window Server)");
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		
		return d;
	}
	
	
	public boolean openRC(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		if (robot == null) throw new RemoteException("<br>Remote System GUI not available.<br>" + 
														"Make sure the RMI Service is started under a Window session.<br>" + 
														"(e.g. X11 Window Server)");
		
		if (rcSession == 0) {
			throw new RemoteException("Session is in used by other user.");
		} else {
			//Set the RC Session to 0
			rcSession = 0;
		}
		
		return true;
	}
	
	
	public boolean releaseRC(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		if (robot == null) throw new RemoteException("<br>Remote System GUI not available.<br>" + 
														"Make sure the RMI Service is started under a Window session.<br>" + 
														"(e.g. X11 Window Server)");
		
		//Perform a housekeeping
		System.gc();
		
		//Reset the RC Session to 1
		rcSession = 1;
		
		return true;
	}
	
	
	public String getCurrPath(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		String currPath = "";
		File f = new File(new File("RMIAdmin.jar").getAbsolutePath());
		currPath = f.getParent();
		
		return currPath;
	}
	
	
	public boolean upgradeRMI(String key) throws RemoteException {
		return false;
	}
	
	
	public long getTimer(String key) throws RemoteException {
		if (!validateKey(key)) throw new RemoteException("You are not authorized to access this RMI system");
		
		return System.currentTimeMillis();
	}
	
	
	public static void main(String [] args) {
		int port = 0;
		String rmiBindName = "rmiAdmin";
		String serverKey = null;
		String encryptedPassphrase = "";
		boolean auth_ON = false;
		
		try {
			for (int i=0; i<args.length; i++) {
				if (args[i].equals("-p")) {
					try {
						port = Integer.parseInt(args[i+1]);
					} catch (NumberFormatException nfe) {
						errorParameter("-i \"Port Number\" must be an integer value");
					} catch (ArrayIndexOutOfBoundsException aiooe) {
						errorParameter("Missing -i \"Port Number\" value");
					}
					
					
				} else if (args[i].equals("-h")) {
					helpMenu();
					
				} else if (args[i].equals("-k")) {
					File f = new File("serverKey.dat");
					if (f.exists() == false) {
						System.out.println("Error! " + f.getName() + " is not found.");
						System.exit(0);
						
					} else {
						BufferedReader br = new BufferedReader(new FileReader(f));
						String tmp = "";
						
						if ((tmp = br.readLine()) != null) {
							serverKey = tmp;
						}
						
						br.close();
					}
					
					f = new File("serverPassphrase.dat");
					if (f.exists() == false) {
						System.out.println("Error! " + f.getName() + " is not found.");
						System.exit(0);
						
					} else {
						BufferedReader br = new BufferedReader(new FileReader(f));
						String tmp = "";
						
						if ((tmp = br.readLine()) != null) {
							rmiBindName = tmp;
							encryptedPassphrase = tmp;
						}
						
						br.close();
					}
					
					System.out.print("Decrypting Server Key to generate RMI Application Name" + "...   ");
						RMIClient_CipherKey cKey = new RMIClient_CipherKey();
						if (cKey.changeKey(serverKey) != true) {
							System.out.println("Fail.\n");
							errorParameter("* Server Key must be characters only\n" + 
											"* Server Key must be in upper case\n" + 
											"* Server Key must be non-empty\n");
						} else {
							rmiBindName = cKey.decryptLine(rmiBindName);
						}
					System.out.println("Done. ");
					
					auth_ON = true;
				}
			}
			
			if (System.getSecurityManager() == null) {
				/********************************************
				 * This line is remarked due to the 
				 * conflict with File System Access
				 *******************************************/
				//System.setSecurityManager(new RMISecurityManager());
			}
			
			if (port == 0) {
				System.out.println("Port number is not specified, default port 1099 will be used.");
				port = 1099;
			}
			
		} catch (Exception exc) {
			//exc.printStackTrace();
			helpMenu();
		}
		
		try {
			System.out.print("Setting Port No. to " + port + "...   ");
			java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.createRegistry(port);
			System.out.println("Done.");
			
			System.out.print("Setting RMI Application Name [" + rmiBindName + "]...   ");
			String app = "rmi://127.0.0.1:" + port + "/" + rmiBindName;
			System.out.println("Done .");
			
			System.out.print("Creating new instance for RAdminServer...   ");
			RMIServer server = new RMIServer();
			System.out.println("Done.");
			
			System.out.print("Rebinding application to RMI Registry...   ");
			Naming.rebind(app, server);
			System.out.println("Done.");
			
			server.rmiBindName = rmiBindName;
			server.app = app;
			server.port = port + "";
			server.ptr = server;
			server.serverKey = serverKey;
			server.serverPassPhrase = rmiBindName;
			server.auth_ON = auth_ON;
			
			System.out.print("Testing Runtime for Command Line...");
			if (server.checkCLready(encryptedPassphrase) == true) {
				System.out.println("Done.");
			} else {
				System.out.println("Fail.");
			}
			
			System.out.print("Validating File System...   ");
			if (server.checkFSready(encryptedPassphrase) == true) {
				System.out.println("Done.");
			} else {
				System.out.println("Fail.");
			}
			
			try {
				System.out.print("Constructing a Robot Object for Native System's Screen...");
				server.robot = new Robot();
				System.out.println("Done.");
				
			} catch (AWTException awtexc) {
				System.out.println("Fail.");
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			System.exit(0);
		}
	}
	
	
	private boolean validateKey(String key) {
		if (auth_ON == false) return true;
		
		boolean passed = false;
		
		try {
				RMIClient_CipherKey cKey = new RMIClient_CipherKey();
				if (cKey.changeKey(serverKey) != true) {
					System.out.println("Fail.\n");
					System.out.println("* Server Key must be characters only\n" + 
										"* Server Key must be in upper case\n" + 
										"* Server Key must be non-empty\n");
				} else {
					String decryptedKey = cKey.decryptLine(key);
					
					if (decryptedKey.equals(serverPassPhrase)) {
						//System.out.println("[Authentication Passed]"); //Debugger
						passed = true;
					} else {
						//System.out.println("[Authentication Denied]"); //Debugger
						passed = false;
					}
				}
				
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
			
		} finally {
			return passed;
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
		System.out.println("Start the RMIServer daemon at local computer");
		System.out.println();
		System.out.println();
		System.out.println("SYNOPSIS");
		System.out.println("=============================");
		System.out.println("java RMIServer -options \"values\"");
		System.out.println();
		System.out.println();
		System.out.println("DESCRIPTION");
		System.out.println("=============================");
		System.out.println("-p\tSpecify the \"Port Number\" for the application service e.g. \"1091\"");
		System.out.println("-k\tTo activate the authentication for RMI ");
		System.out.println();
		System.out.println("AUTHOR");
		System.out.println("=============================");
		System.out.println("Written by Jeffrey Garcia.");
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