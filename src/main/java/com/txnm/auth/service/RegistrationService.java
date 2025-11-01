package com.txnm.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.txnm.auth.dao.TxnmUser;
import com.txnm.auth.dto.RegisterRequest;
import com.txnm.auth.dto.RegisterResponse;

import jakarta.validation.Valid;

@Service
public class RegistrationService {

	@Autowired
	private TxnmUserService txnmUserService;
	@Autowired
	private AuthnProviderService authnProviderService;
	@Autowired
	private OtpService otpService;
	
	public RegisterResponse register(@Valid RegisterRequest registerRequest) {
		
		TxnmUser savedUser = txnmUserService.createUser(registerRequest);
		authnProviderService.saveLocalAuthProvider(registerRequest, savedUser);
		otpService.generateOtp(savedUser.getEmail());
		
		return new RegisterResponse(
	            "User registered successfully. Please check your email for OTP.",
	            savedUser.getId()
	        );
	}

}
