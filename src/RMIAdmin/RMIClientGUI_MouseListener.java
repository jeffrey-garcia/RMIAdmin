package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javax.swing.text.*;


public class RMIClientGUI_MouseListener implements MouseListener {
	JPopupMenu jpm;
	
	
	public RMIClientGUI_MouseListener() {
    	//===== Mouse Right Click Menu =====
    	jpm = new JPopupMenu();
		Action copy = new TextAction("Copy") {
			public void actionPerformed(ActionEvent e) {
				getFocusedComponent().copy();
			}
		};
		Action paste = new TextAction("Paste") {
			public void actionPerformed(ActionEvent e) {
				/***************************************
				 * When pasting large amount of files, 
				 * the process just keep hangup and 
				 * consume extremely high CPU usage. 
				 * 
				 * It is confirmed that the problem is 
				 * solved on JDK 1.4.2 or higher
				 **************************************/
				getFocusedComponent().paste();
			}
		};
		Action cut = new TextAction("Cut") {
			public void actionPerformed(ActionEvent e) {
				getFocusedComponent().cut();
			}
		};
		Action selectAll = new TextAction("Select All") {
			public void actionPerformed(ActionEvent e) {
				getFocusedComponent().setSelectionStart(0);
				getFocusedComponent().setSelectionEnd(getFocusedComponent().getText().length());
			}
		};
        jpm.add(new JMenuItem(cut));
        jpm.add(new JMenuItem(copy));
        jpm.add(new JMenuItem(paste));
        jpm.addSeparator();
        jpm.add(new JMenuItem(selectAll));
    	//==================================
	}
	
	
	public void mouseReleased(MouseEvent me) {
		showPopup(me);
	}
	
	
	public void mousePressed(MouseEvent me) {
		showPopup(me);
	}
	
	
	public void mouseExited(MouseEvent me) {
	}
	
	
	public void mouseEntered(MouseEvent me) {
	}
	
	
	public void mouseClicked(MouseEvent me) {
	}
	
	
	public void showPopup(MouseEvent me) {
		//Restrict popup menu show only when right click
		if (me.isPopupTrigger()) {
			jpm.show(me.getComponent(),me.getX(), me.getY());
		}
	}
}