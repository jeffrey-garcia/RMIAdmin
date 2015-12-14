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
import java.net.*;


public class BugReporter_XMLParser {
	private DOMParser parser01;
	private DocumentBuilder parser02;
	private DocumentBuilderFactory factory;
	
	private Document doc01;
	private Document doc02;
	
	private NodeList n;
	private String xmlFile;
	
	private int row;
	private int col;
	
	private int currRecPtr = 0;
	
	private boolean connected = false;
	
	
	public BugReporter_XMLParser()  {
		xmlFile = "http://127.0.0.1/bugsReport.xml";
		
		try {
			xmlFile = new String(xmlFile);
			
			parser01 = new DOMParser();
			parser01.parse(xmlFile);
			doc01 = parser01.getDocument();
			
			factory = DocumentBuilderFactory.newInstance();
			parser02 = factory.newDocumentBuilder();
			doc02 = parser02.parse(xmlFile);
			
			connected = true;
			
		} catch (Exception exc) {
			exc.printStackTrace();
			connected = false;
		}
	}
	
	
	public boolean isConnected() {
		return connected;
	}
	
	
	public Object [][] loadRecord() {
		Object record [][] = null;
		row = 0;
		col = 0;
		
		try {
			//====== This part get all the data in XML ======
			NodeList nlID;
			NodeList nlSynopsis;
			NodeList nlCategory;
			NodeList nlDate;
			NodeList nlStatus;
			NodeList nlRelatedBug;
			NodeList nlSeverity;
			NodeList nlAffectedVer;
			NodeList nlDetail;
			NodeList nlSolution;
			
			nlID = doc01.getElementsByTagName("id");
			String[] id = new String[nlID.getLength()];
			nlSynopsis = doc01.getElementsByTagName("synopsis");
			String[] synopsis = new String[nlSynopsis.getLength()];
			nlCategory = doc01.getElementsByTagName("category");
			String[] category = new String[nlCategory.getLength()];
			nlDate = doc01.getElementsByTagName("date");
			String[] date = new String[nlDate.getLength()];
			nlStatus = doc01.getElementsByTagName("status");
			String[] status = new String[nlStatus.getLength()];
			nlRelatedBug = doc01.getElementsByTagName("relatedBug");
			String[] relatedBug = new String[nlRelatedBug.getLength()];
			nlSeverity = doc01.getElementsByTagName("severity");
			String[] severity = new String[nlSeverity.getLength()];
			nlAffectedVer = doc01.getElementsByTagName("affectedVer");
			String[] affectedVer = new String[nlAffectedVer.getLength()];
			nlDetail = doc01.getElementsByTagName("detail");
			String[] detail = new String[nlDetail.getLength()];
			nlSolution = doc01.getElementsByTagName("solution");
			String[] solution = new String[nlSolution.getLength()];
			
			row = nlID.getLength();
			col = 10;
			
			for (int i=0; i<nlID.getLength(); i++) {
				id [i] = nlID.item(i).getFirstChild().getNodeValue();
				synopsis [i] = nlSynopsis.item(i).getFirstChild().getNodeValue();
				category [i] = nlCategory.item(i).getFirstChild().getNodeValue();
				date [i] = nlDate.item(i).getFirstChild().getNodeValue();
				status [i] = nlStatus.item(i).getFirstChild().getNodeValue();
				relatedBug [i] = nlRelatedBug.item(i).getFirstChild().getNodeValue();
				severity [i] = nlSeverity.item(i).getFirstChild().getNodeValue();
				affectedVer [i] = nlAffectedVer.item(i).getFirstChild().getNodeValue();
				detail [i] = nlDetail.item(i).getFirstChild().getNodeValue();
				solution [i] = nlSolution.item(i).getFirstChild().getNodeValue();
			}
			//===============================================
			
			//====== This part converted the data in 2 dimensional object array ======
			//System.out.println("Load Bugs Report...");
			//System.out.println("=============================================");
			
			record = new Object [row][col];
			
			for (int i=0; i<row; i++) {
				for (int j=0; j<col; j++) {	
					if (j==0) {
						record [i][j] = id [i];
						
					} else if (j==1) {
						record [i][j] = synopsis [i];
						
					} else if (j==2) {
						record [i][j] = category [i];
						
					} else if (j==3) {
						record [i][j] = date [i];
						
					} else if (j==4) {
						record [i][j] = status [i];
						
					} else if (j==5) {
						record [i][j] = relatedBug [i];
						
					} else if (j==6) {
						record [i][j] = severity [i];
						
					} else if (j==7) {
						record [i][j] = affectedVer [i];
						
					} else if (j==8) {
						record [i][j] = detail [i];
						
					} else if (j==9) {
						record [i][j] = solution [i];
					}
					
					//System.out.println(record [i][j]);
				}
				
				//System.out.println("=============================================");
			}
			
			//System.out.println("Done.");
			//========================================================================
			
		} catch (Exception exc) {
			StackTraceElement[] error = exc.getStackTrace();
			exc.printStackTrace();
		}
		
		return record;
	}
	
	
	public void addRecord(String id,String synopsis, String category, String date, String status,String relatedBug,String severity,String affectedVer,String detail,String solution) {
		try {
			Node bug = getNode(doc02, "bug");
			Node bugInfoNode = createNode(doc02, "bugInfo", "");
			bug.appendChild(bugInfoNode);
			
			Node idNode = createNode(doc02, "id", id);
			Node synopsisNode = createNode(doc02, "synopsis", synopsis);
			Node categoryNode = createNode(doc02, "category", category);
			Node dateNode = createNode(doc02, "date", date);
			Node statusNode = createNode(doc02, "status", status);
			Node relatedBugNode = createNode(doc02, "relatedBug", relatedBug);
			Node severityNode = createNode(doc02, "severity", severity);
			Node affectedVerNode = createNode(doc02, "affectedVer", affectedVer);
			Node detailNode = createNode(doc02, "detail", detail);
			Node solutionNode = createNode(doc02, "solution", solution);
			
			bugInfoNode.appendChild(idNode);
			bugInfoNode.appendChild(synopsisNode);
			bugInfoNode.appendChild(categoryNode);
			bugInfoNode.appendChild(dateNode);
			bugInfoNode.appendChild(statusNode);
			bugInfoNode.appendChild(relatedBugNode);
			bugInfoNode.appendChild(severityNode);
			bugInfoNode.appendChild(affectedVerNode);
			bugInfoNode.appendChild(detailNode);
			bugInfoNode.appendChild(solutionNode);
			
			saveDoc(doc02);
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	
	public int getRow() {
		return row;
	}
	
	
	public int getCol() {
		return col;
	}
	
	
	//Factory method
	private Node createNode(Document doc, String name, String value) {
		Element e = doc.createElement(name);
		e.appendChild(doc.createTextNode(value));
		
		return e;
	}
	
	
	//Factory method
	private Node getNode(Document doc, String nodeName) {
		NodeList nl = doc.getElementsByTagName(nodeName);
		
		if (nl!=null && nl.getLength()>0) {
			return nl.item(0);
		} else {
			return null;
		}
	}
	
	
	//Factory method
	private Node getNode(Document doc, String nodeName, int recNo) {
		NodeList nl = doc.getElementsByTagName(nodeName);
		
		if (nl!=null && nl.getLength()>0) {
			return nl.item(recNo);
		} else {
			return null;
		}
	}
	
	
	//Factory method
	private void saveDoc(Document doc) {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File (xmlFile));
			/*
			transformer.transform(source, result);
	   		*/
	   		System.out.println(xmlFile + " saved successfully.");
	   		
	   	} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}