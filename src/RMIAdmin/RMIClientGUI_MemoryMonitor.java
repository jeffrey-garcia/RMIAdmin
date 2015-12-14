package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.*;


public class RMIClientGUI_MemoryMonitor extends JPanel implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	private RMIClientGUI_Debugger rmiDebug;
	
	private int serverID = 0;
	private String serverHostname = "";
	private String serverIP = "";
	private String serverPort = "";
	private String serverOS = "";
	private String serverShell = "";
	private String serverDesc = "";
	
	
	public RMIClientGUI_MemoryMonitor(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
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
				
				loadMonitor();
			}
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void loadMonitor() {
		new MemoryMonitor(this, serverHostname, serverIP, serverPort).start();
	}
	
	
	private class MemoryMonitor extends Thread implements ActionListener {
		private RMIClientGUI rmiGUI;
		private RMIClientGUI_MemoryMonitor rmiGUI_MM;
		private RMIClient_MemoryMonitor rmi_MM;
		
		private String server;
		private String ip;
		private String port;
		
		private long [] memory;
		
		private JInternalFrame jif;
		private JPanel jpTop;
		private JPanel jpCenter;
		private JPanel jpBottom;
		
		private JButton jbtnGC;
		private JButton jbtnClose;
		private JButton jbtnSystemInfo;
		
		private JLabel jlbFreeMem;
		private JLabel jlbTotalMem;
		private JLabel jlbMaxMem;
		private JLabel jlbMessage;
		
		private Vector vPoint;
		
		private String [][] systemInfo;
		
		private boolean terminate = false;
		
		
		public MemoryMonitor(RMIClientGUI_MemoryMonitor rmiGUI_MM, String server, String ip, String port) {
			this.rmiGUI_MM = rmiGUI_MM;
			this.server = server;
			this.ip = ip;
			this.port = port;
			
			this.rmiGUI = (RMIClientGUI)rmiDebug.getTopLevelAncestor();
		}
		
		public void run() {
			try {
				rmiDebug.setDebugText("Load VM Memory Monitor - " + server + 
										" on IP " + ip + 
										" @ Port " + port + " ...");
				
				rmi_MM = new RMIClient_MemoryMonitor(ip, port, rmiGUI_MM);
				
				drawUI(server);
				
				while (terminate == false) {
					memory = rmi_MM.getMemory();
					
					//Error has occured, break the loop immediately
					if (memory [0] == 0) {
						jif.setTitle("VM Memory Monitor - " + server);
						jlbMessage.setText("<html><font color='red'>Fail to connect server!</font></html>");
						
						terminate = true;
						break;
					}
					
					jif.setTitle("VM Memory Monitor - " + server);
					
					paint(memory); //Paint the graph
					
					sleep(1000); //wait for 1000 milli-seconds
				}
				
				rmiDebug.setDebugText("Memory Monitor - " + server + " has been terminated");
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		private void drawUI(String server) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			jif = new JInternalFrame("VM Memory Monitor - Loading " + server + "...");
			jif.setResizable(false);
	    	jif.setIconifiable(true);
	    	jif.setMaximizable(false);
	    	jif.setClosable(false);
	    	jif.setLayer(JLayeredPane.PALETTE_LAYER); //Makes the JInternalFrame always on top
    		
			jif.getContentPane().setLayout(new BorderLayout());
			
				JPanel jpTop1 = new JPanel();
				
					jpTop = new JPanel();
					jpTop.setBackground(Color.BLACK);
					jpTop.setLayout(new FlowLayout(FlowLayout.CENTER));
				
				jpTop1.add(jpTop);
				jpTop1.setBorder(
					BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Memory Usage History"),
						BorderFactory.createEmptyBorder(5,5,5,5)
					)
				);
				jpTop.setPreferredSize(new Dimension(250,100));
				jpTop.setMaximumSize(new Dimension(250,100));
				jpTop.setMinimumSize(new Dimension(250,100));
				
				jpCenter = new JPanel();
				jpCenter.setLayout(new BorderLayout());
					
					JPanel jpCenter_1 = new JPanel(new GridLayout(3,2,1,1));
					
						JPanel jp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
							JLabel jlbFree = new JLabel("VM Free Memory:");
						jp1.add(jlbFree);
						
						JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
							jlbFreeMem = new JLabel("   ");
						jp2.add(jlbFreeMem);
						
						JPanel jp3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
							JLabel jlbTotal = new JLabel("VM Total Memory:");
						jp3.add(jlbTotal);
						
						JPanel jp4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
							jlbTotalMem = new JLabel("  ");
						jp4.add(jlbTotalMem);
						
						JPanel jp5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
							JLabel jlbMax = new JLabel("VM Max Memory:");
						jp5.add(jlbMax);
						
						JPanel jp6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
							jlbMaxMem = new JLabel("  ");
						jp6.add(jlbMaxMem);
					
					jpCenter_1.add(jp1);
					jpCenter_1.add(jp2);
					jpCenter_1.add(jp3);
					jpCenter_1.add(jp4);
					jpCenter_1.add(jp5);
					jpCenter_1.add(jp6);
					
					JPanel jpCenter_2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
					
						jbtnSystemInfo = new JButton("View remote System Info.");
						jbtnSystemInfo.setToolTipText("Click this button to view the remote system information");
						jbtnSystemInfo.addActionListener(this);
					
					jpCenter_2.add(jbtnSystemInfo);
					
				jpCenter.add(jpCenter_1, BorderLayout.NORTH);
				jpCenter.add(jpCenter_2, BorderLayout.CENTER);
					
				jpCenter.setBorder(
					BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Virtual Machine Memory (KBytes)"),
						BorderFactory.createEmptyBorder(5,5,5,5)
					)
				);
				
				jpBottom = new JPanel();
				jpBottom.setLayout(new FlowLayout(FlowLayout.CENTER));
				
					jlbMessage = new JLabel("Release Un-used Memory: ");
					
					jbtnGC = new JButton("Run GC");
					jbtnGC.setToolTipText("Click this button to invoke remote system garbage collection");
					jbtnGC.addActionListener(this);
					
					jbtnClose = new JButton("Quit");
					jbtnClose.setToolTipText("Click this button to close the momory monitor");
					jbtnClose.addActionListener(this);
				
				jpBottom.add(jlbMessage);
				jpBottom.add(jbtnGC);
				jpBottom.add(jbtnClose);
				
			jif.getContentPane().add(jpTop1, BorderLayout.NORTH);
			jif.getContentPane().add(jpCenter, BorderLayout.CENTER);
			jif.getContentPane().add(jpBottom, BorderLayout.SOUTH);
			jif.pack();
			
		    try {
		    	jif.setSelected(true);
		    } catch (Exception exc) {
		    	//Nothing to do
		    }
		    
		    jdp.add(jif);
			
			//Calulation the position of JInternalFrame to appear in absolute middle of application
			int x = (int)(jdp.getWidth()/2 - jif.getWidth()/2);
			int y = (int)(jdp.getHeight()/2 - 50 - jif.getHeight()/2);
			
			jif.setLocation(x,y);
			jif.setVisible(true);
			
			vPoint = new Vector ();
			for (int i=0; i<jpTop.getWidth(); i++) {
				vPoint.addElement("20");
			}
		}
		
		private void paint(long [] memory) {
			try {
				if (jif.isIcon() == false) {
					Graphics G = jpTop.getGraphics();
					G.clearRect(0,0,jpTop.getWidth(),jpTop.getHeight());
					G.fillRect(0,0,jpTop.getWidth(),jpTop.getHeight());
					jpTop.setBackground(Color.BLACK);
					G.setColor(Color.GREEN.darker());
					
					int width = vPoint.size();
					int height = jpTop.getHeight();
					
					for (int i=0; i<width; i++) {
						if (i%20 == 0) {
							G.drawLine(i,0,i,height);
						}
					}
					
					for (int i=0; i<height; i++) {
						if (i%20 == 0) {
							G.drawLine(0,i,width,i);
						}
					}
					
					int x = width;
					int y = 0;
					
					long freeMemory = memory [0];
					long totalMemory = memory [1];
					long maxMemory = memory [2];
					
					jlbFreeMem.setText((freeMemory/1024) + "");
					jlbTotalMem.setText((totalMemory/1024) + "");
					jlbMaxMem.setText((maxMemory/1024) + "");
					
					G.setColor(Color.YELLOW);
					
					//=== Update the previous coordinates ===
					double d = (double)((totalMemory - freeMemory) * 100 / totalMemory);
					y = new Double(d + "").intValue();
					
					for (int i=0; i<vPoint.size()-1; i++) {
						String nextValue = vPoint.elementAt(i+1) + "";
						vPoint.setElementAt(nextValue, i);
					}
					vPoint.setElementAt(y+"", width-1);
					
					for (int i=0; i<vPoint.size()-1; i++) {
						int nextValue = Integer.parseInt(vPoint.elementAt(i+1) + "");
						int currValue = Integer.parseInt(vPoint.elementAt(i) + "");
						G.drawLine(i,height-currValue,i+1,height-nextValue);
					}
					//=======================================	
				}
				
			} catch (Exception exc) {
				StackTraceElement[] error = exc.getStackTrace();
				rmiDebug.setDebugText(error);
				warning(exc.getMessage());
			}
		}
		
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == jbtnGC) {
				rmiDebug.setDebugText("Invoke Garbage Collection - " + server + 
										" on IP " + ip + 
										" @ Port " + port + " ...");
				
				rmi_MM.collectGarbage();
				
			} else if (ae.getSource() == jbtnClose) {
				terminate = true;
				
				jif.setVisible(false);
				jif.dispose();
				
			} else if (ae.getSource() == jbtnSystemInfo) {
				systemInfo = null;
				
				rmiDebug.setDebugText("Get System Info - " + server + 
										" on IP " + ip + 
										" @ Port " + port + " ...");
				
				systemInfo = rmi_MM.getSystemInfo();
				
				if (systemInfo != null) {
					String info = "";
					
					for (int i=0; i<systemInfo.length; i++) {
						info += systemInfo [i][0] + ": <font color='#666666'>" + systemInfo [i][1] + "</font><br>";
					}
					
					rmiGUI.alert(info);
				}
			}
		}
	}
}