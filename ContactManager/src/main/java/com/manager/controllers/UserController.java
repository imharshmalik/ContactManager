package com.manager.controllers;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.manager.entity.Contact;
import com.manager.entity.User;
import com.manager.helper.Message;
import com.manager.repository.ContactRepository;
import com.manager.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController
{
	@Autowired
	private UserRepository userRepoObj;
	
	@Autowired
	private ContactRepository contactRepoObj;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@ModelAttribute
	public void fetchData(Model model, Principal principal)
	{
		String username = principal.getName();
		User userLoggedIn = userRepoObj.getUserByUserName(username);
		model.addAttribute("currentUser", userLoggedIn);
	}

	
	@RequestMapping("/dashboard")
	public String dashboard(Model model, Principal principal)
	{
		String username = principal.getName();
		User userLoggedIn = userRepoObj.getUserByUserName(username);
		model.addAttribute("currentUser", userLoggedIn);
		model.addAttribute("title", "Dashboard - " + userLoggedIn.getName());
		return "user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String addContactForm(Model model)
	{
		model.addAttribute("title", "Add New Contact");
		model.addAttribute("contact", new Contact());
		return "user_add_contact";
	}
	
	@PostMapping("/handleNewContact")
	public String handleNewContact(@Valid @ModelAttribute("contact") Contact newContact, BindingResult result, @RequestParam("picture") MultipartFile imageFile, Principal principal, Model model, HttpSession session)
	{
		try
		{
			String username = principal.getName();
			User userLoggedIn = userRepoObj.getUserByUserName(username);
			
			if(result.hasErrors())
			{
				model.addAttribute("contact", newContact);
				System.out.println("\nErrors: " + result.toString());
				model.addAttribute("title", "Adding New Contact");
				return "user_add_contact";
			}
			
			if(imageFile.isEmpty())
			{
				newContact.setImage("default-profile-pic.jpg");
			}
			else
			{			
				newContact.setImage(imageFile.getOriginalFilename());
				File savedFile = new ClassPathResource("static/images").getFile();
				Path targetPath = Paths.get(savedFile.getAbsolutePath() + File.separator + imageFile.getOriginalFilename());
				Files.copy(imageFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			}
			
			userLoggedIn.addContact(newContact);
			newContact.setUser(userLoggedIn);
			
			userRepoObj.save(userLoggedIn);	
			
			session.setAttribute("message", new Message("New Contact Added", "alert-success"));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
			session.setAttribute("message", new Message("Something Went Wrong", "alert-danger"));
		}
		model.addAttribute("title", "New Contact Added");
		return "user_add_contact";
	}
	
	@GetMapping("/show-contacts/{page}")
	public String viewAllContacts(@PathVariable("page") Integer page, Model model, Principal principal)
	{
		String username = principal.getName();
		User userLoggedIn = userRepoObj.getUserByUserName(username);
		//List<Contact> allContacts = userLoggedIn.getContacts();
		//List<Contact> allContacts = contactRepoObj.findContactByUser(userLoggedIn.getId());
		
		Pageable pageable = PageRequest.of(page-1, 5);	//  page-1 because we want to start indexing from 1 in the URK path
		Page<Contact> allContacts = contactRepoObj.findContactByUser(userLoggedIn.getId(), pageable);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", allContacts.getTotalPages());		
		
		model.addAttribute("contacts", allContacts);
		model.addAttribute("currentUser", userLoggedIn);		
		model.addAttribute("title", "My Contacts");
		return "user_show_contacts";
	}
	
	@GetMapping("/contact/{contact_id}")
	public String contactFullDetails(@PathVariable("contact_id") Integer contactId, Model model, Principal principal)
	{
		try
		{
			Optional<Contact> optional = contactRepoObj.findById(contactId);
			Contact contactFethed = optional.get();
			
			String username = principal.getName();
			User userLoggedIn = userRepoObj.getUserByUserName(username);
			
			if(userLoggedIn.getId() == contactFethed.getUser().getId())
			{
				model.addAttribute("contact", contactFethed);
			}
		} 
		catch (Exception e)
		{
			model.addAttribute("contact", null);
		}
				
		model.addAttribute("title", "Contact Details");
		return "user_contact_details";
	}
	
	@GetMapping("/delete-contact/{contact_id}")
	public String deleteContact(@PathVariable("contact_id") Integer contactId, Model model, Principal principal, HttpSession session)
	{
		try
		{
			Optional<Contact> optional = contactRepoObj.findById(contactId);
			Contact contactFethed = optional.get();
			
			String username = principal.getName();
			User userLoggedIn = userRepoObj.getUserByUserName(username);
			
			if(userLoggedIn.getId() == contactFethed.getUser().getId())
			{
				contactRepoObj.delete(contactFethed);
				session.setAttribute("message", new Message("Contact Deleted", "alert-success"));
			}
		} 
		catch (Exception e)
		{
			model.addAttribute("contact", null);
			return "user_contact_details";
		}
				
		model.addAttribute("title", "Contact Details");
		return "redirect:/user/show-contacts/1";
	}
	
	@PostMapping("/update-contact/{contact_id}")
	public String updateContact(@PathVariable("contact_id") Integer contactId, Model model, Principal principal, HttpSession session)
	{
		try
		{
			Optional<Contact> optional = contactRepoObj.findById(contactId);
			Contact contactFethed = optional.get();
			
			String username = principal.getName();
			User userLoggedIn = userRepoObj.getUserByUserName(username);
			
			if(userLoggedIn.getId() == contactFethed.getUser().getId())
			{
				model.addAttribute("contact", contactFethed);
			}
		} 
		catch (Exception e)
		{
			model.addAttribute("contact", null);
			return "user_contact_details";
		}
				
		model.addAttribute("title", "Update Contact");
		return "user_update_contact";
	}
	
	@PostMapping("/handleUpdateContact")
	public String handleUpdatContact(@ModelAttribute Contact updatedContact, @RequestParam("picture") MultipartFile newImage, Principal principal, Model model, HttpSession session)
	{
		String username = principal.getName();
		User userLoggedIn = userRepoObj.getUserByUserName(username);
		
		try
		{			
			Optional<Contact> optional = contactRepoObj.findById(updatedContact.getId());
			Contact oldContact = optional.get();
			
			if(newImage.isEmpty())
			{
				updatedContact.setImage(oldContact.getImage());
			}
			else
			{				
				File oldFile = new ClassPathResource("static/images").getFile();
				File fileToDelete = new File(oldFile, oldContact.getImage());
				fileToDelete.delete();
				
				Path targetPath = Paths.get(oldFile.getAbsolutePath() + File.separator + newImage.getOriginalFilename());
				Files.copy(newImage.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
				updatedContact.setImage(newImage.getOriginalFilename());
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		updatedContact.setUser(userLoggedIn);
		contactRepoObj.save(updatedContact);
		
		session.setAttribute("message", new Message("Contact Updated", "alert-success"));
		model.addAttribute("contact", updatedContact);
		model.addAttribute("title", "Contact Details");
		return "redirect:/user/contact/" + updatedContact.getId();
	}
	
	@GetMapping("/profile")
	public String showProfile(Model model, Principal principal)
	{
		String username = principal.getName();
		User userLoggedIn = userRepoObj.getUserByUserName(username);
		model.addAttribute("currentUser", userLoggedIn);
		model.addAttribute("title", "My Profile");
		return "user_profile";
	}
	
	@GetMapping("/change-password")
	public String changePassword(Model model)
	{
		
		model.addAttribute("title", "Change Password");
		return "user_change_password";
	}
	
	@PostMapping("/changePasswordHandler")
	public String changePasswordHandler(@RequestParam("old-password") String oldPassword, @RequestParam("new-password") String newPassword, Principal principal, Model model, HttpSession session)
	{
		String username = principal.getName();
		User userLoggedIn = userRepoObj.getUserByUserName(username);
		
		if(oldPassword.isEmpty() || newPassword.isEmpty())
		{
			session.setAttribute("message", new Message("You can't leave the fields blank", "alert-danger"));
			model.addAttribute("title", "Change Password");
			return "user_change_password";
		}
		
		if(newPassword.length() < 5)
		{
			session.setAttribute("message", new Message("Password should have minimum 5 characters", "alert-danger"));
			model.addAttribute("title", "Change Password");
			return "user_change_password";
		}
		
		if(oldPassword.equals(newPassword))
		{
			session.setAttribute("message", new Message("New Password cannot be the same as Old Password", "alert-danger"));
			model.addAttribute("title", "Change Password");
			return "user_change_password";
		}
		
		if(passwordEncoder.matches(oldPassword, userLoggedIn.getPassword()))
		{
			userLoggedIn.setPassword(passwordEncoder.encode(newPassword));
			userRepoObj.save(userLoggedIn);
			
			session.setAttribute("message", new Message("Password Changed", "alert-success"));
		}
		else
		{
			session.setAttribute("message", new Message("Wrong Password!", "alert-danger"));
		}
		
		model.addAttribute("title", "Change Password");
		return "user_change_password";
	}
}
