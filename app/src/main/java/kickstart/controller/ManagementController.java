package kickstart.controller;

import kickstart.Repositorys.*;
import kickstart.SavedClasses.CDateStorage;
import kickstart.SavedClasses.CMovie;
import kickstart.SavedClasses.Customer;
import kickstart.Repositorys.CustomerRepository;
import kickstart.SavedClasses.CEvent;
import kickstart.model.eventManagement.CProgram;
import kickstart.SavedClasses.Employee;
import kickstart.validation.addEvent;
import kickstart.model.programOverView.Generator;
import kickstart.SavedClasses.CRoom;
import kickstart.SavedClasses.CSeat;
import kickstart.SavedClasses.SaveRoom;
import kickstart.validation.addroom;
import kickstart.validation.ChangeRoom;
import kickstart.validation.RegistrationForm;
import kickstart.validation.addEmployee;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Controls the user access rights
 * @author codemunin
 * @since 02.11.15
 * edit last time by Alexej 12/08/2015
 */


@Controller
@SessionAttributes("management")
public class ManagementController {

    private CProgram cinema_program;
    private UserAccountManager userAccountManager;
    private BusinessTime businessTime;
    private CustomerRepository customerRepository;
    private DBEmployee dbEmployee;
    private ChangeRoom formRoom;
    private Collection<CSeat> seats;
    private CRoom cRoom;
    private Generator generator;
    private DBRoom dbRoom;
    private DBMovie dbMovie;
    private DBEvent dbEvent;
    private DBSeat dbSeat;
    private DBRow dbRow;
    /**
 	* Creates a new Management Controller responsible for the user level administration of each user
 	* @param businessTime 		BusinessTime 		: SalesPoint time
 	* @param userAccountManager UserAccountManager 	: Provided by SalesPoint
 	* @param customerRepository	CustomerRepository	: Customer database
 	* @param dbEmployee			DBEmployee			: The employee database
 	* @param  cProgram CProgram the program
 	* @param  dbcRoom DBCRoom database of actual rooms in use
 	* @param  dbSeat DBSeat seat database
 	* @param  dbRow DBRow row database
 	* @param  dbDate DBDate CDateStorage database
 	* @param  dbMovie DBMovie movie database
 	* @param  dbEvent DBEvent event database
 	* @param  dbRoom DBRoom SaveRoom database
 	*/
    @Autowired
    public ManagementController(CProgram cProgram, DBCRoom dbcRoom, DBSeat dbSeat, 
    		DBRow dbRow, DBDate dbDate, DBMovie dbMovie, BusinessTime businessTime, 
    		UserAccountManager userAccountManager, CustomerRepository customerRepository, 
    		DBEmployee dbEmployee, DBEvent dbEvent, DBRoom dbRoom){
    	
        cinema_program = cProgram;
        this.userAccountManager = userAccountManager;
        this.businessTime = businessTime;
        this.customerRepository = customerRepository;
        this.dbEmployee = dbEmployee;
        this.dbMovie = dbMovie;
        this.dbEvent = dbEvent;
        this.dbRoom = dbRoom;
        this.dbSeat = dbSeat;
        this.dbRow = dbRow;
    }
    
