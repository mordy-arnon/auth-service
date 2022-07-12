package com.codect.authService.db;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AclSid {

	@Id
	private long id;
	private boolean principal;
	private String sid;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isPrincipal() {
		return principal;
	}
	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
}
