package com.famundo;

import org.junit.Test;

import junit.framework.TestCase;

public class FamilyTest extends TestCase {
	//@Test
	public void testFamily() {
		Family family = new Family("family1","mikedoe", "demo");
		assertEquals("family1", family.getFamily()   );
		assertEquals("mikedoe", family.getUser()     );
		assertEquals("demo"   , family.getPassword() );

		try {
			Family family2 = new Family(family.url(),"mikedoe", "demo");
			assertEquals("family1", family2.getFamily() );
			if(ServerEnv.get() == ServerEnv.DUDI_SERVER) {
				assertEquals("http://family1.yanai.loc:3000/", family.url() );
			}
		} catch (Exception e) {
			fail("Family should not throw");
		}

		Family family3 = new Family(family);
		assertEquals("family1", family3.getFamily()   );
		assertEquals("mikedoe", family3.getUser()     );
		assertEquals("demo"   , family3.getPassword() );
	}
	
	//@Test 
	public void testUrlThrow() throws FamundoException{ 
	    try { 		
	    	new Family().url(); 
	    } catch (Exception e) {
			return;
		}
	    fail("empty family does not throw exception");
	}

	//@Test(expected = com.famundo.FamundoException.class) 
	public void testValidate() throws FamundoException{ 
	    try { 		
	    	new Family().validate(); 
	    } catch (Exception e) {
			return;
		}
	    fail("family validate does not throw exception");
	}
}
