package kickstart.SavedClasses;

import junit.framework.TestCase;
import kickstart.Application;
import kickstart.Repositorys.DBRow;
import kickstart.Repositorys.DBSeat;
import kickstart.SavedClasses.*;
import kickstart.SavedClasses.CSeat.EStatus;
import kickstart.SavedClasses.CSeat.Etype;
import kickstart.model.eventManagement.CProgram;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * JUnit test class for CSeat objects.
 * @author Beatrice
 * @since 26.12.15
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)

public class CSeatTest extends TestCase{

	CSeat s1 = new CSeat(2,3,CSeat.Etype.Loge);
	SaveRoom sr1 = new SaveRoom("SR1", "ppp-llll-pppp","2.20","3.30");
	CRoom r1;
	@Autowired
	DBRow dbRow;
	@Autowired
	DBSeat dbSeat;
	
	@Before
	public void setUp()
	{
		CProgram helper = new CProgram();
		r1 = new CRoom(sr1.getName(), new Long(3), helper.createRows(sr1.getRoom(),dbRow,dbSeat));
		r1.changeSeatStatus(3, 1, CSeat.EStatus.RESERVED, new CDateStorage());
	}
	
	@Test
	public void testSeat(){
		assertEquals("Wrong seat type", CSeat.Etype.Loge, s1.getE_Type());
		assertNull("Reservation time should be empty", s1.getReservedUntil());
		assertEquals("Seat recieved wrong status", CSeat.EStatus.AVAILABLE, s1.getE_Status());
		assertEquals("Put wrong ow value", 2, s1.getI_row().intValue());
	}
	
	@Test
	public void test_changeSeatStatus(){
		assertTrue("The seat status should have been changed", r1.changeSeatStatus(2, 2, CSeat.EStatus.PAID, new CDateStorage()));
		assertFalse("The seat status should not have changed", r1.changeSeatStatus(3, 1, CSeat.EStatus.RESERVED, new CDateStorage()));
		assertFalse("The seat status should not be changed on nonexistant seats", r1.changeSeatStatus(10, 10, CSeat.EStatus.RESERVED, new CDateStorage()));
	}
	
	@Test
	public void testSetter(){
		CSeat cs4 = new CSeat(1, 1, Etype.Loge);
		cs4.setI_number(7);
		cs4.setI_row(7);
		cs4.setE_Type(Etype.Parkett);
		assertEquals("Seat should have row 7", Integer.valueOf(7), cs4.getI_row());
		assertEquals("Seat should have number 7", Integer.valueOf(7), cs4.getI_number());
		assertEquals("Seat should have type Parkett", Etype.Parkett, cs4.getE_Type());
	}
	
	@Test
	public void testBooleans(){
		CSeat cs1 = new CSeat(1, 1, Etype.Loge);
		CSeat cs2 = new CSeat(2, 2, Etype.Parkett);
		cs2.setE_Status(EStatus.PAID);
		assertTrue("Seat should be Loge type", cs1.seatLoge());
		assertTrue("Seat should be Available statis", cs1.seatAvailable());
		assertFalse("Seat should not be Loge type", cs2.seatLoge());
		assertFalse("Seat should not be Available statis", cs2.seatAvailable());
	}
}
