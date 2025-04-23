package com.testproject.test.service.impl;

import java.awt.print.Pageable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.javafaker.Faker;
import com.testproject.test.domain.AdminUser;
import com.testproject.test.enums.ErrorMessageEnum;
import com.testproject.test.enums.Gender;
import com.testproject.test.enums.Role;
import com.testproject.test.exception.EmailAlreadyExistsException;
import com.testproject.test.exception.UserListEmptyException;
import com.testproject.test.exception.UserObjNotFoundException;
import com.testproject.test.exception.UsernameAlreadyExists;
import com.testproject.test.proxy.AdminUserProxy;
import com.testproject.test.proxy.LoginRequest;
import com.testproject.test.proxy.LoginResponse;
import com.testproject.test.repository.TestRepo;
import com.testproject.test.service.TestService;
import com.testproject.test.utils.JwtUtils;
import com.testproject.test.utils.MapperUtils;

@Service
public class TestServiceImpl implements TestService {
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private BCryptPasswordEncoder pass;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private TestRepo testRepo;

	private Boolean isAdmin = false;

	@Override
	public String saveUser(AdminUser adminUser) {
		// TODO Auto-generated method stub
//		AdminUser adminUser = MapperUtils.convertValue(adminUserProxy, AdminUser.class);
		Optional<AdminUser> op = testRepo.findByEmail(adminUser.getEmail());
		if (op.isPresent()) {
			throw new EmailAlreadyExistsException(ErrorMessageEnum.EMAIL_ALREADY_EXISTS.getErrMessage(),
					ErrorMessageEnum.EMAIL_ALREADY_EXISTS.getErrCode());
		}
		Optional<AdminUser> opUsername = testRepo.findByUsername(adminUser.getUsername());
		if (opUsername.isPresent()) {
			throw new UsernameAlreadyExists(ErrorMessageEnum.USERNAME_ALREADY_EXISTS.getErrMessage(),
					ErrorMessageEnum.USERNAME_ALREADY_EXISTS.getErrCode());
		}
		adminUser.setAccessRole(Role.USER);
		adminUser.setPassword(pass.encode(adminUser.getPassword()));
		testRepo.save(adminUser);
		return "User Saved Succesfully";
	}

	@Override
	public String saveBulkUsers(Integer size) {
		// TODO Auto-generated method stub
		for (int i = 1; i <= size; i++) {
			if (i == 1) {
				isAdmin = true;
			} else {
				isAdmin = false;
			}
			testRepo.save(generateBulkUsers(isAdmin));
		}
		return size + " users created.";
	}

	private AdminUser generateBulkUsers(Boolean isAdmin) {
		Faker f = new Faker();
		AdminUser adminUser = new AdminUser();
		adminUser.setName(f.name().fullName());
		adminUser.setContactNumber(Long.parseLong(f.number().digits(10)));
		long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
	    long maxDay = LocalDate.of(2015, 12, 31).toEpochDay();
	    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
	    LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
		adminUser.setDob(java.sql.Date.valueOf(randomDate));
		adminUser.setAddress(f.address().toString());
		adminUser.setUsername(f.name().username());
		adminUser.setEmail(f.internet().emailAddress());
		String password = f.internet().password();
		System.out.println("username"+adminUser.getName()+" password:"+password);
		adminUser.setPassword(pass.encode(password));
		String genderStr = f.demographic().sex(); // Returns "Male" or "Female"
		if (genderStr == "Male") {
			adminUser.setGender(Gender.MALE);
		} else {
			adminUser.setGender(Gender.FEMALE);
		}
		adminUser.setProfileImage("");
		adminUser.setPinCode(f.address().zipCode());
		adminUser.setAccessRole(isAdmin ? Role.ADMIN : Role.USER);
		return adminUser;
	}

	@Override
	public org.springframework.data.domain.Page<AdminUserProxy> getAllUsers(org.springframework.data.domain.Pageable pageable) {
	    org.springframework.data.domain.Page<AdminUser> entities = testRepo.findByAccessRole(Role.USER, pageable);
	    
	    return entities.map(entity -> {
	        AdminUserProxy proxy = MapperUtils.convertValue(entity, AdminUserProxy.class);

	        // Set profileImage to null to avoid byte[] issue
	        proxy.setProfileImage(null);

	        return proxy;
	    });
	}


