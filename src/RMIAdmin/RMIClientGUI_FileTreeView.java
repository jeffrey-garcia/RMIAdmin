package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.text.*;

import java.io.*;
import java.util.*;


public class RMIClientGUI_FileTreeView extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	
	private DefaultMutableTreeNode root;
	private DefaultTreeModel dtm;
	private JTree jtree;
	
	private JScrollPane jspFileTree;
	private JProgressBar jpbFileTree;
	
	public FileTreeThread FTT;
	
	public RMIClientGUI_Debugger rmiDebug;
	public RMIClientGUI_FileBrowser rmiGUI_FB;
	
	private Vector vSrcLocation = new Vector();
	private String filePasteType = "";
	
	
	public RMIClientGUI_FileTreeView(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug, RMIClientGUI_FileBrowser rmiGUI_FB) {
		this.rmiGUI_FB = rmiGUI_FB;
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		
		initGUI();
	}
	
	
	private void initGUI() {
		setPreferredSize(new Dimension(280, 250));
		setLayout(new BorderLayout());
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
	
	
	public RMIClientGUI_Debugger getDebugger() {
		return rmiDebug;
	}
	
	
	public void clearView() {
		removeAll();
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(280,250));
		updateUI();
	}
	
	
	public void openFileSystem() {
		FTT = new FileTreeThread(this);
		FTT.start();
	}
	
	
	public void refreshFileTreeView() {
		FTT.refresh();
	}
	
	
	public void refreshFileTreeView(String newLocation, String filename) {
		FTT.openFolder(newLocation, filename);
	}
	
	
	public void setLocation(String path) {
		FTT.setLocation(path);
	}
	
	
	public void setFilesToBePasted(Vector vSrcLocation, String filePasteType) {
		this.vSrcLocation = vSrcLocation;
		this.filePasteType = filePasteType;
		
		/****************************************
		 * This line is necessary as we have to 
		 * are overriding the copied objects in 
		 * the File Tree View by those from the
		 * File Table View
		 ***************************************/
		FTT.srcNode = null;
		
		FTT.paste.setEnabled(true);
	}
	
	
	public void clearFilesToBePasted() {
		vSrcLocation.removeAllElements();
		FTT.paste.setEnabled(false);
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
	
	
	public class FileTreeThread extends Thread implements TreeExpansionListener,TreeModelListener {
		RMIClientGUI_FileTreeView rmiGUI_FTV;
		RMIClient_FileBrowser rmi_FB;
		FileTreeThread FTT_Ptr;
		String srcLocation = "";
		String destLocation = "";
		DefaultMutableTreeNode srcNode = null;
		JMenuItem paste;
		boolean loadFileTable = true;
		
		public FileTreeThread(final RMIClientGUI_FileTreeView rmiGUI_FTV) {
			FTT_Ptr = this;
			this.rmiGUI_FTV = rmiGUI_FTV;
			this.rmi_FB = new RMIClient_FileBrowser(rmiGUI_FB.serverIP, 
													rmiGUI_FB.serverPort, 
													rmiGUI_FB.serverShell, 
													rmiGUI_FTV);
			
			try {
				//this.setPriority(MIN_PRIORITY);
				
				//====== Initializd the Global variable for JTree ======
				root = new DefaultMutableTreeNode("Remote File System");
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
						
						//System.out.println("Open Detail");
						//openDetail();
					}
				});
				//====================================================
				
				//====== Add Mouse Listener to the JTree ======
				/**********************************************
				 *
				 * This part is extremely useful, as we can 
				 * maintain the FileTableView to display 
				 * objects under the current selected node 
				 * in the FileTreeView.
				 *
				 * Such a design allow us to maintain a 
				 * static pointer to the selected node in the 
				 * FileTreeView, and makes the later design 
				 * of FileTableView easier, as we could 
				 * maintain consistent Object state in both 
				 * views.
				 *
				 *********************************************/
 				MouseListener ml = new MouseAdapter() {
 					public void mouseClicked(MouseEvent me) {
 						int selRow = jtree.getRowForLocation(me.getX(), me.getY());
 						
 						if(selRow != -1) {
 							if(me.getClickCount() == 1) {
 								refresh();
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
						new EditFileThread(rmiGUI_FTV, FTT_Ptr).start();
					}
				});
				
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
							//This invoke the file copying using source from Tree View
							new MovingFileObjectThread(rmiGUI_FTV, rmi_FB, FTT_Ptr).start();
							
						} else if (vSrcLocation.size() > 0) {
							//This invoke the file copying using source from Table View
							new MovingFileObjectThread(rmiGUI_FTV, rmi_FB, FTT_Ptr).start();
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
						new DeleteFileObjectThread(rmiGUI_FTV, rmi_FB, FTT_Ptr).start();
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
						new LoadFileSizeThread(FTT_Ptr).start();
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
								if (selectedNode.getPath().length == 2) {
									edit.setEnabled(false);
									
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
									
									Vector vFileInfo = rmi_FB.getFileInfo(location);
									
									//Test if remote drive is really ready
									if (vFileInfo == null) {
										warning("Error Detected! Please check whether:\n" + 
												"1. Drive is ready (e.g. Floppy/CD-Rom)\n" +
												"2. Drive is accessible with sufficient priviledges (e.g. root/Administrator)\n" + 
												"3. Drive has enough disk space\n");
									} else {
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
				drive = rmi_FB.openFileSystem();
				
				if (drive != null) {
					DefaultMutableTreeNode tmp;
					
					rmiDebug.setDebugText("Available drives on Remote File System: ");
					for (int i=0; i<drive.length; i++) {
						rmiDebug.setDebugText(drive [i]); //Debugger
						tmp = new DefaultMutableTreeNode(drive [i] + "");
						tmp.setAllowsChildren(true);
						root.add(tmp);
					}
					
					jtree.addTreeExpansionListener(this);
					
					jspFileTree = new JScrollPane(jtree);
					
					//====== Reload the File Browser Panel ======
					rmiGUI_FTV.removeAll();
					rmiGUI_FTV.add(jspFileTree);
					rmiGUI_FTV.setPreferredSize(new Dimension(280, 250));
					rmiGUI_FTV.updateUI();
					rmiGUI_FTV.revalidate();
					//===========================================
					
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
				            if ((arr_node [j] + "").equals("Remote File System") == false) {
					           	//System.out.println(arr_node [j]); //Debugger
					           	location += arr_node [j] + "/";
				            }
						}
						
						if (location.length() > 0) {
							//Optional Debugger
							rmiDebug.setDebugText("Convert file path string: " + location);
							
							//Check if it is a folder
							Vector vFileInfo = null;
							vFileInfo = rmi_FB.getFileInfo(location);
							if ((vFileInfo.elementAt(4)+"").equals("true")) {
								//String [][] ls = rmi_FB.getFileList(location);
								rmiDebug.setDebugText("Folder detail retrieval complete.");
							}
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
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						//Optional Debugger
						rmiDebug.setDebugText("Selected Object to edit : " + location);
						Vector vFileInfo = rmi_FB.getFileInfo(location);
						
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
							final String srcLocation = location;
							
							//Optional Debugger
							rmiDebug.setDebugText("Download (Ascii) - From " + srcLocation + " to " + tempLocation + " for editing");
							eft.rmi_FTR.downloadFile_Ascii(srcLocation, tempLocation);
							
							RMIClientGUI_FileEditor rmiGUI_FE = new RMIClientGUI_FileEditor(jdp, rmiDebug, rmiGUI_FTV, eft.rmi_FTR, eft.rmi_FTL, srcLocation, tempLocation, "remote");
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
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FB.getFileInfo(location);
						
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
				
				//Set the "Files to be pasted" on FileTableView
				rmiGUI_FB.setFilesToBePasted_FileTableView(srcLocation, filePasteType);
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
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FB.getFileInfo(location);
						
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
				
				//Set the "Files to be pasted" on FileTableView
				rmiGUI_FB.setFilesToBePasted_FileTableView(srcLocation, filePasteType);
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
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FB.getFileInfo(location);
						
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
									boolean duplicate = rmi_FB.checkDuplicateTarget(srcFile, destLocation);
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
										 int totalObject = rmi_FB.countObject(srcLocation);
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
										complete = rmi_FB.copyAndPaste(srcLocation, destLocation);
										
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
										complete = rmi_FB.cutAndPaste(srcLocation, destLocation);
										
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
								
								//Clear the "Files to be pasted" from FileTableView
								rmiGUI_FB.clearFilesToBePasted_FileTableView();
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
		
		private void pasteFiles(MovingFileObjectThread mfoT) {
			boolean overwriteAll = false;
			boolean cancelAll = false;
			int choice = 0;
			
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
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FB.getFileInfo(location);
						
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
									} //End of For Loop
									
									//Stop the progress thread here
									mfoT.stop = true;
									
									//Disable the paste function
									vSrcLocation.removeAllElements();
									paste.setEnabled(false);
									
									//Clear the "Files to be pasted" from FileTableView
									rmiGUI_FB.clearFilesToBePasted_FileTableView();
									
									//Refresh occur here
									refresh();
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
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FB.getFileInfo(location);
						
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
									String complete = rmi_FB.createFile(location, fileType);
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
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FB.getFileInfo(location);
						
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
								
								int totalObject = rmi_FB.countObject(location);
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
								complete = rmi_FB.deleteFile(location);
								
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
									refreshFileTreeView();
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
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								srcLocation += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FB.getFileInfo(srcLocation);
						
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
								String complete = rmi_FB.renameFile(srcLocation, destLocation);
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
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						//Optional Debugger
						rmiDebug.setDebugText("Open detail for: " + location);
						Vector vFileInfo = rmi_FB.getFileInfo(location);
						
						if ((vFileInfo.elementAt(4)+"").equals("true")) {
							rmiDebug.setDebugText("Getting size of: " + location + " recursively");
							
							//Get folder size recursively
							long sizeInLong = rmi_FB.getFileSize(location);
							
							//Replace the file size with rounded values
							String roundedSize = rmi_FB.getRoundedFileSize(sizeInLong);
							vFileInfo.setElementAt(roundedSize, 10);
							
						} else {
							//Replace the file size with rounded values
							String roundedSize = rmi_FB.getRoundedFileSize(location);
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
		
		private void openFolder(String newLocation, String filename) {
			try {
				TreePath selectedPath = jtree.getSelectionPath();
				jtree.expandPath(selectedPath); //Must expand the current path first, otherwise below won't refresh
				
				if (selectedPath != null) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)(selectedPath.getLastPathComponent());
					
					/**************************************
					 * If the current selected node is a 
					 * file, we've to shift the pointer to
					 * its parent such that we can call to
					 * refresh those folders which are in 
					 * this parent
					 *************************************/
					if (selectedNode.isLeaf()) {
						TreePath parentPath = selectedPath.getParentPath();
						jtree.setSelectionPath(parentPath);
						selectedNode = (DefaultMutableTreeNode)(parentPath.getLastPathComponent());
					}
					
					for (Enumeration e=selectedNode.children(); e.hasMoreElements();) {
						Object o = e.nextElement();
						String tmp = o + "";
						DefaultMutableTreeNode newNode = (DefaultMutableTreeNode)o;
						
						if (tmp.equals(filename) == true) {
							//System.out.println(tmp); //Debugger
							TreePath ePath = new TreePath(newNode.getPath());
							jtree.setSelectionPath(ePath);
							
							//Auto-scroll to the selected node
							int row = jtree.getMaxSelectionRow();
							//System.out.println(row); //Debugger
							jtree.scrollRowToVisible(row);
							
							//Here is where refresh occur
							refresh();
							
							break;
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
							if ((arr_node [j] + "").equals("Remote File System") == false) {
								//System.out.println(arr_node [j]); //Debugger
								location += arr_node [j] + "/";
							}
						}
						
						Vector vFileInfo = rmi_FB.getFileInfo(location);
						
						//Make sure we're refreshing a folder, otherwise error may occur
						if ((vFileInfo.elementAt(4)+"").equals("true")) {
							FileTreeProgressThread progress;
							
							if (selectedNode.isLeaf() == false) {
								//Remove then Re-add all the childs in current path
								progress = new FileTreeProgressThread(rmiGUI_FTV, rmi_FB, FTT, "up");
								progress.start();
							} else {
								//Remove then Re-add all the childs in current path
								progress = new FileTreeProgressThread(rmiGUI_FTV, rmi_FB, FTT, "down");
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
						
						Vector vFileInfo = rmi_FB.getFileInfo(location);
						
						//Make sure we're refreshing a folder, otherwise error may occur
						if ((vFileInfo.elementAt(4)+"").equals("true")) {
							FileTreeProgressThread progress;
							
							if (selectedNode.isLeaf() == false) {
								//Remove then Re-add all the childs in current path
								progress = new FileTreeProgressThread(rmiGUI_FTV, rmi_FB, FTT, "up");
								progress.start();
							} else {
								//Remove then Re-add all the childs in current path
								progress = new FileTreeProgressThread(rmiGUI_FTV, rmi_FB, FTT, "down");
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
		//==============================================
		
		//====== Required by TreeExpansionListener interface ======
		public void treeExpanded(TreeExpansionEvent e) {
	        try {
				rmiDebug.setDebugText("Tree-expanded event detected. " + e.getSource()); //Debugger
				jtree.setSelectionPath(e.getPath());
				//rmiDebug.setDebugText(jtree.getSelectionPath()); //Debugger
				
	        	FileTreeProgressThread progress = new FileTreeProgressThread(rmiGUI_FTV, rmi_FB, this, "down");
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
				
				String remotePath = "";
				String separator = rmi_FB.getFileSeparator();
				
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
			            	if ((arr_node [j] + "").equals("Remote File System") == false) {
				            	//System.out.println(arr_node [j]); //Debugger
				            	location += arr_node [j] + "/";
				            	
				            	if ((arr_node [j]+"").indexOf(separator) < 0) {
				            		remotePath += arr_node [j] + "" + separator;
				            	} else {
				            		remotePath += arr_node [j] + "";
				            	}
			            	}
			            }
			            
			            //Optional Debugger
			            rmiDebug.setDebugText("Convert file path string: " + location);
			            
						String [][] ls = rmi_FB.getFileList(location);
						
			            //Load the location into File Table View
			            if (loadFileTable == true) {
			            	rmiGUI_FB.refreshFileTableView(location, ls);
			            }
						
						jpbFileTree.setMaximum(ls.length);
						jpbFileTree.setValue(0);
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
							jpbFileTree.setValue(i);
							
							dtm.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
						}
						
						rmiDebug.setDebugText("Folder detail retrieval complete.");
						rmiGUI_FB.setRemotePath(remotePath);
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
				
			} finally {
				rmiDebug.setDebugText("Tree Expand completed.");
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
		        	
					jpbFileTree.setMaximum(parentNode.getChildCount());
					jpbFileTree.setValue(0);
		        	rmiDebug.setDebugText("Removing current childs for folder: " + parentPath + ", please wait......");
		        	
		        	int i = 1;
		        	while (parentNode.getChildCount()>0) {
		        		childNode = (DefaultMutableTreeNode)parentNode.getChildAt(0);
		        		
						//System.out.println(i); //Debugger
						jpbFileTree.setValue(i);
		        		
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
	    
		private void setLocation(String path) {
			try {
				//Debug mode
				//path = "G:\\aFolder\\aFolder2\\"; //Debugger
				//path = "G:\\aFolder\\aFolder2"; //Debugger
				
				NavigateFileThread nft = new NavigateFileThread(rmiGUI_FTV, FTT_Ptr, path);
				nft.start();
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private DefaultMutableTreeNode traverseNode(String path, String separator) {
			DefaultMutableTreeNode currNode = null;
			
			try {
				/*******************************************************************
				 * Point to the root node, expand it
				 * Traverse the child under root node
				 * Get the first matching node (compare with remote path)
				 * Expand the matching node
				 * Traverse the child under it
				 * Do this recursively until finally reach the node
				 *
				 * Actual Implementation Details:
				 * 1> Must invoke a new thread - NavigateFileThread (NFT)
				 * 2> Within the NFT, we invoke the method setLocation()
				 * 3> And within the setLocation(), we invoke the getDownLevel()
				 * 4> Use a boolean to indicate the updating of FileTableView is 
				 *    not neccessary
				 * 5> Use a line message at the debug console to detect if each 
				 *    traverse (adding of node) is completed
				 *
				 *******************************************************************/
					
				rmiDebug.setDebugText("****" + path);
				
				if (path.lastIndexOf(separator) == path.length()-1) {
					path = path.substring(0, path.length()-1);
				}
				
				TreePath currPath = jtree.getSelectionPath();
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)(currPath.getLastPathComponent());
				currNode = null; //Set the currNode to null such that if no matching node in the folder, null is returned
				
				for (Enumeration e=parentNode.children(); e.hasMoreElements();) {
					DefaultMutableTreeNode tmp = (DefaultMutableTreeNode)e.nextElement();
					String childPath = tmp.getUserObject().toString();
					
					/********************************************
					 * Use the File Separator to determine 
					 * if remote system is Unix-like or MS-Base,
					 * if MS-Base then ignore case matching
					 *******************************************/
					if (separator.equals("\\")) {
						childPath = childPath.toLowerCase();
						path = path.toLowerCase();
					}
					
					if (childPath.lastIndexOf(separator) == childPath.length()-1) {
						childPath = childPath.substring(0, childPath.length()-1);
					}
					
					if (childPath.equals(path)) {
						rmiDebug.setDebugText(childPath + " ### " + path);
						currNode = tmp;
						
						break;
					}
				}
				
				rmiDebug.setDebugText("No. of child for: " + parentNode.getUserObject().toString() + " = " + parentNode.getChildCount());
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
			
			return currNode;
		}
	}
	
	
	public class FileTreeProgressThread extends Thread {
		RMIClientGUI_FileTreeView rmiGUI_FTV;
		RMIClient_FileBrowser rmi_FB;
		FileTreeThread FTT;
		boolean stop = false;
		String type;
		
		public FileTreeProgressThread(RMIClientGUI_FileTreeView rmiGUI_FTV, RMIClient_FileBrowser rmi_FB, FileTreeThread FTT, String type) {
			this.rmiGUI_FTV = rmiGUI_FTV;
			this.rmi_FB = rmi_FB;
			this.FTT = FTT;
			this.type = type;
		}
		
		public void run() {
			try {
				jpbFileTree = new JProgressBar();
		        jpbFileTree.setValue(0);
		        jpbFileTree.setStringPainted(true);
		        jpbFileTree.setBorderPainted(true);
		        
		        rmiGUI_FTV.removeAll();
		        rmiGUI_FTV.setLayout(new FlowLayout(FlowLayout.CENTER));
		        rmiGUI_FTV.add(jpbFileTree);
		        rmiGUI_FTV.updateUI();
		        rmiGUI_FTV.revalidate();
		        
		        if (type.equals("up")) {
		        	FTT.getUpLevel();
		        	FTT.getDownLevel();
		        } else if (type.equals("down")) {
		        	FTT.getDownLevel();
		        }
		        
		        rmiGUI_FTV.removeAll();
		        rmiGUI_FTV.setLayout(new BorderLayout());
		        rmiGUI_FTV.add(jspFileTree, BorderLayout.CENTER);
		        rmiGUI_FTV.updateUI();
		        rmiGUI_FTV.revalidate();
		        
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
		RMIClientGUI_FileTreeView rmiGUI_FTV;
		RMIClient_FileBrowser rmi_FB;
		FileTreeThread FTT;
		boolean stop = false;
		JInternalFrame jif;
		JProgressBar jpb;
		String destLocation;
		JLabel jlbMessage;
		
		public MovingFileObjectThread(RMIClientGUI_FileTreeView rmiGUI_FTV, RMIClient_FileBrowser rmi_FB, FileTreeThread FTT) {
			this.rmiGUI_FTV = rmiGUI_FTV;
			this.rmi_FB = rmi_FB;
			this.FTT = FTT;
			
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
				
				if (FTT.srcNode != null) {
					FTT.paste(this);
				} else if (vSrcLocation.size() > 0) {
					FTT.pasteFiles(this);
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
						
						int counter = rmi_FB.countObject(destLocation);
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
		FileTreeThread FTT;
		
		public LoadFileSizeThread(FileTreeThread FTT) {
			this.FTT = FTT;
		}
		
		public void run() {
			FTT.detail();
		}
	}
	
	
	public class EditFileThread extends Thread {
		RMIClientGUI_FileTreeView rmiGUI_FTV;
		FileTreeThread FTT;
		RMIClient_FileTransferRemote rmi_FTR;
		RMIClient_FileTransferLocal rmi_FTL;
		
		public EditFileThread(RMIClientGUI_FileTreeView rmiGUI_FTV, FileTreeThread FTT) {
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
	
	
	public class NavigateFileThread extends Thread {
		RMIClientGUI_FileTreeView rmiGUI_FTV;
		FileTreeThread FTT;
		String originalPath = "";
		String path = "";
		String separator = "";
		
		public NavigateFileThread(RMIClientGUI_FileTreeView rmiGUI_FTV, FileTreeThread FTT, String path) {
			this.rmiGUI_FTV = rmiGUI_FTV;
			this.FTT = FTT;
			this.path = path;
			this.originalPath = this.path;
		}
		
		public void run() {
			try {
				separator = FTT.rmi_FB.getFileSeparator();
				
				// To retrieve the rootNode
				TreePath currPath = new TreePath(root.getPath());
				DefaultMutableTreeNode currNode = (DefaultMutableTreeNode)(currPath.getLastPathComponent());
				jtree.setSelectionPath(currPath);
				
				//Notify the updating in FileTable view is not necessary
				FTT.loadFileTable = false;
				
				while (path.length()>0) {
					if (path.lastIndexOf(separator) != path.length()-1) {
						path = path + separator;
					}
					
					String tmp = path.substring(0, path.indexOf(separator)+1);
					path = path.substring(path.indexOf(separator)+1, path.length());
					
					currPath = new TreePath(currNode.getPath());
					jtree.setSelectionPath(currPath);
					
					if (jtree.hasBeenExpanded(currPath) == false) {
					jtree.expandPath(currPath);
					
					String key = "Tree Expand completed.\n";
						while (true) {
							if (rmiDebug.getDebugText().lastIndexOf(key) == rmiDebug.getDebugText().length()-key.length()) {
								break;
							} else {
								rmiDebug.setDebugText("NavigateFileThread waiting...");
								Thread.sleep(100); //wait for 0.1 sec before retry
							}
						}
					}
					
					//Thread.sleep(10000); //This is a very very useful debugger
					
					rmiDebug.setDebugText("#### Navigate done");
					currNode = FTT.traverseNode(tmp, separator);
					
					if (currNode == null) {
						break;
					}
					
					rmiDebug.setDebugText("\n");
					rmiDebug.setDebugText(currNode.getUserObject().toString());
					rmiDebug.setDebugText("################################################\n");
				}
				
				if (currNode != null) {
					//Notify the updating in FileTable view is necessary
					FTT.loadFileTable = true;
					
					currPath = new TreePath(currNode.getPath());
					jtree.setSelectionPath(currPath);
					jtree.expandPath(currPath);
					
					//Auto-scroll to the selected node
					int row = jtree.getMaxSelectionRow();
					//System.out.println(row); //Debugger
					jtree.scrollRowToVisible(row);
					
					FTT.refresh();
					
				} else {
					warning("Path: " + originalPath + " not found!\n" + 
							"Make sure you've typed the correct path.\n" + 
							"For Unix-like system, the path is case-sensitive.\n\n"
							);
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
				
			} finally {
				//Notify the updating in FileTable view is necessary
				FTT.loadFileTable = true;
			}
		}
	}
	
	
	public class DeleteFileObjectThread extends Thread {
		RMIClientGUI_FileTreeView rmiGUI_FTV;
		RMIClient_FileBrowser rmi_FB;
		FileTreeThread FTT;
		boolean stop = false;
		JInternalFrame jif;
		JProgressBar jpb;
		String destLocation;
		JLabel jlbMessage;
		
		public DeleteFileObjectThread(RMIClientGUI_FileTreeView rmiGUI_FTV, RMIClient_FileBrowser rmi_FB, FileTreeThread FTT) {
			this.rmiGUI_FTV = rmiGUI_FTV;
			this.rmi_FB = rmi_FB;
			this.FTT = FTT;
			
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
				
				FTT.delete(this);
				
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