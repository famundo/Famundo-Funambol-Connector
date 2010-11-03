package com.funambol.famundo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.server.config.Configuration;

public class FamundoContext {
	private static final String DateFormat = "yyyy-MM-dd HH:mm:ss";
	private static final String KeySeparator = "/";
	private Identity _identity;
	private Module _module;
	private String _domain;
	private SimpleDateFormat _dateFormat;
	private Sync4jDevice _device;
	private String _deviceTimezoneDescr;
	private TimeZone _deviceTimezone;
	private String _deviceCharset;
	
	public FamundoContext(Identity identity, Module module, String domain, Sync4jPrincipal principal)
	throws SyncSourceException
	{
		_identity = identity;
		_module = module;
		_domain = domain;
		if (principal != null)
			initPersonalSettings(principal);
		
		_dateFormat = new SimpleDateFormat(DateFormat);
		_dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public void initPersonalSettings(Sync4jPrincipal principal)
	throws SyncSourceException
	{
		String deviceId = null;
		try
		{
			deviceId = principal.getDeviceId();
			_device = getDevice(deviceId);
			String timezone = _device.getTimeZone();
			if (_device.getConvertDate())
			{
				if (timezone != null && timezone.length() > 0)
				{
					_deviceTimezoneDescr = timezone;
					_deviceTimezone = TimeZone.getTimeZone(_deviceTimezoneDescr);
				}
			}

			_deviceCharset = _device.getCharset();
		}
		catch (PersistentStoreException ex)
		{
			throw new SyncSourceException("Error reading device '" + deviceId + "'", ex);
		}
	}
	
    private Sync4jDevice getDevice(String deviceId) throws PersistentStoreException
    {
        Sync4jDevice myDevice = new Sync4jDevice(deviceId);
        PersistentStore store = Configuration.getConfiguration().getStore();
        store.read(myDevice);
        return myDevice;
    }

	public Identity getIdentity()
	{
		return _identity;
	}

	public void setIdentity(Identity identity)
	{
		_identity = identity;
	}

	public Module getModule()
	{
		return _module;
	}

	public void setModule(Module module)
	{
		_module = module;
	}
	
	public String getDeviceCharset()
	{
		return _deviceCharset;
	}

	public TimeZone getDeviceTimezone()
	{
		return _deviceTimezone;
	}

	public String getDeviceTimezoneDescr()
	{
		return _deviceTimezoneDescr;
	}

	public HttpRequest createRequest(String command, String method)
	{
		return new HttpRequest(_identity, method, command, _domain);
	}

	public HttpRequest createRequest(String command)
	{
		return new HttpRequest(_identity, command, _domain);
	}
	
	public String getFormattedDate(Date date)
	{
		return _dateFormat.format(date);
	}
	
    public void addDateConstraints(HttpRequest request, Date since, Date until)
    {
    	if (since != null)
    		addDateConstraint(request, "since", since);
    	if (until != null)
    		addDateConstraint(request, "until", until);
    }
    
    public void addDateConstraint(HttpRequest request, String key, Date date)
    {
    	request.addParameter(key, getFormattedDate(date));
    }
    
    public SyncItemKey[] fetchKeys(HttpRequest request)
    throws SyncSourceException
    {
    	try
    	{
			QualifiedKeyExtractor e = new QualifiedKeyExtractor(this);
			e.extract(request.run());
			return e.getKeys();
    	}
    	catch (ConnectorException e)
    	{
    		throw new SyncSourceException(e.toString());
    	}
    }
    
    public String fetchSingleQualifiedKey(HttpRequest request)
    throws SyncSourceException
    {
    	try
    	{
    		SingleQualifiedKeyExtractor e = new SingleQualifiedKeyExtractor(this);
			e.extract(request.run());
			return e.getKey();
    	}
    	catch (ConnectorException e)
    	{
    		throw new SyncSourceException(e.toString());
    	}
    }
    
    public String getQualifiedKey(String localKey)
    {
		StringBuffer sb = new StringBuffer(getIdentity().getFamily());
		sb.append(KeySeparator);
		sb.append(getModule().getName());
		sb.append(KeySeparator);
		sb.append(localKey);
		return sb.toString();
    }
    
    public String getLocalKey(String qualifiedKey)
    {
    	int idx = qualifiedKey.lastIndexOf(KeySeparator);
    	if (idx == -1)
    		return qualifiedKey;
    	return qualifiedKey.substring(idx + 1);
    }
    
    public byte[] runRequest(HttpRequest request)
    throws SyncSourceException
    {
    	try
    	{
    		return request.run();
    	}
    	catch (ConnectorException e)
    	{
    		throw new SyncSourceException(e.toString());
    	}
    }
}
