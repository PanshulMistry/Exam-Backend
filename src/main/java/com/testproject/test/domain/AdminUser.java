package com.testproject.test.domain;


import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.testproject.test.enums.Gender;
import com.testproject.test.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class AdminUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 private String name;
	 private Date dob;
	 @Column(unique = true)
	 private String username;
	 private String password;
	 @Enumerated(EnumType.STRING)
	 private Gender gender;
	 @Column(unique = true)
	 private String email;
	 private String address;
	 private String profileImage;
	 private Long contactNumber;
	 private String pinCode;
	 @Enumerated(EnumType.STRING)
	 private Role accessRole;
}
