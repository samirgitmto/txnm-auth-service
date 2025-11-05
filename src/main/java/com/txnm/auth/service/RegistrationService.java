package com.txnm.auth.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.txnm.auth.config.jwt.JwtUtil;
import com.txnm.auth.dao.AuthnProvider;
import com.txnm.auth.dao.TxnmUser;
import com.txnm.auth.dto.LoginRequest;
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
	@Autowired
	private PasswordEncoder passwordEncoder;
	
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

	public String validateLoginAndGenJWT(LoginRequest loginRequest) {
	
		TxnmUser txnmUser = txnmUserService.findByEmail(loginRequest.getEmail());
		
		List<AuthnProvider> authnProviders = txnmUser.getAuthnProviders();

		AuthnProvider authnProvider = authnProviders.stream()
					.filter(auth -> "local".equals(auth.getProvider()))
					.findFirst()
					.orElseThrow(() -> new RuntimeException("local authentication not set"));
		
		if (!passwordEncoder.matches(loginRequest.getPassword(), authnProvider.getPasswordHash())) {
			return "invalid email or password";
		}
		
		if (!txnmUser.isEmailVerified()) {
			return "email not verified";
		}
		
		txnmUser.recordLogin();
		txnmUserService.save(txnmUser);
		
		String jwt = jwtUtil.generateTokenFromUsername(loginRequest.getEmail());
		
		return jwt;
	}
	
}
