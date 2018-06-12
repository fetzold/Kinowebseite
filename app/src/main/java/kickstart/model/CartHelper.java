package kickstart.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import kickstart.SavedClasses.*;

import org.javamoney.moneta.Money;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.time.BusinessTime;

import kickstart.Repositorys.*;
import kickstart.SavedClasses.CCart.CartType;
import kickstart.model.eventManagement.CProgram;
import kickstart.SavedClasses.CSeat.EStatus;

/**
 * 
 * @author Beatrice
 * @since 02.01.2016
 */

public class CartHelper {

	private CartRepo cartRepo;
	private TicketCatalog ticketCatalog;
	private DBEvent dbEvent;
	private BusinessTime businessTime;
	private DBDate dbDate;
	private DBSeat dbSeat;

	public CartHelper(BusinessTime businessTime, CartRepo cartRepo, TicketCatalog ticketCatalog, DBEvent dbEvent, DBDate dbDate, DBSeat dbSeat){
		this.dbEvent = dbEvent;
		this.ticketCatalog = ticketCatalog;
		this.cartRepo = cartRepo;
		this.businessTime = businessTime;
		this.dbDate = dbDate;
		this.dbSeat = dbSeat;
	}
	
	/**
	 * Generates random transaction ID and checks for duplicates.
	 * @return code String value of format 999-9
	 */
	
	public String generateCode(){
		Random rn = new Random();
		String code = new String((100 + rn.nextInt(900) + "-" + rn.nextInt(10)));
		Set<String> map = new HashSet<String>();
		for (CCart cart :cartRepo.findAll()){
			map.add(cart.getResCode());
		}
		while (map.contains(code)){
			code = new String((100 + rn.nextInt(900) + "-" + rn.nextInt(10)));
		}
		return code;
	}
	
	/**
	 * Checks for all cart items if they are still in the cart or have timed out
	 * @param cart Cart user's current cart
	 * @param localDateTime LocalDateTime current date and time
	 * @return true if at least one timed out, false if not
	 */
	
	public Boolean checkInvalid(Cart cart, LocalDateTime localDateTime){
		for (CartItem items : cart){
			CCinemaTicket ticket = (CCinemaTicket) items.getProduct();
			CEvent event = dbEvent.findById(ticket.getEventID());
			
			//clear all invalid reservations in event room
			for (CSeat seat : event.getO_room().clearTimedout(localDateTime, dbDate.save(new CDateStorage(localDateTime.minusMinutes(Long.valueOf(1)))))){
				dbSeat.save(seat);
			}
			//remove ticket from cart if seat has been freed by previous check
			if (event.getO_room().getSeatStatus(ticket.getI_row(), ticket.getI_seat()).equals(EStatus.AVAILABLE)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks for all cart items if they are still in the cart or have timed out, or if their
	 * corresponding event no longer accepts reservations
	 * @param cart Cart user's current cart
	 * @param localDateTime LocalDateTime current date and time
	 * @param min Integer Minutes left before an event's start time still allowing reservations
	 * @param cinema_program CProgram event list
	 * @return true if at least one timed out, false if not
	 */
	
	public Boolean checkInvalidRes(Cart cart, LocalDateTime localDateTime, Integer min, CProgram cinema_program){
		for (CartItem items : cart){
			CCinemaTicket ticket = (CCinemaTicket) items.getProduct();
			CEvent event = cinema_program.findEventByID(ticket.getEventID());
			//clear all invalid reservations in event room
			for (CSeat seat : event.getO_room().clearTimedout(localDateTime, dbDate.save(new CDateStorage(localDateTime.minusMinutes(Long.valueOf(1)))))){
				dbSeat.save(seat);
			}
					
			//remove ticket from cart if seat has been freed by previous check
			if (event.getO_room().getSeatStatus(ticket.getI_row(), ticket.getI_seat()).equals(EStatus.AVAILABLE) || !event.canReserve(localDateTime, min)){
				return true;
			}
		}
		return false;
	}
	

	/**
	 * Formats money values into proper German format.
	 * @param money Money monetary amount to be formatted
	 * @return output String of number without currency symbol
	 */
	
	public String formatMoney(Money money){
		String pattern = "#0.00";
		BigDecimal bd = money.getNumberStripped();
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(bd);
		return output;
	}
}
