package com.famundo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Utilitys for parsing and manipulatin XML object
 * @author dudi
 *
 */
public class FXMLUtil {
	//static final String transformerFactoryClassName = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
	
	static public TransformerFactory transformerFactoryInstance() {
		//return TransformerFactory.newInstance(transformerFactoryClassName, null);
		return TransformerFactory.newInstance();
	}

	//static public final String saxParserFactoryClassName = "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl";
	
	static public SAXParserFactory saxParserFactoryInstance() {
		//return SAXParserFactory.newInstance(saxParserFactoryClassName, null);
		return SAXParserFactory.newInstance();
	}
	
	//static public final String documentBuilderFactoryClassName = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
	
	static public DocumentBuilderFactory documentBuilderFactory() {
		//return DocumentBuilderFactory.newInstance(documentBuilderFactoryClassName, null);
		return DocumentBuilderFactory.newInstance();
	}
	
	/**
	 * parse a list of ids restuend by the famundo server
	 * the keys are in a fromat of:<br/> 
	 * {@code&lt;keys&gt;<br/>}
	 * {@code&nbsp;&nbsp;&lt;Uid&gt;1&lt;/Uid&gt;<br/>}
	 * {@code&nbsp;&nbsp;&lt;Uid&gt;2&lt;/Uid&gt;<br/>}
	 * {@code&lt;keys&gt;<br/>}
	 * 
	 * @param bxml byte[] input xml
	 * @return int[] array of ids
	 */
	public static int[] parseKeys(byte[] bxml) {
		class SaxHandler extends DefaultHandler 
		{
			private boolean inUid = false;
			private ArrayList keys;
			
			SaxHandler(ArrayList keys) {
				super();
				this.keys = keys;
			}
		
			//@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				inUid = "Uid".equals(qName);
			}
			
			//@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				inUid = false;
			}
		
			
			//@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				if(inUid) {
					//System.out.println( new String(ch, start, length) );
					keys.add( new Integer( Integer.parseInt( new String(ch, start, length),10)) );
				}
			}
		}

		
		int ret[] = null;
		try {

			ArrayList keys = new ArrayList();
			SAXParser parser = saxParserFactoryInstance().newSAXParser();
			parser.parse(new ByteArrayInputStream(bxml), new SaxHandler(keys) );
			ret = new int[keys.size()]; //keys.toArray(new Integer[keys.size()]);
			for (int i = 0; i < ret.length; i++) {
				ret[i] = ((Integer)keys.get(i)).intValue();
			}
			
		} catch (Exception e) {
			ret = null;
			//e.printStackTrace();
		} 
		return ret;
	}

	/**
	 * extract the Uid XML elemnt value from a xml sif format 
	 * @param bxml byte[] the sif xml
	 * @return String the Uid value or null
	 */
	public static String parseUidString(byte[] bxml) {
		class UidParserHandler extends DefaultHandler 
		{
			private boolean inUid = false;
			public String uid = null;
			
			//@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				inUid = "Uid".equals(qName);
			}
			
			//@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				inUid = false;
			}
		
			
			//@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				if(inUid) {
					uid = new String(ch, start, length);
					//System.out.println("@@@@@@@UID is : " + uid);
				}
			}
		}
		
		String ret = null;
		try {
			SAXParser parser = saxParserFactoryInstance().newSAXParser();
			UidParserHandler handler = new UidParserHandler();
			parser.parse(new ByteArrayInputStream(bxml), handler );
			ret = handler.uid;
		} catch (Exception e) {
			ret = null;
			e.printStackTrace();
		} 
		return ret;
	}

	/**
	 * Extract the objects famundo database id from the sif Uid value 
	 * @param bxml byte[] the sif xml
	 * @return int the id (-1 if cannot find or parse it)
	 */
	public static int parseUid(byte[] bxml) {
		return FUtil.parseIdFromUid(parseUidString(bxml));
	}

	/**
	 * create document from byte[] input
	 * @param bxml byte[]
	 * @return Document
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	static public Document createDocument(byte[] bxml) 
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = documentBuilderFactory().newDocumentBuilder();
		return builder.parse( new ByteArrayInputStream(bxml) );
	}
	
	/**
	 * Find a direct child Element node with a given tag anme
	 * @param parent Element
	 * @param tagName String
	 * @return Element
	 */
	static public Element findChildWithTagName(Element parent, String tagName) {
		Element ret = null;
		NodeList chilren = parent.getChildNodes();
		for (int i = 0; i < chilren.getLength(); i++) {
			Node node = chilren.item(i);
			if( (node.getNodeType() == Node.ELEMENT_NODE) && 
				(((Element)node).getTagName().equals(tagName) )	) {
				ret = (Element)node;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Get the byte[] stream of a given DOM
	 * @param document Document
	 * @return byte[]
	 * @throws TransformerException 
	 * @throws  
	 */
	static public byte[] toByte(Document document) throws TransformerException {
		Transformer transformer = transformerFactoryInstance().newTransformer();

		DOMSource source = new DOMSource(document);
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(byteStream);
		transformer.transform(source, result);
		return byteStream.toByteArray();
	}
	
	/**
	 * appends or replaces the Uid value in a SIF xml
	 * @param bxml byte[] the SIF xml 
	 * @param objectName String the SIF object root tag name
	 * @param String the objects uid to set or append
	 * @return byte[] the chaned SIF xml
	 */
	public static byte[] setObjectUid(byte[] bxml, String objectName, String uidValue) throws FamundoException {
		try {
			Document  document = createDocument(bxml);
			Element root = document.getDocumentElement();
			if( !root.getTagName().equalsIgnoreCase(objectName) )
				throw new FamundoException("root element is " + root.getTagName() + "and not " + objectName );
			
			// find uid node
			Element uidElement = findChildWithTagName(root, "Uid");
			
			if(uidElement == null) {
				// add a new element
				uidElement = document.createElement("Uid");
				root.appendChild(uidElement);
			} else {
				// remove all children
				while(uidElement.getFirstChild()!= null)
					uidElement.removeChild(uidElement.getFirstChild());
			}
			// add child text node with id
			uidElement.appendChild(document.createTextNode(uidValue));
			
			return toByte(document);
		} catch (FamundoException e) {
			throw e;
		} catch (Exception e) {
			throw new FamundoException(e.toString());
		}
	}

}
