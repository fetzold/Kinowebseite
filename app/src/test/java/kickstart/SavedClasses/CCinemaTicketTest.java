package kickstart.SavedClasses;



import java.math.BigDecimal;

import junit.framework.TestCase;
import kickstart.Application;


import kickstart.Repositorys.*;
import kickstart.SavedClasses.*;
import kickstart.SavedClasses.CCinemaTicket.PriceClass;
import kickstart.model.eventManagement.CProgram;
import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnit test class for CSeat objects.
 * @author Beatrice
 * @since 21.12.15
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)

public class CCinemaTicketTest extends TestCase {

	private @Autowired
	UserAccountManager userAccountManager;
	private @Autowired
	CustomerRepository customerRepository;
	private @Autowired
	BusinessTime businessTime;
	private @Autowired
	DBRoom dbRoom;
	private @Autowired DBMovie dbMovie;
	private @Autowired
	DBDate dbDate;
	private @Autowired
	DBEvent dbEvent;
	private @Autowired DBRow dbRow;
	private @Autowired DBCRoom dbcRoom;
	private @Autowired DBSeat dbSeat;
	private @Autowired CProgram cProgram;
	private @Autowired CartRepo cartRepo;
	private @Autowired DBEmployee dbEmployee;
	
	//start time of test event 1 is 20:15, 12.05.2016
	CDateStorage cds1 = new CDateStorage(15,20,12,5,2016);
	//start time of test event 2 is 22:30, 12.05.2016
	CDateStorage cds2 = new CDateStorage(30,22,12,5,2016);
	SaveRoom sr1 = new SaveRoom("SR1", "ppp-llll-pppp","2.20","3.30");
	CMovie m2 = new CMovie("Boat", "Not a boat.", "bt", Integer.valueOf(120), "10.67", "1.10", "12");
	CMovie m3 = new CMovie("Forest", "Not a forest.", "frst", Integer.valueOf(90), "25", "3.10", "6");
	
	CCinemaTicket ct1;
	CCinemaTicket ct2;

	CProgram cinema_program;

	
	@Before
	public void setUp(){
		//r1 = new CRoom(sr1.getRoom(), sr1.getName(), new Long(3));
		dbMovie.save(m2);
		m3.setIsRent(false);
		dbMovie.save(m3);

		cinema_program = new CProgram();
		cinema_program.setDbDate(dbDate);
		cinema_program.setDbEvent(dbEvent);
		cinema_program.setDbcRoom(dbcRoom);

		cinema_program.createEvent(m2, sr1.getRoom(), sr1.getName(), sr1.getId(), cds1, false, dbRow, dbSeat);
		cinema_program.createEvent(m3, sr1.getRoom(), sr1.getName(), sr1.getId(), cds2, true, dbRow, dbSeat);
		
		CMovie m1 = dbMovie.findById(Long.valueOf(1));
		
		ct1 = new CCinemaTicket(m1.getName(), Money.of(new BigDecimal("1.75"), "EUR"), 1, 2, Long.valueOf(1),"Roomname", "Starttime", CSeat.Etype.Loge, "Moviename");
		ct2 = new CCinemaTicket(m2.getName(), Money.of(new BigDecimal("1.75"), "EUR"), 2, 2, Long.valueOf(3),"Roomname", "Starttime", CSeat.Etype.Parkett, "Moviename");


		
	}
	
	@Test
	public void testTicketData(){
		assertEquals("Ticket is calculating wrong price.", "EUR 1.75", ct1.getPrice().toString());
		assertEquals("Ticket is calculating wrong price.", "EUR 1.75", ct2.getPrice().toString());
	}
	
	@Test
	public void testGetter(){
		assertEquals("Getter Error: Expected price does not match with current","EUR 1.75", ct1.getStringPrice());
		assertEquals("Getter Error: Expected Seat does not match", "2", Integer.toString(ct1.getI_seat()));
		assertEquals("Getter Error: Expected Row does not match", "1", Integer.toString(ct1.getI_row()));
		assertEquals("Getter Error: Expected RoomName does not match", "Roomname", ct1.getS_room());
		assertEquals("Getter Error: Expected Time does not match","Starttime",  ct1.getTime());
		assertEquals("Getter Error: Expected EventID does not match",Long.toString(Long.valueOf(1)), Long.toString(ct1.getEventID()));
		assertEquals("Getter Error: Expected SeatType does not match", CSeat.Etype.Loge,  ct1.getSeattype());
		assertEquals("Getter Error: Movie names do not match", "Moviename",ct1.getMoviename());
		assertEquals("Getter Error: PriceClass  do not match", PriceClass.FULL,ct1.getPriceClass());
		assertEquals("Money To string does not match with expected money string", "1,75",  ct1.moneyToString());
	}
	
	@Test 
	public void testSetter()
	{
		ct1.setPrice(Money.of(new BigDecimal("2.75"), "EUR"));
		ct1.setI_row(3);
		ct1.setI_seat(3);
		ct1.setS_room("DerRaum");
		ct1.setTime("DasEndeIstNahe");
		ct1.setEventID(Long.valueOf(3));
		ct1.setSeattype(CSeat.Etype.DEFECTIVE);
		ct1.setMoviename("DerFilm");
		ct1.setPriceClass(PriceClass.REDUCED);
		
		assertEquals("Getter Error: Expected price does not match with current","EUR 2.75", ct1.getStringPrice());
		assertEquals("Money To string does not match with expected money string", "2,75",  ct1.moneyToString());
		assertEquals("Getter Error: Expected Seat does not match", "3", Integer.toString(ct1.getI_seat()));
		assertEquals("Getter Error: Expected Row does not match", "3", Integer.toString(ct1.getI_row()));
		assertEquals("Getter Error: Expected RoomName does not match", "DerRaum", ct1.getS_room());
		assertEquals("Getter Error: Expected Time does not match","DasEndeIstNahe",  ct1.getTime());
		assertEquals("Getter Error: Expected EventID does not match",Long.toString(Long.valueOf(3)), Long.toString(ct1.getEventID()));
		assertEquals("Getter Error: Expected SeatType does not match", CSeat.Etype.DEFECTIVE,  ct1.getSeattype());
		assertEquals("Getter Error: Movie names do not match", "DerFilm",ct1.getMoviename());
		assertEquals("Getter Error: PriceClass  do not match", PriceClass.REDUCED,ct1.getPriceClass());
		
	}
}
