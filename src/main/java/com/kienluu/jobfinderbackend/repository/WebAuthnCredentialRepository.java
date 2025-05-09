package com.kienluu.jobfinderbackend.repository;

import com.kienluu.jobfinderbackend.entity.WebAuthnCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface WebAuthnCredentialRepository extends JpaRepository<WebAuthnCredential, Long> {

    Optional<WebAuthnCredential> findByCredentialId(String credentialId);

    Optional<WebAuthnCredential> findByUserHandle(String userHandle);

    Set<WebAuthnCredential> findAllByUserHandle(String userHandle);
}

