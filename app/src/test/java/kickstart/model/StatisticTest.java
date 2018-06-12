package kickstart.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.transaction.Transactional;

import junit.framework.TestCase;
import kickstart.Application;
import kickstart.Repositorys.CartRepo;
import kickstart.Repositorys.DBCRoom;
import kickstart.Repositorys.DBDate;
import kickstart.Repositorys.DBEmployee;
import kickstart.Repositorys.DBEvent;
import kickstart.Repositorys.DBMovie;
import kickstart.Repositorys.DBRoom;
import kickstart.Repositorys.DBRow;
import kickstart.Repositorys.DBSeat;
import kickstart.Repositorys.TicketCatalog;
import kickstart.SavedClasses.CCart;
import kickstart.SavedClasses.CCart.CartType;
import kickstart.SavedClasses.CCinemaTicket;
import kickstart.SavedClasses.CCinemaTicket.PriceClass;
import kickstart.SavedClasses.CSeat.Etype;
import kickstart.SavedClasses.CDateStorage;
import kickstart.SavedClasses.CEvent;
import kickstart.SavedClasses.CMovie;
import kickstart.SavedClasses.CRow;
import kickstart.SavedClasses.CSeat;
import kickstart.SavedClasses.Employee;
import kickstart.SavedClasses.SaveRoom;
import kickstart.model.eventManagement.CProgram;

import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)

public class StatisticTest extends TestCase {
	
	private @Autowired BusinessTime businessTime;
	private @Autowired DBRoom dbRoom;
	private @Autowired DBDate dbDate;
	private @Autowired DBMovie dbMovie;
	private @Autowired DBEvent dbEvent;
	private @Autowired DBRow dbRow;
	private @Autowired DBCRoom dbcRoom;
	private @Autowired DBSeat dbSeat;
	private @Autowired DBEmployee dbEmployee;
	private @Autowired CProgram cProgram;
	private @Autowired CartRepo cartRepo;
	private @Autowired TicketCatalog ticketCatalog;
	private @Autowired UserAccountManager userAccountManager;
	
	private Statistic stat;
	private CCart c1, c2, c3, c4;
	SaveRoom sr1 = new SaveRoom("SR1", "ppp-llll-pppp","2.20","3.30");
	SaveRoom sr2 = new SaveRoom("SR2", "ppp-pppp-pppp-pppppp","7.00","9.00");

	@Before
	public void setUp(){
		
		stat = new Statistic(businessTime, cartRepo, ticketCatalog, dbMovie, dbEmployee, dbEvent, dbRoom);
		Money price = Money.of(new BigDecimal("3.00"), "EUR").add(Money.of(new BigDecimal("5.00"), "EUR"));
		
		//first ticket will always be today
		c1 = new CCart("Der Chef", CartType.PURCHASE, "000", businessTime.getTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), "boss@ufo.de");
		c2 = new CCart("Der Chef", CartType.RESERVATION, "845-5", "02.03.2016", "boss@ufo.de");
		c3 = new CCart("Max Meier", CartType.PURCHASE, "000", "02.10.2016", "max@ufo.de");
		c4 = new CCart("Max Meier", CartType.CANCELED, "000", "02.10.2016", "max@ufo.de");
		
		cProgram.createEvent(dbMovie.findById(Long.valueOf(1)), dbRoom.findById(Long.valueOf(1)).getRoom(), getName(), Long.valueOf(1), new CDateStorage(businessTime.getTime().plusDays(Long.valueOf(1))), true, dbRow, dbSeat);
		
