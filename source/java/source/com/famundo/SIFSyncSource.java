package com.famundo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.AbstractSyncSource;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.logging.Sync4jLogger;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;


public class SIFSyncSource extends AbstractSyncSource
implements SyncSource, Serializable, LazyInitBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7729908411735830860L;

	protected Logger log;
	public    boolean bLog = true;
	
	protected int    syncMode;
	protected FamundoAPI api;
	Date syncBeginAt = null;
	
    public SIFSyncSource() {
    	if(bLog) {
	    	log = Sync4jLogger.getLogger("server");
	    	log.info("NEW famundo.SIFSyncSource");
    	}
    }
    
	private SyncItemKey[] ids2keys(int[] ids) {
		SyncItemKey[] keys = new SyncItemKey[ids.length]; 
		for (int i = 0; i < ids.length; i++) {
			keys[i] = new SyncItemKey(api.id2path(ids[i]));
		}
		return keys;
	}
    
    
    private void logit(String msg) {
    	if(bLog && (log != null)) {
    		log.info(msg);
    	}
    }

    private Family createFamily(SyncContext context) throws SyncSourceException {
    	Sync4jUser      s4jUser   = context.getPrincipal().getUser();
		Matcher match = Pattern.compile("^\\s*(\\S*)\\s*/\\s*(\\S*)\\s*$").matcher(s4jUser.getUsername());
		if( match.matches() ) {
			return new Family(match.group(1), match.group(2), s4jUser.getPassword());
		}
		throw new SyncSourceException("Famundo: missing user or family name");
    }
    
    private FumundoObjectType getFumundoObjectType() throws SyncSourceException {
    	FumundoObjectType ret = FumundoObjectType.getFromSourceURI(this.sourceURI);
    	if(ret == null)
    		throw new SyncSourceException("Famundo: unknown sourceURI :" + this.sourceURI);
    	return ret;
    }
    
	public void init() throws BeanInitializationException {
		logit("famundo.SIFSyncSource.init");
	}

	private static Date createSyncBeginAt() {
		Calendar calendar = Calendar.getInstance();
		return new Date(calendar.getTimeInMillis() + calendar.get(Calendar.ZONE_OFFSET) - 1*60*1000);
	}
	//@Override
	public void beginSync(SyncContext context) throws SyncSourceException {
		try {
			logit("famundo.SIFSyncSource.beginSync");
			super.beginSync(context);
			
			syncBeginAt = createSyncBeginAt();
			
			this.syncMode    = context.getSyncMode(); 
			api = new FamundoAPI(createFamily(context), getFumundoObjectType() );
			
			if(bLog) {
				Sync4jUser      s4jUser   = context.getPrincipal().getUser();
		        StringBuilder sb = new StringBuilder("famundo.SIFSyncSource.beginSync Beginning sync with:");
		        sb.append("\n> syncMode            : ").append(syncMode);
		        sb.append("\n> user                : ").append(s4jUser.getUsername());
		        sb.append("\n> password            : ").append(s4jUser.getPassword());
		        sb.append("\n> sourceURI           : ").append(this.sourceURI);
		        sb.append("\n> type                : ").append(this.type); 
		        sb.append("\n> name                : ").append(this.name); 
		        sb.append("\n> family name         : ").append(api.getFamily().getFamily()); 
		        sb.append("\n> user   name         : ").append(api.getFamily().getUser()); 
		        sb.append("\n> syncBeginAt         : ").append(syncBeginAt); 
		        logit(sb.toString());
			}

			api.canSync(); // check the login
			
		} catch (SyncSourceException e) {
			logit("Exception in famundo.SIFSyncSource.beginSync\n" + e.getMessage() );
			throw e;
		} catch (Exception e) {
			logit("Exception in famundo.SIFSyncSource.beginSync\n" + e.getMessage() );
			throw new SyncSourceException(e);
		}
	}

	//@Override
	public void endSync() throws SyncSourceException {
		logit("famundo.SIFSyncSource.endSync");
		super.endSync();
	}

	//@Override
	public void setOperationStatus(String operation, int statusCode, SyncItemKey[] keys) {
		if(bLog) {
	        StringBuffer message = new StringBuffer("famundo.SIFSyncSource.setOperationStatus:\n'");
	        message.append(statusCode).append("' for a '").append(operation).append("'").
	                append(" for this items: ");
	
	        for (int i = 0; i < keys.length; i++) {
	            message.append("\n- " + keys[i].getKeyAsString());
	        }
			logit(message.toString());
		}
	}
	
	//@Override
	
	/**
	 * Called by the engine to add a new SyncItem. The item is also returned, which enables the
     * source to modify its content and return the updated item (e.g., updating the id to the GUID).
     * @param syncInstance – The item to add
	 */
	public SyncItem addSyncItem(SyncItem syncInstance) throws SyncSourceException {
		try {
			logit("famundo.SIFSyncSource.addSyncItem");
			logit( new String(syncInstance.getContent(), "UTF-8") );
			
			int newId = api.addSyncItem(syncInstance.getContent(), syncBeginAt);
			String uid = api.id2path(newId);
			byte[] bxml = FXMLUtil.setObjectUid(syncInstance.getContent(), api.getObjType().getXmlRoot(), uid);
			
			return new SyncItemImpl(
					this, 
					uid, 
					syncInstance.getParentKey(),
                    null, //Object     mappedKey,
                    syncInstance.getState(),
                    bxml,
                    syncInstance.getFormat(),
                    syncInstance.getType(),
                    syncInstance.getTimestamp());

		} catch (Exception e) {
			logit("Exception in famundo.SIFSyncSource.addSyncItem\n" + e.getMessage() );
			throw new SyncSourceException(e);
		}
	}

	
	/**
	 * Called by the engine to get the SyncItemKeys of all items based on the parameters used in the
	 * beginSync call. Returns an array of the SyncItemKeys stored in this source. Returns an empty
     * array if there are no items.
     * 
     * @return SyncItemKey[]
	 */
	//@Override
	public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {
		try {
			logit("famundo.SIFSyncSource.getAllSyncItemKeys");
			int[] ids = api.getAllSyncItemKeys();
			logIds(ids);
			return ids2keys( ids );
		} catch (Exception e) {
			logit("Exception in famundo.SIFSyncSource.getAllSyncItemKeys\n" + e.getMessage() );
			throw new SyncSourceException(e);
		}
	}

	/**
	 * Called by the engine to get the SyncItemKey of all items deleted during the time period
	 * sinceTs - untilTs. This time period is the time between the last synchronization and the start
	 * time of the current synchronization. If sinceTs is null, gets the SyncItemKey of all items
	 * deleted up to and including untilTs. If untilTs is null, gets the SyncItemKey of all items
	 * deleted from sinceTs and later.
	 * 
	 * @param sinceTS – beginning point of time period, i.e., for a fast synchronization, the time of the last synchronization. For a slow synchronization, this parameter is null.
	 * @param untilTS – ending point of time period.
	 * @return SyncItemKey[]
	 * 
	 * @throws SyncSourceException
	 */
	//@Override
	public SyncItemKey[] getDeletedSyncItemKeys(Timestamp sinceTs, Timestamp untilTs) throws SyncSourceException {
		try {
			Date since = FUtil.toUTC(sinceTs);
			Date until = FUtil.toUTC(untilTs);
			logSinceUntil("getDeletedSyncItemKeys", since, until);
			int [] ids = api.getDeletedSyncItemKeys(since, until);
			logIds(ids);
			return ids2keys( ids );
		} catch (Exception e) {
			logit("Exception in famundo.SIFSyncSource.getDeletedSyncItemKeys\n" + e.getMessage() );
			throw new SyncSourceException(e);
		}
	}
	
	
	/**
	 * Called by the engine to get the SyncItemKey of the items created during the time period
	 * sinceTs - untilTs. This time period is the time between the last synchronization and the start
	 * time of the current synchronization. If sinceTs is null, gets the SyncItemKey of all items
	 * created up to and including untilTs. If untilTs is null, gets the SyncItemKey of all items
	 * created from sinceTs and later.
	 * 
	 * @param sinceTS – beginning point of time period, i.e., for a fast synchronization, the time of the last synchronization. For a slow synchronization, this parameter is null.
	 * @param untilTS – ending point of time period.
	 * @return SyncItemKey[]
	 * 
	 * @throws SyncSourceException
	 */
	//@Override
	public SyncItemKey[] getNewSyncItemKeys(Timestamp sinceTs, Timestamp untilTs) throws SyncSourceException {
		try {
			Date since = FUtil.toUTC(sinceTs);
			Date until = FUtil.toUTC(untilTs);
			logSinceUntil("getNewSyncItemKeys", since, until);
			int[] ids =  api.getNewSyncItemKeys(since, until);
			logIds(ids);
			return ids2keys( ids );
		} catch (Exception e) {
			logit("Exception in famundo.SIFSyncSource.getNewSyncItemKeys\n" + e.getMessage() );
			throw new SyncSourceException(e);
		}
	}

	//@Override
	public SyncItem getSyncItemFromId(SyncItemKey syncItemKey) throws SyncSourceException {
		try {
			logFuncWithKey("getSyncItemFromId", syncItemKey);
			byte[] content = api.getSyncItemFromId(FUtil.parseIdFromUid(syncItemKey.getKeyAsString()));
			
	        SyncItem item = new SyncItemImpl(this, syncItemKey.getKeyAsString(), ' ');
	        item.setContent(content);
	        item.setType(getType());
	        return item;
		} catch (Exception e) {
			logit("Exception in famundo.SIFSyncSource.getSyncItemFromId\n" + e.getMessage() );
			throw new SyncSourceException(e);
		}
	}

	/**
	 * Called by the engine to get the SyncItemKeys of the twins of the given item. Each source
	 * implementation can interpret this as desired (i.e., comparing all fields).
	 */
	//@Override
	public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem) throws SyncSourceException {
		try {
			logit("famundo.SIFSyncSource.getSyncItemKeysFromTwin");
			int[] ids = api.getSyncItemKeysFromTwin(syncItem.getContent());
			logIds(ids);
			logit( new String(syncItem.getContent(), "UTF-8") );
			return ids2keys( ids );
		} catch (Exception e) {
			logit("Exception in famundo.SIFSyncSource.getNewSyncItemKeys\n" + e.getMessage() );
			throw new SyncSourceException(e);
		}
	}

	//@Override
	public void removeSyncItem(SyncItemKey itemKey, Timestamp time,boolean softDelete) throws SyncSourceException {
		try {
			logFuncWithKey("removeSyncItem", itemKey);
			api.removeSyncItem(FUtil.parseIdFromUid(itemKey.getKeyAsString()));
		} catch (Exception e) {
			logit("Exception in famundo.SIFSyncSource.removeSyncItem\n" + e.getMessage() );
			throw new SyncSourceException(e);
		}
	}

	//@Override
	public SyncItem updateSyncItem(SyncItem syncInstance) throws SyncSourceException {
		try {
			logit("famundo.SIFSyncSource.updateSyncItem");
			logit( new String(syncInstance.getContent(), "UTF-8") );
			
			int id = -1;
			if( syncInstance.getKey() != null  )
				id = FUtil.parseIdFromUid(syncInstance.getKey().getKeyAsString());
			if(id <= 0)
				id = FXMLUtil.parseUid(syncInstance.getContent());
			
			if(id <= 0 ) {
				logit("NO ID for sorce!!!!!!!!!!!");
				
				int newId = api.addSyncItem(syncInstance.getContent(), syncBeginAt);
				String uid = api.id2path(newId);
				byte[] bxml = FXMLUtil.setObjectUid(syncInstance.getContent(), api.getObjType().getXmlRoot(), uid);

				return new SyncItemImpl(
						this, 
						uid, 
						syncInstance.getParentKey(),
	                    null, //Object     mappedKey,
	                    syncInstance.getState(),
	                    bxml,
	                    syncInstance.getFormat(),
	                    syncInstance.getType(),
	                    syncInstance.getTimestamp());
			} else {
				logit("item id = " + id);
				api.updateSyncItem(id, syncInstance.getContent(), syncBeginAt);
			}	
			return syncInstance;
		} catch (Exception e) {
			logit("Exception in famundo.SIFSyncSource.updateSyncItem\n" + e.getMessage() );
			throw new SyncSourceException(e);
		}
	}

	public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp sinceTs, Timestamp untilTs) throws SyncSourceException {
		try {
			Date since = FUtil.toUTC(sinceTs);
			Date until = FUtil.toUTC(untilTs);
			logSinceUntil("getUpdatedSyncItemKeys", since, until);
			int ids[] =  api.getUpdatedSyncItemKeys(since, until);
			logIds(ids);
			return ids2keys( ids );
		} catch (Exception e) {
			logit("Exception in famundo.SIFSyncSource.getUpdatedSyncItemKeys\n" + e.getMessage() );
			throw new SyncSourceException(e);
		}
	}

	private void logFuncWithKey(String funcName, SyncItemKey syncItemKey) {
		if(bLog) {
	        StringBuilder sb = new StringBuilder("famundo.SIFSyncSource.");
	        sb.append(funcName);
	        sb.append("\n> key : ").append(syncItemKey.getKeyAsString());
	        logit(sb.toString());
		}
	}
	
	private void logSinceUntil(String funcName, Date since, Date until) {
		if(bLog) {
	        StringBuilder sb = new StringBuilder("famundo.SIFSyncSource.");
	        sb.append(funcName);
	        sb.append("\n> sinceTs             : ").append(since);
	        sb.append("\n> untilTs             : ").append(until);
	        logit(sb.toString());
		}
	}

	private void logIds(int ids[]) {
		if(bLog) {
	        StringBuilder sb = new StringBuilder("\n ids = ");
	        for (int i = 0; i < ids.length; i++) {
	        	sb.append(ids[i]).append(", ");
			}
	        logit(sb.toString());
		}
	}
	
}
