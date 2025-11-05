package com.txnm.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.txnm.auth.config.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class LogoutController {

	@Autowired
	private JwtUtil jwtUtil;
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer")) {
			String jwt = bearerToken.substring(7);
			jwtUtil.blacklistToken(jwt);
		}
		
		return ResponseEntity.ok().body("Logged out successfully");
	}
	
}
