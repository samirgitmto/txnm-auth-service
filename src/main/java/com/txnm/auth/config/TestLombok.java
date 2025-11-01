package com.txnm.auth.config;

import lombok.Data;

@Data
public class TestLombok {
    private String name;
    
    public static void main(String[] args) {
        TestLombok test = new TestLombok();
        test.setName("test"); // If this doesn't show error, Lombok works
        System.out.println(test.getName());
    }
}