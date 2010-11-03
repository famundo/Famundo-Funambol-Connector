package com.funambol.famundo.calendar;

import com.funambol.famundo.util.FamundoContentType;

public class ICalContentType extends FamundoContentType {
	public static final long serialVersionUID = 1;
	
	public ICalContentType()
	{
		super("ICal", "text/calendar", "2.0", true);
	}
}