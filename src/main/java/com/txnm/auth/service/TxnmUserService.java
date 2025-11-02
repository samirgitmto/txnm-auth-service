package com.txnm.auth.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.txnm.auth.dao.Role;
import com.txnm.auth.dao.TxnmUser;
import com.txnm.auth.dto.RegisterRequest;
import com.txnm.auth.repo.TxnmUserRepository;

@Service
public class TxnmUserService {

	@Autowired
	private TxnmUserRepository userRepository;
	
	public TxnmUser createUser(RegisterRequest registerRequest) {
		TxnmUser user = new TxnmUser();
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setEmailVerified(false); // Will be verified via email
//        user.setCreatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
        user.setRoles(Set.of(Role.USER));
        
        TxnmUser savedUser = userRepository.save(user);
        return savedUser;
	}
	
	public TxnmUser findByEmail(String email) {
		return userRepository.findByEmail(email).get();
	}
	
	public UUID verifyEmail(String email) {
		TxnmUser savedUser = findByEmail(email);
		savedUser.setEmailVerified(true);
		userRepository.save(savedUser);
		return savedUser.getId();
	}
}