/**
 * JUnit test class for SaveRoom
 * @author Alexej
 */



package kickstart.SavedClasses;



import org.junit.Before;
import org.junit.Test;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import junit.framework.TestCase;


public class TestCustomer extends TestCase
{
	//@Autowired
	//private UserAccountManager userAccountManager;
	private Customer unknownGuy;
	
	@Autowired
	@Before
	public void setUp(UserAccountManager userAccountManager)
	{
		Role customerRole = Role.of("ROLE_CUSTOMER");
		UserAccount ua1 = userAccountManager.create("customer", "123", customerRole);
		ua1.setFirstname("Hans");
		ua1.setLastname("DereinfacheKunde");
		ua1.setEmail("lowbob@ufo.com");
		//userAccountManager.save(ua1);
	
		// sämmtliche späteren zugriffe auf unknownGuy führen zu einem nullpointer
		unknownGuy = new Customer(ua1, "0841224794");	//==============NULLPOINTER!!!!!!!!!!!!!!!=======
		
	}
	
	
	//Setter Tests
	@Test
	public void testSetter()
	{
		/*
		Setter to test
		
		public void setId       (Long id )			{this.id = id;}
		public void setForename	(String firstname)	{userAccount.setFirstname(firstname);}
		public void setLastname	(String lastname)	{userAccount.setLastname(lastname);}
		public void setEmail    (String email)      {userAccount.setEmail(email);}
		public void setPhone    (String phone)      {this.phone = phone;}
		*/
	
	}
	
	//Getter Tests
	@Test
	public void testGetter()
	{
		
		//==============NULLPOINTER!!!!!!!!!!!!!!!======= Sämmtliche zgriffe führen zu einem
	//	unknownGuy.setId(123L);     	//The only way to test it (auto generated)
	//	unknownGuy.getUseraccount().setFirstname("Rob");
	//	unknownGuy.getUseraccount().setLastname("Zombie");
	//	unknownGuy.getUseraccount().setEmail("RandB@zomb.ro");
		
		//assertEquals("Getter Tests : UserAccounts don't match", userAccount ,unknownGuy.getUseraccount());
		//assertEquals("Getter Tests : Expected Id does not match with getted","123",unknownGuy.getId());
		//assertEquals("Getter Tests : Expected Firstname does not match", "Rob",unknownGuy.getForename());
		//assertEquals("Getter Tests : Expected Lastname does not match", "Zombie",unknownGuy.getLastname());
		//assertEquals("Getter Tests : Expected Email does not match", "RandB@zomb.ro",unknownGuy.getEmail());
		//assertEquals("Getter Tests : Expected PhoneNumber does not match", "0841224794",unknownGuy.getPhone());
	}
	
}


