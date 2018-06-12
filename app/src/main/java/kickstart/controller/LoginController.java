package kickstart.controller;


import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import kickstart.SavedClasses.Customer;
import kickstart.Repositorys.CustomerRepository;
import kickstart.validation.RegistrationForm;



/**
 * Responsible for login and Registration
 * @author codemunin, edited by marco
 * @since 02.11.15
 * 
 */


@Controller
public class LoginController {

	
	private final UserAccountManager userAccountManager;
	private final CustomerRepository customerRepository;

	/**
	 * Creates login controller managing user account creation and login
	 * @param userAccountManager UserAccountManager Provided by SalesPoint
	 * @param customerRepository CustomerRepository Customer database
	 */
	
	@Autowired
	public LoginController(UserAccountManager userAccountManager, CustomerRepository customerRepository) {

		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");
		Assert.notNull(customerRepository, "CustomerRepository must not be null!");

		this.userAccountManager = userAccountManager;
		this.customerRepository = customerRepository;
	}

    /**
     * Registrates a new user , goto register page
     * @param registrationForm RegistrationForm : contains all the needed data 
     * @param result BindingResult				: see org.springframework.validation.BindingResult	 
     * @return Returns the String "redirect:/" if input has errors register.html
     */
	@RequestMapping("/registerNew")
	public String registerNew(@ModelAttribute("registrationForm") @Valid RegistrationForm registrationForm,
			BindingResult result) {

		if (result.hasErrors()) {
			return "register";
		}

		UserAccount userAccount = userAccountManager.create(registrationForm.getEmail(), registrationForm.getPassword(), Role.of("ROLE_CUSTOMER"));
		userAccount.setFirstname(registrationForm.getForename());
		userAccount.setLastname(registrationForm.getName());
		userAccount.setEmail(registrationForm.getEmail());
		userAccountManager.save(userAccount);

		//Bnutzer muss hier noch in die Datenbank übertragen werden.
		Customer customer = new Customer(userAccount, registrationForm.getPhone());
		customerRepository.save(customer);
		

		return "redirect:registerSuccess";
	}
	/**
	 * Leads to the register page
	 * @param modelMap ModelMap		Contains the data
	 * @return register.html
	 */
	@RequestMapping("/register")
	public String register(ModelMap modelMap) {
		modelMap.addAttribute("registrationForm", new RegistrationForm());
		return "register";
	}
    
	/**
	 * Displayed upon successful registration
	 * @return register.html
	 */
	@RequestMapping("/registerSuccess")
	public String registersuccess() {
		return "registerSuccess";
	}
	
}
