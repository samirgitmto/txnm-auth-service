package com.txnm.auth.dao;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_providers")
public class AuthnProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private TxnmUser user;
    
    @Column(nullable = false)
    private String provider; // "local", "google", "github", etc.

    @Column(name = "provider_user_id")
    private String providerUserId; // ID from OAuth provider, null for local

    @Column(name = "password_hash")
    private String passwordHash; // Only for local provider

    @Type(JsonBinaryType.class)
    @Column(name = "provider_metadata", columnDefinition = "jsonb")
    private String providerMetadata; // Store additional provider data as JSON

    @Column(name = "linked_at")
    private LocalDateTime linkedAt;

    @PrePersist
    protected void onCreate() {
        linkedAt = LocalDateTime.now();
    }
}