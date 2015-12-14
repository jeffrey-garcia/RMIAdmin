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


public class RMIClientGUI_FileTableView extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	
	public FileTableThread FTbT;
	
	public RMIClientGUI_Debugger rmiDebug;
	public RMIClientGUI_FileBrowser rmiGUI_FB;
	
	private Vector vSrcLocation = new Vector();
	private String filePasteType = "";
	
	
	public RMIClientGUI_FileTableView(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug, RMIClientGUI_FileBrowser rmiGUI_FB) {
		this.rmiGUI_FB = rmiGUI_FB;
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		
		initGUI();
	}
	
	
	public void initGUI() {
		setPreferredSize(new Dimension(220, 250));
		setLayout(new BorderLayout());
		setVisible(true);
	}
	
	
	public RMIClientGUI_Debugger getDebugger() {
		return rmiDebug;
	}
	
	
	public void clearView() {
		removeAll();
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(280,250));
		updateUI();
		
		//Remove the vector to clear previous selected objects
		vSrcLocation.removeAllElements();
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
			rmiGUI_FB.clearAllViews();
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
	
	
	public void refreshFileTableView(String location, String [][] fileList) {
		FTbT = new FileTableThread(location, fileList, this);
		FTbT.start();
	}
	
	
	public void setFilesToBePasted(String srcLocation, String filePasteType) {
		vSrcLocation.addElement(srcLocation);
		this.filePasteType = filePasteType;
		
		FTbT.paste.setEnabled(true);
	}
	
	
	public void clearFilesToBePasted() {
		vSrcLocation.removeAllElements();
		FTbT.paste.setEnabled(false);
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
	
	
	private int confirmDelete(String file) {
		int choice = 2;
		
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);	
			JOptionPane jop;
			JDialog jdg;
			String opt = "Are you sure to delete " + file + "?";
			
			opt = opt.replaceAll("\n","<br>");
			String temp = "<html>" + opt + "</html>";
			
			String [] option = new String [4];
			option [0] = "Delete";
			option [1] = "Delete All";
			option [2] = "Cancel";
			option [3] = "Cancel All";
			
			jop = new JOptionPane(opt, 
									JOptionPane.WARNING_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION,
									null,
									option);
			
			jdg = jop.createDialog(this,"Delete Confirmation!");
			
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
			
			String value = (String)jop.getValue();
			
			for (int i=0; i<option.length; i++) {
				if (value.equals(option [i]) == true) {
					choice = i;
					break;
				}
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
			
			choice = 2;
		}
		
		return choice;
	}
	
	
	private int confirmOverwrite(String file) {
		int choice = 2;
		
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);	
			JOptionPane jop;
			JDialog jdg;
			String opt = "Are you sure to overwrite " + file + "?";
			
			opt = opt.replaceAll("\n","<br>");
			String temp = "<html>" + opt + "</html>";
			
			String [] option = new String [4];
			option [0] = "Overwrite";
			option [1] = "Overwrite All";
			option [2] = "Cancel";
			option [3] = "Cancel All";
			
			jop = new JOptionPane(opt, 
									JOptionPane.WARNING_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION,
									null,
									option);
			
			jdg = jop.createDialog(this,"Overwrite Confirmation!");
			
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
			
			String value = (String)jop.getValue();
			
			for (int i=0; i<option.length; i++) {
				if (value.equals(option [i]) == true) {
					choice = i;
					break;
				}
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
			warning(exc.getMessage());
			
			choice = 2;
		}
		
		return choice;
	}
	
	
	public class FileTableThread extends Thread {
		RMIClientGUI_FileTableView rmiGUI_FTbV;
		RMIClient_FileBrowser rmi_FB;
		FileTableThread FTbT_Ptr;
		FileTableProgressThread progress;
		Object [][] fileList;
		String location;
		DefaultTableModel tableModel;
		Object [][] fileDetail;
		Vector vRec = new Vector();
		JMenuItem paste;
		
		public FileTableThread(String location, Object [][] fileList, RMIClientGUI_FileTableView rmiGUI_FTbV) {
			FTbT_Ptr = this;
			this.location = location;
			this.fileList = fileList;
			this.rmiGUI_FTbV = rmiGUI_FTbV;
			this.rmi_FB = new RMIClient_FileBrowser(rmiGUI_FB.serverIP, 
													rmiGUI_FB.serverPort, 
													rmiGUI_FB.serverShell, 
													rmiGUI_FTbV);
		}
		
		public void run() {
			final int dataRow;
			final int dataCol;
			final String [] colNames;
			
			try {
				rmiGUI_FTbV.removeAll();
				
				JProgressBar jpbFileTable = new JProgressBar();
				jpbFileTable.setMaximum(fileList.length);
				jpbFileTable.setValue(0);
		        jpbFileTable.setStringPainted(true);
		        jpbFileTable.setBorderPainted(true);
		        
				progress = new FileTableProgressThread(rmiGUI_FTbV, rmi_FB, this, jpbFileTable);
				progress.start();
		        
		        while (progress.ready == false) {
		        	sleep(100);
		        }
		        
				dataRow = fileList.length;
				dataCol = 4;
				fileDetail = new Object [dataRow][dataCol];
				
				for (int i=0; i<fileList.length; i++) {
					String file = location + fileList [i][0];
					Vector vFileInfo = rmi_FB.getFileInfo(file);
					//System.out.println(vFileInfo); //Debugger
					jpbFileTable.setValue(i);
					
					for (int j=0; j<dataCol; j++) {
						if (j==0) {
							fileDetail [i][j] = vFileInfo.elementAt(0) + "";
						} else if (j==2) {
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								//fileDetail [i][j] = "Folder";
								fileDetail [i][j] = new ImageIcon(this.getClass().getResource("/image/folderIcon" + ".jpg"));
							} else {
								//fileDetail [i][j] = "File";
								fileDetail [i][j] = new ImageIcon(this.getClass().getResource("/image/fileIcon" + ".jpg"));
							}
						} else if (j==1) {
							fileDetail [i][j] = new Long(vFileInfo.elementAt(10) + ""); // + " bytes";
							//fileDetail [i][j] = new Long(rmi_FB.getFileSize(file) + ""); //Recusively get file size
						} else if (j==3) {
							fileDetail [i][j] = new Date(Long.parseLong(vFileInfo.elementAt(9)+""));
						}
					}
				}
				
				progress.setComplete();
				
				//====== Organize 2 dimensional arrays into JTable ======
					//Setup the Column Names
					colNames = new String [dataCol];
					colNames [0] = "Name";
					colNames [2] = "Type";
					colNames [1] = "Size";
					colNames [3] = "Modified";
					
					//Create the Data Model
					final DefaultTableModel dataModel = new DefaultTableModel() {
						int dataRow1 = dataRow;
						int dataCol1 = dataCol;
						
						public int getColumnCount() { return dataCol1; }
						public int getRowCount() { return dataRow1;}
						public Object getValueAt(int row, int col) {
							return fileDetail [row][col];
						}
						public boolean isCellEditable(int row, int col) {
						   	return false;
						}
						public String getColumnName(int col) {
							return colNames [col];
						}
						public Class getColumnClass(int col) {
							if (col == 2) {
								return ImageIcon.class;
							} else if (col == 1) {
								return Long.class;
							} else if (col == 3) {
								return Date.class;
							} else {
								return String.class;
							}
						}
						public void removeRow(int deletedRowIndex) {
							//System.out.println(fileDetail [deletedRowIndex][0]); //Debugger
							
							Object [][] newObj = new Object [fileDetail.length-1][dataCol];
							for (int i=0; i<fileDetail.length-1; i++) {
								for (int j=0; j<dataCol; j++) {
									if (i<deletedRowIndex) {
										newObj [i][j] = fileDetail [i][j];
									} else {
										newObj [i][j] = fileDetail [i+1][j];
									}
								}
							}
							
							dataRow1 = dataRow1 - 1;
							fileDetail = newObj;
							
							fireTableDataChanged();
							fireTableRowsDeleted(deletedRowIndex, deletedRowIndex);
						}
					};
					tableModel = dataModel;
					
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
							
							//Replace the tip when is is an icon image
							if (tip.indexOf("/")>=0 && tip.indexOf("Icon")>=0) {
								tip = tip.substring(tip.lastIndexOf("/")+1, tip.indexOf("Icon"));
							}
							
							tip = tip.replaceAll("\n","<br>");
							tip = "<html>" + tip + "</html>";
							
							return tip;
						}
					};
					sorter.setTableHeader(jtb.getTableHeader());
					
					//====== Initialize the Popup Menu ======
					final JPopupMenu jpm = new JPopupMenu();
					
					final JMenuItem edit = new JMenuItem("Edit");
					edit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							new EditFileThread(rmiGUI_FTbV, FTbT_Ptr).start();
						}
					});
					
					JMenuItem copy = new JMenuItem("Copy");
					copy.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							if (vRec.size() > 0) copy(vRec);
						}
					});
					JMenuItem cut = new JMenuItem("Cut");
					cut.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							if (vRec.size() > 0) cut(vRec);
						}
					});
					paste = new JMenuItem("Paste");
					paste.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							new MovingFileObjectThread(rmiGUI_FTbV, rmi_FB, FTbT_Ptr, vRec).start();
						}
					});
					JMenuItem create = new JMenuItem("Create");
					create.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							create(vRec);
						}
					});
					JMenuItem delete = new JMenuItem("Delete");
					delete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							if (vRec.size() > 0) {
								new DeleteFileObjectThread(rmiGUI_FTbV, rmi_FB, FTbT_Ptr, vRec).start();
								//delete(vRec);
							}
						}
					});
					JMenuItem rename = new JMenuItem("Rename");
					rename.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							if (vRec.size() > 0) rename(vRec);
						}
					});
					JMenuItem detail = new JMenuItem("Detail");
					detail.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							if (vRec.size() > 0) {
								//detail(vRec);
								new LoadFileSizeThread(FTbT_Ptr, vRec).start();
							}
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
			        
			        if (vSrcLocation.size() <= 0) {
			        	paste.setEnabled(false); //Disable the paste function if no selected file object previously
			        }
					//=======================================
					
					//====== Add MouseListener to enable Popup Menu when right click ======
					jtb.addMouseListener(new MouseListener() {
						public void mouseClicked(MouseEvent me) {
							//Make sure only open folder when double click
							if(me.getClickCount() == 2) {
								openFolder();
							}
						};
						public void mouseEntered(MouseEvent me) {};
						public void mouseExited(MouseEvent me) {};
						public void mousePressed(MouseEvent me) {showPopup(me);};
						public void mouseReleased(MouseEvent me) {showPopup(me);};
						private void showPopup(MouseEvent me) {
							if (me.isPopupTrigger()) {
								//We will only allow editing of the single file Object
								if (vRec.size() == 1) {
									String filename = vRec.elementAt(vRec.size()-1) + "";
									
									for (int j=0; j<fileDetail.length; j++) {
										if (filename.equals(fileDetail [j][0] + "")) {
											//Optional Debugger
											if ((fileDetail [j][2] + "").indexOf("folderIcon") >= 0) {
												edit.setEnabled(false);
											} else {
												edit.setEnabled(true);
											}
											
											break; //Stop the loop
										}
									}
									
								} else {
									edit.setEnabled(false);
								}
								
								jpm.show(me.getComponent(), me.getX(), me.getY());
							}
						}
					});
					//=====================================================================
					
					//Set the width of specific column
					TableColumn column = null;
					for (int i=0; i<dataCol; i++) {
						column = jtb.getColumnModel().getColumn(i);
						if (i == 0) {
							column.setPreferredWidth(150);
						} else if (i == 2) {
							column.setPreferredWidth(30);
						} else if (i == 3) {
							column.setPreferredWidth(70);
						}
					}
				
					jtb.setShowVerticalLines(false);
					jtb.setShowHorizontalLines(false);
					jtb.setIntercellSpacing(new Dimension(1,1));
	    			jtb.setRowHeight(20);
	    			
	    			setImageObserver(jtb);
	    			
					vRec = new Vector();
					ListSelectionModel LSM = jtb.getSelectionModel();
					LSM.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					LSM.addListSelectionListener(new ListSelectionListener() {
					    public void valueChanged(ListSelectionEvent e) {
				    		//Ignore extra messages.
				        	if (e.getValueIsAdjusting()) return;
				        
				        	ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				        		            
				            String output = "";
				            vRec.removeAllElements();
				            if (lsm.isSelectionEmpty()) {
				                output += "No Selected Index";
				                vRec.removeAllElements();
				            } else {
				                // Find out which indexes are selected.
				                int minIndex = lsm.getMinSelectionIndex();
				                int maxIndex = lsm.getMaxSelectionIndex();
				                vRec.removeAllElements();
				                for (int i = minIndex; i <= maxIndex; i++) {
				                    if (lsm.isSelectedIndex(i)) {
				                    	int colFileName = 0;
				                    	for (int j=0; j<jtb.getColumnCount(); j++) {
				                    		if (jtb.getColumnName(j).equals("Name") == true) {
				                    			colFileName = j;
				                    			break;
				                    		}
				                    	}
				                    	
				                    	//System.out.println(jtb.getValueAt(i, colFileName)); //Debugger
				                        output += " " + jtb.getValueAt(i,colFileName) + "";
				                        vRec.addElement(jtb.getValueAt(i,colFileName) + "");
				                    }
				                }
				            }
				            
				            rmiDebug.setDebugText("Selected Row = " + output); //Debugger
				    	}
					});
				//=======================================================
				
				//====== Add the JTable to the existing GUI container ======
	    			JScrollPane jsp = new JScrollPane(jtb);
	    			jsp.setSize(400,400);
				//==========================================================
				
				//====== Reload the File Table Panel ======
				rmiGUI_FTbV.removeAll();
				rmiGUI_FTbV.setLayout(new BorderLayout());
				rmiGUI_FTbV.add(jsp, BorderLayout.CENTER);
				rmiGUI_FTbV.updateUI();
				rmiGUI_FTbV.revalidate();
				//=========================================
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		//====== Put all the File Operations here ======
		private void edit(final EditFileThread eft) {
			try {
				//We will only show all editing of single file object
				if (vRec.size() == 1) {
					String filename = vRec.elementAt(vRec.size()-1) + "";
					
					for (int j=0; j<fileDetail.length; j++) {
						if (filename.equals(fileDetail [j][0] + "")) {
							
							//=== Put the File Operation here ===
							final String srcLocation = location + filename;
							String destLocation = "";
						
							rmiDebug.setDebugText("Get detail for: " + location + filename);
							Vector vFileInfo = rmi_FB.getFileInfo(location + filename);
							
							//Check if the remote drive is really ready
							if (vFileInfo == null) {
								warning("Error Detected! Please check whether:\n" + 
										"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
										"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
										"3. Drive has enough disk space\n");
							} else {
								//Validate if the temp location exist, if not, create it.
								rmiDebug.setDebugText("Validate Temp Space");
								eft.rmi_FTR.validateTempSpace();
								
								final String fileName = vFileInfo.elementAt(0) + "";
								final String tempLocation = System.getProperty("user.home") + File.separator + "RMIAdmin" + File.separator + fileName;
								
								//Optional Debugger
								rmiDebug.setDebugText("Download (Ascii) - From " + srcLocation + " to " + tempLocation + " for editing");
								eft.rmi_FTR.downloadFile_Ascii(srcLocation, tempLocation);
								
								RMIClientGUI_FileEditor rmiGUI_FE = new RMIClientGUI_FileEditor(jdp, rmiDebug, rmiGUI_FTbV, eft.rmi_FTR, eft.rmi_FTL, srcLocation, tempLocation, "remote");
								rmiGUI_FE.setFile(tempLocation);
								rmiGUI_FE.setBuffer(1024000);
								rmiGUI_FE.init();
								rmiGUI_FE.loadFile();
							}
							
							break; //Stop the loop
						}
					}
					
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void copy(Vector vRec) {
			vSrcLocation = new Vector();
			
			try {
				for (int i=0; i<vRec.size(); i++) {
					String filename = vRec.elementAt(i) + "";
					
					for (int j=0; j<fileDetail.length; j++) {
						if (filename.equals(fileDetail [j][0] + "")) {
							
							//=== Put the File Operation here ===
							if ((fileDetail [j][2] + "").indexOf("folderIcon") >= 0) {
								rmiDebug.setDebugText("Selected Folder to copy: " + location + filename);
								vSrcLocation.addElement(location + filename + "/");
							} else {
								rmiDebug.setDebugText("Selected File to copy: " + location + filename);
								vSrcLocation.addElement(location + filename);
							}
								
							//Set the paste type
							filePasteType = "CopyAndPaste";
							
							j = fileDetail.length; //Stop the inner loop
						}
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
			
			//Enable the paste function
			if (vSrcLocation.size() > 0) {
				paste.setEnabled(true);
				
				//Set the "Files to be pasted" on FileTreeView
				rmiGUI_FB.setFilesToBePasted_FileTreeView(vSrcLocation, filePasteType);
			}
		}
		
		private void cut(Vector vRec) {
			vSrcLocation = new Vector();
			
			try {
				for (int i=0; i<vRec.size(); i++) {
					//rmiDebug.setDebugText("Cut " + location + vRec.elementAt(i)); //Debugger
					String filename = vRec.elementAt(i) + "";
					
					for (int j=0; j<fileDetail.length; j++) {
						if (filename.equals(fileDetail [j][0] + "")) {
							
							//=== Put the File Operation here ===
							if ((fileDetail [j][2] + "").indexOf("folderIcon") >= 0) {
								rmiDebug.setDebugText("Selected Folder to cut: " + location + filename);
								vSrcLocation.addElement(location + filename + "/");
							} else {
								rmiDebug.setDebugText("Selected File to cut: " + location + filename);
								vSrcLocation.addElement(location + filename);
							}
								
							//Set the paste type
							filePasteType = "CutAndPaste";
							
							j = fileDetail.length; //Stop the inner loop
						}
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
			
			//Enable the paste function
			if (vSrcLocation.size() > 0) {
				paste.setEnabled(true);
				
				//Set the "Files to be pasted" on FileTreeView
				rmiGUI_FB.setFilesToBePasted_FileTreeView(vSrcLocation, filePasteType);
			}
		}
		
		private void paste(Vector vRec, MovingFileObjectThread mfoT) {
			boolean overwriteAll = false;
			boolean cancelAll = false;
			int choice = 0;
			String selectedFile = "";
			//System.out.println("\n**********************************\n"); //Debugger
			//System.out.println("No. of selected source object: " + vSrcLocation.size()); //Debugger
			
			try {
				if (vSrcLocation.size() > 0) {
					if (vRec.size() > 0) {
						selectedFile = vRec.elementAt(vRec.size()-1) + "";
					} else {
						selectedFile = "";
					}
					
					for (int j=0; j<fileDetail.length; j++) {
						if (selectedFile.equals(fileDetail [j][0]+"") || selectedFile.equals("")) {
							//=== Put the File Operation here ===
							String destLocation = location + selectedFile;
							if (destLocation.lastIndexOf("/") == destLocation.length()-1) {
								destLocation = destLocation.substring(0, destLocation.length()-1);
							}
							
							Vector vFileInfo = rmi_FB.getFileInfo(destLocation);
							
							//Optional Debugger
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								rmiDebug.setDebugText("Target paste location: " + destLocation);
								
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
								 
								//Only need to check the first element of Vector, 
								//cause other elements in the vector must be in 
								//the same path.
								String source = vSrcLocation.elementAt(0) + "";
								
								String srcPath = "";
								String lastValue = "";
								if (source.indexOf("/") >= 0) {
									lastValue = source.substring(source.length()-1, source.length());
									
									if (lastValue.equals("/")) {
										srcPath = source.substring(0,source.lastIndexOf("/"));
										srcPath = srcPath.substring(0,srcPath.lastIndexOf("/"));
									} else {
										srcPath = source.substring(0,source.lastIndexOf("/"));
										
									}
								}
								
								String destPath = destLocation;
								
								//System.out.println("Source Path + Filename: " + source); //Debugger
								//System.out.println("Path of source: " + srcPath); //Debugger
								//System.out.println("Destination Path: " + destPath); //Debugger
								
								if (destPath.equals(srcPath)!=true && destPath.indexOf(source)>=0 && lastValue.equals("/")==true) {
									warning("Error!<br>Target Location cannot be sub-folder of Source Location");
									mfoT.stop = true;
									
								} else if (srcPath.equals(destPath) == true) {
									warning("Error!<br>Target Location cannot be the same as Source Location");
									mfoT.stop = true;
									
								} else if (destPath.equals(srcPath)!=true) {
									for (int k=0; k<vSrcLocation.size(); k++) {
										if (cancelAll == true) {
											break; //Break the loop immediately
										}
										
										rmiDebug.setDebugText("Object to be pasted: " + vSrcLocation.elementAt(k)); //Debugger
										//System.out.println("Object to be pasted: " + vSrcLocation.elementAt(k)); //Debugger
										
										/**************************************
										 * Check if the src file name & target
										 * file name is equal.
										 *************************************/
										String srcLocation = vSrcLocation.elementAt(k) + "";
										String srcFile = vSrcLocation.elementAt(k) + "";
										
										if (srcFile.indexOf("/") >= 0) {
											lastValue = srcFile.substring(srcFile.length()-1, srcFile.length());
											
											if (lastValue.equals("/")) {
												srcFile = srcFile.substring(0, srcFile.lastIndexOf("/"));
												srcFile = srcFile.substring(srcFile.lastIndexOf("/")+1, srcFile.length());
											} else {
												srcFile = srcFile.substring(srcFile.lastIndexOf("/")+1, srcFile.length());
											}
										}
										rmiDebug.setDebugText("Source File: " + srcFile); //Debugger
										//System.out.println("Source File: " + srcFile); //Debugger
										
										boolean duplicate = rmi_FB.checkDuplicateTarget(srcFile, destLocation);
										
										/***************************************
										 * Count the total object in source 
										 * location, this total count will 
										 * later be used in Threading,
										 **************************************/
										int totalObject = rmi_FB.countObject(srcLocation);
										rmiDebug.setDebugText("Total Object: " + totalObject);
										//System.out.println("Total Object: " + totalObject); //Debugger
										
										//Set the message
										if (filePasteType.equals("CopyAndPaste")) {
											mfoT.jlbMessage.setText("<html><font color='#333333'>&nbsp;Copying " + srcFile + "...</font></html>");
										} else {
											mfoT.jlbMessage.setText("<html><font color='#333333'>&nbsp;Moving " + srcFile + "...</font></html>");
										}
												
										if (duplicate == true) {
											rmiDebug.setDebugText("Duplicated File Exist = " + duplicate);
											
											if (overwriteAll == false) {
												choice = confirmOverwrite(srcFile);
												
												if (choice == 1) {
													overwriteAll = true;
												} else if (choice == 3) {
													rmiDebug.setDebugText("All Overwrite Abort.");
													cancelAll = true;
												}
											}
										}
										
										if (choice==0 || overwriteAll == true) {
											rmiDebug.setDebugText("Confirm Overwrite: " + destLocation + "   "  + srcFile);
											
											//Set the destLocation for the Thread Monitor
											mfoT.destLocation = destLocation + "/" + srcFile;
											//System.out.println("Dest-Location of Thread Monitor" + mfoT.destLocation); //Debugger
											
											//Set the totalObject as the Max value of JProgressBar in the Thread
											JProgressBar jpb = mfoT.getProgressBar();
											jpb.setValue(0);
											jpb.setMaximum(totalObject);
											jpb.setStringPainted(false);
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
										if (filePasteType.equals("CopyAndPaste") && (overwriteAll==true || choice==0)) {
											complete = rmi_FB.copyAndPaste(srcLocation, destLocation);
											
											if (complete.equals("true")) {
												rmiDebug.setDebugText("Copy and Paste result: " + complete);
												
											} else {
												rmiDebug.setDebugText("Copy and Paste result: False");
												rmiDebug.setDebugText("=============================================");
												rmiDebug.setDebugText(complete);
												warning("Error!<br>Copy and Paste denied. Please check whether:\n" + 
														"1. Other application is accessing the same resource\n" + 
														"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
														"3. Drive has enough disk space\n");
											}
											
										} else if (filePasteType.equals("CutAndPaste") && (overwriteAll==true || choice==0)) {
											complete = rmi_FB.cutAndPaste(srcLocation, destLocation);
											
											if (complete.equals("true")) {
												rmiDebug.setDebugText("Cut and Paste result: " + complete);
												
											} else {
												rmiDebug.setDebugText("Cut and Paste result: False");
												rmiDebug.setDebugText("=============================================");
												rmiDebug.setDebugText(complete);
												warning("Error!<br>Cut and Paste denied. Please check whether:\n" + 
														"1. Other application is accessing the same resource\n" + 
														"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
														"3. Drive has enough disk space\n");
											}
										}
									} //End of inner For Loop
									
									//Stop the progress thread here
									mfoT.stop = true;
									
									//Disable the paste function
									vSrcLocation.removeAllElements();
									paste.setEnabled(false);
									
									//Clear the "Files to be pasted" from FileTreeView
									rmiGUI_FB.clearFilesToBePasted_FileTreeView();
									
									//Refresh occur here
									refresh();
								}
							}
							//===================================
							
							break; //Stop the outer loop
						}
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void create(Vector vRec) {
			try {
				/***********************************
				 * Check if the selected object 
				 * is a file or folder.
				 *
				 * If file, simply put the newly
				 * created object at the same 
				 * location where the selected 
				 * object resides.
				 *
				 * Otherwise if selected object is 
				 * folder, place the newly created 
				 * object in it.
				 **********************************/
				 
				if (vRec.size() > 0) {
					String selectedFile = vRec.elementAt(vRec.size()-1) + "";
					//System.out.println(selectedFile); //Debugger
					
					for (int j=0; j<fileDetail.length; j++) {
						if (selectedFile.equals(fileDetail [j][0] + "")) {
							
							//=== Put the File Operation here ===
							if ((fileDetail [j][2] + "").indexOf("folderIcon") >= 0) {
								//System.out.println("1. Create new Object in Folder: " + location + selectedFile); //Debugger
								
								//Change the new location
								location = location + selectedFile + "/";
							} else {
								//System.out.println("2. Create new Object in Folder: " + location); //Debugger
							}
							//===================================
							
							break;
						}
					}
				} else {
					//nothing to do
				}
				
				
				String fileInfo = createFileType();
				
				//Make sure filename and filetype has been input before creation
				if (fileInfo.equals("") == false) {
					String fileType = fileInfo.substring(0, fileInfo.indexOf("###"));
					String fileName = fileInfo.substring(fileInfo.indexOf("###")+3, fileInfo.length());
					
					location += fileName + "/";
								
					//Optional Debugger
					rmiDebug.setDebugText("Convert file path string: " + location);
					
					//Create the object on remote file system
					String complete = rmi_FB.createFile(location, fileType);
					if (complete.equals("true")) {
						rmiDebug.setDebugText("Creation result: " + complete);
						
						//Here is where refresh should occur
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
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void delete(Vector vRec, DeleteFileObjectThread dfoT) {
			boolean deleteAll = false;
			boolean cancelAll = false;
			int choice = 0;
			
			/***************************************
			 * Must clone the Vector, since JTable 
			 * will remove other selected Row 
			 * whenever a row is deleted
			 **************************************/
			Vector vDeletedRec = (Vector)vRec.clone();
			
			try {
				for (int i=0; i<vDeletedRec.size(); i++) {
					//rmiDebug.setDebugText("Delete " + location + vDeletedRec.elementAt(i)); //Debugger
					String filename = vDeletedRec.elementAt(i) + "";
					
					if (cancelAll == true) {
						break; //Break the loop immediately
					}
					
					for (int j=0; j<fileDetail.length; j++) {
						if (filename.equals(fileDetail [j][0] + "")) {
							
							//=== Put the File Operation here ===
							if ((fileDetail [j][2] + "").indexOf("folderIcon") >= 0) {
								rmiDebug.setDebugText("Selected Folder to delete: " + location + filename);
							} else {
								rmiDebug.setDebugText("Selected File to delete: " + location + filename);
							}
							
							if (deleteAll == false) {
								choice = confirmDelete(filename);
								
								if (choice == 1) {
									deleteAll = true;
								} else if (choice == 3) {
									rmiDebug.setDebugText("All Deletion Abort.");
									cancelAll = true;
								}
							}
							
							if (choice==0 || deleteAll==true) {
								rmiDebug.setDebugText("Confirm Delete: " + location + filename);
								
								int totalObject = rmi_FB.countObject(location + filename);
								rmiDebug.setDebugText("Total Object: " + totalObject);
								
								//Set the destLocation for the Thread Monitor
								dfoT.destLocation = location + filename;
								dfoT.jlbMessage.setText("<html><font color='#333333'>&nbsp;Deleting " + filename + "...</font></html>");
								
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
								
								//Perform deletion on remote file system
								String complete = "false";
								complete = rmi_FB.deleteFile(location + filename);
								
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
									//nothing to do...
								}
								//===================================
								
								tableModel.removeRow(j);
								j = fileDetail.length; //Stop the inner loop
								
							} else {
								rmiDebug.setDebugText("Delete Abort: " + location + filename);
							}
						}
					}
				}
				
				refresh();
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void rename(Vector vRec) {
			try {
				//We will only show detail of the last selected Object
				String filename = vRec.elementAt(vRec.size()-1) + "";
				
				for (int j=0; j<fileDetail.length; j++) {
					if (filename.equals(fileDetail [j][0] + "")) {
						
						//=== Put the File Operation here ===
						String srcLocation = location + filename;
						String destLocation = "";
						
						String newFileName = renameFile(filename);
						
						if (newFileName.equals("") == false) {
							destLocation = location + newFileName;
							
							//Optional Debugger
							if ((fileDetail [j][2] + "").indexOf("folderIcon") >= 0) {
								rmiDebug.setDebugText("About to rename folder " + srcLocation + " to " + destLocation);
							} else {
								rmiDebug.setDebugText("About to rename file " + srcLocation + " to " + destLocation);
							}
							
							//Rename the object on remote file system
							String complete = rmi_FB.renameFile(srcLocation, destLocation);
							if (complete.equals("true")) {
								rmiDebug.setDebugText("Renaming result: " + complete);
								
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
						//===================================
						
						break; //Stop the loop
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void detail(Vector vRec) {
			try {
				//We will only show detail of the last selected Object
				String filename = vRec.elementAt(vRec.size()-1) + "";
				
				for (int j=0; j<fileDetail.length; j++) {
					if (filename.equals(fileDetail [j][0] + "")) {
						
						//=== Put the File Operation here ===
						rmiDebug.setDebugText("Get detail for: " + location + filename);
						Vector vFileInfo = rmi_FB.getFileInfo(location + filename);
						
						if ((vFileInfo.elementAt(4)+"").equals("true")) {
							rmiDebug.setDebugText("Getting size of: " + location + " recursively");
							
							//Get folder size recursively
							long sizeInLong = rmi_FB.getFileSize(location + filename);
							
							//Replace the file size with rounded values
							String roundedSize = rmi_FB.getRoundedFileSize(sizeInLong);
							vFileInfo.setElementAt(roundedSize, 10);
							
						} else {
							//Replace the file size with rounded values
							String roundedSize = rmi_FB.getRoundedFileSize(location + filename);
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
						//===================================
						
						break; //Stop the loop
					}
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void openFolder() {
			try {
				//We will only show detail of the last selected Object
				String filename = vRec.elementAt(vRec.size()-1) + "";
				
				for (int j=0; j<fileDetail.length; j++) {
					if (filename.equals(fileDetail [j][0] + "")) {
						
						//=== Put the File Operation here ===
						String srcLocation = location + filename;
						
						//Optional Debugger
						if ((fileDetail [j][2] + "").indexOf("folderIcon") >= 0) {
							rmiDebug.setDebugText("About to open folder " + srcLocation);
							rmiGUI_FB.refreshFileTreeView(srcLocation, filename);
						} else {
							//Do nothing if file
						}
						//===================================
						
						break; //Stop the loop
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
				rmiGUI_FB.refreshFileTreeView();
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		//==============================================
		
		private void setImageObserver(JTable table) {
			TableModel model = table.getModel();
			int colCount = model.getColumnCount();
			int rowCount = model.getRowCount();
			for (int col=0; col<colCount; col++) {
				if (ImageIcon.class == model.getColumnClass(col)) {
					for (int row=0; row<rowCount; row++) {
						ImageIcon icon = (ImageIcon)model.getValueAt(row,col);
						if (icon != null) {
							icon.setImageObserver(new CellImageObserver(table, row, col));
						}
					}
				}
			}
		}
		
		private class CellImageObserver implements ImageObserver {
			JTable table;
			int row;
			int col;
 
			CellImageObserver(JTable table,int row, int col) {
				this.table = table;
				this.row   = row;
				this.col   = col;
			}
 
			public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
				if ((flags & (FRAMEBITS|ALLBITS)) != 0) {
					Rectangle rect = table.getCellRect(row,col,false);
					table.repaint(rect);
				}
      
				return (flags & (ALLBITS|ABORT)) == 0;
			}
		}
	}
	
	
	public class FileTableProgressThread extends Thread {
		RMIClientGUI_FileTableView rmiGUI_FTbV;
		RMIClient_FileBrowser rmi_FB;
		FileTableThread FTbT;
		JProgressBar jpbFileTable;
		boolean stop = false;
		boolean ready = false;
		
		public FileTableProgressThread(RMIClientGUI_FileTableView rmiGUI_FTbV, RMIClient_FileBrowser rmi_FB, FileTableThread FTbT, JProgressBar jpbFileTable) {
			this.rmiGUI_FTbV = rmiGUI_FTbV;
			this.rmi_FB = rmi_FB;
			this.FTbT = FTbT;
			this.jpbFileTable = jpbFileTable;
		}
		
		public void run() {
			try {
		        rmiGUI_FTbV.setLayout(new FlowLayout(FlowLayout.CENTER));
		        rmiGUI_FTbV.add(jpbFileTable);
		        rmiGUI_FTbV.updateUI();
		        rmiGUI_FTbV.revalidate();
		        ready = true;
		        
		        while(getComplete() == false) {
		        	sleep(500);
		        }
		        
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
		RMIClientGUI_FileTableView rmiGUI_FTbV;
		RMIClient_FileBrowser rmi_FB;
		FileTableThread FTbT;
		boolean stop = false;
		JInternalFrame jif;
		JProgressBar jpb;
		String destLocation;
		Vector vRec;
		JLabel jlbMessage;
		
		public MovingFileObjectThread(RMIClientGUI_FileTableView rmiGUI_FTbV, RMIClient_FileBrowser rmi_FB, FileTableThread FTbT, Vector vRec) {
			this.rmiGUI_FTbV = rmiGUI_FTbV;
			this.rmi_FB = rmi_FB;
			this.FTbT = FTbT;
			this.vRec = vRec;
			
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
		    	jif.setResizable(false);
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
				
				FTbT.paste(vRec, this);
				
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
						
						int counter = rmi_FB.countObject(destLocation);
						//System.out.println(counter); //Debugger
						
						jpb.setValue(counter);
						sleep(100); //wait for 0.1 sec, then retry
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
		FileTableThread FTbT;
		Vector vRec2;
		
		public LoadFileSizeThread(FileTableThread FTbT, Vector vRec) {
			vRec2 = (Vector)vRec.clone();
			this.FTbT = FTbT;
		}
		
		public void run() {
			FTbT.detail(vRec2);
		}
	}
	
	
	public class EditFileThread extends Thread {
		RMIClientGUI_FileTableView rmiGUI_FTV;
		FileTableThread FTT;
		RMIClient_FileTransferRemote rmi_FTR;
		RMIClient_FileTransferLocal rmi_FTL;
		
		public EditFileThread(RMIClientGUI_FileTableView rmiGUI_FTV, FileTableThread FTT) {
			this.rmiGUI_FTV = rmiGUI_FTV;
			this.FTT = FTT;
			this.rmi_FTR = new RMIClient_FileTransferRemote(rmiGUI_FB.serverIP, 
													rmiGUI_FB.serverPort, 
													rmiGUI_FB.serverShell, 
													rmiGUI_FTV);
			this.rmi_FTL = new RMIClient_FileTransferLocal(rmiGUI_FB.serverIP, 
													rmiGUI_FB.serverPort, 
													rmiGUI_FB.serverShell, 
													rmiGUI_FTV);
		}
		
		public void run() {
			try {
				FTT.edit(this);
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
	}
	
	
	public class DeleteFileObjectThread extends Thread {
		RMIClientGUI_FileTableView rmiGUI_FTbV;
		RMIClient_FileBrowser rmi_FB;
		FileTableThread FTbT;
		boolean stop = false;
		JInternalFrame jif;
		JProgressBar jpb;
		String destLocation;
		Vector vRec;
		JLabel jlbMessage;
		
		public DeleteFileObjectThread(RMIClientGUI_FileTableView rmiGUI_FTbV, RMIClient_FileBrowser rmi_FB, FileTableThread FTbT, Vector vRec) {
			this.rmiGUI_FTbV = rmiGUI_FTbV;
			this.rmi_FB = rmi_FB;
			this.FTbT = FTbT;
			this.vRec = vRec;
			
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
				
				FTbT.delete(vRec, this);
				
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
						
						int counter = rmi_FB.countObject(destLocation);
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