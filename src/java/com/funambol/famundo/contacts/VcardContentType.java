package com.funambol.famundo.contacts;

import java.io.ByteArrayInputStream;

import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.converter.ContactToSIFC;
import com.funambol.common.pim.converter.ContactToVcard;
import com.funambol.common.pim.sif.SIFCParser;
import com.funambol.common.pim.vcard.VcardParser;
import com.funambol.famundo.util.FamundoContentType;
import com.funambol.famundo.util.FamundoContext;
import com.funambol.foundation.util.Def;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

public class VcardContentType extends FamundoContentType {
	public static final long serialVersionUID = 1;
	
	protected static final FunambolLogger _log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);
	
	public VcardContentType()
	{
		super("VCard", "text/x-vcard", "2.1");
	}
	
	public byte[] convertToMain(byte[] data, FamundoContext ctx)
	throws SyncSourceException
	{
		return contact2Sif(vcard2Contact(data, ctx), ctx).getBytes();
	}
	
	public byte[] convertFromMain(byte[] data, FamundoContext ctx)
	throws SyncSourceException
	{
		return contact2Vcard(sif2Contact(data), ctx).getBytes();
	}
	
	private Contact vcard2Contact(byte[] data, FamundoContext ctx)
	throws SyncSourceException
	{
        ByteArrayInputStream buffer = null;
        VcardParser parser = null;
        Contact contact = null;
        try
        {
            contact = new Contact();

            buffer = new ByteArrayInputStream(data);
            if (data.length > 0)
            {
                parser = new VcardParser(buffer, ctx.getDeviceTimezoneDescr(), ctx.getDeviceCharset());
                contact = (Contact) parser.vCard();
            }
            else
            {
                throw new SyncSourceException("No data");
            }
        }
        catch (SyncSourceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
        	_log.info("Bad format: " + new String(data));
            throw new SyncSourceException("Error converting VCARD to Contact.", e);
        }

        return contact;		
	}
	
	private String contact2Sif(Contact contact, FamundoContext ctx)
	throws SyncSourceException
	{
        String sifc = null;
        try
        {
        	ContactToSIFC c2sifc = new ContactToSIFC(ctx.getDeviceTimezone(), ctx.getDeviceCharset());
        	sifc = c2sifc.convert(contact);
        }
        catch (Exception e)
        {
        	throw new SyncSourceException(e.toString());
        }
        
        return sifc;		
	}
	
	private Contact sif2Contact(byte[] data)
	throws SyncSourceException
	{
		ByteArrayInputStream buffer = null;
		SIFCParser parser = null;
		Contact contact = null;
		try
		{
			contact = new Contact();
			buffer = new ByteArrayInputStream(data);
			if (data.length > 0)
			{
				parser = new SIFCParser(buffer);
				contact = (Contact) parser.parse();
			}
			else
			{
				throw new SyncSourceException("No data");
			}
		}
		catch (SyncSourceException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			_log.info("Bad format: " + new String(data));
			throw new SyncSourceException("Error converting SIF-C to Contact.", e);
		}

		return contact;
	}
	
	private String contact2Vcard(Contact contact, FamundoContext ctx)
	throws SyncSourceException
	{
        String vcard = null;
        try
        {
            ContactToVcard c2vc = new ContactToVcard(ctx.getDeviceTimezone(), ctx.getDeviceCharset());
            vcard = c2vc.convert(contact);
        }
        catch (Exception e)
        {
        	throw new SyncSourceException(e.toString());
        }
        
        return vcard;		
	}	
}
