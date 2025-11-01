package com.txnm.auth.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.JoinColumn;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "txnm_users")
public class TxnmUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String name;
    
    @Column(name = "email_verified")
    private boolean emailVerified;
    
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
 // Hibernate will automatically set this on entity creation
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
 // Hibernate will automatically update this on every entity update
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    // One User can have multiple AuthProviders (local, google, etc.)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthnProvider> authnProviders = new ArrayList<>();
    
    // One User can have multiple VerificationTokens (email verify, password reset)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VerificationToken> verificationTokens = new ArrayList<>();
 
    @ElementCollection(fetch = FetchType.LAZY)  // Changed to LAZY
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "txnm_user_roles", 
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role_name")
    private Set<Role> roles = new HashSet<>();
    
 // Business method - Hibernate will auto-update updatedAt via @UpdateTimestamp
    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
        // No need to manually update 'updatedAt' - @UpdateTimestamp handles it
        // user.recordLogin() from the service layer // Updates lastLoginAt
    }
}