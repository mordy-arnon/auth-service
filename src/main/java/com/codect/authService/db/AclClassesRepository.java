package com.codect.authService.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AclClassesRepository extends JpaRepository<AclClass, Integer> {
	AclClass findByClassname(String classname);
}
