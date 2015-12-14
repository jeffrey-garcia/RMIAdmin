package RMIAdmin;


import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.net.*;


public class RMIClientGUI_Startup implements Runnable {
	public JFrame jf;
	public Window w;
	public Image img;
	
	public static void main(String [] args) {
		RMIClientGUI_Startup logo = new RMIClientGUI_Startup();
		Thread thread = new Thread(logo);
		thread.start();
	}
		
	public RMIClientGUI_Startup() {
		jf = new JFrame();
		w = new Window(jf);
		
		ClassLoader cl = this.getClass().getClassLoader();
		URL imgURL = cl.getResource("image/StartupLogo.gif");
		
		img = Toolkit.getDefaultToolkit().getImage(imgURL);
	}
	
	public void run() {
		w.add(new ImageCanvas(img));
		w.setSize(450, 300);
		w.setLocationRelativeTo(null);
		w.setVisible(true);
	}
	
	public void dispose() {
		w.setVisible(false);
		w.dispose();
		jf.dispose();
	}
	
	public class ImageCanvas extends Canvas {
		private Image img;
		
		public ImageCanvas(Image img) {
			this.img = img;
		}
		
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, this);
		}
	}
	
}