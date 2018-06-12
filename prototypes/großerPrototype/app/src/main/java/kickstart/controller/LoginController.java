package kickstart.controller;

import kickstart.model.Menu.Menu;
import org.hsqldb.rights.UserManager;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import javax.validation.Valid;

import kickstart.model.Customer;
import kickstart.model.CustomerRepository;
import kickstart.model.validation.RegistrationForm;



/**
 * Created by codemunin on 02.11.15.
 * edited by marco
 */


@Controller
public class LoginController {

	
	private final UserAccountManager userAccountManager;
	private final CustomerRepository customerRepository;

	@Autowired
	public LoginController(UserAccountManager userAccountManager, CustomerRepository customerRepository) {

		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");
		Assert.notNull(customerRepository, "CustomerRepository must not be null!");

		this.userAccountManager = userAccountManager;
		this.customerRepository = customerRepository;
	}

	
    @RequestMapping("/login")
    public String login(ModelMap modelMap) {

        return "login";
    }
    

	@RequestMapping("/registerNew")
	public String registerNew(@ModelAttribute("registrationForm") @Valid RegistrationForm registrationForm,
			BindingResult result) {

		if (result.hasErrors()) {
			return "register";
		}

		UserAccount userAccount = userAccountManager.create(registrationForm.getName(), registrationForm.getPassword(), Role.of("ROLE_CUSTOMER"));
		userAccountManager.save(userAccount);

		Customer customer = new Customer(userAccount, registrationForm.getAddress(), registrationForm.getEmail(),registrationForm.getPhone());
		customerRepository.save(customer);

		return "redirect:/";
	}

	@RequestMapping("/register")
	public String register(ModelMap modelMap) {
		modelMap.addAttribute("registrationForm", new RegistrationForm());
		return "register";
	}
    
}
