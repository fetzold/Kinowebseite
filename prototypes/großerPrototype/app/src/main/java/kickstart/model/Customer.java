package kickstart.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.salespointframework.useraccount.UserAccount;

@Entity
public class Customer {

	private @Id @GeneratedValue long id;

	private String address;
	private String email;
	private String phone;
	
	@OneToOne private UserAccount userAccount;

	@SuppressWarnings("unused")
	private Customer() {}

	public Customer(UserAccount userAccount, String address, String email, String phone) {
		this.userAccount = userAccount;
		this.address = address;
		this.email = email;
		this.phone = phone;
	}

	public long getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
