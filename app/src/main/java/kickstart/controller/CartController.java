package kickstart.controller;

import kickstart.Repositorys.*;
import kickstart.SavedClasses.CCart;
import kickstart.model.CartHelper;
import kickstart.SavedClasses.CCart.CartType;
import kickstart.SavedClasses.CDateStorage;
import kickstart.SavedClasses.CCinemaTicket;
import kickstart.SavedClasses.CCinemaTicket.PriceClass;
import kickstart.SavedClasses.CEvent;
import kickstart.model.eventManagement.CProgram;
import kickstart.SavedClasses.CSeat;
import kickstart.SavedClasses.CSeat.EStatus;
import kickstart.SavedClasses.SaveRoom;

import org.javamoney.moneta.Money;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * @author Beatrice
 * @since  08.11.15.
 */

@Controller
@SessionAttributes("cart")
public class CartController {

	private CProgram cinema_program;
	private List<CCinemaTicket> ticketlist = null;
	private BusinessTime businessTime;
	private final Integer minutesBeforeMovieDeleteReservation = 30;
	private final Integer minutesUntilClearReservation = 10;
	private CartRepo cartrepo;
	private TicketCatalog ticketCatalog;
	private DBRoom dbRoom;
	private CartHelper cartHelper;
	private DBEvent dbEvent;
	private DBDate dbDate;
	private DBSeat dbSeat;
	
	//Step 1  Object
	private CMail MyMailSender;
	
	
	//Step 2 Get the reference																														
	/**
	 * The CartController constructor it gets some of its attributes from Spring
	 * @param businessTime  BusinessTime					: Provided by SalesPoint, its the time
	 * @param cartrepo		CartRepo						: Provided by SalesPoint, (database)
	 * @param ticketCatalog	TicketCatalog					: Its the ticket catalog contains all tickets
	 * @param dbRoom		DBRoom							: Database Room, needed to save the room "structure"(string)
	 * @param sender		MailSender						: Spring-Mail-Sender sends the SimpleMail, provided by Salespoint	
	 * @param dbSeat DBSeat seat database
	 * @param cProgram CProgram the program
	 * @param dbDate DBDate CDateStorage database
	 * @param dbEvent DBEvent event database
	 */
	@Autowired
	public CartController(DBSeat dbSeat, CProgram cProgram, BusinessTime businessTime, CartRepo cartrepo, TicketCatalog ticketCatalog, DBRoom dbRoom, MailSender sender, DBDate dbDate, DBEvent dbEvent) {
		this.businessTime = businessTime;
		cinema_program = cProgram;
		this.cartrepo = cartrepo;
		this.ticketCatalog = ticketCatalog;
		this.dbRoom = dbRoom;
		this.dbEvent = dbEvent;
		this.dbDate = dbDate;
		this.dbSeat = dbSeat;
		//Step 3 Give it to the obj. at cration
		MyMailSender = new CMail(sender);
		//Step 4/5 Send the mail WARNING!!!! ACTIVATE FAKE SMPT OR CRASH (located in src/main/java)
		//MyMailSender.SendMail("feafbr@gmail", "Title", "Message");
		cartHelper = new CartHelper(businessTime,cartrepo,ticketCatalog,dbEvent, dbDate, dbSeat);
	}
	
	/**
	 * Initialize a new Cart
	 * @return new Cart : Returns a new Cart object
	 */
	@ModelAttribute("cart")
	public Cart initializeCart() {
		return new Cart();
	}

