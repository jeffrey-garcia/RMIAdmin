package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.io.*;
import java.util.*;


/************************************
 * Problem of this program: 
 * 1> Harder implementation of update
 * 2> Can never display full text
 *
 * Benefit of this program:
 * 1> Able to trace the progress
 * 2> Data is fetched by buffering
 * 3> Can define buffer size
 * 4> CPU & Memory usage depends on 
 *    buffer size
 *
 * Refer testRAF.java for the update
 * algorithm
 ************************************/
public class RMIClientGUI_FileEditor {
	private JDesktopPane jdp;
	
	public RMIClientGUI_Debugger rmiDebug;
	public RMIClientGUI_Function rmiGUI_F;
	public RMIClient_FileTransferRemote rmi_FTR;
	public RMIClient_FileTransferLocal rmi_FTL;
	public String srcLocation;
	public String tempLocation;
	
    private String file = "";
    private RandomAccessFile raf = null;
    private byte [] b;
    private long remainSize;
    private byte [] ref_b;
    
    private JInternalFrame jif;
    private JLabel jlbMessage;
    private JTextArea jtaContent;
    private JButton jbtnLoad;
    private JButton jbtnQuit;
    private JButton jbtnDown;
    private JButton jbtnUp;
    private JButton jbtnSetBuffer;
    private JButton jbtnSave;
    private JButton jbtnCancel;
    
    /**********************************
     * action = 0...Load File
     * action = 1...Get Next Segment
     * action = 2...Get Prev Segment
     * action = 3...Save File
     *********************************/
    private int action = 0;
    
