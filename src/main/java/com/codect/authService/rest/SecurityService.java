package com.codect.authService.rest;

import java.util.Arrays;

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
import org.springframework.stereotype.Service;

import com.codect.authService.entities.Organization;
import com.codect.authService.remote.client.RemotePreAuthorize;

@Service
public class SecurityService {

	@Autowired
	private ApplicationContext ac;

	public String dothat(Organization orga) {
		return "mordy";
	}

	@PreAuthorize("hasPermission(#orga, 'ADMINISTRATION')")
	public String dothat2(Organization orga) {
		return "mordy";
	}

	public void grant(String username, Organization organization, String perm) {
		// Prepare the information we'd like in our access control entry (ACE)
		ObjectIdentity oi = new ObjectIdentityImpl(Organization.class, Integer.valueOf(123));
		Sid sid = new PrincipalSid(username);
		Permission p = new DefaultPermissionFactory().buildFromName(perm);
		JdbcMutableAclService aclService=ac.getBean(JdbcMutableAclService.class);
		// Create or update the relevant ACL
		MutableAcl acl = null;
		try {
			acl = (MutableAcl) aclService.readAclById(oi);
		} catch (NotFoundException nfe) {
			acl = aclService.createAcl(oi);
		}
		try{
			acl.isGranted(Arrays.asList(p),Arrays.asList(sid), false);
		}catch (NotFoundException e) {
			// Now grant some permissions via an access control entry (ACE)
			acl.insertAce(acl.getEntries().size(), p, sid, true);
			aclService.updateAcl(acl);
		}
	}

	@RemotePreAuthorize("hasPermission(#orga, 'ADMINISTRATION')")
	public String home3(Organization orga) {
		return "mordy";
	}
}
