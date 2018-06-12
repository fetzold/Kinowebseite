package kickstart.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import kickstart.AbstractWebIntegrationTest;
import kickstart.Application;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class LoginControllerTest  extends AbstractWebIntegrationTest {

	@Test
	public void welcomeIntegrationTest() throws Exception {
		mvc.perform(get("/"))
				.andExpect(view().name("index"))
				.andExpect(status().isOk());
	
		mvc.perform(get("/index.html"))
				.andExpect(view().name("index"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void registerNewIntegrationTest() throws Exception {
		mvc.perform(get("/registerNew"))
				.andExpect(view().name("register"))
				.andExpect(status().isOk());
	}
	@Test
	public void registerNewSuccessIntegrationTest() throws Exception {
		mvc.perform(get("/registerNew")
				.param("Forename", "Matty")
				.param("Name", "May")
				.param("Email", "mail@mail.de")
				.param("Phone", "123456789")
				.param("Password", "abc"))
		.andExpect(view().name("redirect:registerSuccess"))
		.andExpect(status().is(HttpStatus.FOUND.value())); 
	} 
	
	@Test
	public void registerIntegrationTest() throws Exception {
		mvc.perform(get("/register"))
				.andExpect(view().name("register"))
				.andExpect(model().attributeExists("registrationForm"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void timemachineIntegrationTest() throws Exception {
		mvc.perform(get("/timemachine.html"))
			.andExpect(view().name("timemachine"))
			.andExpect(status().isOk());
	}


	@Test
	public void changetimeIntegrationTest() throws Exception {
		mvc.perform(post("/timemachine.html")
				.param("hours", "20"))
			.andExpect(model().attributeExists("success"))
			.andExpect(view().name("timemachine"))
			.andExpect(status().isOk());

		mvc.perform(post("/timemachine.html")
				.param("hours", "abc"))
			.andExpect(model().attributeExists("error"))
			.andExpect(status().isOk());
		
		mvc.perform(post("/timemachine.html")
				.param("hours", "-9"))
			.andExpect(model().attributeExists("success"))
			.andExpect(view().name("timemachine"))
			.andExpect(status().isOk());
		
		mvc.perform(post("/timemachine.html")
				.param("hours", "-d"))
			.andExpect(model().attributeExists("error"))
			.andExpect(status().isOk());
	}
}

