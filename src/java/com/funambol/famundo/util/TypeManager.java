package com.funambol.famundo.util;

import java.util.ArrayList;

import com.funambol.famundo.calendar.ICalContentType;
import com.funambol.famundo.calendar.SifeContentType;
import com.funambol.famundo.calendar.SiftContentType;
import com.funambol.famundo.calendar.VCalContentType;
import com.funambol.famundo.contacts.SifcContentType;
import com.funambol.famundo.contacts.VcardContentType;

public class TypeManager {
	private ArrayList _types = new ArrayList();
	
	public ArrayList getTypes()
	{
		return _types;
	}
	
	public TypeManager()
	{
	}
	
	public void addContentType(FamundoContentType type)
	{
		_types.add(type);
	}
	
	public static TypeManager createContactsManager()
	{
		TypeManager manager = new TypeManager();
		manager.addContentType(new SifcContentType());
		manager.addContentType(new VcardContentType());
		return manager;
	}
	
	public static TypeManager createCalendarManager()
	{
		TypeManager manager = new TypeManager();
		manager.addContentType(new ICalContentType());
		manager.addContentType(new VCalContentType());
		manager.addContentType(new SifeContentType());
		manager.addContentType(new SiftContentType());
		return manager;
	}	
}
