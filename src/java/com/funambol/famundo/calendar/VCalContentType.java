package com.funambol.famundo.calendar;

import java.io.ByteArrayInputStream;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.converter.BaseConverter;
import com.funambol.common.pim.converter.VCalendarConverter;
import com.funambol.common.pim.converter.VComponentWriter;
import com.funambol.common.pim.icalendar.ICalendarParser;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.xvcalendar.XVCalendarParser;
import com.funambol.famundo.util.FamundoContentType;
import com.funambol.famundo.util.FamundoContext;
import com.funambol.foundation.util.Def;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

public class VCalContentType extends FamundoContentType {
	public static final long serialVersionUID = 1;
	
	protected static final FunambolLogger _log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);
	
	public VCalContentType()
	{
		super("VCal", "text/x-vcalendar", "1.0");
	}
	
	public byte[] convertToMain(byte[] data, FamundoContext ctx)
	throws SyncSourceException
	{
		return calendar2webcalendar(vcal2Calendar(data, ctx), ctx, false).getBytes();
	}
	
	public byte[] convertFromMain(byte[] data, FamundoContext ctx)
	throws SyncSourceException
	{
		return calendar2webcalendar(ical2Calendar(data, ctx), ctx, true).getBytes();
	}
	
	private Calendar vcal2Calendar(byte[] data, FamundoContext ctx)
	throws SyncSourceException
	{
        try
        {		
			ByteArrayInputStream buffer = new ByteArrayInputStream(data);
			
			XVCalendarParser parser = new XVCalendarParser(buffer);
			VCalendar vcalendar = (VCalendar)parser.XVCalendar();
			vcalendar.addProperty("VERSION", getVersion());
			
			VCalendarConverter vcf = new VCalendarConverter(ctx.getDeviceTimezone(), getCharset(ctx));
	        Calendar c = vcf.vcalendar2calendar(vcalendar);
	        
	        return c;
        }
        catch (Exception e)
        {
        	_log.info("Bad format: " + new String(data));
            throw new SyncSourceException("Error converting vCal to Calendar.", e);
        }        
	}
	
	private String calendar2webcalendar(Calendar calendar, FamundoContext ctx, boolean vcal)
	throws SyncSourceException
	{
		try
		{
	        VCalendarConverter vcf = new VCalendarConverter(ctx.getDeviceTimezone(), getCharset(ctx));
	        VCalendar vcalendar = vcf.calendar2vcalendar(calendar, vcal);
	        FormatFixer.FixAlldayDates(calendar, vcalendar);
	        
	        VComponentWriter writer = new VComponentWriter();
	        return writer.toString(vcalendar);
		}
        catch (Exception e)
        {
            throw new SyncSourceException("Error converting Calendar to WebCalendar.", e);
        }        
	}
	
	private Calendar ical2Calendar(byte[] data, FamundoContext ctx)
	throws SyncSourceException
	{
        try
        {		
			ByteArrayInputStream buffer = new ByteArrayInputStream(data);
			
            ICalendarParser parser = new ICalendarParser(buffer);
            VCalendar vcalendar = (VCalendar)parser.ICalendar();
			vcalendar.addProperty("VERSION", "2.0");
			
			VCalendarConverter vcf = new VCalendarConverter(ctx.getDeviceTimezone(), getCharset(ctx));
	        Calendar c = vcf.vcalendar2calendar(vcalendar);
	        
	        return c;
        }
        catch (Exception e)
        {
        	_log.info("Bad format: " + new String(data));
            throw new SyncSourceException("Error converting iCal to Calendar.", e);
        }        
	}
	
	private String getCharset(FamundoContext ctx)
	{
		String charset = BaseConverter.CHARSET_UTF7;
		if (ctx.getDeviceCharset() != null)
			charset = ctx.getDeviceCharset();
		
		return charset;
	}
}
