package RMIAdmin;


import java.io.*;
import java.util.*;


public class RMIServer_FileTransfer {
	private BufferedOutputStream bos;
	private FileOutputStream fos;
	
	private BufferedInputStream bis;
	
	
	public RMIServer_FileTransfer() {
		//nothing to do...
	}
	
	
	private String getOS() {
		String os = System.getProperty("os.name");
		return os;
	}
	
	
	public boolean testPathExist(String location) {
		boolean exist = false;
		File f = new File(location);
		
		if (f.exists()) {
			//System.out.println("File already exist!"); //Debugger
			exist = true;
		} else {
			//System.out.println("File not exist!"); //Debugger
			exist = false;
		}
		
		return exist;
	}
	
	
	public String openUploadStream(String destLocation, String type) {
		String error = "";
		
		try {
			System.out.println("Open Upload Stream at location: " + destLocation + " of type: " + type); //Debugger
			
			/*********************************************
			 *
			 * This part is really a problem.
			 * Since error message or exception WILL 
			 * occur at server side when the path is 
			 * drive A and drive A is not ready, which 
			 * then require an answer to the message 
			 * prompt before the process could continue.
			 * 
			 * So the thread will definitely stuck here.
			 *
			 ********************************************/
			String destPath = "";
			if (getOS().toLowerCase().indexOf("window") >= 0) {
				destPath = destLocation.substring(0, destLocation.indexOf(File.separator));
			} else {
				destPath = destLocation.substring(0, 1);
			}
			System.out.println("Dest Path: " + destPath); //Debugger
			
			/***********************************************
			 * Actually this is obsoleted as we previously 
			 * want to use this part to validate if target 
			 * drive is ready
			 **********************************************/
			if (new File(destPath).exists()) {
				System.out.println("Path exist"); //Debugger
				File destFile = new File(destLocation);
				
				if (type.equals("bin")) {
					bos = new BufferedOutputStream(new FileOutputStream(destFile));
				} else if (type.equals("ascii")) {
					fos = new FileOutputStream(destFile);
				}
				
			} else {
				System.out.println("Path not exist"); //Debugger
				error = "Path not exist";
			}
			
		} catch (Exception exc) {
			error = exc.getMessage();
			exc.printStackTrace();
			trapError(exc);
		}
		
		return error;
	}
	
	
	public void closeUploadStream(String destLocation, String type) {
		try {
			System.out.println("Close Upload Stream: " + destLocation); //Debugger
			
			if (type.equals("bin")) {
				if (bos != null) {
					bos.flush();
					bos.close();
				}
			} else if (type.equals("ascii")) {
				if (fos != null) {
					fos.close();
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public String uploadFile_Bin(String destLocation, byte [] buffer) {
		String error = "";
		
		try {
			System.out.println("Uploading to " + destLocation + " in Binary mode... "); //Debugger
			
			bos.write(buffer, 0, buffer.length);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
			error = exc.getMessage() + "";
		}
		
		return error;
	}
	
	
	public String uploadFile_Ascii(String destLocation, byte [] buffer) {
		String error = "";
		
		try {
			System.out.println("Uploading to " + destLocation + " in Ascii mode... "); //Debugger
			
			/*******************************************************
			 *
			 * This is a much improved algorithm, as we filter out 
			 * all the possible carriage return, such that the 
			 * resulting buffer will be a packed byte [] array. 
			 * 
			 * As a result the writing of output stream can benefit
			 * from the use of the buffered data, hence much faster
			 * running time & improved efficiency.
			 *
			 ******************************************************/
			
			if (getOS().toLowerCase().indexOf("window") >= 0) {
				//For Windows OS, no need to remove the carriage return
				fos.write(buffer);
				
			} else {
				//This part find out how many carraige return
				int crCount = 0;
				for (int i=0; i<buffer.length; i++) {
					int data = (int)buffer [i];
					
					if (data == 13) crCount ++;
				}
				
				//Declare a new byte [] array
				byte [] newBuffer = new byte [buffer.length - crCount];
				
				//Fill-in data to the new byte [] array
				int j = 0;
				for (int i=0; i<buffer.length; i++) {
					int data = (int)buffer [i];
					
					if (data != 13) {
						newBuffer [j] = buffer [i];
						j ++;
					}
				}
				
				//Assign the new buffer to the original buffer
				buffer = newBuffer;
				
				fos.write(buffer);
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
			error = exc.getMessage() + "";
		}
		
		return error;
	}
	
	
	public String openDownloadStream(String srcLocation, String type) {
		String error = "";
		
		try {
			System.out.println("Open Download Stream at location: " + srcLocation + " of type: " + type); //Debugger
			
			bis = new BufferedInputStream(new FileInputStream(srcLocation));
			
		} catch (Exception exc) {
			error = exc.getMessage();
			exc.printStackTrace();
			trapError(exc);
		}
		
		return error;
	}
	
	
	public void closeDownloadStream(String srcLocation) {
		try {
			System.out.println("Close Download Stream: " + srcLocation); //Debugger
			
			if (bis != null) {
				bis.close();
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public byte [] downloadFile_Bin(String srcLocation, byte [] buffer) {
		try {
			//System.out.println("Downloading " + srcLocation + "..."); //Debugger
			
			bis.read(buffer,0,buffer.length);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return buffer;
	}
	
	
	public byte [] downloadFile_Ascii(String srcLocation, byte [] buffer) {
		try {
			//System.out.println("Downloading " + srcLocation + "..."); //Debugger
			
			bis.read(buffer,0,buffer.length);
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return buffer;
	}
	
	
	private void trapError(Exception exc) {
		//Print Exception Message to Logfile
		(new RMIServer_EventHandler(exc.getMessage())).getFile();
		
		//Print Stack Trace to Logfile
		(new RMIServer_EventHandler(exc.getStackTrace())).getFile();
	}
}