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

public class addEventTest extends TestCase {
	
	@Autowired private Validator validator;
	private addEvent form;
	
	@Before
	public void setUp(){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        form = new addEvent();
        form.setAllday("1");
		form.setB_private(false);
		form.setBaseCost("2,50");
		form.setDelete(false);
		form.setDescription("Text");
		form.setEventId(Long.valueOf(5));
		form.setMovieID(Long.valueOf(4));
		form.setRent("1,20");
		form.setRoomID(Long.valueOf(2));
		form.setRunTime(Integer.valueOf(120));
		form.setTitle("Titel");
	}

	@Test
	public void testaddEventSuccess() {
        Set<ConstraintViolation<addEvent>> violations = validator.validate(form);
        assertTrue("A field has returned a validation error", violations.isEmpty());
	}
	
	@Test
	public void testaddEventFailure() {
		form = new addEvent();
		form.setAllday("");
		form.setBaseCost("2t50");
		form.setDescription("");
		form.setRent("abc");
        Set<ConstraintViolation<addEvent>> violations = validator.validate(form);
        assertEquals("Fields have not returned the proper amount of violations", 7, violations.size());
	}
	
	@Test
	public void testGetter() {
		assertEquals("Recieved wrong duration value", "1", form.getAllday());
		assertEquals("Recieved wrong base cost value", "2,50", form.getBaseCost());
		assertEquals("Recieved wrong description", "Text", form.getDescription());
		assertEquals("Recieved wrong rent value", "1,20", form.getRent());
		assertEquals("Recieved wrong title value", "Titel", form.getTitle());
		assertFalse("Recieved wrong private value", form.getB_private());
		assertFalse("Recieved wrong delete value", form.getDelete());
		assertEquals("Recieved wrong event id", Long.valueOf(5), form.getEventId());
		assertEquals("Recieved wrong movie id", Long.valueOf(4), form.getMovieID());
		assertEquals("Recieved wrong room id", Long.valueOf(2), form.getRoomID());
		assertEquals("Recieved wrong runtime value", Integer.valueOf(120), form.getRunTime());
	}

}
