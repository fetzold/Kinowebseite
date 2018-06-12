package kickstart.validation;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Adds a new employee and validades the input
 * Created by codemunin on 03.12.15.
 */
public class addEmployee {


	
	@NotBlank(message = "{Form.NotEmpty}")
	private String lastname;
	
	@NotBlank(message = "{Form.NotEmpty}")
	private String forename;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "[A-Za-z_0-9.!#$%&'*+/=?^_`{|}~-]+\\@[A-Za-z_0-9-]+(\\.[a-zA-Z0-9-]+)+", message = "{Form.NotEmail}")
	private String email;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d+", message = "{Form.NotNumber}")
	private String phone;
	
	@NotBlank(message = "{Form.NotEmpty}")
	private String street;
	
	@NotBlank(message = "{Form.NotEmpty}")
	private String town;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d{5}", message = "{addEmployee.zip}")
	private String zip;
	
	@NotBlank(message = "{Form.NotEmpty}")
	@Pattern(regexp = "\\d+\\,\\d{2}", message = "{Form.NotMoney}")			//please double-check your required number format
	private String salary;
	
//	@NotBlank(message = "{Form.NotEmpty}")			//can't check anything for Long
	private Long userRightsId;
	
	@NotEmpty(message = "{Form.NotEmpty}")
	private String userName;
	
	@NotEmpty(message = "{Form.NotEmpty}")
	private String password;

	private Boolean delete;

	public String getLastname() {
		return lastname;
	}
    public String getForename() {
		return forename;
	}  
    public String getTown() {
		return town;
	}
    public String getStreet() {
		return street;
	}
    public String getPhone() {
		return phone;
	}
    public String getZip() {
		return zip;
	}
    public String getSalary() {
		return salary;
	}
    public Long getUserRightsId() {
		return userRightsId;
	}
    public String getEmail() {
		return email;
	}
    public String getUserName() {
		return userName;
	} 
    public String getPassword() {
		return password;
	}
	public Boolean getDelete() {return delete;}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public void setForename(String forename) {
		this.forename = forename;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public void setSalary(String salary) {
		this.salary = salary;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public void setUserRightsId(Long userRightsId) {
		this.userRightsId = userRightsId;
	}
	public void setUserName(String userName){
		this.userName = userName;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public void setDelete(Boolean delete) {
		this.delete = delete;
	}

}