	@Override
	public AdminUserProxy getUserDetails(String email) {
		// TODO Auto-generated method stub
		Optional<AdminUser> op = testRepo.findByEmail(email);
		if (op.isPresent()) {
//			AdminUser adminUser = op.get();
//			String fileName = adminUser.getProfileImage();
//			AdminUserProxy adminUserProxy = MapperUtils.convertValue(op.get(), AdminUserProxy.class);
//			try {
//				String originalClassPath = new ClassPathResource("").getFile().getAbsolutePath();
//				String urlPath = originalClassPath + "\\static\\document";
//				String absolutePath = urlPath + File.separator + fileName;
//				System.out.println("ABSPATH:" + absolutePath);
//				byte[] allBytes = Files.readAllBytes(new File(absolutePath).toPath());
//				adminUserProxy.setProfileImage(allBytes);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			 AdminUser adminUser = op.get();

		        // Convert entity to proxy
		        AdminUserProxy adminUserProxy = MapperUtils.convertValue(adminUser, AdminUserProxy.class);

		        // Always set profile image byte array to null
		        adminUserProxy.setProfileImage(null);

		        return adminUserProxy;
		}
		throw new UserObjNotFoundException();
	}

	@Override
	public String updateUserDetails(AdminUserProxy adminUserProxy, MultipartFile file) {
		Optional<AdminUser> op = testRepo.findByEmail(adminUserProxy.getEmail());
		if (!op.isPresent()) {
			throw new UserObjNotFoundException();
		}

		AdminUser adminUser = op.get();

		adminUser.setName(adminUserProxy.getName());
		adminUser.setDob(adminUserProxy.getDob());
		adminUser.setUsername(adminUserProxy.getUsername());
		adminUser.setPassword(adminUserProxy.getPassword());
		adminUser.setGender(adminUserProxy.getGender());
		adminUser.setEmail(adminUserProxy.getEmail());
		adminUser.setAddress(adminUserProxy.getAddress());
		adminUser.setContactNumber(adminUserProxy.getContactNumber());
		adminUser.setPinCode(adminUserProxy.getPinCode());
		adminUser.setAccessRole(Role.USER);

		// Handle profile image update if file is provided
		if (file != null && !file.isEmpty()) {
			Integer dotIndex = file.getOriginalFilename().indexOf(".");
			String uniqueFileId = UUID.randomUUID().toString() + file.getOriginalFilename().substring(dotIndex);
			adminUser.setProfileImage(uniqueFileId);

			String originalClassPath = null;
			try {
				originalClassPath = new ClassPathResource("").getFile().getAbsolutePath();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String urlPath = originalClassPath + "\\static\\document";

			File f = new File(urlPath);
			if (!f.exists()) {
				f.mkdir();
			}

			String absolutePath = urlPath + File.separator + uniqueFileId;
			try {
				Files.copy(file.getInputStream(), Paths.get(absolutePath), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		testRepo.save(adminUser);
		return "User details updated successfully.";
	}

	@Override
	public String deleteUserByEmail(String email) {
		Optional<AdminUser> op = testRepo.findByEmail(email);
		if (!op.isPresent()) {
			throw new UserObjNotFoundException(); // Custom exception when user not found
		}

		testRepo.delete(op.get());
		return "User deleted successfully.";
	}

	@Override
	public LoginResponse login(LoginRequest logReq) {
	    Authentication auth = new UsernamePasswordAuthenticationToken(logReq.getUsername(), logReq.getPassword());
	    Authentication verfAuth = authManager.authenticate(auth);

	    if (verfAuth.isAuthenticated()) {
	        // Extract single role from authorities without adding "ROLE_" prefix
	        String role = verfAuth.getAuthorities()
	                              .stream()
	                              .findFirst()
	                              .map(grantedAuthority -> grantedAuthority.getAuthority())  // Use the role directly (no prefix)
	                              .orElse("USER");  // Default to "USER" if no role found

	        return new LoginResponse(
	        	role,
	            jwtUtils.generateToken(logReq.getUsername())
	              // Include the extracted role
	        );
	    }

	    return new LoginResponse("NO ROLE","Request Failed");
	}


	


}
