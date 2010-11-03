package com.famundo;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;

import org.junit.Test;
import junit.framework.TestCase;
import com.famundo.FUtil;

public class FUtilTest extends TestCase {
	
	//@Test
	public void testIsBlank() {
		assertTrue(FUtil.isBlank("   ") );
		assertTrue(FUtil.isBlank(" \r\n\t  ") );
		assertFalse(FUtil.isBlank(" k 0 k o  ") );
	}

	//@Test
	public void testGetFamilyUrlTemplateDomain() {
		assertEquals("famundo.com",  FUtil.getFamilyUrlTemplateDomain("https://<FAMILY_NAME>.famundo.com/") );
		assertEquals("yanai.loc:3000", FUtil.getFamilyUrlTemplateDomain("http://<FAMILY_NAME>.yanai.loc:3000/") );
	}
	
	//@Test
	public void testParseFamilyName() {
		String templates[] = {"https://<FAMILY_NAME>.famundo.com/", "http://<FAMILY_NAME>.yanai.loc:3000/"};
		for (int i = 0; i < templates.length; i++) {
			String template = templates[i];
			assertEquals("", FUtil.parseFamilyName("  ", template) );
			assertEquals("", FUtil.parseFamilyName(null, template) );
			assertEquals("koko", FUtil.parseFamilyName(" kOko", template) );
		}
		assertEquals("koko", FUtil.parseFamilyName(" https://koKo.Famundo.Com/path ", "https://<FAMILY_NAME>.famundo.com/") );
		assertEquals("koko", FUtil.parseFamilyName(" https://koKo.Famundo.Com/ "    , "https://<FAMILY_NAME>.famundo.com/") );
		assertEquals("koko", FUtil.parseFamilyName(" https://koKo.Famundo.Com "     , "https://<FAMILY_NAME>.famundo.com/") );
		assertEquals("koko", FUtil.parseFamilyName(" koKo.Famundo.Com/path "        , "https://<FAMILY_NAME>.famundo.com/") );
		assertEquals("koko", FUtil.parseFamilyName(" koKo.Famundo.Com"              , "https://<FAMILY_NAME>.famundo.com/") );

		assertEquals("koko", FUtil.parseFamilyName(" https://koKo.yanai.loc:3000/path ", "http://<FAMILY_NAME>.yanai.loc:3000/") );
		assertEquals("koko", FUtil.parseFamilyName(" https://koKo.yanai.loc:3000/ "    , "http://<FAMILY_NAME>.yanai.loc:3000/") );
		assertEquals("koko", FUtil.parseFamilyName(" https://koKo.yanai.loc:3000 "     , "http://<FAMILY_NAME>.yanai.loc:3000/") );
		assertEquals("koko", FUtil.parseFamilyName(" koKo.yanai.loc:3000/path "        , "http://<FAMILY_NAME>.yanai.loc:3000/") );
		assertEquals("koko", FUtil.parseFamilyName(" koKo.yanai.loc:3000"              , "http://<FAMILY_NAME>.yanai.loc:3000/") );
	}
	
