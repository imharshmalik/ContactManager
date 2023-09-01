package com.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.manager.entity.User;
import com.manager.helper.Message;
import com.manager.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController
{
	
	@Autowired
	private UserRepository repoObj;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@RequestMapping("/")
	public String home(Model m)
	{
		m.addAttribute("title", "Home");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("title", "About");
		return "about";
	}
	
	@RequestMapping("/login")
	public String login(Model m)
	{
		m.addAttribute("title", "Login");
		return "login";
	}
	
	
	@RequestMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("title", "Sign Up");
		m.addAttribute("user", new User());
		return "signup";
	}
	
	@RequestMapping("/terms")
	public String termsAndConditions(Model m)
	{
		m.addAttribute("title", "Terms & Conditions");
		return "terms";
	}
	
	@PostMapping("/handleSignup")
	public String handleSignup(@Valid @ModelAttribute("user") User newUser, BindingResult result, @RequestParam(value="checkbox", defaultValue="false") boolean checkbox, Model m, HttpSession session)
	{
		try
		{
			if(!checkbox)
			{
				System.out.println("Checkbox is not checked!!");
				throw new Exception("Checkbox is not checked!!");
			}
			
			if(result.hasErrors())
			{
				m.addAttribute("user", newUser);
				System.out.println("\nErrors: " + result.toString());
				return "signup";
			}
			
			newUser.setRole("ROLE_USER");
			newUser.setEnabled(true);
			newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
			
			User savedUser = repoObj.save(newUser);
			
			System.out.println("\nUser saved:" + savedUser);
			m.addAttribute("user", savedUser);
			session.setAttribute("message", new Message("New User Registered!", "alert-success"));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			m.addAttribute("user", newUser);
			m.addAttribute("checkbox", checkbox);
			//session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
		}
		
		return "signup";
	}
}
