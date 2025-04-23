package com.testproject.test.proxy;


import java.sql.Date;

import com.testproject.test.enums.Gender;
import com.testproject.test.enums.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserProxy {
	private Long id;
	@NotBlank(message = "Please Enter Name")
	private String name;
	
	@NotNull(message = "Please Enter Date of Birth")
	private Date dob;
	
	@NotBlank(message = "Please enter username")
	private String username;
	
	@NotBlank(message = "Please enter password")
	private String password;

	@NotNull(message = "Please provide gender")
	private Gender gender;
	
	@NotBlank(message = "Please provide address")
	private String address;
	
	@NotBlank(message = "Please provide Email address")
	private String email;
	
	private byte[] profileImage;
	
	@NotNull(message = "Please provide contact Number")
	private Long contactNumber;
	
	@NotBlank(message = "Please provide pinCode")
	private String pinCode;
	
	private Role accessRole;
}
