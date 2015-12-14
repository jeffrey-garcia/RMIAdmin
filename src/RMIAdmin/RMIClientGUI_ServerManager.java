package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.util.*;

import java.net.*;
import javax.help.*;
import javax.help.search.*;


public class RMIClientGUI_ServerManager extends JPanel {
	private JDesktopPane jdp;
	private RMIClientGUI_Debugger rmiDebug;
	private RMIClientGUI rmiGUI;
	
	private String rmiServerIP = "";
	private String rmiServerPort = "";
	
	
	public RMIClientGUI_ServerManager(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
		this.rmiDebug = rmiDebug;
		this.rmiGUI = (RMIClientGUI)rmiDebug.getTopLevelAncestor();
		this.jdp = jdp;
		//setBackground(Color.darkGray);
		setLayout(new BorderLayout());
		
		initGUI();
	}
	
	
	public void initGUI() {
		loadServer();
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
	
	
	private void createServer(final JInternalFrame jifServerList) {
		//====== Add a JInternalFrame to JDesktopPane ======
		final JInternalFrame jif = new JInternalFrame();
		jif.setTitle("Create new Server");
    	jif.setResizable(true);
    	jif.setIconifiable(true);
    	jif.setMaximizable(false);
    	jif.setClosable(true);
    	jif.getContentPane().setLayout(new BorderLayout());
    	
	    	JPanel jpNorth = new JPanel();
	    	jpNorth.setLayout(new GridLayout(5,2,5,5)); //5 Row, 2 Column
	    	
	    		JLabel jlbHostname = new JLabel(" Hostname:");
	    		JLabel jlbIPaddr = new JLabel(" IP Address:");
	    		JLabel jlbDaemonPort = new JLabel(" Daemon Port:");
	    		JLabel jlbOS = new JLabel(" Operating System: ");
	    		JLabel jlbShell = new JLabel("<html>&nbsp;Shell: <i>e.g. cmd.exe</i></html>");
	    		final JTextField jtfHostname = new JTextField(10);
	    		final JTextField jtfIPaddr = new JTextField(10);
	    		final JTextField jtfDaemonPort = new JTextField(10);
	    		final JTextField jtfOS = new JTextField(10);
	    		final JTextField jtfShell = new JTextField(10);
	    		
	    		jtfHostname.addMouseListener(new RMIClientGUI_MouseListener());
	    		jtfIPaddr.addMouseListener(new RMIClientGUI_MouseListener());
	    		jtfDaemonPort.addMouseListener(new RMIClientGUI_MouseListener());
	    		jtfOS.addMouseListener(new RMIClientGUI_MouseListener());
	    		jtfShell.addMouseListener(new RMIClientGUI_MouseListener());
	    		
	    	jpNorth.add(jlbHostname);
	    	jpNorth.add(jtfHostname);
	    	jpNorth.add(jlbIPaddr);
	    	jpNorth.add(jtfIPaddr);
	    	jpNorth.add(jlbDaemonPort);
	    	jpNorth.add(jtfDaemonPort);
	    	jpNorth.add(jlbOS);
	    	jpNorth.add(jtfOS);
	    	jpNorth.add(jlbShell);
	    	jpNorth.add(jtfShell);
    	
	    	JPanel jpCenter = new JPanel();
	    	jpCenter.setLayout(new BorderLayout());
	    	
	    		final JTextArea jtaDescription = new JTextArea();
				jtaDescription.setEditable(true);
				jtaDescription.addMouseListener(new RMIClientGUI_MouseListener());
				jtaDescription.setToolTipText("Description");
				
				JScrollPane jspDescription = new JScrollPane(jtaDescription,
									JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			jpCenter.add(jspDescription, BorderLayout.CENTER);
			jpCenter.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Description"),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
			
			JPanel jpSouth = new JPanel();
			jpSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
			
				JButton jbtnSave = new JButton("Save");
				jbtnSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (jtfHostname.getText().length()>0 &&
							jtfIPaddr.getText().length()>0 &&
							jtfDaemonPort.getText().length()>0 && 
							jtfOS.getText().length()>0 && 
							jtfShell.getText().length()>0 && 
							jtaDescription.getText().length()>0
						) {
							saveNewServer(jtfHostname.getText(), 
											jtfIPaddr.getText(),
											jtfDaemonPort.getText(), 
											jtfOS.getText(), 
											jtfShell.getText(), 
											jtaDescription.getText()
										);
							
							rmiDebug.setDebugText("Save new server setting");			
							jifServerList.setVisible(false);
							jifServerList.dispose();
							loadServer();
							
							jtfHostname.setText("");
							jtfIPaddr.setText("");
							jtfDaemonPort.setText("");
							jtfOS.setText("");
							jtfShell.setText("");
							jtaDescription.setText("");
							
						} else {
							if (jtfHostname.getText().length() == 0) {
								rmiGUI.alert("Missing hostname.");
								jtfHostname.requestFocus();
							} else if (jtfIPaddr.getText().length() == 0) {
								rmiGUI.alert("Missing IP Address.");
								jtfIPaddr.requestFocus();
							} else if (jtfDaemonPort.getText().length() == 0) {
								rmiGUI.alert("Missing Daemon Port.");
								jtfDaemonPort.requestFocus();
							} else if (jtfOS.getText().length() == 0) {
								rmiGUI.alert("Missing Operating System name.");
								jtfOS.requestFocus();
							} else if (jtfShell.getText().length() == 0) {
								rmiGUI.alert("Missing Shell command.");
								jtfShell.requestFocus();
							} else if (jtaDescription.getText().length() == 0) {
								rmiGUI.alert("Missing Description.");
								jtaDescription.requestFocus();
							}
						}
					}
				});
				
