package com.famundo;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;

public class FamundoAPITest extends TestCase {

	FamundoAPI api = null;
	Date syncBeginAt = null;
	
    //@Before
    public void setUp() {
    	api = new FamundoAPI(new Family("family1", "mikedoe", "demo"), FumundoObjectType.CONTACT);
		
    	Calendar calendar = Calendar.getInstance();
    	syncBeginAt = new Date(calendar.getTimeInMillis() + calendar.get(Calendar.ZONE_OFFSET) - 1*60*1000);
    	
    }

    //@After
    public void tearDown() {
    	api = null;
    }
    
    /*
    @Test
    public void testGetXMLb() {
		try {
			byte[] bxml = api.getXML("/api/contact/get_sync_item_from_id/1");
			//System.out.println( new String(bxml, "UTF-8") );
			assertTrue(true);
		} catch (Exception e) {
			//e.printStackTrace();
			fail(e.toString());
		}
    }
    */
    
    //@Test
    public void testCanSync() {
    	try {
    		api.canSync();
		} catch (Exception e) {
			fail("can sync should not have throw an exception");
		}
		try {
			FamundoAPI api2 = new FamundoAPI(new Family("koko","shok", "toko"), FumundoObjectType.CONTACT);
			api2.canSync();
			fail("can sync should have throw an exception");
		} catch (Exception e) {
		}
    }
    
    //@Test
    public void testGetAllSyncItemKeys() {
		try {
			int keys[] = api.getAllSyncItemKeys();
			//for (int i = 0; i < keys.length; i++) { System.out.println( keys[i] ); }
			assertTrue(keys.length > 0);
		} catch (Exception e) {
			//e.printStackTrace();
			fail(e.toString());
		}
    }
    
    //@Test
    public void testGetUpdatedSyncItemKeys() throws FamundoException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2007, 0, 15, 0, 0, 0); //Jan is 0
		Date sinceTs = calendar.getTime();
		calendar.set(2007, 0, 21, 0, 0, 0); //feb is 1
		Date untilTs = calendar.getTime();
    	
    	int[] keys = api.getUpdatedSyncItemKeys(sinceTs,untilTs);
    	dumpKeys(keys, false);
    }
    
    //@Test
    public void testGetDeletedSyncItemKeys() throws FamundoException {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2007, 0, 15, 0, 0, 0); //Jan is 0
		Date sinceTs = calendar.getTime();
		calendar.set(2007, 0, 17, 0, 0, 0); //feb is 1
		Date untilTs = calendar.getTime();
    	
    	int[] keys = api.getDeletedSyncItemKeys(sinceTs,untilTs);
    	dumpKeys(keys, false);
    }
    
    //@Test
    public void testGetSyncItemFromId() throws FamundoException {
    	byte[] bxml = api.getSyncItemFromId(1);
    	dumpXml(bxml, false);
    }

    //@Test
    public void testAddSyncItem() throws FamundoException, UnsupportedEncodingException {
    	int id = api.addSyncItem(sifc1.getBytes("UTF-8"), syncBeginAt);
    	String uid = api.id2path(id);
    	System.out.println( "@@@@ new UID = " +  uid );
    	
    	int[] twins = api.getSyncItemKeysFromTwin(sifc1.getBytes("UTF-8"));
    	assertEquals(1, twins.length);
    	assertEquals(id, twins[0]);
    	
    	
    	byte[] bAppendXML = FXMLUtil.setObjectUid(sifc2.getBytes("UTF-8"), api.getObjType().getXmlRoot(), uid);
    	api.updateSyncItem(bAppendXML, syncBeginAt);
    	api.removeSyncItem(id);
    }
    
    
    private void dumpKeys(int[] keys, boolean bDump) {
    	if(bDump)
    		for (int i = 0; i < keys.length; i++) { System.out.println( keys[i] ); }
    }

    private void dumpXml(byte[] bxml, boolean bDump) {
    	try {
    		if(bDump)
    			System.out.println( new String(bxml, "UTF-8") );
		} catch (Exception e) {
		}
    }
    
    String sifc1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    			   "<contact>" +
    					"<FirstName>Bugs</FirstName>" +
    					"<LastName>Bunny</LastName>" +
						"<BusinessTelephoneNumber>+777 77777 7777</BusinessTelephoneNumber>" +
						"<HomeTelephoneNumber>+999 999 99 9999 </HomeTelephoneNumber>" +
						"<HomeAddressStreet>my dudi Street</HomeAddressStreet>" +
						"<Birthday>1964-09-21</Birthday>" +
						"<Anniversary>1990-10-10</Anniversary>" +
						"<PrimaryTelephoneNumber>111 111 11 1111</PrimaryTelephoneNumber>" +
						"<BusinessAddressCountry>USA</BusinessAddressCountry>" +
						"<BusinessAddressPostalCode>1234</BusinessAddressPostalCode>" +
						"<BusinessAddressState>CA</BusinessAddressState>" +
						"<BusinessAddressStreet>223 Business St</BusinessAddressStreet>" +
						"<BusinessAddressCity>LA</BusinessAddressCity>" +
					"</contact>";

    String sifc2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				   "<contact>" +
				   		"<Uid>family1/contact/20</Uid>" +
						"<FirstName>Bugsi</FirstName>" +
						//"<LastName>Bunny</LastName>" +
						//"<BusinessTelephoneNumber>+777 77777 7777</BusinessTelephoneNumber>" +
						"<HomeTelephoneNumber>+333 333 33 3333</HomeTelephoneNumber>" +
						"<HomeAddressStreet/>" +
						"<Birthday>1967-01-07</Birthday>" +
						"<Anniversary/>" +
						//"<PrimaryTelephoneNumber>111 111 11 1111</PrimaryTelephoneNumber>" +
						//"<BusinessAddressCountry>USA</BusinessAddressCountry>" +
						//"<BusinessAddressPostalCode>1234</BusinessAddressPostalCode>" +
						//"<BusinessAddressState>CA</BusinessAddressState>" +
						//"<BusinessAddressStreet>223 Business St</BusinessAddressStreet>" +
						//"<BusinessAddressCity>LA</BusinessAddressCity>" +
					"</contact>";

}
