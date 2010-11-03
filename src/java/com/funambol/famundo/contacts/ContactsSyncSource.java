package com.funambol.famundo.contacts;

import java.sql.Timestamp;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.famundo.util.FamundoSyncSource;
import com.funambol.famundo.util.HttpRequest;


public class ContactsSyncSource extends FamundoSyncSource {
	public static final long serialVersionUID = 5;
	
	public ContactsSyncSource()
    {
    }
    
	protected String getModuleName()
	{
		return "contact";
	}
	
    public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException
    {
    	_log.info("getAllSyncItemKeys() called.");
    	HttpRequest request = _ctx.createRequest("/api/contact_syncml/get_all_sync_item_keys");
    	_log.info("getAllSyncItemKeys() Request: " + request);
    	SyncItemKey[] result = _ctx.fetchKeys(request);
    	_log.info("getAllSyncItemKeys() item count: " + result.length);
    	return result;
    }

    public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp until)
    throws SyncSourceException
    {
    	_log.info("getNewSyncItemKeys() called.");
    	HttpRequest request = _ctx.createRequest("/api/contact_syncml/get_new_sync_item_keys");
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
    	HttpRequest request = _ctx.createRequest("/api/contact_syncml/get_deleted_sync_item_keys");
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
    	HttpRequest request = _ctx.createRequest("/api/contact_syncml/get_updated_sync_item_keys");
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
    	HttpRequest request = _ctx.createRequest("/api/contact_syncml/get_sync_item_from_id/" + localKey);
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
    	HttpRequest request = _ctx.createRequest("/api/contact_syncml/remove_sync_item/" + localKey,
    			"DELETE");
    	_log.info("removeSyncItem() Request: " + request);
    	_ctx.runRequest(request);
    }

    public SyncItem updateSyncItem(SyncItem syncItem)
    throws SyncSourceException
    {
    	_log.info("updateSyncItem() called.");
    	String localKey = _ctx.getLocalKey(syncItem.getKey().getKeyAsString());
    	HttpRequest request = _ctx.createRequest("/api/contact_syncml/update_sync_item/" + localKey,
    			"PUT");
    	_ctx.addDateConstraint(request, "timestamp", _requestStart);
    	request.setData(_contentType.convertToMain(syncItem.getContent(), _ctx));
    	_log.info("updateSyncItem() Request: " + request);
    	_ctx.runRequest(request);
    	syncItem.setState(SyncItemState.UPDATED);
    	return syncItem;
    }

    public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException
    {
    	_log.info("addSyncItem() called.");
    	HttpRequest request = _ctx.createRequest("/api/contact_syncml/add_sync_item", "POST");
    	_ctx.addDateConstraint(request, "timestamp", _requestStart);
    	byte[] sifData = _contentType.convertToMain(syncItem.getContent(), _ctx);
    	request.setData(sifData);
    	_log.info("addSyncItem() Request: " + request);
    	String qualifiedKey = _ctx.fetchSingleQualifiedKey(request);
    	SifManager sif = new SifManager(sifData);
    	sif.updateId(qualifiedKey);

    	return new SyncItemImpl(this,
    			qualifiedKey,
    			syncItem.getParentKey(),
    			null,
    			SyncItemState.NEW,
    			_contentType.convertFromMain(sif.getXml(), _ctx),
    			syncItem.getFormat(),
    			syncItem.getType(),
    			syncItem.getTimestamp());
    }

    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
    throws SyncSourceException
    {
    	_log.info("getSyncItemKeysFromTwin() called.");
    	HttpRequest request = _ctx.createRequest("/api/contact_syncml/get_sync_item_keys_from_twin", "POST");
    	byte[] sifData = _contentType.convertToMain(syncItem.getContent(), _ctx);
    	request.setData(sifData);
    	_log.info("getSyncItemKeysFromTwin() Request: " + request);
    	SyncItemKey[] result = _ctx.fetchKeys(request);
    	_log.info("getSyncItemKeysFromTwin() item count: " + result.length);
    	return result;
    }
}
