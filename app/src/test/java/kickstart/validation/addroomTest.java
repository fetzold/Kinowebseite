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

public class addroomTest extends TestCase {
	
	@Autowired private Validator validator;
	private addroom form;
	
	@Before
	public void setUp(){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        form = new addroom();
        form.setBasicCharge("2,20");
		form.setExtraCharge("1,29");
		form.setName("Raum1");
		form.setRows("2");
		form.setSeats("5");
	}

	@Test
	public void testaddroomSuccess() {
        Set<ConstraintViolation<addroom>> violations = validator.validate(form);
        assertTrue("A field has returned a validation error", violations.isEmpty());
	}
	
	@Test
	public void testaddroomFailure() {
		form = new addroom();
        form.setBasicCharge("2.20");
		form.setExtraCharge("xxx");
		form.setName("");
		form.setRows("a");
		form.setSeats("b");
        Set<ConstraintViolation<addroom>> violations = validator.validate(form);
        assertEquals("Fields have not returned the proper amount of violations", 7, violations.size());
	}
	
	@Test
	public void testGetter() {
		assertEquals("Recieved wrong basic value", "2,20", form.getBasicCharge());
		assertEquals("Recieved wrong extra value", "1,29", form.getExtraCharge());
		assertEquals("Recieved wrong name value", "Raum1", form.getName());
		assertEquals("Recieved wrong row value", "2", form.getRows());
		assertEquals("Recieved wrong seat value", "5", form.getSeats());
	}

}
