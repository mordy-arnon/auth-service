package com.codect.authService.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {

	GroupMember findByUsernameAndGroupId(String username, int id);

	List<GroupMember> findByGroupId(int groupId);

	List<GroupMember> findByUsername(String username);

}
