package com.codect.authService.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AclSidRepository extends JpaRepository<AclSid, Long>{

	AclSid findBySid(String userName);

	List<AclSid> findAllBySidIn(List<String> sids);

}
