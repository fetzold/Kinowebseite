package kickstart.model;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

import kickstart.Repositorys.CartRepo;

import java.util.*;

import kickstart.Repositorys.DBEmployee;
import kickstart.Repositorys.DBEvent;
import kickstart.Repositorys.DBMovie;
import kickstart.Repositorys.DBRoom;
import kickstart.Repositorys.TicketCatalog;
import kickstart.SavedClasses.CCart;
import kickstart.SavedClasses.CEvent;
import kickstart.SavedClasses.CMovie;
import kickstart.SavedClasses.CRow;
import kickstart.SavedClasses.CSeat;
import kickstart.SavedClasses.CSeat.Etype;
import kickstart.SavedClasses.SaveRoom;
import kickstart.SavedClasses.CCart.CartType;
import kickstart.SavedClasses.CCinemaTicket;
import kickstart.SavedClasses.Employee;

import org.salespointframework.time.BusinessTime;

import java.lang.String;

import kickstart.SavedClasses.CCinemaTicket.PriceClass;

import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author felix
 */
public class Statistic {
    
    //Ergebnisvariablen für Einkommen
    private Double sales;
    
    //Ergebnisvairablen für Ausgaben der Mitarbeiter
    private Double employeeExpense;
    
    //Ergebnisvairablen für Ausgaben der Mitarbeiter
    private Double movieRentExpense;
    
    //Ergebnisvairablen für Gesamtausgaben
    private Double dailyExpense;
    private Double monthlyExpense;
    private Double annualExpense;
    
    //Ergebnissvariablen für Umsatz
    private Double dailySalesLoss;
    private Double monthlySalesLoss;
    private Double annualSalesLoss;
    
    //Ergebnisvariablen für Ticketverkäufe
    private Integer countReducedTicket;
    private Integer countFullTicket;
    private Integer countAllTicket;
    
    
    private @Autowired CartRepo cartRepo;
    private @Autowired TicketCatalog ticketCatalog;
    private @Autowired DBMovie dbMovie;
    private @Autowired DBEmployee dbEmployee;
    private @Autowired DBEvent dbEvent;
    private @Autowired DBRoom dbRoom;
    private BusinessTime businessTime;

//    private Collection<Integer> moneyRed = new ArrayList<>();
//    private Collection<Integer> moneyFull = new ArrayList<>();
//    private Collection<Integer> moneyAll = new ArrayList<>();
    
    private Double seventyPercent = Double.valueOf(0.7);
    private Double thirtyPercent = Double.valueOf(0.3);

    public Statistic(BusinessTime businessTime, CartRepo cartRepo, TicketCatalog ticketCatalog, DBMovie dbMovie, DBEmployee dbEmployee, DBEvent dbEvent, DBRoom dbRoom) {
        this.businessTime = businessTime;
        this.cartRepo = cartRepo;
        this.ticketCatalog = ticketCatalog;
        this.dbEmployee = dbEmployee;
        this.dbMovie = dbMovie;
        this.dbEvent = dbEvent;
        this.dbRoom = dbRoom;
    }

    /**
     * Iterates through all tickets, compares them against a given day, month or year,
     * and sums up the money of all sold tickets.
     * @param format String Formatter pattern for the date comparison
     * @return the amount of the money from the sold tickets
     */
    
    public Double getSales(String format){
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = businessTime.getTime().format(formatter); 
        
        sales = 0.0;
        
        //summing up all sold tickets
        for (CCart cart : cartRepo.findByCartType(CartType.PURCHASE)){
            if(cart.getTime().contains(formattedDateTime)){
                for(CCinemaTicket cinemaTicket : cart.getTickets()){
                    sales = sales + cinemaTicket.getPrice().getNumberStripped().doubleValue();
                }
            }
        }
        
        //adding income from private events
        for (CEvent event : getPrivateEvents(formattedDateTime)){
        	//adds base price of movie + pit seats * amount of all seats regardless of type, then adds a 30% discount
        	sales = sales + (getTotalSeatCost(event) * seventyPercent);
        }
        
        //add income from one-time private screenings, the full amount entered at creation times event length in hours
        for (CEvent event : getSpecialEvents(formattedDateTime)){
        	sales = sales + Double.valueOf(event.getMovie().getBaseCost()) * (event.getMovie().getRunTime() / 60);
        }
        
        return sales;
    }

