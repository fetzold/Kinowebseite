package kickstart.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import kickstart.AbstractWebIntegrationTest;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class StatisticsControllerTest  extends AbstractWebIntegrationTest {



	@Test
	public void statisticIntegrationTest() throws Exception {
		mvc.perform(get("/statistic.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("statistic"))
				.andExpect(status().isOk());
	
		mvc.perform(get("/statistic.html")
				.with(user("emp").roles("EMPLOYEE")))
				.andExpect(status().isForbidden());
	}
	
	@Test
	public void getMoneyIntegrationTest() throws Exception {
		mvc.perform(get("/getMoney.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("getMoney"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getMoneyDayIntegrationTest() throws Exception {
		mvc.perform(get("/getMoneyDay.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("getMoneyDay"))
				.andExpect(model().hasNoErrors())
				.andExpect(model().attributeExists("dailyEmployeeExpense", "dailyExpense", "dailyExpense", "dailySales", "dailySalesLoss", "sales"))
				.andExpect(model().attribute("sales", hasSize(4)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getMoneyMonthIntegrationTest() throws Exception {
		mvc.perform(get("/getMoneyMonth.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("getMoneyMonth"))
				.andExpect(model().hasNoErrors())
				.andExpect(model().attributeExists("employeeExpenseMonth", "MovieExpenseMonthly", "monthlyExpense", "monthlySales", "monthlySalesLoss", "sales"))
				.andExpect(model().attribute("sales", hasSize(4)))
				.andExpect(status().isOk());
		
	}
	
	@Test
	public void getMoneyYearIntegrationTest() throws Exception {
		mvc.perform(get("/getMoneyYear.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("getMoneyYear"))
				.andExpect(model().hasNoErrors())
				.andExpect(model().attributeExists("employeeExpenseAnnual", "MovieExpenseAnnual", "annualExpense", "annualSales", "annualSalesLoss", "sales"))
				.andExpect(model().attribute("sales", hasSize(4)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getTicketsalesIntegrationTest() throws Exception {
		mvc.perform(get("/getTicketsales.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("getTicketsales"))
				.andExpect(model().hasNoErrors())
				.andExpect(status().isOk());
	}
	
	@Test
	public void getTicketsalesEventIntegrationTest() throws Exception {
		mvc.perform(get("/getTicketsalesevent.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("getTicketsalesevent"))
				.andExpect(model().hasNoErrors())
				.andExpect(status().isOk());
	}
}