	//@Test
	public void testParseKeys() throws UnsupportedEncodingException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><keys><Uid>01</Uid><Uid>0022</Uid><Uid>000333</Uid></keys>";
		int keys[] = FXMLUtil.parseKeys(xml.getBytes("UTF-8"));
		assertEquals(3, keys.length);
		assertEquals(keys[0], 1);
		assertEquals(keys[1], 22);
		assertEquals(keys[2], 333);
	}
	
	//@Test 
	public void testParseContactUid() throws UnsupportedEncodingException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><contact><Uid>012345</Uid></contact>";
		int uid = FXMLUtil.parseUid(xml.getBytes("UTF-8"));
		assertEquals(12345,uid);
	}

	
	//@Test
	public void testDate2urlParam() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2006, 0, 15, 11, 22, 33); //Jan is 0
		Date dt = calendar.getTime();
		String p = FUtil.date2urlParam("koko", dt);
		//System.out.println(p);
		assertEquals("koko=2006-01-15+11:22:33", p);
	}
	
	//@Test
	public void testRemoveNulls() {
		String[] cmp = {"a", "b", "c", "d", "e"};
		String[] sa  = {null, "a", null, null, "b", "c", "d", null,null,"e", null, null, null,};
		String[] nsa = FUtil.removeNulls(sa);
		assertEquals(cmp.length, nsa.length);
		for (int i = 0; i < nsa.length; i++) {
			assertEquals(cmp[i], nsa[i]);
		}
	}
	
	/*
	@Test
	public void testAppend2urlPath() {
		String[] params1 = {"v1=a", "v2=b", "v3=c"};
		assertEquals("koko/moko?v1=a&v2=b&v3=c", FUtil.append2urlPath("koko/moko", params1 ) );
		String[] params2 = {null, "v1=a", "v2=b", null};
		assertEquals("koko/moko?v1=a&v2=b", FUtil.append2urlPath("koko/moko", params2 ) );
		String[] params3 = {null, "v1=a", null, null};
		assertEquals("koko/moko?v1=a", FUtil.append2urlPath("koko/moko", params3 ) );
		String[] params4 = {null, null, null, null};
		assertEquals("koko/moko", FUtil.append2urlPath("koko/moko", params4 ) );
	}
	*/
	
	//@Test
	public void testParseIdFromUid() {
		assertEquals(23, FUtil.parseIdFromUid(" family1/contact/23  "));
		assertEquals(23, FUtil.parseIdFromUid(" 23 "));
		assertEquals(-1, FUtil.parseIdFromUid(" koko "));
		assertEquals(-1, FUtil.parseIdFromUid("  "));
		assertEquals(-1, FUtil.parseIdFromUid(" koko23 "));
	}

	//@Test
	public void testSetObjectUid() throws UnsupportedEncodingException, FamundoException {
		String oldUid = "family1/contact/12345";
		byte[] bxml = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?><contact><Uid>" + oldUid + "</Uid></contact>").getBytes("UTF-8");
		assertEquals(oldUid, FXMLUtil.parseUidString(bxml));
		
		String newUid = "family1/contact/987";
		byte[] bxmlAfter = FXMLUtil.setObjectUid(bxml, "contact", newUid);
		assertEquals(newUid, FXMLUtil.parseUidString(bxmlAfter));
		
		bxml = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?><contact><FirstName>Bugs</FirstName></contact>").getBytes("UTF-8");
		bxmlAfter = FXMLUtil.setObjectUid(bxml, "contact", newUid);
		assertEquals(newUid, FXMLUtil.parseUidString(bxmlAfter));
		
		//System.out.println( new String(bxmlAfter, "UTF-8") );
	}
	
	//@Test 
	public void testBuildPath() {
		assertEquals("/api/contact/koko", FUtil.buildPath(FumundoObjectType.CONTACT, "koko"));
		assertEquals("/api/contact/koko/1", FUtil.buildPath(FumundoObjectType.CONTACT, "koko", 1));
		
		String[] parmas1 = {};
		assertEquals("/api/contact/koko", FUtil.buildPath(FumundoObjectType.CONTACT, "koko", parmas1));

		String[] parmas2 = {null, null, null };
		assertEquals("/api/contact/koko", FUtil.buildPath(FumundoObjectType.CONTACT, "koko", parmas2));
		
		String[] parmas3 = {null, "p1=v1", null, "p2=v2", null };
		assertEquals("/api/contact/koko?p1=v1&p2=v2", FUtil.buildPath(FumundoObjectType.CONTACT, "koko", parmas3));
		
		assertEquals("/api/contact/koko/123?p1=v1&p2=v2", FUtil.buildPath(FumundoObjectType.CONTACT, "koko", new Integer(123), parmas3));
	}

    //@Test
    public void testSetObjectUid2() throws FamundoException, UnsupportedEncodingException {
    	
    	byte[] bAppendXML = FXMLUtil.setObjectUid(sifc2.getBytes("UTF-8"), "contact", "family1/contact/1234");
    	dumpXml(bAppendXML, false);
    	
    	//System.out.println("javax.xml.transform.TransformerFactory : " + System.getProperty("javax.xml.transform.TransformerFactory"));
    	
    	//TransformerFactory tFactory = TransformerFactory.newInstance();
    	//System.out.println( "TransformerFactory Canonical Name " + tFactory.getClass().getCanonicalName() );
    	
    	//SAXParserFactory factory = SAXParserFactory.newInstance();
    	//System.out.println( "SAXParserFactory Canonical Name " + factory.getClass().getCanonicalName() );
    	
    	//System.out.println( "DocumentBuilderFactory Canonical Name " + DocumentBuilderFactory.newInstance().getClass().getCanonicalName() );
    }

    //@Test
	public void testTimeZoneConverstions() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+02:00"));
		calendar.set(2006, 0, 15, 12, 0, 0); //Jan is 0
		Date dt = calendar.getTime();
		Date dtUTC = FUtil.toUTC(dt);
		assertEquals(12, dt.getHours());
		assertEquals(10, dtUTC.getHours());
		/*
        StringBuilder sb = new StringBuilder("===testTimeZoneConverstions===");
        sb.append("\n> dt             : ").append(dt.getHours());
        sb.append("\n> dt.TZ Offset   : ").append(dt.getTimezoneOffset());
        sb.append("\n> dtUTC          : ").append(dtUTC.getHours());
        sb.append("\n> dtUTC.TZ Offset: ").append(dtUTC.getTimezoneOffset());
        System.out.println(sb.toString());
		*/
	}
    
/*    
    private void dumpKeys(int[] keys, boolean bDump) {
    	if(bDump)
    		for (int i = 0; i < keys.length; i++) { System.out.println( keys[i] ); }
    }
*/
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
