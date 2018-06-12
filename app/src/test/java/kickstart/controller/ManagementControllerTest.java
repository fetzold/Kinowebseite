/**
 * @author Alexej
 */
package kickstart.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import kickstart.AbstractWebIntegrationTest;
import kickstart.Application;
import kickstart.Repositorys.DBRoom;
import kickstart.SavedClasses.SaveRoom;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ManagementControllerTest extends AbstractWebIntegrationTest{
	
	@Autowired private DBRoom dbRoom;
	@Autowired private BusinessTime businessTime;
	
	@Test
	public void optionsIntegrationTest() throws Exception{
		
		mvc.perform(get("/options.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("redirect:staff"))
				.andExpect(status().isFound());

		mvc.perform(get("/options.html")
				.with(user("customer").roles("CUSTOMER")))
				.andExpect(view().name("redirect:account.html"))
				.andExpect(status().isFound());
	
	}
	
	  @Test
	  public void accountIntegrationTest() throws Exception
	  {

			mvc.perform(get("/account.html")
					.with(user("customer").roles("CUSTOMER"))
					.param("forename", "HansDieter")
					.param("name", "Zollmann")
					.param("phone", "0123123123123123")
					.param("password","wasd"))
				.andExpect(model().hasNoErrors());
			
			mvc.perform(get("/account.html")
					.with(user("boss").roles("BOSS"))
					.param("forename", "Agent")
					.param("name", "FortySeven")
					.param("phone", "640509-040147")
					.param("password","BloodMoney")
					.param("email","BaldyKiller47@your.death")
					.param("street", "DeadEnd47")
					.param("zip", "04747")
					.param("town", "Hitmania"))
				.andExpect(model().hasNoErrors());
			
			mvc.perform(get("/account.html")
					.with(user("authorized").roles("AUTHORIZED"))
					.param("forename", "Agent")
					.param("name", "FortySeven")
					.param("phone", "640509-040147")
					.param("password","BloodMoney")
					.param("email","BaldyKiller47@your.death")
					.param("street", "DeadEnd47")
					.param("zip", "04747")
					.param("town", "Hitmania"))
				.andExpect(model().hasNoErrors());
			
			mvc.perform(get("/account.html")
					.with(user("employee").roles("Employee"))
					.param("forename", "Agent")
					.param("name", "FortySeven")
					.param("phone", "640509-040147")
					.param("password","BloodMoney")
					.param("email","BaldyKiller47@your.death")
					.param("street", "DeadEnd47")
					.param("zip", "04747")
					.param("town", "Hitmania"))
				.andExpect(model().hasNoErrors());
						
	  }
	  
	@Test
	public void staffIntegrationTest() throws Exception{
		mvc.perform(get("/staff.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("staff"))
				.andExpect(model().attributeExists("customers", "customerdetail"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void addRoomIntegrationTest() throws Exception{
		mvc.perform(get("/addRoom.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("addRoom"))
				.andExpect(model().attributeExists("form"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void addroomsIntegrationTest() throws Exception{
		mvc.perform(post("/addRoom.html")
				.with(user("boss").roles("BOSS"))
				.param("rows", "6")
				.param("seats", "3")
				.param("name", "120")
				.param("basicCharge", "1,20")
				.param("extraCharge", "1,20"))
				.andExpect(view().name("redirect:/addRoom"))
				.andExpect(status().isFound());
		
		mvc.perform(post("/addRoom.html")
				.with(user("boss").roles("BOSS"))
				.param("rows", "b")
				.param("seats", "a")
				.param("name", "120")
				.param("basicCharge", "1,20")
				.param("extraCharge", "1,20"))
				.andExpect(view().name("addRoom"))
				.andExpect(status().isOk());
	}
	
	
	@Test
	public void changeroomIntegrationTest() throws Exception{
		
		String id = "";
		for (SaveRoom s : dbRoom.findAllActive()){
			id = s.getId().toString();
			break;
		}
		mvc.perform(get("/changeRoom.html")
				.with(user("boss").roles("BOSS"))
				.param("cRoom", id))
				.andExpect(view().name("changeRoom"))
				.andExpect(model().attributeExists("form", "rooms"))
				.andExpect(model().attribute("rooms", is(iterableWithSize(not(1000000)))))
				.andExpect(status().isOk());
		
		
		/*		mvc.perform(post("/changeRoom.html")
				.with(user("boss").roles("BOSS"))
				.param("rows", "6")
				.param("seats", "3")
				.param("name", "120")
				.param("basicCharge", "1,20")
				.param("extraCharge", "1,20"))
				.andExpect(view().name("redirect:/changeRoom"))
				.andExpect(status().isFound());
		
		mvc.perform(post("/changeRoomDetail.html")
				.with(user("boss").roles("BOSS"))
				.param("getDelete()", "true")
				.param("cRoom", id)
				.param("rows", "6")
				.param("seats", "3")
				.param("name", "120")
				.param("basicCharge", "2,50")
				.param("extraCharge", "2,40"))
				.andExpect(model().attributeExists("form", "seats", "room"))
				.andExpect(model().attributeHasNoErrors("form"))
				.andExpect(view().name("changeRoomDetail"))
				.andExpect(status().isOk()); */
	}
	
/*	@Test
	public void changeSeatsIntegrationTest() throws Exception{
		
		String id = "";
		for (SaveRoom s : dbRoom.findAllActive()){
			id = s.getId().toString();
			break;
		}
		mvc.perform(post("/changeSeats")
				.with(user("boss").roles("BOSS"))
				.flashAttr("form2.getAction", 1)
				.param("cRoom", id))
				.andExpect(view().name("changeRoomDetail"))
				.andExpect(model().attributeExists("form", "rooms"))
				.andExpect(status().isOk());
	}*/
	
	@Test
	public void eventIntegrationTest() throws Exception{
		mvc.perform(get("/addEvent.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("addEvent"))
				.andExpect(model().attributeExists("form", "rooms", "movies"))
				.andExpect(model().attribute("rooms", is(iterableWithSize(1))))
				.andExpect(status().isOk());
	}
	
	@Test
	public void chooseEventIntegrationTest() throws Exception{
		mvc.perform(get("/changeEvent.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("chooseEvent"))
				.andExpect(model().attributeExists("form", "events"))
				.andExpect(status().isOk());
	}
	
/*	@Test
	public void chooseRoomIntegrationTest() throws Exception{
		String id = "";
		for (CEvent e : dbEvent.findAll()){
			id = e.getId().toString();
			break;
		}
		mvc.perform(post("/chooseRoom.html")
				.with(user("boss").roles("BOSS"))
				.param("eventID", id))
				.andExpect(view().name("chooseRoom"))
				.andExpect(model().attributeExists("form", "rooms"))
				.andExpect(model().attribute("rooms", is(iterableWithSize(1))))
				.andExpect(status().isOk());
	} */
	
	@Test
	public void employeeIntegrationTest() throws Exception{
		mvc.perform(get("/addEmployee.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("addEmployee"))
				.andExpect(status().isOk());
		}
	
	@Test
	public void detailEmployeeIntegrationTest() throws Exception{
		mvc.perform(post("/detailEmployee.html")
				.with(user("boss").roles("BOSS"))
				.param("emp", "3"))
				.andExpect(view().name("detailEmployee"))
				.andExpect(model().attributeExists("employee"))
				.andExpect(status().isOk());
}
	
	@Test
	public void editEmployeeIntegrationTest() throws Exception{		
		mvc.perform(post("/editEmployee.html" )
				.with(user("boss").roles("BOSS"))
				.param("emp", "2")
				.param("id", "2")
				.param("forename", "Bob")
				.param("name", "Meier")
				.param("phone", "123123123")
				.param("password","asf")
				.param("email","bobmeier@ufo.de")
				.param("street", "Wand 3")
				.param("zip", "87654")
				.param("town", "Dahinten")
				.param("salary", "2,40")
				.param("userName", "bobob")
				.param("userRightsId", "1"))
				.andExpect(view().name("editEmployee"))
				.andExpect(model().attributeHasNoErrors("form"))
				.andExpect(model().attributeExists("id", "form"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void modifyEmployeeIntegrationTest() throws Exception{
		mvc.perform(post("/modifyEmployee.html")
				.with(user("boss").roles("BOSS"))
				.param("empid", "2")
				.param("forename", "Bob")
				.param("zip", "87654")
				.param("town", "Dahinten")
				.param("salary", "2,40")
				.param("userName", "bobob")
				.param("userRightsId", "1"))
				.andExpect(model().attributeExists("form"))
				.andExpect(model().attributeHasErrors("form"))
				.andExpect(view().name("editEmployee"))
				.andExpect(status().isOk());
		
		mvc.perform(post("/modifyEmployee.html")
				.with(user("boss").roles("BOSS"))
				.param("empid", "2")
				.param("forename", "Bob")
				.param("lastname", "Meier")
				.param("phone", "123123123")
				.param("password","asf")
				.param("email","bobmeier@ufo.de")
				.param("street", "Wand 3")
				.param("zip", "87654")
				.param("town", "Dahinten")
				.param("salary", "2,40")
				.param("userName", "bobob")
				.param("userRightsId", "1"))
				.andExpect(view().name("redirect:/staff"))
				.andExpect(status().isFound());
	}
	
	@Test
	public void deleteemployeeIntegrationTest() throws Exception{
		mvc.perform(post("/deleteEmployee.html")
				.with(user("boss").roles("BOSS"))
				.param("employee", "3")
				.param("id", "3"))
				.andExpect(view().name("redirect:/staff"))
				.andExpect(status().isFound());
	}
	
	@Test
	public void addEventIntegrationTest() throws Exception{
		mvc.perform(get("/addEvent1st.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(model().attributeExists("form", "rooms", "movies"))
				.andExpect(view().name("addEvent1st"))
				.andExpect(status().isOk());
		
		mvc.perform(post("/addEvent2nd.html")
				.with(user("boss").roles("BOSS"))
				.param("movieID", "1"))
				.andExpect(model().attributeExists("form", "times", "days", "isPrivate"))
				.andExpect(view().name("addEvent2nd"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void addEventSpecialIntegrationTest() throws Exception{
		mvc.perform(post("/addEventSpecial.html")
				.with(user("boss").roles("BOSS"))
				.param("movieID", "-1"))
				.andExpect(model().attributeExists("form"))
				.andExpect(view().name("addEvent3rd"))
				.andExpect(status().isOk());
		
		mvc.perform(post("/addEventSpecial.html")
				.with(user("boss").roles("BOSS"))
				.param("movieID", "-1")
				.param("allday", "1")
				.param("title", "Bugs")
				.param("description", "ABCDEF")
				.param("rent", "1,40")
				.param("baseCost", "1,40")
				.param("b_private", "true")
				.param("roomID", "1"))
				.andExpect(model().attributeExists("form", "times", "days", "isPrivate"))
				.andExpect(view().name("addEvent2nd"))
				.andExpect(status().isOk());
	}

	@Test
	public void custChangeIntegrationTest() throws Exception{
		mvc.perform(post("/custChange")
				.with(user("customer").roles("CUSTOMER"))
				.param("forename", "Test")
				.param("delete", "false"))
				.andExpect(redirectedUrl("account.html"))
				.andExpect(status().isFound())
				.andExpect(flash().attributeExists("succsesaMsg"));

		mvc.perform(post("/custChange")
				.with(user("Hans").roles("CUSTOMER"))
				.param("forename", "Test")
				.param("name", "Test")
				.param("email", "test@was.de")
				.param("password", "***")
				.param("phone", "12345679")
				.param("delete", "false"))
				.andExpect(redirectedUrl("account.html"))
				.andExpect(status().isFound())
				.andExpect(flash().attributeExists("succsesaMsg"));
		
		mvc.perform(post("/custChange")
				.with(user("Hans").roles("CUSTOMER"))
				.param("forename", "Test")
				.param("name", "Test")
				.param("email", "test@was.de")
				.param("password", "fgh")
				.param("phone", "12345679")
				.param("delete", "false"))
				.andExpect(redirectedUrl("account.html"))
				.andExpect(status().isFound())
				.andExpect(flash().attributeExists("succsesaMsg"));
		
		mvc.perform(post("/custChange")
				.with(user("customer").roles("CUSTOMER"))
				.param("forename", "LOESCHEN")
				.param("password", "123")
				.param("delete", "true"))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect: /"));
		
		mvc.perform(post("/custChange")
				.with(user("customer").roles("CUSTOMER"))
				.param("forename", "abc")
				.param("password", "123")
				.param("delete", "true"))
				.andExpect(view().name("redirect:account.html"))
				.andExpect(status().isFound());

		mvc.perform(post("/custChange")
				.with(user("customer").roles("CUSTOMER"))
				.param("forename", "")
				.param("delete", "false"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("account.html"))
				.andExpect(flash().attributeExists("succsesaMsg"));
	}

		@Test
		public void empChangeIntegrationTest() throws Exception{

			mvc.perform(post("/empChange")
				.with(user("employee").roles("EMPLOYEE"))
				.param("forename", "Bob")
				.param("lastname", "Meier")
				.param("phone", "123123123")
				.param("password","asf")
				.param("email","bobmeier@ufo.de")
				.param("street", "Wand 3")
				.param("zip", "87654")
				.param("town", "Dahinten")
				.param("salary", "2,40")
				.param("userName", "bobob")
				.param("userRightsId", "1"))
				.andExpect(redirectedUrl("account.html"))
				.andExpect(status().isFound())
				.andExpect(flash().attributeExists("succsesaMsg"));
			
			mvc.perform(post("/empChange")
					.with(user("employee").roles("EMPLOYEE"))
					.param("forename", "Bob")
					.param("salary", "2,40")
					.param("userName", "bobob")
					.param("userRightsId", "1"))
					.andExpect(redirectedUrl("account.html"))
					.andExpect(status().isFound())
					.andExpect(flash().attributeExists("succsesaMsg"));
		
	}
		
		@Test
		public void addemployeeIntegrationTest() throws Exception{		
			mvc.perform(post("/addEmployee.html" )
					.with(user("boss").roles("BOSS"))
					.param("emp", "2")
					.param("id", "2")
					.param("forename", "Bob")
					.param("name", "Meier")
					.param("salary", "2,40")
					.param("userName", "bobob")
					.param("userRightsId", "1"))
					.andExpect(view().name("addEmployee"))
					.andExpect(status().isOk());
			
			int count = businessTime.getTime().getNano();
			String user = "" + count + "@ufo.de";
			
			mvc.perform(post("/addEmployee.html" )
					.with(user("boss").roles("BOSS"))
					.param("emp", "2")
					.param("id", "2")
					.param("forename", "Bob")
					.param("lastname", "Meier")
					.param("phone", "123123123")
					.param("password","asf")
					.param("email","bobmeier@ufo.de")
					.param("street", "Wand 3")
					.param("zip", "87654")
					.param("town", "Dahinten")
					.param("salary", "2,40")
					.param("userName", user)
					.param("userRightsId", "1"))
					.andExpect(view().name("redirect:/staff"))
					.andExpect(flash().attributeExists("succsesaMsg"))
					.andExpect(status().isFound());
		}
}
