/************************************************************************
 *
 * This class is for the auto-upgrade feature.
 * Here are steps for you to perform debugging:
 *
 * 1. Start the Tomcat server, place the RMIAdmin.jar in a specific 
 *	  path.
 *
 * 2. Hardcode the path in the downloadThread.run() method
 *
 * 3. Start a RMIServer locally at c:\
 *
 * 4. Start the RMIClient at the development folder: 
 *	  c:\j2sdk1.4.2_06\002_dev
 *    
 * 5. Use "echo > RMIAdmin.jar" to corrupt the RMIAdmin.jar in the 
 *    development folder (such that we can upgrade it later).
 *
 * 6. Run the auto-upgrade feature.
 *
 ***********************************************************************/
package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.io.*;
import java.util.*;

import java.net.*;
import javax.help.*;
import javax.help.search.*;


public class RMIClientGUI_AutoUpgrade extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	private RMIClientGUI rmiGUI;
	public RMIClientGUI_Debugger rmiDebug;
	
	private JInternalFrame jif;
	private JLabel jlbText;
	private JProgressBar jpb;
	private JButton jbtn_cancel_update;
	private JCheckBox jcb_update_server;
	
	private String replaced = "false";
	
	private boolean updateServer = false;
	private boolean cancel = false;
	private boolean terminate = false;
	
	
	public RMIClientGUI_AutoUpgrade(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		this.rmiGUI = (RMIClientGUI)rmiDebug.getTopLevelAncestor();
		
		initGUI();
		startUpgrade();
	}
	
	
	public boolean warning(String opt) {
		JDialog.setDefaultLookAndFeelDecorated(true);	
		JOptionPane jop;
		JDialog jdg;
		
		if (opt == null) {
			rmiDebug.setDebugText("Error! java.lang.NullPointerException");
			return false;
		}
		
		opt = opt.replaceAll("\n","<br>");
		String temp = "<html>" + opt + "</html>";
		
		jop = new JOptionPane(temp,
									JOptionPane.WARNING_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION);
		
		jdg = jop.createDialog(this,"Warning!");
		
		//Calulation the position of JDialog to appear in absolute middle of application
		JFrame fr = (JFrame)jdp.getTopLevelAncestor();	
		int x = 0;
		int y = 0;
		x = fr.getWidth()/2 - jdg.getWidth()/2;
		x += fr.getX();
		y = fr.getHeight()/2 - jdg.getHeight()/2;
		y += fr.getY();
		jdg.setLocation(x,y);
		jdg.setVisible(true);
		
		if (jop.getValue() != null) {
			int value = ((Integer)jop.getValue()).intValue();
			
			if (value==JOptionPane.YES_OPTION) {
				return true;
			} else {
				return false;
			}
			
		} else {
			return false;
		}
	}
	
	
	public RMIClientGUI_Debugger getDebugger() {
		return rmiDebug;
	}
	
	
	public void initGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		
		jif = new JInternalFrame();
		jif.setTitle("RMIAdmin Auto-Upgrade");
    	jif.setResizable(false);
    	jif.setIconifiable(false);
    	jif.setMaximizable(false);
    	jif.setClosable(false);
    	jif.setLayer(JLayeredPane.PALETTE_LAYER); //Makes the JInternalFrame always on top
    	
		jif.getContentPane().setLayout(new BorderLayout());
		
			JPanel jpTop = new JPanel();
			jpTop.setLayout(new BorderLayout());
				
				jlbText = new JLabel("<html>&nbsp;Initializing... </html>");
				
			jpTop.add(jlbText, BorderLayout.NORTH);
			jpTop.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/fileTransfer.gif"))), BorderLayout.CENTER);
			
			JPanel jpCenter = new JPanel();
			jpCenter.setLayout(new BorderLayout());
			
				jpb = new JProgressBar();
		        jpb.setValue(0);
		        jpb.setStringPainted(true);
		        jpb.setBorderPainted(true);
			
			jpCenter.add(jpb, BorderLayout.CENTER);
			
			JPanel jpBottom = new JPanel();
			jpBottom.setLayout(new BorderLayout());
			
				jcb_update_server = new JCheckBox("<html><font color='#FF0066'>Update <b>ALL</b> the managed RMI Server</font></html>", false);
				jcb_update_server.addItemListener(new ItemListener () {
					public void itemStateChanged(ItemEvent ie) {
						if (ie.getSource() == jcb_update_server) {
							if (jcb_update_server.isSelected() == true) {
								updateServer = true;
								rmiDebug.setDebugText("Confirm updating all the managed RMI Server.");
							} else {
								updateServer = false;
								rmiDebug.setDebugText("Cancel updating all the managed RMI Server.");
							}
						}
					}
				});
				
				jbtn_cancel_update = new JButton("Cancel");
				jbtn_cancel_update.setToolTipText("Click this button to abort the download immediately.");
				jbtn_cancel_update.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jlbText = new JLabel("<html>&nbsp;Cancelling ... </html>");
						rmiDebug.setDebugText("Cancel the auto-upgrade.");
						
						cancel = true;
						replaced = "cancel";
					}
				});
				jbtn_cancel_update.setSelected(true);
				
			jpBottom.add(jcb_update_server, BorderLayout.CENTER);
			jpBottom.add(jbtn_cancel_update, BorderLayout.EAST);
			
		jif.getContentPane().add(jpTop, BorderLayout.NORTH);
		jif.getContentPane().add(jpCenter, BorderLayout.CENTER);
		jif.getContentPane().add(jpBottom, BorderLayout.SOUTH);
		
		jif.pack();
		
	    try {
	    	jif.setSelected(true);
	    } catch (Exception exc) {
	    	//Nothing to do
	    }
	    
	    jdp.add(jif);
		
		//Calulation the position of JDialog to appear in absolute middle of application
		double screenWidth = ((Toolkit.getDefaultToolkit()).getScreenSize()).getWidth();
		double screenHeight = ((Toolkit.getDefaultToolkit()).getScreenSize()).getHeight();
		int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
		int y = (int)(jdp.getHeight()/2 - jif.getHeight()/2);
		
		jif.setLocation(x,y);
    	jif.setVisible(true);
	}
	
	
	private void startUpgrade() {
		new downloadThread(this).start();
	}
	
	
	private class downloadThread extends Thread {
		private RMIClientGUI_AutoUpgrade rmiGUI_AU;
		
		private String file = "RMIAdmin.jar";
		private String tmpFile = "RMIAdmin_tmp";
		
		
		public downloadThread(RMIClientGUI_AutoUpgrade rmiGUI_AU) {
			this.rmiGUI_AU = rmiGUI_AU;
		}
		
		public void run() {
			try {
				sleep(2000);
				
				URL url = new URL("http://www.rmiadmin.net/deploy/" + file);
				//URL url = new URL("http://127.0.0.1/" + file); //for Debug mode
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setDoOutput(true);
				connection.setUseCaches (false);
				connection.connect();
				
				//Update the progress message
				jlbText.setText("<html>&nbsp;Checking new updates ...</html>");
				
				//get jar file length from response header
				int newFileSize = connection.getContentLength();
				
				//Set the max value of progress bar
				jpb.setMaximum(newFileSize);
				
				FileInputStream fis = new FileInputStream(file);
				int oldFileSize = fis.available();
				fis.close();
				
				rmiDebug.setDebugText("New file size from server: " + newFileSize + " bytes");
				rmiDebug.setDebugText("Current file size from client: " + oldFileSize + " bytes");
				
				if (oldFileSize!=newFileSize && cancel==false) {
					InputStream is  = connection.getInputStream();
					FileOutputStream out = new FileOutputStream(tmpFile);
					
					int bytesRead = 0;
					byte[] bytes = new byte[4096];
					
					//the counter for download progress
					long curr = 0;
					
					//Update the progress message
					jlbText.setText("<html>&nbsp;Downloading file ...</html>");
					rmiDebug.setDebugText("Downloading file ...");
					
					while((bytesRead = is.read(bytes)) != -1) {
						out.write(bytes, 0, bytesRead);
						
						//update the progress bar
						curr += bytes.length;
						jpb.setValue((int)curr);
						
						if (cancel == true) {
							//break the loop immediately
							break;
						}
					}
					
					//close all the stream
					is.close();
					out.flush();
					out.close();
					rmiDebug.setDebugText("Downloading completed.");
					
					if (cancel == false) {
						replaced = "true";
						
						//disable the cancel button
						jbtn_cancel_update.setEnabled(false);
						
						//Update the progress message
						jlbText.setText("<html>&nbsp;Replacing file ...</html>");
						rmiDebug.setDebugText("Replacing file ...");
						
						//begin to copy the tmp file to actual output file
						byte[] byteArray = new byte[4096];
						int len = 0;
						
						FileInputStream input = new FileInputStream(tmpFile);
						FileOutputStream output = new FileOutputStream(new File(file));
								
						while ((len=input.read(byteArray)) != -1) {
							output.write(byteArray, 0, len);
						}
						
						input.close();
						output.close();
						rmiDebug.setDebugText("Done.");
						
						//start upgrade all the managed server
						if (updateServer == true) updateRMIServer();
					}
					
				} else {
					//nothing to do
				}
				
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
				
				replaced = "error";
				rmiGUI.warning("File Not Found!<br>" + 
								"The RMIAdmin.jar file is missing or corrupted!");
			
			} catch (ConnectException ce) {
				ce.printStackTrace();
				
				replaced = "error";
				rmiGUI.warning("Upgrade Fail!<br>" + 
								"The network link maybe temporarily unavailable, <br>" + 
								"please try again later.<br><br>" + 
								"Please also ensure that your computer have direct <br>" + 
								"internet access.");
				
			} catch (Exception exc) {	
				exc.printStackTrace();
				
				replaced = "error";
				rmiGUI.warning("Upgrade Fail!<br>" + 
								exc.getMessage());
				
			} finally {
				//Delete the tmp file
				rmiDebug.setDebugText("Deleting tmp file ...");
				new File(tmpFile).delete();
				rmiDebug.setDebugText("Done");
				
				jif.setVisible(false);
				jif.dispose();
				
				if (replaced.equals("true")) {
					rmiDebug.setDebugText("Upgrade Result: File Replaced!");
					
					if (updateServer == true) {
						if (terminate == false) {
							rmiGUI.alert("Upgrade completed successfully!\n" + 
										"Please restart both RMIAdmin client and the \n" + 
										"RMI service at remote server to make the changes \n" + 
										"effect.");
						} else {
							rmiGUI.alert("Upgrade not fully completed!\n" + 
										"Please restart the RMIAdmin client to make the changes \n" + 
										"effect. \n\n" + 
										"Note that the upgrade has been terminated and therefore \n" + 
										"not completed for all the servers.");
						}

					} else {
						rmiGUI.alert("Upgrade completed successfully!\n" + 
									"Please restart RMIAdmin client to make the changes \n" + 
									"effect.");
					}
					
				} else if (replaced.equals("false")) {
					rmiDebug.setDebugText("Upgrade Result: File NOT Replaced!");
					rmiGUI.alert("Already in latest version.<br>Upgrade not performed.");
					
				} else if (replaced.equals("cancel")) {
					rmiDebug.setDebugText("Upgrade Cancel: File NOT Replaced!");
					rmiGUI.alert("Upgrade has been terminated.<br>Upgrade not performed");
				}
			}
		}
		
		private void updateRMIServer() {
			final Object [][] data;
			final int dataRow;
			final int dataCol;
			
			try {
				//====== Get the list of available server ======
				RMIClient_XMLParser rmiXML = new RMIClient_XMLParser(rmiDebug);
				data = rmiXML.loadRecord();
				dataRow = rmiXML.getRow();
				dataCol = rmiXML.getCol();
				//==============================================
				
				//====== Update the progress message ======
				jlbText.setText("<html>&nbsp;Start update all server(s) ...</html>");
				//=========================================
				
				//=== Initilize the max value of JProgressBar ===
				jpb.setMaximum(dataRow);
				//===============================================
				
				//=== re-enable the cancel button ===
				jbtn_cancel_update.setEnabled(true);
				
				//replace the previous listener
				ActionListener [] al = jbtn_cancel_update.getActionListeners();
				for (int k=0; k<al.length; k++) jbtn_cancel_update.removeActionListener(al [k]);
				
				jbtn_cancel_update.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jlbText.setText("<html>&nbsp;Cancelling the update to server...</html>");
						rmiDebug.setDebugText("Cancelling the update to server...");
						
						//since only the update to server has been terminated
						//therefore we restore the cancel to false ,and the 
						//replaced to true;
						//to true
						cancel = false;
						replaced = "true";
						terminate = true;
						
						jbtn_cancel_update.setEnabled(false);
					}
				});
				//===================================
				
				String serverName = "";
				String serverIP = "";
				String serverPort = "";
				String shell = "";
				
				for (int i=0; i<data.length; i++) {
					serverName = data [i][0] + "";
					serverIP = data [i][1] + "";
					serverPort = data [i][2] + "";
					shell = data [i][5] + "";
					
					rmiDebug.setDebugText("Updating " + serverName + 
											" on IP " + serverIP + 
											" @ Port " + serverPort + 
											" using shell " + shell + " ...");
					
					//update the progress bar;
					jpb.setValue(i);
					
					//Update the progress message
					jlbText.setText("<html>&nbsp;Updating " + serverName + " ...</html>");
					
					//One by one update, since we don't want to consume 
					//excessive bandwidth at the same time
					uploadToAllServer(serverName, serverIP, serverPort, shell);
					
					if (jbtn_cancel_update.isEnabled() == false) {
						break;
					}
				}
				
			} catch (Exception exc) {
				rmiGUI.warning(exc.getMessage());
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
			}
		}
		
		private void uploadToAllServer(String serverName, String serverIP, String serverPort, String shell) {
			try {
				//====== Put the implementation here ======
				RMIClient_AutoUpgrade rmi_AU;
				rmi_AU = new RMIClient_AutoUpgrade(serverIP, serverPort, shell, rmiGUI_AU);
				boolean completed = rmi_AU.updateServer("RMIAdmin.jar");
				
				if (completed == true) {
					rmiDebug.setDebugText("Upgrade finished for " + serverName + " successfully.");
				} else {
					rmiDebug.setDebugText("Upgrade for " + serverName + " failed.");
				}
				//=========================================
				
			} catch (Exception exc) {
				rmiGUI.warning(exc.getMessage());
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
			}
		}
	}
}