		c1.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(2), Integer.valueOf(1), Long.valueOf(1), sr1.getName(), dbEvent.findById(Long.valueOf(1)).getStartTime(), CSeat.Etype.Parkett, "Film"));
		c2.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(1), Integer.valueOf(2), Long.valueOf(2), sr1.getName(), dbEvent.findById(Long.valueOf(1)).getStartTime(), CSeat.Etype.Parkett, "Film"));
		c3.addTicket(new CCinemaTicket("Butter", price, Integer.valueOf(1), Integer.valueOf(2), Long.valueOf(3), sr1.getName(), dbEvent.findById(Long.valueOf(1)).getStartTime(), CSeat.Etype.Parkett, "Film"));
		cartRepo.save(c1);
		cartRepo.save(c2);
		cartRepo.save(c3);
		
		for (CCinemaTicket t : c2.getTickets()){
			t.addCategory("canceled");
		}
	}
	
	DecimalFormat formatter = new DecimalFormat("#0.00");
	
	@Test
	@Transactional
	public void testGetSales() {
		String formatDay = businessTime.getTime().format(DateTimeFormatter.ofPattern("DD.MM.yyyy"));
		Double day = 0.0;
		Double month = 0.0;
		Double year = 0.0;
		for (CCart cart : cartRepo.findByCartType(CartType.PURCHASE)){
            if(cart.getTime().contains(formatDay)){
                for(CCinemaTicket cinemaTicket : cart.getTickets()){
                    day = day + cinemaTicket.getPrice().getNumberStripped().doubleValue();
                }
            }
        }
		for (CEvent event : dbEvent.findAll()){
    		if (event.getIsPrivate() && !event.getMovie().getIsSpecial() && event.getO_startDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("DD.MM.yyyy")).contains(formatDay)){
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
    	    	Double seatTotal = ((Double.valueOf(event.getMovie().getBaseCost()) + Double.valueOf(check.getBasicCharge())) * pitSeats) + ((Double.valueOf(event.getMovie().getBaseCost()) + Double.valueOf(check.getBasicCharge())) * logeSeats);
    			day = day + (seatTotal * 0.7);
    		}
    	}
    	for (CEvent event : dbEvent.findAll()){
    		if (event.getMovie().getIsSpecial() && event.getIsPrivate() && event.getO_startDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("DD.MM.yyyy")).contains(formatDay)){
    			day = day + Double.valueOf(event.getMovie().getBaseCost()) * (event.getMovie().getRunTime() / 60);
    		}
    	}
		assertEquals("Wrong calculation of day's sales", day, stat.getSales("DD.MM.yyyy"));

		//assertEquals("Wrong calculation of month's sales", month, stat.getSales("MM.yyyy"));

		//assertEquals("Wrong calculation of year's sales", year, stat.getSales("yyyy"));
	}
	
	@Test
	@Transactional
	public void testSetTicketSales() {
		dbEmployee.deleteAll();
		for (CCinemaTicket t : c1.getTickets()){
			t.setPriceClass(PriceClass.FULL);
		}
		for (CCinemaTicket t : c2.getTickets()){
			t.setPriceClass(PriceClass.REDUCED);
		}
		for (CCinemaTicket t : c3.getTickets()){
			t.setPriceClass(PriceClass.FULL);
		}
		for (CCinemaTicket t : c4.getTickets()){
			t.setPriceClass(PriceClass.REDUCED);
		}
	}
	
	@Test
	@Transactional
	public void testGetEmployeeExpense() {
		Double salary = 0.0;
		for (Employee emp : dbEmployee.findAll()){
			salary = salary + Double.valueOf(emp.getSalary());
		}
		assertEquals("Message", Double.valueOf(7.5) * salary, stat.getEmployeeExpense(1));
		assertEquals("Message", Double.valueOf(7.5) * salary * Double.valueOf(businessTime.getTime().getDayOfMonth()), stat.getEmployeeExpense(businessTime.getTime().getDayOfMonth()));
	}
	
	@Test
	@Transactional
	public void testGetMovieRentExpenseDay() {
		Double expense = 0.0;
		String formattedDateTime = businessTime.getTime().format(DateTimeFormatter.ofPattern("DD.MM.yyyy"));
		ArrayList<CCinemaTicket> ticketlist = new ArrayList<CCinemaTicket>();
		for (CCart cart : cartRepo.findByCartType(CartType.PURCHASE)){
        	if(cart.getTime().contains(formattedDateTime)){
            	for(CCinemaTicket cinemaTicket : cart.getTickets()){
                ticketlist.add(cinemaTicket);
                	for(String category : cinemaTicket.getCategories()){
                    	if (category.equals("canceled")){
                    		ticketlist.remove(cinemaTicket);
                    	}
                	}
            	}
            }
        }
		for(CMovie movie : dbMovie.findAll()){
            if(movie.getIsRent()){
            	expense = expense + Double.valueOf(movie.getRent());
            }
        }
	    for (CEvent event : dbEvent.findAll()){
	    	if (event.getIsPrivate() && !event.getMovie().getIsSpecial() && event.getO_startDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("DD.MM.yyyy")).contains(formattedDateTime)){
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
	        	Double totalSeatCost = ((Double.valueOf(event.getMovie().getBaseCost()) + Double.valueOf(check.getBasicCharge())) * pitSeats) + ((Double.valueOf(event.getMovie().getBaseCost()) + Double.valueOf(check.getBasicCharge())) * logeSeats);
	        	expense = expense + ((totalSeatCost * 0.7) * 0.3);
	    	}
	    }
        for (CCinemaTicket ticket : ticketlist){
        	expense = expense + 0.3 * ticket.getPrice().getNumberStripped().doubleValue();
        }
		assertEquals("Wrong calculation of day's rent expenses", formatter.format(expense), formatter.format(stat.getMovieRentExpense("DD.MM.yyyy")));
	}
	
	@Test
	@Transactional
	public void testGetMovieRentExpenseMonth() {
		Double expense = 0.0;
		String formattedDateTime = businessTime.getTime().format(DateTimeFormatter.ofPattern("MM.yyyy"));
		ArrayList<CCinemaTicket> ticketlist = new ArrayList<CCinemaTicket>();
		for (CCart cart : cartRepo.findByCartType(CartType.PURCHASE)){
        	if(cart.getTime().contains(formattedDateTime)){
            	for(CCinemaTicket cinemaTicket : cart.getTickets()){
                ticketlist.add(cinemaTicket);
                	for(String category : cinemaTicket.getCategories()){
                    	if (category.equals("canceled")){
                    		ticketlist.remove(cinemaTicket);
                    	}
                	}
            	}
            }
        }
		for(CMovie movie : dbMovie.findAll()){
            if(movie.getIsRent()){
            	expense = expense + Double.valueOf(movie.getRent());
            }
        }
	    for (CEvent event : dbEvent.findAll()){
	    	if (event.getIsPrivate() && !event.getMovie().getIsSpecial() && event.getO_startDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("MM.yyyy")).contains(formattedDateTime)){
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
	        	Double totalSeatCost = ((Double.valueOf(event.getMovie().getBaseCost()) + Double.valueOf(check.getBasicCharge())) * pitSeats) + ((Double.valueOf(event.getMovie().getBaseCost()) + Double.valueOf(check.getBasicCharge())) * logeSeats);
	        	expense = expense + ((totalSeatCost * 0.7) * 0.3);
	    	}
	    }
        for (CCinemaTicket ticket : ticketlist){
        	expense = expense + 0.3 * ticket.getPrice().getNumberStripped().doubleValue();
        }
		assertEquals("Wrong calculation of month's rent expenses", formatter.format(expense), formatter.format(stat.getMovieRentExpense("MM.yyyy")));
	}
	
	@Test
	@Transactional
	public void testGetMovieRentExpenseYear() {
		Double expense = 0.0;
		String formattedDateTime = businessTime.getTime().format(DateTimeFormatter.ofPattern("yyyy"));
		ArrayList<CCinemaTicket> ticketlist = new ArrayList<CCinemaTicket>();
		for (CCart cart : cartRepo.findByCartType(CartType.PURCHASE)){
        	if(cart.getTime().contains(formattedDateTime)){
            	for(CCinemaTicket cinemaTicket : cart.getTickets()){
                ticketlist.add(cinemaTicket);
                	for(String category : cinemaTicket.getCategories()){
                    	if (category.equals("canceled")){
                    		ticketlist.remove(cinemaTicket);
                    	}
                	}
            	}
            }
        }
		for(CMovie movie : dbMovie.findAll()){
            if(movie.getIsRent()){
            	expense = expense + (Double.valueOf(movie.getRent()) * businessTime.getTime().getMonthValue());
            }
        }
	    for (CEvent event : dbEvent.findAll()){
	    	if (event.getIsPrivate() && !event.getMovie().getIsSpecial() && event.getO_startDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy")).contains(formattedDateTime)){
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
	        	Double totalSeatCost = ((Double.valueOf(event.getMovie().getBaseCost()) + Double.valueOf(check.getBasicCharge())) * pitSeats) + ((Double.valueOf(event.getMovie().getBaseCost()) + Double.valueOf(check.getBasicCharge())) * logeSeats);
	        	expense = expense + ((totalSeatCost * 0.7) * 0.3);
	    	}
	    }
        for (CCinemaTicket ticket : ticketlist){
        	expense = expense + 0.3 * ticket.getPrice().getNumberStripped().doubleValue();
        }
		assertEquals("Wrong calculation of year's rent expenses", formatter.format(expense), formatter.format(stat.getMovieRentExpense("yyyy")));
	}
	
	@Test
	public void testFormatMoney() {
		assertEquals("Generated string not as expected.","232,50 €", stat.formatMoney(Double.valueOf(232.5)));
	}
	
	@Test
	@Transactional
	public void testGetDailyMoney() {
		//testing money values with one public, normal event
		Double salary = 0.0;
		for (Employee emp : dbEmployee.findAll()){
			salary = salary + Double.valueOf(emp.getSalary());
		}
		Double t = 0.0;
		Double tf = 0.0;
		for (CCinemaTicket ticket : c1.getTickets()){
			tf = ticket.getPrice().getNumberStripped().doubleValue();
			t = ticket.getPrice().getNumberStripped().doubleValue() * 0.3;
		}
		stat.getEmployeeExpense(1);
		stat.getSales("DD.MM.yyyy");
		assertEquals("Wrong day's expenses (normal)", Double.valueOf(7.5) * salary, stat.getDailyExpense());
		assertEquals("Wrong day's sales loss (normal)", tf - (Double.valueOf(7.5) * salary), stat.getDailySalesLoss());
		
		//testing money values with one public, special event
		dbMovie.findById(Long.valueOf(1)).setIsSpecial(true);
		stat.getMovieRentExpense("DD.MM.yyyy");
		assertEquals("Wrong day's expenses (special)", Double.valueOf(7.5) * salary, stat.getDailyExpense());
		assertEquals("Wrong day's sales loss (special)", tf - (Double.valueOf(7.5) * salary), stat.getDailySalesLoss());
		
		//testing money values with one private, special event
		dbEvent.findById(Long.valueOf(1)).setIsPrivate(true);
		stat.getMovieRentExpense("DD.MM.yyyy");
		stat.getSales("DD.MM.yyyy");
		assertEquals("Wrong day's expenses (private, special)", Double.valueOf(7.5) * salary, stat.getDailyExpense());
		//assertEquals("Wrong day's sales loss (private, special)", tf - (Double.valueOf(7.5) * salary), stat.getDailySalesLoss());
		
		//testing money values with one private, normal event
		dbMovie.findById(Long.valueOf(1)).setIsSpecial(false);
		stat.getMovieRentExpense("DD.MM.yyyy");
		stat.getSales("DD.MM.yyyy");
		assertEquals("Wrong day's expenses (private)", Double.valueOf(7.5) * salary, stat.getDailyExpense());
		//assertEquals("Wrong day's sales loss (private)", tf + (44 * 0.7) - (Double.valueOf(7.5) * salary), stat.getDailySalesLoss());
	}
	
	
	
	@Test
	@Transactional
	public void testGetMonthlyMoney() {
		//testing money values with one public, normal event
		Double salary = 0.0;
		for (Employee emp : dbEmployee.findAll()){
			salary = salary + Double.valueOf(emp.getSalary());
		}
		Double tf = 0.0;
		Double t = 0.0;
		for (CCinemaTicket ticket : c1.getTickets()){
			tf = ticket.getPrice().getNumberStripped().doubleValue();
			t = ticket.getPrice().getNumberStripped().doubleValue() * 0.3;
		}
		stat.getEmployeeExpense(businessTime.getTime().getDayOfMonth());
		stat.getMovieRentExpense("MM.yyyy");
		stat.getSales("MM.yyyy");
//		assertEquals("Wrong month's expenses (normal)", ((salary * (Double.valueOf(7.5)) * businessTime.getTime().getDayOfMonth()) + t + privshow + Double.valueOf(dbMovie.findById(Long.valueOf(1)).getRent())), stat.getMonthlyExpense());
//		assertEquals("Wrong month's sales loss (normal)", stat.getSales("MM.yyyy") + stat.getMonthlyExpense(), stat.getMonthlySalesLoss());
		
//		//testing money values with one public, special event
//		dbMovie.findById(Long.valueOf(1)).setIsSpecial(true);
//		stat.getMovieRentExpense("MM.yyyy");
//		assertEquals("Wrong month's expenses (special)", Double.valueOf(5835.26), stat.getMonthlyExpense());
//		assertEquals("Wrong month's sales loss (special)", Double.valueOf(-5826.06), stat.getMonthlySalesLoss());
//		
//		//testing money values with one private, special event
//		dbEvent.findById(Long.valueOf(1)).setIsPrivate(true);
//		stat.getMovieRentExpense("MM.yyyy");
//		stat.getSales("MM.yyyy");
//		assertEquals("Wrong month's expenses (private, special)", Double.valueOf(5825.26), stat.getMonthlyExpense());
//		assertEquals("Wrong month's sales loss (private, special)", Double.valueOf(-5806.06), stat.getMonthlySalesLoss());
//		
//		//testing money values with one private, normal event
//		dbMovie.findById(Long.valueOf(1)).setIsSpecial(false);
//		stat.getMovieRentExpense("MM.yyyy");
//		stat.getSales("MM.yyyy");
//		assertEquals("Wrong month's expenses (private)", Double.valueOf(5862.22), stat.getMonthlyExpense());
//		assertEquals("Wrong month's sales loss (private)", "-5729,82", df.format(stat.getMonthlySalesLoss()));
	}
	
	@Test
	@Transactional
	public void testGetAnnualMoney() {
		Double salary = 0.0;
		for (Employee emp : dbEmployee.findAll()){
			salary = salary + Double.valueOf(emp.getSalary());
		}
		Double tf = 0.0;
		Double t = 0.0;
		for (CCinemaTicket ticket : c1.getTickets()){
			t = ticket.getPrice().getNumberStripped().doubleValue();
			t = ticket.getPrice().getNumberStripped().doubleValue() * 0.3;
		}
		stat.getEmployeeExpense(businessTime.getTime().getDayOfYear());
		stat.getMovieRentExpense("yyyy");
		stat.getSales("yyyy");
//		assertEquals("Wrong year's expenses (normal)", ((salary * (Double.valueOf(7.5)) * businessTime.getTime().getDayOfYear()) + t + t + privshow + Double.valueOf(dbMovie.findById(Long.valueOf(1)).getRent())), stat.getAnnualExpense());
//		assertEquals("Wrong year's sales loss (normal)",stat.getSales("yyyy") + stat.getAnnualExpense(), stat.getAnnualSalesLoss());
		
//		//testing money values with one public, special event
//		dbMovie.findById(Long.valueOf(1)).setIsSpecial(true);
//		stat.getMovieRentExpense("yyyy");
//		assertEquals("Wrong year's expenses (special)", Double.valueOf(34899.14), stat.getAnnualExpense());
//		assertEquals("Wrong year's sales loss (special)", Double.valueOf(-34885.34), stat.getAnnualSalesLoss());
//		
//		//testing money values with one private, special event
//		dbEvent.findById(Long.valueOf(1)).setIsPrivate(true);
//		stat.getMovieRentExpense("yyyy");
//		stat.getSales("yyyy");
//		assertEquals("Wrong year's expenses (private, special)", Double.valueOf(34889.14), stat.getAnnualExpense());
//		assertEquals("Wrong year's sales loss (private, special)", Double.valueOf(-34865.34), stat.getAnnualSalesLoss());
//		
//		//testing money values with one private, normal event
//		dbMovie.findById(Long.valueOf(1)).setIsSpecial(false);
//		stat.getMovieRentExpense("yyyy");
//		stat.getSales("yyyy");
//		assertEquals("Wrong year's expenses (private)", Double.valueOf(34926.1), stat.getAnnualExpense());
//		assertEquals("Wrong year's sales loss (private)", "-34789,10", df.format(stat.getAnnualSalesLoss()));
	}

	@Test
	@Transactional
	public void testGetPercentages() {
		ArrayList<Integer> daylist = new ArrayList<Integer>(Arrays.asList(100, 0, 0, 0));
		assertEquals("Received wrong day's percentages", daylist, stat.getPercentages("DD.MM.yyyy"));
	}
}
