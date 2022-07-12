package com.codect.authService.rest.modals;

public class AclObjectReq {
	private String secureType;
	private long objectId;
	private String owner;
	
	public long getObjectId() {
		return objectId;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getSecureType() {
		return secureType;
	}
	public void setSecureType(String secureType) {
		this.secureType = secureType;
	}
}
