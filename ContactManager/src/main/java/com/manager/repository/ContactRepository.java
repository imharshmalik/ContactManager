package com.manager.repository;


import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.manager.entity.Contact;
import com.manager.entity.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>
{
	@Query("FROM Contact c WHERE c.user.id = :userID")
	//public List<Contact> findContactByUser(@Param("userID") int userID);
	public Page<Contact>findContactByUser(@Param("userID") int userID, Pageable pageable);
	// PAGE IS A SUBLIST OF A LIST OF AN OBJECT
	
	
	public List<Contact> findByNameContainingAndUser(String searchKeyword, User User);
}
