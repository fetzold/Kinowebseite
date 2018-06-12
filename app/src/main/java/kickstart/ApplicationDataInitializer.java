package kickstart;

import kickstart.Repositorys.*;
import kickstart.SavedClasses.*;
import kickstart.model.eventManagement.*;
import kickstart.model.programOverView.Generator;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Is responsible for the initialisation of the basic/requed data
 */


@Component
public class ApplicationDataInitializer implements DataInitializer {

	private @Autowired UserAccountManager userAccountManager;
	private @Autowired CustomerRepository customerRepository;
	private @Autowired BusinessTime businessTime;
	private @Autowired DBRoom dbRoom;
	private @Autowired DBMovie dbMovie;
	private @Autowired DBDate dbDate;
	private @Autowired DBEvent dbEvent;
	private @Autowired DBRow dbRow;
	private @Autowired DBCRoom dbcRoom;
	private @Autowired DBSeat dbSeat;
	private @Autowired CProgram cProgram;
	private @Autowired DBEmployee dbEmployee;
	private @Autowired DBComment dbComment;
	/**
	 * Basic Programm Initialisation. Starts all Init methods of the other components
	 */
	@Override
	public void initialize() {
		initializeUsers(userAccountManager, customerRepository);
		ProgramIni(businessTime);
	}



/**
 * Init time and some Basic standart objects, for example Room, Event, Movie and saves them in the database
 * @param businessTime BusinessTime : Provided by Salespoint, Salespoint businesstime needed for the first movie program  
 */
	public void ProgramIni(BusinessTime businessTime){
		cProgram.setDbDate(dbDate);
		cProgram.setDbEvent(dbEvent);
		cProgram.setDbcRoom(dbcRoom);
		cProgram.setDBMovie(dbMovie);
        cProgram.inizalize();

		Iterator<CEvent> it = dbEvent.findAll().iterator();

		Boolean tmp = true;

		while (it.hasNext()){
			CEvent event = it.next();
			if (event.getMovie().getName().equals("Godfather")) {
				tmp = false;
			}
		}

		if (tmp) {
			CDateStorage cds = new CDateStorage(businessTime.getTime());
			cds.addMinutes(Integer.valueOf(40));
			CMovie m1 = new CMovie("Godfather", "Hier ist kein Boot.", "god.jpg", Integer.valueOf(120), "5.00", "10.00", "16");
			m1 = dbMovie.save(m1);
			dbComment.save(new Comment("Test", 5, dbDate.save(new CDateStorage(LocalDateTime.now())), "Sample", "Person", "mail@test.com", m1.getId()));

			SaveRoom room = new SaveRoom("Kino 1", "-pppppp-dppppd-llllll-ddddd-llllll", "3.00", "5.00");
			room = dbRoom.save(room);
			cProgram.createEvent(m1, room.getRoom(), room.getName(), room.getId(), cds, false, dbRow, dbSeat);

			CDateStorage cds2 = new CDateStorage(businessTime.getTime());
			cds2.addMinutes(60 * 8);
		}
	}


	/**
	 * Initializes default users at start of software
	 * @param userAccountManager UserAccountManager : Provided by Salespoint, user management
	 * @param customerRepository CustomerRepository : Provided by Salespoint, database
	 */

	@Autowired
	private void initializeUsers(UserAccountManager userAccountManager, CustomerRepository customerRepository) {

		if (userAccountManager.findByUsername("boss").isPresent()) {
			return;
		}
		UserAccount bossAccount = userAccountManager.create("boss", "123", Role.of("ROLE_BOSS"));
		bossAccount.setFirstname("Der");
		bossAccount.setLastname("Chef");
		bossAccount.setEmail("admin@ufo.com");
		userAccountManager.save(bossAccount);
		Role customerRole = Role.of("ROLE_CUSTOMER");
		Role employeeRole = Role.of("ROLE_EMPLOYEE");
		Role authorizedRole = Role.of("ROLE_AUTHORIZED");

		UserAccount ua1 = userAccountManager.create("customer", "123", customerRole);
		ua1.setFirstname("Hans");
		ua1.setLastname("Der einfache Kunde");
		ua1.setEmail("lowbob@ufo.com");
		userAccountManager.save(ua1);

		UserAccount ua2 = userAccountManager.create("employee", "123", employeeRole);
		ua2.setFirstname("Max");
		ua2.setLastname("Der fortgeschrittene Kassierer");
		ua2.setEmail("Max@ufo.com");
		userAccountManager.save(ua2);

		UserAccount ua3 = userAccountManager.create("authorized", "123", authorizedRole);
		ua3.setFirstname("Mike");
		ua3.setLastname("Der professionelle Ausbeuter");
		ua3.setEmail("Mike@ufo.com");
		userAccountManager.save(ua3);

		UserAccount ua4 = userAccountManager.create("Hans", "123", authorizedRole);
		ua4.setFirstname("Hans");
		ua4.setLastname("Meier");
		ua4.setEmail("Hans@ufo.com");
		userAccountManager.save(ua4);


		Customer c1 = new Customer(ua1, "0123456789");
		
		Employee c2 = new Employee(ua2, "0852963741", "Dresden","01096","Theaterplatz","12.50");
		Employee c3 = new Employee(ua3, "9876543210", "Dresden","01096","Theaterplatz","8.50");
		Employee e1 = new Employee(ua4, "012324554", "Dresden","01096","Theaterplatz","10.00");
		customerRepository.save(Arrays.asList(c1, c2, c3, e1));
		dbEmployee.save(c2);
		dbEmployee.save(c3);
		dbEmployee.save(e1);


	}
}


