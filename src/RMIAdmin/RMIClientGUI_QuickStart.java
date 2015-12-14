package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.io.*;
import java.util.*;

import java.net.*;
import javax.help.*;
import javax.help.search.*;


public class RMIClientGUI_QuickStart extends JPanel {
	private JDesktopPane jdp;
	private RMIClientGUI rmiGUI;
	public RMIClientGUI_Debugger rmiDebug;
	
	
	public RMIClientGUI_QuickStart(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		this.rmiGUI = (RMIClientGUI)rmiDebug.getTopLevelAncestor();
		
		initGUI();
	}
	
	
	public void initGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		
		final JInternalFrame jif;
		
		jif = new JInternalFrame();
		jif.setTitle("RMIAdmin Quick Start Guide");
    	jif.setResizable(false);
    	jif.setIconifiable(true);
    	jif.setMaximizable(false);
    	jif.setClosable(true);
    	jif.setLayer(JLayeredPane.PALETTE_LAYER); //Makes the JInternalFrame always on top
    	
		jif.getContentPane().setLayout(new BorderLayout());
		
			JPanel jpTop = new JPanel();
			jpTop.setLayout(new FlowLayout(FlowLayout.CENTER));
				
				JLabel jlbText = new JLabel();
				jlbText.setText("<html><" + 
								"<font size='15' color='#FF0066'>Quick Start</font>" + "<br><br>" +
								"&nbsp;Welcome to RMIAdmin, this is a quick start panel which assist you " + "<br>" + 
								"&nbsp;to configure your RMIAdmin, follow the steps below and begin " + "<br>" + 
								"&nbsp;managing your servers today." + "<br>" + 
								"</html>");
			
			jpTop.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/config.gif"))));
			jpTop.add(jlbText);
			
			JPanel jpCenter = new JPanel();
			jpCenter.setLayout(new BorderLayout());
								
				JPanel jpCenter_1 = new JPanel();
				jpCenter_1.setLayout(new GridLayout(3,2,5,5));
				
					JPanel jpCenter_1_1 = new JPanel();
					jpCenter_1_1.setLayout(new BorderLayout());
						
