package com.txnm.auth.config.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.txnm.auth.dao.TxnmUser;
import com.txnm.auth.repo.TxnmUserRepository;
import com.txnm.auth.service.TxnmUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private TxnmUserRepository txnmUserRepository;
	@Autowired
	private TxnmUserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// 1. extract token from request header
		// 2. Validate token & signature
		// 3. load UserDetails i.e. email
		// 4. Create Authentication: A. create UserDetails    B. credentials kept as null     C. Collection <? extends GrantedAuthority> authorities
		// 5. Set SecurityContext with Authentication
		
		String token = extractJwtFromRequest(request);
		
		try {
			if (token!=null && jwtUtil.validateToken(token)) {
				
				String emailFromToken = jwtUtil.getEmailFromToken(token);
				
				// to be removed later
//				TxnmUser txnmUser = txnmUserRepository.findByEmailWithRoles(emailFromToken)
//												.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + emailFromToken));
//				UserDetails userDetails = jwtUtil.createUserDetailsFromTxnmUser(txnmUser);
//				Collection<? extends GrantedAuthority> authorities = jwtUtil.getAuthorities(txnmUser);
				
				UserDetails userDetails = userDetailsService.loadUserByUsername(emailFromToken);
				Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
				UsernamePasswordAuthenticationToken uPAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
				
				SecurityContextHolder.getContext()
									.setAuthentication(uPAuthenticationToken);
			}
		}
		catch (Exception e) {
			System.err.println("Cannot set user authentication: "+ e);
		}
		
		filterChain.doFilter(request, response);
		
	}
	
	private String extractJwtFromRequest(HttpServletRequest request) {
	    String bearerToken = request.getHeader("Authorization");
	    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
	        return bearerToken.substring(7);
	    }
	    return null;
	}
}
