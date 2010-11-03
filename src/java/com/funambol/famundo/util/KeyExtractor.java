package com.funambol.famundo.util;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KeyExtractor {
	public KeyExtractor()
	{
	}
	
	public void extract(byte[] xml)
	throws ConnectorException
	{
		class Parser extends DefaultHandler
		{
			private boolean _inside = false;
			private StringBuilder _data;
			
			public void startElement(String namespaceURI, String sName, String qName, Attributes attrs)
			throws SAXException
			{
				_inside = qName.equals("Uid");
				if (_inside)
					_data = new StringBuilder();
			}
			
			public void endElement(String namespaceURI, String sName, String qName)
			throws SAXException
			{
				_inside = false;
				if (_data != null && _data.length() > 0)
				{
					addKey(_data.toString());
					_data = null;
				}
			}
		
			public void characters(char[] buf, int offset, int len)
			throws SAXException
			{
				if(_inside)
					_data.append(new String(buf, offset, len));
			}
		}
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new ByteArrayInputStream(xml), new Parser());
		}
		catch (Exception e)
		{
			throw new ConnectorException(e.toString());
		}
	}
	
	protected void addKey(String key)
	{
	}
}
