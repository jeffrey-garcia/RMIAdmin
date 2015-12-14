package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;


public class RMIClientGUI_About extends JPanel {
	private JDesktopPane jdp;
	
	public RMIClientGUI_Debugger rmiDebug;
	
	
	public RMIClientGUI_About(JDesktopPane jdp, RMIClientGUI_Debugger rmiDebug) {
		this.rmiDebug = rmiDebug;
		this.jdp = jdp;
		
		initGUI();
	}
	
	
	public void initGUI() {
		JDialog.setDefaultLookAndFeelDecorated(true);
		final JDialog jdg;
		
		jdg = new JDialog((JFrame)jdp.getTopLevelAncestor(), true);
		jdg.setResizable(false);
		jdg.setTitle("About " + ((JFrame)jdp.getTopLevelAncestor()).getTitle());
		jdg.getContentPane().setLayout(new BorderLayout());
		
			JPanel jpTop = new JPanel();
			jpTop.setLayout(new FlowLayout(FlowLayout.CENTER));
		 	jpTop.add(new JLabel(new ImageIcon(this.getClass().getResource("/image/rmiAdmin_logo.gif"))));
		
			JPanel jpCenter = new JPanel();
			jpCenter.setLayout(new BorderLayout());
			
				JLabel jlbText = new JLabel();
				jlbText.setText("<html><" + 
								"<font size='15' color='#FF0066'>rmiAdmin</font>" + "<br>" +
								"Copyright &copy; 2005 RMIAdmin.net , All Rights Reserved." + "<br>" +
								"<br></html>");
								
				JTextArea jtaText = new JTextArea("");
				jtaText.setText("Warning: This computer program is protected by copyright law \n" + 
								"and international treaties." + "\n\n" + 
								"Unauthorized reproduction or distribution of this program, \n" + 
								"or any portion of it, may result in severe civil and \n" + 
								"criminal penalties, and will be prosecuted to the maximum \n" + 
								"extent possible under the law.\n\n");
				jtaText.setEditable(false);
				jtaText.setLineWrap(false);
				jtaText.setCaretPosition(0);
				JScrollPane jspText = new JScrollPane(jtaText,
									JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				jspText.setPreferredSize(new Dimension(180,100));
				
			jpCenter.add(jlbText,BorderLayout.NORTH);
			jpCenter.add(jspText,BorderLayout.CENTER);
			
			jpCenter.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(""),
					BorderFactory.createEmptyBorder(5,5,5,5)
				)
			);
		
			JPanel jpBottom = new JPanel();
			jpBottom.setLayout(new FlowLayout(FlowLayout.CENTER));
			
				JButton jbtn = new JButton(" OK ");
				jbtn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						jdg.setVisible(false);
						jdg.dispose();
					}
				});
			
			jpBottom.add(jbtn);
			
		jdg.getContentPane().add(jpTop, BorderLayout.NORTH);
		jdg.getContentPane().add(jpCenter, BorderLayout.CENTER);
		jdg.getContentPane().add(jpBottom, BorderLayout.SOUTH);
		
		jdg.setSize(430,420);
		//jdg.pack();
		
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
}