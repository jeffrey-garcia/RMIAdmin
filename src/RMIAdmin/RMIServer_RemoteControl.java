package RMIAdmin;


import java.rmi.*;
import java.rmi.server.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.imageio.*;

import java.io.*;
import java.net.*;


public class RMIServer_RemoteControl {
	private Robot r;
	
	
	public RMIServer_RemoteControl(Robot r) {
		this.r = r;
	}
	
	
	public byte [] captureScreen(Rectangle screenRect) {
		BufferedImage bi;
		//ImageIcon ic = null;
		byte [] data = null;
		
		try {
			long t1, t2;
			//t1 = System.currentTimeMillis();
			
			bi = r.createScreenCapture(screenRect);
			//ic = new ImageIcon(bi);
			
			//t2 = System.currentTimeMillis();
			//System.out.println("Screen Captured. Time elapsed:" + (t2 - t1));
			//t1 = t2;
			
			int w = bi.getWidth();
			int h = bi.getHeight();
			
			//convert to a known format
			BufferedImage std = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g = std.createGraphics();
			g.drawRenderedImage(bi, null);
			g.dispose();
			
			//t2 = System.currentTimeMillis();
			//System.out.println("Screen transformed. Time elapsed:" + (t2 - t1));
			//t1 = t2;
			
			//grab data
			DataBufferByte buffer = (DataBufferByte) std.getRaster().getDataBuffer();
			data = buffer.getData();
			
			//t2 = System.currentTimeMillis();
			//System.out.println("Screen converted to data stream. Time elapsed:" + (t2 - t1));
			//t1 = t2;
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		//System.out.println("Sending UI"); //Debugger
		//return ic;
		return data;
	}
	
	
	public void keyPress(int keycode) {
		r.keyPress(keycode);
	}
	
	
	public void keyRelease(int keycode) {
		r.keyRelease(keycode);
	}
	
	
	public void mouseMove(int x, int y) {
		r.mouseMove(x,y);
	}
	
	
	public void mousePress(int buttons) {
		if (buttons == 1) {
			r.mousePress(InputEvent.BUTTON1_MASK);
		} else if (buttons == 2) {
			r.mousePress(InputEvent.BUTTON2_MASK);
		} else if (buttons == 3) {
			r.mousePress(InputEvent.BUTTON3_MASK);
		}
	}
	
	
	public void mouseRelease(int buttons) {
		if (buttons == 1) {
			r.mouseRelease(InputEvent.BUTTON1_MASK);
		} else if (buttons == 2) {
			r.mouseRelease(InputEvent.BUTTON2_MASK);
		} else if (buttons == 3) {
			r.mouseRelease(InputEvent.BUTTON3_MASK);
		}
	}
	
	
	public void mouseWheel(int wheelAmt) {
		r.mouseWheel(wheelAmt);
	}
	
	
	private void trapError(Exception exc) {
		//Print Exception Message to Logfile
		(new RMIServer_EventHandler(exc.getMessage())).getFile();
		
		//Print Stack Trace to Logfile
		(new RMIServer_EventHandler(exc.getStackTrace())).getFile();
	}
}