package com.funambol.famundo.util;

import java.util.ArrayList;

import com.funambol.framework.engine.SyncItemKey;

public class QualifiedKeyExtractor extends KeyExtractor {
	private FamundoContext _ctx;
	private ArrayList _keys = new ArrayList();
	
	public SyncItemKey[] getKeys()
	{
		return (SyncItemKey[])_keys.toArray(new SyncItemKey[0]);
	}
	
	public QualifiedKeyExtractor(FamundoContext ctx)
	{
		_ctx = ctx;
	}
	
	protected void addKey(String key)
	{
		_keys.add(new SyncItemKey(_ctx.getQualifiedKey(key)));
	}
}
