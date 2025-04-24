package com.testproject.test.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.testproject.test.domain.AdminUser;
import com.testproject.test.exception.UserObjNotFoundException;
import com.testproject.test.repository.TestRepo;

@Service
public class AuthUserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private TestRepo repo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<AdminUser> op =repo.findByUsername(username);
		if(!op.isPresent()) throw new UserObjNotFoundException();
		
		AdminUser user = repo.findByUsername(username).get();
		
		System.err.println("LOAD BY USERNAME");
		
		UserDetails userDetail = new UserDetails() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getUsername() {
				// TODO Auto-generated method stub
				return user.getUsername();
			}
			
			@Override
			public String getPassword() {
				// TODO Auto-generated method stub
				return user.getPassword();
			}
			
			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				
				// TODO Auto-generated method stub
				System.out.println(user.getAccessRole());
				return List.of(new SimpleGrantedAuthority(user.getAccessRole().toString()));
			}
			
			public String getRole() {
				// TODO Auto-generated method stub
				return user.getAccessRole().toString();
			}
			

		};
		// TODO Auto-generated method stub
		return userDetail;
	}

}
