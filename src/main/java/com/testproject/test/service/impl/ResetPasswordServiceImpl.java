package com.testproject.test.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.testproject.test.domain.AdminUser;
import com.testproject.test.exception.UserObjNotFoundException;
import com.testproject.test.proxy.ResetPasswordRequest;
import com.testproject.test.proxy.ResetPasswordToken;
import com.testproject.test.repository.ResetPasswordTokenRepository;
import com.testproject.test.repository.TestRepo;
import com.testproject.test.service.ResetPasswordService;


@Service
public class ResetPasswordServiceImpl implements ResetPasswordService{
	
	@Autowired
	private ResetPasswordTokenRepository token_db;
	
	@Autowired
	private TestRepo repo;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Override
	public String getToken(String email) {
		if(repo.findByEmail(email).isEmpty()) throw new UserObjNotFoundException();
		
		ResetPasswordToken newToken = new ResetPasswordToken();
		newToken.setEmail(email);
		newToken.setExpirationTime(LocalDateTime.now().plusMinutes(10));
		newToken = token_db.save(newToken);
		
		System.err.println(newToken);
		
		return "Password reset link sent to the registered email address.";
	}

	@Override
	public Boolean resetPassword(ResetPasswordRequest request) {
		
		if(!token_db.existsById(request.getToken())) return false;
		
		ResetPasswordToken token = token_db.findById(request.getToken()).get();
		
		if(token_db.findById(request.getToken()).get().getExpirationTime()
		.isBefore(LocalDateTime.now()) || repo.findByEmail(token.getEmail()).isEmpty())
		{
			token_db.delete(token);
			return false;
		}
		
		AdminUser user = repo.findByEmail(token.getEmail()).get();
		user.setPassword(encoder.encode(request.getNewPassword()));
		repo.save(user);
		
		token_db.delete(token); 
		
		return true;
	}

}
