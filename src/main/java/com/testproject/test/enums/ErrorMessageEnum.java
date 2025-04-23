package com.testproject.test.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessageEnum {
	LIST_EMPTY("User List Not found","101"),
	EMAIL_ALREADY_EXISTS("Email Already Exists","102"),
	USER_NOT_FOUND("User not Found","103"),
	USERNAME_ALREADY_EXISTS("Username already exists","104");
	private String errMessage;
	private String errCode;
}
