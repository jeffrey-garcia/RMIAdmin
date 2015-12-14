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
import java.util.*;


public class RMIClient_XMLParser_Item {
	private DOMParser parser01;
	private DocumentBuilder parser02;
	private DocumentBuilderFactory factory;
	
	private Document doc01;
	private Document doc02;
	
	private NodeList n;
	private String xmlFile;
	
	private RMIClientGUI_Debugger rmiDebug;
	
	
	public RMIClient_XMLParser_Item(RMIClientGUI_Debugger rmiDebug) {
		this.rmiDebug = rmiDebug;
		xmlFile = "RMIServer.xml";
		
		try {
			xmlFile = new String(xmlFile);
			
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
	
	
	public void addItem(int recNo, String hostname, String ip, String port, String os, String shell, String desc, Vector vBatch) {
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
			
			node = getNode(doc02, "batch", recNo);
			newNode = createNode(doc02, "batch", "");
				for (int i=0; i<vBatch.size(); i++) {
					String tmp = vBatch.elementAt(i) + "";
					
					Node n = createNode(doc02, "detail", tmp);
					newNode.appendChild(n);
				}
			replaceNode(node, newNode);
			
			saveDoc(doc02);
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
	}
	
	
	public Vector loadItem(int recNo) {
		Vector vComBatch = new Vector();
		
		try {
			//====== This part get the item data from XML ======
			NodeList nlHostname;
			NodeList nlBatch;
			
			nlHostname = doc01.getElementsByTagName("hostname");
			nlBatch = doc01.getElementsByTagName("batch");
			
			for (int i=0; i<nlHostname.getLength(); i++) {
				NodeList nlBatchDetail = nlBatch.item(i).getChildNodes();
				
				if (i == recNo) {
					for (int j=0; j<nlBatchDetail.getLength(); j++) {
						String info = nlBatchDetail.item(j).getFirstChild().getNodeValue() + "";
						vComBatch.addElement(info);
					}
				}
			}
			//==================================================
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			rmiDebug.setDebugText(error);
		}
		
		rmiDebug.setDebugText("Loaded Batch Config: " + vComBatch);
		return vComBatch;
	}
	
	
	//--- Factory Method ---
	private Node getNode(Document doc, String nodeName) {
		NodeList nl = doc.getElementsByTagName(nodeName);
		
		if (nl!=null && nl.getLength()>0) {
			return nl.item(0);
		} else {
			return null;
		}
	}
	
	
	//--- Factory Method ---
	private Node getNode(Document doc, String nodeName, int recNo) {
		NodeList nl = doc.getElementsByTagName(nodeName);
		
		if (nl!=null && nl.getLength()>0) {
			return nl.item(recNo);
		} else {
			return null;
		}
	}
	
	
	//--- Factory Method ---
	private Node createNode(Document doc, String name, String value) {
		Element e = doc.createElement(name);
		e.appendChild(doc.createTextNode(value));
		
		return e;
	}
	
	
	//--- Factory Method ---
	private void replaceNode(Node node, Node newNode) {
		Node parent = node.getParentNode();
		parent.replaceChild(newNode,node);
	}
	
	
	//--- Factory Method ---
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