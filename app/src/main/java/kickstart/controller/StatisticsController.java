package kickstart.controller;

import java.util.ArrayList;
import java.util.List;

import kickstart.Repositorys.DBEmployee;
import kickstart.Repositorys.DBEvent;
import kickstart.Repositorys.DBMovie;
import kickstart.Repositorys.DBRoom;
import kickstart.SavedClasses.CMovie;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import kickstart.Repositorys.CartRepo;
import kickstart.Repositorys.TicketCatalog;
import kickstart.model.Statistic;
import kickstart.SavedClasses.CEvent;

import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

import java.util.*;

/**
 * The Statistics Controller. It creates the statistic for the Boss/Admin
 * @author fetzold
 * @since 02.11.15
 */

@Controller
public class StatisticsController {

    private Statistic statistic ;
    private BusinessTime businessTime;
    private CartRepo cartrepo;
    private TicketCatalog ticketCatalog;
    private @Autowired DBMovie dbMovie;
    private @Autowired DBEvent dbEvent;
    private @Autowired DBEmployee dbEmployee;
    private @Autowired DBRoom dbRoom;

    /**
     * Management Controller for all statistics calls
     * @param businessTime BusinessTime time provided by SalesPoint
     * @param cartrepo CartRepo CCart-Repository
     * @param ticketCatalog TicketCatalog CCinemaTicket-Repository
     * @param dbMovie DBMovie movie database
     * @param dbEvent DBEvent event database
     * @param dbEmployee DBEmployee employee database
     * @param dbRoom DBRoom SaveRoom-database
     */
    
    @Autowired
    public StatisticsController(BusinessTime businessTime, CartRepo cartrepo, TicketCatalog ticketCatalog, DBMovie dbMovie, DBEvent dbEvent, DBEmployee dbEmployee, DBRoom dbRoom) {

        this.businessTime = businessTime;
        this.cartrepo = cartrepo;
        this.ticketCatalog = ticketCatalog;
        this.dbMovie = dbMovie;
        this.dbEvent = dbEvent;
        this.dbEmployee = dbEmployee;
        this.dbRoom = dbRoom;
        statistic = new Statistic(businessTime, cartrepo, ticketCatalog, dbMovie, dbEmployee, dbEvent, dbRoom);
    }
      
    /**
     * Displays main statistics page
     * @return statistic.html
     */
    
    @RequestMapping("/statistic.html")
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    public String statistic() {
        return "statistic";
    }

    /**
     * Displays main page of monetary statistics
     * @param model Model provided by Spring
     * @return getMoney.html
     */
    
    @RequestMapping("getMoney.html")
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    public String getMoney(Model model){

        return "getMoney";
    }
    
    /**
     * Displays summary of ticket sales
     * @param model Model provided by Spring
     * @return getTicketsales.html
     */
    
    @RequestMapping(value ="getTicketsales")
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    public String getTicketsales(Model model){
        
        //tägliche Ticketverkäufe
        model.addAttribute("dayMap", statistic.setTicketSales("dd.MM.yyyy"));
        
        //monatliche Ticketverkäufe
        model.addAttribute("monthMap", statistic.setTicketSales("MM.yyyy"));
                
        //jährliche Ticketverkäufe
        Map<CMovie,List<Integer>> m = statistic.setTicketSales("dd.MM.yyyy");
        model.addAttribute("yearMap", statistic.setTicketSales("yyyy"));
        
        ArrayList<String> movies = new ArrayList<String>();
        Collection<Integer> allTickets = new ArrayList<>();
        
        Iterator<CMovie> i1 = m.keySet().iterator();
        while (i1.hasNext()){
            movies.add(i1.next().getName());
        }
        Iterator<List<Integer>> i2 = m.values().iterator();
        while (i2.hasNext()){
            allTickets.add(i2.next().get(2));
        }

        model.addAttribute("movies", movies);
        model.addAttribute("allTickets", allTickets);

        return "getTicketsales";
    }
    
    /**
     * returns list of fill of rooms for events
     * @param model Model provided by Spring
     * @return getTicketsalesevent.html
     */
    
