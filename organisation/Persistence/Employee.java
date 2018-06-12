/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kickstart.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import org.salespointframework.useraccount.UserAccount;

/**
 *
 * @author felix
 */
@Entity
public class Employee { 
	
	/*******************************************************************************************
	*
	*							Variablen
	*		@Column sorgt dafür das Vairablen persistent werden
	*		Keine Vererbung zu Customer, da Sublcass sonst erstellt werden muss
	*
	*******************************************************************************************/
    @Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "empid", nullable = false)
    private Long empId;

    @Column(name = "forename", nullable = false)
    private String forename;
       
    @Column(name = "name", nullable = false)
    private String name;
       
    @Column(name = "email", nullable = false)              
    private String email;
        
    @Column(name = "password", nullable = false)
    private String password;
        
    @Column(name = "phone", nullable = false)
    private String phone;
    
    @Column(name = "street", nullable = false)
    private String street;
    
    @Column(name = "streetnumber", nullable = false)
    private String streetnumber;
    
    @Column(name = "zipcode", nullable = false)
    private String zipcode;
            
    @Column(name = "city", nullable = false)
    private String city;
    
    @Column(name = "hiredate", nullable = false)
    private String hiredate;
    
    @Column(name = "firedate", nullable = false)
    private String firedate;
    
    @Column(name = "salary", nullable = false)
    private Double salary;
    
    @Column(name = "status", nullable = false)
    private String status;
	
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
    private Employee() {}

    public Employee(UserAccount userAccount, String forname, String name, String email, String phone) {
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
    public Long getEmpId() {
        return empId;
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

    public String getStreet() {
        return street;
    }

    public String getStreetnumber() {
        return streetnumber;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getCity() {
        return city;
    }

    public String getHiredate() {
        return hiredate;
    }

    public String getFiredate() {
        return firedate;
    }

    public Double getSalary() {
        return salary;
    }

    public String getStatus() {
        return status;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

	/*******************************************************************************************
	*
	*							Methoden SETTER
	*
	*******************************************************************************************/
    public void setEmpId(Long empId) {
        this.empId = empId;
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

    public void setStreet(String street) {
        this.street = street;
    }

    public void setStreetnumber(String streetnumber) {
        this.streetnumber = streetnumber;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setHiredate(String hiredate) {
        this.hiredate = hiredate;
    }

    public void setFiredate(String firedate) {
        this.firedate = firedate;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    } 
}
