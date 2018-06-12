package kickstart.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;

import kickstart.AbstractWebIntegrationTest;
import kickstart.Application;
import kickstart.Repositorys.CartRepo;
import kickstart.Repositorys.DBDate;
import kickstart.Repositorys.DBEvent;
import kickstart.Repositorys.DBSeat;
import kickstart.SavedClasses.CCart;
import kickstart.SavedClasses.CCart.CartType;
import kickstart.SavedClasses.CCinemaTicket;
import kickstart.SavedClasses.CDateStorage;
import kickstart.SavedClasses.CEvent;
import kickstart.SavedClasses.CSeat.EStatus;
import kickstart.SavedClasses.CSeat.Etype;

import org.javamoney.moneta.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)

public class CheckReservationControllerTest extends AbstractWebIntegrationTest {
	
	@Autowired private CartRepo cartRepo;
	@Autowired private DBEvent dbEvent;
	@Autowired private DBSeat dbseat;
	@Autowired private DBDate dbDate;
	private @Autowired BusinessTime businessTime;
	private @Autowired UserAccountManager userAccountManager;

	@Test
	@Transactional
	public void ticketmainIntegrationTest() throws Exception {
		mvc.perform(get("/checkreservation")
				.with(user("boss").roles("BOSS", "AUTHORIZED", "EMPLOYEE")))
				.andExpect(view().name("checkreservation"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void checkIntegrationTest() throws Exception {
		
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCart cart = new CCart("Bob", CartType.RESERVATION, "888-8", "10.10.2016", "bob@ufo.de");
		cart.addTicket(new CCinemaTicket("888-8", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat"));
		cart.addTicket(new CCinemaTicket("888-8", price, Integer.valueOf(1), Integer.valueOf(2), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat"));
		CCinemaTicket t = new CCinemaTicket("888-8", price, Integer.valueOf(1), Integer.valueOf(3), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		t.addCategory("canceled");
		cart.addTicket(t);
		cartRepo.save(cart);
		
		mvc.perform(post("/checkreservation")
				.with(user("Hans").roles("BOSS"))
				.param("resCode", "888-8"))
				.andExpect(model().attributeExists("found", "included", "total"))
				.andExpect(view().name("ticketview"))
				.andExpect(status().isOk());
		
		mvc.perform(post("/checkreservation")
				.with(user("boss").roles("BOSS"))
				.param("resCode", "000-0"))
				.andExpect(view().name("redirect:checkreservation"))
				.andExpect(status().isFound());
		
		cartRepo.delete(cart);
	}
	
	@Test
	public void checkpurchaseIntegrationTest() throws Exception {
		
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("Boat", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		CCart cart = new CCart("Matt", CartType.PURCHASE, "000", "10.10.2016", "bob@ufo.de");
		cartRepo.save(cart);
		cart.addTicket(ticket);
		
		mvc.perform(post("/checkpurchase")
				.with(user("boss").roles("BOSS", "AUTHORIZED", "EMPLOYEE"))
				.param("name", "Matt"))
				.andExpect(model().attributeExists("found"))
				.andExpect(model().attribute("found", is(iterableWithSize(1))))
				.andExpect(view().name("cartview"))
				.andExpect(status().isOk());
		
		mvc.perform(post("/checkpurchase")
				.with(user("boss").roles("BOSS", "AUTHORIZED", "EMPLOYEE"))
				.param("name", "mup"))
				.andExpect(flash().attributeExists("error"))
				.andExpect(view().name("redirect:checkreservation"))
				.andExpect(status().isFound());
		
		cartRepo.delete(cart);
	}
	
	@Test
	public void eventIntegrationTest() throws Exception {
		
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCart cart = new CCart("Bob", CartType.RESERVATION, "888-8", "10.10.2016", "bob@ufo.de");
		cart.addTicket(new CCinemaTicket("888-8", price, Integer.valueOf(2), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat"));
		cart.addTicket(new CCinemaTicket("888-8", price, Integer.valueOf(2), Integer.valueOf(2), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat"));
		CCinemaTicket t = new CCinemaTicket("888-8", price, Integer.valueOf(1), Integer.valueOf(3), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		t.addCategory("canceled");
		cart.addTicket(t);
		cartRepo.save(cart);
		
		mvc.perform(get("/ticketview/" + cart.getId().toString())
				.with(user("boss").roles("BOSS")))
				.andExpect(model().attributeExists("found", "included", "total"))
				.andExpect(model().attribute("included", is(iterableWithSize(not(100000)))))
				.andExpect(view().name("ticketview"))
				.andExpect(status().isOk());
		
		mvc.perform(get("/ticketview/100546")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("checkreservation"))
				.andExpect(status().isOk());
		
		cartRepo.delete(cart);
	}
	
	@Test
	public void cancelOneIntegrationTest() throws Exception {
		
		String id = "";
		for (CEvent e : dbEvent.findAll()){
			id = e.getId().toString();
			break;
		}
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("888-8", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(id), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		CCart cart = new CCart("Bob", CartType.RESERVATION, "888-8", "10.10.2016", "bob@ufo.de");
		cart.addTicket(ticket);
		cartRepo.save(cart);
		
		mvc.perform(post("/cancelOne")
				.with(user("boss").roles("BOSS"))
				.param("ident", ticket.getId().toString())
				.param("resCode", cart.getId().toString())
				.param("eventID", id))
				.andExpect(view().name("redirect:/ticketview/" + cart.getId().toString()))
				.andExpect(status().isFound());
	}
	
	@Test
	public void cancelAllIntegrationTest() throws Exception {
		
		String id = "";
		for (CEvent e : dbEvent.findAll()){
			id = e.getId().toString();
			break;
		}
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("888-8", price, Integer.valueOf(2), Integer.valueOf(2), Long.valueOf(id), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		CCart cart = new CCart("Bob", CartType.RESERVATION, "888-8", "10.10.2016", "bob@ufo.de");
		cart.addTicket(ticket);
		cartRepo.save(cart);
		String newid = "" + (cart.getId() + 1);
		
		mvc.perform(post("/cancelAll")
				.with(user("boss").roles("BOSS"))
				.param("name", "Bob")
				.param("resCode", cart.getId().toString()))
				.andExpect(view().name("redirect:/ticketview/" + newid))
				.andExpect(status().isFound());
		
		cartRepo.delete(cart);
		
	} 
	
/*	@Test
	public void payIntegrationTest() throws Exception {
		
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("555-5", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		CCart cart = new CCart("Hans", CartType.RESERVATION, "555-5", "10.10.2016", "bob@ufo.de");
		cart.addTicket(ticket);
		cartRepo.save(cart);
		
		mvc.perform(post("/pay.html")
				.with(user("boss").roles("BOSS"))
				.param("resCode", cart.getId().toString()))
				.andExpect(view().name("buy"))
				.andExpect(model().attributeExists("tickets"))
				.andExpect(model().attribute("tickets", is(emptyIterable())))
				.andExpect(status().isOk());
		
		cartRepo.delete(cart);
	} */
	
	@Test
	public void printIntegrationTest() throws Exception {
		
		CCart cart = new CCart("Bob", CartType.RESERVATION, "888-8", "10.10.2016", "bob@ufo.de");
		cartRepo.save(cart);
		
		mvc.perform(post("/print")
				.with(user("boss").roles("BOSS", "AUTHORIZED", "EMPLOYEE"))
				.param("resCode", "1"))
				.andExpect(view().name("buy"))
				.andExpect(model().attributeExists("tickets"))
				.andExpect(model().attribute("tickets", is(emptyIterable())))
				.andExpect(status().isOk());
		
		cartRepo.delete(cart);
	}
	
	@Test
	@Transactional
	public void myordersIntegrationTest() throws Exception {
		
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCart cart = new CCart("Godfather", CartType.PURCHASE, "000", "01.01.2016", "Hans@ufo.com");
		cart.addTicket(new CCinemaTicket("Boat", price, Integer.valueOf(4), Integer.valueOf(2), Long.valueOf(1), "Kino 1", "10.25 (10.01.2016)", Etype.Parkett, "Godfather"));
		cartRepo.save(cart);
		mvc.perform(post("/myorders.html")
				.with(user("customer").roles("CUSTOMER")))
				.andExpect(view().name("myorders"))
				.andExpect(model().attributeExists("orders"))
				.andExpect(model().attribute("orders", is(emptyIterable())))
				.andExpect(status().isOk());
		
		mvc.perform(post("/myorders.html")
				.with(user("Hans").roles("CUSTOMER")))
				.andExpect(view().name("myorders"))
				.andExpect(model().attributeExists("orders"))
				.andExpect(status().isOk());
	}
	
	@Test
	@Transactional
	public void releaseSeatsIntegrationTest() throws Exception {
		CEvent event = dbEvent.findById(Long.valueOf(1));
		event.getO_room().changeSeatStatus(1, 1, EStatus.RESERVED, dbDate.save(new CDateStorage(businessTime.getTime().minusHours(1))));
		event.getO_room().changeSeatStatus(2, 1, EStatus.RESERVED, dbDate.save(new CDateStorage(businessTime.getTime().plusHours(1))));
		event.getO_room().changeSeatStatus(1, 3, EStatus.PAID, dbDate.save(new CDateStorage()));
		dbseat.save(event.getO_room().getSeat(1, 1));
		dbseat.save(event.getO_room().getSeat(2, 1));
		dbseat.save(event.getO_room().getSeat(1, 3));
		mvc.perform(post("/releaseseats/1")
				.with(user("user").roles("EMPLOYEE"))
				.param("ID", "1")
				.param("row", "1")
				.param("seat", "1"))
				.andExpect(view().name("redirect:/event/" + event.getId()))
				.andExpect(status().isFound());
		
		mvc.perform(post("/releaseseats/1")
				.with(user("user").roles("EMPLOYEE"))
				.param("ID", "1")
				.param("row", "500")
				.param("seat", "500"))
				.andExpect(view().name("redirect:/event/" + event.getId()))
				.andExpect(status().isFound());
	}
	
	@Test
	public void rateIntegrationTest() throws Exception {
		mvc.perform(get("/rate.html")
				.with(user("Hans").roles("CUSTOMER")))
				.andExpect(view().name("rate"))
				.andExpect(status().isOk());
		
		mvc.perform(post("/rate.html")
				.with(user("Hans").roles("CUSTOMER"))
				.param("movie", "1")
				.param("comment", "ABC")
				.param("rating", "5"))
				.andExpect(view().name("redirect:/rate.html"))
				.andExpect(status().isFound());
		
		mvc.perform(post("/rate.html")
				.with(user("Hans").roles("CUSTOMER"))
				.param("movie", "1")
				.param("comment", "ABC")
				.param("rating", "25"))
				.andExpect(model().attributeExists("form", "orders"))
				.andExpect(view().name("rate"))
				.andExpect(status().isOk());
	}
}