    /**
     * Iterate through all stuff members, search the income per hour, sum it and multiply with 7,5 
     * and the amount of the days given
     * @param day int days to sum up
     * @return the costs for the employees
     */
    public Double getEmployeeExpense(int day){
        employeeExpense = 0.0;
        for(Employee employee : dbEmployee.findAll()){
            employeeExpense = employeeExpense + (Double.valueOf(employee.getSalary()) * 7.5 * day); 
        }
        return employeeExpense;
    }

    /**
     * Basicly it sums all rent expenses
     * 
     * Iterater over all Movies, check if is rented
     * Iterate over all tickets, search all sold tickets
     * check if the date is matching the given pattern
     * Search all cinema tickets, check if the movie name equals ticket name
     * sum the money and multiply 30% + rent expense 
     * @param format String pattern to format and search the date by
     * @return returns the rent expenses
     */
    public Double getMovieRentExpense(String format){
        movieRentExpense = 0.0;
        ArrayList<CCinemaTicket> ticketlist = new ArrayList<CCinemaTicket>();
        ArrayList<CMovie> movielist = new ArrayList<CMovie>();
        String formattedDateTime = businessTime.getTime().format(DateTimeFormatter.ofPattern(format));

        //building list of all rent movies
        for(CMovie movie : dbMovie.findAll()){
            if(movie.getIsRent()){
            	movielist.add(movie);
            }
        }
        
        //adding all public one-time movies to the list
        for (CEvent event : getPublicSpecials(formattedDateTime)){
        	movielist.add(event.getMovie());
        }
        
        //building list of all sold tickets within the timeframe
        for (CCart cart : cartRepo.findByCartType(CartType.PURCHASE)){
        	if(cart.getTime().contains(formattedDateTime)){
            	for(CCinemaTicket cinemaTicket : cart.getTickets()){
            		if (!cinemaTicket.getCategories().toString().contains("canceled"))
            			ticketlist.add(cinemaTicket);
            	}
            }
        }
        	
        //add up movie rent costs and multiply by months if calculating annual expenses
        for (CMovie movie : movielist){
        	movieRentExpense = movieRentExpense + Double.valueOf(movie.getRent());
        	if (format.equals("yyyy")){
        		movieRentExpense = movieRentExpense * businessTime.getTime().getMonthValue();
        	}
        }
        
        //add 30% of sales income per ticket to expenses
        for (CCinemaTicket ticket : ticketlist){
        	movieRentExpense = movieRentExpense + thirtyPercent * ticket.getPrice().getNumberStripped().doubleValue();
        }
        
      //add 30% of sales income for private screenings of rent films
        for (CEvent event : getPrivateEvents(formattedDateTime)){
        	movieRentExpense = movieRentExpense + ((getTotalSeatCost(event) * seventyPercent) * thirtyPercent);
        }
        
        return movieRentExpense;
    }

/* ******************************************************************************
•   Tagesgesamtausgaben
*******************************************************************************/      
    public Double getDailyExpense() {
        dailyExpense = employeeExpense;
        return dailyExpense;
    }
   
/* ******************************************************************************
•   Monatsgesamtausgaben
*******************************************************************************/
    public Double getMonthlyExpense() {
        monthlyExpense = employeeExpense + movieRentExpense;
        return monthlyExpense;
    }
    
/* ******************************************************************************
•   Jahresgesamtausgaben 
*******************************************************************************/
    public Double getAnnualExpense() {
        annualExpense = employeeExpense + movieRentExpense;
        return annualExpense;
    }
    
/* ******************************************************************************
•   Tagesumsatz
*******************************************************************************/
    public Double getDailySalesLoss() {
        dailySalesLoss = sales - dailyExpense;
        return dailySalesLoss;
    }
    
/* ******************************************************************************
•   Monatsumsatz
*******************************************************************************/
    public Double getMonthlySalesLoss() {
        monthlySalesLoss = sales - monthlyExpense;
        return monthlySalesLoss;  
    }
    
/* ******************************************************************************
•   Jahresumsatz 
*******************************************************************************/
    public Double getAnnualSalesLoss() {
        annualSalesLoss = sales - annualExpense;
        return annualSalesLoss;
    }
    