				JButton jbtnSaveClose = new JButton("Save & Close");
				jbtnSaveClose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (jtfHostname.getText().length()>0 &&
							jtfIPaddr.getText().length()>0 &&
							jtfDaemonPort.getText().length()>0 && 
							jtfOS.getText().length()>0 && 
							jtfShell.getText().length()>0 && 
							jtaDescription.getText().length()>0
						) {
							saveNewServer(jtfHostname.getText(), 
											jtfIPaddr.getText(),
											jtfDaemonPort.getText(), 
											jtfOS.getText(), 
											jtfShell.getText(), 
											jtaDescription.getText()
										);
							rmiDebug.setDebugText("Save new server setting & close");
							jifServerList.setVisible(false);
							jifServerList.dispose();
							jif.setVisible(false);
							jif.dispose();
							loadServer();
							
						} else {
							if (jtfHostname.getText().length() == 0) {
								rmiGUI.alert("Missing hostname.");
								jtfHostname.requestFocus();
							} else if (jtfIPaddr.getText().length() == 0) {
								rmiGUI.alert("Missing IP Address.");
								jtfIPaddr.requestFocus();
							} else if (jtfDaemonPort.getText().length() == 0) {
								rmiGUI.alert("Missing Daemon Port.");
								jtfDaemonPort.requestFocus();
							} else if (jtfOS.getText().length() == 0) {
								rmiGUI.alert("Missing Operating System name.");
								jtfOS.requestFocus();
							} else if (jtfShell.getText().length() == 0) {
								rmiGUI.alert("Missing Shell command.");
								jtfShell.requestFocus();
							} else if (jtaDescription.getText().length() == 0) {
								rmiGUI.alert("Missing Description.");
								jtaDescription.requestFocus();
							}
						}
					}
				});
				
				JButton jbtnReset = new JButton("Reset");
				jbtnReset.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jtfHostname.setText("");
						jtfIPaddr.setText("");
						jtfDaemonPort.setText("");
						jtfOS.setText("");
						jtfShell.setText("");
						jtaDescription.setText("");
						rmiDebug.setDebugText("Reset new server setting");
					}
				});
				
			jpSouth.add(jbtnSave);
			jpSouth.add(jbtnSaveClose);
			jpSouth.add(jbtnReset);
			
    	jif.getContentPane().add(jpNorth, BorderLayout.NORTH);
    	jif.getContentPane().add(jpCenter, BorderLayout.CENTER);
    	jif.getContentPane().add(jpSouth, BorderLayout.SOUTH);
    	
    	jif.setSize(300, 330);
    	
	    try {
	    	jif.setSelected(true);
	    } catch (Exception exc) {
	    	//Nothing to do
	    }
	    
	    jdp.add(jif);
	    
		int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
		int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
		jif.setLocation(x,y);
		jif.setVisible(true);
		//====================================================
	}
	
	
	private void loadServer() {
		new ServerList(this).start();
	}
	
	
	private void updateServer(final Object [][] data, final Vector vRec, final int dataCol, final JInternalFrame jifServerList) {
		String hostname = "";
		String ip = "";
		String port = "";
		String os = "";
		String shell = "";
		String desc = "";
		final int recNo = new Integer(vRec.elementAt(0)+"").intValue(); //Update only support one server at a time
		
		try {
			for (int i=0; i<vRec.size(); i++) {
				for (int j=0; j<dataCol; j++) {
					if (j==0) {hostname = data [Integer.parseInt(vRec.elementAt(i)+"")][j] + "";}
					if (j==1) {ip = data [Integer.parseInt(vRec.elementAt(i)+"")][j] + "";}
					if (j==2) {port = data [Integer.parseInt(vRec.elementAt(i)+"")][j] + "";}
					if (j==3) {os = data [Integer.parseInt(vRec.elementAt(i)+"")][j] + "";}
					if (j==4) {shell = data [Integer.parseInt(vRec.elementAt(i)+"")][j] + "";}
					if (j==5) {desc = data [Integer.parseInt(vRec.elementAt(i)+"")][j] + "";}
				}
			}
			
			//====== Add a JInternalFrame to JDesktopPane ======
			final JInternalFrame jif = new JInternalFrame();
			jif.setTitle("Update Server");
	    	jif.setResizable(true);
	    	jif.setIconifiable(true);
	    	jif.setMaximizable(false);
	    	jif.setClosable(true);
	    	jif.getContentPane().setLayout(new BorderLayout());
	    	
		    	JPanel jpNorth = new JPanel();
		    	jpNorth.setLayout(new GridLayout(5,2,5,5)); //5 Row, 2 Column
		    	
		    		JLabel jlbHostname = new JLabel(" Hostname:");
		    		JLabel jlbIPaddr = new JLabel(" IP Address:");
		    		JLabel jlbDaemonPort = new JLabel(" Daemon Port:");
		    		JLabel jlbOS = new JLabel(" Operating System: ");
		    		JLabel jlbShell = new JLabel("<html>&nbsp;Shell: <i>e.g. cmd.exe</i></html>");
		    		final JTextField jtfHostname = new JTextField(10);
		    		jtfHostname.setText(hostname);
		    		final JTextField jtfIPaddr = new JTextField(10);
		    		jtfIPaddr.setText(ip);
		    		final JTextField jtfDaemonPort = new JTextField(10);
		    		jtfDaemonPort.setText(port);
		    		final JTextField jtfOS = new JTextField(10);
		    		jtfOS.setText(os);
		    		final JTextField jtfShell = new JTextField(10);
		    		jtfShell.setText(shell);
		    		
		    		jtfHostname.addMouseListener(new RMIClientGUI_MouseListener());
		    		jtfIPaddr.addMouseListener(new RMIClientGUI_MouseListener());
		    		jtfDaemonPort.addMouseListener(new RMIClientGUI_MouseListener());
		    		jtfOS.addMouseListener(new RMIClientGUI_MouseListener());
		    		jtfShell.addMouseListener(new RMIClientGUI_MouseListener());
		    		
		    	jpNorth.add(jlbHostname);
		    	jpNorth.add(jtfHostname);
		    	jpNorth.add(jlbIPaddr);
		    	jpNorth.add(jtfIPaddr);
		    	jpNorth.add(jlbDaemonPort);
		    	jpNorth.add(jtfDaemonPort);
		    	jpNorth.add(jlbOS);
		    	jpNorth.add(jtfOS);
		    	jpNorth.add(jlbShell);
		    	jpNorth.add(jtfShell);
	    	
		    	JPanel jpCenter = new JPanel();
		    	jpCenter.setLayout(new BorderLayout());
		    	
		    		final JTextArea jtaDescription = new JTextArea();
		    		jtaDescription.setText(desc);
					jtaDescription.setEditable(true);
					jtaDescription.addMouseListener(new RMIClientGUI_MouseListener());
					jtaDescription.setToolTipText("Description");
					
					JScrollPane jspDescription = new JScrollPane(jtaDescription,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
				jpCenter.add(jspDescription, BorderLayout.CENTER);
				jpCenter.setBorder(
					BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Description"),
						BorderFactory.createEmptyBorder(5,5,5,5)
					)
				);
				
				JPanel jpSouth = new JPanel();
				jpSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
					
					JButton jbtnSaveClose = new JButton("Save & Close");
					jbtnSaveClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							if (jtfHostname.getText().length()>0 &&
								jtfIPaddr.getText().length()>0 &&
								jtfDaemonPort.getText().length()>0 && 
								jtfOS.getText().length()>0 && 
								jtfShell.getText().length()>0 && 
								jtaDescription.getText().length()>0
							) {
								saveModifiedServer(recNo, 
												jtfHostname.getText(), 
												jtfIPaddr.getText(),
												jtfDaemonPort.getText(), 
												jtfOS.getText(), 
												jtfShell.getText(), 
												jtaDescription.getText()
											);
								rmiDebug.setDebugText("Save new server setting & close");
								jifServerList.setVisible(false);
								jifServerList.dispose();
								jif.setVisible(false);
								jif.dispose();
								loadServer();
								
							} else {
								if (jtfHostname.getText().length() == 0) {
									rmiGUI.alert("Missing hostname.");
									jtfHostname.requestFocus();
								} else if (jtfIPaddr.getText().length() == 0) {
									rmiGUI.alert("Missing IP Address.");
									jtfIPaddr.requestFocus();
								} else if (jtfDaemonPort.getText().length() == 0) {
									rmiGUI.alert("Missing Daemon Port.");
									jtfDaemonPort.requestFocus();
								} else if (jtfOS.getText().length() == 0) {
									rmiGUI.alert("Missing Operating System name.");
									jtfOS.requestFocus();
								} else if (jtfShell.getText().length() == 0) {
									rmiGUI.alert("Missing Shell command.");
									jtfShell.requestFocus();
								} else if (jtaDescription.getText().length() == 0) {
									rmiGUI.alert("Missing Description.");
									jtaDescription.requestFocus();
								}
							}
						}
					});
					
					JButton jbtnReset = new JButton("Reset");
					jbtnReset.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							jif.setVisible(false);
							jif.dispose();
							updateServer(data, vRec, dataCol, jifServerList);
							rmiDebug.setDebugText("Reset new server setting");
						}
					});
					
				jpSouth.add(jbtnSaveClose);
				jpSouth.add(jbtnReset);
				
	    	jif.getContentPane().add(jpNorth, BorderLayout.NORTH);
	    	jif.getContentPane().add(jpCenter, BorderLayout.CENTER);
	    	jif.getContentPane().add(jpSouth, BorderLayout.SOUTH);
	    	
	    	jif.setSize(300, 330);
	    	
		    try {
		    	jif.setSelected(true);
		    } catch (Exception exc) {
		    	//Nothing to do
		    }
		    
		    jdp.add(jif);
		    
			int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
			int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
			jif.setLocation(x,y);
		    jif.setVisible(true);
			//====================================================
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void deleteServer(Vector vRec, final JInternalFrame jifServerList) {
		final int recNo = new Integer(vRec.elementAt(0)+"").intValue(); //Delete only support one server at a time
		boolean confirm;
		
		confirm = warning("Are you sure to delete this server?" + "\n" +
							"Click [OK] to delete, [Cancel] to quit.");
							
		if (confirm == true) {
			rmiDebug.setDebugText("Confirm delete server.");
			saveDeletedServer(recNo);
			jifServerList.setVisible(false);
			jifServerList.dispose();
			loadServer();
			
		} else {
			rmiDebug.setDebugText("Cancel delete server.");
		}
	}
	
	
	private void saveNewServer(String hostname, String ip, String port, String os, String shell, String desc) {
		RMIClient_XMLParser rmiXML = new RMIClient_XMLParser(rmiDebug);
		rmiXML.addRecord(hostname, ip, port, os, shell, desc);
	}
	
	
	private void saveModifiedServer(int recNo, String hostname, String ip, String port, String os, String shell, String desc) {
		RMIClient_XMLParser rmiXML = new RMIClient_XMLParser(rmiDebug);
		rmiXML.updateRecord(recNo, hostname, ip, port, os, shell, desc);
	}
	
	
	private void saveDeletedServer(int recNo) {
		RMIClient_XMLParser rmiXML = new RMIClient_XMLParser(rmiDebug);
		rmiXML.removeRecord(recNo);
	}
	
	
	public class ServerList extends Thread {
		private Vector vRec;
		private RMIClientGUI_ServerManager ptr;
		
		public ServerList(RMIClientGUI_ServerManager ptr) {
			this.ptr = ptr;
		}
		
		public void run() {
			try {
				RMIClient_XMLParser rmiXML = new RMIClient_XMLParser(rmiDebug);
				
				final Object [][] data = rmiXML.loadRecord();
				final int dataRow = rmiXML.getRow();
				final int dataCol = rmiXML.getCol();
				
				//=== Put a a hidden record ID in the data object ===
				final Object [][] newData = new Object [dataRow][dataCol+1];
				for (int i=0; i<dataRow; i++) {
					for (int j=0; j<dataCol+1; j++) {
						if (j==0) {
							newData [i][j] = new Integer(i);
						} else {
							newData [i][j] = data [i][j-1];
						}
					}
				}
				//===================================================
				
				//=== Setup the column names ===
				final String [] colNames = new String [dataCol+1];
				colNames [0] = "ID";
				colNames [1] = "Hostname";
				colNames [2] = "IP Address";
				colNames [3] = "Port No.";
				colNames [4] = "OS Type";
				colNames [5] = "Shell";
				colNames [6] = "Description";
				//==============================
				
				//==== Start converting data into java spreadsheet ====
					final TableModel dataModel = new AbstractTableModel() {
						public int getColumnCount() { return dataCol+1; }
						public int getRowCount() { return dataRow;}
						public Object getValueAt(int row, int col) {
							return newData [row][col];
						}
						public boolean isCellEditable(int row, int col) {
						   	return true;
						}
						public String getColumnName(int col) {
							return colNames [col];
						}
						public Class getColumnClass(int col) {
							if (col == 0) {
								return Integer.class;
							} else if (col == 3) {
								return Integer.class;
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
							tip = tip.replaceAll("\n","<br>");
							tip = "<html>" + tip + "</html>";
							
							return tip;
						}
					};
					sorter.setTableHeader(jtb.getTableHeader());
					
					//Set the width of specific column
					TableColumn column = null;
					for (int i=0; i<dataCol; i++) {
						column = jtb.getColumnModel().getColumn(i);
						if (i == 0) {
							column.setPreferredWidth(30);
						}
					}
					
					jtb.setIntercellSpacing(new Dimension(1,1));
	    			jtb.setRowHeight(20);
					vRec = new Vector();
					
					ListSelectionModel LSM = jtb.getSelectionModel();
					LSM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					LSM.addListSelectionListener(new ListSelectionListener() {
					    public void valueChanged(ListSelectionEvent e) {
				    		//Ignore extra messages.
				        	if (e.getValueIsAdjusting()) return;
				        
				        	ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				        	
				            String output = "";
				            vRec = new Vector();
				            if (lsm.isSelectionEmpty()) {
				                output += "No Selected Index";
				                vRec = new Vector();
				            } else {
				                // Find out which indexes are selected.
				                int minIndex = lsm.getMinSelectionIndex();
				                int maxIndex = lsm.getMaxSelectionIndex();
				                vRec = new Vector();
				                for (int i = minIndex; i <= maxIndex; i++) {
				                    if (lsm.isSelectedIndex(i)) {
				                    	//System.out.println(jtb.getValueAt(i, 0)); //Debugger
				                    	
				                        output += " " + jtb.getValueAt(i,0) + "";
				                        vRec.addElement(jtb.getValueAt(i,0) + "");
				                    }
				                }
				            }
				            
				            rmiDebug.setDebugText("Selected Row = " + output); //Debugger
				    	}
					});
					
	    			JScrollPane jsp = new JScrollPane(jtb);
	    			jsp.setSize(400,400);
				
					final JInternalFrame jif = new JInternalFrame();
					jif.getContentPane().setLayout(new BorderLayout());
					jif.setTitle("List of available servers ");
					jif.setIconifiable(true);
					jif.setResizable(true);
					jif.setMaximizable(true);
					jif.setClosable(true);
					
						//====== Advance options Panel ======
						JPanel jp = new JPanel();
						jp.setLayout(new FlowLayout(FlowLayout.CENTER));
						
						JButton jbtnCreate = new JButton("Create");
						JButton jbtnDelete = new JButton("Delete");
						JButton jbtnUpdate = new JButton("Modify");
						JButton jbtnHelp = new JButton("Help");
						
						jbtnCreate.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								createServer(jif);
							}
						});
						
						jbtnDelete.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								if (vRec.size()>0) {
									deleteServer(vRec, jif);
								}
							}
						});
						
						jbtnUpdate.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								if (vRec.size()>0) {
									updateServer(data, vRec, dataCol, jif);
								}
							}
						});
						
						// 1. create HelpSet and HelpBroker objects
						HelpSet hs = RMIClientGUI_HelpSet.getHelpSet("doc/helpset/RMIAdminHelp_ManageServer.hs"); 
						final HelpBroker hb = hs.createHelpBroker();
						
						// 2. handle events
						jbtnHelp.addActionListener(new CSH.DisplayHelpFromSource(hb));
						
						jp.add(jbtnCreate);
						jp.add(jbtnDelete);
						jp.add(jbtnUpdate);
						jp.add(jbtnHelp);
						
				        jp.setBorder(
				                BorderFactory.createCompoundBorder(
				                                BorderFactory.createTitledBorder("Options"),
				                                BorderFactory.createEmptyBorder(5,5,5,5)
								)
						);
						//===================================
					
					jif.getContentPane().add(jsp, BorderLayout.CENTER);
					jif.getContentPane().add(jp, BorderLayout.SOUTH);
					
					jif.setBackground(Color.white);
					
					if (dataRow<=10) {
						jif.setSize(450, 320);
						
						//Dynamically adjust frame size by row no
						//jif.setSize(450, 120+20*dataRow);
						
					} else {
						jif.setSize(450, 320);
					}
					
					jdp.add(jif);
					
			    	try {
			    		jif.setSelected(true);
			    	} catch (Exception exc) {
			    		//Nothing to do
			    	}
			    	
					int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
					int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
					jif.setLocation(x,y);
				    jif.setVisible(true);
				//=====================================================
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
			}
		}	
	}
}