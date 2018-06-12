package kickstart.controller;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * @author Locha08
 */

public class CMail 
{
	private SimpleMailMessage mail;
	private MailSender sender;
	
	/**
	 * 
	 * @param sender MailSender: The Mailsender Object recieved form Spring  
	 */
	CMail(MailSender sender)
	{
		this.sender = sender;
	}	
	
	/**
	 * 
	 * @param To 		String: the EMail address
	 * @param Title		String: the title of the Mail
	 * @param Message	String: the Message inside the Mail
	 */
	public void SendMail(String To, String Title, String Message)
	{
		mail = new SimpleMailMessage();
		mail.setTo(To);
		mail.setSubject(Title);
		mail.setText(Message);
		this.sender.send(mail);
	}
}
