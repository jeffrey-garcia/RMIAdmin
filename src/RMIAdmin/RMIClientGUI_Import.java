package RMIAdmin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import java.io.*;
import java.util.*;


public class RMIClientGUI_Import extends JFrame implements RMIClientGUI_Function {
	private JDesktopPane jdp;
	
	public RMIClientGUI_Debugger rmiDebug;
	
	private JFileChooser jfc;
	
	/****************************
	 * The object being imported
	 * 1. serverList
	 ***************************/
	private String exportType = "";
	
	
	public RMIClientGUI_Import(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug, String exportType) {
		this.rmiDebug = rmiDebug;
		this.exportType = exportType;
		this.jdp = jdp;
		
		initGUI();
	}
	
	
	public void initGUI() {
		jfc = new JFileChooser();
		showJFC();
	}
	
	
	private void showJFC() {
		try {
			//Set the file filter first
			setFileFilter();
			
			//Pass the jdp into the showOpenDialog, such that JFC appear in absolute middle of application
			int returnVal = jfc.showOpenDialog(jdp);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				rmiDebug.setDebugText("Selected File: " + file + " "); //Debugger
				
				rmiDebug.setDebugText("Start Import File...");
				
					FileWriter fw;
					String text = "";
				
					if (exportType.equals("serverList")) {
						fw = new FileWriter("RMIServer.xml",false);
						
						if (file.getName().toLowerCase().indexOf(".xml") >= 0) {
							//nothing to do...
						} else {
							//nothing to do...
						}
						
						BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
						String tmp = "";
						while ((tmp = br.readLine()) != null) {
							text += tmp;
						}
						
					} else {
						fw = new FileWriter(file.getPath(),false);
					}
					
					PrintWriter pw = new PrintWriter(fw);
					pw.println(text);
					
					pw.close();
					fw.close();
				
				rmiDebug.setDebugText("Done");
				//warning("Import Completed."); //not necessary
				
			} else {
				System.out.println("Import cancelled by user.");
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			warning(exc.getMessage());
		}
	}
	
	
	private void setFileFilter() {
		try {
			if (exportType.equals("serverList")) {
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				    //Accept XML file only
				    public boolean accept(File f) {
				        if (f.isDirectory()) {
				            return true;
				        }
				        
				        if (f.getName().indexOf(".xml")>=0) {
				        	return true;
				        } else if (f.getName().indexOf(".XML")>=0) {
				        	return true;
				        } else {
				        	return false;	
				        }
				    }
				
				    //The description of this filter
				    public String getDescription() {
				        return "XML files. (*.xml)";
				    }
				});
				
			} else {
				//nothing to do, no filter applied.
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
			warning(exc.getMessage());
		}
	}
	
	
	public RMIClientGUI_Debugger getDebugger() {
		return rmiDebug;
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
}