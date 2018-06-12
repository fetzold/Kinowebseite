package kickstart.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import kickstart.AbstractWebIntegrationTest;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class RentA_FilmControllerTest  extends AbstractWebIntegrationTest {

	@Test
	public void rentafilmIntegrationTest() throws Exception {
		mvc.perform(get("/rentafilm.html")
				.with(user("boss").roles("BOSS")))
				.andExpect(view().name("rentafilm"))
				.andExpect(status().isOk());
		
		mvc.perform(get("/rentafilm.html")
				.with(user("auth").roles("AUTHORIZED")))
				.andExpect(view().name("rentafilm"))
				.andExpect(status().isOk());
	
		mvc.perform(get("/rentafilm.html")
				.with(user("emp").roles("EMPLOYEE")))
				.andExpect(status().isForbidden());
	}
	
	@Test
	public void rentafilmpostIntegrationTest() throws Exception {
		
		final String fileName = "test.jpg";
		final byte[] content = "Hallo Word".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile("fileData", fileName,"image/jpeg", content);
		
		mvc.perform(post("/rentafilm")
				.with(user("boss").roles("BOSS"))
				.requestAttr("file", mockMultipartFile)
				.param("title", "")
				.param("description", "")
				.param("runTime", "120")
				.param("baseCost", "1,20")
				.param("rent", "1,20")
				.param("fsk", "12"))
				.andExpect(view().name("rentafilm"))
				.andExpect(model().attributeHasErrors("rentafilmform"))
				.andExpect(model().attributeHasFieldErrors("rentafilmform", "title"))
                .andExpect(model().attributeHasFieldErrors("rentafilmform", "description"))
                .andExpect(model().attribute("rentafilmform", hasProperty("runTime", is("120"))))
                .andExpect(model().attribute("rentafilmform", hasProperty("baseCost", is("1,20"))))
                .andExpect(model().attribute("rentafilmform", hasProperty("rent", is("1,20"))))
                .andExpect(model().attribute("rentafilmform", hasProperty("fsk", is("12"))))
				.andExpect(status().isOk());
	}
	
	@Test
	public void changefilmIntegrationTest() throws Exception {
		mvc.perform(get("/changeafilm")
				.with(user("boss").roles("BOSS"))
				.param("movie", "1"))
				.andExpect(view().name("changeafilm"))
				.andExpect(model().attributeExists("form", "movie"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void editfilmpostIntegrationTest() throws Exception {
		mvc.perform(post("/changeafilm")
				.with(user("boss").roles("BOSS"))
				.param("movie", "1")
				.param("baseCost", "2.50"))
				.andExpect(view().name("changeafilm"))
				.andExpect(model().attributeExists("form"))
				.andExpect(model().attributeHasErrors("form"))
				.andExpect(status().isOk());
		
		mvc.perform(post("/changeafilm")
				.with(user("boss").roles("BOSS"))
				.param("movie", "1")
				.param("title", "Neu")
				.param("isRent", "true")
				.param("description", "Ohje")
				.param("runTime", "120")
				.param("baseCost", "1,20")
				.param("rent", "1,20")
				.param("fsk", "12"))
				.andExpect(view().name("redirect:/rentafilm"))
				.andExpect(status().isFound());
		
	}
	
	
	
	@Test
	public void changeimageIntegrationTest() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "original_filename.jpg", null, "data".getBytes());
		mvc.perform(fileUpload("/fileupload").file(file))
				.andExpect(status().is(405));
		
	}
	
	@Test
	public void getImageIntegrationTest() throws Exception {
		
	}
	
}
