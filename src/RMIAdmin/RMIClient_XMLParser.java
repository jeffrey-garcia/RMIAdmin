package RMIAdmin;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// used for parsing
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

// used for printing
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;

import java.io.*;


public class RMIClient_XMLParser {
	private DOMParser parser01;
	private DocumentBuilder parser02;
	private DocumentBuilderFactory factory;
	
	private Document doc01;
	private Document doc02;
	
	private NodeList n;
	private String xmlFile;
	
	private RMIClientGUI rmiGUI;
	private RMIClientGUI_Debugger rmiDebug;
	
	private int row;
	private int col;
	
	
	public RMIClient_XMLParser(RMIClientGUI_Debugger rmiDebug) {
		this.rmiGUI = (RMIClientGUI)rmiDebug.getTopLevelAncestor();
		this.rmiDebug = rmiDebug;
		xmlFile = "RMIServer.xml";
		
		try {
			xmlFile = new String(xmlFile);
			
			//Test the existence of XML File
			testFileExist();
			
			parser01 = new DOMParser();
			parser01.parse(xmlFile);
			doc01 = parser01.getDocument();
			
			factory = DocumentBuilderFactory.newInstance();
			parser02 = factory.newDocumentBuilder();
			doc02 = parser02.parse(xmlFile);
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	private void testFileExist() {
		try {
			File f = new File (xmlFile);
			
			if (f.exists() == false) {
				//XML file does not exist, we'll now create one.
				rmiDebug.setDebugText(xmlFile + " does not exist, an empty server repository will now be generated.");
				
				FileWriter fw = new FileWriter(f, false);
				PrintWriter pw = new PrintWriter(fw);
				
				pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				pw.println("<server/>");
				
				pw.close();
				fw.close();
				
				rmiDebug.setDebugText(xmlFile + " is successfully generated.");
			}
		
		} catch (java.security.AccessControlException ace) {
			StackTraceElement[] error = ace.getStackTrace();
			rmiDebug.setDebugText(error);
			
			rmiGUI.warning("Missing .java.policy in HOME folder.\n\n" + 
								"For Windows: C:\\Document and Settings\\<i>Your Windows Logon Name</i>\n" + 
								"For Linux: /home/<i>Your Logon Name</i>\n\n" + 
								"Please download the file from <u>http://www.rmiAdmin.net/deploy/.java.policy</u>\n\n"
							); //Debugger
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	public void addRecord(String hostname, String ip, String port, String os, String shell, String desc) {
		try {
			Node server = getNode(doc02, "server");
			Node serverInfoNode = createNode(doc02, "serverInfo", "");
			server.appendChild(serverInfoNode);
			
			Node hostnameNode = createNode(doc02, "hostname", hostname);
			Node ipNode = createNode(doc02, "ip", ip );
			Node portNode = createNode(doc02, "port", port);
			Node osNode = createNode(doc02, "os", os);
			Node shellNode = createNode(doc02, "shell", shell);
			Node descNode = createNode(doc02, "desc", desc);
			Node batchNode = createNode(doc02, "batch", "");
			
			serverInfoNode.appendChild(hostnameNode);
			serverInfoNode.appendChild(ipNode);
			serverInfoNode.appendChild(portNode);
			serverInfoNode.appendChild(descNode);
			serverInfoNode.appendChild(osNode);
			serverInfoNode.appendChild(shellNode);
			serverInfoNode.appendChild(batchNode);
			
			saveDoc(doc02);
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	public void updateRecord(int recNo, String hostname, String ip, String port, String os, String shell, String desc) {
		Node node;
		Node newNode;
		
		try {
			node = getNode(doc02, "hostname", recNo);
			newNode = createNode(doc02, "hostname", hostname);
			replaceNode(node, newNode);
			
			node = getNode(doc02, "ip", recNo);
			newNode = createNode(doc02, "ip", ip);
			replaceNode(node, newNode);
			
			node = getNode(doc02, "port", recNo);
			newNode = createNode(doc02, "port", port);
			replaceNode(node, newNode);
			
			node = getNode(doc02, "os", recNo);
			newNode = createNode(doc02, "os", os);
			replaceNode(node, newNode);
			
			node = getNode(doc02, "shell", recNo);
			newNode = createNode(doc02, "shell", shell);
			replaceNode(node, newNode);
			
			node = getNode(doc02, "desc", recNo);
			newNode = createNode(doc02, "desc", desc);
			replaceNode(node, newNode);
			
			saveDoc(doc02);
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	public void removeRecord(int recNo) {
		Node node;
		
		try {
			node = getNode(doc02, "serverInfo", recNo);
			removeNode(node);
			
			saveDoc(doc02);
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	public Object [][] loadRecord() {
		Object record [][] = null;
		row = 0;
		col = 0;
		
		try {
			//====== This part get all the data in XML ======
			NodeList nlHostname;
			NodeList nlIP;
			NodeList nlPort;
			NodeList nlOS;
			NodeList nlShell;
			NodeList nlDesc;
			
			nlHostname = doc01.getElementsByTagName("hostname");
			String[] hostname = new String[nlHostname.getLength()];
			nlIP = doc01.getElementsByTagName("ip");
			String[] ip = new String[nlIP.getLength()];
			nlPort = doc01.getElementsByTagName("port");
			String[] port = new String[nlPort.getLength()];
			nlOS = doc01.getElementsByTagName("os");
			String[] os = new String[nlOS.getLength()];
			nlShell = doc01.getElementsByTagName("shell");
			String[] shell = new String[nlShell.getLength()];
			nlDesc = doc01.getElementsByTagName("desc");
			String[] desc = new String[nlDesc.getLength()];
			
			row = nlHostname.getLength();
			col = 6;
			
			for (int i=0; i<nlHostname.getLength(); i++) {
				hostname [i] = nlHostname.item(i).getFirstChild().getNodeValue();
				ip [i] = nlIP.item(i).getFirstChild().getNodeValue();
				port [i] = nlPort.item(i).getFirstChild().getNodeValue();
				os [i] = nlOS.item(i).getFirstChild().getNodeValue();
				shell [i] = nlShell.item(i).getFirstChild().getNodeValue();
				desc [i] = nlDesc.item(i).getFirstChild().getNodeValue();
			}
			//===============================================
			
			//====== This part converted the data in 2 dimensional object array ======
			rmiDebug.setDebugText("Load Server List...");
			
			record = new Object [row][col];
			
			for (int i=0; i<row; i++) {
				for (int j=0; j<col; j++) {	
					if (j==0) {
						record [i][j] = hostname [i];
						rmiDebug.setDebugText(hostname [i]);
					}
					if (j==1) {
						record [i][j] = ip [i];
						rmiDebug.setDebugText(ip [i]);
					}
					if (j==2) {
						record [i][j] = port [i];
						rmiDebug.setDebugText(port [i]);
						}
					if (j==3) {
						record [i][j] = os [i];
						rmiDebug.setDebugText(os [i]);
					}
					if (j==4) {
						record [i][j] = shell [i];
						rmiDebug.setDebugText(shell [i]);
					}
					if (j==5) {
						record [i][j] = desc [i];
						rmiDebug.setDebugText(desc [i]);
					}
				}
				rmiDebug.setDebugText("=============================================");
			}
			
			rmiDebug.setDebugText("Done.");
			//========================================================================
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
		
		return record;
	}
	
	
	public int getRow() {
		return row;
	}
	
	
	public int getCol() {
		return col;
	}
	
	
	private Node getNode(Document doc, String nodeName) {
		NodeList nl = doc.getElementsByTagName(nodeName);
		
		if (nl!=null && nl.getLength()>0) {
			return nl.item(0);
		} else {
			return null;
		}
	}
	
	
	private Node getNode(Document doc, String nodeName, int recNo) {
		NodeList nl = doc.getElementsByTagName(nodeName);
		
		if (nl!=null && nl.getLength()>0) {
			return nl.item(recNo);
		} else {
			return null;
		}
	}
	
	
	private Node createNode(Document doc, String name, String value) {
		Element e = doc.createElement(name);
		e.appendChild(doc.createTextNode(value));
		
		return e;
	}
	
	
	private void replaceNode(Node node, Node newNode) {
		Node parent = node.getParentNode();
		parent.replaceChild(newNode,node);
	}
	
	
	private void removeNode(Node node) {
		Node parentNode = node.getParentNode();
		parentNode.removeChild(node);
	}
	
	
	private void saveDoc(Document doc) {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File (xmlFile));
			
			transformer.transform(source, result);
	   		
	   	} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
}