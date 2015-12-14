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


public class RMIClientGUI_ConnectionMonitor extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	private RMIClientGUI_Debugger rmiDebug;
	
	private JSplitPane jsptMain;
	private JPanel jpCenter;
	private JScrollPane jsp;
	
	private JButton jbtnRefreshAll;
	private JButton jbtnRefresh;
	private JProgressBar jpb;
	
	private Object [][] serverStatus;
	private Vector vSelectedRec = new Vector();
	
	
	public RMIClientGUI_ConnectionMonitor(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		
		//setBackground(Color.darkGray);
		setLayout(new BorderLayout());
		
		//------ Initialize the GUI ------
		initGUI();
		
		//------ Load all the server automatically ------
		Thread t = new Thread() {
			public void run() {
				refreshAllServer();
			}
		};
		t.start();
	}
	
	
	public void initGUI() {
			//====== Setup the East Panel ======
			JPanel jpEast = new JPanel();
			jpEast.setLayout(new GridLayout(10,1,5,5)); //10 Row, 1 Column
			
			/*************************************************
			 * A bug #4820659 prevents the user from using 
			 * the mouse to close dialogs decorated by the 
			 * Java look and feel. (Not solved until JDK 1.5
			 ************************************************/
			JDialog.setDefaultLookAndFeelDecorated(true);
			JToolBar jtb = new JToolBar("Connection Monitor");
			jtb.add(jpEast);
			
			JScrollPane jspOption = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jspOption.setPreferredSize(new Dimension(70,200));
			jspOption.getViewport().add(jtb);
			
				JButton jbtnHelp = new JButton("Help Menu");
				jbtnRefreshAll = new JButton("Connect All");
				jbtnRefresh = new JButton("Re-Connect");
				JButton jbtnRestartRMI = new JButton("Restart RMI");
				JButton jbtnShutdownRMI = new JButton("Shutdown RMI");
				JButton jbtnClearDebug = new JButton("Clear Debug");
				JButton jbtnReserved = new JButton("Reserved");
				
				//jbtnRestartRMI.setEnabled(false);
				jbtnReserved.setEnabled(false);
				
				jbtnHelp.setToolTipText("Read the help menu on using file transfer");
				jbtnRefresh.setToolTipText("Refresh the connection status of the selected server(s)");
				jbtnRefreshAll.setToolTipText("Refresh the connection status of all the server(s)");
				jbtnRestartRMI.setToolTipText("Restart the RMI Service on the selected server(s)");
				jbtnShutdownRMI.setToolTipText("Shutdown the RMI Service on the selected server(s)");
				jbtnClearDebug.setToolTipText("Clear the debug console");
				
				// 1. create HelpSet and HelpBroker objects
				HelpSet hs = RMIClientGUI_HelpSet.getHelpSet("doc/helpset/RMIAdminHelp_ConnectionMonitor.hs");
				final HelpBroker hb = hs.createHelpBroker();
				
				// 2. handle events
				jbtnHelp.addActionListener(new CSH.DisplayHelpFromSource(hb));
				
				jbtnRefresh.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (vSelectedRec.size() > 0) {
							jbtnRefresh.setEnabled(false);
							refreshServer();
						}
					}
				});
				
				jbtnRefreshAll.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jpCenter.removeAll();
						jpCenter.setLayout(new BorderLayout());
						jpCenter.add(jpb, BorderLayout.SOUTH);
						jpCenter.updateUI();
						jpCenter.revalidate();
						
						jbtnRefreshAll.setEnabled(false);
						refreshAllServer();
					}
				});
				
				jbtnRestartRMI.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (vSelectedRec.size() > 0) {
							boolean confirm = warning("Are you sure?\n" + 
														"All the running process invoked by RMIAdmin to \n" + 
														"to this remote server will be terminated.");
							
							if(confirm == true) {
								restartRMI();
							}
						}
					}
				});
				
				jbtnShutdownRMI.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (vSelectedRec.size() > 0) {
							boolean confirm = warning("Are you sure?\n" + 
														"You will not be able to connect this remote server \n" + 
														"via RMIAdmin anymore.");
							
							if (confirm == true) {
								shutdownRMI();
							}
						}
					}
				});
				
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
			jpEast.add(jbtnRefreshAll);
			jpEast.add(jbtnRefresh);
			jpEast.add(jbtnRestartRMI);
			jpEast.add(jbtnShutdownRMI);
			jpEast.add(jbtnClearDebug);
			//jpEast.add(jbtnReserved);
			//==================================
			
			//====== Setup the Center Panel ======
			jpCenter = new JPanel();
			jpCenter.setLayout(new BorderLayout());
			jpCenter.setPreferredSize(new Dimension(560,500));
			
				jsp = new JScrollPane();
			
				//--- Initialize the JProgressBar ---
				jpb = new JProgressBar();
				jpb.setValue(0);
				jpb.setStringPainted(true);
				jpb.setBorderPainted(true);
				jpb.setVisible(false);
				//-----------------------------------
			
			jpCenter.add(jsp, BorderLayout.CENTER);
			jpCenter.add(jpb, BorderLayout.SOUTH);
			
			jpCenter.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Link Status"),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
			//====================================
			
			//====== Combine the Center & East Panel ======
			jsptMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			jsptMain.setContinuousLayout(true);
			jsptMain.setOneTouchExpandable(true);
			jsptMain.add(jpCenter);
			jsptMain.add(jspOption);
			//=============================================
			
			//====== Setup the South Panel ======
			JPanel jpSouth = new JPanel();
			jpSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
			jpSouth.add(new JLabel("")); //add nothing, just retain this feature
			//===================================
		
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
	
	
	private void refreshAllServer() {
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
			
			//====== Initialize the Object Array to store the Server Status ======
			serverStatus = new Object [dataRow][6];
			//====================================================================
			
			//=== Initilize the max value of JProgressBar ===
			jpb.setMaximum(dataRow);
			//===============================================
			
			String serverName = "";
			String serverIP = "";
			String serverPort = "";
			
			rmiDebug.setDebugText("Connection test invoked.");
			rmiDebug.setDebugText("=============================================");
			
			//Preload the JTable
			displayStatus();
			
			for (int i=0; i<data.length; i++) {
				serverName = data [i][0] + "";
				serverIP = data [i][1] + "";
				serverPort = data [i][2] + "";
				
				//Preset the values of every server
				serverStatus [i][0] = data [i][0];
				serverStatus [i][1] = data [i][1];
				serverStatus [i][2] = data [i][2];
				serverStatus [i][3] = data [i][3];
				
				rmiDebug.setDebugText("Test connection with " + serverName + 
										" on IP " + serverIP + 
										" @ Port " + serverPort + " ...");
				
				connectionThread ct;
				ct = new connectionThread(this, i, serverName, serverIP, serverPort);
				ct.start();
			}
			
			//Make sure the JButtons are re-enabled after test complete
			jbtnRefreshAll.setEnabled(true);
			jbtnRefresh.setEnabled(true);
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	private void refreshServer() {
		try {
			if (vSelectedRec.size() > 0) {
				//=== Initilize the max value of JProgressBar ===
				jpb.setMaximum(vSelectedRec.size());
				//===============================================
			}
			
			for (int i=0; i<vSelectedRec.size(); i++) {
				String selectedServer = vSelectedRec.elementAt(i) + "";
				
				String serverName = "";
				String serverIP = "";
				String serverPort = "";
				int id = 0;
				
				for (int j=0; j<serverStatus.length; j++) {
					if ((serverStatus [j][0]+"").equals(selectedServer)) {
						serverName = serverStatus [j][0] + "";
						serverIP = serverStatus [j][1] + "";
						serverPort = serverStatus [j][2] + "";
						id = j;
						
						break;
					}
				}
				
				rmiDebug.setDebugText("Test connection with " + serverName + 
										" on IP " + serverIP + 
										" @ Port " + serverPort + " ...");
				
				connectionThread ct;
				ct = new connectionThread(this, id, serverName, serverIP, serverPort);
				ct.start();
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	private void restartRMI() {
		try {
			if (vSelectedRec.size() > 0) {
				//=== Initilize the max value of JProgressBar ===
				jpb.setMaximum(vSelectedRec.size());
				//===============================================
			}
			
			for (int i=0; i<vSelectedRec.size(); i++) {
				String selectedServer = vSelectedRec.elementAt(i) + "";
				
				String serverName = "";
				String serverIP = "";
				String serverPort = "";
				int id = 0;
				
				for (int j=0; j<serverStatus.length; j++) {
					if ((serverStatus [j][0]+"").equals(selectedServer)) {
						serverName = serverStatus [j][0] + "";
						serverIP = serverStatus [j][1] + "";
						serverPort = serverStatus [j][2] + "";
						id = j;
						
						break;
					}
				}
				
				rmiDebug.setDebugText("Restarting RMI - " + serverName + 
										" on IP " + serverIP + 
										" @ Port " + serverPort + " ...");
				
				
				//====== Put the implementaion here ======
				RMIClient_ConnectionMonitor rmi_CM;
				rmi_CM = new RMIClient_ConnectionMonitor(serverIP, serverPort, this);
				boolean doneRestart = rmi_CM.restartRMI();
				rmiDebug.setDebugText("Status: " + doneRestart);
				//========================================
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	private void shutdownRMI() {
		try {
			if (vSelectedRec.size() > 0) {
				//=== Initilize the max value of JProgressBar ===
				jpb.setMaximum(vSelectedRec.size());
				//===============================================
			}
			
			for (int i=0; i<vSelectedRec.size(); i++) {
				String selectedServer = vSelectedRec.elementAt(i) + "";
				
				String serverName = "";
				String serverIP = "";
				String serverPort = "";
				int id = 0;
				
				for (int j=0; j<serverStatus.length; j++) {
					if ((serverStatus [j][0]+"").equals(selectedServer)) {
						serverName = serverStatus [j][0] + "";
						serverIP = serverStatus [j][1] + "";
						serverPort = serverStatus [j][2] + "";
						id = j;
						
						break;
					}
				}
				
				rmiDebug.setDebugText("Shutdown RMI - " + serverName + 
										" on IP " + serverIP + 
										" @ Port " + serverPort + " ...");
				
				
				//====== Put the implementaion here ======
				RMIClient_ConnectionMonitor rmi_CM;
				rmi_CM = new RMIClient_ConnectionMonitor(serverIP, serverPort, this);
				boolean doneShutdown = rmi_CM.shutdownRMI();
				rmiDebug.setDebugText("Status: " + doneShutdown);
				//========================================
				
				//Update the server status by attempt to re-connect its RMI service
				refreshServer();
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	private void displayStatus() {
		final int dataRow = serverStatus.length;
		final int dataCol = 6;
		
		try {
			//Setup the Column Names
			final String [] colNames = new String [dataCol];
			colNames [0] = "Hostname";
			colNames [1] = "IP Address";
			colNames [2] = "Port No.";
			colNames [3] = "OS Type";
			colNames [4] = "Connection";
			colNames [5] = "Details";
			
			//====== Convert the 2-dimensonal Object array into GUI Spreadsheet ======
			//Create the Data Model
			final TableModel dataModel = new AbstractTableModel() {
				public int getColumnCount() { return dataCol; }
				public int getRowCount() { return dataRow;}
				public Object getValueAt(int row, int col) {
					return serverStatus [row][col];
				}
				public boolean isCellEditable(int row, int col) {
				   	return false;
				}
				public String getColumnName(int col) {
					return colNames [col];
				}
				public Class getColumnClass(int col) {
					if (col == 4) {
						return ImageIcon.class;
					} else {
						return String.class;
					}
				}
			};
			
			//Set the sorter for this table
			RMIClientGUI_TableSorter sorter = new RMIClientGUI_TableSorter(dataModel);
			final JTable jtb = new JTable(sorter) {
	    		//Implement table cell tool tips.
	    		public String getToolTipText(MouseEvent e) {
					String tip = null;
					java.awt.Point p = e.getPoint();
					int rowIndex = rowAtPoint(p);
					int colIndex = columnAtPoint(p);
					int realColumnIndex = convertColumnIndexToModel(colIndex);
					TableModel model = getModel();
					
					tip = getValueAt(rowIndex, colIndex) + "";
					
					//Replace the tip when it is an icon image
					if (tip.indexOf("/")>=0 && tip.indexOf(".gif")>=0) {
						if (tip.indexOf("up") >= 0) {
							tip = "Up";
						} else if (tip.indexOf("down") >= 0) {
							tip = "Down";
						} else if (tip.indexOf("test_link") >= 0) {
							tip = "Connecting";
						}
					}
					
					tip = tip.replaceAll("\n","<br>");
					tip = "<html>" + tip + "</html>";
					
					return tip;
				}
			};
			sorter.setTableHeader(jtb.getTableHeader());
			
			jtb.setShowVerticalLines(true);
			jtb.setShowHorizontalLines(true);
			jtb.setIntercellSpacing(new Dimension(1,1));
	    	jtb.setRowHeight(30);
			
			vSelectedRec = new Vector();
			ListSelectionModel LSM = jtb.getSelectionModel();
			LSM.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			LSM.addListSelectionListener(new ListSelectionListener() {
			    public void valueChanged(ListSelectionEvent e) {
			   		//Ignore extra messages.
			       	if (e.getValueIsAdjusting()) return;
			       	
			       	ListSelectionModel lsm = (ListSelectionModel)e.getSource();
					
					String output = "";
					vSelectedRec.removeAllElements();
					if (lsm.isSelectionEmpty()) {
						output += "No Selected Index";
						vSelectedRec.removeAllElements();
					} else {
						// Find out which indexes are selected.
						int minIndex = lsm.getMinSelectionIndex();
						int maxIndex = lsm.getMaxSelectionIndex();
						vSelectedRec.removeAllElements();
						for (int i = minIndex; i <= maxIndex; i++) {
							if (lsm.isSelectedIndex(i)) {
								int colFileName = 0;
								for (int j=0; j<jtb.getColumnCount(); j++) {
									if (jtb.getColumnName(j).equals("ID") == true) {
				               			colFileName = j;
				               			break;
				               		}
				               	}
				               	
				               	//System.out.println(jtb.getValueAt(i, colFileName)); //Debugger
				               	output += " " + jtb.getValueAt(i,colFileName) + "";
				               	vSelectedRec.addElement(jtb.getValueAt(i,colFileName) + "");
							}
						}
					}
					 
					//System.out.println("Selected Row = " + output); //Debugger
				}
			});
			
			jsp = new JScrollPane(jtb);
			
			jpCenter.removeAll();
			jpCenter.setLayout(new BorderLayout());
	    	jpCenter.add(jsp, BorderLayout.CENTER);
	    	jpCenter.add(jpb, BorderLayout.SOUTH);
	    	jpCenter.updateUI();
	    	jpCenter.revalidate();
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	public class connectionThread extends Thread {
		RMIClientGUI_ConnectionMonitor rmiGUI_CM;
		
		int id = 0;
		String serverName = "";
		String serverIP = "";
		String serverPort = "";
		
		public connectionThread (RMIClientGUI_ConnectionMonitor rmiGUI_CM, int id,
								String serverName, String serverIP, String serverPort) {
			
			this.rmiGUI_CM = rmiGUI_CM;
			
			this.id = id;
			this.serverName = serverName;
			this.serverIP = serverIP;
			this.serverPort = serverPort;
		}
		
		public void run() {
			try {
				RMIClient_ConnectionMonitor rmi_CM;
				rmi_CM = new RMIClient_ConnectionMonitor(serverIP, serverPort, rmiGUI_CM);
				
				//Set the link icon to a testing state, such that user know that the server is being tested
				serverStatus [id][4] = new ImageIcon(this.getClass().getResource("/image/test_link" + ".gif"));
				serverStatus [id][5] = "Connecting ...";
				
				//Update the JTable
	    		jpCenter.updateUI();
	    		jpCenter.revalidate();
	    	
				Object [] obj = rmi_CM.connect();
				String isConnected = obj [0] + "";
				String error = obj [1] + "";
				
				//System.out.println(serverName + " alive: " + isConnected + ", error: " + error); //Debugger
				rmiDebug.setDebugText(serverName + " alive: " + isConnected + ", error: " + error); 
				
				//Set the link icon depends on the status
				if (isConnected.equals("true")) {
					serverStatus [id][4] = new ImageIcon(this.getClass().getResource("/image/up" + ".gif"));
				} else {
					serverStatus [id][4] = new ImageIcon(this.getClass().getResource("/image/down" + ".gif"));
				}
				
				//Set the connection status and error message (if any) of every server
				serverStatus [id][5] = error;
				
				//Update the JTable
	    		jpCenter.updateUI();
	    		jpCenter.revalidate();
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
				
			} finally {
				//Update the JProgressBar
				jpb.setVisible(true);
				jpb.setValue(jpb.getValue() + 1);
				
				//Test if the serverStatus are all returned
				if (jpb.getValue() == jpb.getMaximum()) {
					//displayStatus();
					jbtnRefresh.setEnabled(true);
					jbtnRefreshAll.setEnabled(true);
					jpb.setValue(0);
					jpb.setVisible(false);
				}
			}
		}
	}
}