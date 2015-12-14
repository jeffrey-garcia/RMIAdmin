package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

import java.io.*;
import java.util.*;

import java.net.*;
import javax.help.*;
import javax.help.search.*;


public class RMIClientGUI extends JFrame implements WindowListener {
	public JDesktopPane jdp;
	public static int windowCount;
	
	private JSplitPane jspt;
	private JPanel jp;
	private JTabbedPane jtpFunction;
	private JMenuBar jmb;
	private JMenu jmFile, jmView, jmTools, jmConfigure, jmHelp;
	private JMenuItem jmi;
	
	private int jdpWidth = 800; //formerly 850
	private int jdpHeight= 600; //formerly 650
	
	public RMIClientGUI_CommandLine rmiGUI_CLI;
	public RMIClientGUI_FileTransfer rmiGUI_FTP;
	public RMIClientGUI_FileBrowser rmiGUI_FBR;
	public RMIClientGUI_ConnectionMonitor rmiGUI_CM;
	public RMIClientGUI_Debugger rmiGUI_Debug;
	public RMIClientGUI_ServerManager rmiGUI_SM;
	public RMIClientGUI_MemoryMonitor rmiGUI_MM;
	public RMIClientGUI_RemoteControl rmiGUI_RC;
	public RMIClientGUI_RemoteTimer rmiGUI_RT;
	
	public boolean isEvaluation = true;
	private String inputKey = "";
	private JMenuItem jmRegister;
	private JMenuItem jmGenAuthen;
	private JMenuItem jmMemMonitor;
	private JMenuItem jmAutoUpgrade;
	
	private boolean guiReady = false;
	