	/**
	 * Checks the account and gives back the access rights
	 * @param userAccount		Optional  &#60;UserAccount&#62;	: User account database
	 * @return Depends on the access rights. If fails get back to login page
	 */
    @RequestMapping("/options.html")
    @PreAuthorize(value = "isAuthenticated()")
    public String management(@LoggedIn Optional<UserAccount> userAccount) {
    	if (userAccount.get().hasRole(Role.of("ROLE_BOSS"))) {
    		return "redirect:staff";
        }
        return "redirect:account.html";
    }

    
    /**
     * Leads you to the account settings site
     * @param model Model: Provided by Spring
     * @param userAccount	Optional  &#60;UserAccount&#62;	: the account of the user
     * @param customerForm	RegistrationForm contains the customer data
     * @param employeeForm	addEmployee contains the employee data
     * @return account.html
     */
    @RequestMapping("account.html")
    @PreAuthorize(value = "isAuthenticated()")
    public String account(Model model, @LoggedIn Optional<UserAccount> userAccount,RegistrationForm customerForm, addEmployee employeeForm){
    	UserAccount account = userAccount.get();
    	if (account.hasRole(Role.of("ROLE_CUSTOMER"))){
        	customerForm.setEmail(account.getEmail());
        	customerForm.setPassword(account.getPassword().toString());
        	customerForm.setForename(account.getFirstname());
        	customerForm.setName(account.getLastname());
        	customerForm.setPassword("***");
            for (Customer customer : customerRepository.findAll()){
            	if (account.equals(customer.getUseraccount())){
                	customerForm.setPhone(customer.getPhone());
                }
            }
            model.addAttribute(customerForm);
    	}
        if ((account.hasRole(Role.of("ROLE_BOSS")))||account.hasRole(Role.of("ROLE_EMPLOYEE"))||account.hasRole(Role.of("ROLE_AUTHORIZED"))){
        	for (Employee employee : dbEmployee.findAll()){
            	if (employee.getUseraccount().equals(account)){
                	employeeForm.setPhone(employee.getPhone());
                    employeeForm.setEmail(employee.getEmail());
                    employeeForm.setLastname(employee.getLastname());
                    employeeForm.setForename(employee.getForename());
                    employeeForm.setStreet(employee.getStreet());
                    employeeForm.setTown(employee.getTown());
                    employeeForm.setZip(employee.getZip());
                    employeeForm.setSalary(employee.getSalary().replace(".", ","));
                    employeeForm.setPassword("***");
                    employeeForm.setUserName(employee.getUseraccount().getUsername().toString());
                    employeeForm.setUserRightsId(Long.valueOf(0));
                    if (account.hasRole(Role.of("ROLE_BOSS"))){
                    	employeeForm.setUserRightsId(Long.valueOf(2));
                    }
                    if (account.hasRole(Role.of("ROLE_AUTHORIZED"))){
                    	employeeForm.setUserRightsId(Long.valueOf(1));
                    }
                }
            }
            model.addAttribute("form2", employeeForm);
        }
        return "account";
    }

    /**
     * Called when a customer modifies their account data
     * @param optional Optional  &#60;UserAccount&#62;	: the account of the user
     * @param customerForm RegistrationForm contains the customer data
     * @param redirectAttributes Provided by Spring
     * @param result BindingResult returning validated form errors
     * @return account.html on success
     */
    @RequestMapping(value = "/custChange", method = RequestMethod.POST)
    @PreAuthorize(value = "isAuthenticated()")
    public String custChange(@LoggedIn Optional<UserAccount> optional, @ModelAttribute("form") @Valid RegistrationForm customerForm, BindingResult result, RedirectAttributes redirectAttributes){

        if (customerForm.getDelete()) {
                UserAccount userAccount2 = optional.get();
                Password inputPw = new Password(customerForm.getPassword());


                if (!(customerForm.getForename().equals("LOESCHEN"))) {
                    redirectAttributes.addFlashAttribute("succsesaMsg", new String("Zum löschen geben Sie bitte den Text zwischen den Klammern als Bestätigung bei Vorname ein <LOESCHEN>"));
                    return "redirect:account.html";
                }else {
                    for (Customer customer : customerRepository.findAll()){
                        if (customer.getUseraccount().equals(userAccount2)){
                            customerRepository.delete(customer);
                            userAccountManager.disable(userAccount2.getIdentifier());
                            redirectAttributes.addFlashAttribute("succsesaMsg", new String("Ihr Konto wurde erfolgreich aufgelöst!"));
                            return "redirect: /";
                        }
                    }

                }
        }

        if (result.hasErrors()) {
    		redirectAttributes.addFlashAttribute("succsesaMsg", new String("Bitte vervollständigen Sie ihre Angaben."));
			return "redirect:account.html";
		}
            UserAccount userAccount = optional.get();
            userAccount.setFirstname(customerForm.getForename());
            userAccount.setLastname(customerForm.getName());
            for (Customer customer : customerRepository.findAll()){
                if (userAccount.equals(customer.getUseraccount())){
                    customer.setPhone(customerForm.getPhone());
                }
            }
            if (!customerForm.getPassword().equals("***")){
                userAccountManager.changePassword(userAccount, customerForm.getPassword());
            }
            userAccountManager.save(userAccount);
            redirectAttributes.addFlashAttribute("succsesaMsg", new String("Änderungen waren erfolgreich."));
            return "redirect:account.html";
    }

