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

public class addEmployeeTest extends TestCase {
	
	@Autowired private Validator validator;
	private addEmployee form;
	
	@Before
	public void setUp(){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        form = new addEmployee();
		form.setDelete(false);
		form.setEmail("bob@ufo.de");
		form.setForename("Bob");
		form.setLastname("Meier");
		form.setPassword("abc");
		form.setPhone("1234567890");
		form.setSalary("1,20");
		form.setStreet("Bodengasse 4");
		form.setTown("Kleinstadt");
		form.setUserName(form.getEmail());
		form.setUserRightsId(Long.valueOf(1));
		form.setZip("01547");
	}

	@Test
	public void testaddEmployeeSuccess() {
        Set<ConstraintViolation<addEmployee>> violations = validator.validate(form);
        assertTrue("A field has returned a validation error", violations.isEmpty());
	}
	
	@Test
	public void testaddEmployeeFailure() {
		form = new addEmployee();
        form.setEmail("2");
        form.setForename("");
		form.setLastname("");
		form.setPhone("abc");
		form.setSalary("xy");
		form.setStreet("");
		form.setTown("");
		form.setZip("01547-345");
        Set<ConstraintViolation<addEmployee>> violations = validator.validate(form);
        assertEquals("Fields have not returned the proper amount of violations", 10, violations.size());
	}
	
	@Test
	public void testGetDelete(){
		assertFalse("Value should be false", form.getDelete());
	}
	
	@Test
	public void testGetter() {
		assertEquals("Recieved wrong email value", "bob@ufo.de", form.getEmail());
		assertEquals("Recieved wrong forename value", "Bob", form.getForename());
		assertEquals("Recieved wrong lastname value", "Meier", form.getLastname());
		assertEquals("Recieved wrong phone value", "1234567890", form.getPhone());
		assertEquals("Recieved wrong salary value", "1,20", form.getSalary());
		assertEquals("Recieved wrong street value", "Bodengasse 4", form.getStreet());
		assertEquals("Recieved wrong town value", "Kleinstadt", form.getTown());
		assertEquals("Recieved wrong zip value", "01547", form.getZip());
		assertEquals("Recieved wrong user rights value", Long.valueOf(1), form.getUserRightsId());
	}

}
