package com.txnm.auth.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.txnm.auth.config.jwt.JwtUtil;
import com.txnm.auth.dao.TxnmUser;
import com.txnm.auth.dto.RegisterRequest;
import com.txnm.auth.dto.RegisterResponse;
import com.txnm.auth.dto.VerifyOtpRequest;
import com.txnm.auth.dto.VerifyOtpResponse;

import jakarta.validation.Valid;

@Service
public class RegistrationService {

	@Autowired
	private TxnmUserService txnmUserService;
	@Autowired
	private AuthnProviderService authnProviderService;
	@Autowired
	private OtpService otpService;
	@Autowired
	private JwtUtil jwtUtil;
	
	public RegisterResponse register(@Valid RegisterRequest registerRequest) {
		
		TxnmUser savedUser = txnmUserService.createUser(registerRequest);
		authnProviderService.saveLocalAuthProvider(registerRequest, savedUser);
		otpService.generateOtp(savedUser.getEmail());
		
		return new RegisterResponse(
	            "User registered successfully. Please check your email for OTP.",
	            savedUser.getId()
	        );
	}

	public VerifyOtpResponse verifyOtp(@Valid VerifyOtpRequest verifyOtpRequest) {
		
		boolean isValidOtp = otpService.validateOtp(verifyOtpRequest.getEmail(), verifyOtpRequest.getOtp());
		if (!isValidOtp)
			return null;
		
		UUID userId = txnmUserService.verifyEmail(verifyOtpRequest.getEmail());
		
		String jwt = jwtUtil.generateTokenFromUsername(verifyOtpRequest.getEmail());
		
		return new VerifyOtpResponse(
                "Email verified successfully",
                jwt,
                userId
            );
	}

}
