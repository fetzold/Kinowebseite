package kickstart.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.salespointframework.useraccount.UserAccount;

@Entity
public class Customer {
	
	/*******************************************************************************************
	*
	*							Variablen
	*		@Column sorgt dafür das Vairablen persistent werden
	*
	*******************************************************************************************/
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "custid", nullable = false)
    private Long custId;

    @Column(name = "forename", nullable = false)
    private String forename;
       
    @Column(name = "name", nullable = false)
    private String name;
       
    @Column(name = "email", nullable = false)              
    private String email;
        
    @Column(name = "password", nullable = false)
    private String password;
        
    @Column(name = "phone")
    private String phone;
	
    @OneToOne 
	@Transient
	private UserAccount userAccount;

	/*******************************************************************************************
	*
	*							Konstruktor
	*		Konstruktor überprüfen ob Vairablen fehlen!!!!
	*
	*******************************************************************************************/
    @SuppressWarnings("unused")
    private Customer() {}

    public Customer(UserAccount userAccount, String forname, String name, String email, String phone) {
    	this.userAccount = userAccount;
        this.forename = forename;
        this.name = name;
        this.email = email;
	this.phone = phone;
    }
	
	/*******************************************************************************************
	*
	*							Methoden GETTER
	*
	*******************************************************************************************/
    public Long getCustId() {
        return custId;
    }

    public String getForename() {
        return forename;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

	/*******************************************************************************************
	*
	*							Methoden SETTER
	*
	*******************************************************************************************/
    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }       
}
