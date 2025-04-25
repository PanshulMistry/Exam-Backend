package com.testproject.test.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.testproject.test.exception.UserObjNotFoundException;
import com.testproject.test.exception.UsernameAlreadyExists;
import com.testproject.test.proxy.AdminUserProxy;
import com.testproject.test.proxy.LoginRequest;
import com.testproject.test.proxy.LoginResponse;
import com.testproject.test.repository.TestRepo;
import com.testproject.test.service.TestService;
import com.testproject.test.utils.JwtUtils;

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
		adminUser.setAddress(f.address().fullAddress());
		adminUser.setUsername(f.name().username());
		adminUser.setEmail(f.internet().emailAddress());
		String password = f.internet().password();
		System.out.println("username"+adminUser.getName()+" password:"+password);
		adminUser.setPassword(pass.encode(password));
		String genderStr = f.demographic().sex();
		System.err.println("Gender:"+genderStr);// Returns "Male" or "Female"
		if (genderStr.equalsIgnoreCase("Male")) {
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
	       
	        AdminUserProxy proxy = new AdminUserProxy();

	        
	        proxy.setId(entity.getId());
	        proxy.setName(entity.getName());
	        proxy.setDob(entity.getDob());
	        proxy.setUsername(entity.getUsername());
	        proxy.setPassword(entity.getPassword());
	        proxy.setGender(entity.getGender());
	        proxy.setAddress(entity.getAddress());
	        proxy.setEmail(entity.getEmail());
	        proxy.setContactNumber(entity.getContactNumber());
	        proxy.setPinCode(entity.getPinCode());
	        proxy.setAccessRole(entity.getAccessRole());

//	      System.err.println(proxy);
	        if (entity.getProfileImage() != null && !entity.getProfileImage().isEmpty()) {
	            try {
	                String fileName = entity.getProfileImage();
	                String originalClassPath = new ClassPathResource("").getFile().getAbsolutePath();
	                String urlPath = originalClassPath + File.separator + "static" + File.separator + "document";
	                String absolutePath = urlPath + File.separator + fileName;

	               
	                byte[] imageBytes = Files.readAllBytes(new File(absolutePath).toPath());
	                proxy.setProfileImage(imageBytes);

	            } catch (IOException e) {
	               
	                System.err.println("Failed to load image: " + entity.getProfileImage());
	                e.printStackTrace();
	            }
	        }

	        return proxy;
	    });
	}
	
	@Override
	public Page<AdminUserProxy> searchUsers(String keyword, Pageable pageable) {
	    Page<AdminUser> entities = testRepo.searchByKeywordAndRole(keyword, Role.USER, pageable);

	    return entities.map(entity -> {
	        AdminUserProxy proxy = new AdminUserProxy();

	        proxy.setId(entity.getId());
	        proxy.setName(entity.getName());
	        proxy.setDob(entity.getDob());
	        proxy.setUsername(entity.getUsername());
	        proxy.setPassword(entity.getPassword());
	        proxy.setGender(entity.getGender());
	        proxy.setAddress(entity.getAddress());
	        proxy.setEmail(entity.getEmail());
	        proxy.setContactNumber(entity.getContactNumber());
	        proxy.setPinCode(entity.getPinCode());
	        proxy.setAccessRole(entity.getAccessRole());

	        if (entity.getProfileImage() != null && !entity.getProfileImage().isEmpty()) {
	            try {
	                String fileName = entity.getProfileImage();
	                String originalClassPath = new ClassPathResource("").getFile().getAbsolutePath();
	                String urlPath = originalClassPath + File.separator + "static" + File.separator + "document";
	                String absolutePath = urlPath + File.separator + fileName;

	                byte[] imageBytes = Files.readAllBytes(new File(absolutePath).toPath());
	                proxy.setProfileImage(imageBytes);

	            } catch (IOException e) {
	                System.err.println("Failed to load image: " + entity.getProfileImage());
	                e.printStackTrace();
	            }
	        }

	        return proxy;
	    });
	}



	@Override
	public AdminUserProxy getUserDetails(String email) {
	    Optional<AdminUser> op = testRepo.findByEmail(email);
	    if (op.isPresent()) {
	        AdminUser adminUser = op.get();
	        
	        AdminUserProxy adminUserProxy = new AdminUserProxy();
	        
	       
	        adminUserProxy.setId(adminUser.getId());
	        adminUserProxy.setName(adminUser.getName());
	        adminUserProxy.setDob(adminUser.getDob());
	        adminUserProxy.setUsername(adminUser.getUsername());
	        adminUserProxy.setPassword(adminUser.getPassword());
	        adminUserProxy.setGender(adminUser.getGender());
	        adminUserProxy.setAddress(adminUser.getAddress());
	        adminUserProxy.setEmail(adminUser.getEmail());
	        adminUserProxy.setContactNumber(adminUser.getContactNumber());
	        adminUserProxy.setPinCode(adminUser.getPinCode());
	        adminUserProxy.setAccessRole(adminUser.getAccessRole());
	        
	        
	        
	        if (adminUser.getProfileImage() != null && !adminUser.getProfileImage().isEmpty()) {
	            try {
	                String fileName = adminUser.getProfileImage();
	                String originalClassPath = new ClassPathResource("").getFile().getAbsolutePath();
	                String urlPath = originalClassPath + File.separator + "static" + File.separator + "document";
	                String absolutePath = urlPath + File.separator + fileName;
	                
	                byte[] imageBytes = Files.readAllBytes(new File(absolutePath).toPath());
	                adminUserProxy.setProfileImage(imageBytes);
	                
	            } catch (IOException e) {
	               
	                System.err.println("Failed to load image: " + adminUser.getProfileImage());
	                e.printStackTrace();
	            }
	        }
	        System.err.println(adminUser);
	        System.err.println(adminUserProxy);
	        
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
	    adminUser.setGender(adminUserProxy.getGender());
	    adminUser.setEmail(adminUserProxy.getEmail());
	    adminUser.setAddress(adminUserProxy.getAddress());
	    adminUser.setContactNumber(adminUserProxy.getContactNumber());
	    adminUser.setPinCode(adminUserProxy.getPinCode());
	    adminUser.setAccessRole(Role.USER);

	    
	    if (adminUserProxy.getPassword() != null && !adminUserProxy.getPassword().isEmpty()) {
	        
	        if (!adminUserProxy.getPassword().startsWith("$2a$") && !adminUserProxy.getPassword().startsWith("$2b$")) {
	            adminUser.setPassword(pass.encode(adminUserProxy.getPassword()));
	        } else {
	           
	            adminUser.setPassword(adminUserProxy.getPassword());
	        }
	    }

	   
	    if (file != null && !file.isEmpty()) {
	        Integer dotIndex = file.getOriginalFilename().indexOf(".");
	        String uniqueFileId = UUID.randomUUID().toString() + file.getOriginalFilename().substring(dotIndex);
	        adminUser.setProfileImage(uniqueFileId);

	        try {
	            String originalClassPath = new ClassPathResource("").getFile().getAbsolutePath();
	            String urlPath = originalClassPath + "\\static\\document";

	            File f = new File(urlPath);
	            if (!f.exists()) {
	                f.mkdirs(); 
	            }

	            String absolutePath = urlPath + File.separator + uniqueFileId;
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
			throw new UserObjNotFoundException(); 
		}

		testRepo.delete(op.get());
		return "User deleted successfully.";
	}

	@Override
	public LoginResponse login(LoginRequest logReq) {
	    Authentication auth = new UsernamePasswordAuthenticationToken(logReq.getUsername(), logReq.getPassword());
	    Authentication verfAuth = authManager.authenticate(auth);

	    if (verfAuth.isAuthenticated()) {
	    
	        
	        String role = verfAuth.getAuthorities()
	                              .stream()
	                              .findFirst()
	                              .map(grantedAuthority -> grantedAuthority.getAuthority())  
	                              .orElse("USER"); 
	        Optional<AdminUser> userOptional = testRepo.findByUsername(logReq.getUsername());
	        String email = userOptional.get().getEmail();
	        return new LoginResponse(
	        	role,
	            jwtUtils.generateToken(logReq.getUsername()),
	            		email);
	    }

	    return new LoginResponse("NO ROLE","Request Failed","No Email");
	}
	
	public List<AdminUserProxy> getUsers() {
	   
	    List<AdminUser> entities = testRepo.findByAccessRole(Role.USER);
	    List<AdminUserProxy> proxies = new ArrayList<>();

	    for (AdminUser entity : entities) {
	        
	        AdminUserProxy proxy = new AdminUserProxy();
	        
	       
	        proxy.setId(entity.getId());
	        proxy.setName(entity.getName());
	        proxy.setDob(entity.getDob());
	        proxy.setUsername(entity.getUsername());
	        proxy.setPassword(entity.getPassword());
	        proxy.setGender(entity.getGender());
	        proxy.setAddress(entity.getAddress());
	        proxy.setEmail(entity.getEmail());
	        proxy.setContactNumber(entity.getContactNumber());
	        proxy.setPinCode(entity.getPinCode());
	        proxy.setAccessRole(entity.getAccessRole());
	        
	        if (entity.getProfileImage() != null && !entity.getProfileImage().isEmpty()) {
	            try {
	                String fileName = entity.getProfileImage();
	                String originalClassPath = new ClassPathResource("").getFile().getAbsolutePath();
	                String urlPath = originalClassPath + File.separator + "static" + File.separator + "document";
	                String absolutePath = urlPath + File.separator + fileName;
	                
	               
	                byte[] imageBytes = Files.readAllBytes(new File(absolutePath).toPath());
	                proxy.setProfileImage(imageBytes);
	                
	            } catch (IOException e) {
	               
	                System.err.println("Failed to load image: " + entity.getProfileImage());
	                e.printStackTrace();
	            }
	        }
	        
	        proxies.add(proxy);
	    }
	    
	    return proxies;
	}

	@Override
	public List<AdminUser> getAllUserList() {
		// TODO Auto-generated method stub
		
		return testRepo.findByAccessRole(Role.USER);
	}
}
