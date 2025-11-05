package com.txnm.auth.service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.txnm.auth.config.jwt.JwtUtil;
import com.txnm.auth.dao.AuthnProvider;
import com.txnm.auth.dao.TxnmUser;
import com.txnm.auth.repo.TxnmUserRepository;

@Service
public class TxnmUserDetailsService implements UserDetailsService {

	@Autowired
	private TxnmUserRepository userRepository;
	@Autowired
	private JwtUtil jwtUtil;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (username==null || username.trim().isEmpty()) {
			throw new UsernameNotFoundException("username can't be null or empty");
		}
		Optional<TxnmUser> txnmUser = userRepository.findByEmailWithRelations(username);
		TxnmUser user = txnmUser.orElseThrow(() -> new UsernameNotFoundException("no user found"));
		UserDetails userDetails = createUserDetailsFromTxnmUser(user);
		return userDetails;
	}

	public UserDetails createUserDetailsFromTxnmUser(TxnmUser txnmUser) {
		UserDetails userDetails = User.builder()
			.username(txnmUser.getEmail())
			.password(getPasswordFromTxnmUser(txnmUser))     // Handle both local and OAuth2 users
			.authorities(getAuthorities(txnmUser))
			.accountExpired(false)
			.accountLocked(false)
			.credentialsExpired(false)
			.disabled(!txnmUser.isEmailVerified())
			.build();
		
		return userDetails;
	}

	public Collection<? extends GrantedAuthority> getAuthorities(TxnmUser txnmUser) {
	    return txnmUser.getRoles().stream()
	            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
	            .collect(Collectors.toList());
	}

	private String getPasswordFromTxnmUser(TxnmUser txnmUser) {
	    return txnmUser.getAuthnProviders().stream()
	            .filter(auth -> "local".equals(auth.getProvider()))
	            .findFirst()
	            .map(AuthnProvider::getPasswordHash)
	            .orElse("");
	}
	
}
