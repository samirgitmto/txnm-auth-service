package com.txnm.auth.repo;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.txnm.auth.dao.TxnmUser;

@Repository
public interface TxnmUserRepository extends JpaRepository<TxnmUser, UUID> {

	@Query("SELECT u FROM TxnmUser u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<TxnmUser> findByEmailWithRoles(@Param("email") String email);

    // Simple query - since roles are eagerly fetched, this might be sufficient
    Optional<TxnmUser> findByEmail(String email);
	
}
