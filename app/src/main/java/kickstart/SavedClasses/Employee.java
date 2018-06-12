
/**
 * The Employee. Stores the data of the regular Employee
 * @author Alexej S.
 * @since 11/19/2015 changed 11/30/2015
 */ 

package kickstart.SavedClasses;

import kickstart.SavedClasses.Customer;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.text.DecimalFormat;

@Entity
public class Employee extends Customer{

//==========================================Variables=========================================================
	
	protected String street;
	protected String town;
	protected String zip;
	protected String salary;
	
	
//Extend!

//-----------------------------------------------------C&D---------------------------------------
	@SuppressWarnings("unused")
	private Employee() {}
	

	/**
	 * The main constructor for an employee
	 * @param userAccount the account of the user
	 * @param phone			phone number of the employee
	 * @param town			city in wicht the employee lives
	 * @param zip			zip code of the city
	 * @param street		street number of the city
	 * @param salary		the salary of the employee
	 */
	
public Employee(UserAccount userAccount, String phone, String town, String zip, String street, String salary){
	super(userAccount, phone);
	this.town = town;
	this.zip = zip;
	this.street = street;
	this.salary = salary;
	
}
//END C&D

//=====================================Public Methods================================================		
//___________________________________________GETTER_______________________________________________
	public String getStreet			(){return street;}
	public String getTown			(){return town;}
	public String getZip     		(){return zip;}
	public String getSalary			(){return salary;}
//___________________________________SETTER__________________________________________________
	public void setStreet	(String street)		{this.street = street;}
	public void setTown		(String town)		{this.town = town;}
	public void setZip		(String zip)       	{this.zip = zip;}
	public void setSalary	(String salary)		{this.salary = salary;}
	
	
	/**
	 * Convert the salary to a string
	 * @return the salary string
	 */
	public String moneyToString(){
		
		String pattern = "#0.00";
		BigDecimal bd = new BigDecimal(this.getSalary().toString());
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(bd);
		return output;
	}
	
}
// END CEmployee