package kickstart.SavedClasses;


import junit.framework.TestCase;

import org.junit.Test;
import kickstart.SavedClasses.CCart.CartType;

public class CCartTest extends TestCase {
	
	@Test
	public void testSetter(){
		CCart cart = new CCart("Mensch", CartType.RESERVATION, "333-3", "12.12.2016", "abc@mail.de");
		cart.setResCode("000-4");
		cart.setTime("11.11.2016");
		cart.setUaccount("123@ufo.de");
		assertEquals("Cart returns wrong resCode", "000-4", cart.getResCode());
		assertEquals("Cart returns wrong time",  "11.11.2016", cart.getTime());
		assertEquals("Cart returns wrong Email",  "123@ufo.de", cart.getUaccount());
	}
}
