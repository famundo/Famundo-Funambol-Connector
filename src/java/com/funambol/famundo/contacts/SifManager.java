package com.funambol.famundo.contacts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.funambol.framework.engine.source.SyncSourceException;

public class SifManager {
	byte[] _xml;
	
	public byte[] getXml()
	{
		return _xml;
	}
	
	public SifManager(byte[] xml)
	{
		_xml = xml;
	}
	
	public void updateId(String id)
	throws SyncSourceException
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(_xml));
			Element root = document.getDocumentElement();
			NodeList uidList = root.getElementsByTagName("Uid");
			if (uidList != null && uidList.getLength() > 0)
			{
				for (int i = 0; i < uidList.getLength(); i++)
				{
					Node node = uidList.item(i);
					node.getParentNode().removeChild(node);
				}
			}
	
			Element uid = document.createElement("Uid");
			uid.appendChild(document.createTextNode(id));
			root.appendChild(uid);
			
			_xml = doc2bytes(document);
		}
		catch (Exception e)
		{
			throw new SyncSourceException(e.toString());
		}
	}
	
    public byte[] doc2bytes(Node node)
    {
        try
        {
            Source source = new DOMSource(node);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(out);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
            return out.toByteArray();
        }
        catch (TransformerConfigurationException e)
        {
        }
        catch (TransformerException e)
        {
        }
        
        return null;
    }	
}