    @RequestMapping(value ="getTicketsalesevent")
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    public String getTicketsalesEvent(Model model){
        
    	List<ArrayList<CEvent>> mvl = new ArrayList<ArrayList<CEvent>>();
    	ArrayList<Long> pEvents = new ArrayList<Long>();
    	
    	for (CEvent event : dbEvent.findAll()){
    		if (event.getIsPrivate()){
    			pEvents.add(event.getMovie().getId());
    		}
    	}
    	
    	
    	for (CMovie movie : dbMovie.findAll()){
    		if (movie.getIsSpecial() && pEvents.contains(movie.getId())){
    			continue;
    		}
    		ArrayList<CEvent> mv = new ArrayList<CEvent>();
    		for (CEvent event : dbEvent.findAll()){
    			if (event.getMovie().getName().equals(movie.getName()) && !event.getIsPrivate()){
    				mv.add(event);
    			}
    			
    		}
    		mvl.add(mv);
    	}
    	
    	model.addAttribute("alllist", mvl);
    	
        model.addAttribute("events", dbMovie.findAll());
        model.addAttribute("movies", dbMovie.findAll());
        return "getTicketsalesevent";
    }
    
    
    /**
     * Displays summary of daily money income and costs
     * @param model Model provided by Spring
     * @return getMoneyDay.html
     */
    
    @RequestMapping(value ="getMoneyDay")
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    public String getMoneyDay(Model model){
        
        //Ausgaben Mitarbeiter am Tag
        model.addAttribute("dailyEmployeeExpense", statistic.formatMoney(statistic.getEmployeeExpense(1)));
        
        //Tageseinkommen
        model.addAttribute("dailySales", statistic.formatMoney(statistic.getSales("dd.MM.yyyy")));
        
        //Tagesgesamtausgaben
        model.addAttribute("dailyExpense", statistic.formatMoney(statistic.getDailyExpense()));
        
        //Gesamtumsatz
        model.addAttribute("dailySalesLoss", statistic.formatMoney(statistic.getDailySalesLoss()));
        
      //Prozente Transaktionstypen
        model.addAttribute("sales", statistic.getPercentages("dd.MM.yyyy"));
        
        return "getMoneyDay";
    }
    
    /**
     * Displays summary of monthly money income and costs
     * @param model Model provided by Spring
     * @return getMoneyMonth.html
     */
    
    @RequestMapping(value ="getMoneyMonth")
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    public String getMoneyMonth(Model model){
        
        //Ausgaben Mitarbeiter im Monat
        model.addAttribute("employeeExpenseMonth", statistic.formatMoney(statistic.getEmployeeExpense(businessTime.getTime().getDayOfMonth())));
        
        //Filmausgaben im Monat
        model.addAttribute("MovieExpenseMonthly", statistic.formatMoney(statistic.getMovieRentExpense("MM.yyyy")));
        
        //Gesamtausgaben Monat
        model.addAttribute("monthlyExpense", statistic.formatMoney(statistic.getMonthlyExpense()));
        
        //Monatseinkommen
        model.addAttribute("monthlySales", statistic.formatMoney(statistic.getSales("MM.yyyy")));
        
        //Monatsumsatz
        model.addAttribute("monthlySalesLoss", statistic.formatMoney(statistic.getMonthlySalesLoss()));
        
      //Prozente Transaktionstypen
        model.addAttribute("sales", statistic.getPercentages("MM.yyyy"));
        
        return "getMoneyMonth";
    }
    
    /**
     * Displays summary of yearly money income and costs
     * @param model Model provided by Spring
     * @return getMoneyYear.html
     */
    
    @RequestMapping("getMoneyYear.html")
    @PreAuthorize(value = "hasRole('ROLE_BOSS')")
    public String getMoneyYear(Model model){

        //Ausgaben Mitarbeiter im Jahr
        model.addAttribute("employeeExpenseAnnual", statistic.formatMoney(statistic.getEmployeeExpense(businessTime.getTime().getDayOfYear())));

        //Filmausgaben im Jahr
        model.addAttribute("MovieExpenseAnnual", statistic.formatMoney(statistic.getMovieRentExpense("yyyy")));
        
        //Gesamtausgaben im Jahr
        model.addAttribute("annualExpense", statistic.formatMoney(statistic.getAnnualExpense()));
        
        //Jahreseinkommen
        model.addAttribute("annualSales", statistic.formatMoney(statistic.getSales("yyyy")));
        
        //Jahresumsatz
        model.addAttribute("annualSalesLoss", statistic.formatMoney(statistic.getAnnualSalesLoss()));
        
        //Prozente Transaktionstypen
        model.addAttribute("sales", statistic.getPercentages("yyyy"));
        
        return "getMoneyYear";
    }
}
