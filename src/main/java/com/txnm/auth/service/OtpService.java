package com.txnm.auth.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.txnm.auth.dto.OtpData;

@Service
public class OtpService {
    
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private static final long OTP_VALIDITY_DURATION = 10 * 60 * 1000; // 10 minutes
    
    @Value("${otp.length:6}")
    private int otpLength;
    
    public String generateOtp(String email) {
        String otp = generateRandomOtp();
        System.out.println("**********otp");
        System.out.println(otp);
        OtpData otpData = new OtpData(otp, System.currentTimeMillis());
        otpStorage.put(email, otpData);
        return otp;
    }
    
    public boolean validateOtp(String email, String otp) {
        OtpData otpData = otpStorage.get(email);
        
        if (otpData == null) {
            return false;
        }
        
        if (System.currentTimeMillis() - otpData.getCreatedAt() > OTP_VALIDITY_DURATION) {
            otpStorage.remove(email);
            return false;
        }
        
        if (otpData.getOtp().equals(otp)) {
            otpStorage.remove(email);
            return true;
        }
        
        return false;
    }
    
    private String generateRandomOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }
    
}