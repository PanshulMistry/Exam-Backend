package com.testproject.test.service;

import com.testproject.test.proxy.ResetPasswordRequest;

public interface ResetPasswordService {
	
	public String getToken(String email);
	public Boolean resetPassword(ResetPasswordRequest request);
	
}
