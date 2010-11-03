package com.funambol.famundo.calendar;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.model.Parameter;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.model.VCalendarContent;

public final class FormatFixer {
	public static void FixCalendar(Calendar c)
	{
		// Make property to be of type Short.
		// There is a bug in Funambol converter
		Property p = c.getCalendarContent().getAccessClass();
		String classString = p.getPropertyValueAsString();
		if (classString != null)
			p.setPropertyValue(Short.valueOf(classString));
	}
	
	public static void FixAlldayDates(Calendar cal, VCalendar vcal)
	{
		CalendarContent cc = cal.getCalendarContent();
		//if (!(cc instanceof Event) || !cc.isAllDay())
		if (!cc.isAllDay())
			return;
		
		VCalendarContent content = vcal.getVCalendarContent();
		FixDateProperty(content, "DTSTART");
		FixDateProperty(content, "DTEND");
		FixDateProperty(content, "DUE");
		FixDateProperty(content, "EXDATE");
	}
	
	private static void FixDateProperty(VCalendarContent content, String propertyName)
	{
		com.funambol.common.pim.model.Property p = content.getProperty(propertyName);
		if (p != null)
		{
			p.setValue(p.getValue().replaceAll("-", ""));
			AddDateParameter(p);
		}
		
	}
	
	private static void AddDateParameter(com.funambol.common.pim.model.Property property)
	{
		Parameter p = new Parameter("VALUE", "DATE");
		property.addParameter(p);
	}
	
	public static void DropDateParameters(Calendar cal)
	{
		CalendarContent cc = cal.getCalendarContent();
		DropValueParameter(cc.getDtStart());
		DropValueParameter(cc.getDtEnd());
	}
	
	private static void DropValueParameter(Property property)
	{
		if ("DATE".equals(property.getValue()))
			property.setValue(null);
	}
}
