package com.funambol.famundo.util;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import com.funambol.foundation.util.Def;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.AbstractSyncSource;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.tools.beans.LazyInitBean;

public abstract class FamundoSyncSource extends AbstractSyncSource
implements SyncSource, Serializable, LazyInitBean {
    protected static final FunambolLogger _log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);
    protected FamundoContext _ctx;
    protected Properties _properties;
    protected Date _requestStart;
	protected FamundoContentType _contentType;
	
    public Properties getProperties()
    {
		return _properties;
	}

	public void setProperties(Properties properties)
	{
		_properties = properties;
	}
	
	public FamundoContentType getContentType()
	{
		return _contentType;
	}
	
	public void setContentType(FamundoContentType contentType)
	{
		_contentType = contentType;
	}
	

    public void init()
    {
    	_log.info("init() called.");
    }

    public void beginSync(SyncContext context)
    throws SyncSourceException
    {
    	super.beginSync(context);
    	_log.info("beginSync() called.");

    	Sync4jUser user = context.getPrincipal().getUser();
    	String[] parts = user.getUsername().trim().split(" ");
    	if (parts.length < 2)
    		throw new SyncSourceException("Missing family name or username");
    	Identity identity = new Identity(parts[0], parts[1], user.getPassword());
    	Module contacts = new Module(getModuleName());
    	_ctx = new FamundoContext(identity,
    			contacts,
    			_properties.getProperty("domain"),
    			context.getPrincipal()
    			);
    	_requestStart = new Date();
    }
    
    protected abstract String getModuleName();

    public void setOperationStatus(String operation, int statusCode, SyncItemKey[] keys)
    {
    }
}
