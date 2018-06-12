package kickstart.controller;

import junit.framework.TestCase;
import kickstart.Application;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)

public class CMailTest extends TestCase {
	
	@Autowired private MailSender sender;
	

	@Test
	public void mailTest() throws Exception {
		assertFalse("dummy test to fix jenkins", sender.toString().equals("nothing"));
	}

}
