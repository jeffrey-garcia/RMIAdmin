package RMIAdmin;


import java.io.*;
import java.util.*;


public class RMIServer_FileBrowser {
	
	
	public static void main(String [] args) throws Exception {
		RMIServer_FileBrowser rmiS_FB = new RMIServer_FileBrowser();
		String [][] fList;
		File [] drive;
		
		fList = rmiS_FB.getFiles("c:/");
		drive = rmiS_FB.getDrives();
		
		//====== Get all the files and directories under c: ======
		for (int i=0; i<fList.length; i++) {
			System.out.println(fList [i][0] + "," + fList [i][1]); //Debugger
		}
		//========================================================
		
		//====== Get the available drives in current Root File System ======
		for (int i=0; i<drive.length; i++) {
			System.out.println(drive [i]); //Debugger
		}
		//==================================================================
	}
	
	
	public RMIServer_FileBrowser() {
		//nothing to do...
	}
	
	
	public File [] getDrives() {
		File [] drive = File.listRoots();
		
		//System.out.println("Available Drives in Remote File System");
		//System.out.println("--------------------------------------------------");
		for (int i=0; i<drive.length; i++) {
			//System.out.println(drive [i]); //Debugger
		}
		//System.out.println("");
		
		return drive;
	}
	
	
	public String [][] getFiles(String location) {
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
			trapError(exc);
		}
		
		return fList;
	}
	
	
	public Vector getFileInfo(String location) {
		File file = null;
		Vector vFileInfo = null;
		
		try {
			if (location.equals("")==false && location!=null) {
				file = new File (location);
				
				/*
				//------ Debugger ------
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
				*/
				
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
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
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
			trapError(exc);
			
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
			trapError(exc);
			
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
			trapError(exc);
			
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
			trapError(exc);
			
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
			trapError(exc);
			
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
		
		//System.out.println("Create Directory " + destDir.getName()); //Debugger
		
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
	
	
	public int countObject(String src) {
		int totalObject = 0;
		
		try {
			File srcFile = new File(src);
			
			if (srcFile.isDirectory() == true) {
				//System.out.println("Source is a Directory"); //Debugger
				totalObject = countObjectInDir(srcFile, totalObject);
				
			} else {
				/*******************************
				 * If source object is a file,
				 * return totalObject as size 
				 * of file
				 ******************************/
				//System.out.println("Source is a File"); //Debugger
				totalObject = new Long(srcFile.length()).intValue();
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
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
	
	
	public boolean checkDuplicateTarget(String srcFile, String dest) {
		boolean duplicate = false;
		
		try {
			String [][] fList =  getFiles(dest);
			
			if (fList != null) {
				for (int i=0; i<fList.length; i++) {
					String destFile = fList [i][0];
					//System.out.println("****" + destFile); //Debugger
					
					if (destFile.equals(srcFile)) {
						//System.out.println(srcFile + " found in " + dest); //Debugger
						duplicate = true;
						break;
					}
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return duplicate;
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
	
	
	private void trapError(Exception exc) {
		//Print Exception Message to Logfile
		(new RMIServer_EventHandler(exc.getMessage())).getFile();
		
		//Print Stack Trace to Logfile
		(new RMIServer_EventHandler(exc.getStackTrace())).getFile();
	}
}