/**
 * JUnit test class for SaveRoom
 * @author Alexej
 */

package kickstart.SavedClasses;

import org.junit.Test;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;

import junit.framework.TestCase;

import org.junit.Before;

import kickstart.SavedClasses.Employee;



public class EmployeeTest extends TestCase {

	@Autowired
	private UserAccount userAccount;
	private Employee deadTerrorist;
	
	@Before
	public void setUp(){
		deadTerrorist = new Employee(userAccount, "555666777", "SileceHill", "555", "ElmasStreet69", "99999");
	}
	
	
	//Setter Tests
	@Test
	public void testSetter(){
		deadTerrorist.setStreet("ElmasStreet001A");
		deadTerrorist.setTown("SilentHill");
		deadTerrorist.setZip("06660");
		deadTerrorist.setSalary("1");
		
		assertEquals("Setter Tests : Street does not match with expected Street","ElmasStreet001A",deadTerrorist.getStreet());
		assertEquals("Setter Tests : Town name does not match with expected town name","SilentHill",deadTerrorist.getTown());
		assertEquals("Setter Tests : ZipCode does not match with expected zip code", "06660",deadTerrorist.getZip());
		assertEquals("Setter Tests : Salary does not match with expected Salary","1", deadTerrorist.getSalary());
	}
	
	//Getter Tests
	@Test
	public void testGetter(){
		assertEquals("Getter Tests : Street does not match with expected Street","ElmasStreet69",deadTerrorist.getStreet());
		assertEquals("Getter Tests : Town name does not match with expected town name","SileceHill",deadTerrorist.getTown());
		assertEquals("Getter Tests : ZipCode does not match with expected zip code", "555",deadTerrorist.getZip());
		assertEquals("Getter Tests : Salary does not match with expected Salary","99999", deadTerrorist.getSalary());
	}
	
	@Test
	public void testMoneyToString(){
		assertEquals("Salary does not match with expected","99999,00",deadTerrorist.moneyToString());
	}
	
}
