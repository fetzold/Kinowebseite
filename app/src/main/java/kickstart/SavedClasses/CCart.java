package kickstart.SavedClasses;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Class saving the details of a completed transaction
 * @author Beatrice
 * @since 12.11.2015
 * 
 */

@Entity
public class CCart {
	
    private @Id @GeneratedValue(strategy = GenerationType.AUTO) Long Id;
	private String username;
	private String resCode;
	private CartType cartType;
	private String time;
	private String uaccount;
	
	public static enum CartType {
		RESERVATION, PURCHASE, CANCELED;
	}
	
	@OneToMany(cascade = CascadeType.ALL) private List<CCinemaTicket> tickets = new LinkedList<CCinemaTicket>();

	@SuppressWarnings("unused")
	private CCart(){}


	
	/**
	 * @param username contains the users first and last name as one String, alternatively the entered Email address
	 * @param cartType value of CartType enum
	 * @param resCode transaction ID
	 * @param uaccount String the buying or reserving person's email address for easy comparison
	 * @param time completed date of the purchase or reservation
	 */
	
	public CCart(String username, CartType cartType, String resCode, String time, String uaccount){
		this.username = username;
		this.cartType = cartType;
		this.resCode = resCode;
		this.time = time;
		this.uaccount = uaccount;
	}
	
	public void addTicket(CCinemaTicket ticket) {
		tickets.add(ticket);
	}

	public Iterable<CCinemaTicket> getTickets() {
		return tickets;
	}
	
	public Long getId(){
		return Id;
	}
	
	public String getUser(){
		return username;
	}
	
	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}
	
	public CartType getCartType() {
		return cartType;
	}

	public void setCartType(CartType cartType) {
		this.cartType = cartType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public String getUaccount() {
		return uaccount;
	}

	public void setUaccount(String uaccount) {
		this.uaccount = uaccount;
	}
}