package com.txnm.auth.service;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.txnm.auth.config.jwt.TokenValidator;

import jakarta.annotation.PreDestroy;

@Component
@Qualifier("TokenBlacklistImplLru")
public class TokenBlacklistImplLru implements TokenBlacklist {
    
    private static final int MAX_CAPACITY = 5;
    private final Map<String, Long> tokenBlacklist;
    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
    private final TokenValidator tokenValidator;
    
    public TokenBlacklistImplLru(TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
        this.tokenBlacklist = Collections.synchronizedMap(
            new LinkedHashMap<String, Long>(MAX_CAPACITY, 0.75f, true) {

				@Override
                protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
                    return size() > MAX_CAPACITY;
                }
            }
        );
        
        // Background cleanup for expired tokens
        startExpiredTokensCleanup();
    }
    
    @Override
    public boolean isBlacklisted(String token) {
        // Access order update - makes this token "recently used"
        return tokenBlacklist.containsKey(token);
    }
    
    @Override
    public void addToBlacklist(String token) {
        try {
            if (tokenValidator.isValidTokenWithoutBlacklistCheck(token)) {
                Date expiration = tokenValidator.getExpirationDateFromToken(token);
                tokenBlacklist.put(token, expiration.getTime());
                
                // Schedule individual cleanup
                long ttl = expiration.getTime() - System.currentTimeMillis();
                if (ttl > 0) {
                    cleanupScheduler.schedule(() -> removeFromBlacklist(token), ttl, TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            // Token is invalid or expired, no need to blacklist
        }
    }
    
    @Override
    public void removeFromBlacklist(String token) {
        tokenBlacklist.remove(token);
    }
    
    @Override
    public int size() {
        return tokenBlacklist.size();
    }
    
    private void startExpiredTokensCleanup() {
        // Periodic cleanup every 5 minutes for any missed expirations
        cleanupScheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            tokenBlacklist.entrySet().removeIf(entry -> entry.getValue() < now);
        }, 5, 5, TimeUnit.MINUTES);
    }
    
    @Override
    public void cleanup() {
        cleanupScheduler.shutdown();
    }
    
    @PreDestroy
    public void onDestroy() {
        cleanup();
    }
}