package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;

import java.net.*;
import javax.help.*;
import javax.help.search.*;


public class RMIClientGUI_FileTransfer extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	
	private JScrollPane jspFile;
	private JSplitPane jsptFile;
	private JSplitPane jsptMain;
	
	public int serverID = 0;
	public String serverHostname = "";
	public String serverIP = "";
	public String serverPort = "";
	public String serverOS = "";
	public String serverShell = "";
	public String serverDesc = "";
	
	private RMIClientGUI_TransferLocalView jpTransferLocalView;
	private RMIClientGUI_TransferRemoteView jpTransferRemoteView;
	
	private JTextField jtfServer;
	private JLabel jlbServer;
	public JTextArea jtaOutput;
	
	public RMIClientGUI_Debugger rmiDebug;
	
	
	public RMIClientGUI_FileTransfer(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		jlbServer = new JLabel("<html><font color='#333333'>No Server Selected, " + 
								"press the Select Server button to choose the target." +
								"</font></html>");
		
		//setBackground(Color.darkGray);
		setLayout(new BorderLayout());
		
		//------ Initialize the GUI ------
		initGUI();
	}
	
	
	private void initGUI() {
			//====== Setup the North Panel ======
			JPanel jpNorth = new JPanel();
			jpNorth.setLayout(new BorderLayout());
			
				JPanel jpNorth1 = new JPanel();
				jpNorth1.setLayout(new BorderLayout());
					
					jtfServer = new JTextField("No Server Selected");
					jtfServer.setEditable(false);
					jtfServer.setBackground(Color.BLACK);
					jtfServer.setForeground(Color.YELLOW);
					jtfServer.addMouseListener(new RMIClientGUI_MouseListener());
				
				jpNorth1.add(jtfServer, BorderLayout.CENTER);
				
				JButton jbtnSelectServer = new JButton("Select Server");
				
				jbtnSelectServer.setToolTipText("Open server list to select remote host");
				jbtnSelectServer.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						selectServer();
					}
				});
				
			jpNorth.add(jpNorth1, BorderLayout.CENTER);
			jpNorth.add(jbtnSelectServer, BorderLayout.EAST);
			//===================================
			
			//====== Setup the East Panel ======
			JPanel jpEast = new JPanel();
			jpEast.setLayout(new GridLayout(10,1,5,5)); //10 Row, 1 Column
			
			/*************************************************
			 * A bug #4820659 prevents the user from using 
			 * the mouse to close dialogs decorated by the 
			 * Java look and feel. (Not solved until JDK 1.5)
			 ************************************************/
			JDialog.setDefaultLookAndFeelDecorated(true);	
			final JToolBar jtb = new JToolBar("File Transfer");
			jtb.add(jpEast);
			
			JScrollPane jspOption = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jspOption.setPreferredSize(new Dimension(70,200));
			jspOption.getViewport().add(jtb);
			
				JButton jbtnFileTransfer = new JButton("Reload Server");
				JButton jbtnHelp = new JButton("Help Menu");
				JButton jbtnClearDebug = new JButton("Clear Debug");
				JButton jbtnReserved = new JButton("Reserved");
				jbtnReserved.setEnabled(false);
				
				jbtnFileTransfer.setToolTipText("Reload the browser object for remote file system");
				jbtnHelp.setToolTipText("Read the help menu on using file transfer");
				jbtnClearDebug.setToolTipText("Clear the debug console");
				jbtnReserved.setToolTipText("Reserved for future use");
				
				jbtnFileTransfer.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (serverShell.equals("")) {
							warning("Please select Server first!");
						} else {
							openFileSystem();
						}
					}
				});
				
				// 1. create HelpSet and HelpBroker objects
				HelpSet hs = RMIClientGUI_HelpSet.getHelpSet("doc/helpset/RMIAdminHelp_FileTransfer.hs");
				final HelpBroker hb = hs.createHelpBroker();
				
				// 2. handle events
				jbtnHelp.addActionListener(new CSH.DisplayHelpFromSource(hb));
				
				jbtnClearDebug.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiDebug.clearText();
						rmiDebug.setDebugText("Debug Console cleared.");
					}
				});
				
			jspOption.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Option Panel"),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
			
			jpEast.add(jbtnHelp);
			jpEast.add(jbtnFileTransfer);
			jpEast.add(jbtnClearDebug);
			//jpEast.add(jbtnReserved);
			//==================================
			
			//====== Setup the Center Panel ======
			JPanel jpCenter = new JPanel();
			jpCenter.setLayout(new BorderLayout());
			jpCenter.setPreferredSize(new Dimension(560,500));
			
				jpTransferLocalView = new RMIClientGUI_TransferLocalView(jdp, rmiDebug, this);
				jpTransferRemoteView = new RMIClientGUI_TransferRemoteView(jdp, rmiDebug, this);
			
				//====== Setup JSplitPane ======
				jsptFile = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
				jsptFile.setContinuousLayout(true);
				jsptFile.setOneTouchExpandable(true);
				jsptFile.add(jpTransferLocalView);
				jsptFile.add(jpTransferRemoteView);
				//==============================
				
				jspFile = new JScrollPane(jsptFile,
									JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
			jpCenter.add(jsptFile, BorderLayout.CENTER);
			jpCenter.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("File Transfer"),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
			//====================================
			
			//====== Combine the Center & Option Panels ======
			jsptMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			jsptMain.setContinuousLayout(true);
			jsptMain.setOneTouchExpandable(true);
			jsptMain.add(jpCenter);
			jsptMain.add(jspOption);
			//================================================
			
			//====== Setup the South Panel ======
			JPanel jpSouth = new JPanel();
			jpSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
			jpSouth.add(new JLabel("")); //add nothing, just retain this feature
			//===================================
			
		add(jpNorth, BorderLayout.NORTH);
		add(jsptMain, BorderLayout.CENTER);
		add(jpSouth, BorderLayout.SOUTH);
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
		
		//Empty the FileBrowser and FileDetail view when remote server not available
		if (opt.indexOf("java.net.ConnectException") >= 0) {
			jpTransferLocalView.clearView();
			jpTransferRemoteView.clearView();
		}
		
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
	
	
	private void selectServer() {
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
				
				jlbServer.setText("<html><font color='#333333'>Selected Server: " + 
									serverHostname + " at IP " + serverIP + " on Port " + serverPort + 
									"</font></html>");
				jtfServer.setText("Selected Server: " + 
									serverHostname + " at IP " + serverIP + " on Port " + serverPort + 
									"");
				
				//Auto load the local and remote file system
				openFileSystem();
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	/**********************************************************************
	 * These are the 3 core methods which implements
	 * the idea to maintain a consistent file object 
	 * state and operations among 2 different views:
	 *
	 * 1> TransferRemoteView
	 * 2> TransferLocalView
	 *
	 * With this design, the FileTransfer serves as 
	 * the entry point whenever we load the Local and 
	 * Remote File System.
	 *
	 * Such that whenever there is a change detected 
	 * by the TransferLocalView, it can also notify an 
	 * update to the TransferRemoteView via:
	 * -> clearAllViews()
	 * -> getRemoteViewPath()
	 * -> refreshRemoteView()
	 *
	 * While if an update is made in the TransferRemoteView, 
	 * TransferRemoteView will be able to notify the 
	 * changes to TransferLocalView via:
	 * -> clearAllViews()
	 * -> getLocalViewPath()
	 * -> refreshFileTreeView()
	 * 
	 * See how we control the syncronization of these 
	 * two different views.
	 *
	 * 1) File System loaded into TransferLocalView <-------------------|
	 *                                                                  |
	 *                     |                                            |
	 *                     V                                            |
	 *                                                                  |
	 * 2) File System loaded into TransferRemoteView <--------------|   |
	 *                                                              |   |
	 *                     |                                        |   |
	 *                     V                                        |   |
	 *                                                              |   |
	 * 3) TransferLocalView UPLOAD FILE to TransferRemoteView ----->|   |
	 *                                                                  |
	 *                     |                                            |
	 *                     V                                            |
	 *                                                                  |
	 * 4) TransferRemoteView DOWNLOAD FILE from TransferLocalView ----->|
	 *
	 ************************************************************************/
	private void openFileSystem() {
		jpTransferLocalView.openFileSystem();
		jpTransferRemoteView.openFileSystem();
	}
	
	
	public void clearAllViews() {
		jpTransferLocalView.clearView();
		jpTransferRemoteView.clearView();
	}
	
	
	public String getRemoteViewPath() {
		return jpTransferRemoteView.getRemotePath();
	}
	
	
	public String getLocalViewPath() {
		return jpTransferLocalView.getLocalPath();
	}
	
	
	public void refreshRemoteView() {
		jpTransferRemoteView.refreshRemoteView();
	}
	
	
	public void refreshLocalView() {
		jpTransferLocalView.refreshLocalView();
	}
}