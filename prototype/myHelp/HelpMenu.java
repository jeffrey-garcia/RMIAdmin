import java.net.*;
import javax.help.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HelpMenu {
	
	public HelpMenu() {
	   	//Make sure we have nice window decorations.
	   	JFrame.setDefaultLookAndFeelDecorated(true);
	   	
	   	JFrame f;
	   	JMenuItem topics;
	   	
	   	f = new JFrame("Menu Example");
	   	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   	JMenuBar mbar = new JMenuBar();
	
		// a file menu
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");
		
		// add an item to the help menu
		help.add(topics = new JMenuItem("Help Topics"));
		
		// add the menu items to the menu bar
		mbar.add(file);
		mbar.add(help);
		
		// 1. create HelpSet and HelpBroker objects
		HelpSet hs = getHelpSet("RMIAdminHelp.hs"); 
		HelpBroker hb = hs.createHelpBroker();
		
		// 2. assign help to components
		CSH.setHelpIDString(topics, "top");
		
		// 3. handle events
		topics.addActionListener(new CSH.DisplayHelpFromSource(hb));
		
		// attach menubar to frame, set its size, and make it visible
		f.setJMenuBar(mbar);
		f.setSize(500, 300);
		f.setVisible(true);
	}
	
	/**
	 * find the helpset file and create a HelpSet object
	 */
	public HelpSet getHelpSet(String helpsetfile) {
		HelpSet hs = null;
		ClassLoader cl = this.getClass().getClassLoader();
		
		try {
			URL hsURL = HelpSet.findHelpSet(cl, helpsetfile);
			hs = new HelpSet(null, hsURL);
		} catch(Exception ee) {
			System.out.println("HelpSet: "+ee.getMessage());
			System.out.println("HelpSet: "+ helpsetfile + " not found");
		}
		
		return hs;
	}
	
	public static void main(String argv[]) {
		new HelpMenu();
	}
}