    /**
     * Method providing the form to change a user's personal data
     * @param optional Optional  &#60;UserAccount&#62;	: the account of the user
     * @param form addEmployee contains the employee data
     * @param result BindingResult returning validated form errors
     * @param redirectAttributes Provided by Spring
     * @return account.html
     */

    @RequestMapping(value = "/empChange", method = RequestMethod.POST)
    @PreAuthorize(value = "isAuthenticated()")
    public String empChange(@LoggedIn Optional<UserAccount> optional, @ModelAttribute("form2") @Valid addEmployee form, BindingResult result, RedirectAttributes redirectAttributes){
    	if (result.hasErrors()) {
    		redirectAttributes.addFlashAttribute("succsesaMsg", new String("Bitte vervollständigen Sie ihre Angaben."));
			return "redirect:account.html";
		}
            UserAccount userAccount = optional.get();
            userAccount.setEmail(form.getEmail());
            userAccount.setFirstname(form.getForename());
            userAccount.setLastname(form.getLastname());
            for (Employee employee : dbEmployee.findAll()){
                if (userAccount.equals(employee.getUseraccount())){
                    employee.setPhone(form.getPhone());
                    employee.setZip(form.getZip());
                    employee.setTown(form.getTown());
                    employee.setForename(form.getForename());
                    employee.setSalary(form.getSalary().replace(",", "."));
                }
            }
            if (!form.getPassword().equals("***")){
                userAccountManager.changePassword(userAccount, form.getPassword());
            }
            userAccountManager.save(userAccount);
            redirectAttributes.addFlashAttribute("succsesaMsg", new String("Änderungen waren erfolgreich."));
            return "redirect:account.html";
    }
    /**
     * Leads you to the staff overview page, there you can change the stuff member attributes
     * @param model			Model 							: Provided by SalesPoint	
     * @return if has user level staff.html otherwise login.html
     */
    @RequestMapping(value = "/staff" , method = RequestMethod.GET)
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    public String staff(Model model){
    	model.addAttribute("customers", userAccountManager.findAll());
    	Collection<Employee> e = (Collection<Employee>) dbEmployee.findAll();
    	model.addAttribute("customerdetail", e);		
    	return "staff";
    }

    /**
     * Leads the user if he is the boss to the room creating page
     * @param form 		addroom	: The data container of the room
     * @param modelMap	Model	: Provided by Spring
     * @return if succsessful addRoom.html
     */
    @RequestMapping(value = "/addRoom" , method = RequestMethod.GET)
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    public String rooms(addroom form, Model modelMap) {
        modelMap.addAttribute("form",form);
        return "addRoom";
    }

    /**
     * Adds new rooms to the database. Before adding anything it checks if the user is an Admin  
     * @param model Model : Provided by Spring
     * @param form addroom : the requed data form the form
     * @param result BindingResult : provided by Spring
     * @param redirectAttributes provided by Spring
     * @return if everything was successful the method returns the string "success". <p> 
     *  If not the user get back to the login window. (return "login")
     */
    @RequestMapping(value = "/addRoom" , method = RequestMethod.POST)
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    public String addrooms(@ModelAttribute("addroom") @Valid addroom form,	BindingResult result, Model model, RedirectAttributes redirectAttributes) {

    	if (result.hasErrors()) {
			return "addRoom";
		}
    	for (SaveRoom checkroom : dbRoom.findAllActive()){
    		if (checkroom.getName().equals(form.getName())){
    			model.addAttribute("duplicate", new String("duplicate"));
    			return "addRoom";
    		}
    	}
    	Long rows = new Long(form.getRows());
    	Long seats = new Long(form.getSeats());
        String s_room = "";
        for (int i = 0; i < rows; i++){
            s_room = s_room + "-";
            for (int z = 0; z < seats; z++){
                s_room = s_room + "p";
            }
        }
        String basicCharge = form.getBasicCharge().replace(",", ".");
        String extraCharge = form.getExtraCharge().replace(",", ".");
        dbRoom.save(new SaveRoom(form.getName(), s_room, basicCharge, extraCharge));
        redirectAttributes.addFlashAttribute("succsesaMsg", new String("Raum wurde erfolgreich erstellt."));
        return "redirect:/addRoom";
    }