    /**
     * Calculates the daily ticket sales
     * 
     * Iterate over all movies, search for tickets that has been sold
     * filter tickets from today, if the ticket is reduced, increase amount of reduced tickets
     * else increase the variable for full tickets
     * after all tickets has been evaluated, calculate the sales for all tickets
     * after all tickets has been calculated get the film title
     * @param format String pattern to format and search the date by
     * @return returns the hashmap with updated values
     */
    public Map<CMovie, List<Integer>> setTicketSales(String format){
        //Aktuelle Zeit holen und formatieren
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = businessTime.getTime().format(formatter);

        Map<CMovie, List<Integer>> m = new HashMap<>();
        Set<CMovie> activeMovies = new HashSet<CMovie>(); 
        
        for (CEvent event : dbEvent.findAll()){
        	if (!event.getIsPrivate()){
        		activeMovies.add(event.getMovie());
        	}
        }
        for(CMovie movie : activeMovies){
            countReducedTicket = Integer.valueOf(0);
            countFullTicket = Integer.valueOf(0);
            countAllTicket = Integer.valueOf(0);
            
            for (CCart cart : cartRepo.findByCartType(CartType.PURCHASE)){
                if(cart.getTime().contains(formattedDateTime)){
                    for(CCinemaTicket cinemaTicket : cart.getTickets()){
                        if (cinemaTicket.getMoviename().equals(movie.getName()) && !cinemaTicket.getCategories().toString().contains("canceled")) {
                            if (cinemaTicket.getPriceClass().equals(PriceClass.REDUCED)) {
                                countReducedTicket++;
                            } else {
                                countFullTicket++;

                            }
                        }
                    }
                }
            }
            countAllTicket = countReducedTicket + countFullTicket;
            List<Integer> c = new ArrayList<>();
            c.add(new Integer(countReducedTicket));
            c.add(new Integer(countFullTicket));
            c.add(new Integer(countAllTicket));
            m.put(movie, c);
        }
        return m;
    }
    
    /**
     * Formats monetary amounts to be limited to 2 digits after the comma
     * @param money Double Money to be formatted
     * @return output String money amount including currency symbol
     */
    
    public String formatMoney(Double money){
		String pattern = "#0.00";
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(money) + " €";
		return output;
	}
    
    /**
     * Searches all events for private events that match the given time frame
     * @param formattedDateTime String date to search for
     * @return list of all private non-special events
     */
    
