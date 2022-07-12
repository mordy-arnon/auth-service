package com.codect.authService.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AclClass {
	
	@Id
	private short id;
	@Column(name = "class")
	private String classname;
	
	public AclClass() {}
	public AclClass(short id, String classname) {
		this.id = id;
		this.classname = classname;
	}
	public short getId() {
		return id;
	}
	public void setId(short id) {
		this.id = id;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
}
