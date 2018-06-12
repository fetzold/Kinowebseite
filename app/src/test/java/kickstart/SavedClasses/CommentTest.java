package kickstart.SavedClasses;


//import kickstart.SavedClasses.CDateStorage;
//import kickstart.SavedClasses.CRoom;
import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import kickstart.validation.CommentForm;
import org.junit.Before;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CommentTest {
	
	private Comment cm;
	private LocalDateTime ldt;
	private CommentForm form;
	@Autowired private Validator validator;

	
	@Before
	public void setUp(){
		ldt = LocalDateTime.now();
		cm = new Comment("Inhalt", 4, new CDateStorage(ldt), "Bob", "Meier", "meiermail@dot.com", Long.valueOf(1));
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
	}
	
	@Test
	public void testContent(){
		assertEquals("Comment contains wrong text", "Inhalt", cm.getText());
		assertEquals("Comment contains wrong email", "meiermail@dot.com", cm.getEmail());
		assertEquals("Comment contains wrong rating", 4, cm.getRating());
		assertEquals("Comment contains wrong date", new CDateStorage(ldt).toLocalDateTime(), cm.getDate());
		assertEquals("Comment contains wrong forename", "Bob", cm.getForname());
		assertEquals("Comment contains wrong lastname", "Meier", cm.getLastname());
		assertEquals("Comment contains wrong movie id", Long.valueOf(1), cm.getMovieid());
		assertEquals("Content of comment is turned into wrong String", "Inhalt", cm.toString());
	}
	
	@Test
	public void testForm(){
		form = new CommentForm();
		form.setComment("ABC");
		form.setRating("3");
		Set<ConstraintViolation<CommentForm>> violations = validator.validate(form);
		assertTrue("A field has returned a validation error", violations.isEmpty());
	}
	
	@Test
	public void testFormFalse(){
		form = new CommentForm();
		form.setComment("");
		form.setRating("");
		Set<ConstraintViolation<CommentForm>> violations = validator.validate(form);
        assertEquals("Fields have not returned the proper amount of violations", 3, violations.size());
	}

}
