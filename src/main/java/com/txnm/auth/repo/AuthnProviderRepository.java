package com.txnm.auth.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.txnm.auth.dao.AuthnProvider;

@Repository
public interface AuthnProviderRepository extends JpaRepository<AuthnProvider, UUID> {

}
