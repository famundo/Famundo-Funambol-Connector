package com.funambol.famundo.auth;

import java.io.Serializable;
import java.security.Principal;

import com.funambol.famundo.util.FamundoContext;
import com.funambol.famundo.util.HttpRequest;
import com.funambol.famundo.util.Identity;
import com.funambol.famundo.util.Module;
import com.funambol.framework.core.Authentication;
import com.funambol.framework.core.Cred;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.server.admin.UserManager;
import com.funambol.server.config.Configuration;
import com.funambol.server.security.DBOfficer;

public class FamundoOfficer extends DBOfficer implements LazyInitBean, Serializable {
	public static final long serialVersionUID = 2;
	
	private String domain = null;
	public String getDomain()
	{
		return domain;
	}
	
	public void setDomain(String domain)
	{
		this.domain = domain;
	}
	
	private String clientAuth = Cred.AUTH_TYPE_BASIC;
	public String getClientAuth()
	{
		return this.clientAuth;
	}

	private String serverAuth = Cred.AUTH_NONE;
	public String getServerAuth()
	{
		return this.serverAuth;
	}

	public void setServerAuth(String serverAuth)
	{
		this.serverAuth = serverAuth;
	}

	// ------------------------------------------------------------ Constructors
	public FamundoOfficer() {
		super();
	}

	// ---------------------------------------------------------- Public methods

	public void init() {
		super.init();
	}

	public Sync4jUser authenticateUser(Cred credential) {

		Configuration config = Configuration.getConfiguration();
		ps = config.getStore();

		userManager = (UserManager)config.getUserManager();

		String type = credential.getType();
		log.info("FamundoAUTH: type=" + type);

		if ((Cred.AUTH_TYPE_BASIC).equals(type))
			return authenticateBasicCredential(credential);

		return null;
	}

	public AuthStatus authorize(Principal principal, String resource)
	{
		return AuthStatus.AUTHORIZED;
	}

	public void unAuthenticate(Cred credential)
	{
	}

	public boolean isAccountExpired()
	{
		return false;
	}

	protected Sync4jUser authenticateBasicCredential(Cred credential)
	{
		String username = null, password = null;
		Sync4jPrincipal principal = null;

		Authentication auth = credential.getAuthentication();
		String deviceId = auth.getDeviceId();

		String userpwd = new String(Base64.decode(auth.getData()));

		int p = userpwd.indexOf(':');
		if (p == -1)
		{
			username = userpwd;
			password = "";
		}
		else
		{
			username = (p > 0) ? userpwd.substring(0, p) : "";
			password = (p == (userpwd.length() - 1)) ? "" :
				userpwd.substring(p + 1);
		}
		
		log.info("FamundoAUTH: username=" + username);

		Sync4jUser user = getUser(username, null);
		if (user == null)
		{
			log.info("FamundoAUTH: local user not found, trying Famundo");
			try
			{
		    	String[] parts = username.trim().split(" ");
		    	if (parts.length < 2)
		    		return null;
		    	Identity identity = new Identity(parts[0], parts[1], password);
				Module module = new Module("contacts");
				FamundoContext ctx = new FamundoContext(identity, module, domain, null);
				HttpRequest request = ctx.createRequest("/api/valid");
				log.info("FamundoAUTH: Famundo request: " + request);
		    	request.run();
			}
			catch (Exception e)
			{
				return null;
			}

			user = new Sync4jUser();
			user.setUsername(username);
			user.setPassword(password);
			user.setRoles(new String[] {ROLE_USER});
		}
		else
		{
			log.info("FamundoAUTH: local user found, checking password and role");
			String storedPassword = user.getPassword();
			if (!password.equals(storedPassword))
				return null;

			if (!isASyncUser(user))
				return null;
		}

		log.info("FamundoAUTH: creating principal if it is not existing");
		try
		{
			principal = handlePrincipal(username, deviceId);
		}
		catch (PersistentStoreException e)
		{
			return null;
		}

		credential.getAuthentication().setPrincipalId(principal.getId());

		log.info("FamundoAUTH: all ok");
		return user;
	}

	protected Sync4jPrincipal getPrincipal(String userName, String deviceId)
	throws PersistentStoreException
	{
		Sync4jPrincipal principal = null;
		principal = Sync4jPrincipal.createPrincipal(userName, deviceId);
		try
		{
			ps.read(principal);
		}
		catch (NotFoundException ex)
		{
			return null;
		}

		return principal;
	}

	protected Sync4jPrincipal insertPrincipal(String userName, String deviceId)
	throws PersistentStoreException
	{
		Sync4jPrincipal principal = Sync4jPrincipal.createPrincipal(userName, deviceId);
		ps.store(principal);
		return principal;
	}

	protected Sync4jPrincipal handlePrincipal(String username, String deviceId)
	throws PersistentStoreException
	{
		Sync4jPrincipal principal = null;
		principal = getPrincipal(username, deviceId);
		if (principal == null)
			principal = insertPrincipal(username, deviceId);
		return principal;
	}
}
