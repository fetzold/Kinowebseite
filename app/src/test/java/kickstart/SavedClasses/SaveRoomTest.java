/**
 * JUnit test class for SaveRoom
 * @author Alexej
 */

package kickstart.SavedClasses;

import org.junit.Test;

import junit.framework.TestCase;

import org.junit.Before;

import kickstart.SavedClasses.CSeat;


public class SaveRoomTest extends TestCase{

	private SaveRoom r1;
	
	@Before
	public void setUp(){
		r1 = new SaveRoom("Raum 21", "-pppppp-dppppd-llllll-ddddd-llllll", "3.00", "5.00");
		r1.setId(213L);		//Id is auto generated so I still need to know the value without the getter

	}
	
	
	//Setter Tests
	@Test
	public void testSetter()
	{
		
		//Setter test conditions
		r1.setName("Raum 22");
		r1.setRoom("-ppppp-dppppd-llllll-ddddd-llllll");
		r1.setId(212L);
		r1.setBasicCharge("3.50");
		r1.setExtraCharge("5.50");
		
		assertEquals("Setter Tests: Room string representation does not match with expected string", "-ppppp-dppppd-llllll-ddddd-llllll", r1.getRoom());
		assertEquals("Setter Tests: Room name does not match with expected string", "Raum 22", r1.getName());
		assertEquals("Setter Tests: Expected and actual id's do not match", "212", Long.toString(r1.getId()));
		assertEquals("Setter Tests: Basic Charge does not match with the expected Basic Charge", "3.50", r1.getBasicCharge());
		assertEquals("Setter Tests: Extra Charge does not match with the expected Extra Charge", "5.50", r1.getExtraCharge());
	}
	
	
	//Getter Tests
	@Test
	public void testGetter(){
		assertEquals("Getter Tests: Room string representation does not match with expected string", "-pppppp-dppppd-llllll-ddddd-llllll", r1.getRoom());
		assertEquals("Getter Tests: Room name does not match with expected string", "Raum 21", r1.getName());
		assertEquals("Getter Tests: Expected and actual id's do not match", "213", Long.toString(r1.getId()));
		assertEquals("Getter Tests: Basic Charge does not match with the expected Basic Charge", "3.00", r1.getBasicCharge());
		assertEquals("Getter Tests: Extra Charge does not match with the expected Extra Charge", "5.00", r1.getExtraCharge());
		
		assertEquals("The Return Value does not match with expected price class" ,"3.00", r1.getPrice(CSeat.Etype.Parkett));
		assertEquals("The Return Value does not match with expected price class" ,"5.00", r1.getPrice(CSeat.Etype.Loge));
		
		assertNull("Null was expected",r1.getPrice(CSeat.Etype.DEFECTIVE));
		
	}
	
}
