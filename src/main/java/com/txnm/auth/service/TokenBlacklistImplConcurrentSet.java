package com.txnm.auth.service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.txnm.auth.config.jwt.TokenValidator;

import jakarta.annotation.PreDestroy;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j     // Automatically creates: private static final Logger log = ...
@Component
@Qualifier("TokenBlacklistImplConcurrentSet")
public class TokenBlacklistImplConcurrentSet implements TokenBlacklist {

	private static Logger LOGGER = LoggerFactory.getLogger(TokenBlacklistImplConcurrentSet.class);
	
	private final Set<String> concurrentBlacklistTokenSet = ConcurrentHashMap.newKeySet();
	private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
	private final TokenValidator tokenValidator;
	
	public TokenBlacklistImplConcurrentSet(TokenValidator tokenValidator) {
		this.tokenValidator = tokenValidator;
	}

	@Override
	public boolean isBlacklisted(String token) {
		return concurrentBlacklistTokenSet.contains(token);
	}

	@Override
	public void addToBlacklist(String token) {
		try {
			if (tokenValidator.isValidTokenWithoutBlacklistCheck(token)) {
				concurrentBlacklistTokenSet.add(token);
				
				Date expiration = tokenValidator.getExpirationDateFromToken(token);
				long ttl = expiration.getTime() - System.currentTimeMillis();
				
				if (ttl > 0) {
					cleanupScheduler.schedule(() -> removeFromBlacklist(token), ttl, TimeUnit.MILLISECONDS);
					LOGGER.debug("cleanup scheduled");
				}
			}
		}
		catch (Exception e) {
			LOGGER.debug("Token is invalid or expired, no need to blacklist");
		}
		
	}

	@Override
	public void removeFromBlacklist(String token) {
		concurrentBlacklistTokenSet.remove(token);
		LOGGER.debug("Token removed from blacklist");
		
	}

	@Override
	public int size() {
		return concurrentBlacklistTokenSet.size();
	}

	@Override
	public void cleanup() {
		LOGGER.debug("cleanup started");
		cleanupScheduler.shutdown();
	}
	
	@PreDestroy
	public void onDestroy() {
		cleanup();
	}
	
}