package kickstart.SavedClasses;



import junit.framework.TestCase;
import kickstart.Application;
import kickstart.Repositorys.*;
import kickstart.SavedClasses.*;
import kickstart.SavedClasses.CSeat.Etype;
import kickstart.model.eventManagement.CProgram;
import kickstart.model.eventManagement.CProgrampoint;
import kickstart.model.programOverView.Timestap;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;


/**
 * JUnit test class for movies and related class methods.
 * @author Beatrice
 * @since 26.12.15
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)

public class ProgramMovieEventRoomTest extends TestCase {

	private @Autowired
	UserAccountManager userAccountManager;
	private @Autowired
	CustomerRepository customerRepository;
	private @Autowired
	BusinessTime businessTime;
	private @Autowired
	DBRoom dbRoom;
	private @Autowired
	DBMovie dbMovie;
	private @Autowired
	DBDate dbDate;
	private @Autowired
	DBEvent dbEvent;
	private @Autowired DBRow dbRow;
	private @Autowired DBCRoom dbcRoom;
	private @Autowired DBSeat dbSeat;
	private @Autowired
	CProgram cProgram;
	private @Autowired CartRepo cartRepo;
	private @Autowired DBEmployee dbEmployee;
	CDateStorage cds1 = new CDateStorage(15,20,12,5,2016);

	LocalDateTime today = LocalDateTime.now();

	SaveRoom sr1 = new SaveRoom("SR1", "ppp-llll-pppp","2.20","3.30");
	SaveRoom sr2 = new SaveRoom("SR2", "ppp-pppp-pppp-pppppp","7.00","9.00");

	CRoom r1;
	CRoom r2;

	CMovie m2 = new CMovie("Boat", "Not a boat.", "bt", Integer.valueOf(120), "10.67", "1.10", "12");
	CMovie m3 = new CMovie("Forest", "Not a forest.", "frst", Integer.valueOf(90), "25", "3.10", "6");
	
	CEvent e2;
	CEvent pev;
	CEvent npev;
	CProgram cinema_program;

	CProgrampoint p1;
	CProgrampoint p2;

	
	@Before
	public void setUp(){

		sr1 = dbRoom.save(sr1);
		sr2 = dbRoom.save(sr2);
		cinema_program = new CProgram();
		cinema_program.setDbcRoom(dbcRoom);
		cinema_program.setDbEvent(dbEvent);
		cinema_program.setDbDate(dbDate);

		dbMovie.save(m2);
		m3.setIsRent(false);
		dbMovie.save(m3);
		
		CDateStorage cds2 = new CDateStorage(today.getMinute(),today.getHour(),today.getDayOfMonth(), today.getMonthValue(),today.getYear());
		cds2.subMinutes(20);
		
		cinema_program.createEvent(m2, sr1.getRoom(), sr1.getName(), sr1.getId(), cds1, false, dbRow, dbSeat);
		cinema_program.createEvent(m3, sr1.getRoom(), sr1.getName(), sr1.getId(), cds2, true, dbRow, dbSeat);
		
		e2 = cinema_program.findEventByID(Long.valueOf(2));

		CProgram helper = new CProgram();
		r1 = new CRoom(sr1.getName(), new Long(3), helper.createRows(sr1.getRoom(),dbRow,dbSeat));
		r2 = new CRoom(sr2.getName(), new Long(7), helper.createRows(sr2.getRoom(),dbRow,dbSeat));
		r1.creatRow(10, CSeat.Etype.Parkett);
		r1.changeSeatStatus(1, 3, CSeat.EStatus.RESERVED,null);

		Iterator<CProgrampoint> it = cinema_program.getProgramPoints().iterator();
		p1 = it.next();
		p2 = it.next();

		Iterator <CEvent> iterator = dbEvent.findAll().iterator();
		while (iterator.hasNext()){
			pev = iterator.next();
			if (pev.getIsPrivate()){
				break;
			}
		}

		iterator = dbEvent.findAll().iterator();
		while (iterator.hasNext()){
			npev = iterator.next();
			if (!npev.getIsPrivate() && npev.getMovie().getName().equals(m2.getName())){
				break;
			}
		}


	}
	
	@Test
	public void testMovieIsRent(){
		assertTrue("Movie should be rent", m2.getIsRent());
		assertFalse("Movie should not be rent", m3.getIsRent());
	}

	@Test
	public void testEventValues(){
		assertTrue("Event should be private", cinema_program.findEventByID(pev.getId()).getIsPrivate());
		assertFalse("Event should not be private", cinema_program.findEventByID(npev.getId()).getIsPrivate());
		assertEquals("Event is set for wrong movie.", m2.getName(), cinema_program.findEventByID(npev.getId()).getMovie().getName());
	}

	/*@Test
	public void testCan_Reserve(){
		assertTrue("Reservations should be allowed", cinema_program.findEventByID(Long.valueOf(1)).canReserve(today, 30));
		assertFalse("Reservations should not be allowed", cinema_program.findEventByID(Long.valueOf(3)).canReserve(today, 30));
	}*/

	@Test
	public void testRoom(){
		assertNotNull("Seat should exist in added row", r1.getSeat(3, 4));
		assertNull("Seat should not exist", r1.getSeat(4, 5));
	}

	@Test
	public void testType() {
		assertTrue("Could not change seat status in TestCRow",r1.changeSeatStatus(2, 3, CSeat.EStatus.RESERVED,null));
		assertFalse("Seat status should be not changed in TestCRow",r1.changeSeatStatus(21,1, CSeat.EStatus.AVAILABLE,null));
	}


	@Test
	public void test_hasReservation() {
		assertTrue("The room should have reservated seats",r1.hasReservation());
		assertFalse("There should be no reservations in the room",r2.hasReservation());

	}

	@Test
	public void testSeatStatus(){
		assertEquals("Seat should be avalibel if nothing done with it", r1.getSeatStatus(2,2), CSeat.EStatus.AVAILABLE);
		assertEquals("Seat should be Reserved!", r1.getSeatStatus(1,3), CSeat.EStatus.RESERVED);
	}

	@Test
	public void testGetSeat(){
		assertFalse("Seat should be differnt, in different Rooms", r1.getSeat(1,1).equals(r2.getSeat(1,1)));
		assertEquals("Seat should be the same in one Room, with same numbers", r1.getSeat(1,1), r1.getSeat(1,1));
	}

	@Test
	public void testCanReserve(){
		cds1 = new CDateStorage(businessTime.getTime().plusDays(Long.valueOf(1)));
		e2 = new CEvent(cds1,cds1,m2,r1);
		assertTrue("Event should be reservable", e2.canReserve(businessTime.getTime(), 30));

		cds1 = new CDateStorage(businessTime.getTime().plusMinutes(Long.valueOf(29)));
		e2 = new CEvent(cds1,cds1,m2,r1);
		assertFalse("Event shouldn'd be reserable", e2.canReserve(businessTime.getTime(), 30));
	}

	@Test
	public void testProgramPoint(){
		assertEquals("Programpoints Name should equal Movie name", p1.getName(), "Boat");
		p1.setMovie(m3);
		assertEquals("Programpoints Movie should be changed", p1.getMovie(), m3);
		assertTrue("Programpoint contains private Movies!", p2.includesPrivateEvents());
		assertTrue("Programpoint contains non Private Movies!", p1.includesNonPrivateEvents());
		assertFalse("Programpoint contains not nonPrivateMovie", p2.includesNonPrivateEvents());
		assertFalse("Programpoint contains not Privat movies", p1.includesPrivateEvents());
		CEvent etmp = new CEvent(new CDateStorage(LocalDateTime.now().minusYears(Long.valueOf(99))), null, m3, r1);
		p1.addEvent(etmp);
		assertTrue("Events werden nicht sortiert eingefügt!", p1.getEvents().iterator().next().equals(etmp));
	}

	@Test
	public void testProgrammInizializer(){
		CEvent etmp = new CEvent(dbDate.save(new CDateStorage(LocalDateTime.now().plusDays(Long.valueOf(99)))), dbDate.save(new CDateStorage(LocalDateTime.now())), dbMovie.save(m3), dbcRoom.save(r2));
		etmp = dbEvent.save(etmp);
		cinema_program.inizalize();
		assertNotNull("new DBEvent should be found by new ini",cinema_program.findEventByID(etmp.getId()));
	}

	@Test
	public void testProgramUpdate(){
		cinema_program.updateProgramPoints(new CDateStorage(LocalDateTime.now().plusYears(Long.valueOf(999))));
		assertTrue("After update old Programpoints should be removed", cinema_program.getProgramPoints().isEmpty());
	}

	@Test
	public void testFindEventsByRoom(){
		for (CEvent event : cinema_program.findEventsByRoom(sr1.getId())){
			assertEquals("Event room Id should equal Room Id", event.getO_room().getI_id(), sr1.getId());
		}
	}
	
	@Test
	public void testtoString(){
		assertEquals("Room is not creating correct String", "-llll-pppp-pppppppppp", r1.toString());
	}
	
	@Test
	public void testcheckTimed(){
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("Boat", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		assertTrue("Ticket should be invalid due to available seat", r1.checkTimedoutOrAvailabe(today, ticket));
	}
	
	@Test
	public void testCDateStorage(){
		CDateStorage date1 = new CDateStorage();
		date1.setDate(3, 3, 3, 3, 2003);
		assertEquals("Wrong day has been set", Integer.valueOf(3), date1.getDay());
		assertEquals("Wrong year has been set", Integer.valueOf(2003), date1.getYear());
		assertEquals("Wrong month has been set", Integer.valueOf(3), date1.getMonth());
		assertEquals("Wrong hour has been set", Integer.valueOf(3), date1.getHour());
		assertEquals("Wrong minute has been set", Integer.valueOf(3), date1.getMinute());
		
		assertFalse("Value of sec should be null", date1.laterThan(null));
		assertTrue("Year difference should be under 0", date1.laterThan(new CDateStorage(1,1,1,1,2002)));
		assertFalse("Value of sec should be null", date1.erlierThen(null));
		assertTrue("Year difference should be under 0", date1.erlierThen(new CDateStorage(1,1,1,1,2008)));
		
		date1.setDay(4);
		date1.setHour(4);
		date1.setMinute(4);
		date1.setMonth(4);
		date1.setYear(2004);
		assertEquals("Wrong daate has been set", new CDateStorage(4,4,4,4,2004).toString(), date1.toString());
	}
	
	@Test
	public void testTimestap(){
		Timestap ts = new Timestap(1,1,1);
		ts.setMinute(5);
		ts.setHour(5);
		assertEquals("Returns wrong minute", Integer.valueOf(5), ts.getMinute());
		assertEquals("Returns wrong hour", Integer.valueOf(5), ts.getHour());
	}
	
}
