package com.funambol.famundo.calendar;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.famundo.util.FamundoSyncSource;
import com.funambol.famundo.util.HttpRequest;


public class CalendarSyncSource extends FamundoSyncSource {
	public static final long serialVersionUID = 1;
	
	public static final int ACCEPTEDTYPE_EVENTS	 = 1;
	public static final int ACCEPTEDTYPE_TASKS	 = 2;
	public static final int ACCEPTEDTYPE_ALL	 = 3;
	
	private int _acceptedTypes;
	public int getAcceptedTypes()
	{
		return _acceptedTypes;
	}
	
	public void setAcceptedTypes(int types)
	{
		_acceptedTypes = types;
	}
	
	public CalendarSyncSource()
    {
    }
    
	protected String getModuleName()
	{
		return "calendar";
	}
	
	public void init()
	{
		super.init();
	}
    
    public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException
    {
    	_log.info("getAllSyncItemKeys() called.");
    	HttpRequest request = _ctx.createRequest("/api/calendar_syncml/get_all_sync_item_keys");
    	adjustRequestWithType(request);
    	_log.info("getAllSyncItemKeys() Request: " + request);
    	SyncItemKey[] result = _ctx.fetchKeys(request);
    	_log.info("getAllSyncItemKeys() item count: " + result.length);
    	return result;
    }

    public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp until)
    throws SyncSourceException
    {
    	_log.info("getNewSyncItemKeys() called.");
    	HttpRequest request = _ctx.createRequest("/api/calendar_syncml/get_new_sync_item_keys");
    	adjustRequestWithType(request);
    	_ctx.addDateConstraints(request, since, until);
    	_log.info("getNewSyncItemKeys() Request: " + request);
    	SyncItemKey[] result = _ctx.fetchKeys(request);
    	_log.info("getNewSyncItemKeys() item count: " + result.length);
    	return result;
    }

    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp until)
    throws SyncSourceException
    {
    	_log.info("getDeletedSyncItemKeys() called.");
    	HttpRequest request = _ctx.createRequest("/api/calendar_syncml/get_deleted_sync_item_keys");
    	adjustRequestWithType(request);
    	_ctx.addDateConstraints(request, since, until);
    	_log.info("getDeletedSyncItemKeys() Request: " + request);
    	SyncItemKey[] result = _ctx.fetchKeys(request);
    	_log.info("getDeletedSyncItemKeys() item count: " + result.length);
    	return result;
    }

    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp until)
    throws SyncSourceException
    {
    	_log.info("getUpdatedSyncItemKeys() called.");
    	HttpRequest request = _ctx.createRequest("/api/calendar_syncml/get_updated_sync_item_keys");
    	adjustRequestWithType(request);
    	_ctx.addDateConstraints(request, since, until);
    	_log.info("getUpdatedSyncItemKeys() Request: " + request);
    	SyncItemKey[] result = _ctx.fetchKeys(request);
    	_log.info("getUpdatedSyncItemKeys() item count: " + result.length);
    	return result;
    }

    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
    throws SyncSourceException
    {
    	_log.info("getSyncItemFromId() called.");
    	String qualifiedKey = syncItemKey.getKeyAsString();
    	SyncItem result = new SyncItemImpl(this, qualifiedKey);

    	String localKey = _ctx.getLocalKey(qualifiedKey);
    	HttpRequest request = _ctx.createRequest("/api/calendar_syncml/get_sync_item_from_id/" + localKey);
    	if (localKey.startsWith("e"))
    		request.addParameter("type", "event");
    	else if (localKey.startsWith("t"))
    		request.addParameter("type", "todo");
    	_log.info("getSyncItemFromId() Request: " + request);
    	result.setContent(_contentType.convertFromMain(_ctx.runRequest(request), _ctx));
    	result.setType(_contentType.getContentType());
    	result.setState(SyncItemState.NEW);
    	return result;
    }

    public void removeSyncItem(SyncItemKey syncItemKey,
                               Timestamp   time       ,
                               boolean     softDelete )
    throws SyncSourceException
    {
    	_log.info("removeSyncItem() called.");
    	String localKey = _ctx.getLocalKey(syncItemKey.getKeyAsString());
    	HttpRequest request = _ctx.createRequest("/api/calendar_syncml/remove_sync_item/" + localKey,
    			"DELETE");
    	_log.info("removeSyncItem() Request: " + request);
    	_ctx.runRequest(request);
    }

    public SyncItem updateSyncItem(SyncItem syncItem)
    throws SyncSourceException
    {
    	_log.info("updateSyncItem() called.");
    	String localKey = _ctx.getLocalKey(syncItem.getKey().getKeyAsString());
    	HttpRequest request = _ctx.createRequest("/api/calendar_syncml/update_sync_item/" + localKey,
    			"PUT");
    	request.setData(wrapInXml(_contentType.convertToMain(syncItem.getContent(), _ctx)));
    	_log.info("updateSyncItem() Request: " + request);
    	_ctx.runRequest(request);
    	syncItem.setState(SyncItemState.UPDATED);
    	return syncItem;
    }

    public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException
    {
    	_log.info("addSyncItem() called.");
    	HttpRequest request = _ctx.createRequest("/api/calendar_syncml/add_sync_item", "POST");
    	request.setData(wrapInXml(_contentType.convertToMain(syncItem.getContent(), _ctx)));
    	_log.info("addSyncItem() Request: " + request);
    	String qualifiedKey = _ctx.fetchSingleQualifiedKey(request);

    	return new SyncItemImpl(this,
    			qualifiedKey,
    			syncItem.getParentKey(),
    			null,
    			SyncItemState.NEW,
    			syncItem.getContent(),
    			syncItem.getFormat(),
    			syncItem.getType(),
    			syncItem.getTimestamp());
    }

    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
    throws SyncSourceException
    {
    	_log.info("getSyncItemKeysFromTwin() called.");
    	HttpRequest request = _ctx.createRequest("/api/calendar_syncml/get_sync_item_keys_from_twin", "POST");
    	request.setData(wrapInXml(_contentType.convertToMain(syncItem.getContent(), _ctx)));
    	_log.info("getSyncItemKeysFromTwin() Request: " + request);
    	SyncItemKey[] result = _ctx.fetchKeys(request);
    	_log.info("getSyncItemKeysFromTwin() item count: " + result.length);
    	return result;
    }

    private void adjustRequestWithType(HttpRequest request)
    {
    	switch (_acceptedTypes)
    	{
    		case ACCEPTEDTYPE_EVENTS:
    			request.addParameter("type", "event");
    			break;
    		case ACCEPTEDTYPE_TASKS:
    			request.addParameter("type", "todo");
    			break;
    	}
    }
    
    private static byte[] wrapInXml(byte[] data)
    {
    	try
    	{
	    	ByteArrayOutputStream os = new ByteArrayOutputStream();
	    	DataOutputStream dataStream = new DataOutputStream(os);
	    	dataStream.writeBytes("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	    	dataStream.writeBytes("<item>\n");
	    	dataStream.writeBytes("<![CDATA[\n");
	    	dataStream.write(data);
	    	dataStream.writeBytes("]]>\n");
	    	dataStream.writeBytes("</item>\n");
	    	return os.toByteArray();
    	}
    	catch (IOException e)
    	{
    		return data;
    	}
    }
}
