package com.funambol.famundo.util;

public class Identity {
	private String _family;
	private String _user;
	private String _password;
	
	public Identity()
	{
	}
	
	public Identity(String family, String user, String password)
	{
		_family = family;
		_user = user;
		_password = password;
	}
	
	public String getFamily() {
		return _family;
	}
	public void setFamily(String family) {
		this._family = family;
	}
	public String getPassword() {
		return _password;
	}
	public void setPassword(String password) {
		this._password = password;
	}
	public String getUser() {
		return _user;
	}
	public void setUser(String user) {
		this._user = user;
	}
}