	/*********************************************************** 
	 * This is the String determining the application mode
	 * 
	 * 0 = default shareware mode
	 * 1 = web demo
	 **********************************************************/
	private final int appMode = 0;
	
	
	public RMIClientGUI() {
		windowCount++;
		initGUI();
	}
	
	
	private void initGUI() {
		//JFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(getAppName() + " " + getAppVersion());
		setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/image/rmiAdmin_small_logo.gif")));
		
		setupTheme();
		setupJDesktopPane();
		setupFunctionMenu();
		setupActionPane();
		setupToolbar();
		
		addWindowListener(this);
		setSize(jdpWidth, jdpHeight);
		setLocationRelativeTo(null);
		setVisible(true);
		
		if (appMode == 0) {
			validateLicense();
		}
		
		//This part is for double protection in setting the Application Title
		if (isEvaluation == true) {
			this.setTitle(this.getAppName() + " " + this.getAppVersion() + " - [Evaluation Mode]");
			
			//Enable the register function
			jmRegister.setEnabled(true);
			
		} else {
			this.setTitle(this.getAppName() + " " + this.getAppVersion() + "");
			
			//Disable the register function
			jmRegister.setEnabled(false);
			
			//Enable Generate Authentication
			jmGenAuthen.setEnabled(true);
		}
		
		//detect software configuration files
		detectConfig();
		
		guiReady = true;
	}
	
	
	private void setupTheme() {
		RMIClient_UIColor theme;
		
		try {
			theme = new RMIClient_UIColor();
			theme.setPrimary1(new ColorUIResource(150, 150, 150)); //Menu Border
			theme.setPrimary2(new ColorUIResource(100, 100, 100)); //Scrollbar Background
			theme.setPrimary3(new ColorUIResource(190, 190, 200)); //Scrollbar Foreground + border
			
			theme.setSecondary2(new ColorUIResource(70, 70, 70)); //Panel Background, Tabbed Pane Color #Pressed Button
			theme.setSecondary3(new ColorUIResource(130, 130, 130)); //Panel Foreground
			
			if (UIManager.getLookAndFeel().getName().indexOf("Metal") >= 0) {
				MetalLookAndFeel.setCurrentTheme(theme);
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			}
			
			/*****************************************************
			 * This part is problem, since when displaying 
			 * Unicode fonts (like Trad Chi) will produce 
			 * monster character
			 *****************************************************
			Font rmiFont = new Font ("Arial", Font.PLAIN, 11);
			Enumeration en = UIManager.getDefaults().keys();
			
			while (en.hasMoreElements()) {
				String key = en.nextElement().toString();
				
				if (key.indexOf("font") >= 0) {
					//System.out.println("key: "+ key +" | value: " + UIManager.getDefaults().get(key)); //Debugger
					UIManager.getDefaults().put(key, rmiFont);
				}
				
			}
			*/
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	
	private void setupJDesktopPane() {
		//===== Setup the JDesktopPane =====
		jdp = new JDesktopPane();
		jdp.setBackground(Color.lightGray);
        setContentPane(jdp);
        //==================================
	}
	
	
	private void setupActionPane() {
    	//===== Setup the JInternalFrame =====
    	JInternalFrame jif = new JInternalFrame("Action Panel");
    	//jif.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
    	jif.getContentPane().add(jp);
    	jif.setSize(750,500);
    	jif.setResizable(true);
    	jif.setIconifiable(true);
    	jif.setMaximizable(true);
    	jif.setClosable(false);
	    	try {
	    		jif.setSelected(true);
	    	} catch (Exception exc) {
	    		//Nothing to do
	    	}
	    jdp.add(jif); //Add the JInternalFrame to the JDesktopPane
	    
		/***********************************
		 * Since JInternalFrame do not have
		 * a "setLocationRelativeTo(null)"
		 * method, we therefore must use 
		 * the below coding to place the 
		 * JInternalFrame in the middle of 
		 * the screen.
		 **********************************/
		double screenWidth = ((Toolkit.getDefaultToolkit()).getScreenSize()).getWidth();
		double screenHeight = ((Toolkit.getDefaultToolkit()).getScreenSize()).getHeight();
		int x = (int)(jdpWidth/2 - jif.getWidth()/2);
		int y = (int)(jdpHeight/2 - 30 - jif.getHeight()/2);
		//int y = (int)(jdpHeight/2 - 50 - jif.getHeight()/2);
		
		jif.setLocation(x,y);
    	jif.setVisible(true);
    	//====================================
	}
	
	
	private void setupFunctionMenu() {
		//===== Top Level Panel ======
		jp = new JPanel();
		//jp.setBackground(Color.WHITE);
    	jp.setLayout(new BorderLayout());
		//============================
		
    	//===== Add the Functional Menu ======
    	rmiGUI_Debug = new RMIClientGUI_Debugger(jdp);
    	rmiGUI_CLI = new RMIClientGUI_CommandLine(jdp, rmiGUI_Debug);
    	rmiGUI_FTP = new RMIClientGUI_FileTransfer(jdp, rmiGUI_Debug);
    	rmiGUI_FBR = new RMIClientGUI_FileBrowser(jdp, rmiGUI_Debug);
    	rmiGUI_CM = new RMIClientGUI_ConnectionMonitor(jdp, rmiGUI_Debug);
    	rmiGUI_MM = new RMIClientGUI_MemoryMonitor(jdp, rmiGUI_Debug);
    	rmiGUI_RC = new RMIClientGUI_RemoteControl(jdp, rmiGUI_Debug);
    	rmiGUI_RT = new RMIClientGUI_RemoteTimer(jdp, rmiGUI_Debug);
    	
    	jtpFunction = new JTabbedPane();
    	jtpFunction.add(rmiGUI_CLI, "Command Line");
    	jtpFunction.add(rmiGUI_FTP, "File Transfer");
    	jtpFunction.add(rmiGUI_FBR, "File Browser");
    	jtpFunction.add(rmiGUI_CM, "Connection Monitor");
    	jtpFunction.setPreferredSize(new Dimension(400,350));
    	
        jtpFunction.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Function Menu"),
                                BorderFactory.createEmptyBorder(5,5,5,5)
				)
		);
    	//====================================
    	
		//====== Setup JSplitPane ======
		JSplitPane jspt = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jspt.setContinuousLayout(true);
		jspt.setOneTouchExpandable(true);
		jspt.add(jtpFunction);
		jspt.add(rmiGUI_Debug);
		//==============================
    	
    	jp.add(jspt);
	}
	
	
	private void setupToolbar() {
		try {
 			//===== Setup the Toolbar =====
			jmb = new JMenuBar();
			
			jmFile = new JMenu("File");
			jmFile.setMnemonic(KeyEvent.VK_F);
			jmFile.getAccessibleContext().setAccessibleDescription("File Option");
	
				jmi = new JMenuItem("New Window",KeyEvent.VK_N);
				jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Open new application window");
						new RMIClientGUI();
					}
				});
				jmFile.add(jmi);
				
				jmFile.addSeparator();
				
				JMenu jmImport = new JMenu("Import");
				jmImport.setMnemonic(KeyEvent.VK_I);
					JMenuItem importServerList = new JMenuItem("Server List",KeyEvent.VK_S);
					importServerList.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							rmiGUI_Debug.setDebugText("Import Server List selected");
							
							//Imxport the Debug Console
							new RMIClientGUI_Import(jdp, rmiGUI_Debug, "serverList");
						}
					});
				jmImport.add(importServerList);
				
				jmFile.add(jmImport);
				
				JMenu jmExport = new JMenu("Export");
				jmExport.setMnemonic(KeyEvent.VK_E);
					JMenuItem exportDebugConsole = new JMenuItem("Debug Console",KeyEvent.VK_D);
					exportDebugConsole.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							rmiGUI_Debug.setDebugText("Export Debug Console selected");
							
							//Export the Debug Console
							new RMIClientGUI_Export(jdp, rmiGUI_Debug, "debugConsole");
						}
					});
					
					JMenuItem exportServerList = new JMenuItem("Server List",KeyEvent.VK_S);
					exportServerList.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							rmiGUI_Debug.setDebugText("Export Server List selected");
							
							//Export the Server List
							new RMIClientGUI_Export(jdp, rmiGUI_Debug, "serverList");
						}
					});	
				jmExport.add(exportDebugConsole);
				jmExport.add(exportServerList);
				
				jmFile.add(jmExport);
				
				jmFile.addSeparator();
				
				jmRegister = new JMenuItem("Register",KeyEvent.VK_R);
				jmRegister.setEnabled(false);
				jmRegister.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						if (appMode == 1) {
							warning("Registraion not allowed in Web Demo mode!");
						} else {
							rmiGUI_Debug.setDebugText("Register");
							validateLicense();
						}
					}
				});
				jmFile.add(jmRegister);
				jmFile.addSeparator();
				
				jmi = new JMenuItem("Quit",KeyEvent.VK_Q);
				jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Exit application window");
						
						boolean quit= warning("Are you sure to quit?");
						if (quit == true) {
							quitApplication();
						}
					}
				});
				jmFile.add(jmi);
				
				//jmFile.getItem(2).setEnabled(false); //Dim the import option
			
			jmView = new JMenu("View");
			jmView.setMnemonic(KeyEvent.VK_V);
			jmView.getAccessibleContext().setAccessibleDescription("View Option");
			
				jmi = new JMenuItem("Minimize All Frames",KeyEvent.VK_N);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						try {
							rmiGUI_Debug.setDebugText("Minimize All Frames");
							final JInternalFrame [] jif = jdp.getAllFrames();
							
							for (int i=0; i<jif.length; i++) {
								rmiGUI_Debug.setDebugText("Minimizing Frame ID: " + i);
								jif [i].setIcon(true);
							}
							
						} catch (Exception exc) {
							exc.printStackTrace();
						}
					}
				});
				jmView.add(jmi);
				
				jmi = new JMenuItem("Maximize All Frames",KeyEvent.VK_X);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						try {
							rmiGUI_Debug.setDebugText("Maximize All Frames");
							JInternalFrame [] jif = jdp.getAllFrames();
							
							for (int i=0; i<jif.length; i++) {
								rmiGUI_Debug.setDebugText("Maximizing Frame ID: " + i);
								jif [i].setIcon(false);
							}
							
						} catch (Exception exc) {
							exc.printStackTrace();
						}
					}
				});
				jmView.add(jmi);
				
				jmView.addSeparator();
				
				jmi = new JMenuItem("Switch Windows",KeyEvent.VK_S);
				jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						try {
							rmiGUI_Debug.setDebugText("Show all opened Windows");
							JInternalFrame [] jif = jdp.getAllFrames();
							showWindows(jif);
							
						} catch (Exception exc) {
							exc.printStackTrace();
						}
					}
				});
				jmView.add(jmi);
			
			jmTools = new JMenu("Tools");
			jmTools.setMnemonic(KeyEvent.VK_T);
			jmTools.getAccessibleContext().setAccessibleDescription("Tools Option");
			
				jmi = new JMenuItem("Command Line",KeyEvent.VK_C);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Selected Tool: Command Line");
						String tmp = ((JMenuItem)ae.getSource()).getText();
						
						for (int i=0; i<jtpFunction.getTabCount(); i++) {
							if (jtpFunction.getTitleAt(i).equals(tmp)) {
								jtpFunction.setSelectedIndex(i);
								break;
							}
						}
					}
				});
				jmTools.add(jmi);
				
				jmi = new JMenuItem("File Transfer",KeyEvent.VK_T);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Selected Tool: File Transfer");
						String tmp = ((JMenuItem)ae.getSource()).getText();
						
						for (int i=0; i<jtpFunction.getTabCount(); i++) {
							if (jtpFunction.getTitleAt(i).equals(tmp)) {
								jtpFunction.setSelectedIndex(i);
								break;
							}
						}
					}
				});
				jmTools.add(jmi);
				
				jmi = new JMenuItem("File Browser",KeyEvent.VK_B);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Selected Tool: File Browser");
						String tmp = ((JMenuItem)ae.getSource()).getText();
						
						for (int i=0; i<jtpFunction.getTabCount(); i++) {
							if (jtpFunction.getTitleAt(i).equals(tmp)) {
								jtpFunction.setSelectedIndex(i);
								break;
							}
						}
					}
				});
				jmTools.add(jmi);
				
				jmi = new JMenuItem("Connection Monitor",KeyEvent.VK_M);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Selected Tool: Connection Monitor");
						String tmp = ((JMenuItem)ae.getSource()).getText();
						
						for (int i=0; i<jtpFunction.getTabCount(); i++) {
							if (jtpFunction.getTitleAt(i).equals(tmp)) {
								jtpFunction.setSelectedIndex(i);
								break;
							}
						}
					}
				});
				jmTools.add(jmi);
				
				jmi = new JMenuItem("Remote Control [Beta]",KeyEvent.VK_C);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Selected Tool: Remote Control");
						String tmp = ((JMenuItem)ae.getSource()).getText();
						
						alert(tmp + " is currently a BETA version, we are <br>" + 
									"providing this function as a courtesy to System Admin <br>" + 
									"and Developers for problem resolution.<br><br>" + 
									"Please kindly note that this utility is still in the <br>" + 
									"beginning stages of development, so it may perform <br>" + 
									"erratically. If something's not working, please visit <br>" + 
									"the RMIAdmin website at <a href='http://www.rmiAdmin.net'>http://www.rmiAdmin.net</a> <br>" + 
									"for the latest enhancements, bug and security fixes.<br><br>" + 
									"Should you have any comments or suggestion, please <br>" + 
									"kindly send us an email to " + 
									"<a href='mailto:cs@rmiadmin.net'>cs@rmiAdmin.net</a>.");
						
						rmiGUI_RC.selectServer();
					}
				});
				jmTools.add(jmi);
				
				jmi = new JMenuItem("Remote Timer",KeyEvent.VK_I);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Selected Tool: Remote Timer");
						String tmp = ((JMenuItem)ae.getSource()).getText();
						
						rmiGUI_RT.selectServer();
					}
				});
				jmTools.add(jmi);
				
				jmTools.addSeparator();
				
			jmConfigure = new JMenu("Configure");
			jmConfigure.setMnemonic(KeyEvent.VK_C);
			jmConfigure.getAccessibleContext().setAccessibleDescription("Configure Option");
			
				jmMemMonitor = new JMenuItem("Manage Performance",KeyEvent.VK_P);
				jmMemMonitor.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Memory Monitor selected");
						
						rmiGUI_MM.selectServer();
					}
				});
				jmConfigure.add(jmMemMonitor);
				
				jmGenAuthen = new JMenuItem("Generate Authentication",KeyEvent.VK_A);
				jmGenAuthen.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Generate Authentication selected");
						
						generateServerKey();
					}
				});
				jmConfigure.add(jmGenAuthen);
				
				jmi = new JMenuItem("Activate Secure Channel",KeyEvent.VK_S);
				//jmi.addActionListener(this);
				jmConfigure.add(jmi);
				
				jmi = new JMenuItem("Server List",KeyEvent.VK_N);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						try {
							rmiGUI_Debug.setDebugText("Selected Config: Server Manager");
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
								rmiGUI_SM = new RMIClientGUI_ServerManager(jdp, rmiGUI_Debug);
							}
							
						} catch (Exception exc) {
							exc.printStackTrace();
						}
					}
				});
				jmConfigure.add(jmi);
				
				jmConfigure.getItem(1).setEnabled(false); //Dim the Generate Authentication
				jmConfigure.getItem(2).setEnabled(false); //Dim the Activate Secure Channel
				
				jmConfigure.addSeparator();
				
			jmHelp = new JMenu("Help");
			jmHelp.setMnemonic(KeyEvent.VK_H);
			jmHelp.getAccessibleContext().setAccessibleDescription("Help Option");
			
				jmi = new JMenuItem("Help Contents...",KeyEvent.VK_H);
					// 1. create HelpSet and HelpBroker objects
					HelpSet hs = RMIClientGUI_HelpSet.getHelpSet("doc/helpset/RMIAdminHelp.hs"); 
					final HelpBroker hb = hs.createHelpBroker();
					
					// 2. assign help to components
					CSH.setHelpIDString(jmHelp, "top");
					
					// 3. handle events
					jmi.addActionListener(new CSH.DisplayHelpFromSource(hb));
				jmHelp.add(jmi);
				
				jmi = new JMenuItem("Quick Start Guide",KeyEvent.VK_Q);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						rmiGUI_Debug.setDebugText("Selected Item: Quick Start Guide");
						
						new RMIClientGUI_QuickStart(jdp, rmiGUI_Debug);
					}
				});
				jmHelp.add(jmi);
				
				jmi = new JMenuItem("API Help",KeyEvent.VK_A);
				//jmi.addActionListener(this);
				jmHelp.add(jmi);
				
				jmi = new JMenuItem("Tip of the Day",KeyEvent.VK_T);
				//jmi.addActionListener(this);
				jmHelp.add(jmi);
				
				jmHelp.addSeparator();
				
				jmi = new JMenuItem("On line forum",KeyEvent.VK_F);
				//jmi.addActionListener(this);
				jmHelp.add(jmi);
				
				jmi = new JMenuItem("Website...",KeyEvent.VK_W);
				//jmi.addActionListener(this);
				jmHelp.add(jmi);
				
				jmAutoUpgrade = new JMenuItem("Check Updates...",KeyEvent.VK_U);
				jmAutoUpgrade.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						try {
							rmiGUI_Debug.setDebugText("Selected Item: Auto Upgrade");
							
							boolean confirm = warning("Auto-Upgrade require your computer to have direct <br>" +
														"internet connection. Depending on your network <br>" + 
														"bandwidth, the download time may vary.<br><br>" + 
														"Are you sure to upgrade?<br>" + 
														"[OK] to begin, [Cancel] to abort.");
							
							if (confirm == true) {
								rmiGUI_Debug.setDebugText("Upgrade will now start...");
								
								new RMIClientGUI_AutoUpgrade(jdp, rmiGUI_Debug);
							}
							
						} catch (Exception exc) {
							exc.printStackTrace();
						}
					}
				});
				jmHelp.add(jmAutoUpgrade);
				
				jmHelp.addSeparator();
				
				jmi = new JMenuItem("About",KeyEvent.VK_B);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						new RMIClientGUI_About(jdp, rmiGUI_Debug);
					}
				});
				jmHelp.add(jmi);
				
				jmHelp.getItem(2).setEnabled(false); //Dim the API Help option
				jmHelp.getItem(3).setEnabled(false); //Dim the Tip of Day option
				jmHelp.getItem(5).setEnabled(false); //Dim the on-line forum option
				jmHelp.getItem(6).setEnabled(false); //Dime the website option
				
				//Dim the auto-update option if is web demo
				if (getAppMode() == 1) {
					jmHelp.getItem(7).setEnabled(false); 
				}
				
			jmb.add(jmFile);
			jmb.add(jmView);
			jmb.add(jmTools);
			jmb.add(jmConfigure);
			jmb.add(jmHelp);
			setJMenuBar(jmb);
	    	//=============================
	    	
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	
	private void detectConfig() {
		RMIClientGUI_SetConfig c = new RMIClientGUI_SetConfig(rmiGUI_Debug);
		String value = "";
		String error = "";
		
		//====== Get the value of QuickStart ======
		value = c.get("quickStart");
		error = c.getMessage();
		
		if (error.length() > 0) {
			warning(error);
			rmiGUI_Debug.setDebugText(error);
		} else {
			if (value.equals("yes")) {
				new RMIClientGUI_QuickStart(jdp, rmiGUI_Debug);
			}
		}
		//=========================================
	}
	
	
	public boolean isReady() {
		return guiReady;
	}
	
	
	private void quitApplication() {
		windowCount--;
		
		if (windowCount <= 0) {
			System.exit(0);
		} else {
			dispose();
		}
	}
	
	
	public void windowOpened(WindowEvent we) {}
	public void windowClosing(WindowEvent we) { quitApplication(); }
	public void windowClosed(WindowEvent we) {}
	public void windowIconified(WindowEvent we) {}
	public void windowDeiconified(WindowEvent we) {}
	public void windowActivated(WindowEvent we) {}
	public void windowDeactivated(WindowEvent we) {}
	
	
	private void showWindows(JInternalFrame [] jif) {
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane jop;
			JDialog jdg;
			
			String selectedWindow = "";
			
			String window [] = new String [jif.length];
			for (int i=0; i<jif.length; i++) {
				window [i] = jif [i].getTitle() + "";
			}
			
			jop = new JOptionPane("",
									JOptionPane.QUESTION_MESSAGE,
									JOptionPane.OK_CANCEL_OPTION);
			
			jop.setSelectionValues(window);	
			jdg = jop.createDialog(this,"Select Window");
			
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
			
			selectedWindow = (String)jop.getInputValue();
			
			if (selectedWindow.equals("uninitializedValue") == false) {
				rmiGUI_Debug.setDebugText("Selected Window to Focus: " + selectedWindow);
				
				for (int i=0; i<jif.length; i++) {
					String s = jif [i].getTitle();
					
					if (s.equals(selectedWindow)) {
						rmiGUI_Debug.setDebugText("Focus Windows: " + selectedWindow);
						jif [i].setIcon(false);
						jif [i].setSelected(true);
						jif [i].toFront();
					}
				}
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	
	public boolean warning(String opt) {
		JDialog.setDefaultLookAndFeelDecorated(true);	
		JOptionPane jop;
		JDialog jdg;
		
		if (opt == null) {
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
	
	
	public void alert(String opt) {
		JDialog.setDefaultLookAndFeelDecorated(true);	
		JOptionPane jop;
		JDialog jdg;
		
		opt = opt.replaceAll("\n","<br>");
		String temp = "<html>" + opt + "</html>";
		
		jop = new JOptionPane(temp, JOptionPane.INFORMATION_MESSAGE);
		
		jdg = jop.createDialog(this,"System Message");
		
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
	}
	
	
	public void showInfo(String opt) {
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane jop;
			JDialog jdg;
			
			jop = new JOptionPane(opt,JOptionPane.INFORMATION_MESSAGE);
			jop.setWantsInput(false);
			
			jdg = jop.createDialog(this, "Registration");
			
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
			rmiGUI_Debug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	private void postRegMsg() {
		//Remove the Evaluation Mode from Application Window Title
		this.setTitle(this.getAppName() + " " + this.getAppVersion() + "");
		
		//Disable the register function
		jmRegister.setEnabled(false);
		
		//Enable Generate Authentication
		jmGenAuthen.setEnabled(true);
		
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JOptionPane jop;
			JDialog jdg;
			String opt = "";
			
			opt += "<html>";
			opt += "<h2>Congratultions! Registration Success.</h2>";
			opt += "<br>";
			opt += "The following features are now activated:";
			opt += "<hr>";
				opt += "1. Generate Authentication.<br>";
				opt += "<font color='#808080'>";
				opt += "&nbsp;&nbsp;&nbsp;&nbsp;<i>Use Configure > Generate Authentication to setup Server Key</i><br>";
				opt += "&nbsp;&nbsp;&nbsp;&nbsp;<i>For usage, please view the HELP menu for details.</i>";
				opt += "</font>";
			opt += "<br><br><br>";
			opt += "</html>";
			
			jop = new JOptionPane(opt,JOptionPane.INFORMATION_MESSAGE);
			jop.setWantsInput(false);
			
			jdg = jop.createDialog(this, "Registration Complete");
			
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
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiGUI_Debug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	public String inputKey() {
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			final JDialog jdg;
			
			jdg = new JDialog((JFrame)jdp.getTopLevelAncestor(), true);
			jdg.setResizable(false);
			jdg.setTitle(this.getTitle() + " Registration");
			jdg.getContentPane().setLayout(new BorderLayout());
			
				JPanel jpTop = new JPanel();
				jpTop.setLayout(new FlowLayout(FlowLayout.CENTER));
			 	jpTop.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/rmiAdmin_logo.gif"))));
				
				JPanel jpCenter = new JPanel();
				jpCenter.setLayout(new BorderLayout());
					JLabel jlbText = new JLabel();
					jlbText.setText("<html>" + 
									"This software allows free evalutation for 30 Days.<br>" + 
									"After that the software will be expired unless having a valid registration<br>" + 
									"key purhcased.<br><br>" + 
									"For how to purchase the registration key, please visit " + 
									"<font color='#666666'><u>http://www.rmiAdmin.net</u></font><br>" + 
									"If the key is lost, please send us an email to <font color='#666666'><u>rmi_cs@yahoo.com.hk</u></font> " + 
									"on how to get it back.<br><br>" + 
									"<hr><br>" + 
									"Please input your registration key." + 
							 		"</html>");
					
					JPanel jpCenter_1 = new JPanel();
					jpCenter_1.setLayout(new FlowLayout(FlowLayout.CENTER));
					
						JPanel jpCenter_11 = new JPanel();
						jpCenter_11.setLayout(new FlowLayout(FlowLayout.CENTER));
						
							final JTextField jtf = new JTextField(24);
							jtf.setEditable(true);
							jtf.addMouseListener(new RMIClientGUI_MouseListener());
						
						jpCenter_11.add(jtf);
						
						jpCenter_11.setBorder(
							BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder(""),
								BorderFactory.createEmptyBorder(5,5,5,5)
							)
						);
					
					jpCenter_1.add(jpCenter_11);
					
				jpCenter.add(jlbText,BorderLayout.CENTER);
				jpCenter.add(jpCenter_1, BorderLayout.SOUTH);
				
				JPanel jpBottom = new JPanel();
				jpBottom.setLayout(new FlowLayout(FlowLayout.CENTER));
				
					JButton jbtnOK = new JButton("OK");
					JButton jbtnCancel = new JButton("Cancel");
					
					jbtnOK.setToolTipText("Please click this button to proceed registration");
					jbtnCancel.setToolTipText("Please click this button to skip registration");
					
					jbtnOK.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							inputKey = jtf.getText();
							
							jdg.setVisible(false);
							jdg.dispose();
						}
					});
					jbtnCancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							jdg.setVisible(false);
							jdg.dispose();
						}
					});
					
				jpBottom.add(jbtnOK);
				jpBottom.add(jbtnCancel);
			
			JPanel jp = new JPanel();
			jp.setLayout(new BorderLayout());
			jp.add(jpTop, BorderLayout.NORTH);
			jp.add(jpCenter, BorderLayout.CENTER);
			jp.add(jpBottom, BorderLayout.SOUTH);
			jp.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(""),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
			
			jdg.getContentPane().add(jp, BorderLayout.CENTER);
			
			//jdg.setSize(430,350);
			jdg.pack();
			
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
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiGUI_Debug.setDebugText(error);
			warning(exc.getMessage());
			
		} finally {
			return inputKey;
		}
	}
	
	
	private void generateServerKey() {
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			final JDialog jdg;
			
			jdg = new JDialog((JFrame)jdp.getTopLevelAncestor(), true);
			jdg.setResizable(false);
			jdg.setTitle("Generate Server Key");
			jdg.getContentPane().setLayout(new BorderLayout());
			
				JPanel jpTop = new JPanel();
				jpTop.setLayout(new FlowLayout(FlowLayout.LEFT));
					JLabel jlbText = new JLabel();
					String text = 	"<html>" + 
									"Please define both the Server Key and the PassPhrase<br>" + 
									"<font color='#808080'><i>Press HELP button for details</i></font><br>" +
									"</html>";
					jlbText.setText(text);
				jpTop.add(jlbText);
				
				JPanel jpCenter = new JPanel();
				jpCenter.setLayout(new BorderLayout());
				
					JPanel jpCenter_1 = new JPanel();
					jpCenter_1.setLayout(new GridLayout(2,1,5,5));
					jpCenter_1.add(new JLabel("Server Key: "));
					jpCenter_1.add(new JLabel("Passphrase: "));
				
					JPanel jpCenter_2 = new JPanel();
					jpCenter_2.setLayout(new GridLayout(2,1,5,5));
					
						final JTextField jtfServerKey = new JTextField(5);
						jtfServerKey.addMouseListener(new RMIClientGUI_MouseListener());
						jtfServerKey.setToolTipText("<html>Enter any non-empty String as the server key.<br>" + 
											"The Server Key will then be used to encrypt <br>" + 
											"the Passphrase.</html>");
					
						final JPasswordField jpfPassPhrase = new JPasswordField(5);
						jpfPassPhrase.addMouseListener(new RMIClientGUI_MouseListener());
						jpfPassPhrase.setToolTipText("<html>Enter any non-empty String as the Passphrase.<br>" + 
											"The passphrase will then be encrypted by <br>" + 
											"the Server Key.</html>");
					
					jpCenter_2.add(jtfServerKey);
					jpCenter_2.add(jpfPassPhrase);

				jpCenter.add(jpCenter_1, BorderLayout.WEST);
				jpCenter.add(jpCenter_2, BorderLayout.CENTER);
				
				JPanel jpBottom = new JPanel();
				jpBottom.setLayout(new FlowLayout(FlowLayout.CENTER));
				
					JButton jbtnOK = new JButton("OK");
					JButton jbtnCancel = new JButton("Cancel");
					JButton jbtnHelp = new JButton("Help");
					
					jbtnOK.setToolTipText("Please click this button to proceed key generation");
					jbtnCancel.setToolTipText("Please click this button to abort key generation");
					jbtnHelp.setToolTipText("Please click this button to view the help menu");
					
					jbtnOK.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							String serverKey = jtfServerKey.getText();
							String passphrase = new String(jpfPassPhrase.getPassword());
							
							//Debugger, should hide the passphrase value
							rmiGUI_Debug.setDebugText("New passphrase defined");
							
							boolean complete = false;
							try {
								complete = encryptServerKey(serverKey, passphrase);
								
							} catch (Exception exc) {
								StackTraceElement[] error = exc.getStackTrace();
								rmiGUI_Debug.setDebugText(error);
								warning(exc.getMessage() + "\n\nGenerate Authentication Fail!");
								complete = false;
							}
							
							if (complete == true) {
								jdg.setVisible(false);
								jdg.dispose();
							}
						}
					});
					jbtnCancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							rmiGUI_Debug.setDebugText("Server Key generation abort.");
							
							jdg.setVisible(false);
							jdg.dispose();
						}
					});
					
					// 1. create HelpSet and HelpBroker objects
					HelpSet hs = RMIClientGUI_HelpSet.getHelpSet("doc/helpset/RMIAdminHelp_GenerateAuth.hs"); 
					final HelpBroker hb = hs.createHelpBroker();
					
					// 2. handle events
					jbtnHelp.addActionListener(new CSH.DisplayHelpFromSource(hb));
					
				jpBottom.add(jbtnOK);
				jpBottom.add(jbtnCancel);
				jpBottom.add(jbtnHelp);
				
			JPanel jp = new JPanel();
			jp.setLayout(new BorderLayout());
			jp.add(jpTop, BorderLayout.NORTH);
			jp.add(jpCenter, BorderLayout.CENTER);
			jp.add(jpBottom, BorderLayout.SOUTH);
			jp.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(""),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
			
			jdg.getContentPane().add(jp, BorderLayout.CENTER);
			
			//jdg.setSize(430,350);
			jdg.pack();
			
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
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiGUI_Debug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	private boolean encryptServerKey(String serverKey, String passphrase) throws Exception {
		boolean complete = false;
		String serverKeyFile = "serverKey.dat";
		String serverPassphraseFile = "serverPassphrase.dat";
		RMIClient_CipherKey cKey;
		
		cKey = new RMIClient_CipherKey();
		
		//=== This part is just for validating the passphrase ===
		if (cKey.changeKey(passphrase) == true) {
			rmiGUI_Debug.setDebugText("Passphrase accepted.");
		} else {
			warning("Incorrect Passphrase.\n" + 
					"* Passphrase must be characters only\n" + 
					"* Passphrase must not be empty\n" + 
					"* Passphrase must be in upper case");
			return false;
		}
		//=======================================================
		
		//=== This part is for validating and setting the Server Key ===
		if (cKey.changeKey(serverKey) == true) {
			rmiGUI_Debug.setDebugText("Server Key accepted.");
		} else {
			warning("Incorrect Key.\n" + 
					"* Key must be characters only\n" + 
					"* Key must not be empty\n" + 
					"* Key must be in upper case");
			return false;
		}
		//==============================================================
		
		String tmp = cKey.encryptLine(passphrase);
		System.out.println("Encryption Key: " + serverKey); //Debugger
		System.out.println("Passphrease: " + passphrase); //Debugger
		System.out.println("Encrypted Passphrase: " + tmp); //Debugger
		
		FileWriter fw = new FileWriter(serverPassphraseFile, false);
		fw.write(tmp);
		fw.close();
		
		fw = new FileWriter(serverKeyFile, false);
		fw.write(serverKey);
		fw.close();
		
		rmiGUI_Debug.setDebugText("Server Passphrase successfully generated to " + 
									new File(serverPassphraseFile).getCanonicalPath() + ".");
		rmiGUI_Debug.setDebugText("Server Key successfully generated to " + 
									new File(serverKeyFile).getCanonicalPath() + ".");
		
		warning("Server Passphrase successfully generated to " + 
				"\n" + 
				"<font color='blue'>" + 
				new File(serverPassphraseFile).getCanonicalPath() + "." +
				"</font>" + 
				"\n" + 
				"Server Key successfully generated to " + 
				"\n" + 
				"<font color='blue'>" + 
				new File(serverKeyFile).getCanonicalPath() + "." +
				"</font>" + 
				"\n\n" + 
				"Please upload both files to all RMIServer, then restart " + 
				"\n" + 
				"the RMIServer daemon to make the changes effect." + 
				"\n" + 
				"<font color='#808080'>" + 
				"<i>For details, please read the HELP menu.</i>" +
				"</font>" + 
				"\n\n");
		
		complete = true;
		return complete;
	}
	
	
	public String getAppName() {
		return "RMIAdmin";
	}
	
	
	public String getAppVersion() {
		return "1.3";
	}
	
	
	public void validateLicense() {
		try {
			RMIClient_RegKeyValidator regV = new RMIClient_RegKeyValidator(this);
			
			if (regV.isRegister() == true) {
				rmiGUI_Debug.setDebugText("Already Registered.");
				
				//Disable the register function
				jmRegister.setEnabled(false);
				
				//Set the mode
				isEvaluation = false;
				
			} else {
				//Set the title to Evaluation Mode
				this.setTitle(this.getAppName() + " " + this.getAppVersion() + " - [Evaluation Mode]");
				
				rmiGUI_Debug.setDebugText("Not yet registered");
				String key = inputKey();
				
				regV.setRegKey(key);
				
				if (regV.register() == true)  {
					rmiGUI_Debug.setDebugText("Registration success.");
					postRegMsg();
					
					//Set the mode
					isEvaluation = false;
					
				} else {
					rmiGUI_Debug.setDebugText("Registration fail.");
					warning("Invalid Key!\nRegistration Fail.");
					
					if (regV.isExpired() == true) {
						//Evalutation expired, quit the software
						System.exit(0);
						
					} else {
						//Set the mode
						isEvaluation = true;
					}
				}
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiGUI_Debug.setDebugText(error);
			warning(exc.getMessage());
		}
	}
	
	
	public int getAppMode() {
		return appMode;
	}
	
	
	public boolean isEvaluation() {
		return isEvaluation;
	}
}