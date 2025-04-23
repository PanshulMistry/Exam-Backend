package com.testproject.test.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.testproject.test.Filters.JwtFilter;
import com.testproject.test.service.impl.AuthUserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class TestSecurityConfiguration {
	@Autowired
	private JwtFilter jwtFilter;
	@Autowired
	private CustomAuthEntryPoint c;

	@Bean
	public SecurityFilterChain securityFilterchain(HttpSecurity http) throws Exception {
		System.err.println("SECURITY FILTER CHAIN");
		http.csrf(c -> c.disable());
		http.authorizeHttpRequests(
				auth -> auth.requestMatchers("/test/getUsers","/test/getUserDetails/**","/test/update-user","/test/deleteUser","/test/getAllUsers","/test/resetPasswordMail/**","/test/reset-password","/test/login","/test/saveUser", "/test/save-bulk-users/**")
						.permitAll().anyRequest().authenticated());
//				.requestMatchers("/security/getAllStudents").hasAnyAuthority("ADMIN").anyRequest().authenticated());
//		http.formLogin(Customizer.withDefaults());
		http.httpBasic(Customizer.withDefaults());
		http.exceptionHandling(e -> e.authenticationEntryPoint(c));
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passEncoder());
		return authProvider;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new AuthUserDetailsServiceImpl(); // Explicitly define the bean
	}

	@Bean
	public BCryptPasswordEncoder passEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
}
