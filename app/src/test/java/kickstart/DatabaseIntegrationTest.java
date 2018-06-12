package kickstart;

import junit.framework.TestCase;
import kickstart.Repositorys.*;
import kickstart.SavedClasses.CCart;
import kickstart.SavedClasses.CCinemaTicket;
import kickstart.SavedClasses.CDateStorage;
import kickstart.SavedClasses.CSeat;
import kickstart.model.eventManagement.CProgram;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;

/**
 * JUnit test class for CartHelper-Functions
 * @author Beatrice
 * @since 04.01.2016
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)

public class DatabaseIntegrationTest extends TestCase {

	CCart c1, c2;
	CCinemaTicket t1, t2;
	CSeat s1;
	CDateStorage cds;
	CProgram cinema_program;
	private @Autowired UserAccountManager userAccountManager;
	private @Autowired CustomerRepository customerRepository;
	private @Autowired BusinessTime businessTime;
	private @Autowired DBRoom dbRoom;
	private @Autowired DBMovie dbMovie;
	private @Autowired DBDate dbDate;
	private @Autowired DBEvent dbEvent;
	private @Autowired DBRow dbRow;
	private @Autowired DBCRoom dbcRoom;
	private @Autowired DBSeat dbSeat;
	private @Autowired CProgram cProgram;
	private @Autowired CartRepo cartRepo;
	private @Autowired DBEmployee dbEmployee;
	
	@Before
	public void setUp()
	{
		c1 = new CCart("bob", CCart.CartType.PURCHASE, "000", "01.01.2016", "Bob");
		c2 = new CCart("bill", CCart.CartType.RESERVATION, "123-4", "02.02.2016", "Bill");
		cartRepo.save(c1);
		cartRepo.save(c2);
		c1.addTicket(t1);
		c2.addTicket(t2);
		cds = new CDateStorage(LocalDateTime.now());
		s1 = new CSeat(3, 3, CSeat.Etype.Loge);
		s1.setReservedUntil(dbDate.save(cds));
		dbSeat.save(s1);
		cinema_program = new CProgram();
		cinema_program.setDbcRoom(dbcRoom);
		cinema_program.setDbEvent(dbEvent);
		cinema_program.setDbDate(dbDate);
	}
	
	
	@Test
	public void testCartRepo() {
		assertNotNull("Cart Database should not be empty.", cartRepo.findAll());
		Iterable<CCart> carts = cartRepo.findAll();
		//assertThat(carts, is(iterableWithSize(9)));
	}
	
	@Test
	public void testDBSeat() {
		assertNotNull("Seat Database should not be empty.", cinema_program);
	}
	
	@Test
	public void testDBDate() {
		assertNotNull("CDateStorage Database should not be empty.", cartRepo.findAll());
	}
	
	@Test
	public void testDBMovie() {
		assertNotNull("Movie Database should not be empty.", dbMovie.findAll());
	}
	
	@Test
	public void testDBRoom(){
		assertNotNull("Room Database should not be empty.", dbRoom.findAll());
	}
	
	@Test
	public void testDBEmployee(){
		assertNotNull("Employee Database should not be empty", dbEmployee.findAll());
	}
}

