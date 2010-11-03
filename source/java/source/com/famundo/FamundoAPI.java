package com.famundo;

import java.util.Date;

/**
 * Use to access the fumondo server for syncml
 * the xml format will be<br>
 * Contacts<br>
 * 	URI=scard MIME=text/x-s4j-sifc<br>
 * Calendar<br>
 * 	URI=scal MIME=text/x-s4j-sife<br>
 * @author dudi
 *
 */
public class FamundoAPI {
	public static final String MIME_XML = "application/xml";
	private Family family;
	private FumundoObjectType objType = null;
	
	/**
	 * @return current Family
	 */
	public Family getFamily() {
		return family;
	}

	/**
	 * Set current family
	 * @param family Family
	 */
	public void setFamily(Family family) {
		this.family = family;
	}

	/**
	 * Get the current object type (e.g FumundoObjectType.CONTACT }
	 * @return FumundoObjectType
	 */
	public FumundoObjectType getObjType() {
		return objType;
	}

	/**
	 * Set current object type
	 * @param objType FumundoObjectType
	 */
	public void setObjType(FumundoObjectType objType) {
		this.objType = objType;
	} 
	
	/**
	 * a uid from a id using the current family and type
	 * @param id int the famundo database item id
	 * @return String the uid
	 */
	public String id2path(int id) {
		return FUtil.id2uid(family, objType, id);
	}
	
	/**
	 * Create a new FamundoAPI
	 * @param family Family the current family
	 * @param objType FumundoObjectType the object type
	 */
	public FamundoAPI(Family family, FumundoObjectType objType) {
		setFamily(family);
		setObjType(objType);
	}
	
	/**
	 * test the login info
	 * if fails to log in throw an exception
	 * @throws FamundoException
	 */
	public void canSync() throws FamundoException {
		getXML(FUtil.buildPath(objType, "can_sync"));
	}
	
	/**
	 * get all items keys of the current type from the server 
	 * @return int[] array of keys
	 * @throws FamundoException
	 */
	public int[] getAllSyncItemKeys() throws FamundoException {
		return getKeys("get_all_sync_item_keys");
	}

	/**
	 * Get all new items keys 
	 * @param sinceTs Date can be null
	 * @param untilTs Date can be null
	 * @return int[] array of keys
	 * @throws FamundoException
	 */
	public int[] getNewSyncItemKeys(Date sinceTs,Date untilTs) throws FamundoException {
		return getKeys("get_new_sync_item_keys", sinceTs, untilTs);
	}
	
	/**
	 * Get all changed items 
	 * @param sinceTs Date can be null
	 * @param untilTs Date can be null
	 * @return int[] array of keys
	 * @throws FamundoException
	 */
	public int[] getUpdatedSyncItemKeys(Date sinceTs,Date untilTs) throws FamundoException {
		return getKeys("get_updated_sync_item_keys", sinceTs, untilTs);
	}
	
	/**
	 * Get all deleted items 
	 * @param sinceTs Date can be null
	 * @param untilTs Date can be null
	 * @return int[] array of keys
	 * @throws FamundoException
	 */
	public int[] getDeletedSyncItemKeys(Date sinceTs,Date untilTs) throws FamundoException {
		return getKeys("get_deleted_sync_item_keys", sinceTs, untilTs);
	}
	
	public int[] getSyncItemKeysFromTwin(byte[] data) throws FamundoException {
		return FXMLUtil.parseKeys( postXML(FUtil.buildPath(objType, "get_sync_item_keys_from_twin"),data) );
	}
	
	/**
	 * get the SIF XML of a famundo item of the current type
	 * @param id int the items id
	 * @return byte[] the items SIF XML
	 * @throws FamundoException
	 */
	public byte[] getSyncItemFromId(int id) throws FamundoException {
		return getXML(FUtil.buildPath(objType, "get_sync_item_from_id", id));
	}
	
	/**
	 * Create a new item of the current type
	 * @param data byte[] the items SIF XML
	 * @return int the new items id
	 * @throws FamundoException
	 */
	public int addSyncItem(byte[] data, Date timestamp) throws FamundoException {
		String[] params = { FUtil.date2urlParam("timestamp", timestamp) };
		String path = FUtil.buildPath(objType, "add_sync_item", null, params);
		byte[] bxml = postXML(path, data); 
		return FXMLUtil.parseUid(bxml);
	}


	/**
	 * Update a item of the current type 
	 * @param id int the items id
	 * @param data byte[] the items new SIF XML
	 * @throws FamundoException
	 */
	public void updateSyncItem(int id, byte[] data, Date timestamp) throws FamundoException {
		String[] params = { FUtil.date2urlParam("timestamp", timestamp) };
		String path = FUtil.buildPath(objType, "update_sync_item", new Integer(id), params);
		putXML(path, data); 
	}

	/**
	 * Update a item of the current type<br> 
	 * the items id is parsed from the SIF XML
	 * @param data byte[] the items new SIF XML 
	 * @throws FamundoException
	 */
	public void updateSyncItem(byte[] data, Date timestamp) throws FamundoException {
		int id = FXMLUtil.parseUid(data);
		updateSyncItem(id, data, timestamp);
	}
	
	/**
	 * Delete an item from the server 
	 * @param id int the items id
	 * @throws FamundoException
	 */
	public void removeSyncItem(int id) throws FamundoException {
		FHttpUtil.sendBytes(family, FUtil.buildPath(objType, "remove_sync_item", id), "DELETE", MIME_XML, null, null);
	}
	
	private byte[] getXML(String path) throws FamundoException {
		return FHttpUtil.sendBytes(family, path, "GET", MIME_XML, null, null);
	}

	private byte[] putXML(String path, byte[] data) throws FamundoException {
		return sendReciveXML(path, "PUT", data);
	}
	
	private byte[] postXML(String path, byte[] data) throws FamundoException {
		return sendReciveXML(path, "POST", data);
	}
	
	private byte[] sendReciveXML(String path, String verb, byte[] data) throws FamundoException {
		return FHttpUtil.sendBytes(family, path, verb, MIME_XML, MIME_XML, data);
	}
	
	private int[] getKeys(String path) throws FamundoException {
		return FXMLUtil.parseKeys(getXML(FUtil.buildPath(objType, path)));
	}
	
	private int[] getKeys(String path, Date sinceTs,Date untilTs) throws FamundoException {
		String[] params = { 
				FUtil.date2urlParam("since", sinceTs),
				FUtil.date2urlParam("until", untilTs)
		};
		return FXMLUtil.parseKeys(getXML(FUtil.buildPath(objType, path, params)));
	}

}
