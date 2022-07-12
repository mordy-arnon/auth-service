package com.codect.authService.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codect.authService.db.AclClass;
import com.codect.authService.db.UserGroup;
import com.codect.authService.db.UserGroupRepository;
import com.codect.authService.rest.modals.AclObjectReq;
import com.codect.authService.rest.modals.GrantReq;
import com.codect.authService.rest.modals.GroupIdReq;
import com.codect.authService.rest.modals.UserGroupReq;
import com.codect.authService.services.AuthorizationService;
import com.codect.authService.services.JwtService;

@RestController
@RequestMapping("/api/authorization")
public class AuthorizationController {

	@Autowired
	JwtService jwtService;
	@Autowired
	private UserGroupRepository groupRepository;
	@Autowired
	private AuthorizationService authorizationService;

// ----------------------------- between our services -----------------------------
	@GetMapping("/entityTypes")
	public Map<String,Object> getAllSecuredEntities(){
		HashMap<String, Object> ret = new HashMap<String, Object>();
		Map<String, String> entities = authorizationService.getEntityTypes();
		ret.put("entities",entities);
		return ret;
	}

	@PostMapping("/createObject")
	public void securedObjectCreated(@RequestBody AclObjectReq aclObj) {
		authorizationService.createSecureObject(aclObj);
	}

	@GetMapping(value="/group/user")
	public List<String> getAllUsersInGroup(GroupIdReq groupId) {
		UserGroup userGroup = new UserGroup();
		userGroup.setId(groupId.getGroupId());
		return authorizationService.getAllUsersInGroup(userGroup);
	}

	@PostMapping(value="/allow")
	public boolean isAllow(GrantReq grantReq) {
		AclClass clas = new AclClass();
		clas.setClassname(grantReq.getClassname());
		return authorizationService.isAllow(grantReq.getUsername(),clas,grantReq.getObjectId(),grantReq.getPerm());
	}

// ----------------------------- external for applications ------------------------ 

	@PostMapping("/group")
	public void createGroup(Map<String,String> group) {
		groupRepository.save(new UserGroup(group.get("name")));
		String username=authorizationService.getUserName();
		// insert admin perm for this object
	}

	@PostMapping("/group/user")
	public void addUserToGroup(UserGroupReq userGroup) {
		Optional<UserGroup> group = groupRepository.findById(userGroup.getGroupId());
		if (group.isPresent()) {
			authorizationService.addUserToGroup(group.get(),userGroup.getUsername());
		}
		else
			throw new IllegalArgumentException("No groupId:"+userGroup.getGroupId());
	}

	@RequestMapping(value="/group/user",method=RequestMethod.DELETE)
	public void removeUserFromGroup(UserGroupReq userGroup) {
		Optional<UserGroup> group = groupRepository.findById(userGroup.getGroupId());
		if (group.isPresent()) {
			authorizationService.removeUserFromGroup(group.get(),userGroup.getUsername());
		}
		else
			throw new IllegalArgumentException("No groupId:"+userGroup.getGroupId());
	}

	@RequestMapping(value="/group",method=RequestMethod.DELETE)
	public void deleteGroup(GroupIdReq groupId) {
		UserGroup ug = new UserGroup();
		ug.setId(groupId.getGroupId());
		authorizationService.deleteGroup(ug);
	}
	
	@GetMapping("/group")
	public void listGroups() {
		authorizationService.listGroups();
	}

//	@RequestMapping(value="/group/user",method=RequestMethod.DELETE)
//	public void listUsersInGroup(UserGroupReq userGroup) {
//		Optional<UserGroup> group = groupRepository.findById(userGroup.getGroupId());
//		if (group.isPresent()) {
//			authorizationService.removeUserFromGroup(group.get(),userGroup.getUsername());
//		}
//		else
//			throw new IllegalArgumentException("No groupId:"+userGroup.getGroupId());
//	}
	
	@PostMapping(value="/grant")
	public void grant(GrantReq grantReq) {
		AclClass clas = new AclClass();
		clas.setClassname(grantReq.getClassname());
		authorizationService.grant(grantReq.getUsername(),clas,grantReq.getObjectId(),grantReq.getPerm());
	}
	
//	getAllowFor. input user + list of objects/classes.
	
//	can return all permissions compressed in the JWT for other services to use.
//	       suggestion for compression: list of strings in the format: ${class_id}_${object_id}_${mask}
//	list users with rights to object+permission.
	
}
