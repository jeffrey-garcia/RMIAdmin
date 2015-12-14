package RMIAdmin;


import javax.swing.*;


public class initRMIClient {
	
	
	public static void main(String [] args) {
		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		final RMIClientGUI_Startup logo = new RMIClientGUI_Startup();
		final Thread init = new Thread(logo);
		init.start();
		
		try {
			init.sleep(5000);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        
        logo.dispose();
	}
	
	
	private static void createAndShowGUI() {
		new RMIClientGUI();
	}
	
	
	/*
	public static void main(String [] args) {
		new RMIClient_Thread().start();
	}
	
	
	private class RMIClient_Thread extends Thread {
		public void run(){
			RMIClientGUI gui = new RMIClientGUI();
			
			RMIClientGUI_Startup logo = new RMIClientGUI_Startup();
			Thread init = new Thread(logo);
			init.start();
			
			try {
				while (gui.isReady() == false) {
					init.sleep(1000);
				}
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}
	*/
}
