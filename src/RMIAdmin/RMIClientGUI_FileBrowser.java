package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.text.*;
import javax.swing.table.*;

import java.io.*;
import java.util.*;

import java.net.*;
import javax.help.*;
import javax.help.search.*;


public class RMIClientGUI_FileBrowser extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	
	private JScrollPane jspFile;
	private JSplitPane jsptFile;
	private JSplitPane jsptMain;
	private JTextField jtfRemotePath;
	
	public int serverID = 0;
	public String serverHostname = "";
	public String serverIP = "";
	public String serverPort = "";
	public String serverOS = "";
	public String serverShell = "";
	public String serverDesc = "";
	
	private RMIClientGUI_FileTreeView jpFileTreeView;
	private RMIClientGUI_FileTableView jpFileTableView;
	
	private JTextField jtfServer;
	private JLabel jlbServer;
	public JTextArea jtaOutput;
	
	public RMIClientGUI_Debugger rmiDebug;
	
	
	public RMIClientGUI_FileBrowser(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
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
				
				//jpNorth1.add(jlbServer, BorderLayout.WEST);
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
			JToolBar jtb = new JToolBar("File Browser");
			jtb.add(jpEast);
			
			JScrollPane jspOption = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jspOption.setPreferredSize(new Dimension(70,200));
			jspOption.getViewport().add(jtb);
				
				JButton jbtnHelp = new JButton("Help Menu");
				JButton jbtnFileBrowser = new JButton("Reload");
				JButton jbtnClearDebug = new JButton("Clear Debug");
				JButton jbtnReserved = new JButton("Reserved");
				jbtnReserved.setEnabled(false);
				
				jbtnHelp.setToolTipText("Read the help menu on using file transfer");
				jbtnFileBrowser.setToolTipText("Reload the browser object for remote file system");
				jbtnReserved.setToolTipText("Reserved for future use");
				jbtnClearDebug.setToolTipText("Clear the debug console");
				
				jbtnFileBrowser.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (serverShell.equals("")) {
							warning("Please select Server first!");
						} else {
							openFileSystem();
						}
					}
				});
				
				// 1. create HelpSet and HelpBroker objects
				HelpSet hs = RMIClientGUI_HelpSet.getHelpSet("doc/helpset/RMIAdminHelp_FileBrowser.hs");
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
			jpEast.add(jbtnFileBrowser);
			jpEast.add(jbtnClearDebug);
			//jpEast.add(jbtnReserved);
			//==================================
			
			//====== Setup the Center Panel ======
			JPanel jpCenter = new JPanel();
			jpCenter.setLayout(new BorderLayout());
			jpCenter.setPreferredSize(new Dimension(560,500));
			
				JPanel jpCenterTop = new JPanel();
				jpCenterTop.setLayout(new BorderLayout());
			
					JLabel jlbRemotePath = new JLabel("Remote Path: ");
					
					jtfRemotePath = new JTextField(30);
					jtfRemotePath.setToolTipText("Type your target location here.");
					jtfRemotePath.addMouseListener(new RMIClientGUI_MouseListener());
					
					JButton jbtnGo = new JButton("Go");
					jbtnGo.setToolTipText("Click this button to launch the remote path.");
					jbtnGo.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							if (jtfRemotePath.getText().length() > 0) {
								setFileTreeViewLocation(jtfRemotePath.getText());
							}
						}
					});
					
					JButton jbtnUp = new JButton("Up");
					jbtnUp.setToolTipText("Click this button to back to parent path.");
					jbtnUp.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							String path = jtfRemotePath.getText();
							//System.out.println(path); //Debugger
							
							if (path.lastIndexOf("/")==path.length()-1 || path.lastIndexOf("\\")==path.length()-1) {
								path = path.substring(0, path.length()-1);
							}
							//System.out.println(path); //Debugger
							
							if (path.indexOf("/")>=0) {
								path = path.substring(0, path.lastIndexOf("/"));
								setFileTreeViewLocation(path);
							}
							
							if (path.indexOf("\\")>=0) {
								path = path.substring(0, path.lastIndexOf("\\"));
								setFileTreeViewLocation(path);
							}
							//System.out.println(path); //Debugger
							
							jtfRemotePath.setText(path);
						}
					});
					
					JPanel jpCenterTop1 = new JPanel();
					jpCenterTop1.setLayout(new BorderLayout());
					jpCenterTop1.add(jbtnGo, BorderLayout.WEST);
					jpCenterTop1.add(jbtnUp, BorderLayout.EAST);
					
				jpCenterTop.add(jlbRemotePath, BorderLayout.WEST);
				jpCenterTop.add(jtfRemotePath, BorderLayout.CENTER);
				jpCenterTop.add(jpCenterTop1, BorderLayout.EAST);
				jpCenterTop.setPreferredSize(new Dimension(500,25));
				
				jpFileTreeView = new RMIClientGUI_FileTreeView(jdp, rmiDebug, this);
				jpFileTableView = new RMIClientGUI_FileTableView(jdp, rmiDebug, this);
			
				//====== Setup JSplitPane ======
				jsptFile = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
				jsptFile.setContinuousLayout(true);
				jsptFile.setOneTouchExpandable(true);
				jsptFile.add(jpFileTreeView);
				jsptFile.add(jpFileTableView);
				//==============================
				
				jspFile = new JScrollPane(jsptFile,
									JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
			jpCenter.add(jpCenterTop, BorderLayout.NORTH);
			jpCenter.add(jsptFile, BorderLayout.CENTER);
			jpCenter.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("File Browser"),
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
				
				//Auto load the remote file system
				openFileSystem();
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	/****************************************************
	 * These are the 3 core methods which implements
	 * the idea to maintain a consistent file object 
	 * state and operations among 2 different views:
	 *
	 * 1> FileTreeView
	 * 2> FileTableView
	 *
	 * With this design, the FileBrowser serves as 
	 * the entry point whenever we load the remote 
	 * File System.
	 *
	 * Such that whenever there is a change detected 
	 * by the FileTreeView, it will also notify an 
	 * update to the FileTableView via:
	 * -> refreshFileTableView()
	 * -> clearAllViews()
	 *
	 * While if an update is made in the FileTableView, 
	 * FileTableView will notify the changes to 
	 * FileTreeView via:
	 * -> refreshFileTreeView()
	 * -> clearAllViews()
	 * 
	 * FileTreeView would in turn produce a notify 
	 * back to FileTableView after its update is done.
	 * Therefore FileTableView is also updated at last.
	 *
	 * See how we control the syncronization of these 
	 * two different views.
	 *
	 * 1) File System loaded into FileTreeView
	 * 
	 *                     |
	 *                     V                         
	 * 
	 * 2) FileTreeView loaded the FileTableView <----|
	 *                                               |
	 *                     |                         |
	 *                     V                         |
	 *                                               |
	 * 3) any update over the FileTreeView --------->|
	 *                                               |
	 *                     |                         |
	 *                     V                         |
	 *                                               |
	 * 4) any update over the FileTableView -------->|
	 *
	 ***************************************************/
	private void openFileSystem() {
		clearAllViews();
		jpFileTreeView.openFileSystem();
	}
	
	
	public void refreshFileTreeView() {
		jpFileTreeView.refreshFileTreeView();
	}
	
	
	public void refreshFileTreeView(String newLocation, String filename) {
		jpFileTreeView.refreshFileTreeView(newLocation, filename);
	}
	
	
	public void refreshFileTableView(String location, String [][] fileList) {
		jpFileTableView.refreshFileTableView(location, fileList);
	}
	
	
	public void setFileTreeViewLocation(String path) {
		jpFileTreeView.setLocation(path);
	}
	
	
	public void clearAllViews() {
		jpFileTreeView.clearView();
		jpFileTableView.clearView();
	}
	
	
	public void setFilesToBePasted_FileTreeView(Vector vSrcLocation, String filePasteType) {
		jpFileTreeView.setFilesToBePasted(vSrcLocation, filePasteType);
	}
	
	
	public void setFilesToBePasted_FileTableView(String srcLocation, String filePasteType) {
		jpFileTableView.setFilesToBePasted(srcLocation, filePasteType);
	}
	
	
	public void clearFilesToBePasted_FileTreeView() {
		jpFileTreeView.clearFilesToBePasted();
	}
	
	
	public void clearFilesToBePasted_FileTableView() {
		jpFileTableView.clearFilesToBePasted();
	}
	
	
	public void setRemotePath(String path) {
		jtfRemotePath.setText(path);
	}
}