	/**
	 * Method called every time the cart contents are read.
	 * It checks the cart's contents for tickets that have since
	 * passed their cart reservation time or have been made AVAILABLE
	 * by another session.
	 * @param cart 					Cart 				: type of repository that contains all Products currently in the user's cart
	 * @param model					Model				: Provided by SalesPoint
	 * @return cart.html
	 */
	@RequestMapping(value = "/cart", method = RequestMethod.GET)
	public String basket(@ModelAttribute Cart cart, Model model) {
		// separate list for tickets marked for removal, as to not break the iterator
		List<String> removeList = new ArrayList<String>();
		LocalDateTime localDateTime = businessTime.getTime();

	    //loop checking for invalid tickets, still needs to be improved (doesn't work in all cases)
		for (CartItem items : cart){
			CCinemaTicket ticket = (CCinemaTicket) items.getProduct();
			CEvent event = cinema_program.findEventByID(ticket.getEventID());
			
			//clear all invalid reservations in event room
			for (CSeat seat : event.getO_room().clearTimedout(localDateTime, dbDate.save(new CDateStorage(localDateTime.minusMinutes(Long.valueOf(1)))))){
				dbSeat.save(seat);
			}
			
			//remove ticket from cart if seat has been freed by previous check
			if (event.getO_room().getSeatStatus(ticket.getI_row(), ticket.getI_seat()).equals(EStatus.AVAILABLE) || event.getO_room().getSeatStatus(ticket.getI_row(), ticket.getI_seat()).equals(EStatus.PAID)){
				removeList.add(items.getIdentifier());
			}

			//if one or more tickets are found invalid, they're removed
		}
		for (String ID : removeList){
			cart.removeItem(ID);
		}

		model.addAttribute("total", cartHelper.formatMoney(cart.getPrice()));

		return "cart";
	}
	
