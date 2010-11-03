package com.funambol.famundo.util;


public class SingleQualifiedKeyExtractor extends KeyExtractor {
	private FamundoContext _ctx;
	private String _key;
	
	public String getKey()
	{
		return _key;
	}
	
	public SingleQualifiedKeyExtractor(FamundoContext ctx)
	{
		_ctx = ctx;
	}
	
	protected void addKey(String key)
	{
		_key = _ctx.getQualifiedKey(key);
	}
}
