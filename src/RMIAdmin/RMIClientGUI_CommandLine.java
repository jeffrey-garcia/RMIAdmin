package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import java.net.*;
import javax.help.*;
import javax.help.search.*;


public class RMIClientGUI_CommandLine extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	private JSplitPane jspt;
	private JSplitPane jsptMain;
	
	private JTextField jtfServer;
	private JLabel jlbServer;
	public JTextArea jtaOutput;
	
	private int serverID = 0;
	private String serverHostname = "";
	private String serverIP = "";
	private String serverPort = "";
	private String serverOS = "";
	private String serverShell = "";
	private String serverDesc = "";
	
	public RMIClientGUI_Debugger rmiDebug;
	
	private Vector vBatch, vBatchName;
	
	
	public RMIClientGUI_CommandLine(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		jlbServer = new JLabel("<html><font color='#333333'>No Server Selected, " + 
								"press the Select Server button to choose the target." +
								"</font></html>");
		
		//setBackground(Color.darkGray);
		setLayout(new BorderLayout());
		
		//------ Initialize the Global Variable ------
		vBatch = new Vector();
		vBatchName = new Vector();
		
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
					
					//Auto highlight the current Server
					//jtfServer.setSelectionStart(0);
					//jtfServer.setSelectionEnd(jtfServer.getText().length());
					//jtfServer.requestFocus();
				
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
			
			//====== Setup the West Panel ======
			JPanel jpWest = new JPanel();
			jpWest.setLayout(new GridLayout(10,1,5,5)); //10 Row, 1 Column
			
			/*************************************************
			 * A bug #4820659 prevents the user from using 
			 * the mouse to close dialogs decorated by the 
			 * Java look and feel. (Not solved until JDK 1.5
			 ************************************************/
			JDialog.setDefaultLookAndFeelDecorated(true);
			JToolBar jtb = new JToolBar("Command Line");
			jtb.add(jpWest);
			
			JScrollPane jspOption = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jspOption.setPreferredSize(new Dimension(80,200));
			jspOption.getViewport().add(jtb);
			
				JButton jbtnHelp = new JButton("Help Menu");
				JButton jbtnSetupShellEnv = new JButton("Setup Shell");
				JButton jbtnCustomBatch = new JButton("Custom Batch");
				JButton jbtnCommandConsole = new JButton("Run Command");
				JButton jbtnSaveSetting = new JButton("Save Config");
				JButton jbtnClearOutput = new JButton("Clear Screen");
				JButton jbtnClearDebug = new JButton("Clear Debug");
				JButton jbtnReserved = new JButton("Reserved");
				jbtnReserved.setEnabled(false);
				
				jbtnHelp.setToolTipText("Read the help menu on executing remote command");
				jbtnSetupShellEnv.setToolTipText("Create and setup remote shell environment");
				jbtnCustomBatch.setToolTipText("Customize batch job for list of defined tasks");
				jbtnCommandConsole.setToolTipText("Execute one-time command");
				jbtnSaveSetting.setToolTipText("Save the modification made");
				jbtnClearOutput.setToolTipText("Clear the output screen");
				jbtnReserved.setToolTipText("Reserved for future use");
				jbtnClearDebug.setToolTipText("Clear the debug console");
				
				// 1. create HelpSet and HelpBroker objects
				HelpSet hs = RMIClientGUI_HelpSet.getHelpSet("doc/helpset/RMIAdminHelp_ExecuteCommand.hs");
				final HelpBroker hb = hs.createHelpBroker();
					
				// 2. handle events
				jbtnHelp.addActionListener(new CSH.DisplayHelpFromSource(hb));
				
				jbtnSetupShellEnv.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (serverShell.equals("")) {
							warning("Please select Server first!");
						} else {
							setupShell();
						}
					}
				});
				
				jbtnCustomBatch.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (serverShell.equals("")) {
							warning("Please select Server first!");
						} else {
							customBatch();
						}
					}
				});
				
				jbtnCommandConsole.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (serverShell.equals("")) {
							warning("Please select Server first!");
						} else {
							showCommandConsole();
						}
					}
				});
				
				jbtnSaveSetting.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (serverShell.equals("")) {
							warning("Please select Server first!");
						} else {
							saveBatchConfig();
						}
					}
				});
				
				jbtnClearOutput.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jtaOutput.setText("");
						rmiDebug.setDebugText("Output Screen cleared.");
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
			
			jpWest.add(jbtnHelp);
			jpWest.add(jbtnSetupShellEnv);
			jpWest.add(jbtnCustomBatch);
			jpWest.add(jbtnCommandConsole);
			jpWest.add(jbtnSaveSetting);
			jpWest.add(jbtnClearOutput);
			jpWest.add(jbtnClearDebug);
			//jpWest.add(jbtnReserved);
			//==================================
			
			//====== Setup the Center Panel ======
			JPanel jpCenter = new JPanel();
			jpCenter.setLayout(new BorderLayout());
			jpCenter.setPreferredSize(new Dimension(560,500));
			
				JPanel jpCenterTop = new JPanel();
				jpCenterTop.setLayout(new BorderLayout());
			
					JLabel jlbSearchOutput = new JLabel("Search Output: ");
					
					final JTextField jtfSearchOutput = new JTextField(20);
					jtfSearchOutput.setToolTipText("Input the keyword(s) to search");
					jtfSearchOutput.addMouseListener(new RMIClientGUI_MouseListener());
					
					final JButton jbtnSearchOutput = new JButton("Find");
					jbtnSearchOutput.setToolTipText("Click this button to begin searching the keyword(s)");
					jbtnSearchOutput.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent ae) {
							jtaOutput.requestFocusInWindow();
							
							String keyword = jtfSearchOutput.getText();
							String screenText = jtaOutput.getText();
							
							if (screenText.indexOf(keyword) >= 0) {
								jtaOutput.setCaretPosition(screenText.indexOf(keyword));
								jtaOutput.moveCaretPosition(jtaOutput.getCaretPosition() + keyword.length());
							}
						}
					});
					
					final JButton jbtnSearchOutputNext = new JButton("Next");
					jbtnSearchOutputNext.setToolTipText("Click this button to search the next match");
					jbtnSearchOutputNext.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent ae) {
							jtaOutput.requestFocusInWindow();
							
							String keyword = jtfSearchOutput.getText();
							String currentText = jtaOutput.getText();
							currentText = currentText.substring(jtaOutput.getCaretPosition(), jtaOutput.getText().length());
							
							if (currentText.indexOf(keyword) >= 0) {
								jtaOutput.setCaretPosition(jtaOutput.getCaretPosition() + currentText.indexOf(keyword));
								jtaOutput.moveCaretPosition(jtaOutput.getCaretPosition() + keyword.length());
							}
						}
					});
					
					JPanel jpCenterTop1 = new JPanel();
					jpCenterTop1.setLayout(new BorderLayout());
					jpCenterTop1.add(jbtnSearchOutput, BorderLayout.WEST);
					jpCenterTop1.add(jbtnSearchOutputNext, BorderLayout.EAST);
					
				jpCenterTop.add(jlbSearchOutput, BorderLayout.WEST);
				jpCenterTop.add(jtfSearchOutput, BorderLayout.CENTER);
				jpCenterTop.add(jpCenterTop1, BorderLayout.EAST);
				jpCenterTop.setPreferredSize(new Dimension(500,25));
				
    			jtaOutput = new JTextArea("");
				jtaOutput.setEditable(false);
				jtaOutput.setLineWrap(false);
				jtaOutput.setBackground(Color.BLACK);
				jtaOutput.setForeground(Color.CYAN);
				jtaOutput.setToolTipText("Output Screen");
				jtaOutput.addMouseListener(new RMIClientGUI_MouseListener());
				
				JScrollPane jspOutput = new JScrollPane(jtaOutput,
									JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
				jspOutput.setPreferredSize(new Dimension(560,500));
			
			jpCenter.add(jpCenterTop, BorderLayout.NORTH);	
			jpCenter.add(jspOutput, BorderLayout.CENTER);
			jpCenter.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Output Screen"),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
			//====================================
			
			//====== Combine the Center & West Panel ======
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
			
		add(jpNorth, BorderLayout.NORTH);
		//add(jspOption, BorderLayout.WEST);
		add(jsptMain, BorderLayout.CENTER);
		add(jpSouth, BorderLayout.SOUTH);
	}
	
	
	private void loadBatchConfig() {
		Vector vComBatch = new Vector();
		
		try {
			//====== Get the Batch Configurations ======
			RMIClient_XMLParser_Item rmiXML = new RMIClient_XMLParser_Item(rmiDebug);
			vComBatch = rmiXML.loadItem(serverID);
			//==========================================
			
			//====== Reset the Batch Global Variables ======
			vBatchName = new Vector();
			vBatch = new Vector();
			//==============================================
			
			//====== Parse the Batch Config into Batch Name & Batch Detail ======
			for (int i=0; i<vComBatch.size(); i++) {
				String tmp = vComBatch.elementAt(i) + "";
				String name = tmp.substring(0, tmp.indexOf("......"));
				String info = tmp.substring(tmp.indexOf("......")+6, tmp.length());
				
				vBatchName.add(name);
				vBatch.add(info);
			}
			//===================================================================
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
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
				
				//Auto highlight the current Server
				//jtfServer.setSelectionStart(0);
				//jtfServer.setSelectionEnd(jtfServer.getText().length());
				//jtfServer.requestFocus();
					
				loadBatchConfig();
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void setupShell_old() {
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane jop;
			JDialog jdg;
			String opt = "<html>Type in the batch/script to be preset<br>" +
						 "to the target shell environment<br>" + 
						 "<i><font color='#666666'>e.g. cmd.exe or /bin/bash</font></i><br>" + 
						 "current Shell = " + serverShell + "</html>";
			
			jop = new JOptionPane(opt,
									JOptionPane.INFORMATION_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION);
			jop.setWantsInput(true);
			
			jdg = jop.createDialog(this, "Setup Shell Environment");
			
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
			
			String tmpShell = (String)jop.getInputValue();
			
			if (tmpShell.length()>0 && tmpShell.equals("uninitializedValue")==false) {
				serverShell = tmpShell;
				rmiDebug.setDebugText("Input Shell = " + serverShell);
			} else {
				rmiDebug.setDebugText("Shell = " + serverShell);
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void setupShell() {
		try {
			JTextField jtf = new JTextField(10);
			jtf.addMouseListener(new RMIClientGUI_MouseListener());
			
			Object[] msg = {
				"<html>Type in the batch/script to be preset<br>" +
				"to the target shell environment<br>" + 
				"<i><font color='#666666'>e.g. cmd.exe or /bin/bash</font></i><br>" + 
				"current Shell = " + serverShell + "</html>", 
				jtf
			};
			
			int result = JOptionPane.showConfirmDialog((JFrame)jdp.getTopLevelAncestor(), 
														msg, "Setup Shell Environment", 
														JOptionPane.OK_CANCEL_OPTION, 
														JOptionPane.INFORMATION_MESSAGE);
			
			if(result == JOptionPane.OK_OPTION) {
				String tmpShell = jtf.getText();
				
				if(!tmpShell.equals("")) {
					serverShell = tmpShell;
					rmiDebug.setDebugText("Input Shell = " + serverShell);
				} else {
					rmiDebug.setDebugText("Shell = " + serverShell);
				}
				
			} else {
				rmiDebug.setDebugText("Shell = " + serverShell);
			}

		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void customBatch() {
		try {
			final JInternalFrame jif = new JInternalFrame("Batch Manager");
			jif.getContentPane().setLayout(new BorderLayout());
	    	jif.setResizable(true);
	    	jif.setIconifiable(true);
	    	jif.setMaximizable(false);
	    	jif.setClosable(true);
			jif.setSize(270,250);
			jif.setLocation(0,0);
			
				JPanel jpBatch = new JPanel(new BorderLayout());
					JPanel jpCenter = new JPanel();
					jpCenter.setLayout(new BorderLayout());
					
						final JList jlBatch = new JList();
						jlBatch.setListData(vBatchName);
				        jlBatch.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				        
				        JScrollPane jsp_01 = new JScrollPane(jlBatch);
						
						JPanel jpTmp = new JPanel();
						jpTmp.setLayout(new FlowLayout(FlowLayout.CENTER));
						
							JButton jbtnCreate = new JButton("Create");
							JButton jbtnDelete = new JButton("Delete");
							JButton jbtnModify = new JButton("Modify");
						
							jbtnCreate.setToolTipText("Create batch job");
							jbtnDelete.setToolTipText("Delete batch job");
							jbtnModify.setToolTipText("Modify batch job");
						
					        jbtnCreate.addActionListener(new ActionListener() {
					        	public void actionPerformed(ActionEvent ae) {
					        		createBatch(jlBatch);
					        	}
					        });
					        jbtnDelete.addActionListener(new ActionListener() {
					        	public void actionPerformed(ActionEvent ae) {
					        		deleteBatch(jlBatch);
					        	}
					        });
					        jbtnModify.addActionListener(new ActionListener() {
					        	public void actionPerformed(ActionEvent ae) {
					        		modifyBatch(jlBatch);
					        	}
					        });
						
						jpTmp.add(jbtnCreate);
						jpTmp.add(jbtnDelete);
						jpTmp.add(jbtnModify);
						
					jpCenter.add(jsp_01, BorderLayout.CENTER);
					jpCenter.add(jpTmp, BorderLayout.SOUTH);
					
					JPanel jpBottom = new JPanel();
					jpBottom.setLayout(new BorderLayout());
									        
				        JButton jbtnRunBatch = new JButton("Execute");
				        jbtnRunBatch.setToolTipText("Execute batch job");
				        jbtnRunBatch.addActionListener(new ActionListener() {
				        	public void actionPerformed(ActionEvent ae) {
								if (serverShell.equals("")) {
									warning("Please select Server first!");
								} else {
									executeBatch(jlBatch);
								}
				        	}
				        });
			        	
			        jpBottom.add(jbtnRunBatch, BorderLayout.CENTER);
			        
		        jpBatch.setBorder(
		                BorderFactory.createCompoundBorder(
		                                BorderFactory.createTitledBorder("Custom Batch"),
		                                BorderFactory.createEmptyBorder(5,5,5,5)
						)
				);
				jpBatch.add(jpCenter, BorderLayout.CENTER);
				jpBatch.add(jpBottom, BorderLayout.SOUTH);
			
			jif.getContentPane().add(jpBatch);
			
		    jif.setSelected(true);
		    jdp.add(jif);
		    
			int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
			int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
			jif.setLocation(x,y);
			jif.setVisible(true);
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void createBatch(final JList jlBatch) {
		final RMIClientGUI_CommandLine rmiGUI_CLI = this;
		
		try {
			final JInternalFrame jif = new JInternalFrame("Create New Batch Job");
			jif.getContentPane().setLayout(new BorderLayout());
	    	jif.setResizable(true);
	    	jif.setIconifiable(true);
	    	jif.setMaximizable(false);
	    	jif.setClosable(true);
			jif.setSize(300,250);
			jif.setLocation(0,0);
			
			JPanel jpTop = new JPanel();
			jpTop.setLayout(new FlowLayout(FlowLayout.LEFT));
			
				JLabel jlbBatchName = new JLabel("Name:");
				final JTextField jtfBatchName = new JTextField(20);
				jtfBatchName.setToolTipText("Key-in a name for this batch job.");
				jtfBatchName.addMouseListener(new RMIClientGUI_MouseListener());
			
			jpTop.add(jlbBatchName);
			jpTop.add(jtfBatchName);
			
			final JTextArea jtaBatch = new JTextArea("");
			jtaBatch.setToolTipText("Type your batch here.");
			jtaBatch.addMouseListener(new RMIClientGUI_MouseListener());
			
			JScrollPane jspBatch = new JScrollPane();
			jspBatch.getViewport().add(jtaBatch);
			
			JPanel jpBottom = new JPanel();
			jpBottom.setLayout(new GridLayout(1,2,5,5));
			
				JButton jbtnCreate = new JButton("Create");
				jbtnCreate.setToolTipText("Click this button to create the batch.");
				jbtnCreate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (jtfBatchName.getText().length()>0 && jtaBatch.getText().length()>0) {
							String batchName = jtfBatchName.getText();						
							String batch = jtaBatch.getText();
							batch = batch.replaceAll("\n", " && ");
							//System.out.println(batch); //Debugger
							
							//--- Update the JList of Batch ---
							vBatch.addElement(batch);
							vBatchName.addElement(batchName);
							
							jlBatch.setListData(vBatchName);
							//---------------------------------
							
							rmiDebug.setDebugText("Batch job [" + batchName + "] created.");
							jif.setVisible(false);
							jif.dispose();
						}
					}
				});
				
				JButton jbtnClear = new JButton("Clear");
				jbtnClear.setToolTipText("Click this button to reset.");
				jbtnClear.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jtfBatchName.setText("");
						jtaBatch.setText("");
					}
				});
			
			jpBottom.add(jbtnCreate);
			jpBottom.add(jbtnClear);
			
			jif.getContentPane().add(jpTop, BorderLayout.NORTH);
			jif.getContentPane().add(jspBatch, BorderLayout.CENTER);
			jif.getContentPane().add(jpBottom, BorderLayout.SOUTH);
			
		    jif.setSelected(true);
		    jdp.add(jif);
			
			int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
			int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
			jif.setLocation(x,y);
			jif.setVisible(true);
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void deleteBatch(final JList jlBatch) {
		int i = jlBatch.getSelectedIndex();
		
		if (i != -1) {
			String batchName = vBatchName.elementAt(i) + "";
			String batch = vBatch.elementAt(i) + "";
			
			rmiDebug.setDebugText("Selected batch to Delete: " + batchName);
			
			vBatch.removeElementAt(i);
			vBatchName.removeElementAt(i);
			jlBatch.setListData(vBatchName);
			
			rmiDebug.setDebugText("Done.");
		}
	}
	
	
	private void modifyBatch(final JList jlBatch) {
		final RMIClientGUI_CommandLine rmiGUI_CLI = this;
		
		try {
			final int i = jlBatch.getSelectedIndex();
			
			if (i != -1) {
				final String batchName = vBatchName.elementAt(i) + "";
				
				String tmpBatch = vBatch.elementAt(i) + "";
				tmpBatch = tmpBatch.replaceAll(" && ","\n"); //replace all "&&" to "\n"
				final String batch = tmpBatch;
				
				rmiDebug.setDebugText("Selected batch to Edit: " + batchName);
				
				final JInternalFrame jif = new JInternalFrame("Edit Batch Job");
				jif.getContentPane().setLayout(new BorderLayout());
		    	jif.setResizable(true);
		    	jif.setIconifiable(true);
		    	jif.setMaximizable(false);
		    	jif.setClosable(true);
				jif.setSize(300,250);
				jif.setLocation(0,0);
				
				JPanel jpTop = new JPanel();
				jpTop.setLayout(new FlowLayout(FlowLayout.LEFT));
				
					JLabel jlbBatchName = new JLabel("Name:");
					final JTextField jtfBatchName = new JTextField(20);
					jtfBatchName.setToolTipText("Key-in a name for this batch job.");
					jtfBatchName.setText(batchName);
					jtfBatchName.addMouseListener(new RMIClientGUI_MouseListener());
				
				jpTop.add(jlbBatchName);
				jpTop.add(jtfBatchName);
				
				final JTextArea jtaBatch = new JTextArea("");
				jtaBatch.setToolTipText("Type your batch here.");
				jtaBatch.addMouseListener(new RMIClientGUI_MouseListener());
				jtaBatch.setText(batch);
				
				JScrollPane jspBatch = new JScrollPane();
				jspBatch.getViewport().add(jtaBatch);
				
				JPanel jpBottom = new JPanel();
				jpBottom.setLayout(new GridLayout(1,2,5,5));
				
					JButton jbtnSave = new JButton("Save");
					jbtnSave.setToolTipText("Click this button to save the batch.");
					jbtnSave.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							if (jtfBatchName.getText().length()>0 && jtaBatch.getText().length()>0) {
								String newBatchName = jtfBatchName.getText();						
								String newBatch = jtaBatch.getText();
								
								newBatch = newBatch.replaceAll("\n", " && ");
								//System.out.println(newBatch); //Debugger
								
								//--- Update the JList of Batch ---
								vBatch.setElementAt(newBatch, i);
								vBatchName.setElementAt(newBatchName, i);
								
								jlBatch.setListData(vBatchName);
								//---------------------------------
								
								rmiDebug.setDebugText("Batch job [" + batchName + "] modified.");
								jif.setVisible(false);
								jif.dispose();
							}
						}
					});
					
					JButton jbtnClear = new JButton("Reset");
					jbtnClear.setToolTipText("Click this button to reset.");
					jbtnClear.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							jtfBatchName.setText(batchName);
							jtaBatch.setText(batch);
						}
					});
				
				jpBottom.add(jbtnSave);
				jpBottom.add(jbtnClear);
				
				jif.getContentPane().add(jpTop, BorderLayout.NORTH);
				jif.getContentPane().add(jspBatch, BorderLayout.CENTER);
				jif.getContentPane().add(jpBottom, BorderLayout.SOUTH);
				
			    jif.setSelected(true);
			    jdp.add(jif);
				
				int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
				int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
				jif.setLocation(x,y);
				jif.setVisible(true);
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void executeBatch(final JList jlBatch) {
		RMIClientGUI_CommandLine rmiGUI_CLI = this;
		String batch = "";
		String batchName = "";
		
		try {
			int i = jlBatch.getSelectedIndex();
			if (i != -1) {
				batchName = vBatchName.elementAt(i) + "";
				batch = vBatch.elementAt(i) + "";
				
				rmiDebug.setDebugText("Selected batch to execute: " + batchName);
				new runBatchThread(rmiGUI_CLI, batch, batchName).start();
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void saveBatchConfig() {
		try {
			//====== Convert the custom batch programs with its batch name into one container ======
			Vector vComBatch = new Vector();
			for (int i=0; i<vBatchName.size(); i++) {
				vComBatch.addElement(vBatchName.elementAt(i) + "......" + vBatch.elementAt(i));
			}
			//======================================================================================
			
			//====== Save the Batch Setting to XML ======
			RMIClient_XMLParser_Item rmiXML = new RMIClient_XMLParser_Item(rmiDebug);
			rmiXML.addItem(serverID, serverHostname, serverIP, serverPort, serverOS, serverShell, serverDesc, vComBatch);
			//==============================================
			
			warning("Batch Configuration Saved.");
			rmiDebug.setDebugText("New Batch Configuration Saved.");
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
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
	
	
	private void showCommandConsole() {
		final RMIClientGUI_CommandLine rmiGUI_CLI = this;
		
		try {
			final JInternalFrame jif = new JInternalFrame();
			jif.setTitle("Command Console");
	    	jif.setResizable(true);
	    	jif.setIconifiable(true);
	    	jif.setMaximizable(false);
	    	jif.setClosable(true);
	    	jif.getContentPane().setLayout(new BorderLayout());
	    	
			//===== Setup the command history area =====
	    	final JTextArea jtaHistory = new JTextArea();
			jtaHistory.setEditable(false);
			jtaHistory.addMouseListener(new RMIClientGUI_MouseListener());
			jtaHistory.setBackground(Color.BLACK);
			jtaHistory.setForeground(Color.MAGENTA);
			jtaHistory.setToolTipText("Command History");
			
			JScrollPane jspHistory = new JScrollPane(jtaHistory,
								JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
								
		    jspHistory.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Command History"),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
				
			jspHistory.setPreferredSize(new Dimension(300,150));
			//===========================================
	    	
			//==== Setup the command input area ====
			JPanel jpInputArea = new JPanel();
			jpInputArea.setLayout(new BorderLayout());
			
				final JTextField jtfCommand = new JTextField();
				jtfCommand.addMouseListener(new RMIClientGUI_MouseListener());
				jtfCommand.setEditable(true);
			
			jpInputArea.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("Input \"One-Time\" Command"),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
				
			jpInputArea.add(jtfCommand, BorderLayout.CENTER);
			//======================================
	    	
			//===== Setup the button area =====
			JPanel jpButtonArea = new JPanel();
			jpButtonArea.setLayout(new FlowLayout(FlowLayout.CENTER));
			
				final JButton jbtn_run = new JButton(" Run ");
				final JButton jbtn_stop = new JButton(" Stop ");
				final JButton jbtn_clear = new JButton(" Clear ");
				final JButton jbtn_close = new JButton(" Quit ");
				final JLabel jlb_status = new JLabel("<html><font color='#008000'>Ready.</font></html>");
				
				jbtn_stop.setEnabled(false);
				
				jbtn_run.setToolTipText("Execute command");
				jbtn_stop.setToolTipText("Terminate executing command");
				jbtn_clear.setToolTipText("Clear & reset the Command Console to its initial state");
				jbtn_close.setToolTipText("Quit & exit the Command Console");
				
				jbtn_run.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae) {
						String command = jtfCommand.getText();
						
						if (command.length() > 0) {
							jtaHistory.append(command + "\n");
							jtaHistory.setCaretPosition(jtaHistory.getDocument().getLength());
							new runCommandThread(rmiGUI_CLI, command, jif, jbtn_run, jbtn_stop, jbtn_close, jlb_status).start();
							jtfCommand.setText("");
						}
					}
				});
				
				jbtn_clear.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae) {
						jtaOutput.setText("");
						jtaHistory.setText("");
						jtfCommand.setText("");
					}
				});
				
				jbtn_close.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae) {
						if (jbtn_stop.isEnabled() == false) {
							jif.setVisible(false);
							jif.dispose();
						}
					}
				});
				
			jpButtonArea.add(jbtn_run);
			jpButtonArea.add(jbtn_stop);
			jpButtonArea.add(jbtn_clear);
			jpButtonArea.add(jbtn_close);
			//=================================
			
			//===== Setup the status area =====
			JPanel jpStatus = new JPanel();
			jpStatus.setLayout(new FlowLayout(FlowLayout.LEFT));
			jpStatus.add(jlb_status);
			//=================================
	
			//===== Merge jpButtonArea and jpStatus =====
			JPanel jpBottom = new JPanel();
			jpBottom.setLayout(new BorderLayout());
			jpBottom.add(jpButtonArea, BorderLayout.CENTER);
			jpBottom.add(jpStatus, BorderLayout.SOUTH);
			jpBottom.setPreferredSize(new Dimension(300,60));
			//===========================================
			
			//===== Merge jspHistory and ipInputArea =====
			JPanel jpCenter = new JPanel();
			jpCenter.setLayout(new BorderLayout());
			jpCenter.add(jspHistory, BorderLayout.CENTER);
			jpCenter.add(jpInputArea, BorderLayout.SOUTH);
			//============================================
			
			//===== Put all the components together =====
			jif.getContentPane().add(jpCenter, BorderLayout.CENTER);
			jif.getContentPane().add(jpBottom, BorderLayout.SOUTH);
			//===========================================
	    	
	    	jif.pack();
	    	
	    	//Restrict the minimal size of the command console
	    	jif.setMinimumSize(new Dimension(jif.getWidth(), jif.getHeight()));
	    	
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
	    	
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	public class runCommandThread extends Thread {
		RMIClientGUI_CommandLine rmiGUI_CLI;
		RMIClient_CommandLine rmi_CLI;
		String command;
		
		JInternalFrame jif;
		JButton jbtn_run;
		JButton jbtn_stop;
		JButton jbtn_close;
		JLabel jlb_status;
		
		public runCommandThread(RMIClientGUI_CommandLine rmiGUI_CLI, String command, JInternalFrame jif, JButton jbtn_run, JButton jbtn_stop, JButton jbtn_close, JLabel jlb_status) {
			this.jbtn_run = jbtn_run;
			this.jbtn_stop = jbtn_stop;
			this.jbtn_close = jbtn_close;
			this.jlb_status = jlb_status;
			this.jif = jif;
			
			this.command = command;
			this.rmiGUI_CLI = rmiGUI_CLI;
			this.rmi_CLI = new RMIClient_CommandLine(serverIP, serverPort, serverShell, command, rmiGUI_CLI);
			
			rmi_CLI.rexec();
		}
		
		public void run() {
			try {
				jbtn_run.setEnabled(false);
				jbtn_stop.setEnabled(true);
				jlb_status.setText("<html><font color='blue'>Running, please wait...</font></html>");
				
				jbtn_stop.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae) {
						rmi_CLI.cancelExec();
						jbtn_stop.setEnabled(false);
						jlb_status.setText("<html><font color='red'>Terminating, please wait...</font></html>");
					}
				});
				
				jbtn_close.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent ae) {
						if (jbtn_stop.isEnabled() == false) {
							jif.setVisible(false);
							jif.dispose();
						} else {
							rmi_CLI.cancelExec();
							jbtn_stop.setEnabled(false);
							jlb_status.setText("<html><font color='red'>Terminating, please wait...</font></html>");
						}
					}
				});
				
				while (rmi_CLI.getCompleteStatus() == false) {
					Thread.sleep(500); //wait for 0.5 sec, then retry
				}
				
				jbtn_run.setEnabled(true);
				jbtn_stop.setEnabled(false);
				jlb_status.setText("<html><font color='#008000'>Ready.</font></html>");
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
			}
		}
	}
	
	
	public class runBatchThread extends Thread {
		RMIClientGUI_CommandLine rmiGUI_CLI;
		RMIClient_CommandLine rmi_CLI;
		String command;
		String batchName;
		
		public runBatchThread(RMIClientGUI_CommandLine rmiGUI_CLI, String command, String batchName) {
			this.command = command;
			this.batchName = batchName;
			this.rmiGUI_CLI = rmiGUI_CLI;
			this.rmi_CLI = new RMIClient_CommandLine(serverIP, serverPort, serverShell, command, rmiGUI_CLI);
		}
		
		public void run() {
			try {
				new execThread().start();
				
				while (rmi_CLI.getCompleteStatus() == false) {
					rmiDebug.setDebugText("Waiting Batch - " + batchName + "  to complete...");
					Thread.sleep(500); //wait for 0.5 sec, then retry
				}
				
				rmiDebug.setDebugText("Batch - " + batchName + "  finished.");
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
			}
		}
		
		public class execThread extends Thread {
			public void run() {
				try {
					rmi_CLI.rexec();
					
				} catch (Exception exc) {
					StackTraceElement[] error = exc.getStackTrace();
					rmiDebug.setDebugText(error);
				}
			}
		}
	}
	
}