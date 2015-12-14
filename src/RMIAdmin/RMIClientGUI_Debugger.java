package RMIAdmin;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class RMIClientGUI_Debugger extends JPanel {
	private JDesktopPane jdp;
	private JTextArea jtaDebug;
	private String debugTxt;
	
	
	public RMIClientGUI_Debugger(JDesktopPane jdp) {
		this.jdp = jdp;
		debugTxt = "";
		//setBackground(Color.darkGray);
		setLayout(new BorderLayout());
		
		initGUI();
	}
	
	
	private void initGUI() {
		JPanel jpCenter = new JPanel();
		jpCenter.setLayout(new BorderLayout());
		
    		jtaDebug = new JTextArea("System Ready.\n");
			jtaDebug.setEditable(false);
			jtaDebug.addMouseListener(new RMIClientGUI_MouseListener());
			jtaDebug.setBackground(Color.BLACK);
			jtaDebug.setForeground(Color.GREEN);
			jtaDebug.setToolTipText("Debug Console");
			
			JScrollPane jspDebug = new JScrollPane(jtaDebug,
								JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		jpCenter.add(jspDebug, BorderLayout.CENTER);
		jpCenter.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Debug Console"),
				BorderFactory.createEmptyBorder(5,5,5,5)
			)
		);
		
		add(jpCenter);
	}
	
	
	public void setDebugText(String text) {
		jtaDebug.append(text + "\n");
		jtaDebug.setCaretPosition(jtaDebug.getDocument().getLength());
	}
	
	
	public void setDebugText(StackTraceElement ste []) {
		for (int i=0; i<ste.length; i++) {
			jtaDebug.append(ste [i] + "\n");
			jtaDebug.setCaretPosition(jtaDebug.getDocument().getLength());
		}
	}
	
	
	public String getDebugText() {
		return jtaDebug.getText();
	}
	
	
	public void clearText() {
		jtaDebug.setText("");
	}
}