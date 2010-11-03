package com.funambol.famundo.util;

import java.io.Serializable;

import com.funambol.framework.engine.source.SyncSourceException;

public class FamundoContentType implements Serializable {
	public static final long serialVersionUID = 1;
	
	private String _name;
	private String _contentType;
	private String _version;
	private boolean _main;
	
	public String getContentType()
	{
		return _contentType;
	}

	public String getName()
	{
		return _name;
	}
	
	public String getVersion()
	{
		return _version;
	}

	public boolean isMain()
	{
		return _main;
	}

	public FamundoContentType(String name, String contentType, String version, boolean main)
	{
		_name = name;
		_contentType = contentType;
		_version = version;
		_main = main;
	}

	public FamundoContentType(String name, String contentType, String version)
	{
		this(name, contentType, version, false);
	}
	
	public byte[] convertToMain(byte[] data, FamundoContext ctx)
	throws SyncSourceException
	{
		return data;
	}
	
	public byte[] convertFromMain(byte[] data, FamundoContext ctx)
	throws SyncSourceException
	{
		return data;
	}
	
	public String toString()
	{
		return _name;
	}
	
	public boolean equals(Object that)
	{
		if (this == that)
			return true;
		if (!(that instanceof FamundoContentType)) return false;
		
		FamundoContentType type = (FamundoContentType)that;
		return this.getName().equals(type.getName());
	}
}
