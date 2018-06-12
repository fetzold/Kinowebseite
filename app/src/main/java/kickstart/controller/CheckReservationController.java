package kickstart.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import kickstart.Application;
import kickstart.Repositorys.CartRepo;
import kickstart.Repositorys.DBComment;
import kickstart.Repositorys.DBDate;
import kickstart.Repositorys.DBEvent;
import kickstart.Repositorys.DBMovie;
import kickstart.Repositorys.DBSeat;
import kickstart.Repositorys.TicketCatalog;
import kickstart.SavedClasses.CCart;
import kickstart.model.CartHelper;
import kickstart.SavedClasses.CCart.CartType;
import kickstart.SavedClasses.CDateStorage;
import kickstart.SavedClasses.CCinemaTicket;
import kickstart.SavedClasses.CEvent;
import kickstart.SavedClasses.CMovie;
import kickstart.model.eventManagement.CProgram;
import kickstart.validation.CommentForm;
import kickstart.SavedClasses.CSeat;
import kickstart.SavedClasses.CSeat.EStatus;
import kickstart.SavedClasses.Comment;

import org.javamoney.moneta.Money;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;

/**
 * CartController: connection between the html and java part of the cart
 * @author Beatrice
 * @since  08.11.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Controller
public class CheckReservationController {
	
	private CProgram cprogram;
	private CartRepo cartrepo;
	private BusinessTime businessTime;
	private TicketCatalog ticketCatalog;
	private CartHelper cartHelper;
	private DBDate dbDate;
	private DBSeat dbSeat;
	private DBMovie dbMovie;
	private DBComment dbComment;
    private DBEvent dbEvent;
        
	/**
	 * Constructor of the Class, creates a controller which checks for reservations
	 * @param businessTime	BusinessTime					: Provided by SalesPoint, its the Time
	 * @param cartrepo		CartRepo						: Provided by SalesPoint, database
	 * @param ticketCatalog	TicketCatalog					: Ticket catalog, contains all the tickets
	 * @param  dbComment DBComment comment database
	 * @param  dbSeat DBSeat seat database
	 * @param  cProgram CProgram the program
	 * @param  dbDate DBDate CDateStorage database
	 * @param dbMovie DBMovie movie database
	 * @param  dbEvent DBEvent event database
	 */
	@Autowired
	public CheckReservationController(DBMovie dbMovie, DBComment dbComment, DBSeat dbSeat, CProgram cProgram, BusinessTime businessTime, CartRepo cartrepo, TicketCatalog ticketCatalog, DBDate dbDate, DBEvent dbEvent) {
		this.businessTime = businessTime;
		this.dbDate = dbDate;
		this.cartrepo = cartrepo;
		this.ticketCatalog = ticketCatalog;
		this.dbSeat = dbSeat;
        this.dbEvent = dbEvent;
        this.dbComment = dbComment;
        this.dbMovie = dbMovie;
		cprogram = cProgram;
		cartHelper = new CartHelper(businessTime, cartrepo, ticketCatalog, dbEvent, dbDate, dbSeat);
	}

	/**
	 * Displays the main page of ticket and reservation management.
	 * Currently iterates through the CCart repository in an attempt to display all currently used
	 * objects for testing purposes.
	 * @return checkreservation.html
	 */
	@RequestMapping(value="checkreservation", method = RequestMethod.GET)
	@PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED', 'ROLE_EMPLOYEE')")
    public String ticketmain() {
		CDateStorage cds = new CDateStorage(businessTime.getTime());
		for (CEvent m : dbEvent.findAll()){
			if (m.getO_startDate().laterThan(cds)){
				for (CSeat seat : m.getO_room().clearTimedout(businessTime.getTime(), dbDate.save(new CDateStorage(businessTime.getTime().minusMinutes(Long.valueOf(1)))))){
					dbSeat.save(seat);
				}
			}
		}
		for (CCart cart : cartrepo.findByCartType(CartType.RESERVATION)){
			for (CCinemaTicket t : cart.getTickets()){
				for (String c : t.getCategories()){
					if (c.equals("reservation") && dbEvent.findById(t.getEventID()).getO_room().getSeat(t.getI_row(), t.getI_seat()).getReservedUntil().erlierThen(cds)){
						t.removeCategory("reservation");
						t.addCategory("canceled");
					}
				}
			}
			cartrepo.save(cart);
		}
		ArrayList<CCart> canceledCarts = new ArrayList<CCart>();
		
		for (CCart cart : cartrepo.findByCartType(CartType.PURCHASE)){
			int a = 0;
			int b = 0;
			for (CCinemaTicket t : cart.getTickets()){
				a++;
				if (t.getCategories().toString().equals("canceled")){
					b++;
				}
					
			}
			if (a == b){
				canceledCarts.add(cart);
			}
		}
		for (CCart cart : cartrepo.findByCartType(CartType.RESERVATION)){
			int a = 0;
			int b = 0;
			for (CCinemaTicket t : cart.getTickets()){
				a++;
				if (t.getCategories().toString().equals("canceled")){
					b++;
				}
			}
			if (a == b){
				canceledCarts.add(cart);
			}
		}
		
		for (CCart cart : canceledCarts){
			cart.setCartType(CartType.CANCELED);
			cartrepo.save(cart);
		}
		
		
        return "checkreservation";
    }

	/**
	 * Checks if an entered transaction ID exists in the database
	 * and returns all relevant information including the tickets
	 * belonging to the transaction.
	 * Tickets have to be found separately as they cannot be saved
	 * as attribute of CCart due to persistence functionality.
	 * @param resCode				String 				: value read from user input of the transaction ID
	 * @param model 				Model 				: Provided by Spring
	 * @param redirectAttributes 	RedirectAttributes 	: Provided by Spring
	 * @return	ticketview.html when successful, otherwise notfound.html
	 */
	@RequestMapping(value="checkreservation", method = RequestMethod.POST)
	@PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED', 'ROLE_EMPLOYEE')")
	public String check(@RequestParam("resCode") String resCode, Model model, RedirectAttributes redirectAttributes) {
		Money total = Money.of(new BigDecimal("0.00"), "EUR");
		if (!resCode.equals("000")){
			for (CCart cart : cartrepo.findAll()){
				if (cart.getResCode().equals(resCode)){						//compares input code with object code
					for (CCinemaTicket ticket : cart.getTickets()){
						if (!ticket.getCategories().toString().contains("canceled")){
							total = total.add(ticket.getPrice());
						}
	        		}
					
					model.addAttribute("found", cart);
					model.addAttribute("included", cart.getTickets());
	        		model.addAttribute("total", cartHelper.formatMoney(total));
					return "ticketview";
				}
			}
		}
		redirectAttributes.addFlashAttribute("error", new String("rescode"));
		return "redirect:checkreservation";
  }
	
	/**
	 * Check the database for all transactions belonging to an entered buyer's name
	 * @param name 				String 				: first name and last name
	 * @param model 			Model				: provided by SalesPoint
	 * @param redirectAttributes RedirectAttributes : Provided by Spring
	 * @return cartview.html
	 */ 
	
	@RequestMapping(value="checkpurchase", method = RequestMethod.POST)
	@PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED', 'ROLE_EMPLOYEE')")
	public String checkpurchase(@RequestParam("name") String name, Model model, RedirectAttributes redirectAttributes) {
		List<CCart> cartlist = new ArrayList<CCart>();
		for (CCart cart : cartrepo.findAll()){
			if (cart.getUser().equals(name)){						//compares input name with object usernames
				cartlist.add(cart);
			}
		}
		if (cartlist.isEmpty()){
			redirectAttributes.addFlashAttribute("error", new String("name"));
			return "redirect:checkreservation";
		}
		model.addAttribute("found", cartlist);
		return "cartview";
  }
	

	/**
	 * Method called when looking up a specific cart, not necessarily a reservation
	 * @param Ident Long 	: The identification number of the event
	 * @param model Model 	: Provided by Spring
	 * @param Ident Long  	: cart identification number 
	 * @return ticket view of coressponding ID
	 */
	
    @RequestMapping("/ticketview/{Ident}")
    public String event(@PathVariable("Ident") Long Ident, Model model ) {
    	Money total = Money.of(new BigDecimal("0.00"), "EUR");
    	if (!cartrepo.findOne(Ident).isPresent()){
    		return "checkreservation";
    	}
    	CCart cart = cartrepo.findOne(Ident).get();
       	if (cart.getId().equals(Ident)){
    		int a = 0;
    		int b = 0;
    		for (CCinemaTicket t : cart.getTickets()){
    			a++;
    			if (t.getCategories().toString().contains("canceled")){
    				b++;
    			}
    		}
    		if (a == b){
    			cart.setCartType(CartType.CANCELED);
    			cartrepo.save(cart);
    		}
       		model.addAttribute("found", cart);
       		model.addAttribute("included", cart.getTickets());
       		for (CCinemaTicket ticket : cart.getTickets()){
       			if (!ticket.getCategories().toString().contains("canceled")){
       				total = total.add(ticket.getPrice());
       			}
       		}
       		
       		model.addAttribute("total", cartHelper.formatMoney(total));
       		return "ticketview";
        	}
        return "checkreservation";
    }
	
	/**
	 * Method called when canceling a single ticket.
	 * @param resCode	transaction-ID given by ticketview.html, currently not used
	 * @param ticket	ticket object given via ProductIdentifier by ticketview.html
	 * @param eventID 	ID of event of canceled ticket, allows going directly to event to issue a new ticket
	 * @param redirectAttributes RedirectAttributes Provided by Spring
	 * @param model Model : Provided by SalesPoint
	 * @return deleted.html placeholder
	 */
	
	@RequestMapping(value="cancelOne", method= RequestMethod.POST)
	@PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED', 'ROLE_EMPLOYEE')")
	public String cancelOne(@RequestParam("resCode") Long resCode, @RequestParam("ident") CCinemaTicket ticket, @RequestParam("eventID") Long eventID, Model model, RedirectAttributes redirectAttributes){
		CDateStorage current = new CDateStorage(businessTime.getTime());
		current.addMinutes(30);
		CEvent event = cprogram.findEventByID(eventID);
		if (event.getO_startDate().erlierThen(current)){
			redirectAttributes.addFlashAttribute("eventID", eventID);
			redirectAttributes.addFlashAttribute("resCode", resCode);
			redirectAttributes.addFlashAttribute("error", "time");
			return "redirect:/ticketview/" + resCode;
		}
		event.getO_room().changeSeatStatus(ticket.getI_row(), ticket.getI_seat(), EStatus.AVAILABLE, dbDate.save(new CDateStorage()));
		dbSeat.save(event.getO_room().getSeat(ticket.getI_row(), ticket.getI_seat()));
		ArrayList<String> cat = new ArrayList<String>();
		for (String c : ticket.getCategories()){
			cat.add(c);
		}
		for (String s : cat){
			ticket.removeCategory(s);
		}
		ticket.addCategory("canceled");
		ticketCatalog.save(ticket);
		model.addAttribute("eventID", eventID);
		model.addAttribute("resCode", resCode);
		Optional<CCart> cart = cartrepo.findOne(resCode);
		return "redirect:/ticketview/" + cart.get().getId();
	}
	
	/**
	 * Method called when canceling an entire order. Reservation ID is freed and Seats are made
	 * AVAILABLE. The original cart and all its tickets are recreated as canceled objects.
	 * Alternatively they could be deleted.
	 * @param cartID	cart-ID given by ticketview.html
	 * @param redirectAttributes RedirectAttributes provided by Spring
	 * @return ticketview of new cart
	 */
	
	@RequestMapping(value="cancelAll", method= RequestMethod.POST)
	@PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED', 'ROLE_EMPLOYEE')")
	public String cancelAll(@RequestParam("resCode") Long cartID, RedirectAttributes redirectAttributes){
		CDateStorage current = new CDateStorage(businessTime.getTime());
		current.addMinutes(30);
		CCart cart = cartrepo.findOne(cartID).get();
		for (CCinemaTicket t : cart.getTickets()){
			if (dbEvent.findById(t.getEventID()).getO_startDate().erlierThen(current)){
				redirectAttributes.addFlashAttribute("resCode", cartID);
				redirectAttributes.addFlashAttribute("error", "one");
				return "redirect:/ticketview/" + cartID;
			}
		}
		List<CCinemaTicket> removelist = new ArrayList<CCinemaTicket>();
		//frees the previous reservation ID if the card had been a reservation
		CCart newcart = new CCart(cart.getUser(), CartType.CANCELED, "000", cart.getTime(), cart.getUaccount());
		cartrepo.save(newcart);
		for (CCinemaTicket item : cart.getTickets()){
			CEvent event = cprogram.findEventByID(item.getEventID());
			removelist.add(item);
			event.getO_room().changeSeatStatus(item.getI_row(), item.getI_seat(), EStatus.AVAILABLE, dbDate.save(new CDateStorage()));
			dbSeat.save(event.getO_room().getSeat(item.getI_row(), item.getI_seat()));
			CCinemaTicket tmp = new CCinemaTicket(newcart.getId().toString(), item.getPrice(), item.getI_row(), item.getI_seat(), item.getEventID(),event.getO_room().getS_name(),event.getStartTime(), item.getSeattype(), event.getMovie().getName());
			tmp.addCategory("canceled");
			newcart.addTicket(tmp);
			cartrepo.save(newcart);
			}
		cartrepo.delete(cart);
		ticketCatalog.delete(removelist);
		return "redirect:/ticketview/" + newcart.getId();
	}
	
	/**
	 * Method called when paying for a reservation at the cash desk. Seats
	 * get marked as PAID. The cart type is marked as RESERVATIONPAID to
	 * for statistical purposes, retaining both its original reservation status
	 * and its completed payment.
	 * @param cartID 		String					: resCode	transaction-ID given by ticketview.html
	 * @param model			Model					: Provided by SalesPoint
	 * @return buy.html displaying the proper tickets for print
	 */
	
	@RequestMapping(value="pay", method= RequestMethod.POST)
	@PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED', 'ROLE_EMPLOYEE')")
	public String pay(@RequestParam("resCode") String cartID, Model model){
		CCart cart = cartrepo.findByResCode(cartID);
		CCart newcart = new CCart(cart.getUser(), CartType.PURCHASE, "000", cart.getTime(), cart.getUaccount());
		ArrayList<CCinemaTicket> ticketlist = new ArrayList<CCinemaTicket>();
		for (CCinemaTicket ticket : cart.getTickets()){
			if (!ticket.getCategories().toString().contains("canceled")){
				ticketlist.add(ticket);
			}
		}
		for (CCinemaTicket item : ticketlist){
				CEvent event = cprogram.findEventByID(item.getEventID());
				//changing seat status to paid, remove timer
				event.getO_room().changeSeatStatus(item.getI_row(), item.getI_seat(), EStatus.PAID, dbDate.save(new CDateStorage()));
				dbSeat.save(event.getO_room().getSeat(item.getI_row(), item.getI_seat()));
				item.removeCategory("reservation");
				item.addCategory("paid_reservation");
				ticketCatalog.save(item);
				newcart.addTicket(item);
		}
		cartrepo.delete(cart);
		cartrepo.save(newcart);
		model.addAttribute("tickets", newcart.getTickets());
		return "buy";
	}
	
	/**
	 * Method called when printing online paid tickets.
	 * @param cartID		String					: transaction-ID given by ticketview.html
	 * @param model			Model					: Provided by SalesPoint
	 * @return buy.html displaying the proper tickets for print
	 */
	
	@RequestMapping(value="print", method= RequestMethod.POST)
	@PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED', 'ROLE_EMPLOYEE')")
	public String print(@RequestParam("resCode") String cartID, Model model){
		
		ArrayList<CCinemaTicket> ticketlist = new ArrayList<CCinemaTicket>();
		
		for (CCart cart : cartrepo.findAll()){
			if (cart.getId().toString().equals(cartID)){
				for (CCinemaTicket ticket : cart.getTickets()){
					if (!ticket.getCategories().toString().contains("canceled")){
						ticketlist.add(ticket);
					}
				}
			}
		}
		
		model.addAttribute("tickets", ticketlist);
		return "buy";
	}
	
	/**
	 * releases seats by just their row and number given
	 * @param ID Long event's ID
	 * @param r String input Seat row
	 * @param s String input Seat number
	 * @return /event/{ID}
	 */
	
    @RequestMapping(value = "/releaseseats/{ID}" , method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyRole('ROLE_EMPLOYEE','ROLE_BOSS','ROLE_AUTHORIZED')")
    public String releaseSeats(@PathVariable("ID") Long ID, @RequestParam("row") String r,@RequestParam("seat") String s) {
    	CEvent event = cprogram.findEventByID(ID);
    	if (!s.matches("\\d+") || !r.matches("\\d+")){
    		return "redirect:/event/" + event.getId();
    	}
    	int row = new Integer(r);
    	int seat = new Integer(s);
    	if (event.getO_room().getSeat(row, seat) == null){
    		return "redirect:/event/" + event.getId();
    	}
        event.getO_room().changeSeatStatus(row, seat, CSeat.EStatus.AVAILABLE, dbDate.save(new CDateStorage()));
        dbSeat.save(event.getO_room().getSeat(row, seat));
        for (CCinemaTicket t : ticketCatalog.findAll()){
        	if (t.getEventID().equals(ID) && t.getI_row().equals(row) && t.getI_seat().equals(seat)){
        		ArrayList<String> cat = new ArrayList<String>();
        		for (String c : t.getCategories()){
        			cat.add(c);
        		}
        		for (String strng : cat){
        			t.removeCategory(strng);
        		}
        		t.addCategory("canceled");
        		ticketCatalog.save(t);
        		break;
        	}
        }
        return "redirect:/event/" + event.getId();
    }
    
    /**
     * Method collecting and displaying all active and completed transactions of a user
     * @param model Model provided by Spring
     * @param userAccount Optional &#60;UserAccount&#62; active userAccount
     * @return myorders.html
     */

    @RequestMapping("myorders.html")
    @PreAuthorize(value = "hasAnyRole('ROLE_CUSTOMER')")
    public String myorders(Model model, @LoggedIn Optional<UserAccount> userAccount){

    	LocalDateTime localDateTime = businessTime.getTime();

    	//build list of all carts belonging to person, regardless of status
		ArrayList<CCart> orders = new ArrayList<CCart>();
		for(CCart cart : cartrepo.findAll()){
			if (cart.getUaccount().matches(userAccount.get().getEmail())){
				orders.add(cart);
			}
		}
		
		//clearing rooms of all expired reservations
		for (CCart cart : orders){
			for (CCinemaTicket t : cart.getTickets()){
				for (CSeat seat : dbEvent.findById(t.getEventID()).getO_room().clearTimedout(localDateTime, dbDate.save(new CDateStorage(localDateTime.minusMinutes(Long.valueOf(1)))))){
					dbSeat.save(seat);
				}
			}
		}
		
		//find reservations that have been resold, and a list of tickets to delete
		ArrayList<CCinemaTicket> expiredTickets = new ArrayList<CCinemaTicket>();
		for (CCart cart : orders){
			for (CCinemaTicket t : cart.getTickets()){
				if (dbEvent.findById(t.getEventID()).getO_room().getSeat(t.getI_row(), t.getI_seat()).getE_Status().equals(EStatus.PAID) || dbEvent.findById(t.getEventID()).getO_room().getSeat(t.getI_row(), t.getI_seat()).getE_Status().equals(EStatus.AVAILABLE)){
					if (t.getCategories().toString().contains("reservation")){
						t.addCategory("canceled");
						t.removeCategory("reservation");
						expiredTickets.add(t);
					}
				}
			}
			cartrepo.save(cart);
		}
		model.addAttribute("orders", orders);
        return "myorders";
    }
	
    /**
     * Method providing a list of all written comments
     * @param form CommentForm providing the inputs of rating and text
     * @param model Model provided by Spring
     * @param userAccount Optional &#60;UserAccount&#62; active userAccount
     * @return rate.html
     */
    
    @RequestMapping(value="rate.html",  method = RequestMethod.GET)
    @PreAuthorize(value = "hasAnyRole('ROLE_CUSTOMER')")
    public String rate(CommentForm form, Model model, @LoggedIn Optional<UserAccount> userAccount){
		
		model.addAttribute("form", new CommentForm());
		model.addAttribute("orders", getWatchedMovies(userAccount.get().getEmail(), new CDateStorage(businessTime.getTime())));
        return "rate";
    }
    
    /**
     * Method taking the user's input and saving the to a Comment object
     * @param form CommentForm handling the inputs of rating and text
     * @param result BindingResult handling form errors
     * @param model Model provided by Spring
     * @param userAccount Optional &#60;UserAccount&#62; active userAccount
     * @param movie CMovie
     * @return redirect:/rate.html
     */
    
    @RequestMapping(value="rate.html" , method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyRole('ROLE_CUSTOMER')")
    public String rate(@ModelAttribute("form")  @Valid CommentForm form, BindingResult result, Model model, @LoggedIn Optional<UserAccount> userAccount, @RequestParam("movie") CMovie movie){
    	if (result.hasErrors()) {
    		model.addAttribute("form", form);
    		model.addAttribute("orders", getWatchedMovies(userAccount.get().getEmail(), new CDateStorage(businessTime.getTime())));
			return "rate";
		}
    	dbComment.save(new Comment(form.getComment(), Integer.valueOf(form.getRating()), dbDate.save(new CDateStorage(businessTime.getTime())), userAccount.get().getFirstname(), userAccount.get().getLastname(), userAccount.get().getEmail(), movie.getId()));
    	cprogram.inizalize();
    	return "redirect:/rate.html";	
    }
    
    /**
     * Returns a list of all watched movies of the given user
     * @param ldt CDateStorage current time
     * @param email String the current user's email for identification
     * @return watchedmovies
     */
    
    private HashSet<CMovie> getWatchedMovies(String email, CDateStorage ldt){
    	
    	HashSet<Long> watchedmovies = new HashSet<Long>();
    	HashSet<Long> ratedmovies = new HashSet<Long>();
		HashSet<CMovie> unrated = new HashSet<CMovie>();
		ArrayList<CCinemaTicket> tickets = new ArrayList<CCinemaTicket>();
		
		//setting up list of all watched movie IDs
    	for(CCart cart : cartrepo.findByCartType(CartType.PURCHASE)){
			if (cart.getUaccount().matches(email)){
				for (CCinemaTicket ticket : cart.getTickets()){
					CDateStorage moviedate = dbEvent.findById(ticket.getEventID()).getO_endDate();
					if (ldt.laterThan(moviedate) && !ticket.getCategories().toString().contains("canceled")){
						tickets.add(ticket);
					}
				}
			}
		}
    	for (CCinemaTicket ticket : tickets){
			watchedmovies.add(dbEvent.findById(ticket.getEventID()).getMovie().getId());
		}
    	
    	//look for all movies with the saved ID's, remove those that have comments containing
    	//the user's email address
    	for (Long l : watchedmovies){
    		for (Comment c : dbComment.findAll()){
    			if (c.getMovieid().equals(l) && c.getEmail().equals(email)){
    				ratedmovies.add(l);
    			}
    		}
    	}
    	watchedmovies.removeAll(ratedmovies);
    	for (Long l : watchedmovies){
    		unrated.add(dbMovie.findById(l));
		}

		return unrated;
    }

}