    /**
     * The method Lead you to the changeRoom.html if your acc has the boss level authorisation
     * @param form 	addEvent : addEvent Class, adds a new event also the container class
     * @param model Model : Provided by SalesPoint
     * @return changeRoom.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/changeRoom" , method = RequestMethod.GET)
    public String changeroom(addEvent form, Model model) {
        model.addAttribute("form", form);
        model.addAttribute("rooms", dbRoom.findAllActive());
        return "changeRoom";
    }
    
    /**
     * Saves the changed room in the database, if everything was correct it leads you to succsess.html
     * @param form2 ChangeRoom	collects original field data
     * @param redirectAttributes RedirectAttributes : Provided by Spring
     * @param model Model provided by Spring
     * @param result  provided by Spring
     * @return succsess.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/changeRoom" , method = RequestMethod.POST)
    public String changeroom(@ModelAttribute("form") @Valid ChangeRoom form2, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
    	if (result.hasErrors()) {
    		model.addAttribute("money", "Format '0,00' für Geldbeträge");
    		model.addAttribute("form", formRoom);
            model.addAttribute("seats", seats);
            model.addAttribute("room", cRoom);
			return "changeRoomDetail";
		}


        SaveRoom room = dbRoom.findById(cRoom.getI_id());
        room.setBasicCharge(form2.getBasicCharge().replace(",", "."));
        room.setExtraCharge(form2.getExtraCharge().replace(",", "."));
        room.setName(form2.getName());
        room.setRoom(cRoom.toString());
        dbRoom.save(room);
        redirectAttributes.addFlashAttribute("succsesaMsg", new String("Raum wurde erfolgreich bearbeitet."));
        return "redirect:/changeRoom";
    }

    /**
     * Responsible for the changing of the room details. as allways you need boss level authorisation
     * @param form 	addEvent	: Adds a new event, the class contains all necercerry data holders	
     * @param model Model		: Provided by Spring
     * @param from2 ChangeRoom	: The data container of the room the class contains all getter and setter
     * @param redirectAttributes RedirectAttributes provided by Spring
     * @return /changeRoom/changeRoomDetail.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/changeRoomDetail" , method = RequestMethod.POST)
    public String changeroomdetail(addEvent form, Model model, ChangeRoom from2, RedirectAttributes redirectAttributes) {
        SaveRoom room = dbRoom.findById(form.getRoomID());

        if (form.getDelete()){
            room.setActive(false);
            dbRoom.save(room);
            redirectAttributes.addFlashAttribute("succsesaMsg", new String("Raum wird zum nächstmöglichen Zeitpunkt aus dem System entfernt."));
            return "redirect:/changeRoom";
        }


        formRoom = from2;
        formRoom.setBasicCharge(room.getBasicCharge().replace(".", ","));
        formRoom.setExtraCharge(room.getExtraCharge().replace(".", ","));
        formRoom.setName(room.getName());
        seats = new ArrayList<>();

        cRoom = new CRoom(room.getName(),room.getId(),cinema_program.createRows(room.getRoom(),dbRow, dbSeat));
        model.addAttribute("form", formRoom);
        model.addAttribute("seats", seats);
        model.addAttribute("room", cRoom);
        return "changeRoomDetail";
    }

    /**
     * Responsible for the changing of the room details(In this case the seats). as always you need boss level authorization
     * @param model Model	: Provided by Spring
     * @param row Integer 	: The row number
     * @param seat Integer 	: The seat number
     * @return /changeRoom/changeRoomDetail.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/changeRoomDetail/{row}/{number}" , method = RequestMethod.GET)
    public String changeroomdetail(Model model, @PathVariable(value = "row") Integer row, @PathVariable(value = "number") Integer seat) {
        CSeat seat1 = cRoom.getSeat(row,seat);
        SaveRoom room = dbRoom.findById(cRoom.getI_id());
        formRoom.setBasicCharge(room.getBasicCharge().replace(".", ","));
        formRoom.setExtraCharge(room.getExtraCharge().replace(".", ","));
        formRoom.setName(room.getName());
        seat1.setE_Status(CSeat.EStatus.RESERVED);
        seats.add(seat1);
        model.addAttribute("form", formRoom);
        model.addAttribute("room", cRoom);
        model.addAttribute("seats", seats);
        return "changeRoomDetail";
    }

    /**
     * Responsible for the changing of the room details. as always you need boss level authorization
     * @param model Provided by Spring
     * @param form ChangeRoom Form managing the inputs
     * @return /changeRoom/changeRoomDetail.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/addSeats" , method = RequestMethod.POST)
    public String changeroomdetail(Model model, ChangeRoom form) {
    	if (!form.getSeats().toString().matches("\\d+") ||new Integer(form.getSeats()) > 20){
    		model.addAttribute("form", formRoom);
            model.addAttribute("room", cRoom);
            model.addAttribute("seats", seats);
    		model.addAttribute("error", new String("Max. 20 Sitze"));
    		return "changeRoomDetail";
    	}
        cRoom.creatRow(new Integer(form.getSeats()), CSeat.Etype.Loge);
        SaveRoom room = dbRoom.findById(cRoom.getI_id());
        formRoom.setBasicCharge(room.getBasicCharge().replace(".", ","));
        formRoom.setExtraCharge(room.getExtraCharge().replace(".", ","));
        formRoom.setName(room.getName());
        model.addAttribute("form", formRoom);
        model.addAttribute("room", cRoom);
        model.addAttribute("seats", seats);
        return "changeRoomDetail";
    }

    /**
     * Changes the Seat Status. It identifies the row and make the seats all the same type 
     * @param form2 ChangeRoom Form managing the inputs
     * @param model Provided by Spring
     * @return  /changeRoom/changeRoomDetail.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/changeSeats" , method = RequestMethod.POST)
    public String changeseats(ChangeRoom form2, Model model) {
        if (form2.getAction().equals(0)) {
            cRoom.deleteSeats(seats);
        }

        if (form2.getAction().equals(1)) {
            for (CSeat seat : seats) {
                seat.setE_Type(CSeat.Etype.Loge);
            }
        }

        if (form2.getAction().equals(2)) {
            for (CSeat seat : seats) {
                seat.setE_Type(CSeat.Etype.Parkett);
            }
        }

        if (form2.getAction().equals(3)) {
            for (CSeat seat : seats) {
                seat.setE_Type(CSeat.Etype.DEFECTIVE);
            }
        }
        for (CSeat seat : seats){
            seat.setE_Status(CSeat.EStatus.AVAILABLE);
        }
        seats.clear();
        SaveRoom room = dbRoom.findById(cRoom.getI_id());
        formRoom.setBasicCharge(room.getBasicCharge().replace(".", ","));
        formRoom.setExtraCharge(room.getExtraCharge().replace(".", ","));
        formRoom.setName(room.getName());

        model.addAttribute("form", formRoom);
        model.addAttribute("room", cRoom);
        model.addAttribute("seats", seats);

        return "changeRoomDetail";
    }
    

/**
 * Adds a new Event need BOSS auth.emp. userlevel
 * @param form addEvent Form managing the inputs
 * @param modelMap Model Provided by Spring
 * @return addEvent.html
 */
    @RequestMapping(value = "/addEvent" , method = RequestMethod.GET)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String event(addEvent form, Model modelMap) {
    	List<CMovie> mv = new ArrayList<CMovie>();
    	for (CMovie movie : dbMovie.findAll()){
    		if (movie.getIsRent()){
    			mv.add(movie);
    		}
    	}
        modelMap.addAttribute("movies", mv);
        modelMap.addAttribute("rooms", dbRoom.findAllActive());
        modelMap.addAttribute("form",form);
        return "addEvent";
    }

/**
 * selects the selected event one of the events need BOSS auth.emp. userlevel
 * @param model Model Provided by Spring
 * @param form addEvent Form managing the inputs
 * @return /changeEvent/chooseEvent.html
 */
    @RequestMapping(value = "/changeEvent")
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String chooseEvent(Model model, addEvent form){
        Collection<CEvent> c = new ArrayList<>();
        for (CEvent event : dbEvent.findAll()){
            if ((event.getO_startDate().laterThan(new CDateStorage(businessTime.getTime())))&&
            (!event.getO_room().hasReservation())){
                c.add(event);
            }
        }
        form.setDelete(false);
        model.addAttribute("events", c);
        model.addAttribute("form", form);
        return "chooseEvent";
    }

