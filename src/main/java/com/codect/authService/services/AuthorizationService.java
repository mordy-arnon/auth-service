package com.codect.authService.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.codect.authService.db.AclClass;
import com.codect.authService.db.AclClassesRepository;
import com.codect.authService.db.AclEntry;
import com.codect.authService.db.AclEntryRepository;
import com.codect.authService.db.AclObjectIdentity;
import com.codect.authService.db.AclObjectIdentityRepository;
import com.codect.authService.db.AclSid;
import com.codect.authService.db.AclSidRepository;
import com.codect.authService.db.GroupMember;
import com.codect.authService.db.GroupMemberRepository;
import com.codect.authService.db.UserGroup;
import com.codect.authService.db.UserGroupRepository;
import com.codect.authService.rest.modals.AclObjectReq;

@Service
public class AuthorizationService {

	@Autowired
	private AclSidRepository sidRepository;
	@Autowired
	private GroupMemberRepository groupMemberRepository;
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private AclObjectIdentityRepository aclObjectIdentityRepository;
	@Autowired
	private AclClassesRepository aclClassesRepository;
	@Autowired
	private AclEntryRepository aclEntryRepository;
	@Autowired
	private ApplicationContext ac;

	@PreAuthorize("hasPermission(userGroup, 'WRITE')")
	public void addUserToGroup(UserGroup userGroup, String username) {
		groupMemberRepository.save(new GroupMember(username, userGroup.getId()));
	}

	@PreAuthorize("hasPermission(userGroup, 'WRITE')")
	@Transactional
	public void removeUserFromGroup(UserGroup userGroup, String username) {
		GroupMember userInGroup = groupMemberRepository.findByUsernameAndGroupId(username, userGroup.getId());
		groupMemberRepository.delete(userInGroup);
	}

	@PreAuthorize("hasPermission(ug, 'DELETE')")
	@Transactional
	public void deleteGroup(UserGroup ug) {
		userGroupRepository.delete(ug);
		JdbcMutableAclService aclService = ac.getBean(JdbcMutableAclService.class);
		aclService.deleteAcl(new ObjectIdentityImpl(UserGroup.class, ug.getId()), true);
	}

	// some can be in cache.
	public List<UserGroup> listGroups() {
		AclClass aclClass = aclClassesRepository.findByClassname(UserGroup.class.getName());
		List<AclObjectIdentity> allGroups = aclObjectIdentityRepository.findByobjectIdClass(aclClass.getId());
		long sid = sidRepository.findBySid(getUserName()).getId();
		List<Long> allGroupsIds = allGroups.stream().map(x -> x.getId()).collect(Collectors.toList());
		List<AclEntry> myEntries = aclEntryRepository.findBySidAndAclObjectIdentityInAndGranting(sid, allGroupsIds, true);
		List<Integer> groupsIds = myEntries.stream().map(x -> (int) x.getAclObjectIdentity())
				.collect(Collectors.toList());
		return userGroupRepository.findAllById(groupsIds);
	}

