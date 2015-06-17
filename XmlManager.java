/*
 * Filename: XmlManager.java
 * 
 * Purpose: Encapsulate read/write functions of bug XML data, 
 * and uses the Bug class to create & manipulate Bug objects. 
 * 
 * References: 
 * 
 * http://www.javacodegeeks.com/2013/05/parsing-xml-using-dom-sax-and-stax-parser-in-java.html
 * http://docs.oracle.com/javase/tutorial/jaxp/xslt/writingDom.html
 * http://stackoverflow.com/questions/729621/convert-string-xml-fragment-to-document-node-in-java
 * 
 * Change Log
 * 
 * 01/31/15	-	file created
 * 02/01/15	-	editField and SaveChanges added
 */
package bugmanager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlManager {

	private final String filepath;
	private Document doc;
	private List<Bug> bugs;
	private int numBugs;

	public XmlManager(String filepath) throws Exception, IOException{
		this.filepath = filepath;
		init();
	}
	
	private void init() throws Exception, IOException{
		//Get the DOM Builder Factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	    //Get the DOM Builder
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    
	    //Check if XML has been created already
	    if(new File(this.filepath).exists())
	    {
	    	//Load and Parse the XML document
		    //document contains the complete XML as a Tree.
		    this.doc = builder.parse(new FileInputStream(this.filepath));
		    
		    //Instantiate List to contain bug objects
		    this.bugs = new ArrayList<>();

		    //Iterating through the nodes and extracting the data.
		    NodeList nodeList = this.doc.getDocumentElement().getElementsByTagName("bug");

		    //How many bugs there are 
		    this.numBugs= nodeList.getLength();
		    
		    //Loop through each node, creating a bug object and adding it to the bug list
		    for (int i = 0; i < this.numBugs; i++) 
		    {
				Node node = nodeList.item(i);	    
				Element element = (Element) node;
				
				//Parse Node for bug attributes				
				final int Id =  Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
				final long TimeAdded = Long.parseLong(GetField(element,"timeadded")); 
				final long TimeLastModified = Long.parseLong(GetField(element,"timelastmodified"));
				final String Title = GetField(element,"title");
				final String BugShortDescription = GetField(element,"bugshortdescription");
				final String BugLongDescription = GetField(element,"buglongdescription");
				short Priorty = Short.parseShort(GetField(element,"priority"));
				final String BugStatus = GetField(element,"status");
				final String UserReportedEmail = GetField(element,"userreportedemail");
				final String UserAssignedEmail = GetField(element,"userassignedemail");
				
				Bug bug = new Bug(Id, TimeAdded, TimeLastModified, Title, BugShortDescription, BugLongDescription, Priorty, BugStatus, UserReportedEmail, UserAssignedEmail);
				this.bugs.add(bug);
		    }
	    }
	    
	    else
	    {
	    	throw new FileNotFoundException(this.filepath);
	    }
	    
	}
	

	public void UpdateBug(Bug bug)
	{
		// update bug list
		// loop through bugs
		for (int i = 0; i < this.numBugs; i++) 
		{
			// if desired bug
			if(this.bugs.get(i).getId() == bug.getId())
			{
				// replace old version of the bug
				this.bugs.set(i, bug);
				return;
			}
		}

	}
	
	// get particular xml field
	public static String GetField(Element e, String FieldName)
	{	
		if(e.getElementsByTagName(FieldName).item(0) != null)
			return e.getElementsByTagName(FieldName).item(0).getTextContent();
		else
			return "";	
	}
	
	// update particular xml field
	/*public void EditBugField(int BugId, String FieldName, String FieldValue)
	{
		// loop through bugs
		for (int i = 0; i < this.numBugs; i++) 
		{
			// if desired bug
			if(this.bugs.get(i).getId() == BugId)
			{
				// use different method depending on field being edited
				switch(FieldName)
				{
				// update title
				case "title":
					this.bugs.get(i).setTitle(FieldValue);
					break;
				}
				return; // stop looping through bugs
			}
		}
		
	}*/
	
	// Create new document, and re-build bug list 
	public void SaveChanges() throws Exception
	{
		// create a new, empty document
	    this.doc = null;
		this.doc = DocumentBuilderFactory.newInstance()
	            .newDocumentBuilder().newDocument();
		Element root = this.doc.createElement("bugs");
		this.doc.appendChild(root);
		
		// loop through list of bug objects
	    for (int i = 0; i < this.numBugs; i++) {
	    	// get current bug.
	    	Bug bug = this.bugs.get(i);
	    	// Create node.
	        Element node = GetElementFromBug(bug);	     
	        // Append the node to document
	        Node importedNode = this.doc.importNode(node, true);	     
	        this.doc.getDocumentElement().appendChild(importedNode);  
	    }
	    // Update File
	    this.WriteFile();
	}
	
	// Determine what the next bug ID will be based on the existing bugs
	public int GetNextID()
	{
		int max = 0;
		int current = 0;
		
		// get the largest ID
		// loop through list of bug objects
	    for (int i = 0; i < this.numBugs; i++) {
	    	current = this.bugs.get(i).getId();
	    	if (current > max)
	    		max = current;
	    }
		
		return current + 1;
	}
	
	// add new Bug to XML & Bug List
	public void AddNewBug(Bug bug) throws SAXException, IOException, ParserConfigurationException, TransformerException
	{
    	// Create node.
        Element node = GetElementFromBug(bug);	     
        // Append the node to document
        Node importedNode = this.doc.importNode(node, true);	     
        this.doc.getDocumentElement().appendChild(importedNode);  	      
        // Update file
        this.WriteFile();
        // Update Bug List
        this.bugs.add(bug);
        // Increase bug count
        this.numBugs += 1;
	}
		
	// Write XML to file
	private void WriteFile() throws TransformerException
	{
	    // Use a Transformer for output
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer();
	
	    // Output to file
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(this.filepath);
	    transformer.transform(source, result);		
	}
	
	public List<Bug> getBugList()
	{
		return this.bugs;
	}
	
	public int getNumBugs()
	{
		return this.numBugs;
	}


	// Creates a Bug off of an XML node
	public static Bug GetBugFromNode(Node bugNode) throws EmailException
	{
		Element element = (Element) bugNode;
		
		//Parse Node for bug attributes				
		final int Id =  Integer.parseInt(bugNode.getAttributes().getNamedItem("id").getNodeValue());
		final long TimeAdded = Long.parseLong(GetField(element,"timeadded")); 
		final long TimeLastModified = Long.parseLong(GetField(element,"timelastmodified"));
		final String Title = GetField(element,"title");
		final String BugShortDescription = GetField(element,"bugshortdescription");
		final String BugLongDescription = GetField(element,"buglongdescription");
		short Priorty = Short.parseShort(GetField(element,"priority"));
		final String BugStatus = GetField(element,"status");
		final String UserReportedEmail = GetField(element,"userreportedemail");
		final String UserAssignedEmail = GetField(element,"userassignedemail");
		
		return new Bug(Id, TimeAdded, TimeLastModified, Title, BugShortDescription, BugLongDescription, Priorty, BugStatus, UserReportedEmail, UserAssignedEmail);
				
	}
	
	// Creates an XML Element off a Bug object
	public static Element GetElementFromBug(Bug bug) throws SAXException, IOException, ParserConfigurationException
	{
		// Create XML string for Bug Object
		String BugXml = "\n<bug id=\""+bug.getId()+"\">";
        BugXml += "\n\t<title>"+bug.getTitle()+"</title>";
        BugXml += "\n\t<timeadded>"+bug.getTimeAdded()+"</timeadded>";
        BugXml += "\n\t<timelastmodified>"+bug.getTimeLastModifed()+"</timelastmodified>";
        BugXml += "\n\t<shortdescription>"+bug.getShortDescription()+"</shortdescription>";
        BugXml += "\n\t<longdescription>"+bug.getLongDescription()+"</longdescription>";
        BugXml += "\n\t<priority>"+bug.getPriority()+"</priority>";
        BugXml += "\n\t<status>"+bug.getStatus()+"</status>";
        BugXml += "\n\t<userreportedemail>"+bug.getUserReportedEmail()+"</userreportedemail>";	        
        BugXml += "\n\t<userassignedemail>"+bug.getUserAssignedEmail()+"</userassignedemail>";
        BugXml += "\n</bug>";
        
        // Create a new node from the string.
        Element node =  DocumentBuilderFactory
        	    .newInstance()
        	    .newDocumentBuilder()
        	    .parse(new ByteArrayInputStream(BugXml.getBytes()))
        	    .getDocumentElement();
		return node;
	}
	

}
