package com.txnm.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.txnm.auth.dto.RegisterRequest;
import com.txnm.auth.dto.RegisterResponse;
import com.txnm.auth.dto.VerifyOtpRequest;
import com.txnm.auth.dto.VerifyOtpResponse;
import com.txnm.auth.service.RegistrationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class RegistrationController {

	@Autowired
	private RegistrationService regService;
	
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
		
		try {
			System.err.println("request started");
			RegisterResponse register = regService.register(registerRequest);
			return ResponseEntity.ok(register);
		}
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								.body("Registration failed: " + e.getMessage());
		}
	}
	
	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
		
		try {
			VerifyOtpResponse verifyOtpResponse = regService.verifyOtp(verifyOtpRequest);
			return ResponseEntity.ok(verifyOtpResponse);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								.body("OTP verification failed: " + e.getMessage());
		}
	}
	
}
