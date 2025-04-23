package com.testproject.test.exception.globalexception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.testproject.test.enums.ErrorMessageEnum;
import com.testproject.test.exception.EmailAlreadyExistsException;
import com.testproject.test.exception.UserObjNotFoundException;

@RestControllerAdvice
public class GlobalException {
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public Map<String, String> handleAttributes(MethodArgumentNotValidException e) {
		Map<String, String> errList = new HashMap<>();
		e.getAllErrors().forEach(ex -> {
			String fieldName = ((FieldError) ex).getField();
			String value = ex.getDefaultMessage();
			errList.put(fieldName, value);
		});
		return errList;
	}
	
	@ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(new ErrorResponse(ErrorMessageEnum.EMAIL_ALREADY_EXISTS.getErrMessage(), ErrorMessageEnum.EMAIL_ALREADY_EXISTS.getErrCode()), HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(UserObjNotFoundException.class)
    public ResponseEntity<Object> handleUserObjNotFoundException(UserObjNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(ErrorMessageEnum.USER_NOT_FOUND.getErrMessage(), ErrorMessageEnum.USER_NOT_FOUND.getErrCode()), HttpStatus.BAD_REQUEST);
    }

}
