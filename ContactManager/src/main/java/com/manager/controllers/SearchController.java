package com.manager.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.manager.entity.Contact;
import com.manager.entity.User;
import com.manager.repository.ContactRepository;
import com.manager.repository.UserRepository;

@RestController
public class SearchController
{
	@Autowired
	private UserRepository userRepoObj;
	
	@Autowired
	private ContactRepository contactRepoObj;
	
	@GetMapping("/search/{search-keyword}")
	public ResponseEntity<?> searchHandler(@PathVariable("search-keyword") String searchKeyword, Principal principal)
	{
		String username = principal.getName();
		User userLoggedIn = userRepoObj.getUserByUserName(username);
		
		List<Contact> searchResult = contactRepoObj.findByNameContainingAndUser(searchKeyword, userLoggedIn);
		
		return ResponseEntity.ok(searchResult);
	}
}
