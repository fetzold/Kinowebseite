package kickstart;

import java.util.Arrays;

import kickstart.model.eventManagement.CEvent;
import kickstart.model.eventManagement.ICatalog;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import kickstart.model.Customer;
import kickstart.model.CustomerRepository;

@Component
public class ApplicationDataInitializer implements DataInitializer {
	
	private final UserAccountManager userAccountManager;
	private final CustomerRepository customerRepository;

	@Autowired
	public ApplicationDataInitializer(CustomerRepository customerRepository, UserAccountManager userAccountManager) {
		Assert.notNull(customerRepository, "CustomerRepository must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");
		this.customerRepository = customerRepository;
		this.userAccountManager = userAccountManager;
		
	}
		
	@Override
	public void initialize() {

		initializeUsers(userAccountManager, customerRepository);
	}

	@Autowired
	public void CatalogIni(ICatalog catalog){
		CEvent event = new CEvent();
		event.setName("My little Pony");
		catalog.save(event);
		event = new CEvent();
		event.setName("My Little Pony 2");
		catalog.save(event);

	}

	@Autowired
	private void initializeUsers(UserAccountManager userAccountManager, CustomerRepository customerRepository) {

		if (userAccountManager.findByUsername("boss").isPresent()) {
			return;
		}
		UserAccount bossAccount = userAccountManager.create("boss", "123", Role.of("ROLE_BOSS"));
		userAccountManager.save(bossAccount);
		Role customerRole = Role.of("ROLE_CUSTOMER");
		Role employeeRole = Role.of("ROLE_EMPLOYEE");
		Role authorizedRole = Role.of("ROLE_AUTHORIZED");

		UserAccount ua1 = userAccountManager.create("customer", "123", customerRole);
		userAccountManager.save(ua1);
		UserAccount ua2 = userAccountManager.create("employee", "123", employeeRole);
		userAccountManager.save(ua2);
		UserAccount ua3 = userAccountManager.create("authorized", "123", authorizedRole);
		userAccountManager.save(ua3);

		Customer c1 = new Customer(ua1, "Placeholder C1", "c1@home.com", "0123456789");
		Customer c2 = new Customer(ua2, "Placeholder C2", "c2@home.com", "0852963741");
		Customer c3 = new Customer(ua3, "Placeholder C3", "c3@home.com", "9876543210");

		customerRepository.save(Arrays.asList(c1, c2, c3));
		System.out.println("Created!");
	}
	
}
