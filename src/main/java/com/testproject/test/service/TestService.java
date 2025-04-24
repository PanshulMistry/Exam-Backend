package com.testproject.test.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.testproject.test.domain.AdminUser;
import com.testproject.test.proxy.AdminUserProxy;
import com.testproject.test.proxy.LoginRequest;
import com.testproject.test.proxy.LoginResponse;

public interface TestService {
	public String saveUser(AdminUser adminUser);
	public String saveBulkUsers(Integer size);
	public List<AdminUserProxy> getUsers();
	public AdminUserProxy getUserDetails(String email);
	public String updateUserDetails(AdminUserProxy adminUserProxy,MultipartFile file);
	public String deleteUserByEmail(String email);
	public LoginResponse login(LoginRequest logReq);
	public org.springframework.data.domain.Page<AdminUserProxy> getAllUsers(org.springframework.data.domain.Pageable pageable);
	public org.springframework.data.domain.Page<AdminUserProxy> searchUsers(String keyword, Pageable pageable);
}