    /**
     * With this you can choose a specific room for an event
     * @param model provided by Spring
     * @param form addEvent Form managing the inputs
     * @param redirectAttributes Provided by Spring
     * @return /changeEvent/chooseRoom.html
     */
    @RequestMapping(value = "/chooseRoom", method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String chooseRoom(Model model, addEvent form, RedirectAttributes redirectAttributes){
        form.setMovieID(dbEvent.findById(form.getEventId()).getMovie().getId());
        dbEvent.delete(form.getEventId());
        cinema_program.inizalize();
        if (form.getDelete()){
            redirectAttributes.addFlashAttribute("succsesaMsg", new String("Event wurde erfolgreich entfernt."));
            return "redirect:/addEvent";
        }
        model.addAttribute("rooms", dbRoom.findAllActive());
        model.addAttribute("form", form);
        return "chooseRoom";
    }

    /**
     * Leads you to the addemployee page need BOSS userlevel
     * @param form addEmployee Form managing the inputs
     * @param modelMap provided by Spring
     * @return addEmployee.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/addEmployee" , method = RequestMethod.GET)
    public String employee(addEmployee form, Model modelMap) {
        modelMap.addAttribute("addEmployeeForm", form);
        return "addEmployee";
    }
    
    /**
     * Leads you to the employee detail page if you have the boss user level
     * @param employee String selected employee's ID
     * @param modelMap Model provided by Spring
     * @return detailEmployee.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/detailEmployee" , method = RequestMethod.POST)
    public String detailemployee(@RequestParam("emp") String employee, Model modelMap) {
    	Long empid = new Long(employee);
    	Employee emp = dbEmployee.findOne(empid);
        modelMap.addAttribute("employee", emp);
        return "detailEmployee";
    }
    
    /**
     * Leads you to the employee editing page if you have the boss user level
     * @param form addEmployee Form managing the inputs
     * @param employee String ID of selected employee
     * @param model Model provided by Spring
     * @return editemployee.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/editEmployee" , method = RequestMethod.POST)
    public String editemployee(addEmployee form, @RequestParam("id") String employee, Model model) {

    	Long empid = new Long(employee);
       	Employee emp = dbEmployee.findOne(empid);
    	
            form.setPhone(emp.getPhone());
            form.setEmail(emp.getEmail());
            form.setLastname(emp.getLastname());
            form.setForename(emp.getForename());
            form.setStreet(emp.getStreet());
            form.setTown(emp.getTown());
            form.setZip(emp.getZip());
            form.setSalary(emp.getSalary().replace(".", ","));
            form.setPassword("***");
            form.setUserName(emp.getUseraccount().getUsername().toString());
            form.setUserRightsId(Long.valueOf(0));
            if (emp.getUseraccount().hasRole(Role.of("ROLE_BOSS"))){
                form.setUserRightsId(Long.valueOf(2));
            }
            if (emp.getUseraccount().hasRole(Role.of("ROLE_AUTHORIZED"))){
                form.setUserRightsId(Long.valueOf(1));
            }
            model.addAttribute("form", form);
            model.addAttribute("id", empid);
        
            return "editEmployee";
    }
    
    /**
     * Allows the Boss to modify information on the employees
     * @param form addEmployee Form managing the inputs
     * @param result BindingResult returning form errors
     * @param redirectAttributes RedirectAttributes provided by Spring
     * @param identifier String edited employee's ID
     * @return staff.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/modifyEmployee" , method = RequestMethod.POST)
    public String modifyemployee(@ModelAttribute("form") @Valid addEmployee form, BindingResult result, RedirectAttributes redirectAttributes, @RequestParam("empid") String identifier) {
    	if (result.hasErrors()) {
			return "editEmployee";
		}
    	Long ID = new Long(identifier);
    	
    	Employee employee = dbEmployee.findOne(ID);
                employee.setPhone(form.getPhone());
                employee.setZip(form.getZip());
                employee.setTown(form.getTown());
                employee.setForename(form.getForename());
                employee.setSalary(form.getSalary().replace(",", "."));
                employee.setStreet(form.getStreet());
                Role r = Role.of("ROLE_EMPLOYEE");
                if (form.getUserRightsId().equals(Long.valueOf(1)))
                    r = Role.of("ROLE_AUTHORIZED");
                if (form.getUserRightsId().equals(Long.valueOf(2)))
                    r = Role.of("ROLE_BOSS");
                UserAccount oA = employee.getUseraccount();
                for(Role role : oA.getRoles()){
                	oA.remove(role);
                oA.add(r);
                oA.setFirstname(form.getForename());
                oA.setLastname(form.getLastname());
                oA.setEmail(form.getEmail());
                userAccountManager.save(oA);
                dbEmployee.save(employee);
    	}
        
        redirectAttributes.addFlashAttribute("succsesaMsg", new String("Angestellter wurde erfolgreich bearbeitet."));
        return "redirect:/staff";
    }

    /**
     * Adding a new Employee if the user has the boss user level 
     * @param form 					addEmployee			: Responsible for getter and setter of the employee also saves him inside the database
     * @param result				BindingResult		: Provided by Spring: org.springframework.validation.BindingResult
     * @param redirectAttributes	RedirectAttributes	: Provided by Spring
     * @return	redirect:/staff.html if input fails validation return addEmployee.html
     */
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    @RequestMapping(value = "/addEmployee" , method = RequestMethod.POST)
    public String addemployee(@ModelAttribute("addEmployeeForm") @Valid addEmployee form, BindingResult result, RedirectAttributes redirectAttributes) {
    	if (result.hasErrors()) {
			return "addEmployee";
		}
            Role r = Role.of("ROLE_EMPLOYEE");
            if (form.getUserRightsId().equals(Long.valueOf(1)))
                r = Role.of("ROLE_AUTHORIZED");
            if (form.getUserRightsId().equals(Long.valueOf(2)))
                r = Role.of("ROLE_BOSS");

            //Employee in table
            UserAccount uA = userAccountManager.create(form.getUserName(),form.getPassword(),r);
            uA.setFirstname(form.getForename());
            uA.setLastname(form.getLastname());
            uA.setEmail(form.getEmail());
            userAccountManager.save(uA);
            Employee c1 = new Employee(uA, form.getPhone(), form.getTown(), form.getZip(), form.getStreet(), form.getSalary().replace(",", "."));
            dbEmployee.save(c1);
            redirectAttributes.addFlashAttribute("succsesaMsg", new String("Angestellter wurde erfolgreich erstellt."));
       return "redirect:/staff";
    }

    /**
     * Deletes the given Employee
     * @param employee String ID of the selected Employee
     * @param redirectAttributes  Provided by Spring
     * @return redirect:/staff.html
     */

    @RequestMapping(value = "/deleteEmployee" , method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS')")
    public String deleteemployee(@RequestParam("id") String employee, RedirectAttributes redirectAttributes) {
    	Long empid = Long.valueOf(employee);
    	userAccountManager.disable(dbEmployee.findOne(empid).getUseraccount().getId());
    	dbEmployee.delete(dbEmployee.findOne(empid));
    	redirectAttributes.addFlashAttribute("succsesaMsg", new String("Angestellter wurde gelöscht."));
    	return "redirect:/staff";
    }
    
    
    /**
     * Shows selection of available events
     * @param form addEvent Form managing inputs
     * @param modelMap Model provided by Spring
     * @return /addEvent/addEvent1st
     */
    @RequestMapping(value = "/addEvent1st" , method = RequestMethod.GET)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String addEvent1st(addEvent form, Model modelMap) {

        modelMap.addAttribute("rooms", dbRoom.findAllActive());
        modelMap.addAttribute("movies", dbMovie.findAll());
        modelMap.addAttribute("form",form);
        return "addEvent1st";
    }

    /**
     * Adds a new event need boss or autorized user level, provides interface for creating special events
     * @param form addEvent Form managing inputs
     * @param modelMap Model provided by Spring
     * @return addEvent2nd.html
     */
    @RequestMapping(value = "/addEvent2nd" , method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String addEvent2nd(addEvent form, Model modelMap) {
        if ((form.getMovieID().equals(Long.valueOf("-1"))) && form.getAllday() == null){
            modelMap.addAttribute("form", form);
            return "addEvent3rd";
        }

        CDateStorage cds = new CDateStorage(businessTime.getTime());
        generator = new Generator(cds, form.getRoomID(), dbMovie.findById(form.getMovieID()),cinema_program);
        Boolean isPrivate;
        if ((form.getB_private() == null)||(form.getB_private().equals(false))){
            isPrivate = false;
        }else isPrivate = true;

        modelMap.addAttribute("days", generator.getCollection());
        modelMap.addAttribute("times", generator.getTime());
        modelMap.addAttribute("form",form);
        modelMap.addAttribute("isPrivate", isPrivate);
        return "addEvent2nd";
    }
    
    
    
    /**
     * Method creating a special event with a new movie, added only for this occasion
     * @param form addEvent Form managing inputs
     * @param model Model provided by Spring
     * @param result BindingResult provided by Spring
     * @return addEvent2nd.html
     */
    @RequestMapping(value = "/addEventSpecial" , method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String addEventSpecial(@ModelAttribute("form") @Valid addEvent form, BindingResult result, Model model) {
    	if (result.hasErrors()){
    		model.addAttribute("form",form);
    		return "addEvent3rd";
    	}
    	
    	//makeshift check if there are no hours entered
    	if (form.getAllday().equals("0")){
    		if (form.getRunTime() == null){
    			model.addAttribute("error", "Bitte füllen sie alle Felder aus");
    	    	model.addAttribute("form",form);
    	    	return "addEvent3rd";
    		}
    	}
    	
    	//duration value of 1 means all day, 0 means a certain amount of hours
    	Integer runtime = 0;
    	CMovie movie;
    	
    	if (form.getAllday() != null){
	    	 if (form.getAllday().equals("1")){
	         	runtime = 1405;
	         }
	         
	         if (form.getAllday().equals("0")){
	         	runtime = form.getRunTime() * 60;
	         }
       }
       //creating the actual new "movie" for display
    	movie = new CMovie(form.getTitle(),form.getDescription(),"spec.jpg", runtime, form.getBaseCost().replace(",", "."), form.getRent().replace(",", "."), "0");
    	movie.setIsSpecial(true);
       movie.setIsRent(false);
       movie = dbMovie.save(movie);
       form.setMovieID(movie.getId());
       
       CDateStorage cds = new CDateStorage(businessTime.getTime());
       generator = new Generator(cds, form.getRoomID(), dbMovie.findById(form.getMovieID()),cinema_program);
       Boolean isPrivate;
       if ((form.getB_private() == null)||(form.getB_private().equals(false))){
           isPrivate = false;
       }else isPrivate = true;

       model.addAttribute("days", generator.getCollection());
       model.addAttribute("times", generator.getTime());
       model.addAttribute("form",form);
       model.addAttribute("isPrivate", isPrivate);
       return "addEvent2nd";
       
    }

        

    /**
     * Adds a new event with all new attributs							
     * @param roomId	the specific room id
     * @param movieId	the specific movie id
     * @param year		the specific year
     * @param month		the month of the event
     * @param day		the day of the event
     * @param hour		the hour of the event
     * @param minute	the minutes of the event
     * @param redirectAttributes  Provided by Spring
     * @param isPrivate	private 1 = true 0 = false, needed if a customer wats his own event and meets a room for ir 
     * @return redirect:/addEven.html
     */
    @RequestMapping(value = "/add/{roomId}/{movieId}/{year}/{month}/{day}/{hour}/{minute}/{private}" , method = RequestMethod.GET)
    @PreAuthorize(value = "hasAnyRole('ROLE_BOSS','ROLE_AUTHORIZED')")
    public String addEvent3rd(@PathVariable("roomId") Long roomId,
                              @PathVariable("movieId") Long movieId,
                              @PathVariable("year") Integer year,
                              @PathVariable("month") Integer month,
                              @PathVariable("day") Integer day,
                              @PathVariable("hour") Integer hour,
                              @PathVariable("minute") Integer minute,
                              @PathVariable("private") Integer isPrivate,
                              RedirectAttributes redirectAttributes){
        SaveRoom room = dbRoom.findById(roomId);
        CDateStorage cds = new CDateStorage(minute,hour,day,month,year);
        generator.update();
        Boolean isPrivateB = new Boolean(false);
        if (isPrivate.equals(1))
            isPrivateB = true;
        if (generator.startTimeIsValid(cds)) {
        	if (dbMovie.findById(movieId).getRunTime().equals(1405)){
            	cds = new CDateStorage(0,0,day,month,year);
            }
            cinema_program.createEvent(dbMovie.findById(movieId), room.getRoom(), room.getName(), roomId, cds, new Boolean(isPrivateB), dbRow,dbSeat);
            redirectAttributes.addFlashAttribute("succsesaMsg", new String("Event wurde erfolgreich erstellt."));
        } else redirectAttributes.addFlashAttribute("succsesaMsg", new String("Leider ist etwas schief gelaufen. Vielleicht ist der Termin inzwischen durch einen Anderen Mitarbeiter belegt worden."));

        return "redirect:/addEvent";
    }
}