						final JButton jbtn_create_server = new JButton("Create New Server");
						jbtn_create_server.setBackground(Color.lightGray);
						jbtn_create_server.setForeground(Color.darkGray);
						jbtn_create_server.setToolTipText("Click this button to create new managed server");
						jbtn_create_server.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								try {
									rmiDebug.setDebugText("Selected Config: Server Manager");
									JInternalFrame [] jif = jdp.getAllFrames();
									boolean found = false;
									
									for (int i=0; i<jif.length; i++) {
										String windowTitle = jif [i].getTitle() + "";
										
										if (windowTitle.indexOf("List of available servers") >= 0) {
											jif [i].setIcon(false);
											jif [i].toFront();
											
											found = true;
											break;
										}
									}
									
									if (found == false) {
										RMIClientGUI_ServerManager rmiGUI_SM = new RMIClientGUI_ServerManager(jdp, rmiDebug);
									}
									
								} catch (Exception exc) {
									exc.printStackTrace();
								}
							}
						});
						jbtn_create_server.addMouseListener(new MouseListener() {
							public void mouseEntered(MouseEvent me) {
								jbtn_create_server.setBackground(Color.darkGray);
								jbtn_create_server.setForeground(Color.lightGray);	
							}
							public void mouseExited(MouseEvent me) {
								jbtn_create_server.setBackground(Color.lightGray);
								jbtn_create_server.setForeground(Color.darkGray);
							}
							public void mouseReleased(MouseEvent me) {}
							public void mousePressed(MouseEvent me) {}
							public void mouseClicked(MouseEvent me) {}
						});
					
					jpCenter_1_1.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/rmi1.gif"))), BorderLayout.WEST);
					jpCenter_1_1.add(new JLabel("<html>Add a remote server to your list<br>of managed computer</html>"), BorderLayout.CENTER);
					jpCenter_1_1.add(jbtn_create_server, BorderLayout.SOUTH);
					
					JPanel jpCenter_1_2 = new JPanel();
					jpCenter_1_2.setLayout(new BorderLayout());
						
						final JButton jbtn_start_server = new JButton("Start the Remote Server");
						jbtn_start_server.setBackground(Color.lightGray);
						jbtn_start_server.setForeground(Color.darkGray);
						jbtn_start_server.setToolTipText("Click this button to see how to start the RMI service of your remote server");
						
						// 1. create HelpSet and HelpBroker objects
						HelpSet hs = RMIClientGUI_HelpSet.getHelpSet("doc/helpset/RMIAdminHelp_StartRMIServer.hs");
						final HelpBroker hb = hs.createHelpBroker();
						
						// 2. handle events
						jbtn_start_server.addActionListener(new CSH.DisplayHelpFromSource(hb));
						
						jbtn_start_server.addMouseListener(new MouseListener() {
							public void mouseEntered(MouseEvent me) {
								jbtn_start_server.setBackground(Color.darkGray);
								jbtn_start_server.setForeground(Color.lightGray);	
							}
							public void mouseExited(MouseEvent me) {
								jbtn_start_server.setBackground(Color.lightGray);
								jbtn_start_server.setForeground(Color.darkGray);
							}
							public void mouseReleased(MouseEvent me) {}
							public void mousePressed(MouseEvent me) {}
							public void mouseClicked(MouseEvent me) {}
						});
					
					jpCenter_1_2.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/rmi2.gif"))), BorderLayout.WEST);
					jpCenter_1_2.add(new JLabel("<html>See how to start the RMI Service<br>of your remote server</html>"), BorderLayout.CENTER);
					jpCenter_1_2.add(jbtn_start_server, BorderLayout.SOUTH);
					
					JPanel jpCenter_1_3 = new JPanel();
					jpCenter_1_3.setLayout(new BorderLayout());
						
						final JButton jbtn_manage_server = new JButton("Managing Remote Server");
						jbtn_manage_server.setBackground(Color.lightGray);
						jbtn_manage_server.setForeground(Color.darkGray);
						jbtn_manage_server.setToolTipText("Click this button to see how to begin managing your remote server");
						
						// 1. create HelpSet and HelpBroker objects
						HelpSet hs3 = RMIClientGUI_HelpSet.getHelpSet("doc/helpset/RMIAdminHelp_ExecuteCommand.hs");
						final HelpBroker hb3 = hs3.createHelpBroker();
						
						// 2. handle events
						jbtn_manage_server.addActionListener(new CSH.DisplayHelpFromSource(hb3));
						
						jbtn_manage_server.addMouseListener(new MouseListener() {
							public void mouseEntered(MouseEvent me) {
								jbtn_manage_server.setBackground(Color.darkGray);
								jbtn_manage_server.setForeground(Color.lightGray);	
							}
							public void mouseExited(MouseEvent me) {
								jbtn_manage_server.setBackground(Color.lightGray);
								jbtn_manage_server.setForeground(Color.darkGray);
							}
							public void mouseReleased(MouseEvent me) {}
							public void mousePressed(MouseEvent me) {}
							public void mouseClicked(MouseEvent me) {}
						});
					
					jpCenter_1_3.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/rmi3.gif"))), BorderLayout.WEST);
					jpCenter_1_3.add(new JLabel("<html>See how to begin managing your<br>remote server today</html>"), BorderLayout.CENTER);
					jpCenter_1_3.add(jbtn_manage_server, BorderLayout.SOUTH);
					
					JPanel jpCenter_1_4 = new JPanel();
					jpCenter_1_4.setLayout(new BorderLayout());
						
						final JButton jbtn_register = new JButton("Register");
						jbtn_register.setBackground(Color.lightGray);
						jbtn_register.setForeground(Color.darkGray.darker());
						jbtn_register.setToolTipText("Click this button to register your RMIAdmin and activate to full version");
						
						jbtn_register.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								if (rmiGUI.getAppMode() == 1) {
									rmiGUI.warning("Registraion not allowed in Web Demo mode!");
								} else {
									//Test if the application is in evaluation mode
									if (rmiGUI.isEvaluation() == true) {
										rmiDebug.setDebugText("Check Registration");
										rmiGUI.validateLicense();
									} else {
										rmiGUI.alert("Already registered.");
									}
								}
							}
						});
						jbtn_register.addMouseListener(new MouseListener() {
							public void mouseEntered(MouseEvent me) {
								jbtn_register.setBackground(Color.darkGray);
								jbtn_register.setForeground(Color.lightGray);	
							}
							public void mouseExited(MouseEvent me) {
								jbtn_register.setBackground(Color.lightGray);
								jbtn_register.setForeground(Color.darkGray);
							}
							public void mouseReleased(MouseEvent me) {}
							public void mousePressed(MouseEvent me) {}
							public void mouseClicked(MouseEvent me) {}
						});
					
					jpCenter_1_4.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/rmi4.gif"))), BorderLayout.WEST);
					jpCenter_1_4.add(new JLabel("<html>Register your RMIAdmin to get the<br>full version</html>"), BorderLayout.CENTER);
					jpCenter_1_4.add(jbtn_register, BorderLayout.SOUTH);
					
					JPanel jpCenter_1_5 = new JPanel();
					jpCenter_1_5.setLayout(new BorderLayout());
						
						final JButton jbtn_upgrade = new JButton("Upgrade");
						jbtn_upgrade.setBackground(Color.lightGray);
						jbtn_upgrade.setForeground(Color.darkGray);
						jbtn_upgrade.setToolTipText("Click this button to upgrade your RMIAdmin to latest version");
						jbtn_upgrade.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								boolean confirm = rmiGUI.warning("Auto-Upgrade require your computer to have direct <br>" +
															"internet connection. Depending on your network <br>" + 
															"bandwidth, the download time may vary.<br><br>" + 
															"Are you sure to upgrade?<br>" + 
															"[OK] to begin, [Cancel] to abort.");
								
								if (confirm == true) {
									rmiDebug.setDebugText("Upgrade will now start...");
									
									new RMIClientGUI_AutoUpgrade(jdp, rmiDebug);
								}
							}
						});
						jbtn_upgrade.addMouseListener(new MouseListener() {
							public void mouseEntered(MouseEvent me) {
								jbtn_upgrade.setBackground(Color.darkGray);
								jbtn_upgrade.setForeground(Color.lightGray);	
							}
							public void mouseExited(MouseEvent me) {
								jbtn_upgrade.setBackground(Color.lightGray);
								jbtn_upgrade.setForeground(Color.darkGray);
							}
							public void mouseReleased(MouseEvent me) {}
							public void mousePressed(MouseEvent me) {}
							public void mouseClicked(MouseEvent me) {}
						});
					
					jpCenter_1_5.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/rmi5.gif"))), BorderLayout.WEST);
					jpCenter_1_5.add(new JLabel("<html>Upgrade your RMIAdmin<br>(* Require direct internet access)</html>"), BorderLayout.CENTER);
					jpCenter_1_5.add(jbtn_upgrade, BorderLayout.SOUTH);
					
					JPanel jpCenter_1_6 = new JPanel();
					jpCenter_1_6.setLayout(new BorderLayout());
						
						final JButton jbtn_view_help = new JButton("Full Help Menu");
						jbtn_view_help.setBackground(Color.lightGray);
						jbtn_view_help.setForeground(Color.darkGray);
						jbtn_view_help.setToolTipText("Click this button to read the RMIAdmin tutorial");
						
						// 1. create HelpSet and HelpBroker objects
						HelpSet hs2 = RMIClientGUI_HelpSet.getHelpSet("doc/helpset/RMIAdminHelp.hs");
						final HelpBroker hb2 = hs2.createHelpBroker();
						
						// 2. handle events
						jbtn_view_help.addActionListener(new CSH.DisplayHelpFromSource(hb2));
						
						jbtn_view_help.addMouseListener(new MouseListener() {
							public void mouseEntered(MouseEvent me) {
								jbtn_view_help.setBackground(Color.darkGray);
								jbtn_view_help.setForeground(Color.lightGray);	
							}
							public void mouseExited(MouseEvent me) {
								jbtn_view_help.setBackground(Color.lightGray);
								jbtn_view_help.setForeground(Color.darkGray);
							}
							public void mouseReleased(MouseEvent me) {}
							public void mousePressed(MouseEvent me) {}
							public void mouseClicked(MouseEvent me) {}
						});
					
					jpCenter_1_6.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/rmi6.gif"))), BorderLayout.WEST);
					jpCenter_1_6.add(new JLabel("<html>Read the RMIAdmin tutorial</html>"), BorderLayout.CENTER);
					jpCenter_1_6.add(jbtn_view_help, BorderLayout.SOUTH);
					
				jpCenter_1.add(jpCenter_1_1);
				jpCenter_1.add(jpCenter_1_2);
				jpCenter_1.add(jpCenter_1_3);
				jpCenter_1.add(jpCenter_1_4);
				jpCenter_1.add(jpCenter_1_5);
				jpCenter_1.add(jpCenter_1_6);
				
			//jpCenter.add(jlbText,BorderLayout.NORTH);
			jpCenter.add(jpCenter_1,BorderLayout.CENTER);
			
			jpCenter.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(""),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
		
			JPanel jpBottom = new JPanel();
			jpBottom.setLayout(new FlowLayout(FlowLayout.CENTER));
				
				final JCheckBox jcb_autoLoad;
				
				
				if (loadSettings("quickStart").equals("yes")) {
					jcb_autoLoad = new JCheckBox("<html><font color='#333333'>Display this Quick Start Panel whenever startup</font></html>", true);
				} else {
					jcb_autoLoad = new JCheckBox("<html><font color='#333333'>Display this Quick Start Panel whenever startup</font></html>", false);
				}
				
				jcb_autoLoad.addItemListener(new ItemListener () {
					public void itemStateChanged(ItemEvent ie) {
						if (ie.getSource() == jcb_autoLoad) {
							//System.out.println(jcb_autoLoad.isSelected()); //Debugger
							boolean saveSetting;
							
							if (jcb_autoLoad.isSelected() == true) {
								saveSetting = rmiGUI.warning("You have enabled Quick Start to be displayed at startup<br>" + 
																"Do you want to save this setting?");					
								if (saveSetting == true) {
									saveSettings("quickStart","yes");
								}
								
							} else {
								saveSetting = rmiGUI.warning("You have disabled Quick Start to be displayed at startup<br>" + 
															"Do you want to save this setting?");					
								if (saveSetting == true) {
									saveSettings("quickStart","no");
								}
							}
						}
					}
				});
				
				JButton jbtn = new JButton(" Quit ");
				jbtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jif.setVisible(false);
						jif.dispose();
					}
				});
			
			jpBottom.add(jcb_autoLoad);
			jpBottom.add(jbtn);
			
		jif.getContentPane().add(jpTop, BorderLayout.NORTH);
		jif.getContentPane().add(jpCenter, BorderLayout.CENTER);
		jif.getContentPane().add(jpBottom, BorderLayout.SOUTH);
		
		jif.pack();
		
	    try {
	    	jif.setSelected(true);
	    } catch (Exception exc) {
	    	//Nothing to do
	    }
	    
	    jdp.add(jif);
		
		//Calulation the position of JDialog to appear in absolute middle of application
		double screenWidth = ((Toolkit.getDefaultToolkit()).getScreenSize()).getWidth();
		double screenHeight = ((Toolkit.getDefaultToolkit()).getScreenSize()).getHeight();
		int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
		int y = (int)(jdp.getHeight()/2 - jif.getHeight()/2);
		
		jif.setLocation(x,y);
    	jif.setVisible(true);
	}
	
	
	private void saveSettings(String item, String value) {
		RMIClientGUI_SetConfig c = new RMIClientGUI_SetConfig(rmiDebug);
		c.set(item,value);
		
		String error = c.getMessage();
		if (error.length() > 0) {
			rmiGUI.warning(error);
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private String loadSettings(String item) {
		String value = "";
		
		RMIClientGUI_SetConfig c = new RMIClientGUI_SetConfig(rmiDebug);
		value = c.get(item);
		
		String error = c.getMessage();
		if (error.length() > 0) {
			rmiGUI.warning(error);
			rmiDebug.setDebugText(error);
		}
		
		return value;
	}
}