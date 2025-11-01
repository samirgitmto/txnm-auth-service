package com.txnm.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.txnm.auth.dto.RegisterRequest;
import com.txnm.auth.dto.RegisterResponse;
import com.txnm.auth.service.RegistrationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class RegistrationController {

	@Autowired
	private RegistrationService regService;
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid RegisterRequest registerRequest) {
		
		try {
			RegisterResponse register = regService.register(registerRequest);
			return ResponseEntity.ok(register);
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								.body("Registration failed: " + e.getMessage());
		}
	}
	
}
