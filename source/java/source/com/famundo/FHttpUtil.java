package com.famundo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Utility for sending http reqests
 * @author dudi
 *
 */
public class FHttpUtil {
	/**
	 * send a HTTP message
	 * @param family 
	 * @param path the path without the domain info (this we get from family)
	 * @param verb HTTP verb (GET, POST, PUT, DELETE)
	 * @param accept "Accept" ("application/xml")
	 * @param content HTTP "Content-Type" ("application/xml")
	 * @param data data to send if content is not null
	 * @return the http returned data or null if not data read
	 * @throws FamundoException
	 */
	public static byte[] sendBytes(Family family, 
							  String path, 
							  String verb, 
							  String accept,
							  String content, 
							  byte[] data) throws FamundoException {
		byte[] ret = null;
		HttpURLConnection connection = null;
		try {
			if(path.startsWith("/"))
				path = path.substring(1);
			connection = open(family.url() + path, verb, accept, content); 
			setFamilyHeaders(connection, family);
			if(!FUtil.isBlank(content))
				FHttpUtil.writeBytes(connection, data);
			if(!FUtil.isBlank(accept))
				ret = FHttpUtil.readBytes(connection);
		} catch (FamundoException e) {
			throw e;
		} catch (Exception e) {
			throw new FamundoException(e.toString());
		} finally {
			try {
				if(null != connection)
					connection.disconnect();
			} catch (Exception e) {
				// do nothing
			}
		}
		return ret;
	}

	/**
	 * open and prepers connection
	 * @param path full url path
	 * @param verb HTTP verb (GET, POST, PUT, ...)
	 * @param content "Content-Type" ("application/xml")
	 * @param accept  "Accept" ("application/xml")
	 * @return an open HttpURLConnection
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private static HttpURLConnection open(String path, String verb, String accept, String content) 
											throws MalformedURLException, IOException, ProtocolException 	{
		URL u = new URL(path);
		URLConnection uc = u.openConnection();
		HttpURLConnection connection = (HttpURLConnection)uc;
		connection.setRequestMethod(verb);
		
	    if(!FUtil.isBlank(content))
	    {
			connection.setRequestProperty("Content-Type", content);
			connection.setDoOutput(true);
	    }
	    
		if(!FUtil.isBlank(accept))
		{
			connection.setRequestProperty("Accept", accept);
			connection.setDoInput(true); 
		}
		
		return connection;
	}

	/**
	 * sets the http fmaundo family headers
	 * @param connection HttpURLConnection
	 * @param family Family
	 * @throws FamundoException
	 */
	private static void setFamilyHeaders(HttpURLConnection connection, Family family) throws FamundoException {
		family.validate();
		connection.setRequestProperty("X-Fmd-Family",   family.getFamily() );
		connection.setRequestProperty("X-Fmd-User",     family.getUser() );
		connection.setRequestProperty("X-Fmd-Password", family.getPassword() );
	}

	/**
	 * write the put or post data to the connection stream
	 * @param connection URLConnection
	 * @param data byte[]
	 * @throws IOException
	 */
	private static void writeBytes(URLConnection connection, byte[] data) throws IOException {
		OutputStream os = connection.getOutputStream();
		os.write(data);
	    os.close();
	}

	/**
	 * read the servers answer from the http reqest
	 * @param connection URLConnection
	 * @return byte[]
	 * @throws IOException
	 */
	private static byte[] readBytes(URLConnection connection) throws IOException {
		ArrayList bytes = new ArrayList();
		int b;
		InputStream in = connection.getInputStream();
		while( (b = in.read()) != -1 ) {
			bytes.add( new Byte((byte)b) );
		}
		in.close();
		byte[] ret = new byte[bytes.size()];
		for( int i = 0; i < bytes.size(); i++) {
			ret[i] = ((Byte)bytes.get(i)).byteValue();
		}
		return ret;
	}

}
