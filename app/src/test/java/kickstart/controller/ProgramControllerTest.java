package kickstart.controller;

import kickstart.AbstractWebIntegrationTest;
import kickstart.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)

public class ProgramControllerTest extends AbstractWebIntegrationTest {


	@Test
	public void programIntegrationTest() throws Exception {
		mvc.perform(get("/program"))
				.andExpect(model().attributeExists("program"))
				.andExpect(model().attribute("program", is(iterableWithSize(not(100000000)))))
				.andExpect(view().name("program"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void changeprogramIntegrationTest() throws Exception {
		mvc.perform(get("/changeprogram")
				.with(user("boss").roles("BOSS", "AUTHORIZED")))
				.andExpect(model().attributeExists("movies"))
				.andExpect(model().attribute("movies", is(iterableWithSize(not(10000000)))))
				.andExpect(view().name("changeprogram"))
				.andExpect(status().isOk());
	}

}
