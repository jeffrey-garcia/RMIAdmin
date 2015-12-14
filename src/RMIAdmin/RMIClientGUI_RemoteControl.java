package RMIAdmin;


import java.rmi.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.color.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;

import java.io.*;
import java.net.*;


public class RMIClientGUI_RemoteControl extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	private RMIClientGUI_Debugger rmiDebug;
	
	private int serverID = 0;
	private String serverHostname = "";
	private String serverIP = "";
	private String serverPort = "";
	private String serverOS = "";
	private String serverShell = "";
	private String serverDesc = "";
	
	
	public RMIClientGUI_RemoteControl(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		
		//setBackground(Color.darkGray);
		setLayout(new BorderLayout());
		
		//------ Initialize the GUI ------
		initGUI();
	}
	
	
	public void initGUI() {
		//nothing to do...
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
		
		//Such that the remote cotrol window will be immediately shutdown 
		//even if this warning message is not yet answered
		jdg.setModal(false);
		
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
	
	
	public void selectServer() {
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
				
				loadRemoteControl();
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void loadRemoteControl() {
		new RemoteControl(this, serverHostname, serverIP, serverPort).start();
	}
	
	
	private class RemoteControl extends Thread {
		private RMIClientGUI rmiGUI;
		private RMIClientGUI_RemoteControl rmiGUI_RC;
		private RMIClient_RemoteControl rmi_RC;
		
		private String server;
		private String ip;
		private String port;
		
		private JFrame jf;
		private JScrollPane jsp;
		private JPanel jpScreen;
		private JPanel jpBottom;
		private JLabel jlbScreen;
		private JLabel jlbMessage;
		
		//private ImageIcon ic;
		private Dimension d;
		private Rectangle screenRect;
		
		private boolean terminate = false;
		
		public RemoteControl(RMIClientGUI_RemoteControl rmiGUI_RC, String server, String ip, String port) {
			this.rmiGUI_RC = rmiGUI_RC;
			this.server = server;
			this.ip = ip;
			this.port = port;
			
			this.rmiGUI = (RMIClientGUI)rmiDebug.getTopLevelAncestor();
		}
		
		public void run() {
			boolean openState = false;
			
			try {
				rmiDebug.setDebugText("Remote Control - " + server + 
										" on IP " + ip + 
										" @ Port " + port + " ...");
				
				rmi_RC = new RMIClient_RemoteControl(ip, port, rmiGUI_RC);
				
				openState = rmi_RC.openRC();
				rmiDebug.setDebugText("Open Remote Control session: " + openState);
				
				if (openState == true) {
					drawUI();
					
					d = rmi_RC.getScreenSize();;
					screenRect = new Rectangle(0,0,(int)d.getWidth(),(int)d.getHeight());
					
					while (terminate == false) {
						//System.out.println("Receiving UI"); //Debugger
						//ic = rcserver.captureScreen(screenRect);
						byte [] data = rmi_RC.captureScreen(screenRect);
						
						//Error has occured, break the loop immediately
						if (data == null) {
							jf.setTitle("Remote Control - " + server);
							jlbMessage.setText("<html>Status: <font color='red'>Fail to connect server!</font></html>");
							
							terminate = true;
							break;
						}
						
						jf.setTitle("Remote Control - " + server);
						jlbMessage.setText("Status: Connected");
						
						paint(data);
					}
					
					rmiDebug.setDebugText("Destroy Remote Control session: " + rmi_RC.releaseRC());
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
				
			} finally {
				rmiDebug.setDebugText("Remote Control [Beta] - " + server + " has been terminated");
				
				if (openState == true) {
					jpScreen.removeAll();
					jpScreen.revalidate();
					jpScreen.updateUI();
					
					//Close the Window
					jf.setVisible(false);
					jf.dispose();
				}
			}
		}
		
		public void drawUI() {
			//Avoid flashing when JDialog and JFrame update themselves
			System.setProperty("sun.awt.noerasebackground", "true");
			
			jf = new JFrame("Remote Control - Loading... " + server);
			jf.setResizable(false);
			jf.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/image/rmiAdmin_small_logo.gif")));
			
			jf.getContentPane().setLayout(new BorderLayout());
				jsp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					jpScreen = new JPanel();
					jpScreen.setLayout(new BorderLayout());
				
						jlbScreen = new JLabel();
						jlbScreen.addMouseMotionListener(new MouseMotionListener() {
							public void mouseMoved(MouseEvent me) {
								int x = me.getX();
								int y = me.getY();
								
								try {
									rmi_RC.mouseMove(x,y);
								} catch (Exception exc) {
									StackTraceElement[] error = exc.getStackTrace();
									rmiDebug.setDebugText(error);
									warning(exc.getMessage());
								}
							}
							public void mouseDragged(MouseEvent me) {
								int x = me.getX();
								int y = me.getY();
								
								try {
									rmi_RC.mouseMove(x,y);
								} catch (Exception exc) {
									StackTraceElement[] error = exc.getStackTrace();
									rmiDebug.setDebugText(error);
									warning(exc.getMessage());
								}
							}
						});
						jlbScreen.addMouseListener(new MouseListener() {
							public void mousePressed(MouseEvent me) {
								int buttons = me.getButton();
								
								try {
									rmi_RC.mousePress(buttons);
								} catch (Exception exc) {
									StackTraceElement[] error = exc.getStackTrace();
									rmiDebug.setDebugText(error);
									warning(exc.getMessage());
								}
							}
							public void mouseClicked(MouseEvent me) {
							}
							public void mouseEntered(MouseEvent me) {
							}
							public void mouseExited(MouseEvent me) {
							}
							public void mouseReleased(MouseEvent me) {
								int buttons = me.getButton();
								
								try {
									rmi_RC.mouseRelease(buttons);
								} catch (Exception exc) {
									StackTraceElement[] error = exc.getStackTrace();
									rmiDebug.setDebugText(error);
									warning(exc.getMessage());
								}
							}
						});
						jlbScreen.addMouseWheelListener(new MouseWheelListener() {
							public void mouseWheelMoved(MouseWheelEvent mwe) {
								int wr = mwe.getWheelRotation();
								
								try {
									rmi_RC.mouseWheel(wr);
								} catch (Exception exc) {
									StackTraceElement[] error = exc.getStackTrace();
									rmiDebug.setDebugText(error);
									warning(exc.getMessage());
								}
							}
						});
						jf.addKeyListener(new KeyAdapter() {
							public void keyPressed(KeyEvent ke) {
								int keycode = ke.getKeyCode();
								//System.out.println(KeyEvent.getKeyText(keycode)); //Debugger
								
								try {
									rmi_RC.keyPress(keycode);
								} catch (Exception exc) {
									StackTraceElement[] error = exc.getStackTrace();
									rmiDebug.setDebugText(error);
									warning(exc.getMessage());
								}
							} 
							public void keyReleased(KeyEvent ke) {
								int keycode = ke.getKeyCode();
								//System.out.println(KeyEvent.getKeyText(keycode)); //Debugger
								
								try {
									rmi_RC.keyRelease(keycode);
								} catch (Exception exc) {
									StackTraceElement[] error = exc.getStackTrace();
									rmiDebug.setDebugText(error);
									warning(exc.getMessage());
								}
							}
							public void keyTyped(KeyEvent ke) {}
						});
						
					jpScreen.add(jlbScreen, BorderLayout.CENTER);
					
				jsp.getViewport().add(jpScreen);
				
				jpBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
					jlbMessage = new JLabel("Status: ");
				jpBottom.add(jlbMessage);
				
			jf.getContentPane().add(jsp, BorderLayout.CENTER);
			jf.getContentPane().add(jpBottom, BorderLayout.SOUTH);
			
			jf.addWindowListener(new WindowListener() {
				public void windowOpened(WindowEvent we) {}
				public void windowIconified(WindowEvent we) {}
				public void windowDeiconified(WindowEvent we) {}
				public void windowDeactivated(WindowEvent we) {}
				public void windowClosing(WindowEvent we) {
					terminate = true;
				}
				public void windowClosed(WindowEvent we) {}
				public void windowActivated(WindowEvent we) {}
			});
			
			jf.setSize(800,600);
			jf.setLocationRelativeTo(null);
			jf.setVisible(true);
		}
		
		public void paint(byte [] data) {
			//reconstruct bufferedimage from data,w,h
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
			ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
			SampleModel sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, (int)d.getWidth(), (int)d.getHeight(), 3, 3*(int)d.getWidth(), new int[]{2,1,0});
			DataBuffer resultBuffer = new DataBufferByte(data, data.length);
			WritableRaster resultRaster = Raster.createWritableRaster(sm, resultBuffer, null);
			BufferedImage bi = new BufferedImage(cm, resultRaster, false, null);
			
			jlbScreen.setIcon(new ImageIcon(bi));
			jlbScreen.updateUI();
			jlbScreen.revalidate();
		}
		
		public void terminate() {
			terminate = true;
		}
	}
}