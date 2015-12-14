package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.text.*;

import java.util.*;
import java.io.*;


public class RMIClientGUI_TransferLocalView extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	
	private DefaultMutableTreeNode root;
	private DefaultTreeModel dtm;
	private JTree jtree;
	
	private JPanel jpSouth;
	private JScrollPane jspLocalView;
	private JProgressBar jpbLocalView;
	private JButton jbtnOpenQueue;
	
	public LocalViewThread LVT;
	
	public RMIClientGUI_Debugger rmiDebug;
	public RMIClientGUI_FileTransfer rmiGUI_FT;
	
	private String filePasteType = "";
	private String localViewPath = "";
	
	private Vector vUploadQueue = new Vector();
	
	
	public RMIClientGUI_TransferLocalView(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug, RMIClientGUI_FileTransfer rmiGUI_FT) {
		this.rmiGUI_FT = rmiGUI_FT;
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		
		initGUI();
	}
	
	
	public void initGUI() {
		setPreferredSize(new Dimension(180, 250));
		setMinimumSize(new Dimension(150, 150));
		setLayout(new BorderLayout());
		
			jpSouth = new JPanel();
			jpSouth.setLayout(new FlowLayout(FlowLayout.CENTER));
			
				jbtnOpenQueue = new JButton("Upload Queue");
				jbtnOpenQueue.setToolTipText("Click this button to open the upload queue");
				
				jbtnOpenQueue.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (vUploadQueue.size() > 0) {
							//Convert the records in the Upload Queue (Vector) to a 2-Dimensional Object array
							rmiDebug.setDebugText("Convert Objects in Upload Queue (Vector) to 2-Dimensional Object array");
							
							Object [][] data = new Object [vUploadQueue.size()][6];
							
							for (int i=0; i<vUploadQueue.size(); i++) {
								Object [] tmp = (Object [])vUploadQueue.elementAt(i);
								
								for (int j=0; j<tmp.length; j++) {
									data [i][j] = tmp [j];
								}
							}
							
							openQueue(data);
							
						} else {
							Object [][] data = new Object [0][6];
							openQueue(data);
						}
					}
				});
				
			jpSouth.add(jbtnOpenQueue);
			jpSouth.setVisible(false);
			
		setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Local View"),
				BorderFactory.createEmptyBorder(5,5,5,5)
			)
		);
		setVisible(true);
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
			rmiGUI_FT.clearAllViews();
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
	
	
	public void clearView() {
		removeAll();
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(280,250));
		updateUI();
		revalidate();
	}
	
	
	public String getLocalPath() {
		return localViewPath;
	}
	
	
	public void openFileSystem() {
		LVT = new LocalViewThread(this);
		LVT.start();
	}
	
	
	public void refreshLocalView() {
		LVT.refresh();
	}
	
	
	private void showFileInfo(Vector vFileInfo) {
		if (vFileInfo==null || vFileInfo.size()==0) {
			return;
		}
		
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane jop;
			JDialog jdg;
			String opt = "";
			
			opt += "<html>";
			for (int i=0; i<vFileInfo.size(); i++) {
				if (i==0) {
					opt += "File name: <font color='#666666'>" + vFileInfo.elementAt(i) + "</font><br>";
				}
				if (i==1) {
					opt += "Canonical pathname: <font color='#666666'>" + vFileInfo.elementAt(i) + "</font><br>";
				}
				if (i==2) {
					opt += "File path: <font color='#666666'>" + vFileInfo.elementAt(i) + "</font><br>";
				}
				if (i==3) {
					opt += "Parent pathname: <font color='#666666'>" + vFileInfo.elementAt(i) + "</font><br>";
				}
				if (i==4 && (vFileInfo.elementAt(i)+"").equals("true")) {
					opt += "Type: <font color='#666666'>Directory</font><br>";
				}
				if (i==4 && (vFileInfo.elementAt(i)+"").equals("false")) {
					opt += "Type: <font color='#666666'>File</font><br>";
				}
				if (i==6) {
					if ((vFileInfo.elementAt(i)+"").equals("true")) {
						opt += "Hidden: <font color='#666666'>Yes</font><br>";
					} else {
						opt += "Hidden: <font color='#666666'>No</font><br>";
					}
				}
				if (i==7) {
					if ((vFileInfo.elementAt(i)+"").equals("true")) {
						opt += "File readable: <font color='#666666'>Yes</font><br>";
					} else {
						opt += "File readable: <font color='#666666'>No</font><br>";
					}
				}
				if (i==8) {
					if ((vFileInfo.elementAt(i)+"").equals("true")) {
						opt += "File writable: <font color='#666666'>Yes</font><br>";
					} else {
						opt += "File writable: <font color='#666666'>No</font><br>";
					}
				}
				if (i==9) {
					opt += "Last modified: <font color='#666666'>" + new Date(Long.parseLong(vFileInfo.elementAt(i)+"")) + "</font><br>";
				}
				if (i==10) {
					opt += "File size: <font color='#666666'>" + vFileInfo.elementAt(i) + "</font><br>";
				}
			}
			opt += "</html>";
			
			jop = new JOptionPane(opt,JOptionPane.INFORMATION_MESSAGE);
			jop.setWantsInput(false);
			
			jdg = jop.createDialog(this, "File Information");
			
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
			
			String close = (String)jop.getInputValue();
			
			if (close.length()>0 && close.equals("uninitializedValue")==false) {
			} else {
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	private String renameFile_old(String oldFileName) {
		String newFileName = "";
		
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane jop;
			JDialog jdg;
			String opt = "<html>Enter new Name</html>";
			
			jop = new JOptionPane(opt,
									JOptionPane.QUESTION_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION);
									
			jop.setWantsInput(true);
			jop.setInitialSelectionValue(oldFileName);
			
			jdg = jop.createDialog(this, "Rename File/Directory");
			
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
			
			newFileName = (String)jop.getInputValue();
			
			if (newFileName.length()>0 && newFileName.equals("uninitializedValue")==false) {
				rmiDebug.setDebugText("New Name for File/Folder = " + newFileName);
				
			} else {
				rmiDebug.setDebugText("Rename File/Folder cancelled");
				newFileName = "";
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
		
		return newFileName;
	}
	
	
	private String renameFile(String oldFileName) {
		String newFileName = "";
		
		try {
			JTextField jtf = new JTextField(10);
			jtf.setText(oldFileName);
			jtf.addMouseListener(new RMIClientGUI_MouseListener());
			
			Object[] msg = {
				"<html>Enter new Name</html>", 
				jtf
			};
			
			int result = JOptionPane.showConfirmDialog((JFrame)jdp.getTopLevelAncestor(), 
														msg, "Rename File/Directory", 
														JOptionPane.OK_CANCEL_OPTION, 
														JOptionPane.INFORMATION_MESSAGE);
			
			if(result == JOptionPane.OK_OPTION) {
				newFileName = jtf.getText();
				
				if(!newFileName.equals("")) {
					rmiDebug.setDebugText("New Name for File/Folder = " + newFileName);
					
				} else {
					rmiDebug.setDebugText("Rename File/Folder cancelled");
					newFileName = "";
				}
				
			} else {
				rmiDebug.setDebugText("Rename File/Folder cancelled");
				newFileName = "";
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
		
		return newFileName;
	}
	
	
	private String createFileType() {
		String fileInfo = "";
		
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane jop;
			JDialog jdg;
			String opt = "<html>Select the created object type</html>";
			
			String type [] = new String [2];
			type [0] = "Directory";
			type [1] = "File";
			
			jop = new JOptionPane(opt,
									JOptionPane.QUESTION_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION);
									
			jop.setSelectionValues(type);
			//jop.setWantsInput(true);
			
			jdg = jop.createDialog(this, "Create File/Directory");
			
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
			
			String selectedType = (String)jop.getInputValue();
			
			if (selectedType.length()>0 && selectedType.equals("uninitializedValue")==false) {
				rmiDebug.setDebugText("Creation Type = " + selectedType);
				fileInfo = createFileName(selectedType);
				
			} else {
				rmiDebug.setDebugText("Creation Type = " + selectedType);
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
		
		return fileInfo;
	}
	
	
	private String createFileName_old(String selectedType) {
		String fileInfo = "";
		
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane jop;
			JDialog jdg;
			String opt = "<html>Input the created object name</html>";
			
			jop = new JOptionPane(opt,
									JOptionPane.QUESTION_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION);
									
			jop.setWantsInput(true);
			
			jdg = jop.createDialog(this, "Input File/Directory name");
			
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
			
			String inputName = (String)jop.getInputValue();
			
			if (inputName.length()>0 && inputName.equals("uninitializedValue")==false) {
				rmiDebug.setDebugText("Creation Name = " + inputName);
				fileInfo = selectedType + "###" + inputName;
				
			} else {
				rmiDebug.setDebugText("Creation Name = " + inputName);
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
		
		return fileInfo;
	}
	
	
	private String createFileName(String selectedType) {
		String fileInfo = "";
		
		try {
			JTextField jtf = new JTextField(10);
			jtf.addMouseListener(new RMIClientGUI_MouseListener());
			
			Object[] msg = {
				"<html>Input the created object name</html>", 
				jtf
			};
			
			int result = JOptionPane.showConfirmDialog((JFrame)jdp.getTopLevelAncestor(), 
														msg, "Input File/Directory name", 
														JOptionPane.OK_CANCEL_OPTION, 
														JOptionPane.INFORMATION_MESSAGE);
			String inputName = "";
			if(result == JOptionPane.OK_OPTION) {
				inputName = jtf.getText();
				
				if(!inputName.equals("")) {
					rmiDebug.setDebugText("Creation Name = " + inputName);
					fileInfo = selectedType + "###" + inputName;
					
				} else {
					rmiDebug.setDebugText("Creation Name = " + inputName);
				}
				
			} else {
				rmiDebug.setDebugText("Creation Name = " + inputName);
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
		
		return fileInfo;
	}
	
	
	private synchronized void openQueue(final Object [][] data) {
		final int dataRow = data.length;
		final int dataCol = 6;
		final Vector vSelectedRec;
		
		try {
			//Setup the Column Names
			final String [] colNames = new String [dataCol];
			colNames [0] = "ID";
			colNames [1] = "Source";
			colNames [2] = "File Type";
			colNames [3] = "File Size";
			colNames [4] = "Transfer Mode";
			colNames [5] = "Destination";
			
			//====== Convert the 2-dimensonal Object array into GUI Spreadsheet ======
			//Create the Data Model
			final TableModel dataModel = new AbstractTableModel() {
				public int getColumnCount() { return dataCol; }
				public int getRowCount() { return dataRow;}
				public Object getValueAt(int row, int col) {
					return data [row][col];
				}
				public boolean isCellEditable(int row, int col) {
				   	return true;
				}
				public String getColumnName(int col) {
					return colNames [col];
				}
				public Class getColumnClass(int col) {
					if (col == 2) {
						return Long.class;
					} else if (col == 0){
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
					
					//Replace the tip when it is an icon image
					if (tip.indexOf("/")>=0 && tip.indexOf("Icon")>=0) {
						tip = tip.substring(tip.lastIndexOf("/")+1, tip.indexOf("Icon"));
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
	    	jtb.setRowHeight(20);
			
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
					 
					System.out.println("Selected Row = " + output); //Debugger
				}
			});
			
	    	JScrollPane jsp = new JScrollPane(jtb);
	    	
			final JInternalFrame jif = new JInternalFrame();
			jif.getContentPane().setLayout(new BorderLayout());
			jif.setTitle("Upload Queue");
			jif.setIconifiable(true);
			jif.setResizable(true);
			jif.setMaximizable(true);
			jif.setClosable(true);
			jif.setBackground(Color.white);
			jif.setSize(450,300);
			
			//------ Advance options Field ------
			JPanel jp = new JPanel();
			jp.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			JButton jbtnUpload = new JButton("Start Upload");
			jbtnUpload.setToolTipText("Click this button to start upload process");
			jbtnUpload.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					jif.setVisible(false);
					jif.dispose();
					
					new uploadQueueThread(LVT.rmiGUI_TLV, LVT.rmi_FT, LVT, data).start();
				}
			});
			
			JButton jbtnRefresh = new JButton("Refresh");
			jbtnRefresh.setToolTipText("Click this button to start upload process");
			jbtnRefresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					jif.setVisible(false);
					jif.dispose();
					
					//Convert the records in the Upload Queue (Vector) to a 2-Dimensional Object array
					rmiDebug.setDebugText("Re-convert Objects in Upload Queue (Vector) to 2-Dimensional Object array");
					
					Object [][] newData = new Object [vUploadQueue.size()][6];
					
					for (int i=0; i<vUploadQueue.size(); i++) {
						Object [] tmp = (Object [])vUploadQueue.elementAt(i);
						
						for (int j=0; j<tmp.length; j++) {
							newData [i][j] = tmp [j];
						}
					}
							
					openQueue(newData);
				}
			});
			
			JButton jbtnDelete = new JButton("Cancel");
			jbtnDelete.setToolTipText("Click this button to cancel the selected object from queue");
			jbtnDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					jif.setVisible(false);
					jif.dispose();
					
					if (vSelectedRec.size() > 0) {
						/******************************************
						 * Iterate thru all the Records which 
						 * are marked as cancel, then retrieve 
						 * the ID and use it to reference Objects
						 * in the Queue to remove them.
						 *****************************************/
						for (int i=0; i<vSelectedRec.size(); i++) {
							int cancelRecID = Integer.parseInt(vSelectedRec.elementAt(i) + "");
							
							//Set null to records which are marked for cancel
							vUploadQueue.setElementAt(null, cancelRecID);
							
							//Reset the ID of all the Elements behind (if exist) to ID - 1
							for (int j=cancelRecID+1; j<vUploadQueue.size(); j++) {
								Object [] tmp = (Object [])vUploadQueue.elementAt(j);
								tmp [0] = new Integer(Integer.parseInt(tmp [0] + "") - 1);
							}
						}
						
						/**************************************
						 * This part is for housekeeping, as 
						 * we need to remove the Vector whose 
						 * value is null
						 *************************************/
						for (int i=0; i<vUploadQueue.size(); i++) {
							if (vUploadQueue.elementAt(i) == null) {
								vUploadQueue.removeElementAt(i);
								i--;
							}
						}
						
						if (vUploadQueue.size() > 0) {
							//Re-convert the remaining records in the Upload Queue (Vector) to a 2-Dimensional Object array
							rmiDebug.setDebugText("Re-onvert remaining Objects in Upload Queue (Vector) to 2-Dimensional Object array");
							
							Object [][] newData = new Object [vUploadQueue.size()][6];
							for (int i=0; i<vUploadQueue.size(); i++) {
								Object [] tmp = (Object [])vUploadQueue.elementAt(i);
								
								for (int j=0; j<tmp.length; j++) {
									newData [i][j] = tmp [j];
								}
							}
							
							openQueue(newData);
							
						} else {
							//Re-create an empty Object Array
							Object [][] newData = new Object [vUploadQueue.size()][6];
							openQueue(newData);
						}
					}
				}
			});
			
			JButton jbtnDeleteAll = new JButton("Cancel All");
			jbtnDeleteAll.setToolTipText("Click this button to cancel all the objects from queue");
			jbtnDeleteAll.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae) {
					jif.setVisible(false);
					jif.dispose();
					
					//Clear all the Objects in the Vector
					vUploadQueue.removeAllElements();
					
					//Re-create an empty Object Array
					Object [][] newData = new Object [vUploadQueue.size()][6];
					openQueue(newData);
				}
			});
			
			//Disable all the operational buttons if null record
			if (data.length <= 0) {
				jbtnUpload.setEnabled(false);
				jbtnDelete.setEnabled(false);
				jbtnDeleteAll.setEnabled(false);
			}
			
			jp.add(jbtnUpload);
			jp.add(jbtnRefresh);
			jp.add(jbtnDelete);
			jp.add(jbtnDeleteAll);
			
			//----- Put the JPanel into a JScrollPane -----
			JScrollPane jspDetail = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jspDetail.setPreferredSize(new Dimension(450,50));
			jspDetail.getViewport().add(jp);
			//---------------------------------------------
			
			//----- Add all the Panes to the JInternalFrame -----
			jif.getContentPane().add(jsp, BorderLayout.CENTER);
			jif.getContentPane().add(jspDetail, BorderLayout.SOUTH);
			//---------------------------------------------------
			
			Point p = jdp.getLocationOnScreen();	
			//Calulation the position of JInternalFrame
			int x = 0;
			int y = 0;
			x = jdp.getWidth()/2 - jif.getWidth()/2;
			y = jdp.getHeight()/2 - jif.getHeight()/2;	
			jif.setLocation(x,y);
			
			jif.setVisible(true);
			jdp.add(jif);
			jif.setSelected(true);
			jif.show();
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	public class LocalViewThread extends Thread implements TreeExpansionListener,TreeModelListener {
		RMIClientGUI_TransferLocalView rmiGUI_TLV;
		RMIClient_FileTransferLocal rmi_FT;
		LocalViewThread LVT_Ptr;
		String srcLocation = "";
		String destLocation = "";
		DefaultMutableTreeNode srcNode = null;
		JMenuItem paste;
		
		public LocalViewThread(final RMIClientGUI_TransferLocalView rmiGUI_TLV) {
			LVT_Ptr = this;
			this.rmiGUI_TLV = rmiGUI_TLV;
			this.rmi_FT = new RMIClient_FileTransferLocal(rmiGUI_FT.serverIP, 
													rmiGUI_FT.serverPort, 
													rmiGUI_FT.serverShell, 
													rmiGUI_TLV);
			
			try {
				//====== Initializd the Global variable for JTree ======
				root = new DefaultMutableTreeNode("Local File System");
				dtm = new DefaultTreeModel(root);
				dtm.addTreeModelListener(this);
				jtree = new JTree (dtm);
				jtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
				jtree.setShowsRootHandles(true);
				dtm.setAsksAllowsChildren(true);
				
				ImageIcon leafIcon = new ImageIcon(this.getClass().getResource("image/fileIcon.jpg"));
				ImageIcon nodeIcon = new ImageIcon(this.getClass().getResource("image/folderIcon.jpg"));
				if (leafIcon!=null && nodeIcon!=null) {
					DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
						ImageIcon driveIcon = new ImageIcon(this.getClass().getResource("image/driveIcon.gif"));
						ImageIcon rootIcon = new ImageIcon(this.getClass().getResource("image/computer.gif"));
						
						public Component getTreeCellRendererComponent(
							JTree tree,
							Object value,
							boolean sel,
							boolean expanded,
							boolean leaf,
							int row,
							boolean hasFocus
						) {
							super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
							
							if (!leaf && isDrive(value)) {
								setIcon(driveIcon);
							}
							if (!leaf && isRoot(value)) {
								setIcon(rootIcon);
							}
							
							return this;
						}
						
						protected boolean isDrive(Object value) {
							String tmp = value + "";
							
							if(tmp.indexOf(":")==1 || tmp.equals("/")) {
								return true;
							}
							
							return false;
						}
						
						protected boolean isRoot(Object value) {
							String tmp = value + "";
							
							if(tmp.equals("Local File System") || tmp.equals("Remote File System")) {
								return true;
							}
							
							return false;
						}
					};
					
					renderer.setLeafIcon(leafIcon);
					renderer.setOpenIcon(nodeIcon);
					renderer.setClosedIcon(nodeIcon);
					jtree.setCellRenderer(renderer);
				}
				//======================================================
				
				//====== Add TreeSelectionListener to the JTree ======
				jtree.addTreeSelectionListener(new TreeSelectionListener() {
					public void valueChanged (TreeSelectionEvent tse) {
						TreePath path = tse.getPath();
						//System.out.println ("Selected: " + path.getLastPathComponent()); //Debugger
						Object elements [] = path.getPath();
						
						openDetail();
					}
				});
				//====================================================
				
				//====== Add Mouse Listener to the JTree ======
 				MouseListener ml = new MouseAdapter() {
 					public void mouseClicked(MouseEvent me) {
 						int selRow = jtree.getRowForLocation(me.getX(), me.getY());
 						
 						if(selRow != -1) {
 							if(me.getClickCount() == 1) {
 								//refresh();
 							} else if(me.getClickCount() == 2) {
 								//Ignore this part, cause we let the TreeExpaned Listener to handle it
 							}
 						}
 					}
 				};
 				jtree.addMouseListener(ml);
				//=============================================
				
				//====== Initialize the Popup Menu ======
				final JPopupMenu jpm = new JPopupMenu();
				
				final JMenuItem edit = new JMenuItem("Edit");
				edit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						new EditFileThread(rmiGUI_TLV, LVT_Ptr).start();
					}
				});
				
				JMenu upload = new JMenu("Upload");
					JMenu instantUpload = new JMenu("Instant Upload");
						JMenuItem instantUpload_Bin = new JMenuItem("Binary");
						instantUpload_Bin.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								new uploadThread(rmiGUI_TLV, rmi_FT, LVT_Ptr, "bin").start();
							}
						});
						JMenuItem instantUpload_Ascii = new JMenuItem("ASCII");
						instantUpload_Ascii.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								new uploadThread(rmiGUI_TLV, rmi_FT, LVT_Ptr, "ascii").start();
							}
						});
					instantUpload.add(instantUpload_Bin);
					instantUpload.add(instantUpload_Ascii);
					
					JMenu delayUpload = new JMenu("Delay Upload");
						JMenuItem delayUpload_Bin = new JMenuItem("Binary");
						delayUpload_Bin.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								delayUpload_Bin();
							}
						});
						JMenuItem delayUpload_Asc = new JMenuItem("ASCII");
						delayUpload_Asc.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								delayUpload_Ascii();
							}
						});
					delayUpload.add(delayUpload_Bin);
					delayUpload.add(delayUpload_Asc);
					
				upload.add(instantUpload);
				upload.add(delayUpload);
				
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						copy();
					}
				});
				JMenuItem cut = new JMenuItem("Cut");
				cut.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						cut();
					}
				});
				paste = new JMenuItem("Paste");
				paste.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (srcNode != null) {
							new MovingFileObjectThread(rmiGUI_TLV, rmi_FT, LVT_Ptr).start();
						}
					}
				});
				JMenuItem create = new JMenuItem("Create");
				create.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						create();
					}
				});
				JMenuItem delete = new JMenuItem("Delete");
				delete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						new DeleteFileObjectThread(rmiGUI_TLV, rmi_FT, LVT_Ptr).start();
						//delete();
					}
				});
				JMenuItem rename = new JMenuItem("Rename");
				rename.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rename();
					}
				});
				JMenuItem detail = new JMenuItem("Detail");
				detail.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						//detail();
						new LoadFileSizeThread(LVT_Ptr).start();
					}
				});
				JMenuItem refresh = new JMenuItem("Refresh");
				refresh.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						refresh();
					}
				});
				
				jpm.add(edit);
				jpm.addSeparator();
				jpm.add(upload);
				jpm.addSeparator();
		        jpm.add(copy);
		        jpm.add(cut);
		        jpm.add(paste);
		        jpm.addSeparator();
		        jpm.add(create);
		        jpm.add(delete);
		        jpm.add(rename);
		        jpm.addSeparator();
		        jpm.add(detail);
		        jpm.addSeparator();
		        jpm.add(refresh);
		        jpm.addSeparator();
		        
				if (srcLocation.length() <= 0) {
					paste.setEnabled(false); //Disable the paste function if no selected file object previously
				}
				//=======================================
				
				//====== Add MouseListener to enable Popup Menu when right click ======
				jtree.addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent me) {};
					public void mouseEntered(MouseEvent me) {};
					public void mouseExited(MouseEvent me) {};
					public void mousePressed(MouseEvent me) {showPopup(me);};
					public void mouseReleased(MouseEvent me) {showPopup(me);};
					private void showPopup(MouseEvent me) {
						if (me.isPopupTrigger()) {
							TreePath selectedPath = jtree.getPathForLocation(me.getX(), me.getY());
							jtree.setSelectionPath(selectedPath);
							
							if (selectedPath != null) {
								DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
								MutableTreeNode parent = (MutableTreeNode)(selectedNode.getParent());
								
								/**************************************************************
								 * Disable the Edit option if the selected Node is a folder
								 *************************************************************/
								if (selectedNode.getPath().length <= 2) {
									edit.setEnabled(false);
									
								} else {
									TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
							       	arr_node = selectedNode.getPath();
									String location = "";
									
									for (int j=0; j<arr_node.length; j++) {
										if ((arr_node [j] + "").equals("Local File System") == false) {
											//System.out.println(arr_node [j]); //Debugger
											location += arr_node [j] + "/";
										}
									}
									
									Vector vFileInfo = rmi_FT.getFileInfo(location);
									
									//Test if remote drive is really ready
									if (vFileInfo == null) {
										warning("Error Detected! Please check whether:\n" + 
												"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
												"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
												"3. Drive has enough disk space\n");
									} else {
										edit.setEnabled(false);
										
										if ((vFileInfo.elementAt(4)+"").equals("true")) {
											//Check if it is a Directory
											edit.setEnabled(false);
											
										} else {
											//Check if it is a File
											edit.setEnabled(true);
										}
									}
								}
							}
							
							jpm.show(me.getComponent(), me.getX(), me.getY());
						}
					}
				});
				//=====================================================================
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		public void run() {
			String [] drive;
			
			try {
				drive = rmi_FT.openFileSystem();
				
				if (drive != null) {
					DefaultMutableTreeNode tmp;
					
					rmiDebug.setDebugText("Available drives on Local File System: ");
					for (int i=0; i<drive.length; i++) {
						rmiDebug.setDebugText(drive [i]); //Debugger
						tmp = new DefaultMutableTreeNode(drive [i] + "");
						tmp.setAllowsChildren(true);
						root.add(tmp);
					}
					
					jtree.addTreeExpansionListener(this);
					
					jspLocalView = new JScrollPane(jtree);
					
					//====== Reload the File Transfer Panel ======
					rmiGUI_TLV.removeAll();
					rmiGUI_TLV.setLayout(new BorderLayout());
					rmiGUI_TLV.add(jspLocalView, BorderLayout.CENTER);
					rmiGUI_TLV.add(jpSouth, BorderLayout.SOUTH);
					jpSouth.setVisible(true);
					rmiGUI_TLV.setPreferredSize(new Dimension(280, 250));
					rmiGUI_TLV.updateUI();
					rmiGUI_TLV.revalidate();
					//============================================
					
				} else {
					//Nothing to do...
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		//====== Put all the File Operations here ======
		private void openDetail() {
			try {
				// To retrieve elements inside the seleted node
		        DefaultMutableTreeNode parentNode = null;
		        TreePath parentPath = jtree.getSelectionPath();
				String location = "";
				TreeNode arr_node [];
				
				rmiDebug.setDebugText("Selected path: " + parentPath);
				
				//Make sure the selectedPath is not null
				if (parentPath != null) {
			        if ((parentPath + "").equals("[root]") == true) {
			            parentNode = root;
			            
			        } else {
			            parentNode = (DefaultMutableTreeNode)(parentPath.getLastPathComponent());
			            DefaultMutableTreeNode childNode;
			            
						arr_node = new TreeNode [parentNode.getLevel()+1];
						arr_node = parentNode.getPath();
						
						for (int j=0; j<arr_node.length; j++) {
				            if ((arr_node [j] + "").equals("Local File System") == false) {
					           	//System.out.println(arr_node [j]); //Debugger
					           	location += arr_node [j] + "/";
				            }
						}
						 
						//Optional Debugger
						rmiDebug.setDebugText("Convert file path string: " + location);
						
						//Set a pointer on the current selected path for external referencing
						if (location.equals("") == false) {
							localViewPath = location;
							localViewPath = localViewPath.substring(0, localViewPath.length()-1); //remove the last "/" character
						}
						
						//Check if it is a folder
						Vector vFileInfo = rmi_FT.getFileInfo(location);
						if ((vFileInfo.elementAt(4)+"").equals("true")) {
							String [][] ls = rmi_FT.getFileList(location);
							rmiDebug.setDebugText("Folder detail retrieval complete.");
						}
			        }
				}

			} catch (NullPointerException npe) {
				warning("Error Detected! Please check whether:\n" + 
						"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
						"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
						"3. Drive has enough disk space\n");
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void edit(final EditFileThread eft) {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				
				if (selectedPath != null) {
					final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					if (selectedNode.getPath().length != 1) {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
			          	arr_node = selectedNode.getPath();
						String location = "";
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						//Optional Debugger
						rmiDebug.setDebugText("Selected Object to edit : " + location);
						Vector vFileInfo = eft.rmi_FTL.getFileInfo(location);
						
						//Check if the remote drive is really ready
						if (vFileInfo == null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							//Validate if the temp location exist, if not, create it.
							rmiDebug.setDebugText("Validate Temp Space");
							eft.rmi_FTL.validateTempSpace();
							
							final String fileName = vFileInfo.elementAt(0) + "";
							final String tempLocation = System.getProperty("user.home") + File.separator + "RMIAdmin" + File.separator + fileName;
							final String srcLocation = location;
							
							//Optional Debugger
							rmiDebug.setDebugText("Copy (Ascii) - From " + srcLocation + " to " + tempLocation + " for editing");
							eft.rmi_FTL.copyAndPaste(srcLocation, System.getProperty("user.home") + File.separator + "RMIAdmin");
							
							RMIClientGUI_FileEditor rmiGUI_FE = new RMIClientGUI_FileEditor(jdp, rmiDebug, rmiGUI_TLV, eft.rmi_FTR, eft.rmi_FTL, srcLocation, tempLocation, "local");
							rmiGUI_FE.setFile(tempLocation);
							rmiGUI_FE.setBuffer(1024000);
							rmiGUI_FE.init();
							rmiGUI_FE.loadFile();
						}	
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void copy() {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					MutableTreeNode parent = (MutableTreeNode)(selectedNode.getParent());
					
					/**************************************************************
					 * Check whether the action happens at Root node,
					 * if yes, ignore the request
					 *************************************************************/
					if (selectedNode.getPath().length == 2) {
						//Nothing to do...
					} else {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
				       	arr_node = selectedNode.getPath();
						String location = "";
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(location);
						
						//Test if remote drive is really ready
						if (vFileInfo == null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							srcLocation = location;
							
							//Set the paste type
							filePasteType = "CopyAndPaste";
							
							//Set a pointer to the source node
							srcNode = selectedNode;
							
							//Optional Debugger
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								rmiDebug.setDebugText("Copied Folder: " + location);
							} else {
								rmiDebug.setDebugText("Copied File: " + location);
							}
						}
					}
				}
				
	        } catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
	        }
	        
			//Enable the paste function
			if (srcLocation.length() > 0) {
				paste.setEnabled(true);
			}
		}
		
		private void cut() {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					MutableTreeNode parent = (MutableTreeNode)(selectedNode.getParent());
					
					/**************************************************************
					 * Check whether the action happens at Root node,
					 * if yes, ignore the request
					 *************************************************************/
					if (selectedNode.getPath().length == 2) {
						//Nothing to do...
					} else {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
				       	arr_node = selectedNode.getPath();
						String location = "";
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(location);
						
						//Test if remote drive is really ready
						if (vFileInfo == null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							srcLocation = location;
							
							//Set the paste type
							filePasteType = "CutAndPaste";
							
							//Set a pointer to the source node
							srcNode = selectedNode;
							
							//Optional Debugger
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								rmiDebug.setDebugText("Cut Folder: " + location);
							} else {
								rmiDebug.setDebugText("Cut File: " + location);
							}
						}
					}
				}
				
	        } catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
	        }
	        
			//Enable the paste function
			if (srcLocation.length() > 0) {
				paste.setEnabled(true);
			}
		}
		
		private void paste(MovingFileObjectThread mfoT) {
			DefaultMutableTreeNode srcNode = this.srcNode;
			
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					MutableTreeNode parent = (MutableTreeNode)(selectedNode.getParent());
					
					/**************************************************************
					 * Check whether the action happens at Root node,
					 * if yes, ignore the request
					 *************************************************************/
					if (selectedNode.getPath().length == 1) {
						//Nothing to do...
					} else {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
				       	arr_node = selectedNode.getPath();
						String location = "";
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(location);
						
						//Test if remote drive is really ready
						if (vFileInfo == null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							destLocation = location;
							
							//Optional Debugger
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								rmiDebug.setDebugText("Target Folder: " + location);
								
								/******************************************************
								 * Perform the "copy & paste" and "cut & paste" 
								 * on Remote File System
								 * 
								 * Make sure the destination is a folder, otherwise 
								 * copy will be denied.
								 *
								 * Make sure the source and destination are different, 
								 * otherwise copy will be deinied.
								 *
								 * Make sure the Copied Node or Deleted Node is not 
								 * parent of the Target Node
								 *
								 * Make sure if source file name is the same as target
								 * filename, a message prompt will be shown to seek the 
								 * overwrite confirmation from users.
								 *
								 ******************************************************/
								String srcPath = "";
								String srcFile = "";
								if (srcLocation.indexOf("/") >= 0) {
									srcPath = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
									srcPath = srcPath.substring(0,srcPath.lastIndexOf("/")+1);
									
									srcFile = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
									srcFile = srcFile.substring(srcFile.lastIndexOf("/")+1, srcFile.length());
								}
								
								//System.out.println("is Descendent " + srcNode.isNodeDescendant(selectedNode)); //Debugger
								
								if (srcPath.equals(destLocation)!=true && srcNode.isNodeDescendant(selectedNode)==false) {
									/**************************************
									 * Check if the src file name & target
									 * file name is equal.
									 *************************************/
									boolean duplicate = rmi_FT.checkDuplicateTarget(srcFile, destLocation);
									boolean overwrite = false;
									
									if (duplicate == true) {
										rmiDebug.setDebugText("Duplicated File Exist = " + duplicate);
										overwrite = warning(srcFile + " exist!&nbsp;Overwrite?\n\n" +
															"Click <font color='blue'>[OK]</font> to overwrite.\n" + 
															"Click <font color='red'>[Cancel]</font> to abort."
															);
															
										rmiDebug.setDebugText("Overwrite: " + overwrite);
										
									} else {
										overwrite = true;
									}
									
									/***************************************
									 * Count the total object in source 
									 * location, this total count will 
									 * later be used in Threading,
									 **************************************/
									if (overwrite == true) {
										 int totalObject = rmi_FT.countObject(srcLocation);
										 rmiDebug.setDebugText("Total Object: " + totalObject);
										 
										 //Set the destLocation for the Thread Monitor
										 mfoT.destLocation = destLocation + srcFile;
										 
										 //Set the message
										 if (filePasteType.equals("CopyAndPaste")) {
										 	mfoT.jlbMessage.setText("<html><font color='#333333'>&nbsp;Copying " + srcFile + "...</font></html>");
										 } else {
										 	mfoT.jlbMessage.setText("<html><font color='#333333'>&nbsp;Moving " + srcFile + "...</font></html>");
										 }
										 
										 //Set the totalObject as the Max value of JProgressBar in the Thread
										 JProgressBar jpb = mfoT.getProgressBar();
										 jpb.setMaximum(totalObject);
										 if (totalObject == 0) {
										 	jpb.setStringPainted(false);
										 } else {
										 	jpb.setStringPainted(true);
										 }
										 
										 //Start the Thread Monitor
										 mfoT.startMonitor();
									}
									
									/**************************************
									 * Check if the operation is 
									 * "copy & paste" or "cut & paste"
									 *************************************/
									String complete = "false";
									if (filePasteType.equals("CopyAndPaste") && overwrite==true) {
										complete = rmi_FT.copyAndPaste(srcLocation, destLocation);
										
										if (complete.equals("true")) {
											rmiDebug.setDebugText("Copy and Paste result: " + complete);
											
											//Refresh the destination path
											refresh();
											
										} else {
											rmiDebug.setDebugText("Copy and Paste result: False");
											rmiDebug.setDebugText("=============================================");
											rmiDebug.setDebugText(complete);
											warning("Error!<br>Copy and Paste denied. Please check whether:\n" + 
													"1. Other application is accessing the same resource\n" + 
													"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
													"3. Drive has enough disk space\n");
										}
										
										mfoT.stop = true;
										
									} else if (filePasteType.equals("CutAndPaste") && overwrite==true) {
										complete = rmi_FT.cutAndPaste(srcLocation, destLocation);
										
										if (complete.equals("true")) {	
											//Remove cut-off node from JTree
											if (parent != null) {
												dtm.removeNodeFromParent(srcNode);
											}
											rmiDebug.setDebugText("Cut and Paste result: " + complete);
											
											//Refresh the destination path
											refresh();
											
										} else {
											rmiDebug.setDebugText("Cut and Paste result: False");
											rmiDebug.setDebugText("=============================================");
											rmiDebug.setDebugText(complete);
											warning("Error!<br>Cut and Paste denied. Please check whether:\n" + 
													"1. Other application is accessing the same resource\n" + 
													"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
													"3. Drive has enough disk space\n");
										}
										
										mfoT.stop = true;
									}
								}
								
								if (srcNode.isNodeDescendant(selectedNode) == true) {
									warning("Error!<br>Target Location cannot be sub-folder of Source Location");
									mfoT.stop = true;
								}
								
								if (srcPath.equals(destLocation) == true) {
									warning("Error!<br>Target Location cannot be the same as Source Location");
									mfoT.stop = true;
								}
								
								//Disable the paste function
								srcLocation = "";
								paste.setEnabled(false);
							}
						}
					}
				}
				
	        } catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
	        }
		}
		
		private void create() {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					/**************************************************************
					 * Check whether the action happens at Root node,
					 * if yes, ignore the request
					 *************************************************************/
					if (selectedNode.getPath().length == 1) {
						//Nothing to do...
					} else {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
				       	arr_node = selectedNode.getPath();
						String location = "";
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(location);
						
						//Test if remote drive is really ready
						if (vFileInfo == null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							/**********************************************
							 * Test whether it is a directory or file, 
							 * if file the action will abandon
							 *********************************************/
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								//System.out.println("is Directory"); //Debugger
								String fileInfo = createFileType();
								
								//Make sure filename and filetype has been input before creation
								if (fileInfo.equals("") == false) {
									String fileType = fileInfo.substring(0, fileInfo.indexOf("###"));
									String fileName = fileInfo.substring(fileInfo.indexOf("###")+3, fileInfo.length());
									
									location += fileName + "/";
									
									//Optional Debugger
									rmiDebug.setDebugText("Convert file path string: " + location);
									
									//Create the object on remote file system
									String complete = rmi_FT.createFile(location, fileType);
									if (complete.equals("true")) {
										rmiDebug.setDebugText("Creation result: " + complete);
										
										//Refresh the current path
										refresh();
											
									} else {
										rmiDebug.setDebugText("Creation result: False");
										rmiDebug.setDebugText("=============================================");
										rmiDebug.setDebugText(complete);
										warning("Error!<br>File/Directory creation denied. Please check whether:\n" + 
												"1. Other application is accessing the same resource\n" + 
												"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
												"3. Drive has enough disk space\n");
									}
								}
							}
						}
					}
				}
				
	        } catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
	        }
		}
		
		private void delete(DeleteFileObjectThread dfoT) {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					MutableTreeNode parent = (MutableTreeNode)(selectedNode.getParent());
					TreePath parentPath = selectedPath.getParentPath();
					
					/**************************************************************
					 * Check whether the action happens at Root node,
					 * if yes, ignore the request
					 *************************************************************/
					if (selectedNode.getPath().length == 2) {
						//Nothing to do...
					} else {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
				       	arr_node = selectedNode.getPath();
				       	String srcFile = arr_node [arr_node.length - 1] + "";
						String location = "";
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(location);
						
						//Test if remote drive is really ready
						if (vFileInfo == null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							boolean delete = warning("Are you sure to delete " + srcFile + "?");
							
							if (delete == true) {
								rmiDebug.setDebugText("Deletion Confirm.");
								
								int totalObject = rmi_FT.countObject(location);
								rmiDebug.setDebugText("Total Object: " + totalObject);
								
								//Set the destLocation for the Thread Monitor
								dfoT.destLocation = location;
								dfoT.jlbMessage.setText("<html><font color='#333333'>&nbsp;Deleting " + vFileInfo.elementAt(0) + "...</font></html>");
								
								//Set the totalObject as the Max value of JProgressBar in the Thread
								JProgressBar jpb = dfoT.getProgressBar();
								jpb.setMaximum(totalObject);
								if (totalObject == 0) {
									jpb.setStringPainted(false);
								} else {
									jpb.setStringPainted(true);
								}
								
								//Start the Thread Monitor
								dfoT.startMonitor();
								
								//Remove node from JTree
								if (parent != null) {
									dtm.removeNodeFromParent(selectedNode);
								}
								
								if ((vFileInfo.elementAt(4)+"").equals("true")) {
									rmiDebug.setDebugText("Selected Folder to delete: " + location);
								} else {
									rmiDebug.setDebugText("Selected File to delete: " + location);
								}
								
								//Perform deletion on remote file system
								String complete = "false";
								complete = rmi_FT.deleteFile(location);
								
								//Stop the progress
								dfoT.stop = true;
								
								if (complete.equals("true")) {
									rmiDebug.setDebugText("Deletion result: " + complete);
									
								} else {
									rmiDebug.setDebugText("Deletion result: False");
									rmiDebug.setDebugText("=============================================");
									rmiDebug.setDebugText(complete);
									warning("Error!<br>File/Directory deletion denied. Please check whether:\n" + 
											"1. Other application is accessing the same resource\n" + 
											"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
											"3. Drive has enough disk space\n");
									
								}
								
								if (complete.equals("true")) {
									//Refresh the deleted file's parent path
									jtree.setSelectionPath(parentPath);
									refreshLocalView();
								}
								
							} else {
								rmiDebug.setDebugText("Deletion Abort.");
							}
						}
					}
				}
				
	        } catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
	        }
		}
		
		private void rename() {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					/**************************************************************
					 * Check whether the action happens at Root node,
					 * if yes, ignore the request
					 *************************************************************/
					if (selectedNode.getPath().length == 2) {
						//Nothing to do...
					} else {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
				       	arr_node = selectedNode.getPath();
						String srcLocation = "";
						String destLocation = "";
						String srcFile = arr_node [arr_node.length - 1] + "";
						//System.out.println(srcFile); //Debugger
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								srcLocation += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(srcLocation);
						
						//Test if remote drive is really ready
						if (vFileInfo == null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							String newFileName = renameFile(srcFile);
							
							if (newFileName.equals("") == false) {
								destLocation = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
								destLocation = destLocation.substring(0,destLocation.lastIndexOf("/")+1);
								destLocation = destLocation + newFileName + "/";
								
								//Optional Debugger
								if ((vFileInfo.elementAt(4)+"").equals("true")) {
									rmiDebug.setDebugText("About to rename folder " + srcLocation + " to " + destLocation);
								} else {
									rmiDebug.setDebugText("About to rename file " + srcLocation + " to " + destLocation);
								}
								
								//Rename the object on remote file system
								String complete = rmi_FT.renameFile(srcLocation, destLocation);
								if (complete.equals("true")) {
									rmiDebug.setDebugText("Renaming result: " + complete);
									
									/*******************************************
									 * Automatically refresh the file system at 
									 * one level up the current selected node
									 ******************************************/
									DefaultMutableTreeNode parent = (DefaultMutableTreeNode)(selectedNode.getParent());
									TreePath parentPath = new TreePath(parent.getPath()); //jtree.getSelectionPath();
									
									if (parent != null) {
										dtm.removeNodeFromParent(selectedNode);
									} 
									
									jtree.setSelectionPath(parentPath);
									
									//Here is where refresh occur
									refresh();
									
								} else {
									rmiDebug.setDebugText("Renaming result: False");
									rmiDebug.setDebugText("=============================================");
									rmiDebug.setDebugText(complete);
									warning("Error!<br>File/Directory renaming denied. Please check whether:\n" + 
											"1. Other application is accessing the same resource\n" + 
											"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
											"3. Drive has enough disk space\n");
								}
							}
						}
					}
				}
				
	        } catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
	        }
		}
		
		private void detail() {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				rmiDebug.setDebugText("Get detail for path: " + selectedPath); //debugger
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					if (selectedNode.getPath().length != 1) {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
			          	arr_node = selectedNode.getPath();
						String location = "";
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						//Optional Debugger
						rmiDebug.setDebugText("Open detail for: " + location);
						Vector vFileInfo = rmi_FT.getFileInfo(location);
						
						if ((vFileInfo.elementAt(4)+"").equals("true")) {
							rmiDebug.setDebugText("Getting size of: " + location + " recursively");
							
							//Get folder size recursively
							long sizeInLong = rmi_FT.getFileSize(location);
							
							//Replace the file size with rounded values
							String roundedSize = rmi_FT.getRoundedFileSize(sizeInLong);
							vFileInfo.setElementAt(roundedSize, 10);
							
						} else {
							//Replace the file size with rounded values
							String roundedSize = rmi_FT.getRoundedFileSize(location);
							vFileInfo.setElementAt(roundedSize, 10);
						}
						
						//Check if the remote drive is really ready
						if (vFileInfo == null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							showFileInfo(vFileInfo);
						}
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		public void refresh() {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				rmiDebug.setDebugText("Refresh path: " + selectedPath); //debugger
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					/**************************************************************
					 * Check whether the action happens at Root node,
					 * if no, do the refresh by getUpLevel(), then getDownLevel()
					 * if yes, do the refresh by openFileSystem()
					 *************************************************************/
					if (selectedNode.getPath().length == 1) {
						openFileSystem();
						
					} else {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
			          	arr_node = selectedNode.getPath();
						String location = "";
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(location);
						
						//Make sure we're refreshing a folder, otherwise error may occur
						if ((vFileInfo.elementAt(4)+"").equals("true")) {
							LocalViewProgressThread progress;
							
							if (selectedNode.isLeaf() == false) {
								//Remove then Re-add all the childs in current path
								progress = new LocalViewProgressThread(rmiGUI_TLV, rmi_FT, this, "up");
								progress.start();
							} else {
								//Remove then Re-add all the childs in current path
								progress = new LocalViewProgressThread(rmiGUI_TLV, rmi_FT, this, "down");
								progress.start();
							}
							
						} else {
							//Nothing to do...
						}
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		public void refreshUpOneLevel() {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				rmiDebug.setDebugText("Refresh path: " + selectedPath); //debugger
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					/**************************************************************
					 * Check whether the action happens at Root node,
					 * if no, do the refresh by getUpLevel(), then getDownLevel()
					 * if yes, do the refresh by openFileSystem()
					 *************************************************************/
					if (selectedNode.getPath().length == 1) {
						openFileSystem();
						
					} else {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
			          	arr_node = selectedNode.getPath();
						String location = "";
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(location);
						
						//Make sure we're refreshing a folder, otherwise error may occur
						if ((vFileInfo.elementAt(4)+"").equals("true")) {
							LocalViewProgressThread progress;
							
							if (selectedNode.isLeaf() == false) {
								//Remove then Re-add all the childs in current path
								progress = new LocalViewProgressThread(rmiGUI_TLV, rmi_FT, this, "up");
								progress.start();
							} else {
								//Remove then Re-add all the childs in current path
								progress = new LocalViewProgressThread(rmiGUI_TLV, rmi_FT, this, "down");
								progress.start();
							}
							
						} else {
							DefaultMutableTreeNode parent = (DefaultMutableTreeNode)(selectedNode.getParent());
							TreePath parentPath = new TreePath(parent.getPath()); //jtree.getSelectionPath();
							
							if (parent != null) {
								dtm.removeNodeFromParent(selectedNode);
							} 
							
							jtree.setSelectionPath(parentPath);
							
							//Now invoke refresh again
							refresh();
						}
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void instantUpload_Bin(uploadThread ut) {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				rmiDebug.setDebugText("Get detail for path: " + selectedPath); //debugger
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					if (selectedNode.getPath().length != 1) {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
			          	arr_node = selectedNode.getPath();
						String srcLocation = "";
						String destLocation = rmiGUI_FT.getRemoteViewPath();
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								srcLocation += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(srcLocation);
						Vector vFileInfo2 = rmi_FT.getRemoteFileInfo(destLocation);
						
						//Check if the remote drive is really ready
						if (vFileInfo==null || vFileInfo2==null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								//Source Object is Folder
								destLocation = destLocation + rmi_FT.getFileSeparator() + vFileInfo.elementAt(0);
								
							} else {
								//Source Object is File, append the file name at to destLocation
								destLocation = destLocation + rmi_FT.getFileSeparator() + vFileInfo.elementAt(0);
							}
							
							String srcPath = "";
							String srcFile = "";
							if (srcLocation.indexOf("/") >= 0) {
								srcPath = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
								srcPath = srcPath.substring(0,srcPath.lastIndexOf("/")+1);
								
								srcFile = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
								srcFile = srcFile.substring(srcFile.lastIndexOf("/")+1, srcFile.length());
							}
							
							/**************************************
							 * Check if the src file name & target
							 * file name is equal.
							 *************************************/
							boolean duplicate = rmi_FT.checkRemoteDuplicateTarget(srcFile, rmiGUI_FT.getRemoteViewPath());
							boolean overwrite = false;
							
							if (duplicate == true) {
								rmiDebug.setDebugText("Duplicated File Exist = " + duplicate);
								overwrite = warning(srcFile + " exist!&nbsp;Overwrite?\n\n" +
													"Click <font color='blue'>[OK]</font> to overwrite.\n" + 
													"Click <font color='red'>[Cancel]</font> to abort."
													);
								
								rmiDebug.setDebugText("Overwrite: " + overwrite);
								
							} else {
								overwrite = true;
							}
							
							if (overwrite == true) {
								//Optional Debugger
								rmiDebug.setDebugText("Upload (Binary) - From " + srcLocation + " to " + destLocation);
								
								int totalObject = rmi_FT.countObject(srcLocation);
								rmiDebug.setDebugText("Total Object: " + totalObject);
								
								//Set the message
								ut.jlbMessage.setText("<html><font color='#333333'>&nbsp;Uploading " + srcFile + "...</font></html>");
								
								//Set the destLocation for the Thread Monitor
								ut.destLocation = destLocation;
								
								//Set the totalObject as the Max value of JProgressBar in the Thread
								JProgressBar jpb = ut.getProgressBar();
								jpb.setMaximum(totalObject);
								if (totalObject == 0) {
									jpb.setStringPainted(false);
								} else {
								 	jpb.setStringPainted(true);
								}
								
								//Start the Thread Monitor
								ut.startMonitor();
								
								rmi_FT.uploadFile_Bin(srcLocation, destLocation);
								
								ut.stop = true;
								
								//Refresh the Remote View
								rmiGUI_FT.refreshRemoteView();
							}
						}
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void instantUpload_Ascii(uploadThread ut) {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				rmiDebug.setDebugText("Get detail for path: " + selectedPath); //debugger
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					if (selectedNode.getPath().length != 1) {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
			          	arr_node = selectedNode.getPath();
						String srcLocation = "";
						String destLocation = rmiGUI_FT.getRemoteViewPath();
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								srcLocation += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(srcLocation);
						Vector vFileInfo2 = rmi_FT.getRemoteFileInfo(destLocation);
						
						//Check if the remote drive is really ready
						if (vFileInfo==null || vFileInfo2==null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								//Source Object is Folder
								destLocation = destLocation + rmi_FT.getFileSeparator() + vFileInfo.elementAt(0);
								
							} else {
								//Source Object is File, append the file name at to destLocation
								destLocation = destLocation + rmi_FT.getFileSeparator() + vFileInfo.elementAt(0);
							}
							
							String srcPath = "";
							String srcFile = "";
							if (srcLocation.indexOf("/") >= 0) {
								srcPath = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
								srcPath = srcPath.substring(0,srcPath.lastIndexOf("/")+1);
								
								srcFile = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
								srcFile = srcFile.substring(srcFile.lastIndexOf("/")+1, srcFile.length());
							}
							
							/**************************************
							 * Check if the src file name & target
							 * file name is equal.
							 *************************************/
							boolean duplicate = rmi_FT.checkRemoteDuplicateTarget(srcFile, rmiGUI_FT.getRemoteViewPath());
							boolean overwrite = false;
							
							if (duplicate == true) {
								rmiDebug.setDebugText("Duplicated File Exist = " + duplicate);
								overwrite = warning(srcFile + " exist!&nbsp;Overwrite?\n\n" +
													"Click <font color='blue'>[OK]</font> to overwrite.\n" + 
													"Click <font color='red'>[Cancel]</font> to abort."
													);
								
								rmiDebug.setDebugText("Overwrite: " + overwrite);
								
							} else {
								overwrite = true;
							}
							
							if (overwrite == true) {
								//Optional Debugger
								rmiDebug.setDebugText("Upload (Ascii) - From " + srcLocation + " to " + destLocation);
								
								int totalObject = rmi_FT.countObject(srcLocation);
								rmiDebug.setDebugText("Total Object: " + totalObject);
								
								//Set the message
								ut.jlbMessage.setText("<html><font color='#333333'>&nbsp;Uploading " + srcFile + "...</font></html>");
								
								//Set the destLocation for the Thread Monitor
								ut.destLocation = destLocation;
								
								//Set the totalObject as the Max value of JProgressBar in the Thread
								JProgressBar jpb = ut.getProgressBar();
								jpb.setMaximum(totalObject);
								if (totalObject == 0) {
									jpb.setStringPainted(false);
								} else {
								 	jpb.setStringPainted(true);
								}
								
								//Start the Thread Monitor
								ut.startMonitor();
								
								rmi_FT.uploadFile_Ascii(srcLocation, destLocation);
								rmiGUI_FT.refreshRemoteView();
								
								ut.stop = true;
							}
						}
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void delayUpload_Bin() {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				rmiDebug.setDebugText("Get detail for path: " + selectedPath); //debugger
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					if (selectedNode.getPath().length != 1) {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
			          	arr_node = selectedNode.getPath();
						String srcLocation = "";
						String destLocation = rmiGUI_FT.getRemoteViewPath();
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								srcLocation += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(srcLocation);
						Vector vFileInfo2 = rmi_FT.getRemoteFileInfo(destLocation);
						
						//Check if the remote drive is really ready
						if (vFileInfo==null || vFileInfo2==null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								//Source Object is Folder
								destLocation = destLocation + rmi_FT.getFileSeparator() + vFileInfo.elementAt(0);
								
							} else {
								//Source Object is File, append the file name at to destLocation
								destLocation = destLocation + rmi_FT.getFileSeparator() + vFileInfo.elementAt(0);
							}
							
							String srcPath = "";
							String srcFile = "";
							if (srcLocation.indexOf("/") >= 0) {
								srcPath = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
								srcPath = srcPath.substring(0,srcPath.lastIndexOf("/")+1);
								
								srcFile = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
								srcFile = srcFile.substring(srcFile.lastIndexOf("/")+1, srcFile.length());
							}
							
							/**************************************
							 * Check if the src file name & target
							 * file name is equal.
							 *************************************/
							boolean duplicate = rmi_FT.checkRemoteDuplicateTarget(srcFile, rmiGUI_FT.getRemoteViewPath());
							boolean overwrite = false;
							
							if (duplicate == true) {
								rmiDebug.setDebugText("Duplicated File Exist = " + duplicate);
								overwrite = warning(srcFile + " exist!&nbsp;Overwrite?\n\n" +
													"Click <font color='blue'>[OK]</font> to overwrite.\n" + 
													"Click <font color='red'>[Cancel]</font> to abort."
													);
								
								rmiDebug.setDebugText("Overwrite: " + overwrite);
								
							} else {
								overwrite = true;
							}
							
							if (overwrite == true) {
								String fileType = "";
								if ((vFileInfo.elementAt(4)+"").equals("true")) {
									fileType = "Directory";
								} else {
									fileType = "File";
								}
								Long fileSize = new Long(vFileInfo.elementAt(10) + "");
								String uploadType = "Binary";
								
								Object [] obj = new Object [6];
								obj [0] = new Integer(vUploadQueue.size());
								obj [1] = srcLocation;
								obj [2] = fileType;
								obj [3] = fileSize;
								obj [4] = uploadType;
								obj [5] = destLocation;
								
								//Optional Debugger
								rmiDebug.setDebugText("Added to Upload Queue (Binary)\n" + 
														"=============================================\n" + 
														"Task ID: " + vUploadQueue.size() + "\n" + 
														"Source: " + srcLocation + "\n" + 
														"Destination: " + destLocation + "\n" + 
														"File Type: " + fileType + "\n" + 
														"File Size: " + fileSize + "\n" + 
														"Transfer Mode: " + uploadType + "\n" + 
														"=============================================");
								
								vUploadQueue.addElement(obj);
							}
						}
					}
				}
				
	        } catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
	        }
		}
		
		private void delayUpload_Ascii() {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				rmiDebug.setDebugText("Get detail for path: " + selectedPath); //debugger
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					if (selectedNode.getPath().length != 1) {
						TreeNode [] arr_node = new TreeNode [selectedNode.getLevel()+1];
			          	arr_node = selectedNode.getPath();
						String srcLocation = "";
						String destLocation = rmiGUI_FT.getRemoteViewPath();
						
						for (int j=0; j<arr_node.length; j++) {
							if ((arr_node [j] + "").equals("Local File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								srcLocation += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FT.getFileInfo(srcLocation);
						Vector vFileInfo2 = rmi_FT.getRemoteFileInfo(destLocation);
						
						//Check if the remote drive is really ready
						if (vFileInfo==null || vFileInfo2==null) {
							warning("Error Detected! Please check whether:\n" + 
									"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
									"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
									"3. Drive has enough disk space\n");
						} else {
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								//Source Object is Folder
								destLocation = destLocation + rmi_FT.getFileSeparator() + vFileInfo.elementAt(0);
								
							} else {
								//Source Object is File, append the file name at to destLocation
								destLocation = destLocation + rmi_FT.getFileSeparator() + vFileInfo.elementAt(0);
							}
							
							String srcPath = "";
							String srcFile = "";
							if (srcLocation.indexOf("/") >= 0) {
								srcPath = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
								srcPath = srcPath.substring(0,srcPath.lastIndexOf("/")+1);
								
								srcFile = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
								srcFile = srcFile.substring(srcFile.lastIndexOf("/")+1, srcFile.length());
							}
							
							/**************************************
							 * Check if the src file name & target
							 * file name is equal.
							 *************************************/
							boolean duplicate = rmi_FT.checkRemoteDuplicateTarget(srcFile, rmiGUI_FT.getRemoteViewPath());
							boolean overwrite = false;
							
							if (duplicate == true) {
								rmiDebug.setDebugText("Duplicated File Exist = " + duplicate);
								overwrite = warning(srcFile + " exist!&nbsp;Overwrite?\n\n" +
													"Click <font color='blue'>[OK]</font> to overwrite.\n" + 
													"Click <font color='red'>[Cancel]</font> to abort."
													);
								
								rmiDebug.setDebugText("Overwrite: " + overwrite);
								
							} else {
								overwrite = true;
							}
							
							if (overwrite == true) {
								String fileType = "";
								if ((vFileInfo.elementAt(4)+"").equals("true")) {
									fileType = "Directory";
								} else {
									fileType = "File";
								}
								Long fileSize = new Long(vFileInfo.elementAt(10) + "");
								String uploadType = "Ascii";
								
								Object [] obj = new Object [6];
								obj [0] = new Integer(vUploadQueue.size());
								obj [1] = srcLocation;
								obj [2] = fileType;
								obj [3] = fileSize;
								obj [4] = uploadType;
								obj [5] = destLocation;
								
								//Optional Debugger
								rmiDebug.setDebugText("Added to Upload Queue (Binary)\n" + 
														"=============================================\n" + 
														"Task ID: " + vUploadQueue.size() + "\n" + 
														"Source: " + srcLocation + "\n" + 
														"Destination: " + destLocation + "\n" + 
														"File Type: " + fileType + "\n" + 
														"File Size: " + fileSize + "\n" + 
														"Transfer Mode: " + uploadType + "\n" + 
														"=============================================");
								
								vUploadQueue.addElement(obj);
							}
						}
					}
				}
				
	        } catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
	        }
		}
		//==============================================
		
		//====== Required by TreeExpansionListener interface ======
		public void treeExpanded(TreeExpansionEvent e) {
	        try {
				rmiDebug.setDebugText("Tree-expanded event detected. " + e.getSource()); //Debugger
				jtree.setSelectionPath(e.getPath());
				//rmiDebug.setDebugText(jtree.getSelectionPath()); //Debugger
				
	        	LocalViewProgressThread progress = new LocalViewProgressThread(rmiGUI_TLV, rmi_FT, this, "down");
	        	progress.start();
	        	
	        	rmiDebug.setDebugText("============================================="); //Debugger
	        	
	        } catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
	        }
		}
		
		public void treeCollapsed(TreeExpansionEvent e) {
	        try {
				rmiDebug.setDebugText("Tree-collapsed event detected. " + e.getPath()); //Debugger
		        jtree.setSelectionPath(e.getPath());
		        //rmiDebug.setDebugText(jtree.getSelectionPath()); //Debugger
	        	
	        } catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
	        }
		}
		
	    public void treeNodesChanged(TreeModelEvent e) {}
	    
	    public void treeNodesInserted(TreeModelEvent e) {}
	    
	    public void treeNodesRemoved(TreeModelEvent e) {}
	    
	    public void treeStructureChanged(TreeModelEvent e) {}
	    //=========================================================
	    
		public void getDownLevel () {
			try {
				// To retrieve elements inside the seleted node
		        DefaultMutableTreeNode parentNode = null;
		        TreePath parentPath = jtree.getSelectionPath();
				String location = "";
				TreeNode arr_node [];
				
				rmiDebug.setDebugText("Selected path: " + parentPath);
				
		        if ((parentPath + "").equals("[root]") == true) {
		            parentNode = root;
		            
		        } else {
		            parentNode = (DefaultMutableTreeNode)(parentPath.getLastPathComponent());
		            DefaultMutableTreeNode childNode;
		            
		            if (parentNode.getChildCount() == 0) {
			            arr_node = new TreeNode [parentNode.getLevel()+1];
			            arr_node = parentNode.getPath();
			            
			            for (int j=0; j<arr_node.length; j++) {
			            	if ((arr_node [j] + "").equals("Local File System") == false) {
				            	//System.out.println(arr_node [j]); //Debugger
				            	location += arr_node [j] + "/";
			            	}
			            }
			            
			            //Optional Debugger
			            rmiDebug.setDebugText("Convert file path string: " + location);
			            
						String [][] ls = rmi_FT.getFileList(location);
						
						jpbLocalView.setMaximum(ls.length);
						jpbLocalView.setValue(0);
						rmiDebug.setDebugText("Retrieving childs for folder: " + parentPath + ", please wait......");
						
						for (int i=0; i<ls.length; i++) {
							//System.out.println(ls [i][0] + "," + ls [i][1]); //Debugger
							childNode = new DefaultMutableTreeNode(ls [i][0] + "");
							if (ls [i][1].equals("D")==true) {
								childNode.setAllowsChildren(true);
							} else {
								childNode.setAllowsChildren(false);
							}
							
							//System.out.println(i); //Debugger
							jpbLocalView.setValue(i);
							
							dtm.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
						}
						
						rmiDebug.setDebugText("Folder detail retrieval complete.");
		            }
		        }

			} catch (NullPointerException npe) {
				warning("Error Detected! Please check whether:\n" + 
						"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
						"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
						"3. Drive has enough disk space\n");
			
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		public void getUpLevel() {
			try {
				DefaultMutableTreeNode parentNode = null;
				DefaultMutableTreeNode childNode = null;
				TreePath parentPath = jtree.getSelectionPath();
				
				rmiDebug.setDebugText("Selected path: " + parentPath);
				
		        if ((parentPath + "").equals("[root]")==true) {
		            parentNode = root;
		            
		        } else {
		        	parentNode = (DefaultMutableTreeNode)(parentPath.getLastPathComponent());
		        	//System.out.println(parentNode.getChildCount()); //Debugger
		        	
					jpbLocalView.setMaximum(parentNode.getChildCount());
					jpbLocalView.setValue(0);
		        	rmiDebug.setDebugText("Removing current childs for folder: " + parentPath + ", please wait......");
		        	
		        	int i = 1;
		        	while (parentNode.getChildCount()>0) {
		        		childNode = (DefaultMutableTreeNode)parentNode.getChildAt(0);
		        		
						//System.out.println(i); //Debugger
						jpbLocalView.setValue(i);
		        		
		        		dtm.removeNodeFromParent(childNode);
		        		i++;
		        	}
		        	
		        	parentNode.setAllowsChildren(true);
		        	//System.out.println(parentNode.getChildCount()); //Debugger
		        	
					rmiDebug.setDebugText("Childs removal complete.");
		        }
		        
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
	    }
	}
	
	
	public class LocalViewProgressThread extends Thread {
		RMIClientGUI_TransferLocalView rmiGUI_TLV;
		RMIClient_FileTransferLocal rmi_FT;
		LocalViewThread LVT;
		boolean stop = false;
		String type;
		
		public LocalViewProgressThread(RMIClientGUI_TransferLocalView rmiGUI_TLV, RMIClient_FileTransferLocal rmi_FT, LocalViewThread LVT, String type) {
			this.rmiGUI_TLV = rmiGUI_TLV;
			this.rmi_FT = rmi_FT;
			this.LVT = LVT;
			this.type = type;
		}
		
		public void run() {
			try {
				jpbLocalView = new JProgressBar();
		        jpbLocalView.setValue(0);
		        jpbLocalView.setStringPainted(true);
		        jpbLocalView.setBorderPainted(true);
		        
		        rmiGUI_TLV.removeAll();
		        rmiGUI_TLV.setLayout(new BorderLayout());
		        rmiGUI_TLV.add(jpbLocalView, BorderLayout.NORTH);
		        rmiGUI_TLV.add(jpSouth, BorderLayout.SOUTH);
		        rmiGUI_TLV.updateUI();
		        rmiGUI_TLV.revalidate();
		        
		        if (type.equals("up")) {
		        	LVT.getUpLevel();
		        	LVT.getDownLevel();
		        } else if (type.equals("down")) {
		        	LVT.getDownLevel();
		        }
		        
		        rmiGUI_TLV.removeAll();
		        rmiGUI_TLV.setLayout(new BorderLayout());
		        rmiGUI_TLV.add(jspLocalView, BorderLayout.CENTER);
				rmiGUI_TLV.add(jpSouth, BorderLayout.SOUTH);
		        rmiGUI_TLV.updateUI();
		        rmiGUI_TLV.revalidate();
		        
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
				
			} finally {
				setComplete();
			}
		}
		
		public void setComplete() {
			stop = true;
		}
		
		public boolean getComplete() {
			return stop;
		}
	}
	
	
	public class MovingFileObjectThread extends Thread {
		RMIClientGUI_TransferLocalView rmiGUI_TLV;
		RMIClient_FileTransferLocal rmi_FT;
		LocalViewThread LVT;
		boolean stop = false;
		JInternalFrame jif;
		JProgressBar jpb;
		String destLocation;
		JLabel jlbMessage;
		
		public MovingFileObjectThread(RMIClientGUI_TransferLocalView rmiGUI_TLV, RMIClient_FileTransferLocal rmi_FT, LocalViewThread LVT) {
			this.rmiGUI_TLV = rmiGUI_TLV;
			this.rmi_FT = rmi_FT;
			this.LVT = LVT;
			
			//--- Initialize the JProgressBar ---
			jpb = new JProgressBar();
			jpb.setValue(0);
			jpb.setStringPainted(true);
			jpb.setBorderPainted(true);
			//-----------------------------------
		}
		
		public JProgressBar getProgressBar() {
			return jpb;
		}
		
		public void startMonitor() {
			new MonitorProgressThread().start();
		}
		
		public void run() {
			jif = new JInternalFrame("In Progress...");
			
			try {
				jif.getContentPane().setLayout(new BorderLayout());
				jif.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
		    	jif.setResizable(true);
		    	jif.setIconifiable(true);
		    	jif.setMaximizable(false);
		    	jif.setClosable(true);
				
					JPanel jpTop = new JPanel();
					jpTop.setLayout(new BorderLayout());
					jpTop.setBackground(Color.WHITE);
					
						jlbMessage = new JLabel("     "); //Must put a blank string
						
					jpTop.add(jlbMessage, BorderLayout.NORTH);
					jpTop.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/fileCopy.gif"))), BorderLayout.CENTER);
				
				jif.getContentPane().add(jpTop, BorderLayout.CENTER);
				jif.getContentPane().add(jpb, BorderLayout.SOUTH);
			    jif.setSelected(true);
			    jdp.add(jif);
				
				LVT.paste(this);
				
				while (stop == false) {
					sleep(50); //wait for 0.05 sec, then retry
				}
		        
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
				
			} finally {
				stop = true;
			}
			
			if (stop == true) {
				jif.setVisible(false);
				jif.dispose();
			}
		}
		
		public class MonitorProgressThread extends Thread {
			public void run() {
				try {
					jif.pack();
					int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
					int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
					jif.setLocation(x,y);
					jif.setVisible(true);
					
					while (stop == false) {
						//Set the title of the JInternalFrame
						jif.setTitle(jlbMessage.getText());
						
						int counter = rmi_FT.countObject(destLocation);
						//System.out.println(counter); //Debugger
						
						jpb.setValue(counter);
						sleep(10); //wait for 0.01 sec, then retry
					}
					
				} catch (Exception exc) {
					StackTraceElement[] error = exc.getStackTrace();
					rmiDebug.setDebugText(error);
					warning(exc.getMessage());
					
				} finally {
					jif.setVisible(false);
					jif.dispose();
				}
			}
		}
	}
	
	
	public class LoadFileSizeThread extends Thread {
		LocalViewThread LVT;
		
		public LoadFileSizeThread(LocalViewThread LVT) {
			this.LVT = LVT;
		}
		
		public void run() {
			LVT.detail();
		}
	}
	
	
	public class uploadThread extends Thread {
		RMIClientGUI_TransferLocalView rmiGUI_TLV;
		RMIClient_FileTransferLocal rmi_FT;
		LocalViewThread LVT;
		boolean stop = false;
		JInternalFrame jif;
		JProgressBar jpb;
		String destLocation;
		JLabel jlbMessage;
		String transferType;
		
		public uploadThread(RMIClientGUI_TransferLocalView rmiGUI_TLV, RMIClient_FileTransferLocal rmi_FT, LocalViewThread LVT, String transferType) {
			this.rmiGUI_TLV = rmiGUI_TLV;
			this.rmi_FT = rmi_FT;
			this.LVT = LVT;
			this.transferType = transferType;
			
			//--- Initialize the JProgressBar ---
			jpb = new JProgressBar();
			jpb.setValue(0);
			jpb.setStringPainted(true);
			jpb.setBorderPainted(true);
			//-----------------------------------
		}
		
		public JProgressBar getProgressBar() {
			return jpb;
		}
		
		public void startMonitor() {
			new MonitorProgressThread().start();
		}
		
		public void run() {
			jif = new JInternalFrame("Uploading ...");
			
			try {
				jif.getContentPane().setLayout(new BorderLayout());
				jif.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
		    	jif.setResizable(true);
		    	jif.setIconifiable(true);
		    	jif.setMaximizable(false);
		    	jif.setClosable(true);
				
					JPanel jpTop = new JPanel();
					jpTop.setLayout(new BorderLayout());
					jpTop.setBackground(Color.WHITE);
					
						jlbMessage = new JLabel("     "); //Must put a blank string
						
					jpTop.add(jlbMessage, BorderLayout.NORTH);
					jpTop.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/fileTransfer.gif"))), BorderLayout.CENTER);
				
				jif.getContentPane().add(jpTop, BorderLayout.CENTER);
				jif.getContentPane().add(jpb, BorderLayout.SOUTH);
			    jif.setSelected(true);
			    jdp.add(jif);
				
				if (transferType.equals("bin")) {
					LVT.instantUpload_Bin(this);
					
				} else if (transferType.equals("ascii")) {
					LVT.instantUpload_Ascii(this);
				}
				
				while (stop == false) {
					sleep(50); //wait for 0.05 sec, then retry
				}
		        
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
				
			} finally {
				stop = true;
			}
			
			if (stop == true) {
				jif.setVisible(false);
				jif.dispose();
			}
		}
		
		public class MonitorProgressThread extends Thread {
			public void run() {
				try {
					jif.pack();
					int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
					int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
					jif.setLocation(x,y);
					jif.setVisible(true);
					
					while (stop == false) {
						//Set the title of the JInternalFrame
						jif.setTitle(jlbMessage.getText());
						
						int counter = rmi_FT.countRemoteObject(destLocation);
						//System.out.println(counter); //Debugger
						
						jpb.setValue(counter);
						sleep(10); //wait for 0.01 sec, then retry
					}
					
					jif.setVisible(false);
					jif.dispose();
					
				} catch (Exception exc) {
					StackTraceElement[] error = exc.getStackTrace();
					rmiDebug.setDebugText(error);
					warning(exc.getMessage());
				}
			}
		}
	}
	
	
	public class uploadQueueThread extends Thread {
		RMIClientGUI_TransferLocalView rmiGUI_TLV;
		RMIClient_FileTransferLocal rmi_FT;
		LocalViewThread LVT;
		Object [][] data = null;
		
		public uploadQueueThread(RMIClientGUI_TransferLocalView rmiGUI_TLV, RMIClient_FileTransferLocal rmi_FT, LocalViewThread LVT, Object [][] data) {
			this.rmiGUI_TLV = rmiGUI_TLV;
			this.rmi_FT = rmi_FT;
			this.LVT = LVT;
			this.data = data;
		}
		
		public synchronized void run() {
			jbtnOpenQueue.setEnabled(false);
			
			try {
				//Iterate over all the Objects in the queue
				for (int i=0; i<data.length; i++) {
					String srcLocation = data [i][1] + "";
					String transferType = data [i][4] + "";
					Long fileSize = (Long)data [i][3];
					String destLocation = data [i][5] + "";
					
					uploadThread ut = null;
					
					if (transferType.equals("Binary")) {
						ut = new uploadThread(rmiGUI_TLV, rmi_FT, LVT, "bin_delay");
						ut.start();
					} else if (transferType.equals("Ascii")) {
						ut = new uploadThread(rmiGUI_TLV, rmi_FT, LVT, "ascii_delay");
						ut.start();
					}
					
					//Optional Debugger
					rmiDebug.setDebugText("Delay Upload (" + transferType + ") - From " + srcLocation + " to " + destLocation);
					
					int totalObject = rmi_FT.countObject(srcLocation);
					rmiDebug.setDebugText("Total Object: " + totalObject);
					
					String srcPath = "";
					String srcFile = "";
					if (srcLocation.indexOf("/") >= 0) {
						srcPath = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
						srcPath = srcPath.substring(0,srcPath.lastIndexOf("/")+1);
						
						srcFile = srcLocation.substring(0,srcLocation.lastIndexOf("/"));
						srcFile = srcFile.substring(srcFile.lastIndexOf("/")+1, srcFile.length());
					}
					
					//Set the message
					sleep(1000); //wait for 1.0 second
					ut.jlbMessage.setText("<html><font color='#333333'>&nbsp;Uploading " + srcFile + "...</font></html>");
					
					//Set the destLocation for the Thread Monitor
					ut.destLocation = destLocation;
					
					//Set the totalObject as the Max value of JProgressBar in the Thread
					JProgressBar jpb = ut.getProgressBar();
					jpb.setMaximum(totalObject);
					if (totalObject == 0) {
						jpb.setStringPainted(false);
					} else {
					 	jpb.setStringPainted(true);
					}
					
					//Start the Thread Monitor
					ut.startMonitor();
					
					if (transferType.equals("Binary")) {
						rmi_FT.uploadFile_Bin(srcLocation, destLocation);
					} else if (transferType.equals("Ascii")) {
						rmi_FT.uploadFile_Ascii(srcLocation, destLocation);
					}
					
					/*********************************************
					 * no need to refresh the Remote View, as 
					 * the Objects uploaded from the queue maybe 
					 * originated from different source locations
					 ********************************************/
					//rmiGUI_FT.refreshRemoteView();
					
					ut.stop = true;
					
					/*******************************************
					 * If upload complete, null the record in 
					 * the Vector
					 ******************************************/
					vUploadQueue.setElementAt(null, i);
				}
				
				/**************************************
				 * This part is for housekeeping, as 
				 * we need to remove the Vector whose 
				 * value is null
				 *************************************/
				for (int i=0; i<vUploadQueue.size(); i++) {
					if (vUploadQueue.elementAt(i) == null) {
						vUploadQueue.removeElementAt(i);
						i--;
					}
				}
				
				if (vUploadQueue.size() > 0) {
					//Re-convert the remaining records in the Upload Queue (Vector) to a 2-Dimensional Object array
					rmiDebug.setDebugText("Convert remaining Objects in Upload Queue (Vector) to 2-Dimensional Object array");
					
					Object [][] newData = new Object [vUploadQueue.size()][6];
					
					for (int i=0; i<vUploadQueue.size(); i++) {
						Object [] tmp = (Object [])vUploadQueue.elementAt(i);
						
						for (int j=0; j<tmp.length; j++) {
							newData [i][j] = tmp [j];
						}
					}
					
					openQueue(newData);
					
				} else {
					Object [][] data = new Object [0][6];
					openQueue(data);
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
				
			} finally {
				jbtnOpenQueue.setEnabled(true);
			}
		}
	}
	
	
	public class EditFileThread extends Thread {
		RMIClientGUI_TransferLocalView rmiGUI_TLV;
		LocalViewThread LVT;
		RMIClient_FileTransferRemote rmi_FTR;
		RMIClient_FileTransferLocal rmi_FTL;
		
		public EditFileThread(RMIClientGUI_TransferLocalView rmiGUI_TLV, LocalViewThread LVT) {
			this.rmiGUI_TLV = rmiGUI_TLV;
			this.LVT = LVT;
			this.rmi_FTR = new RMIClient_FileTransferRemote(rmiGUI_FT.serverIP, 
													rmiGUI_FT.serverPort, 
													rmiGUI_FT.serverShell, 
													rmiGUI_TLV);
			this.rmi_FTL = new RMIClient_FileTransferLocal(rmiGUI_FT.serverIP, 
													rmiGUI_FT.serverPort, 
													rmiGUI_FT.serverShell, 
													rmiGUI_TLV);
		}
		
		public void run() {
			try {
				LVT.edit(this);
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
	}
	
	
	public class DeleteFileObjectThread extends Thread {
		RMIClientGUI_TransferLocalView rmiGUI_TLV;
		RMIClient_FileTransferLocal rmi_FT;
		LocalViewThread LVT;
		boolean stop = false;
		JInternalFrame jif;
		JProgressBar jpb;
		String destLocation;
		JLabel jlbMessage;
		
		public DeleteFileObjectThread(RMIClientGUI_TransferLocalView rmiGUI_TLV, RMIClient_FileTransferLocal rmi_FT, LocalViewThread LVT) {
			this.rmiGUI_TLV = rmiGUI_TLV;
			this.rmi_FT = rmi_FT;
			this.LVT = LVT;
			
			//--- Initialize the JProgressBar ---
			jpb = new JProgressBar();
			jpb.setValue(0);
			jpb.setStringPainted(true);
			jpb.setBorderPainted(true);
			//-----------------------------------
		}
		
		public JProgressBar getProgressBar() {
			return jpb;
		}
		
		public void startMonitor() {
			new MonitorProgressThread().start();
		}
		
		public void run() {
			jif = new JInternalFrame("In Progress...");
			
			try {
				jif.getContentPane().setLayout(new BorderLayout());
				jif.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
		    	jif.setResizable(true);
		    	jif.setIconifiable(true);
		    	jif.setMaximizable(false);
		    	jif.setClosable(true);
				
					JPanel jpTop = new JPanel();
					jpTop.setLayout(new BorderLayout());
					jpTop.setBackground(Color.WHITE);
					
						jlbMessage = new JLabel("     "); //Must put a blank string
					
					jpTop.add(jlbMessage, BorderLayout.NORTH);
					jpTop.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/FileDelete.gif"))), BorderLayout.CENTER);
				
				jif.getContentPane().add(jpTop, BorderLayout.CENTER);
				jif.getContentPane().add(jpb, BorderLayout.SOUTH);
			    jif.setSelected(true);
			    jdp.add(jif);
				
				LVT.delete(this);
				
				while (stop == false) {
					sleep(50); //wait for 0.05 sec, then retry
				}
		        
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
				
			} finally {
				stop = true;
			}
			
			if (stop == true) {
				jif.setVisible(false);
				jif.dispose();
			}
		}
		
		public class MonitorProgressThread extends Thread {
			public void run() {
				try {
					jif.pack();
					int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
					int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
					jif.setLocation(x,y);
					jif.setVisible(true);
					
					while (stop == false) {
						//Set the title of the JInternalFrame
						jif.setTitle(jlbMessage.getText());
						
						int counter = rmi_FT.countObject(destLocation);
						//System.out.println(counter); //Debugger
						//System.out.println(jpb.getMaximum()-counter); //Debugger
						
						jpb.setValue(jpb.getMaximum()-counter);
						sleep(10); //wait for 0.01 sec, then retry
					}
					
				} catch (Exception exc) {
					StackTraceElement[] error = exc.getStackTrace();
					rmiDebug.setDebugText(error);
					warning(exc.getMessage());
					
				} finally {
					jif.setVisible(false);
					jif.dispose();
				}
			}
		}
	}
}