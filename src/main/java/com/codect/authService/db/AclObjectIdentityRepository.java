package com.codect.authService.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AclObjectIdentityRepository extends JpaRepository<AclObjectIdentity, Long>{
	List<AclObjectIdentity> findByobjectIdClass(short cId);
}
