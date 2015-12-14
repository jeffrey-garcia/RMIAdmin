package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import java.text.*;


public class RMIClientGUI_RemoteTimer extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	private RMIClientGUI_Debugger rmiDebug;
	
	private int serverID = 0;
	private String serverHostname = "";
	private String serverIP = "";
	private String serverPort = "";
	private String serverOS = "";
	private String serverShell = "";
	private String serverDesc = "";
	
	
	public RMIClientGUI_RemoteTimer(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		
		//setBackground(Color.darkGray);
		setLayout(new BorderLayout());
		
		//------ Initialize the GUI ------
		initGUI();
	}
	
	
	public void initGUI() {
		//nothing to do...
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
	
	
	public void selectServer() {
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
			
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane jop;
			JDialog jdg;
			
			String selectedServer = "";
			
			String server [] = new String [dataRow];
			for (int i=0; i<dataRow; i++) {
				server [i] = i + ". " + data [i][0] + "";
			}
			
			jop = new JOptionPane("",
									JOptionPane.QUESTION_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION);
									
			jop.setSelectionValues(server);	
			jdg = jop.createDialog(this,"Select Server");
			
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
			
			selectedServer = (String)jop.getInputValue();
			
			if (selectedServer.equals("uninitializedValue") == false) {
				rmiDebug.setDebugText("Selected Server: " + selectedServer);
				
				int i = Integer.parseInt(selectedServer.substring(0, selectedServer.indexOf(". ")));
				
				serverID = i;
				serverHostname = data [i][0] + "";
				serverIP = data [i][1] + "";
				serverPort = data [i][2] + "";
				serverOS = data [i][3] + "";
				serverShell = data [i][4] + "";
				serverDesc = data [i][5] + "";
				
				loadTimer();
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void loadTimer() {
		new RemoteTimer(this, serverHostname, serverIP, serverPort).start();
	}
	
	
	private class RemoteTimer extends Thread {
		private RMIClientGUI rmiGUI;
		private RMIClientGUI_RemoteTimer rmiGUI_RT;
		private RMIClient_RemoteTimer rmi_RT;
		
		private String server;
		private String ip;
		private String port;
		
		private JInternalFrame jif;
		private JPanel jpCenter;
		
		private JLabel jlbRemoteTime;
		private JLabel jlbLocalTime;
		
		private boolean terminate = false;
		
		private Date d = new Date();
		private String sTime = "MM/dd/yyyy HH:mm:ss";
		private SimpleDateFormat formatter = new SimpleDateFormat(sTime);
		
		public RemoteTimer(RMIClientGUI_RemoteTimer rmiGUI_RT, String server, String ip, String port) {
			this.rmiGUI_RT = rmiGUI_RT;
			this.server = server;
			this.ip = ip;
			this.port = port;
			
			this.rmiGUI = (RMIClientGUI)rmiDebug.getTopLevelAncestor();
		}
		
		public void run() {
			try {
				rmiDebug.setDebugText("System Timer - " + server + 
										" on IP " + ip + 
										" @ Port " + port + " ...");
				
				rmi_RT = new RMIClient_RemoteTimer(ip, port, rmiGUI_RT);
				
				drawUI(server);
				
				long time = 0;
				while (terminate == false) {
					time = rmi_RT.getTimer();
					
					//Error has occured, break the loop immediately
					if (time == 0) {
						jif.setTitle("Remote Timer - " + server);
						jlbRemoteTime.setText("<html>Remote System: " + server + "<br><font color='red'>Fail to connect server!</font></html>");
						
						terminate = true;
						break;
					}
					
					jif.setTitle("Remote Timer - " + server);
					
					//update the Timer String
					d.setTime(time);
					sTime = formatter.format(d);
					jlbRemoteTime.setText("<html>Remote System: " + server + "<br>Time: " + sTime);
					
					sleep(1000); //wait for 1000 milli-seconds
				}
				
				rmiDebug.setDebugText("Remote Timer - " + server + " has been terminated");
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void drawUI(String server) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			jif = new JInternalFrame("Remote Timer - Loading " + server + "...");
			jif.setResizable(false);
	    	jif.setClosable(true);
	    	jif.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
	    	jif.setLayer(JLayeredPane.PALETTE_LAYER); //Makes the JInternalFrame always on top
    		
			jif.getContentPane().setLayout(new BorderLayout());
			
				jpCenter = new JPanel();
				jpCenter.setLayout(new FlowLayout(FlowLayout.LEFT));
				
					final JButton jbtn = new JButton("Hide");
					jbtn.setToolTipText("Click this button to hide the timer");
					jbtn.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							try {
								jif.setIcon(true);
							} catch (Exception exc) {
							}
						}
					});
					
					jlbRemoteTime = new JLabel("<html>Remote System: " + server + "<br>Time:" + sTime);
				
				jpCenter.add(jbtn);
				jpCenter.add(jlbRemoteTime);
				
			jif.getContentPane().add(jpCenter, BorderLayout.CENTER);
			
			jif.addInternalFrameListener(new InternalFrameListener() {
				public void internalFrameOpened(InternalFrameEvent ife) {}
				public void internalFrameIconified(InternalFrameEvent ife) {}
				public void internalFrameDeiconified(InternalFrameEvent ife) {}
				public void internalFrameDeactivated(InternalFrameEvent ife) {}
				public void internalFrameClosing(InternalFrameEvent ife) {
					terminate = true;
				}
				public void internalFrameClosed(InternalFrameEvent ife) {}
				public void internalFrameActivated(InternalFrameEvent ife) {}
			});
			
			jif.pack();
			
		    try {
		    	jif.setSelected(true);
		    } catch (Exception exc) {
		    	//Nothing to do
		    }
		    
		    jdp.add(jif);
			
			//Calulation the position of JInternalFrame to appear in top right corner of application
			int x = (int)(jdp.getWidth() - jif.getWidth());
			int y = 0;
			
			jif.setLocation(x,y);
			jif.setVisible(true);
		}
	}
}