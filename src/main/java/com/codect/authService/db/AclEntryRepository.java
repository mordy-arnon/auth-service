package com.codect.authService.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AclEntryRepository extends JpaRepository<AclEntry, Long>{

	List<AclEntry> findBySidAndAclObjectIdentityInAndGranting(Long sid,List<Long> collect, boolean b);

	List<AclEntry> findBySidIn(List<Long> sids);
	
}
