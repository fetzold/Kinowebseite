package kickstart.SavedClasses;


import kickstart.SavedClasses.CSeat.Etype;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Objects of products to actually be sold, created at time of selection
 * @author Beatrice
 * @since 25.11.2015
 */
@Entity
public class CCinemaTicket extends Product {
	/**
	 * declaring default SerialVersionUID, to be changed
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer i_row;
	private Integer i_seat;
	private String s_room;
	private String time;
	private Long eventID;
	private Etype seattype;
	private String moviename;
	private PriceClass priceClass;
	
	public static enum PriceClass {
		FULL, REDUCED;
	}

	@SuppressWarnings("deprecation")
	public CCinemaTicket(){};
	
	/**
	 * Creates a new CCinemaTicket
	 * @param name String : name of move for purchases, reservation ID for reservations, 000 for canceled or timed out reservations, 
	 * @param price Money : Money value of the final price
	 * @param i_row Integer:  seat's row number
	 * @param i_seat Integer:  seat's seat number
	 * @param eventID Long 	 :  corresponding event ID
	 * @param roomName String name of the room the corresponding event is in
	 * @param startTime String start time of the event saved as string
	 * @param seatType Etype type of the ticket's seat, loge or pit
	 * @param moviename String name of the movie
	 */
	
	public CCinemaTicket(String name, Money price, Integer i_row, Integer i_seat, Long eventID,String roomName, String startTime, Etype seatType, String moviename){
		
		super(name, price);

		this.eventID = eventID;
		this.i_row = i_row;
		this.i_seat = i_seat;
		this.s_room = roomName;
		this.time = startTime;
		this.seattype = seatType;
		this.moviename = moviename;
		this.priceClass = PriceClass.FULL;
	}
	
	public String getStringPrice(){
		return getPrice().abs().toString();
	}
	public Integer getI_seat() {
		return i_seat;
	}
	public void setI_seat(Integer i_seat) {
		this.i_seat = i_seat;
	}
	public Integer getI_row() {
		return i_row;
	}
	public void setI_row(Integer i_row) {
		this.i_row = i_row;
	}
	public String getS_room() {
		return s_room;
	}
	public void setS_room(String s_room) {
		this.s_room = s_room;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	public Long getEventID() {
		return eventID;
	}

	public void setEventID(Long eventID) {
		this.eventID = eventID;
	}

	public Etype getSeattype() {
		return seattype;
	}

	public void setSeattype(Etype seattype) {
		this.seattype = seattype;
	}

	public String getMoviename() {
		return moviename;
	}

	public void setMoviename(String moviename) {
		this.moviename = moviename;
	}

	public PriceClass getPriceClass() {
		return priceClass;
	}

	public void setPriceClass(PriceClass priceClass) {
		this.priceClass = priceClass;
	}
	
	/**
	 * Returns proper money format with trailing zeroes for displaying on website
	 * @return the money string
	 */
	
	public String moneyToString(){
		String pattern = "#0.00";
		BigDecimal bd = this.getPrice().getNumberStripped();
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(bd);
		return output;
	}
}