    //Can be remote or local
    private String editType;
    
    
    public static void main(String [] args) {
        RMIClientGUI_FileEditor rmiGUI_FE = new RMIClientGUI_FileEditor();
        //rmiGUI_FE.setFile("C:/j2sdk1.4.0/002_dev/RMIClientGUI_TransferRemoteView.java");
        //rmiGUI_FE.setFile("event.log");
        rmiGUI_FE.setFile("test.txt");
        rmiGUI_FE.setBuffer(1024000);
        rmiGUI_FE.init();
    }
    
    
    public RMIClientGUI_FileEditor() {
    }
    
    
    public RMIClientGUI_FileEditor(JDesktopPane jdp, 
    								RMIClientGUI_Debugger rmiDebug, 
    								RMIClientGUI_Function rmiGUI_F, 
    								RMIClient_FileTransferRemote rmi_FTR,
    								RMIClient_FileTransferLocal rmi_FTL,
    								String srcLocation,
    								String tempLocation,
    								String editType
    								) {
    	
    	JFrame.setDefaultLookAndFeelDecorated(true);
    	
    	this.jdp = jdp;
    	this.rmiDebug = rmiDebug;
    	this.rmiGUI_F = rmiGUI_F;
    	this.rmi_FTR = rmi_FTR;
    	this.rmi_FTL = rmi_FTL;
    	this.srcLocation = srcLocation;
    	this.tempLocation = tempLocation;
    	this.editType = editType;
    }
    
    
    public void init() {
        try {
            //Initialize the GUI
            jif = new JInternalFrame("File Editor - " + new File(file).getName());
            jif.getContentPane().setLayout(new BorderLayout());
            
                //====== This part is the Top Panel ======
                JPanel jpTop = new JPanel();
                jpTop.setLayout(new FlowLayout(FlowLayout.LEFT));
                
                    jlbMessage = new JLabel();
                    jlbMessage.setText("File not loaded.");
                
                jpTop.add(jlbMessage);
                //========================================
                
                //=== This part is the file content panel ===
                jtaContent = new JTextArea();
                jtaContent.setEditable(true);
                jtaContent.setBackground(Color.BLACK);
                jtaContent.setForeground(Color.lightGray);
                jtaContent.setToolTipText("File Content");
				jtaContent.addMouseListener(new RMIClientGUI_MouseListener());
                
                JScrollPane jspContent = new JScrollPane(jtaContent,
                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                                    
                jspContent.setBorder(
                    BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("File Content"),
                        BorderFactory.createEmptyBorder(5,5,5,5)
                    )
                );
                
                jspContent.setPreferredSize(new Dimension(400,300));
                jspContent.setMinimumSize(new Dimension(400,300));
                //===========================================
                
                //====== This part is the function panel ======
                JPanel jpBottom = new JPanel();
                jpBottom.setLayout(new FlowLayout(FlowLayout.CENTER));
                
                    jbtnLoad = new JButton("Reload");
                    jbtnLoad.setToolTipText("Click this button to reload the file");
                    jbtnLoad.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent ae) {
                            action = 0;
                            new LoadFileThread().start();
                        }
                    });
                
                    jbtnQuit = new JButton("Quit");
                    jbtnQuit.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent ae) {
							quit();
							
							boolean deletion = rmi_FTR.removeTempFile(file);
							rmiDebug.setDebugText("Temp file: " + file + " deletion result=" + deletion);
						}
                    });
                    
                    jbtnDown = new JButton("Next Pg");
                    jbtnDown.setToolTipText("Click this button to go to next page");
                    jbtnDown.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent ae) {
                            action = 1;
                            new LoadFileThread().start();
                        }
                    });
                    jbtnDown.setEnabled(false);
                    
                    jbtnUp = new JButton("Prev Pg");
                    jbtnUp.setToolTipText("Click this button to back to previous page");
                    jbtnUp.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent ae) {
                            action = 2;
                            new LoadFileThread().start();
                        }
                    });
                    jbtnUp.setEnabled(false);
                
                	jbtnSetBuffer = new JButton("Buffer");
                	jbtnSetBuffer.setToolTipText("Click this button to setup the amount of data loaded each page");
                    jbtnSetBuffer.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent ae) {
                            setBufferGUI();
                        }
                    });
                    
                    jbtnSave = new JButton("Save");
                    jbtnSave.setToolTipText("Click this button to save the file");
                    jbtnSave.addActionListener(new ActionListener(){
                    	public void actionPerformed(ActionEvent ae) {
                    		action = 3;
                    		new LoadFileThread().start();
                    	}
                    });
                    
					JButton jbtnCancel = new JButton("Cancel");
					jbtnCancel.setToolTipText("Click this button to cancel editing the file and abort any modification");
					jbtnCancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							cancel();
							
							boolean deletion = rmi_FTR.removeTempFile(file);
							rmiDebug.setDebugText("Temp file: " + file + " deletion result=" + deletion);
						}
					});
                
                jpBottom.add(jbtnLoad);
                jpBottom.add(jbtnSetBuffer);
                jpBottom.add(jbtnDown);
                jpBottom.add(jbtnUp);
                //jpBottom.add(jbtnSave); //Debug mode
                jpBottom.add(jbtnQuit);
                jpBottom.add(jbtnCancel);
                //=============================================
            
            jif.getContentPane().add(jpTop, BorderLayout.NORTH);
            jif.getContentPane().add(jspContent, BorderLayout.CENTER);
            jif.getContentPane().add(jpBottom, BorderLayout.SOUTH);
            jif.pack();
			jif.setIconifiable(true);
			jif.setMaximizable(true);
			jif.setResizable(true);
			jif.setClosable(false);
			
			int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
			int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
			
			jif.setLocation(x,y);
			
			jdp.add(jif);
			try {
				jif.setSelected(true);
			} catch (Exception exc) {
				//Nothing to do
			}
			jif.show();
            
        } catch (Exception exc) {
            exc.printStackTrace();
            warning(exc.getMessage());
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
		
		jdg = jop.createDialog((JFrame)jdp.getTopLevelAncestor(),"Warning!");
		
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
    
    
    public void setFile(String s) {
        file = s;
    }
    
    
    public void setBuffer(int i) {
        b = new byte [i];
    }
    
    
    private void setBufferGUI_old() {
    	try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane jop;
			JDialog jdg;
			String opt = 	"<html>Input Buffer Value" + "&nbsp;" + 
							"(<font color='red'>File will be reloaded.</font>)" + "<br><br>" + 
							"<font color='blue'>" + 
							"Use this value to set how many byte(s) of " + "<br>" +
							"data will be fetched to memory each time." + "<br>" + 
							"For large file size, it is recommended to use " + "<br>" + 
							"this value to avoid Buffer Overflow against " + "<br>" + 
							"insufficient memeory." + "<br>" + 
							"<font color='green'>" + "Max Value = 1024000 (1 MB)" + 
							"</font>" + "<br><br>" + 
							"</font>" + 
							"</html>";
			
			jop = new JOptionPane(opt,
									JOptionPane.INFORMATION_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION);
			jop.setWantsInput(true);
			jop.setInitialSelectionValue(b.length + "");
			
			jdg = jop.createDialog(jdp, "Setup Buffer");
			jdg.setVisible(true);
			
			String buffer = (String)jop.getInputValue();
			
			if (buffer.equals("uninitializedValue")==false) {
				if (buffer.length() == 0) {
					setBufferGUI();
					
				} else {
					try {
						int i = Integer.parseInt(buffer);
						b = new byte [i];
						
						if (i>0 && i<=1024000) {
							action = 0;
							new LoadFileThread().start();
						} else {
							setBufferGUI();
						}
						
					} catch (Exception exc) {
						setBufferGUI();
					}
				}
			}
    		
    	} catch (Exception exc) {
            exc.printStackTrace();
            warning(exc.getMessage());
        }
    }
    
    
    private void setBufferGUI() {
    	try {
			JTextField jtf = new JTextField(10);
			jtf.setText(b.length + "");
			jtf.addMouseListener(new RMIClientGUI_MouseListener());
			
			Object[] msg = {
				"<html>Input Buffer Value" + "&nbsp;" + 
				"(<font color='red'>File will be reloaded.</font>)" + "<br><br>" + 
				"<font color='blue'>" + 
				"Use this value to set how many byte(s) of " + "<br>" +
				"data will be fetched to memory each time." + "<br>" + 
				"For large file size, it is recommended to use " + "<br>" + 
				"this value to avoid Buffer Overflow against " + "<br>" + 
				"insufficient memeory." + "<br>" + 
				"<font color='green'>" + "Max Value = 1024000 (1 MB)" + 
				"</font>" + "<br><br>" + 
				"</font>" + 
				"</html>", 
				jtf
			};
			
			int result = JOptionPane.showConfirmDialog((JFrame)jdp.getTopLevelAncestor(), 
														msg, "Setup Buffer", 
														JOptionPane.OK_CANCEL_OPTION, 
														JOptionPane.INFORMATION_MESSAGE);
			String buffer = "";
			if(result == JOptionPane.OK_OPTION) {
				buffer = jtf.getText();
				
				if(!buffer.equals("")) {
					try {
						int i = Integer.parseInt(buffer);
						b = new byte [i];
						
						if (i>0 && i<=1024000) {
							action = 0;
							new LoadFileThread().start();
						} else {
							setBufferGUI();
						}
						
					} catch (Exception exc) {
						setBufferGUI();
					}
					
				} else {
					setBufferGUI();
				}
				
			} else {
				//Set buffer cancelled, nothing to do...
			}
    		
    	} catch (Exception exc) {
    		exc.printStackTrace();
    		warning(exc.getMessage());
    	}
    }
    
    
    public void loadFile() {
        try {
            raf = new RandomAccessFile(file, "rw");
            System.out.println("File size: " + raf.length());
            System.out.println("File loaded successfully");
            System.out.println("********************************************");
            
            /*****************************************
             * This value must be mutiplication of 
             * and bigger than the buffer size
             ****************************************/
            //raf.seek(512); //Important Debugger
            
            //=== put all the operations here ===
            //System.out.println("Current pointer: " + raf.getFilePointer());
            remainSize = raf.length() - raf.getFilePointer();
            
            jlbMessage.setText("Buffering data into Memory...");
            
            if (remainSize  == 0) {
                System.out.println("End of file");
                
                jbtnDown.setEnabled(false);
                jbtnUp.setEnabled(false);
                
            } else {
                if (b.length < remainSize) {
                    getNextSegment();
                } else {
                    getNextSegment();
                }
            }
            //===================================
            
            jlbMessage.setText("File Size: " + raf.length() + " byte(s) " + 
                                "Buffer Size: " + b.length + " byte(s)"
                              );
            
        } catch (Exception exc) {
            exc.printStackTrace();
            warning(exc.getMessage());
        }
    }
    
    
    private void getNextSegment() {
        try {
            //=== put all the operations here ===
            System.out.println("Current pointer: " + raf.getFilePointer());
            
            long curr = raf.getFilePointer();
            remainSize = raf.length() - raf.getFilePointer();
            
            if (remainSize == 0) {
            	//At end of of file
            	System.out.println("At end of file");
            	
            } else {
            	if (remainSize >= b.length) {
            		/***************************************************
            		 * remaining portion exceed the size of buffer
            		 * CAN be either the first portion, intermediate 
            		 * portion or the last portion of file
             		 **************************************************/
            		
            		if (curr == 0) {
            			//Beginning portion
            			raf.readFully(b);
	            		jtaContent.setText(new String (b));
	            		jtaContent.setCaretPosition(0);
            			
            			//File is at beginning, down enable
            			jbtnDown.setEnabled(true);
            			
            			//File is at beginning, up disable
            			jbtnUp.setEnabled(false);
            			
            		} else if (remainSize == b.length) {
            			//Last portion
            			raf.readFully(b);
	            		jtaContent.setText(new String (b));
	            		jtaContent.setCaretPosition(0);
	            		
            			//File is at end, down disable
            			jbtnDown.setEnabled(false);
            			
            			//File is at end, up enable
            			jbtnUp.setEnabled(true);
            			
            		} else if (remainSize > b.length) {
            			//Intermediate portion
            			raf.readFully(b);
	            		jtaContent.setText(new String (b));
	            		jtaContent.setCaretPosition(0);
	            		
            			//File is at intermediate, down enable
            			jbtnDown.setEnabled(true);
            			
            			//File is at intermediate, up enable
            			jbtnUp.setEnabled(true);
            		}
            		
            	} else {
            		/***************************************************
            		 * remaining portion is within the size of buffer,
            		 * MUST be end of file or the last portion of file
            		 **************************************************/
            		 
            		byte [] tmp_b = new byte [new Long(remainSize).intValue()];
            		raf.readFully(tmp_b);
            		jtaContent.setText(new String (tmp_b));
            		jtaContent.setCaretPosition(0);
            		
            		//File can go not futher, down disable
            		jbtnDown.setEnabled(false);
            		
            		if (curr == 0) {
            			//File is at beginning, up disable
            			jbtnUp.setEnabled(false);
            		} else {
            			//File is at end, up enable
            			jbtnUp.setEnabled(true);
            		}
            	}
            }
            
            //Save the text from current console
            ref_b = jtaContent.getText().getBytes();
            System.out.println("Saved text: " + new String(ref_b));
            
            //===================================
            System.out.println("Updated pointer: " + raf.getFilePointer());
            
        } catch (Exception exc) {
            exc.printStackTrace();
            warning(exc.getMessage());
        }
    }
    
    
    private void getPrevSegment() {
    	/******************************************
    	 * This method is a somewhat different 
    	 * from getNextSegment, since this method 
    	 * does not cater for the change of button 
    	 * state.
    	 * 
    	 * In fact this method only deals with 
    	 * pointer, and the verifying procedure 
    	 * is left behind when it calls the 
    	 * getNextSegment() method
    	 *****************************************/
    	 
        try {
            //=== put all the operations here ===
            System.out.println("Current pointer: " + raf.getFilePointer());
            
            long curr = raf.getFilePointer();
            remainSize = raf.length() - raf.getFilePointer();
            
            if (curr == 0) {
            	//At head of file
            	System.out.println("At head of file");
            
            } else {
            	if (remainSize == 0) {
	            	//Last Portion
	            	long remainder;
	            	
	            	if (ref_b.length > b.length) {
	            		remainder = ref_b.length;
	            	} else {
	            		remainder = raf.length() % b.length;
	            	}
	            	
	            	if (remainder == 0) {
	            		/********************************************
	            		 * Remaining portion contain text equal to 
	            		 * the size of buffer
	            		 *******************************************/
	            		
						/********************************************
						 * Here we use b.length*2 such that we can
						 * set the pointer back to the beggining of 
						 * previous segment, not just only the head 
						 * of the current segment.
						 *
						 * Moreover we don't have to worry about if 
						 * the current segment does really have a 
						 * previous segment, since this is already 
						 * handled by the getNextSegment() method, 
						 * and this method can be invoked only when: 
						 * It have prevous segment (not head of file)
						 *******************************************/
						raf.seek(raf.getFilePointer() - b.length*2);
						getNextSegment();
						
					} else if (remainder > b.length) {
	            		/********************************************
	            		 * Remaining portion contain text more than 
	            		 * the size of buffer, this may happen 
	            		 * actually when we write new data to the
	            		 * file
	            		 *******************************************/
	            		
						raf.seek(raf.getFilePointer() - ref_b.length - b.length);
						getNextSegment();
						
	            	} else {
	            		/********************************************
	            		 * Remaining portion contain text less than 
	            		 * the size of buffer
	            		 *******************************************/
	            		
	            		raf.seek(raf.getFilePointer() - remainder - b.length);
	            		getNextSegment();
	            	}
	            	
            	} else {
            		//Intermdiate portion
            		
            		if (ref_b.length > b.length) {
            			raf.seek(raf.getFilePointer() - ref_b.length - b.length);
            		} else {
            			raf.seek(raf.getFilePointer() - b.length*2);
            		}
            		
            		getNextSegment();
            	}
            }
            
            System.out.println("Updated pointer: " + raf.getFilePointer());
            //===================================
            
        } catch (Exception exc) {
            exc.printStackTrace();
            warning(exc.getMessage());
        }
    }
    
    
    private void saveFile() {
    	try {
    		long curr = raf.getFilePointer();
    		remainSize = raf.length() - raf.getFilePointer();
    		
    		//=== put all the operations here ===
    		if (remainSize == 0) {
    			/*******************************************************
    			 * At end of file, current portion is displaying the 
    			 * whole file, and its size must be either same as or
    			 * smaller than the buffer size
    			 ******************************************************/
    			
    			//Retrieve the remaining data, prepared for relocaing
    			byte [] remain_b = new byte [new Long(remainSize).intValue()];
    			raf.readFully(remain_b);
    			raf.seek(curr);
    			
    			//Retrieve the new data, prepared for writing to file
    			byte [] new_b = jtaContent.getText().getBytes();
    			
    			if (curr == 0) {
    				//File is empty
    				System.out.println("File is empty"); //Debugger
    				
    				//Write the new data
    				raf.seek(curr);
    				raf.write(new_b);
    				
    				//No remain data 
    				
    				//Set the new pointer
    				raf.seek(curr + new_b.length);
    				
    			} else {
    				if (raf.length() > b.length) {
    					/*********************************
    					 * File size is bigger than the 
    					 * buffer, hence this portion is
    					 * exactly the last portion of 
    					 * the current file
    					 ********************************/
    					
    					long remainder;
    					
    					/*****************************
    					 * This part detect if the 
    					 * original content size of 
    					 * this portion exceed the 
    					 * size of the buffer, if yes 
    					 * we then use the original 
    					 * content size to trace back 
    					 * the pointer
    					 ****************************/
    					if (ref_b.length > b.length) {
    						remainder = ref_b.length;
    						System.out.println("Remainder (1): " + remainder); //Debugger
    					} else {
    						remainder = raf.length() % b.length;
    						
    						if (remainder == 0) remainder = 2;
    						System.out.println("Remainder (2): " + remainder); //Debugger
    					}
    					
	    				//Write the new data
	    				raf.seek(curr - remainder);
	    				raf.write(new_b);
	    				
	    				//No remain data 
	    				
	    				System.out.println("Prev String: " + new String(ref_b));
	    				if (new_b.length < ref_b.length) {
		    				//Cut-off the obsoleted file size at the end
		    				System.out.println("Old size:" + raf.length()); //Debugger
		    				raf.setLength(raf.length() - (ref_b.length - new_b.length));
		    				System.out.println("New size:" + raf.length()); //Debugger
	    				}
	    				
	    				//Set the new pointer
	    				//System.out.println("*" + curr);
	    				//System.out.println("*" + remainder);
	    				//System.out.println("*" + new_b.length);
	    				raf.seek(curr - remainder + new_b.length);
	    				//raf.seek(curr);
	    				
    				} else if (raf.length() < b.length) {
    					/*********************************
    					 * File size is smaller than the 
    					 * buffer size, therefore the 
    					 * current portion must be the 
    					 * only portion which already 
    					 * display the whole file cotent
    					 ********************************/
    					
	    				//Write the new data
	    				raf.seek(0);
	    				raf.write(new_b);
	    				
	    				//No remain data 
    					
    					//Cut-off the obsoleted data
	    				System.out.println("Old size:" + raf.length()); //Debugger
	    				raf.setLength(new_b.length);
	    				System.out.println("New size:" + raf.length()); //Debugger
	    				
	    				//Set the new pointer
	    				raf.seek(0 + new_b.length);
		    			
    				} else {
    					/*********************************
    					 * File size is equal to the 
    					 * buffer size, therefore the 
    					 * current portion must be the 
    					 * only portion which already 
    					 * display the whole file cotent
    					 ********************************/
    					
	    				//Write the new data
	    				raf.seek(0);
	    				raf.write(new_b);
	    				
	    				//No remain data 
	    				
	    				//Set the new pointer
	    				raf.seek(0 + b.length);
    				}
    			}
    			
    		} else {
    			/*******************************************************
    			 * Still have remaining portion, current portion must 
    			 * have size same as the buffer
    			 ******************************************************/
    			
    			//Retrieve the remaining data, prepared for relocaing
    			byte [] remain_b = new byte [new Long(remainSize).intValue()];
    			raf.readFully(remain_b);
    			raf.seek(curr);
    			
    			//Retrieve the new data, prepared for writing to file
    			byte [] new_b = jtaContent.getText().getBytes();
    			
    			if (new_b.length > b.length) {
    				/***************************************************
    				 * Size of new data is bigger than size of buffer, 
    				 * we therefore have to move the remaining data 
    				 * forward
    				 **************************************************/
    				
    				//Write the new data first
    				raf.seek(curr - b.length);
    				raf.write(new_b);
    				
    				//Write the remain data with position shift forward
    				raf.seek(curr - b.length + new_b.length);
    				System.out.println(raf.getFilePointer());
    				raf.write(remain_b);
    				
    				//Set the new pointer
    				raf.seek(curr - b.length + new_b.length);
    				
    			} else if (new_b.length < b.length) {
    				/***************************************************
    				 * Size of new data is bigger than size of buffer, 
    				 * we therefore have to move the remaining data 
    				 * backward
    				 **************************************************/
    				
    				//Write the new data first
    				raf.seek(curr - b.length);
    				raf.write(new_b);
    				System.out.println(new String(new_b));
    				
    				//Write the remain data with position shift backward
    				raf.seek(curr - b.length + new_b.length);
    				raf.write(remain_b);
    				
    				//Cut-off the obsoleted file size at the end
    				System.out.println("Old size:" + raf.length()); //Debugger
    				raf.setLength(raf.length() - (b.length - new_b.length));
    				System.out.println("New size:" + raf.length()); //Debugger
    				
    				//Set the new pointer
    				raf.seek(curr - b.length + new_b.length);
    				
    			} else {
    				/***************************************************
    				 * Size of new data is equal to size of buffer, 
    				 * Moving of the remaining data is not required
    				 **************************************************/
    				
    				//Write the new data first
    				raf.seek(curr - b.length);
    				raf.write(new_b);
    				
    				//Write the remain data with NO position shift
    				raf.seek(curr); //original position
    				raf.write(remain_b);
    				
    				//Set the new pointer
    				raf.seek(curr - b.length + new_b.length);
    			}
    		}
    		
    		//put the newly saved text into ref_b
    		ref_b = jtaContent.getText().getBytes();
    		
    		//===================================
    		System.out.println("Reset Pointer to: " + raf.getFilePointer());
    		
    	} catch (Exception exc) {
    		exc.printStackTrace();
    		warning(exc.getMessage());
    	}
    }
    
    
    private void unloadFile() {
        try {
            System.out.println("File unloaded successfully\n\n");
            raf.close();
            
        } catch (Exception exc) {
            exc.printStackTrace();
            warning(exc.getMessage());
        }
    }
    
    
    private void cancel() {
    	try {
            if (raf != null) {
                unloadFile();
            }
            
            rmiDebug.setDebugText("Overwrite aborted, current file disposed");
            
            jif.setVisible(false);
            jif.dispose();
            
            System.out.println("File closed successfully"); //Debugger
            
    	} catch (Exception exc) {
    		exc.printStackTrace();
    		warning(exc.getMessage());
    	}
    }
    
    
    private void quit() {
        try {
            if (raf != null) {
            	saveFile();
                unloadFile();
            }
            
            //Seek the confirmation of overwriting the original file
            boolean replaceSource = false;
            //replaceSource = rmiGUI_F.warning("Are you sure to REPLACE source file?");
            replaceSource = rmiGUI_F.warning("Do you want to SAVE and REPLACE the original file?");
            
            if (replaceSource == true) {
            	//====== Determine if the file being edit is on Local or Remote ======
            	rmiDebug.setDebugText("Confirm overwrite the source file, upload proceed");
            	
            	if (editType.equals("remote")) {
					rmiDebug.setDebugText("Upload (Ascii) - From " + file + " back to " + srcLocation);
					rmi_FTL.uploadFile_Ascii(file, srcLocation);
            	} else if (editType.equals("local")) {
            		rmiDebug.setDebugText("Copy (Ascii) - From " + file + " back to " + srcLocation);
            		rmi_FTL.copyAndPaste(file, new File(srcLocation).getParent());
            	}
            	//====================================================================
            	
				//====== Here is where refresh occur ======
				System.out.println(rmiGUI_F.getClass().getName()); //Debugger
				
				if (rmiGUI_F.getClass().getName().equals("RMIAdmin.RMIClientGUI_TransferRemoteView")) {
					((RMIClientGUI_TransferRemoteView)rmiGUI_F).refreshRemoteView();
					
				} else if (rmiGUI_F.getClass().getName().equals("RMIAdmin.RMIClientGUI_FileTableView")) {
					((RMIClientGUI_FileTableView)rmiGUI_F).FTbT.refresh();
					
				} else if (rmiGUI_F.getClass().getName().equals("RMIAdmin.RMIClientGUI_FileTreeView")) {
					((RMIClientGUI_FileTreeView)rmiGUI_F).FTT.refreshUpOneLevel();
					
				} else if (rmiGUI_F.getClass().getName().equals("RMIAdmin.RMIClientGUI_TransferLocalView")) {
					((RMIClientGUI_TransferLocalView)rmiGUI_F).LVT.refreshUpOneLevel();
				}
				//=========================================
				
            } else {
            	rmiDebug.setDebugText("Overwrite aborted, current file disposed");
            }
            
            jif.setVisible(false);
            jif.dispose();
            
            System.out.println("File closed successfully"); //Debugger
            
        } catch (Exception exc) {
            exc.printStackTrace();
            warning(exc.getMessage());
        }
    }
    
    
    public class LoadFileThread extends Thread {
        public void run() {
            try {
                sleep(500); //Delay for 0.5 sec
                
	        	//=== Detect if the text in the JTextArea has been modified ===
	        	if (ref_b!=null && jtaContent.getText().equals(new String(ref_b))==false) {
	        		rmiDebug.setDebugText("Change Detected!"); //Debugger
	        		//System.out.println("**" + jtaContent.getText()); //Debugger
	        		//System.out.println("**" + new String(ref_b)); //Debugger
	        		
	        		//boolean saveUpdate = false;
	        		//saveUpdate = rmiGUI_F.warning("Do you want to save the changes?");
	        		boolean saveUpdate = true;
	        		
	        		if (saveUpdate == true) {
	        			rmiDebug.setDebugText("Save invoked.");
	        			saveFile();
	        		} else {
	        			rmiDebug.setDebugText("Save aborted.");
	        		}
	        	}
                //=============================================================
                
                if (action == 0) {
                	jtaContent.setText("");
                	jlbMessage.setText("Buffering data into Memory...");
                	
                    if (raf != null) {
                        unloadFile();
                    }
                    
                    rmiDebug.setDebugText("Re-Download (Ascii) - From " + srcLocation + " to " + tempLocation + " for editing");
					rmi_FTR.downloadFile_Ascii(srcLocation, tempLocation);
                    
                    loadFile();
                    
                } else if (action == 1) {
                 	jtaContent.setText("");
                	jlbMessage.setText("Buffering data into Memory...");
                	
                    getNextSegment();
                    
                } else if (action == 2) {
                	jtaContent.setText("");
                	jlbMessage.setText("Buffering data into Memory...");
                	
                    getPrevSegment();
                    
                } else if (action == 3) {
                	jlbMessage.setText("Saving data into Memory...");
                	saveFile();
                }
                
                jlbMessage.setText("File Size: " + raf.length() + " byte(s) " + 
                                    "Buffer Size: " + b.length + " byte(s)"
                                  );
                
            } catch (Exception exc) {
                exc.printStackTrace();
                warning(exc.getMessage());
            }
        }
    }
}