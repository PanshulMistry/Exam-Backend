package com.testproject.test.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.testproject.test.domain.AdminUser;
import com.testproject.test.proxy.AdminUserProxy;
import com.testproject.test.proxy.LoginRequest;
import com.testproject.test.proxy.LoginResponse;
import com.testproject.test.proxy.ResetPasswordRequest;
import com.testproject.test.service.ResetPasswordService;
import com.testproject.test.service.TestService;
import com.testproject.test.utils.MapperUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")  // Angular frontend URL
public class TestController {

	@Autowired
	private TestService testService;
	
	@Autowired
	private ResetPasswordService resetPasswordService;

	@GetMapping("/save-bulk-users/{size}")
	public ResponseEntity<String> saveBulkUsers(@PathVariable("size") Integer size) {
		return new ResponseEntity<String>(testService.saveBulkUsers(size), HttpStatus.OK);
	}
	
	@GetMapping("/getUserDetails/{email}")
    public ResponseEntity<AdminUserProxy> getUserDetails(@PathVariable String email) {
        AdminUserProxy user = testService.getUserDetails(email);
        return ResponseEntity.ok(user);
    }
	
	@PostMapping("/update-user")
	public ResponseEntity<String> updateUserDetails(
	        @RequestPart("adminUserProxy") AdminUserProxy adminUserProxy,
	        @RequestPart(value = "file", required = false) MultipartFile file) {
	    String response = testService.updateUserDetails(adminUserProxy, file);
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/getAllUsers")
	public Page<AdminUserProxy> getAllUsers(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "id") String sortBy,
	        @RequestParam(defaultValue = "asc") String sortDir) {

	    Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
	    Pageable pageable = PageRequest.of(page, size, sort);
	    return testService.getAllUsers(pageable);
	}

	


	@PostMapping("/updateUser")
	public ResponseEntity<String> updateUser(@RequestParam("profilePhoto") MultipartFile file,
			@RequestPart("user") @Valid AdminUserProxy adminUserProxy) {
		return new ResponseEntity<String>(testService.updateUserDetails(adminUserProxy,file),HttpStatus.OK);
	}

	@DeleteMapping("/deleteUser")
	public ResponseEntity<String> deleteUser(@RequestParam("email") String email) {
	    return new ResponseEntity<>(testService.deleteUserByEmail(email), HttpStatus.OK);
	}

	@PostMapping("/saveUser")
	public ResponseEntity<String> saveUser(@RequestParam("profilePhoto") MultipartFile file,
			@RequestPart("user") @Valid AdminUserProxy adminUserProxy) {
		AdminUser adminUser = MapperUtils.convertValue(adminUserProxy, AdminUser.class);
		Integer dotIndex = file.getOriginalFilename().indexOf(".");
		String uniqueFileId = UUID.randomUUID().toString() + file.getOriginalFilename().substring(dotIndex);
		adminUser.setProfileImage(uniqueFileId);

		String originalClassPath = null;
		try {
			originalClassPath = new ClassPathResource("").getFile().getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<String>(testService.saveUser(adminUser), HttpStatus.OK);
	}

	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest logReq) {
		return new ResponseEntity<LoginResponse>(testService.login(logReq),HttpStatus.OK);
	}
	
	@GetMapping("/resetPasswordMail/{email}")
	public String getToken(@PathVariable @NotBlank String email)
	{
		return resetPasswordService.getToken(email);
	}
	
	@PostMapping("/reset-password")
	public Boolean resetPassword(@Valid @RequestBody ResetPasswordRequest request)
	{
		return resetPasswordService.resetPassword(request);
	}
}
