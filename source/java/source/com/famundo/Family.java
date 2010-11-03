package com.famundo;

/**
 * Holds the famundo family info
 * @author dudi
 *
 */
public class Family {
	private String family;
	private String user;
	private String password;
	
	public Family() {
		this.family   = "";
		this.user     = "";
		this.password = "";
	}

	public Family(Family fo) {
		this.family   = fo.family;
		this.user     = fo.user;
		this.password = fo.password;
	}
	
	public Family(String family , String user, String password) {
		setFamily(family);
		setUser(user);
		setPassword(password);
	}
	
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = ServerEnv.parseFamilyName(family);
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = FUtil.isBlank(password) ? "": password;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = FUtil.isBlank(user) ? "": user;
	}

	public String url() throws FamundoException {
		if( FUtil.isBlank(family) )
			throw new FamundoException("Family is blank");
		return ServerEnv.getCurrentTemplate().replace("<FAMILY_NAME>", family);
	}
	
	public boolean isValid() {
		return !FUtil.isBlank(family) && !FUtil.isBlank(user) && !FUtil.isBlank(password);
	}
	
	public void validate() throws FamundoException {
		if( !isValid())
			throw new FamundoException("Family info is invalid");
	}
}