	/**
	 * Method called upon when a purchase is made.
	 * The cart's contents are once again checked for invalid tickets
	 * (expired or AVAILABLE). If any are found, they are removed and
	 * the user is returned to the cart page.
	 * Otherwise, it checks the user's ROLE and creates a corresponding
	 * CCart object, completing the purchase and clearing the cart.
	 * @param cart 					Cart							: type of repository that contains all Products currently in the user's cart
	 * @param userAccount 			Optional &#60;UserAccount&#62; 	: active userAccount
	 * @param model 				Model 							: Provided by SalesPoint
	 * @param redirectAttributes 	RedirectAttributes 				: Provided by Spring
	 * @return buy.html if successful, cart.html if failed
	 */
	@RequestMapping(value = "/buy", method = RequestMethod.POST)
	@PreAuthorize(value = "isAuthenticated()")
	public String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount, Model model, RedirectAttributes redirectAttributes) {
		String date = businessTime.getTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));//String saving current date
		
		//check if all tickets in cart can still be sold, returns to cart page if not, displays error
		if (cartHelper.checkInvalid(cart, businessTime.getTime())){
			redirectAttributes.addFlashAttribute("error", new String("purchase"));
			return "redirect:cart";
		}
		
		//generate email and check if mail server is active before trying to sell to a customer
    	if (userAccount.get().hasRole(Role.of("ROLE_CUSTOMER"))){
    		String message = "Vielen Dank für Ihren Kartenkauf bei UFO Kino. Sollten sie das Ihnen angezeigte Kartenblatt ausversehen geschlossen oder verloren haben, wenden Sie sich bitte vor Ort an die Kasse.";
    		try {
    			MyMailSender.SendMail(userAccount.get().getEmail(), "Ihr Kauf bei UFO Kino", message);
    		}catch (MailException me){
    			redirectAttributes.addFlashAttribute("mailserver", new String("purchase"));
    			return "redirect:cart";
    		}
    	}
		
		//creating the cart object, all tickets are valid
		CCart cc = new CCart(userAccount.get().getFirstname() + " " + userAccount.get().getLastname(), CartType.PURCHASE, "000", date, userAccount.get().getEmail());		// Creating CCart-transaction with default 000
		cartrepo.save(cc);	
		
		// creating actual tickets for the transaction, added to the cart's list
		for (CartItem items : cart){
			
			//valid tickets are marked PAID
			CCinemaTicket ticket = (CCinemaTicket) items.getProduct();
			CEvent event = cinema_program.findEventByID(ticket.getEventID());
			event.getO_room().changeSeatStatus(ticket.getI_row(), ticket.getI_seat(), CSeat.EStatus.PAID, dbDate.save(new CDateStorage()));
			dbSeat.save(event.getO_room().getSeat(ticket.getI_row(), ticket.getI_seat()));
			ticket.removeCategory("reservation");
			cc.addTicket(ticket);
			
			//called if user is customer
			if (userAccount.get().hasRole(Role.of("ROLE_CUSTOMER"))){
				ticket.addCategory("online_purchase");
			}
			//called if user is employee, authorized or boss
			else {
				ticket.addCategory("direct_purchase");
			}
			ticketCatalog.save(ticket);
		}
		model.addAttribute("customer", userAccount.get().getFirstname() + " " + userAccount.get().getLastname());
	    model.addAttribute("code", cc.getId().toString());
		model.addAttribute("tickets", cc.getTickets());
		cart.clear();
		return "buy";
    }
	
	/**
	 * Method to RESERVE tickets.
	 * All used attributes are read out of the Salespoint
	 * cart ModelAttribute. None are manually submitted.
	 * 
	 * @param userAccount 	Optional&#60;UserAccount&#62;	: active userAccount
	 * @param cart 			Cart							: type of repository that contains all Products currently in the user's cart
	 * @param model			Model							: Provided by SalesPoint
	 * @param redirectAttributes							: Provided by Spring
	 * @param mail 			String							: The email for the reservation
	 * @return reserve.html
	 */
    @RequestMapping (value = "/reserve", method = RequestMethod.POST)
    public String reserve(@LoggedIn Optional<UserAccount> userAccount, @ModelAttribute Cart cart, Model model, RedirectAttributes redirectAttributes, @RequestParam("mail") String mail){
    	if (!userAccount.isPresent()) {
    		if (!mail.matches("[A-Za-z_0-9.!#$%&'*+/=?^_`{|}~-]+\\@[A-Za-z_0-9-]+(\\.[a-zA-Z0-9-]+)+")){
    			redirectAttributes.addFlashAttribute("mailerror", new String("Bitte einen gültige Email-Adresse angeben!"));
    			return "redirect:cart";
    		}
    	}
    	
		String date = businessTime.getTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));//String saving current date
		String email = "";
		
		//check if all tickets in cart can still be reserved, returns to cart page if not, displays error.
		if (cartHelper.checkInvalidRes(cart, businessTime.getTime(), minutesBeforeMovieDeleteReservation, cinema_program)){
			redirectAttributes.addFlashAttribute("error", new String("reservation"));
			return "redirect:cart";
		}
		
		//creating reservation code, all tickets are valid
    	String resCode = cartHelper.generateCode();
    	model.addAttribute("code", resCode);
    	
    	//generate email and check if mail server is active before trying to reserve
    	if (!userAccount.isPresent() || userAccount.get().hasRole(Role.of("ROLE_CUSTOMER"))){
    		if (!userAccount.isPresent()){
    			email = mail;
    		}
    		if (userAccount.isPresent() && userAccount.get().hasRole(Role.of("ROLE_CUSTOMER"))){
    			email = userAccount.get().getEmail();
    		}
    		String message = "Vielen Dank für Ihre Reservierung bei UFO Kino. Dies ist Ihre Reservierungsnummer: " + resCode + " Bitte kommen sie mindestens 30 Minuten vor Filmbeginn zur Kasse, um Ihre Reservierung zu bezahlen";
    		try {
    			MyMailSender.SendMail(email, "Ihre Reservierung bei UFO Kino", message);
    		}catch (MailException me){
    			redirectAttributes.addFlashAttribute("mailserver", new String("purchase"));
    			return "redirect:cart";
    		}
    	}

    	//mail sent successfully, now finally creating cart
    	CCart cc = null;
    	//called for any logged-in user
    			if (userAccount.isPresent()) {
    				cc = new CCart(userAccount.get().getFirstname() + " " + userAccount.get().getLastname(), CartType.RESERVATION, resCode, date, userAccount.get().getEmail());
    			}
    			//called for non-registered users
    			else {
    				cc = new CCart(mail, CartType.RESERVATION, resCode, date, "guest");
    			}

    	//adding items to cart
		for (CartItem items : cart){
			CCinemaTicket ticket = (CCinemaTicket) items.getProduct();
			CEvent event = cinema_program.findEventByID(ticket.getEventID());
			//tickets belonging to Reservations get their name set to the corresponding cart reservation code
			ticket.setName(cc.getResCode());
			ticketCatalog.save(ticket);
			cc.addTicket(ticket);

			//sets reservation max time to 30min before event start
			CDateStorage tmp = new CDateStorage(event.getO_startDate());
			tmp.subMinutes(minutesBeforeMovieDeleteReservation);
			
			event.getO_room().changeSeatStatus(ticket.getI_row(), ticket.getI_seat(), CSeat.EStatus.RESERVED, dbDate.save(tmp));
			dbSeat.save(event.getO_room().getSeat(ticket.getI_row(), ticket.getI_seat()));
		}
		cartrepo.save(cc);
		model.addAttribute("tickets", cc.getTickets());
		cart.clear();
        return "reserve";
    }
    
    /**
     * Completely clears out a user's current cart, and
     * sets all selected CSeats back to AVAILABLE. All
     * tickets are removed from the TicketCatalog.
     * @param cart Cart	: type of repository that contains all Products currently in the user's cart
     * @return cart.html
     */
	@RequestMapping(value = "/empty")
	public String empty(@ModelAttribute Cart cart) {
		for (CartItem items : cart){
			CCinemaTicket ticket = (CCinemaTicket) items.getProduct();
			CEvent event = cinema_program.findEventByID(ticket.getEventID());
			event.getO_room().changeSeatStatus(ticket.getI_row(), ticket.getI_seat(), CSeat.EStatus.AVAILABLE, dbDate.save(new CDateStorage()));
			dbSeat.save(event.getO_room().getSeat(ticket.getI_row(), ticket.getI_seat()));
		}
		cart.clear();
	return "redirect:cart";
    }
	
	/**
     * Removes a single ticket from cart and frees the seat
     * @param cart 		Cart	: type of repository that contains all Products currently in the user's cart
     * @param cartItem 	String	: Single Cart Item
     * @return cart.html
     */
	@RequestMapping(value = "/removeone")
	public String removeOne(@ModelAttribute Cart cart, @RequestParam("ticket") String cartItem) {
		CCinemaTicket ticket = (CCinemaTicket) cart.getItem(cartItem).get().getProduct();
		CEvent event = cinema_program.findEventByID(ticket.getEventID());
		event.getO_room().changeSeatStatus(ticket.getI_row(), ticket.getI_seat(), CSeat.EStatus.AVAILABLE, dbDate.save(new CDateStorage()));
		dbSeat.save(event.getO_room().getSeat(ticket.getI_row(), ticket.getI_seat()));
		cart.removeItem(cartItem);
	return "redirect:cart";
    }
	
	
	/**
	 * Method adding a CCinemaTicket to the user's cart every time a seat
	 * is selected on the event page. The selected seat is marked RESERVED
	 * for a limited time and the detail page of the event is reloaded.
	 * @param cart					Cart				:The Cart and its containment
	 * @param ID					Integer				: Event ID
	 * @param row					Integer				: selected seat's row
	 * @param number				Integer				: selected seat number
	 * @param redirectAttributes 	RedirectAttributes	: sends seat and row attributes to side display of selected tickets, still buggy
	 * @return event/ID/
	 */
	@RequestMapping("/cart/{ID}/{row}/{number}")
	public String event(@ModelAttribute Cart cart, @PathVariable("ID") Long ID, @PathVariable("row") Integer row, @PathVariable("number") Integer number, RedirectAttributes redirectAttributes) {
		LocalDateTime localDateTime = businessTime.getTime();
		CEvent event = dbEvent.findById(ID);

		//check if ticket, that is trying to be added, already exists in cart
		for (CartItem item : cart){
			CCinemaTicket ticket = (CCinemaTicket) item.getProduct();
			if (ticket.getName().equals(event.getMovie().getName()) && ticket.getI_row().equals(row) && ticket.getI_seat().equals(number)){
				redirectAttributes.addFlashAttribute("tickets", ticketlist);
				event.getO_room().changeSeatStatus(ticket.getI_row(),ticket.getI_seat(), EStatus.AVAILABLE, dbDate.save(new CDateStorage()));
				dbSeat.save(event.getO_room().getSeat(ticket.getI_row(), ticket.getI_seat()));
				cart.removeItem(item.getIdentifier());
				return "redirect:/event/" + event.getId();
			}
		}
		//returns to event view if seat has already been reserved or bought while the page was open
		if (event.getO_room().getSeatStatus(row, number).equals(EStatus.RESERVED) || event.getO_room().getSeatStatus(row, number).equals(EStatus.PAID)){
			redirectAttributes.addFlashAttribute("tickets", ticketlist);
			return "redirect:/event/" + event.getId();
		}
		//creates the new CCinemTicket with all received input variables
		SaveRoom check = dbRoom.findById(event.getO_room().getI_id());
		Money price = Money.of(new BigDecimal(check.getPrice(event.getO_room().getSeat(row, number).getE_Type())), "EUR").add(Money.of(new BigDecimal(event.getMovie().getBaseCost()), "EUR"));
		CCinemaTicket ticket = new CCinemaTicket(event.getMovie().getName(), price, row, number, event.getId(),event.getO_room().getS_name(), event.getStartTime(), event.getO_room().getSeat(row,number).getE_Type(), event.getMovie().getName());
		ticket.addCategory("reservation");
		cart.addOrUpdateItem(ticket, Quantity.of(1));		//ticket is added to cart

		//setting up the html ticketlist
		ticketlist = new ArrayList<CCinemaTicket>();
		for (CartItem item : cart){
			CCinemaTicket tmp = (CCinemaTicket) item.getProduct();
			if (tmp.getEventID().equals(ID)){
			ticketlist.add((CCinemaTicket) item.getProduct());
			}
		}

		redirectAttributes.addFlashAttribute("tickets", ticketlist);
		CDateStorage actualTime = new CDateStorage(localDateTime.getMinute(),
				localDateTime.getHour(),
				localDateTime.getDayOfMonth(),
				localDateTime.getMonth().getValue(),
				localDateTime.getYear());
		actualTime.addMinutes(minutesUntilClearReservation);
		//sets the selected seat to RESERVED
		event.getO_room().changeSeatStatus(row, number, CSeat.EStatus.RESERVED, dbDate.save(actualTime));
		dbSeat.save(event.getO_room().getSeat(row, number));

		return "redirect:/event/" + event.getId();

	}

	/**
	 * Method changing the price class of a single cart item, price difference hardcoded for now.
	 * @param cart 			Cart	: type of repository that contains all Products currently in the user's cart
	 * @param cartItem 		String	: Identifier of the selected cart item
	 * @param priceclass 	String	: target priceclass selected by user
	 * @return cart.html
	 */
	
	@RequestMapping(value = "changeclass")
	public String changeClass(@ModelAttribute Cart cart, @RequestParam("cartitem") String cartItem, @RequestParam("priceclass") String priceclass) {
		Money priceDifference = Money.of(new BigDecimal("2.00"), "EUR");
		CCinemaTicket ticket = (CCinemaTicket) cart.getItem(cartItem).get().getProduct();
		if (priceclass.equals("full")){
			if (ticket.getPriceClass().equals(PriceClass.FULL)){
				return "cart";
			}
			ticket.setPriceClass(PriceClass.FULL);
			ticket.setPrice(ticket.getPrice().add(priceDifference));
		}
		if (priceclass.equals("reduced")){
			if (ticket.getPriceClass().equals(PriceClass.REDUCED)){
				return "cart";
			}
			ticket.setPriceClass(PriceClass.REDUCED);
			ticket.setPrice(ticket.getPrice().subtract(priceDifference));
			
		}
		cart.addOrUpdateItem(ticket, Quantity.of(0));
		return "redirect:cart";
    }
	
    /**
     * Leads you to the event page
     * @param ID Long the specific Id of the event
     * @param model Model provided by Spring
     * @param cart Cart cart modelattribute provided by SalesPoint
     * @return event.html
     */
    @RequestMapping("/event/{ID}")
    public String event(@ModelAttribute Cart cart, @PathVariable("ID") Long ID, Model model ) {

        CEvent event = dbEvent.findById(ID);
        LocalDateTime localDateTime = businessTime.getTime();
        for (CSeat seat : event.getO_room().clearTimedout(localDateTime, dbDate.save(new CDateStorage(localDateTime.minusMinutes(Long.valueOf(1)))))){
        	dbSeat.save(seat);
        }

		List <CSeat> selectedSeats = new ArrayList<>();
		ticketlist = new ArrayList<CCinemaTicket>();
		for (CartItem item : cart){
			CCinemaTicket tmp = (CCinemaTicket) item.getProduct();
			if (tmp.getEventID().equals(ID)){
				ticketlist.add((CCinemaTicket) item.getProduct());
				selectedSeats.add(event.getO_room().getSeat(tmp.getI_row(),tmp.getI_seat()));
			}
		}

    	CDateStorage current = new CDateStorage(businessTime.getTime());
		current.addMinutes(30);
		if (event.getO_startDate().erlierThen(current)){
			model.addAttribute("time", "value");
		}

		model.addAttribute("selected", selectedSeats);
		model.addAttribute("tickets", ticketlist);
		model.addAttribute("event", event);
		model.addAttribute("server", System.getProperty("catalina.home"));
        return "event";
    }
	
}