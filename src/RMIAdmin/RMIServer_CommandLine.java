package RMIAdmin;


import java.io.*;
import java.util.*;


public class RMIServer_CommandLine {
	
	
	public RMIServer_CommandLine() {
		//Nothing to do in constructor...
	}
	
	
	public String getOS() {
		String os = System.getProperty("os.name") + "," + System.getProperty("os.version");
		return os;
	}	
	
	
	public String setCommand(String shell, String command) {
		String commandLine [];
		
		if (getOS().indexOf("Windows") >= 0) {
			commandLine = new String [] {shell, "/C", command};
		} else {
			commandLine = new String [] {shell, "-c", command};
		}
		
		commandThread cmdT = new commandThread();
		cmdT.setCMD(commandLine);
		cmdT.start();
		
		return cmdT.getName();
	}
	
	
	public void runCommand(String threadName) {
		try {
            //=== Check all threads status, make sure the Thread is interrupted ===
            boolean found = true;
			Thread [] threads = new Thread [Thread.activeCount()];
			int enumerated = Thread.enumerate(threads);
			
			while (enumerated > 0) {
				threads = new Thread [Thread.activeCount()];
				enumerated = Thread.enumerate(threads);
				
				System.out.println("No. of Thread = " + enumerated); //Debugger
				System.out.println("-----------------------------");
				
				for (int i=0; i<enumerated; i++) {
					System.out.println(threads [i].getName()); //Debugger
					
					if (threads [i].getName().equals(threadName)) {
						found = true;
						((commandThread)threads [i]).start = true;
						break;
					}
				}
				
				System.out.println("-----------------------------");
				System.out.println();
				
				if (found == true) {
					break;
				}
				
				//wait for 1 seconds, then re-check
				Thread.sleep(1000);
			}
			//=====================================================================
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public void cancelCommand(String threadName) {
		commandThread cmdT = null;
		
		try {
            //=== Check all threads status, make sure the Thread is interrupted ===
            boolean found = true;
			Thread [] threads = new Thread [Thread.activeCount()];
			int enumerated = Thread.enumerate(threads);
			
			while (enumerated > 0) {
				threads = new Thread [Thread.activeCount()];
				enumerated = Thread.enumerate(threads);
				
				System.out.println("* No. of Thread = " + enumerated); //Debugger
				System.out.println("* -----------------------------");
				
				for (int i=0; i<enumerated; i++) {
					System.out.println("* " + threads [i].getName()); //Debugger
					
					if (threads [i].getName().equals(threadName)) {
						found = true;
						//threads [i].interrupt();
						cmdT = (commandThread)threads [i];
						cmdT.pause("terminate");
						break;
					}
				}
				
				System.out.println("* -----------------------------");
				System.out.println();
				
				if (found == true) {
					break;
				}
				
				//wait for 1 seconds, then re-check
				Thread.sleep(1000);
			}
			//=====================================================================
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
	}
	
	
	public boolean checkCommandStatus(String threadName) {
		commandThread cmdT = null;
		boolean completed = false;
		
		try {
            //=== Check all threads status, verify if the command thread execution is complete ===
			Thread [] threads = new Thread [Thread.activeCount()];
			int enumerated = Thread.enumerate(threads);
			
			while (enumerated > 0) {
				threads = new Thread [Thread.activeCount()];
				enumerated = Thread.enumerate(threads);
				
				System.out.println("* No. of Thread = " + enumerated); //Debugger
				System.out.println("* -----------------------------");
				
				for (int i=0; i<enumerated; i++) {
					System.out.println("* " + threads [i].getName()); //Debugger
					
					if (threads [i].getName().equals(threadName)) {
						cmdT = (commandThread)threads [i];
						
						if (cmdT.finish == true) {
							completed = true;
							break;
						} else {
							completed = false;
							break;
						}
					}
				}
				
				System.out.println("* -----------------------------");
				System.out.println();
				
				if (cmdT == null) {
					completed = true;
				}
				
				break; //Check once only then quit the loop
			}
			//====================================================================================
			
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		return completed;
	}
	
	
	public LinkedList getCommandResult(String threadName) {
		commandThread cmdT = null;
		LinkedList llOutput = new LinkedList();
		
		try {
            //=== Check all threads status, verify if the command thread execution is complete ===
			Thread [] threads = new Thread [Thread.activeCount()];
			int enumerated = Thread.enumerate(threads);
			
			while (enumerated > 0) {
				threads = new Thread [Thread.activeCount()];
				enumerated = Thread.enumerate(threads);
				
				System.out.println("* No. of Thread = " + enumerated); //Debugger
				System.out.println("* -----------------------------");
				
				for (int i=0; i<enumerated; i++) {
					System.out.println("* " + threads [i].getName()); //Debugger
					
					if (threads [i].getName().equals(threadName)) {
						cmdT = (commandThread)threads [i];
						
						//=== Check if the command thread has been paused then reset it in running state ===
						if (cmdT.interruptType.equals("wait") == true) {
							cmdT.interruptType = "";
							
							/* Let the command thread to execute 
							 * for 1.5 sec before it is paused again
							 */
							Thread.sleep(1500); 
						}
						//==================================================================================
						
						//=== Get the output no matter thread is finish or not ===
						if (cmdT.finish == true) {
							cmdT.pause("wait");
							
							for (int j=0; j<cmdT.llOutput.size(); j++) {
								llOutput.add(cmdT.llOutput.get(j));
							}
							
							cmdT.llOutput.clear();
							cmdT.quit = true;
							break;
							
						} else {
							cmdT.pause("wait");
							
							for (int j=0; j<cmdT.llOutput.size(); j++) {
								llOutput.add(cmdT.llOutput.get(j));
							}
							
							cmdT.llOutput.clear();
							break;
						}
						//========================================================
					}
				}
				
				System.out.println("* -----------------------------");
				System.out.println();
				
				break; //Check once only then quit the loop
			}
			//====================================================================================
		} catch (Exception exc) {
			exc.printStackTrace();
			trapError(exc);
		}
		
		//System.out.println(llOutput.size()); //Debugger
		return llOutput;
	}
	
	
	public class commandThread extends Thread {
		Runtime R;
		Process child;
		int exitCode = -999;
		String [] commandLine;
		boolean start = false;
		boolean finish = false;
		boolean quit = false;
		LinkedList llOutput = new LinkedList();
		String interruptType = "";
		grepErrorThread geT;
		
		
		public commandThread() {
			this.setPriority(MIN_PRIORITY);
			System.out.println("Priority = " + this.getPriority()); //Debugger
		}
		
		public void setCMD(String [] commandLine) {
			System.out.println("Set Command Line: ");
			
			//for (int i=0; i<commandLine.length; i++) {
				//System.out.print(commandLine [i] + " "); //Debugger
			//}
			//System.out.println();
			
			this.commandLine = commandLine;
		}
		
		public void pause(String type) {
			if (type.equals("terminate") == true) {
				interruptType = type;
				this.interrupt();
			} else if (type.equals("wait") == true) {
				interruptType = type;
			}
		}
		
		public void run() {
			try {
				//loop until confirm to start
				while (start == false) {
					Thread.sleep(1000); //wait for 1 seconds, then re-check
				}
				
				R = Runtime.getRuntime();
				child = R.exec(commandLine);
				
				System.out.println("Command Thread started.");
				
				for (int i=0; i<commandLine.length; i++) {
					System.out.print(commandLine [i] + " "); //Debugger
				}
				System.out.println();
				
				geT = new grepErrorThread(child);
				geT.start();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(child.getInputStream()));
				llOutput = new LinkedList();
				String output = "";
				
				while((output = br.readLine())!=null) {					
					try {
						if (this.isInterrupted() && interruptType.equals("terminate")==true) {
							System.out.println("*** Interruption Occured ***");
							child.destroy();
							
							PrintWriter pw = new PrintWriter(child.getOutputStream());
							pw.println("quit");
							pw.flush();
							pw.close();
							
							break;
							
						} else if (interruptType.equals("wait")==true) {
							System.out.println("*** Thread Waiting ***");
							
							while (interruptType.equals("wait")==true) {
								Thread.sleep(500); //wait for 0.5 seconds, then re-check
							}
						}
						
						//System.out.println(output); //Debugger
						llOutput.add(output);
						
					} catch (InterruptedException ie) {
						System.out.println("*** Interruption Occured ***");
						child.destroy();
						
						PrintWriter pw = new PrintWriter(child.getOutputStream());
						pw.println("quit");
						pw.flush();
						pw.close();
						
						break;
					}
				}
				
				if (this.isInterrupted() == false) {
					child.waitFor();
					exitCode = child.exitValue();
				}
				
				if (exitCode == 0) {
					System.out.println("Exit Code = " + exitCode);
					System.out.println("Process completed successfully.");
				} else if (exitCode != -999) {
					System.out.println("Exit Code = " + exitCode);
					System.out.println("Process completed with error.");
					
					//=== Handle the output from grepErrorThread ===
					//System.out.println(geT.llError.size());
					LinkedList llError = geT.getError();
					llOutput = new LinkedList();
					for (int j=0; j<llError.size(); j++) {
						llOutput.add(llError.get(j));
					}
					//==============================================
				}
				
				geT.quit = true;
				finish = true;
				
				//loop until confirm to quit
				while (quit == false) {
					try {
						System.out.println("Command Thread quit status = " + quit); //Debugger
						Thread.sleep(1000); //wait for 1 seconds, then re-check
						
					} catch (InterruptedException ie) {
						break;
					}
				}
				System.out.println("Command Thread terminated. No. of lines = " + llOutput.size());
				
			} catch (Exception exc) {
				exc.printStackTrace();
				trapError(exc);
			}
		}
		
		public LinkedList getOutput() {
			return llOutput;
		}
		
		public void destroy() {
			interrupt();
			interruptType = "terminate";
			quit = true;
		}
	}
	
	
	public class grepErrorThread extends Thread {
		Process child;
		LinkedList llError = new LinkedList();
		boolean quit = false;
		
		public grepErrorThread(Process child) {
			this.setPriority(MIN_PRIORITY);
			this.child = child;
		}
		
		public void run() {
			try {
				System.out.println("Grep Error Thread started.");
				
	    		BufferedReader br = new BufferedReader(new InputStreamReader(child.getErrorStream()));
				llError = new LinkedList();
				String output = "";
				
				/***************************************
				 * This part can only be ended when 
				 * the child is set to null, otherwise
				 * infinite looping will occur
				 **************************************/
				while((output = br.readLine())!=null) {
					System.out.println(output);
					llError.add(output);
				}
				
				//loop until confirm to quit
				while (quit == false) {
					try {
						System.out.println("Grep Error Thread quit status = " + quit); //Debugger
						Thread.sleep(1000); //wait for 1 seconds, then re-check
						
					} catch (InterruptedException ie) {
						break;
					}
				}
				System.out.println("Grep Error Thread terminated. No of lines = " + llError.size());
				
			} catch (Exception exc) {
				exc.printStackTrace();
				trapError(exc);
			}
		}
		
		public LinkedList getError() {
			return llError;
		}
		
		public void destroy() {
			/************************************
			 * Make the child null such that 
			 * Grep Error Thread could be ended
			 ***********************************/
			child = null;
			quit = true;
		}
	}
	
	
	private void trapError(Exception exc) {
		//Print Exception Message to Logfile
		(new RMIServer_EventHandler(exc.getMessage())).getFile();
		
		//Print Stack Trace to Logfile
		(new RMIServer_EventHandler(exc.getStackTrace())).getFile();
	}
}