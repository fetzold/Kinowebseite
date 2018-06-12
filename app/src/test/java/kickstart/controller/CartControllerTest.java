package kickstart.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import java.math.BigDecimal;

import kickstart.AbstractWebIntegrationTest;
import kickstart.Application;
import kickstart.Repositorys.TicketCatalog;
import kickstart.SavedClasses.CCinemaTicket;
import kickstart.SavedClasses.CSeat.Etype;

import org.javamoney.moneta.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salespointframework.catalog.Product;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CartControllerTest extends AbstractWebIntegrationTest {
	
	@Autowired private TicketCatalog ticketCatalog;
	
	@Test
	public void basketIntegrationTest() throws Exception {
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		Cart cart = new Cart();
		CCinemaTicket ct = new CCinemaTicket("Boat", price, Integer.valueOf(3), Integer.valueOf(3), Long.valueOf(1), "Kino 1", "10.25 (12.11.2016)", Etype.Parkett, "Godfather");
		ticketCatalog.save(ct);
		cart.addOrUpdateItem(ct, Quantity.of(1));
		mvc.perform(get("/cart")
				.sessionAttr("cart", new Cart()))
				.andExpect(view().name("cart"))
				.andExpect(model().attributeExists("total"))
				.andExpect(model().attribute("cart", is(emptyIterable())))
				.andExpect(status().isOk());
	}
	
	@Test
	public void emptyIntegrationTest() throws Exception {
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		Cart cart = new Cart();
		cart.addOrUpdateItem(new Product("Karte", price), Quantity.of(1));
		mvc.perform(get("/empty"))
			.andExpect(view().name("redirect:cart"))
			.andExpect(model().attributeExists("cart"))
			.andExpect(status().isFound());
		
		mvc.perform(get("/empty"))
				.andExpect(view().name("redirect:cart"))
				.andExpect(model().attributeExists("cart"))
				.andExpect(status().isFound());
	}

	@Test
	public void removeOneIntegrationTest() throws Exception {
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		Cart cart = new Cart();
		CCinemaTicket ct = new CCinemaTicket("Boat", price, Integer.valueOf(3), Integer.valueOf(3), Long.valueOf(1), "Kino 1", "10.25 (12.11.2016)", Etype.Parkett, "Godfather");
		ticketCatalog.save(ct);
		cart.addOrUpdateItem(ct, Quantity.of(1));
		String item = "";
		for (CartItem c : cart){
			item = c.getIdentifier();
			break;
		}
		mvc.perform(get("/removeone")
				.sessionAttr("cart", new Cart())
				.flashAttr("cart", cart)
				.param("cart", "cart")
				.param("ticket", item))
				.andExpect(view().name("redirect:cart"))
				.andExpect(model().attributeExists("cart"))
				.andExpect(status().isFound());
	}
	
	@Test
	public void eventIntegrationTest() throws Exception {
		mvc.perform(get("/event/1"))
				.andExpect(view().name("event"))
				.andExpect(model().attributeExists("selected", "tickets", "event"))
				.andExpect(status().isOk());
		
		mvc.perform(get("/cart/1/2/2"))
				.andExpect(view().name("redirect:/event/1"))
				.andExpect(status().isFound());
		
		mvc.perform(get("/cart/1/2/2"))
				.andExpect(view().name("redirect:/event/1"))
				.andExpect(status().isFound());
	}
	
	@Test
	public void changeClassIntegrationTest() throws Exception {
		
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("Boat", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		Cart cart = new Cart();
		cart.addOrUpdateItem(ticket, Quantity.of(1));
		String id = "";
		
		for (CartItem p : cart){
			id = p.getIdentifier();
		}
		
		mvc.perform(get("/changeclass")
				.sessionAttr("cart", new Cart())
				.flashAttr("cart", cart)
				.param("cartitem", id)
				.param("priceclass", "full"))
				.andExpect(view().name("cart"))
				.andExpect(status().isOk());
		
		mvc.perform(get("/changeclass")
				.sessionAttr("cart", new Cart())
				.flashAttr("cart", cart)
				.param("cartitem", id)
				.param("priceclass", "reduced"))
				.andExpect(view().name("redirect:cart"))
				.andExpect(status().isFound());
	}
	
	@Test
	public void buyIntegrationTest() throws Exception {
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("Boat", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		Cart cart = new Cart();
		cart.addOrUpdateItem(ticket, Quantity.of(1));
		
		mvc.perform(post("/buy")
				.with(user("Hans").roles("Customer"))
				.sessionAttr("cart", new Cart())
				.flashAttr("cart", cart))
				.andExpect(view().name("redirect:cart"))
				.andExpect(flash().attributeExists("error"))
				.andExpect(status().isFound());
		
		mvc.perform(post("/buy")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("buy"))
				.andExpect(model().attributeExists("customer", "code", "tickets"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void reserveIntegrationTest() throws Exception {
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("Boat", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		Cart cart = new Cart();
		cart.addOrUpdateItem(ticket, Quantity.of(1));
		
	mvc.perform(post("/reserve")
				.with(user("boss").roles("BOSS"))
				.param("mail", "boss@ufo.de"))
				.andExpect(view().name("reserve"))
				.andExpect(model().attributeExists("tickets"))
				.andExpect(status().isOk());
	
	mvc.perform(post("/reserve")
			.with(user("Hans").roles("Customer"))
			.sessionAttr("cart", new Cart())
			.flashAttr("cart", cart)
			.param("mail", "abcde"))
			.andExpect(view().name("redirect:cart"))
			.andExpect(flash().attributeExists("error"))
			.andExpect(status().isFound());
	}
	
	@Test
	public void reserveNoUserIntegrationTest() throws Exception {
		Money price = Money.of(new BigDecimal("2.50"), "EUR");
		CCinemaTicket ticket = new CCinemaTicket("Boat", price, Integer.valueOf(1), Integer.valueOf(1), Long.valueOf(1), "Kino 1", "10.50 (25.12.2016)", Etype.Loge, "Boat");
		Cart cart = new Cart();
		cart.addOrUpdateItem(ticket, Quantity.of(1));
		
	mvc.perform(post("/reserve")
			.param("mail", "abcde"))
			.andExpect(view().name("redirect:cart"))
			.andExpect(flash().attributeExists("mailerror"))
			.andExpect(status().isFound());
	}
}
