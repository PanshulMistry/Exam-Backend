package com.testproject.test.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
	
	@Autowired
	@Qualifier("handlerExceptionResolver")
	private HandlerExceptionResolver exRes;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
        System.out.println("In commence");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Access Denied");
        errorResponse.put("errorClass", authException.getClass());
        errorResponse.put("message", authException.getMessage()); 
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("errorPath", request.getServletPath());
                
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

//		exRes.resolveException(request, response, null, authException);
	}
}

