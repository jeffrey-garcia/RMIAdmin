package RMIAdmin;


import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.*;
import java.awt.*;


public class RMIClient_UIColor extends DefaultMetalTheme {
	private String name = "MutableMetalTheme";
	private ColorUIResource primary1 = null;
	private ColorUIResource primary2 = null;
	private ColorUIResource primary3 = null;
	private ColorUIResource secondary1 = null;
	private ColorUIResource secondary2 = null;
	private ColorUIResource secondary3 = null;
	private ColorUIResource black = null;
	private ColorUIResource white = null;
	
	
	public RMIClient_UIColor(){
		initColors();
	}
	
	//Methods to set each ColorUIResource value
	protected void setPrimary1( ColorUIResource primary1 ){
		this.primary1 = primary1;
	}
	
	
	//...other setXXX methods corresponding to
	//each ColorUIResource variable
	protected void setPrimary2( ColorUIResource primary2 ){
		this.primary2 = primary2;
	}
	
	
	protected void setPrimary3( ColorUIResource primary3 ){
		this.primary3 = primary3;
	}
	
	
	protected void setSecondary1( ColorUIResource secondary1 ){
		this.secondary1 = secondary1;
	}
	
	
	protected void setSecondary2( ColorUIResource secondary2 ){
		this.secondary2 = secondary2;
	}
	
	
	protected void setSecondary3( ColorUIResource secondary3 ){
		this.secondary3 = secondary3;
	}
	
	
	//Utility methods to set ColorUIResource values with a Color object
	protected void setPrimary1( Color primary1 ){
		this.primary1 = new ColorUIResource( primary1 );
	}
	
	
	//Methods to support the MetalLookAndFeel theme mechanism... 
	public String getName() { return name; }
	protected ColorUIResource getPrimary1() {
		return primary1;
	}
	protected ColorUIResource getPrimary2() {
		return new ColorUIResource(Color.GRAY);
		//return primary2;
	}
	protected ColorUIResource getPrimary3() {
		return primary3;
	}
	protected ColorUIResource getSecondary1() {
		return secondary1;
	}
	protected ColorUIResource getSecondary2() {
		return new ColorUIResource(Color.lightGray);
		//return secondary2;
	}
	protected ColorUIResource getSecondary3() {
		return new ColorUIResource(Color.WHITE);
		//return secondary3;
	}
	protected ColorUIResource getBlack() {
		return new ColorUIResource(Color.darkGray);
		//return black;
	}
	protected ColorUIResource getWhite() {
		return new ColorUIResource(Color.WHITE);
		//return white;
	}
	
	
	/**
	* Initialize all colors to be the same as in the current MetalLookAndFeel.
	*/
	private void initColors() {
		MetalLookAndFeel lf = null;
		
		try{
			//System.out.println(UIManager.getLookAndFeel().getName()); //Debugger
			
			if (UIManager.getLookAndFeel().getName().indexOf("Metal") >= 0) {
				lf = (MetalLookAndFeel) UIManager.getLookAndFeel();
				
				primary1 = lf.getPrimaryControlDarkShadow();
				primary2 = lf.getDesktopColor();
				primary3 = lf.getPrimaryControl();
				secondary1 = lf.getControlDarkShadow();
				secondary2 = lf.getControlShadow();
				secondary3 = lf.getControl();
				black = lf.getControlHighlight();
				white = lf.getUserTextColor();
			}
			
		} catch( Exception e ){
			e.printStackTrace();
		}
	}
}
