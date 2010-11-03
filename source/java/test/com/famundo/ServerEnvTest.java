package com.famundo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class ServerEnvTest extends TestCase {
	private ServerEnv currentEnv = null;
	
    //@Before
    public void setUp() {
    	if(ServerEnv.get() != ServerEnv.DUDI_SERVER) {
    		currentEnv = ServerEnv.get();
    		ServerEnv.set(ServerEnv.DUDI_SERVER);
    	}
    }

    //@After
    public void tearDown() {
    	if(currentEnv != null)
    		ServerEnv.set(currentEnv);
    }
	
	//@Test
	public void testServerEnv() {
		assertEquals(ServerEnv.DUDI_SERVER, ServerEnv.get() );
		assertEquals("http://<FAMILY_NAME>.yanai.loc:3000/", ServerEnv.getCurrentTemplate() );
		assertEquals("yanai.loc:3000", ServerEnv.getDomain() );
		assertEquals("koko", ServerEnv.parseFamilyName(" http://koko.yanai.loc:3000/moko") );
	}

}
