package com.txnm.auth.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.txnm.auth.dao.AuthnProvider;
import com.txnm.auth.dao.TxnmUser;
import com.txnm.auth.dto.RegisterRequest;
import com.txnm.auth.repo.AuthnProviderRepository;

@Service
public class AuthnProviderService {

	@Autowired
	private AuthnProviderRepository repository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	void saveLocalAuthProvider(RegisterRequest registerRequest, TxnmUser savedUser) {
		AuthnProvider localAuth = new AuthnProvider();
        localAuth.setUser(savedUser);
        localAuth.setProvider("local");
        localAuth.setProviderUserId(null);
        localAuth.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        localAuth.setLinkedAt(LocalDateTime.now());
        
        repository.save(localAuth);
	}
}
