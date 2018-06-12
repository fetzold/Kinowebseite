package kickstart.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.transaction.Transactional;

import kickstart.Repositorys.CartRepo;
import kickstart.Repositorys.DBCRoom;
import kickstart.Repositorys.DBDate;
import kickstart.Repositorys.DBEvent;
import kickstart.Repositorys.DBRow;
import kickstart.Repositorys.DBSeat;
import kickstart.Repositorys.TicketCatalog;
import kickstart.SavedClasses.CCart;
import kickstart.SavedClasses.CCinemaTicket;
import kickstart.SavedClasses.CDateStorage;
import kickstart.SavedClasses.CSeat.EStatus;
import kickstart.SavedClasses.SaveRoom;
import kickstart.SavedClasses.CCart.CartType;
import kickstart.SavedClasses.CSeat;
import kickstart.SavedClasses.CSeat.Etype;
import kickstart.model.CartHelper;
import kickstart.model.eventManagement.CProgram;

import org.hibernate.Hibernate;
import org.javamoney.moneta.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;
import kickstart.Application;


/**
 * JUnit test class for CartHelper-Functions
 * @author Beatrice
 * @since 04.01.2016
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)

public class CartHelperTest extends TestCase {
	
	private CartHelper ch;
	private @Autowired BusinessTime businessTime;
	private CCart c1, c2, c3, c4;
	private LocalDateTime ldt;
	private ArrayList<CCart> deletelist;
	@Autowired private CartRepo cartRepo;
	@Autowired private TicketCatalog ticketCatalog;
	@Autowired private DBEvent dbEvent;
	@Autowired private DBDate dbDate;
	@Autowired private DBRow dbRow;
	@Autowired private DBCRoom dbcRoom;
	@Autowired private DBSeat dbSeat;
	@Autowired private CProgram cProgram;
	private Money price;
	private SaveRoom sr1 = new SaveRoom("SR1", "ppp-llll-pppp","2.20","3.30");
	private CDateStorage cds1 = new CDateStorage(15,10,12,2,2016);
	private CDateStorage cds2 = new CDateStorage(30,22,12,2,2016);
	
	@Before
	public void setUp()
	{
		ch = new CartHelper(businessTime, cartRepo, ticketCatalog,dbEvent,dbDate, dbSeat);
		ldt = businessTime.getTime();
		deletelist = new ArrayList<CCart>();
		
		price = Money.of(new BigDecimal("2.50"), "EUR").add(Money.of(new BigDecimal("2.40"), "EUR"));
		
		c1 = new CCart("Der Chef", CartType.PURCHASE, "000", "25.01.2016", "max@ufo.de");
		c2 = new CCart("Der Chef", CartType.RESERVATION, "845-5", "25.03.2016", "max@ufo.de");
		c3 = new CCart("Max Meier", CartType.PURCHASE, "845-5", "15.10.2016", "max@ufo.de");
		c4 = new CCart("Max Meier", CartType.CANCELED, "845-5", "25.10.2016", "max@ufo.de");
		
		c1.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(2), Integer.valueOf(1), Long.valueOf(1), sr1.getName(), cds1.toString(), CSeat.Etype.Parkett, "Film"));
		c1.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(2), Integer.valueOf(2), Long.valueOf(1), sr1.getName(), cds1.toString(), CSeat.Etype.Parkett, "Film"));
		c2.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(1), Integer.valueOf(2), Long.valueOf(1), sr1.getName(), cds2.toString(), CSeat.Etype.Parkett, "Film"));
		c2.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(1), Integer.valueOf(3), Long.valueOf(1), sr1.getName(), cds2.toString(), CSeat.Etype.Parkett, "Film"));
		c3.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(1), Integer.valueOf(2), Long.valueOf(1), sr1.getName(), cds2.toString(), CSeat.Etype.Parkett, "Film"));
		c4.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Raum", "2.15 (24.12.2016)", CSeat.Etype.Parkett, "Film"));
		c4.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(1), Integer.valueOf(2), Long.valueOf(1), "Raum", "2.15 (24.12.2016)", CSeat.Etype.Parkett, "Film"));
		c4.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(1), Integer.valueOf(3), Long.valueOf(1), "Raum", "2.15 (24.12.2016)", CSeat.Etype.Parkett, "Film"));
		cartRepo.save(c1);
		cartRepo.save(c2);
		cartRepo.save(c3);
		cartRepo.save(c4);
		ticketCatalog.save(c1.getTickets());
		ticketCatalog.save(c2.getTickets());
		ticketCatalog.save(c3.getTickets());
		ticketCatalog.save(c4.getTickets());
		
	}
	
	@Test
	public void testGenerateCode(){
		assertNotSame("Generated String should not be empty.", new String(""), ch.generateCode());
		assertNotNull("Generated String should not be Null.", ch.generateCode());
		assertTrue("Generated Code doesn't match expected pattern 000-0", ch.generateCode().matches("\\d\\d\\d\\-\\d"));
		assertNotSame("Generated Code should be different when called twice in a row.", ch.generateCode(), ch.generateCode());
	}
	
/*	@Test
	@Transactional
	public void testFreeResCodes() {
		//assertEquals("Returned list should be empty.", 1, ch.freeResCodes(ldt, deletelist, cProgram).size());
		assertNotSame("Returned list should not be empty.", 0, ch.freeResCodes(ldt, cProgram).size());
	}
	

	@Test
	@Transactional
	public void testCancelCarts() {
		assertNotSame("List should not be empty", new ArrayList<CCart>(), ch.cancelCarts());
	} */
	
	@Test
	@Transactional
	public void testCheckInvalid(){
		
		Cart cart = new Cart();
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("Boat", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		cart.addOrUpdateItem(ticket, Quantity.of(1));
		assertTrue("Ticket should be invalid, seat is available", ch.checkInvalid(cart, businessTime.getTime()));
		
		ticket = new CCinemaTicket("Boat", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		dbEvent.findById(Long.valueOf(1)).getO_room().changeSeatStatus(1, 1, EStatus.PAID, dbDate.save(new CDateStorage()));
		cart.addOrUpdateItem(ticket, Quantity.of(1));
		assertFalse("Ticket should not be invalid, seat is paid",  ch.checkInvalid(cart, businessTime.getTime()));
	}
	
	@Test
	@Transactional
	public void testCheckInvalidRes(){
		Cart cart = new Cart();
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("Boat", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		cart.addOrUpdateItem(ticket, Quantity.of(1));
		assertTrue("Invalid carts should exist", ch.checkInvalidRes(cart, businessTime.getTime().plusDays(Long.valueOf(5)), 30, cProgram));
	}
	
	@Test
	public void testFormatMoney(){
		assertNotSame("Formatted String should not be empty.", new String(""), ch.formatMoney(Money.of(new BigDecimal("12.09009"), "EUR")).matches("12,09"));
		assertNotNull("Formatted String should not be Null.", ch.formatMoney(Money.of(new BigDecimal("12.09009"), "EUR")).matches("12,09"));
		assertTrue("Formatted money string doesn't match pattern.", ch.formatMoney(Money.of(new BigDecimal("12.09009"), "EUR")).matches("12,09"));
	}
	
	@After
	public void resetDB(){
		cartRepo.deleteAll();
		ticketCatalog.deleteAll();
	}
}

