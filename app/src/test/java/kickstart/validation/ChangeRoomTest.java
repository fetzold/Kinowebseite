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

public class ChangeRoomTest extends TestCase {
	
	@Autowired private Validator validator;
	private ChangeRoom form;
	
	@Before
	public void setUp(){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        form = new ChangeRoom();
        form.setBasicCharge("2,20");
		form.setExtraCharge("1,29");
		form.setName("Raum1");
		form.setRoomId(Long.valueOf(2));
		form.setAction(Integer.valueOf(1));
		form.setSeats("5");
	}

	@Test
	public void testChangeRoomSuccess() {
        Set<ConstraintViolation<ChangeRoom>> violations = validator.validate(form);
        assertTrue("A field has returned a validation error", violations.isEmpty());
	}
	
	@Test
	public void testChangeRoomFailure() {
		form = new ChangeRoom();
        form.setBasicCharge("2g20");
		form.setExtraCharge("xxx");
		form.setSeats("b");
        Set<ConstraintViolation<ChangeRoom>> violations = validator.validate(form);
        assertEquals("Fields have not returned the proper amount of violations", 3, violations.size());
	}
	
	@Test
	public void testGetter() {
		assertEquals("Recieved wrong basic value", "2,20", form.getBasicCharge());
		assertEquals("Recieved wrong extra value", "1,29", form.getExtraCharge());
		assertEquals("Recieved wrong name value", "Raum1", form.getName());
		assertEquals("Recieved wrong room id", "2", form.getRoomId());
		assertEquals("Recieved wrong action value", Integer.valueOf(1), form.getAction());
		assertEquals("Recieved wrong seat value", "5", form.getSeats());
	}

}
