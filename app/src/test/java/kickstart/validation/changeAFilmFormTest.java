package kickstart.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class changeAFilmFormTest extends TestCase {
	
	@Autowired private Validator validator;
	private changeAFilmForm form;
	
	@Before
	public void setUp(){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        form = new changeAFilmForm();
        form.setBaseCost("2,50");
		form.setFsk("12");
		form.setDescription("Text");
		form.setRunTime("90");
		form.setTitle("Title");
		form.setRent("1,10");
		form.setIsRent(true);
	}

	@Test
	public void testchangeAFilmFormSuccess() {
        Set<ConstraintViolation<changeAFilmForm>> violations = validator.validate(form);
        assertTrue("A field has returned a validation error", violations.isEmpty());
	}
	
	@Test
	public void testchangeAFilmFormFailure() {
		form = new changeAFilmForm();
		form.setBaseCost("x");
		form.setFsk("25");
		form.setDescription("");
		form.setRunTime("");
		form.setTitle("");
		form.setRent("");
        Set<ConstraintViolation<changeAFilmForm>> violations = validator.validate(form);
        assertEquals("Fields have not returned the proper amount of violations", 8, violations.size());
	}
	
	@Test
	public void testGetter() {
		assertEquals("Recieved wrong base cost value", "2,50", form.getBaseCost());
		assertEquals("Recieved wrong description", "Text", form.getDescription());
		assertEquals("Recieved wrong fsk value", "12", form.getFsk());
		assertEquals("Recieved wrong rent amount value", "1,10", form.getRent());
		assertEquals("Recieved wrong runtime value", "90", form.getRunTime());
		assertEquals("Recieved wrong title value", "Title", form.getTitle());
		assertTrue("Rent status should be true", form.getIsRent());
	}

}
