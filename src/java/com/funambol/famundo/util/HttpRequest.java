package com.funambol.famundo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class HttpRequest {
	private static final String ContentType = "application/xml";
	
	private Identity _identity;
	private String _method;
	private String _command;
	private ArrayList _parameters = new ArrayList();
	private byte[] _data = null;
	private String _domain;
	
	public HttpRequest(Identity identity, String method, String command, String domain)
	{
		_identity = identity;
		_method = method;
		_command = command;
		_domain = domain;
	}

	public HttpRequest(Identity identity, String command, String domain)
	{
		this(identity, "GET", command, domain);
	}
	
	public void addParameter(String key, String value)
	{
		StringBuffer sb = new StringBuffer(key);
		sb.append("=");
		sb.append(value);
		_parameters.add(sb.toString());
	}
	
	public byte[] getData()
	{
		return _data;
	}

	public void setData(byte[] data)
	{
		_data = data;
	}
	
	public String getDomain()
	{
		return _domain;
	}
	
	public void setDomain(String domain)
	{
		_domain = domain;
	}

	public byte[] run()
	throws ConnectorException
	{
		HttpURLConnection request = null;
		try
		{
			request = createRequest();
			setLoginInformation(request);
			setHeaders(request);
			if (_data != null)
				request.getOutputStream().write(_data);
			return receiveData(request.getInputStream());
		}
		catch (Exception e)
		{
			throw new ConnectorException(e.toString());
		}
		finally
		{
			if (request != null)
				request.disconnect();
		}
	}

	private HttpURLConnection createRequest()
	throws IOException, ProtocolException, ConnectorException
	{
		URLConnection connection = constructUrl().openConnection();
		HttpURLConnection result = (HttpURLConnection)connection;
		result.setRequestMethod(_method);
		if (_method == "POST" || _method == "PUT")
			result.setDoOutput(true);
			
		return result;
	}
	
	private static byte[] receiveData(InputStream stream)
	throws IOException
	{
		byte[] buf = new byte[2048];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int len;
		while ((len = stream.read(buf, 0, buf.length)) != -1)
			outputStream.write(buf, 0, len);
		return outputStream.toByteArray();
	}
	
	private void setHeaders(HttpURLConnection request)
	{
		request.setRequestProperty("Content-Type", ContentType);
		request.setRequestProperty("Accept", ContentType);
	}
	
	private void setLoginInformation(HttpURLConnection request)
	{
		request.setRequestProperty("X-Fmd-Family", _identity.getFamily());
		request.setRequestProperty("X-Fmd-User", _identity.getUser());
		request.setRequestProperty("X-Fmd-Password", _identity.getPassword());
	}
	
	private URL constructUrl()
	throws ConnectorException
	{
		String hostname = _identity.getFamily() + _domain;
		try
		{
			URI uri = new URI("http", hostname, _command, getParameters(), null);
			return uri.toURL();
		}
		catch (Exception e)
		{
			throw new ConnectorException(e.toString());
		}
	}
	
	private String getParameters()
	{
		if (_parameters.isEmpty())
			return null;
		return join(_parameters, "&");
	}
	
    private static String join(Collection s, String delimiter)
    {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }
    
    public String toString()
    {
    	try
    	{
    		return _method + " " + constructUrl().toString();
    	}
    	catch (ConnectorException e)
    	{
    		return "";
    	}
    }
}
