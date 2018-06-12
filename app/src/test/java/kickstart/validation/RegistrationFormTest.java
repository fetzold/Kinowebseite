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

public class RegistrationFormTest extends TestCase {
	
	@Autowired private Validator validator;
	private RegistrationForm form;
	
	@Before
	public void setUp(){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        form = new RegistrationForm();
        form.setDelete(false);
		form.setEmail("bob@mail.de");
		form.setForename("Bob");
		form.setName("Mups");
		form.setPassword("123");
		form.setPhone("123456789");
	}

	@Test
	public void testRegistrationFormSuccess() {
        Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form);
        assertTrue("A field has returned a validation error", violations.isEmpty());
	}
	
	@Test
	public void testRegistrationFormFailure() {
		form = new RegistrationForm();
        form.setDelete(false);
		form.setEmail("");
		form.setForename("");
		form.setName("");
		form.setPassword("");
		form.setPhone("abc");
        Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form);
        assertEquals("Fields have not returned the proper amount of violations", 6, violations.size());
	}
	
	@Test
	public void testGetter() {
		assertEquals("Recieved wrong email value", "bob@mail.de", form.getEmail());
		assertEquals("Recieved wrong forename value", "Bob", form.getForename());
		assertEquals("Recieved wrong lastname value", "Mups", form.getName());
		assertEquals("Recieved wrong phone value", "123456789", form.getPhone());
		assertEquals("Recieved wrong password value", "123", form.getPassword());
		assertFalse("Deletion value should be false", form.getDelete());
	}

}
