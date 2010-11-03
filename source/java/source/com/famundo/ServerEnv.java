package com.famundo;
/**
 * Enum of server info<br>
 * the default server is loaded when the calss is first used.<br> 
 * acroding to the FAMUNDO_SERVER_TYPE<br/>
 * Example to point to the famtest server use:<br>
 * {@code export FAMUNDO_SERVER_TYPE=TEST_SERVER }
 * @author dudi
 *
 */
public class ServerEnv {
	/**
	 * the servers URL template
	 */
	private String template;
	
	/**
	 * the servers id to use when we look at the FAMUNDO_SERVER_TYPE enviroment
	 */
	private String envName;
	
	public String getEnvName() { return envName; }
	public String getTemplate() { return template; }
	
	private ServerEnv(String template, String envName) {
		this.template = template;
		this.envName = envName;
	}
		
	public static final ServerEnv DUDI_SERVER       
		= new ServerEnv("http://<FAMILY_NAME>.yanai.loc:3000/"  , "DUDI_TEST");
	
	public static final ServerEnv TEST_SERVER       
		= new ServerEnv("http://<FAMILY_NAME>.famtest.com/"    , "TEST_SERVER");
	
	public static final ServerEnv PRODUCTION_SERVER 
		= new ServerEnv("https://<FAMILY_NAME>.famundo.com/"    , "PRODUCTION_SERVER");
	
	/**
	 * current server to use
	 */
	static private ServerEnv serverEnv = initServerEnv();
	
	/**
	 * 
	 * @return current server enum
	 */
	static public ServerEnv get() {
		return serverEnv;
	}
	
	/**
	 * Set the current server
	 * @param se
	 */
	static public void set(ServerEnv se) {
		serverEnv = se;
	}

	/**
	 * Get the enums famundo server template
	 * @return String current famundo server URL
	 */
	static public String getCurrentTemplate() {
		return serverEnv.template;
	}

	/**
	 * @return String current server domain name (e.g "famundo.com")
	 */
	static public String getDomain() {
		return FUtil.getFamilyUrlTemplateDomain(serverEnv.template);
	}
	
	/**
	 * Extract the family name from a string<br/>
	 * this will get the family name from a full or partial family URL
	 * @param family some form of the family URL
	 * @return String
	 */
	static public String parseFamilyName(String family) {
		return FUtil.parseFamilyName(family, getCurrentTemplate());
	}
	
	
	/**
	 * read enviroment value of FAMUNDO_SERVER_TYPE and return the enum it maches
	 * @return enum ServerEnv
	 */
	static private ServerEnv initServerEnv() {
		
		String env = System.getenv("FAMUNDO_SERVER_TYPE");
		if( !FUtil.isBlank(env))
		{
			ServerEnv[] srva = {DUDI_SERVER, TEST_SERVER, PRODUCTION_SERVER };
			for (int i = 0; i < srva.length; i++) {
				if(srva[i].getEnvName().equals(env))
					return srva[i];
			}
		}
		return DUDI_SERVER; // default
	}
};
