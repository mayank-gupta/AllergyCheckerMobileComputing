package com.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * This class implements XML Parser which uses DOM parser to parse XML file which is receieved from google health server. 
 * It contains Google health profile of particular user. It extracts list of allergies from the Google Health Profile XML.  
 * */
public class XMLParser {
	
	
	/*
	 * This function forms XML file of given 
	 * */
	private Document getXMLParser(String xml){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			xml = "<xml>"+xml+"</xml>";
			
			InputSource xmlInputSource = new InputSource();
			xmlInputSource.setCharacterStream(new StringReader(xml));

			//parse using builder to get DOM representation of the XML file
			Document dom = db.parse(xmlInputSource);

			return dom;
			
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}
	
	public ArrayList<String> getAllergy(String healthProfileDataXML){
	
		ArrayList<String> allergyList = new ArrayList<String>();
		
		Document dom  = getXMLParser(healthProfileDataXML);
		
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <employee> elements
		NodeList allergyTypes = docEle.getElementsByTagName("Alert");
				
		if(allergyTypes != null && allergyTypes.getLength() > 0) {
			for(int i = 0 ; i < allergyTypes.getLength();i++) {							
				//get the employee element
				Element allergy = (Element)allergyTypes.item(i);
				
				Element description = (Element)allergy.getElementsByTagName("Description").item(0);
				
				Element name = (Element)description.getElementsByTagName("Text").item(0);

				allergyList.add(name.getTextContent().toLowerCase());
				
			}
			return allergyList;
		}
		return null;
	}
}
