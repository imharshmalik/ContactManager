package com.manager.controllers;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.manager.entity.User;
import com.manager.helper.Message;
import com.manager.repository.UserRepository;
import com.manager.service.EmailService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPasswordController
{
	Random random = new Random(10000);
	
	@Autowired
	private EmailService emailservice;
	
	@Autowired
	private UserRepository userRepoObj;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@RequestMapping("/forgot-password")
	public String forgotPassword(Model model)
	{
		model.addAttribute("title", "Forgot Password");
		return "forgot_password";
	}
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, Model model, HttpSession currentSession)
	{
		int otp = random.nextInt(99999);	
		String subject = "OTP Email from Contact Manager";
		String content = "OTP to Reset Password is: " + otp;
		try
		{
			boolean emailSent = emailservice.sendEmailWithNoAttachment(subject, content, email);
			
			if(emailSent)
			{
				currentSession.setAttribute("generatedOtp", otp);
				currentSession.setAttribute("submittedEmail", email);
				model.addAttribute("title", "Verify OTP");
				return "verify_otp";
			}

		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		currentSession.setAttribute("message", new Message("Something Went Wrong!", "alert-danger"));
		model.addAttribute("title", "Forgot Password");
		return "forgot_password";
		
		
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("submittedOtp") int submittedOtp, HttpSession currentSession, Model model)
	{
		int generatedOtp = (int) currentSession.getAttribute("generatedOtp");
		
		if(generatedOtp == submittedOtp)
		{
			String submittedEmail = (String) currentSession.getAttribute("submittedEmail");
			User fetchedUser = userRepoObj.getUserByUserName(submittedEmail);
			
			if(fetchedUser == null)
			{
				currentSession.setAttribute("message", new Message("Email Not Registered!", "alert-danger"));
				model.addAttribute("title", "Forgot Password");
				return "forgot_password";
			}
			
			model.addAttribute("title", "Reset Password");
			return "reset_password";
		}
		else
		{
			currentSession.setAttribute("message", new Message("Incorrect OTP! Try Again", "alert-danger"));
			model.addAttribute("title", "Verify OTP");
			return "verify_otp";
		}
	}
	
	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword, HttpSession currentSession, Model model)
	{
		if(newPassword.isEmpty() || confirmPassword.isEmpty())
		{
			currentSession.setAttribute("message", new Message("You can't leave the fields blank", "alert-danger"));
			model.addAttribute("title", "Reset Password");
			return "reset_password";
		}
		
		if(newPassword.length() < 5)
		{
			
			currentSession.setAttribute("message", new Message("Enter minimum 5 characters", "alert-danger"));
			model.addAttribute("title", "Reset Password");
			return "reset_password";
		}
		
		if(newPassword.equals(confirmPassword))
		{
			String submittedEmail = (String) currentSession.getAttribute("submittedEmail");
			User fetchedUser = userRepoObj.getUserByUserName(submittedEmail);
			fetchedUser.setPassword(passwordEncoder.encode(newPassword));
			userRepoObj.save(fetchedUser);
			
			currentSession.setAttribute("message", new Message("Password Changed", "alert-success"));
			model.addAttribute("title", "Login");
			return "login";		
		}
		else
		{
			currentSession.setAttribute("message", new Message("Different Password!", "alert-danger"));
			model.addAttribute("title", "Reset Password");
			return "reset_password";
		}
	}
}
