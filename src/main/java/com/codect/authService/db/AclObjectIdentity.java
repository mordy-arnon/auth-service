package com.codect.authService.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AclObjectIdentity{

	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	private short objectIdClass;
	private long objectIdIdentity;
	private long ownerSid;
	//parent_object bigint,
	private boolean entriesInheriting=false;
	
	public AclObjectIdentity() {}
	public AclObjectIdentity(short objectIdClass, long objectIdIdentity, long ownerSid) {
		this.objectIdClass = objectIdClass;
		this.objectIdIdentity = objectIdIdentity;
		this.ownerSid = ownerSid;
	}
	public Long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public short getObjectIdClass() {
		return objectIdClass;
	}
	public void setObjectIdClass(short objectIdClass) {
		this.objectIdClass = objectIdClass;
	}
	public long getObjectIdIdentity() {
		return objectIdIdentity;
	}
	public void setObjectIdIdentity(long objectIdIdentity) {
		this.objectIdIdentity = objectIdIdentity;
	}
	public long getOwnerSid() {
		return ownerSid;
	}
	public void setOwnerSid(long ownerSid) {
		this.ownerSid = ownerSid;
	}
	public boolean isEntriesInheriting() {
		return entriesInheriting;
	}
	public void setEntriesInheriting(boolean entriesInheriting) {
		this.entriesInheriting = entriesInheriting;
	}
}
