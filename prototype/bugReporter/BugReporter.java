import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

import java.util.*;


public class BugReporter extends JApplet {
	private JApplet jap; //A pointer to this object for referencing in inner class
	
	private JScrollPane jsp; //The JScrollPane which display the information of retrieved records
	private JPanel jpRecNav; //The JPanel which contain the navigator to traverse the retrieved records
	private JTextArea jta; //The JTextArea which contain the information of retrieved records
	private JPanel jpStatus; //The JPanel which contain the system message or status of the search
	
	private JLabel jlbSearchResult;
	private JLabel jlbStatusMsg;
	private JButton jbtnNextRec;
	private JButton jbtnBackRec;
	
	private int searchBy = -1; //the default value
	
	private final static int byID = 0;
	private final static int byCategory = 2;
	private final static int byReleaseDate = 3;
	private final static int bySynopsisKey = 1;
	private final static int byDetailKey = 8;
	private final static int byStatus = 4;
	private final static int bySeverity = 6;
	
	
	public void init() {
		getContentPane().setLayout(new BorderLayout());
		
		JPanel jpMain = new JPanel();
		jpMain.setLayout(new BorderLayout());
		jpMain.setBackground(Color.WHITE);
		
			JPanel jpTop = new JPanel();
			jpTop.setLayout(new GridLayout(2,1,2,2));
			jpTop.setBackground(Color.WHITE);
			
				JPanel jpTop_1 = new JPanel();
				jpTop_1.setLayout(new FlowLayout(FlowLayout.LEFT));
				jpTop_1.setBackground(Color.WHITE);
				
					final JComboBox jcb = new JComboBox();
					jcb.setBackground(Color.WHITE);
					jcb.addItem("Any");
					jcb.addItem("Bug ID");
					jcb.addItem("Category");
					jcb.addItem("Release Date");
					jcb.addItem("Keyword in Synopsis");
					jcb.addItem("Keyword in Bug Detail");
					jcb.addItem("Status");
					jcb.addItem("Severity");
					jcb.setToolTipText("Select the preferred search criteria.");
				
				jpTop_1.add(new JLabel("<html><font color='#FF0066'>Search by :</font></html>"));
				jpTop_1.add(jcb);
				
				JPanel jpTop_2 = new JPanel();
				jpTop_2.setLayout(new FlowLayout(FlowLayout.LEFT));
				jpTop_2.setBackground(Color.WHITE);
				
					final JTextField jtf = new JTextField(15);
					jtf.setToolTipText("Input your search string here.");
				
					JButton jbtnSearch = new JButton("Search");
					jbtnSearch.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							searchBy = jcb.getSelectedIndex();
							
							if (searchBy <= 0) {
								searchBy = -1; //make it the default choice if nothing is chosen
							} else {
								if (searchBy == 1) {
									searchBy = BugReporter.byID;
								} else if (searchBy == 2) {
									searchBy = BugReporter.byCategory;
								} else if (searchBy == 3) {
									searchBy = BugReporter.byReleaseDate;
								} else if (searchBy == 4) {
									searchBy = BugReporter.bySynopsisKey;
								} else if (searchBy ==5) {
									searchBy = BugReporter.byDetailKey;
								} else if (searchBy == 6) {
									searchBy = BugReporter.byStatus;
								} else if (searchBy == 7) {
									searchBy = BugReporter.bySeverity;
								}
							}
							
							//=== Search without input keyword ===
							//put the code here ...
							
							//=== Search with input keyword ===
							//put the code here ...
							
							//=== A dumb search simply return all records (for debugging) ===
							new SearchThread(searchBy,jtf.getText()).start();
						}
					});
					jbtnSearch.setToolTipText("Click this button to begin searching the bug database.");
				
					JButton jbtnClearAll = new JButton("Reset");
					jbtnClearAll.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ae) {
							jcb.setSelectedIndex(0);
							jtf.setText("");
							jta.setText("");
							jlbSearchResult.setText("<html><font color='#FF6600'>Total Found: 0</font></html>");
							jlbStatusMsg.setText("<html><font color='#FF6600'>System reset invoked.</font></html>");
							
							//remove the previous button's listener
							ActionListener [] al;
							al = jbtnNextRec.getActionListeners();
							for (int i=0; i<al.length; i++) {
								jbtnNextRec.removeActionListener(al [i]);
							}
							al = jbtnBackRec.getActionListeners();
							for (int i=0; i<al.length; i++) {
								jbtnBackRec.removeActionListener(al [i]);
							}
							
							jbtnNextRec.setEnabled(false);
							jbtnBackRec.setEnabled(false);
							
							System.gc();
						}
					});
					jbtnClearAll.setToolTipText("Click this button to reset and clear the previous search results.");
				
				jpTop_2.add(new JLabel("<html><font color='#FF0066'>Input search value :</font></html>"));
				jpTop_2.add(jtf);
				jpTop_2.add(jbtnSearch);
				jpTop_2.add(jbtnClearAll);
			
			jpTop.add(jpTop_1);
			jpTop.add(jpTop_2);
			
			jpRecNav = new JPanel();
			jpRecNav.setLayout(new BorderLayout());
			jpRecNav.setBackground(Color.WHITE);
			
				JPanel jp_ = new JPanel();
				jp_.setLayout(new FlowLayout(FlowLayout.LEFT));
				jp_.setBackground(Color.WHITE);
				
					jlbSearchResult = new JLabel("<html><font color='#FF6600'>Total Found: 0</font></html>");
					
					jbtnNextRec = new JButton(" Next ");
					jbtnNextRec.setToolTipText("Click this button to load the next record.");
					jbtnNextRec.setEnabled(false);
					
					jbtnBackRec = new JButton(" Back ");
					jbtnBackRec.setToolTipText("Click this button to load the previous record");
					jbtnBackRec.setEnabled(false);
					
				jp_.add(jlbSearchResult);
				jp_.add(jbtnNextRec);
				jp_.add(jbtnBackRec);
				
				jsp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				jsp.setBackground(Color.WHITE);
				
					jta = new JTextArea();
					jta.setForeground(Color.GRAY.darker());
					jta.setEditable(false);
					jta.setLineWrap(true);
					jta.setWrapStyleWord(true);
				
				jsp.getViewport().add(jta);
		        jsp.setBorder(
		                BorderFactory.createCompoundBorder(
		                                BorderFactory.createTitledBorder("Search Result"),
		                                BorderFactory.createEmptyBorder(5,5,5,5)
						)
				);
				jsp.setMinimumSize(new Dimension(250,200));
				jsp.setPreferredSize(new Dimension(250,200));
			
			jpRecNav.add(jp_, BorderLayout.NORTH);
			jpRecNav.add(jsp, BorderLayout.CENTER);
		
			jpStatus = new JPanel();
			jpStatus.setBackground(Color.WHITE);
			jpStatus.setLayout(new FlowLayout(FlowLayout.LEFT));
			
				jlbStatusMsg = new JLabel("<html><font color='#FF6600'>Ready.</font></html>");
			
			jpStatus.add(jlbStatusMsg);
		
		jpMain.add(jpTop, BorderLayout.NORTH);
		jpMain.add(jpRecNav, BorderLayout.CENTER);
		jpMain.add(jpStatus, BorderLayout.SOUTH);
		
		getContentPane().add(jpMain, BorderLayout.CENTER);
		jap = this;
	}
	
	
	public class SearchThread extends Thread {
		private Vector vData = new Vector();
		
		private long searchStartTime;
		private long searchFinishTime;
		
		private int searchBy;
		private String searchKey;
		
		boolean searchFinish = false;
		
		//used as pointer referencing the current position of the data in the container
		private int currPos = 0;
		
		
		public SearchThread(int searchBy, String searchKey) {
			this.searchBy = searchBy;
			this.searchKey = searchKey;
			
			new SearchProgress(this).start();
			//System.out.println("Search By: " + searchBy); //Debugger
		}
		
		
		public void run() {
			//Start the timer
			searchStartTime = System.currentTimeMillis();
			jlbStatusMsg.setText("<html><font color='#FF6600'>Search in progress...</font></html>");
			
			final Object [][] data;
			final int dataRow;
			final int dataCol;
			
			//disable the button
			jbtnNextRec.setEnabled(false);
			jbtnBackRec.setEnabled(false);
			
			//====== Get the information of all bugs ======
			BugReporter_XMLParser xmlparser = new BugReporter_XMLParser();
			if (xmlparser.isConnected() == true) {
				data = xmlparser.loadRecord();
				dataRow = xmlparser.getRow();
				dataCol = xmlparser.getCol();
				
				for (int i=0; i<data.length; i++) {
					if (searchBy==-1 || searchKey.equals("")) {
						//the default search criteria or nothing is input in the search string field
						vData.addElement(data [i]);
						
					} else {
						if (searchBy == BugReporter.byID) {
							if (data [i][BugReporter.byID].toString().toLowerCase().indexOf(searchKey.toLowerCase()) >= 0)
							vData.addElement(data [i]);
						} else if (searchBy == BugReporter.byCategory) {
							if (data [i][BugReporter.byCategory].toString().toLowerCase().indexOf(searchKey.toLowerCase()) >= 0)
							vData.addElement(data [i]);
						} else if (searchBy == BugReporter.byReleaseDate) {
							if (data [i][BugReporter.byReleaseDate].toString().toLowerCase().indexOf(searchKey.toLowerCase()) >= 0)
							vData.addElement(data [i]);
						} else if (searchBy == BugReporter.bySynopsisKey) {
							if (data [i][BugReporter.bySynopsisKey].toString().toLowerCase().indexOf(searchKey.toLowerCase()) >= 0)
							vData.addElement(data [i]);
						} else if (searchBy == BugReporter.byDetailKey) {
							if (data [i][BugReporter.byDetailKey].toString().toLowerCase().indexOf(searchKey.toLowerCase()) >= 0)
							vData.addElement(data [i]);
						} else if (searchBy == BugReporter.byStatus) {
							if (data [i][BugReporter.byStatus].toString().toLowerCase().indexOf(searchKey.toLowerCase()) >= 0)
							vData.addElement(data [i]);
						} else if (searchBy == BugReporter.bySeverity) {
							if (data [i][BugReporter.bySeverity].toString().toLowerCase().indexOf(searchKey.toLowerCase()) >= 0)
							vData.addElement(data [i]);
						}
					}
				}
			}
			
			//Mark the search as finished
			searchFinish = true;
			
			if (vData.size() > 0) {
				//enable the button
				jbtnNextRec.setEnabled(true);
				jbtnBackRec.setEnabled(true);
				
				displayRecord();
				
			} else {
				searchFinishTime = System.currentTimeMillis();
				
				//Nothing is found or nothing match the search
				jta.setText(""); //Clear the JTextArea

				jta.append("No records found.\n");
				jta.append("\n\n");
								
				jlbSearchResult.setText("<html><font color='#FF6600'>Total Record(s) Found: " + vData.size() + "</font></html>");
				
				long elapsedTime = searchFinishTime - searchStartTime;
				
				if (xmlparser.isConnected() == true) {
					jlbStatusMsg.setText("<html><font color='#FF6600'>Search completed in " + elapsedTime + " milli-sec.</font></html>");
				} else {
					jlbStatusMsg.setText("<html><font color='red'>Fail to connect database!</font></html>");
				}
			}
		}
		
		
		private void displayRecord() {
			//====== Display the bug into the JPanel ======
			Object [] currRec = (Object [])vData.elementAt(currPos);
			searchFinishTime = System.currentTimeMillis();
			
			//remove the previous button's listener
			ActionListener [] al;
			al = jbtnNextRec.getActionListeners();
			for (int i=0; i<al.length; i++) {
				jbtnNextRec.removeActionListener(al [i]);
			}
			al = jbtnBackRec.getActionListeners();
			for (int i=0; i<al.length; i++) {
				jbtnBackRec.removeActionListener(al [i]);
			}
			
			String bugID = currRec [0].toString();
			String synopsis = currRec [1].toString();
			String category = currRec [2].toString();
			String date = currRec [3].toString();
			String status = currRec [4].toString();
			String relatedBug = currRec [5].toString();
			String severity = currRec [6].toString();
			String affectedVer = currRec [7].toString();
			String detail = currRec [8].toString();
			String solution = currRec [9].toString();
			
			jta.setText(""); //Clear the JTextArea
			
			jta.append("[ Bug ID ]\n");
			jta.append(bugID);
			jta.append("\n\n");
			
			jta.append("[ Synopsis ]\n");
			jta.append(synopsis);
			jta.append("\n\n");
			
			jta.append("[ Category ]\n");
			jta.append(category);
			jta.append("\n\n");
			
			jta.append("[ Submit Date ]\n");
			jta.append(date);
			jta.append("\n\n");
			
			jta.append("[ Status ]\n");
			jta.append(status);
			jta.append("\n\n");
			
			jta.append("[ Related Bugs ]\n");
			jta.append(relatedBug);
			jta.append("\n\n");
			
			jta.append("[ Severity ]\n");
			jta.append(severity);
			jta.append("\n\n");
			
			jta.append("[ Affected RMIAdmin Version ]\n");
			jta.append(affectedVer);
			jta.append("\n\n");
			
			jta.append("[ Bugs Detail ]\n");
			jta.append(detail);
			jta.append("\n\n");
			
			jta.append("[ Work Around ]\n");
			jta.append(solution);
			jta.append("\n\n");
			
			jlbSearchResult.setText("<html><font color='#FF6600'>Total Record(s) Found: " + vData.size() + "</font></html>");
			
			long elapsedTime = searchFinishTime - searchStartTime;
			jlbStatusMsg.setText("<html><font color='#FF6600'>Search completed in " + elapsedTime + " milli-sec. &nbsp;" + 
								"Current record " + (currPos + 1) + " of " + vData.size() + "." + 
								"</font></html>");
			
			jbtnNextRec.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (currPos < vData.size()-1) {
						currPos += 1;
						
						searchStartTime = System.currentTimeMillis();
						displayRecord();
					} else {
						JOptionPane.showMessageDialog(null, "This is the last record!", "alert", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			
			jbtnBackRec.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					if (currPos > 0) {
						currPos -= 1;
						
						searchStartTime = System.currentTimeMillis();
						displayRecord();
					} else {
						JOptionPane.showMessageDialog(null, "This is the first record!", "Alert", JOptionPane.ERROR_MESSAGE); 
					}
				}
			});
			
			jta.setCaretPosition(0);
			jsp.revalidate(); //not neccessary but just let it exist
			jsp.updateUI(); //not neccessary but just let it exist
			//=============================================
		}
		
		public boolean isFinished() {
			return searchFinish;
		}
	}
	
	
	public class SearchProgress extends Thread {
		SearchThread st;
		boolean finish = false;
		
		
		public SearchProgress(SearchThread st) {
			this.st = st;
		}
		
		
		public void run() {
			try {
				JFrame jpProgress = new JFrame("Search in progress...");
				jpProgress.setResizable(false);
				jpProgress.setBackground(Color.WHITE);
				jpProgress.getContentPane().setLayout(new BorderLayout());
				
					JProgressBar jpb = new JProgressBar();
					jpb.setBackground(Color.WHITE);
					jpb.setMaximum(10);
					jpb.setBorderPainted(true);
					jpb.setIndeterminate(true);
				
				jpProgress.getContentPane().add(jpb, BorderLayout.CENTER);
				jpProgress.setSize(250,80);
				
				//Calulation the position of JFrame to appear in absolute middle of application
				Point p = jap.getLocationOnScreen();
				int x = 0;
				int y = 120;
				x = jap.getWidth()/2 - jpProgress.getWidth()/2;
				x += p.x;
				y += p.y;
				jpProgress.setLocation(x,y);
				
				jpProgress.setVisible(true);
				
				int i = 1;
				while (st.isFinished() == false) {
					jpb.setValue(i);
					
					if (i >= jpb.getMaximum()) {
							i = 1;
					} else {
						i++;
					}
					
					sleep(10);
				}
				
				jpProgress.setVisible(false);
				jpProgress.dispose();
								
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}
}