	public String getUserName() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		} else {
			return principal.toString();
		}
	}

	@PreAuthorize("hasPermission(userGroup, 'ADMINISTRATOR')")
	public List<String> getAllUsersInGroup(UserGroup userGroup) {
		return groupMemberRepository.findByGroupId(userGroup.getId()).stream().map(x -> x.getUsername())
				.collect(Collectors.toList());
	}

	public boolean isAllow(String username, AclClass clas, long objectId, int perm) {
		ObjectIdentity oi = new ObjectIdentityImpl(clas.getClassname(), objectId);
		Sid sid = new PrincipalSid(username);
		Permission p = new DefaultPermissionFactory().buildFromMask(perm);
		JdbcMutableAclService aclService = ac.getBean(JdbcMutableAclService.class);
		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			acl = aclService.createAcl(oi);
		}
		try {
			return acl.isGranted(Arrays.asList(p), Arrays.asList(sid), false);
		} catch (Exception e) {
			return false;
		}
	}

	@PreAuthorize("hasPermission(theObject, 'ADMINISTRATOR')")
	public void grant(String username, AclClass clas, long objectId, int perm) {
		// Prepare the information we'd like in our access control entry (ACE)
		ObjectIdentity oi = new ObjectIdentityImpl(clas.getClassname(), objectId);
		Sid sid = new PrincipalSid(username);
		Permission p = new DefaultPermissionFactory().buildFromMask(perm);
		JdbcMutableAclService aclService = ac.getBean(JdbcMutableAclService.class);
		// Create or update the relevant ACL
		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			acl = aclService.createAcl(oi);
		}
		try {
			acl.isGranted(Arrays.asList(p), Arrays.asList(sid), false);
		} catch (NotFoundException e) {
			// Now grant some permissions via an access control entry (ACE)
			acl.insertAce(acl.getEntries().size(), p, sid, true);
			aclService.updateAcl(acl);
		}
	}

	/**
	 * 
	 * return the list of object_ids and object classes and masks that the user has
	 * access to. it return as string of "rules" with underscore (_):
	 * class_objectId_mask. 1000_3241_16
	 * 
	 * select from aclclass c join aclobject o on o.objectIdClass=c.id join aclentry
	 * e on e.aclObjectIdentity=o.id where e.sidId in select sid from aclSid where
	 * name in ((select g.username from userGroups g join groupmembers gm on
	 * gm.groupId=g.id where gm.username=username) union select username) and
	 * principal=false
	 * 
	 * @param username
	 * @return
	 */
	public List<String> getUserACLs(String username) {
		List<GroupMember> userGroups = groupMemberRepository.findByUsername(username);
		List<UserGroup> groupNames = userGroupRepository
				.findAllById(userGroups.stream().map(x -> x.getGroupId()).collect(Collectors.toList()));
		List<String> sids = groupNames.stream().map(x -> x.getName()).collect(Collectors.toList());
		sids.add(username);
		List<AclSid> sid = sidRepository.findAllBySidIn(sids);
		List<AclEntry> findBySid = aclEntryRepository.findBySidIn(sid.stream().map(AclSid::getId).collect(Collectors.toList()));
		List<String> ret = new ArrayList<String>();
		Map<Long, List<AclEntry>> myAclsByObject = findBySid.stream()
				.collect(Collectors.groupingBy(AclEntry::getAclObjectIdentity));
		for (Long objectId : myAclsByObject.keySet()) {
			List<AclEntry> list = myAclsByObject.get(objectId);
			long mask = calcMask(list);
			AclObjectIdentity aclObjectIdentity = aclObjectIdentityRepository.findById(objectId).get();
			Long theObjectId = aclObjectIdentity.getObjectIdIdentity();
			short objectIdClass = aclObjectIdentity.getObjectIdClass();
			ret.add(objectIdClass + "_" + theObjectId + "_" + mask);
		}
		return ret;
	}

	public Map<String, String> getEntityTypes() {
		Map<String,String> entities=new HashMap<String, String>();
		List<AclClass> findAll = aclClassesRepository.findAll();
		findAll.stream().forEach(c->entities.put(c.getClassname(),""+c.getId()));
		return entities;
	}

	private long calcMask(List<AclEntry> list) {
		int mask = 0;
		for (AclEntry aclEntry : list) {
			if (aclEntry.isGranting())
				mask |= aclEntry.getMask();
			else
				mask &= ~aclEntry.getMask();
		}
		return mask;
	}

	public void createSecureObject(AclObjectReq aclObj) {
		AclClass cls = aclClassesRepository.findByClassname(aclObj.getSecureType());
		AclSid sid = sidRepository.findBySid(aclObj.getOwner());
		try {
			aclObjectIdentityRepository.save(new AclObjectIdentity(cls.getId(),aclObj.getObjectId(),sid.getId()));
		}catch (Exception e) {
			if (!e.getMessage().contains("constraint [unique"))
				throw e;
		}
		grant(aclObj.getOwner(),cls,Long.valueOf(aclObj.getObjectId()),31);
	}
}
