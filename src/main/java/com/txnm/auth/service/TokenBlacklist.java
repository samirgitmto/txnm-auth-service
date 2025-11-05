package com.txnm.auth.service;

import org.springframework.stereotype.Service;

public interface TokenBlacklist {
    boolean isBlacklisted(String token);
    void addToBlacklist(String token);
    void removeFromBlacklist(String token);
    int size();
    void cleanup();
}