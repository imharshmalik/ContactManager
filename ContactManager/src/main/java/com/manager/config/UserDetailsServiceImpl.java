package com.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.manager.entity.User;
import com.manager.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService
{
	@Autowired
	private UserRepository repoObj;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		User userFetched = repoObj.getUserByUserName(username);
		
		if(userFetched == null)
		{
			throw new UsernameNotFoundException("No User Found!");
		}
		
		UserDetailsImpl userDetails = new UserDetailsImpl(userFetched) ;
		return userDetails;
	}

}
