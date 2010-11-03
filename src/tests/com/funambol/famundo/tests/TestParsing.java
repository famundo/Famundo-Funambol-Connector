package com.funambol.famundo.tests;

import com.funambol.famundo.calendar.SifeContentType;
import com.funambol.famundo.calendar.VCalContentType;
import com.funambol.famundo.util.FamundoContext;
import com.funambol.famundo.util.Identity;
import com.funambol.famundo.util.Module;
import com.funambol.framework.engine.source.SyncSourceException;

import junit.framework.TestCase;

public class TestParsing extends TestCase {
	private FamundoContext _ctx;
	
	public void setUp()
	throws SyncSourceException
	{
		Identity identity = new Identity("family1", "mikedoe", "demo");
		Module module = new Module("calendar");
		_ctx = new FamundoContext(identity, module, ".famtest.com", null);
	}
	
	public void testVCal()
	throws SyncSourceException
	{
		VCalContentType vcal = new VCalContentType();
		byte[] result = vcal.convertFromMain(getTestData(), _ctx);
		byte[] ical = vcal.convertToMain(result, _ctx);
		System.out.println(new String(ical));
	}
	
	public void testOutlook()
	throws SyncSourceException
	{
		SifeContentType sife = new SifeContentType();
		byte[] result = sife.convertFromMain(getAlldayTestData(), _ctx);
		System.out.println(new String(result));
	}
	
	private static byte[] getTestData()
	{
		String result = "BEGIN:VCALENDAR\n" +
		"VERSION:2.0\n" +
		"METHOD:PUBLISH\n" +
		"PRODID:-//Famundo LLC//Famundo.com//EN\n" +
		"CALSCALE:Gregorian\n" +
		"BEGIN:VEVENT\n" +
		"DTSTART:20070821T074500Z\n" +
		"DTSTAMP:20070831T174843Z\n" +
		"SEQUENCE:0\n" +
		"UID:3b7c76c4f0c48cb3498297ad5a784091a791faef@famundo.com\n" +
		"SUMMARY:Morning Carpool\n" +
		"DTEND:20070821T081500Z\n" +
		"END:VEVENT\n" +
		"END:VCALENDAR\n";
		return result.getBytes();
	}
	
	private static byte[] getAlldayTestData()
	{
		String result = "BEGIN:VCALENDAR\n" +
		"VERSION:2.0\n" +
		"METHOD:PUBLISH\n" +
		"PRODID:-//Famundo LLC//Famundo.com//EN\n" +
		"CALSCALE:Gregorian\n" +
		"BEGIN:VEVENT\n" +
		"DTSTART;VALUE=DATE:20070901\n" +
		"DTSTAMP:20070902T114928Z\n" +
		"SEQUENCE:0\n" +
		"UID:3b7c76c4f0c48cb3498297ad5a784091a791faef@famundo.com\n" +
		"SUMMARY:Morning Carpool\n" +
		"DTEND;VALUE=DATE:20070902\n" +
//		"DTEND:20070902\n" +
		"END:VEVENT\n" +
		"END:VCALENDAR\n";
		return result.getBytes();
	}	
}
