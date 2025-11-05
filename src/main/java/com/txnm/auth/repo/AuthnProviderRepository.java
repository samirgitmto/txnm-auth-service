package com.txnm.auth.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.txnm.auth.dao.AuthnProvider;

@Repository
public interface AuthnProviderRepository extends JpaRepository<AuthnProvider, UUID> {

	@Query("SELECT ap FROM AuthnProvider ap WHERE ap.user.id =:userId")
	Optional<AuthnProvider> findByUser(@Param("userId") UUID userId);
	
	// Can use findByUserId as well. Both are equivalent — Spring treats _ as a property separator for readability.
	Optional<AuthnProvider> findByUser_Id(UUID uuid);
}
