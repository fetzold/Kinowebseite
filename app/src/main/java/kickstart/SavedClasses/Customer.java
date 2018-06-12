package kickstart.SavedClasses;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;

/**
 * The Customer class represent the logical data collection of a real customer
 * @author Bea
 */

@Entity     //vorher @entity
//@Table(name="TEST")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)// oder .SINGLE_TABLE ?
public class Customer {

//============================================================Variables=================================================================

	private @Id @GeneratedValue(strategy = GenerationType.TABLE) long id;
	private String lastname;
	private String firstname;
	private String email;
	private String phone;
	
	@OneToOne private UserAccount userAccount;

//-------------------------------------------------------------C&D-------------------------------------------------------------	
	@SuppressWarnings("unused")
	protected Customer() {}

	/**
	 * Every object represent a customer
	 * @param userAccount : his user account
	 * @param phone phone number : phonenumber of the user

	 */
	
	public Customer(UserAccount userAccount, String phone) {
		this.userAccount = userAccount;
//		userAccount.setFirstname(forename);
//		userAccount.setLastname(lastname);
//		userAccount.setEmail(email);
		this.phone = phone;
	}

//============================================================Public Methods=============================================================	
//_____________________________________________________________GETTER__________________________________________________________________________
	public UserAccount getUseraccount   (){return userAccount;} 
	public Long getId                   (){return id;}
	public String getForename			(){return userAccount.getFirstname();}
	public String getLastname			(){return userAccount.getLastname();}
	public String getEmail              (){return userAccount.getEmail();}
	public String getPhone              (){return phone;}

	//_____________________________________________________________SETTER__________________________________________________________________________
	public void setId       (Long id )			{this.id = id;}
	public void setForename	(String firstname)	{userAccount.setFirstname(firstname);}
	public void setLastname	(String lastname)	{userAccount.setLastname(lastname);}
	public void setEmail    (String email)      {userAccount.setEmail(email);}
	public void setPhone    (String phone)      {this.phone = phone;}

}