    private ArrayList<CEvent> getPrivateEvents(String formattedDateTime){
    	ArrayList<CEvent> privateEvents = new ArrayList<CEvent>();
    	for (CEvent event : dbEvent.findAll()){
    		if (event.getIsPrivate() && !event.getMovie().getIsSpecial() && event.getO_startDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("DD.MM.yyyy")).contains(formattedDateTime)){
    			privateEvents.add(event);
    		}
    	}
    	return privateEvents;
    }
    
    /**
     * Searches all events for one-time private movie screenings that match the given time frame
     * @param formattedDateTime String date to search for
     * @return list of all private and special events
     */
    
    private ArrayList<CEvent> getSpecialEvents(String formattedDateTime){
    	ArrayList<CEvent> specialEvents = new ArrayList<CEvent>();
    	for (CEvent event : dbEvent.findAll()){
    		if (event.getMovie().getIsSpecial() && event.getIsPrivate() && event.getO_startDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("DD.MM.yyyy")).contains(formattedDateTime)){
    			specialEvents.add(event);
    		}
    	}
    	return specialEvents;
    }
    
    /**
     * Searches all events for one-time public movie screenings that match the given time frame
     * @param formattedDateTime String date to search for
     * @return list of all public and special events
     */
    
    private ArrayList<CEvent> getPublicSpecials(String formattedDateTime){
    	ArrayList<CEvent> publicEvents = new ArrayList<CEvent>();
    	for (CEvent event : dbEvent.findAll()){
    		if (event.getMovie().getIsSpecial() && !event.getIsPrivate() && event.getO_startDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("DD.MM.yyyy")).contains(formattedDateTime)){
    			publicEvents.add(event);
    		}
    	}
    	
    	return publicEvents;
    }
    
    /**
     * Sums up the total cost of a fully sold-out room
     * @param event CEvent event to count the seats for
     * @return total price of all seats
     */
    
    private Double getTotalSeatCost(CEvent event){
    	SaveRoom check = dbRoom.findById(event.getO_room().getI_id());
    	Double pitSeats = 0.0;
    	Double logeSeats = 0.0;
    	for (CRow row : event.getO_room().getMyRows()){
            for (CSeat seat : row.getSeats()){
            	if (seat.getE_Type().equals(Etype.Loge)){
            		logeSeats++;
            	}
            	if (seat.getE_Type().equals(Etype.Parkett)){
            		pitSeats++;
            	}
            }
    	}
    	Double pitTotal = ((Double.valueOf(event.getMovie().getBaseCost()) + Double.valueOf(check.getBasicCharge())) * pitSeats);
    	Double logeTotal = ((Double.valueOf(event.getMovie().getBaseCost()) + Double.valueOf(check.getBasicCharge())) * logeSeats);
    	return pitTotal + logeTotal;
    }

    /**
     * Method calculating the percentages of the different types of possible transactions.
     * For simplicity this method does not add tickets of entirely canceled purchases but
     * ignores single cancellations within valid carts.
     * @param format String Formatter pattern for the date comparison
     * @return list of 4 integer values in order of sales, online-sales, reservations, online-reservations
     */
    
	public ArrayList<Integer> getPercentages(String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedDateTime = businessTime.getTime().format(formatter);
        
        //setting up lists to count the tickets
        ArrayList<CCinemaTicket> reservations = new ArrayList<CCinemaTicket>();
        ArrayList<CCinemaTicket> onlinereservations = new ArrayList<CCinemaTicket>();
        ArrayList<CCinemaTicket> localsales = new ArrayList<CCinemaTicket>();
        ArrayList<CCinemaTicket> onlinesales = new ArrayList<CCinemaTicket>();
        
        //list to compare names against easily
        ArrayList<String> employees = new ArrayList<String>();
        for (Employee emp : dbEmployee.findAll()){
        	employees.add(emp.getForename() + " " + emp.getLastname());
        }
        employees.add("Der Chef");
        
        //sorting all sold tickets within a time frame
        for (CCart cart : cartRepo.findByCartType(CartType.PURCHASE)){
        	if (cart.getTime().contains(formattedDateTime)){
        		if (employees.contains(cart.getUser())){
        			for (CCinemaTicket ticket : cart.getTickets()){
        				localsales.add(ticket);
        			}
        		}
        		else {
        			for (CCinemaTicket ticket : cart.getTickets()){
        				onlinesales.add(ticket);
        			}
        		}
        	}
        }
        
      //sorting all reservations within a time frame
        for (CCart cart : cartRepo.findByCartType(CartType.RESERVATION)){
        	if (cart.getTime().contains(formattedDateTime)){
        		if (employees.contains(cart.getUser())){
        			for (CCinemaTicket ticket : cart.getTickets()){
        				reservations.add(ticket);
        			}
        		}
        		else {
        			for (CCinemaTicket ticket : cart.getTickets()){
        				onlinereservations.add(ticket);
        			}
        		}
        	}
        }
        
        //creating List to be returned to Controller
        Integer total = localsales.size() + onlinesales.size() + reservations.size() + onlinereservations.size();
        ArrayList<Integer> percentages = new ArrayList<Integer>();
        if (total.equals(0)){
        	percentages.add(0);
        	percentages.add(0);
        	percentages.add(0);
        	percentages.add(0);
        }else {
        percentages.add(localsales.size() * 100 / total);
        percentages.add(onlinesales.size() * 100 / total);
        percentages.add(reservations.size() * 100 / total);
        percentages.add(onlinereservations.size() * 100 / total);
        }
        
		return percentages;
	}
    
}
 