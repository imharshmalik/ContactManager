package com.manager.service;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
	public boolean sendEmailWithNoAttachment(String subject, String content, String to) 
	{
		boolean flag = false;
		String from = "xxxxx6002@gmail.com";
		
		// STEP 1: SETTING UP PROPERTIES 
		Properties properties = System.getProperties(); 
		properties.put("mail.smtp.host","smtp.gmail.com"); 
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
	  
	  
		// STEP 2: SETTING UP SESSION OBJECT 
		Session currentSession = Session.getInstance(properties, new Authenticator() 
		{ 
			protected PasswordAuthentication getPasswordAuthentication() 
			{ 
				return new  PasswordAuthentication("xxxx6002@gmail.com", "xxxxxxxxxxx"); 
			} 
		});
		  
		  
		// STEP 3: COMPOSING THE EMAIL USING MIMEMESSAGE 
		MimeMessage newEmail = new MimeMessage(currentSession); 
		try 
		{ 
			newEmail.setText(content);
			newEmail.setSubject(subject); newEmail.setFrom(from);
			newEmail.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		} 
		catch (Exception e) 
		{
			e.printStackTrace(); 
		}
		  
		  
		// STEP 4: SENDING THE EMAIL USING TRANSPORT CLASS
		try
		{
			Transport.send(newEmail);
			flag = true;
			System.out.println("Email Sent!"); 
		} 
		catch (Exception e)
		{
			e.printStackTrace(); 
		}
		
		return flag;
	}
}
