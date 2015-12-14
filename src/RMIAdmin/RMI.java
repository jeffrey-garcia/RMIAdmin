package RMIAdmin;


import java.rmi.*;

import java.awt.*;
import javax.swing.*;

import java.io.*;
import java.util.*;


public interface RMI extends Remote {
	
	//====== Methods for RMI maintenance ======
	boolean isConnected(String key) throws RemoteException;
	String getOS(String key) throws RemoteException;
	boolean restartRMI(String key) throws RemoteException;
	void shutdownRMI(String key) throws RemoteException;
	void collectGarbage(String key) throws RemoteException;
	//=========================================
	
	//====== Methods for Command Line ======
	String setCommand(String key, String shell, String command) throws RemoteException;
	void runCommand(String key, String threadName) throws RemoteException;
	void cancelCommand(String key, String threadName) throws RemoteException;
	boolean checkCommandStatus(String key, String threadName) throws RemoteException;
	LinkedList getCommandResult(String key, String threadName) throws RemoteException;
	//======================================
	
	//====== Methods for File Browser ======
	boolean checkFSready(String key) throws RemoteException;
	String [] openFileSystem(String key) throws RemoteException;
	String [][] getFileList(String key, String location) throws RemoteException;
	Vector getFileInfo(String key, String location) throws RemoteException;
	String createFile(String key, String location, String type) throws RemoteException;
	String deleteFile(String key, String location) throws RemoteException;
	String renameFile(String key, String srcLocation, String destLocation) throws RemoteException;
	String copyAndPaste(String key, String srcLocation, String destLocation) throws RemoteException;
	String cutAndPaste(String key, String srcLocation, String destLocation) throws RemoteException;
	boolean checkDuplicateTarget(String key, String srcFile, String destLocation) throws RemoteException;
	int countObject(String key, String srcLocation) throws RemoteException;
	long getFileSize(String key, String srcLocation) throws RemoteException;
	String getRoundedFileSize(String key, String srcLocation) throws RemoteException;
	String getRoundedFileSize(String key, long sizeInLong) throws RemoteException;
	String getFileSeparator(String key) throws RemoteException;
	//======================================
	
	//====== Methods for File Transfer ======
	boolean testPathExist(String key, String location) throws RemoteException;
	int uploadFile_Bin(String key, String destLocation, byte [] buffer, int session, int vID) throws RemoteException;
	int uploadFile_Ascii(String key, String destLocation, byte [] buffer, int session, int vID) throws RemoteException;
	Object [] downloadFile_Bin(String key, String srcLocation, byte[] buffer, int session, int vID) throws RemoteException;
	Object [] downloadFile_Ascii(String key, String srcLocation, byte[] buffer, int session, int vID) throws RemoteException;
	//=======================================
	
	//====== Methods for Memory Monitor ======
	long [] getMemory(String key) throws RemoteException;
	String [][] getSystemInfo(String key) throws RemoteException;
	//========================================
	
	//====== Methods for Remote Control ======
	public byte [] captureScreen(String key, Rectangle screenRect) throws RemoteException;
	public void keyPress(String key, int keycode) throws RemoteException;
	public void keyRelease(String key, int keycode) throws RemoteException;
	public void mouseMove(String key, int x, int y) throws RemoteException;
	public void mousePress(String key, int buttons) throws RemoteException;
	public void mouseRelease(String key, int buttons) throws RemoteException;
	public void mouseWheel(String key, int wheelAmt) throws RemoteException;
	public Dimension getScreenSize(String key) throws RemoteException;
	public boolean openRC(String key) throws RemoteException;
	public boolean releaseRC(String key) throws RemoteException;
	//========================================
	
	//====== Methods for Remote Timer ======
	public long getTimer(String key) throws RemoteException;
	//======================================
	
	//====== Methods for Auto-Upgrade ======
	public String getCurrPath(String key) throws RemoteException;
	public boolean upgradeRMI(String key) throws RemoteException;
	//======================================
}