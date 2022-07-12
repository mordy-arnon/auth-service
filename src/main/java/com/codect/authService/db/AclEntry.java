package com.codect.authService.db;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AclEntry {
	
	@Id
	private long id;
	private long aclObjectIdentity;
	private int aceOrder;
	private long sid;
	private int mask;
	private boolean granting;
	private boolean auditSuccess;
	private boolean auditFailure;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getAclObjectIdentity() {
		return aclObjectIdentity;
	}
	public void setAclObjectIdentity(long aclObjectIdentity) {
		this.aclObjectIdentity = aclObjectIdentity;
	}
	public int getAceOrder() {
		return aceOrder;
	}
	public void setAceOrder(int aceOrder) {
		this.aceOrder = aceOrder;
	}
	public long getSid() {
		return sid;
	}
	public void setSid(long sid) {
		this.sid = sid;
	}
	public int getMask() {
		return mask;
	}
	public void setMask(int mask) {
		this.mask = mask;
	}
	public boolean isGranting() {
		return granting;
	}
	public void setGranting(boolean granting) {
		this.granting = granting;
	}
	public boolean isAuditSuccess() {
		return auditSuccess;
	}
	public void setAuditSuccess(boolean auditSuccess) {
		this.auditSuccess = auditSuccess;
	}
	public boolean isAuditFailure() {
		return auditFailure;
	}
	public void setAuditFailure(boolean auditFailure) {
		this.auditFailure = auditFailure;
	}
}
