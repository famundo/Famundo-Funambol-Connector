package com.funambol.famundo.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.funambol.famundo.contacts.SifManager;
import com.funambol.famundo.contacts.SifcContentType;
import com.funambol.famundo.util.ConnectorException;
import com.funambol.famundo.util.FamundoContentType;
import com.funambol.famundo.util.FamundoContext;
import com.funambol.famundo.util.HttpRequest;
import com.funambol.famundo.util.Identity;
import com.funambol.famundo.util.Module;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncSourceException;

import junit.framework.TestCase;

public class TestHttp extends TestCase {
	private FamundoContext _ctx;
	private Calendar _calendar = Calendar.getInstance(TimeZone.getDefault());
	private FamundoContentType _contentType;
	
	public void setUp()
	throws SyncSourceException
	{
		Identity identity = new Identity("family1", "mikedoe", "demo");
		Module module = new Module("calendar");
		_ctx = new FamundoContext(identity, module, ".famtest.com", null);
		_contentType = new SifcContentType();
	}
	
	public void testHttpGetRequest()
	throws SyncSourceException
	{
		HttpRequest r = _ctx.createRequest("/api/calendar_syncml/get_sync_item_from_id/t2");
		r.addParameter("type", "todo");
		System.out.println("Request: " + r);
		byte[] result = _ctx.runRequest(r);
		System.out.println(new String(result));
	}
//
//	public void testGetAllKeys()
//	throws SyncSourceException
//	{
//    	SyncItemKey[] r = _ctx.fetchKeys(_ctx.createRequest("/api/contact/get_all_sync_item_keys"));
//		System.out.println(r.length);
//	}

//	public void testGetDeletedKeys()
//	throws SyncSourceException
//	{
//    	HttpRequest request = _ctx.createRequest("/api/contact/get_new_sync_item_keys");
//    	_calendar.set(2001, 3, 17, 17, 33, 21);
//    	Date since = _calendar.getTime();
//    	_calendar.set(2008, 3, 17, 17, 33, 21);
//    	Date until = _calendar.getTime();
//    	_ctx.addDateConstraints(request, since, until);
//    	System.out.println(request);
//    	SyncItemKey[] r = _ctx.fetchKeys(request);
//    	System.out.println(r.length);
//	}
	
/*	public void testAddSyncItem()
	throws SyncSourceException
	{
		String sifc1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<contact>" +
		"<FirstName>Test</FirstName>" +
		"<LastName>User</LastName>" +
		"<BusinessTelephoneNumber>+777 77777 7777</BusinessTelephoneNumber>" +
		"<HomeTelephoneNumber>+999 999 99 9999 </HomeTelephoneNumber>" +
		"<HomeAddressStreet>Test Street</HomeAddressStreet>" +
		"<Birthday>1977-02-12</Birthday>" +
		"<Anniversary>1992-03-01</Anniversary>" +
		"<PrimaryTelephoneNumber>111 111 11 1111</PrimaryTelephoneNumber>" +
		"<BusinessAddressCountry>USA</BusinessAddressCountry>" +
		"<BusinessAddressPostalCode>1234</BusinessAddressPostalCode>" +
		"<BusinessAddressState>CA</BusinessAddressState>" +
		"<BusinessAddressStreet>223 Business St</BusinessAddressStreet>" +
		"<BusinessAddressCity>LA</BusinessAddressCity>" +
		"</contact>";
	   
    	HttpRequest request = _ctx.createRequest("/api/contact/add_sync_item", "POST");
    	_ctx.addDateConstraint(request, "timestamp", new Date());
    	System.out.println(request);
    	byte[] sifData = _contentType.convertToMain(sifc1.getBytes(), _ctx);
    	request.setData(sifData);
    	String qualifiedKey = _ctx.fetchSingleQualifiedKey(request);
    	SifManager sif = new SifManager(sifData);
    	sif.updateId(qualifiedKey);
	}	
*/}
