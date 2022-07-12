package com.codect.authService.remote.client;

public class SecuredEntityLetsSayCamera {

	private int id;
	private String name;
	public SecuredEntityLetsSayCamera() {
		
	}
	public SecuredEntityLetsSayCamera(int id, String name) {
		this.id=id;
		this.name=name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "SecuredEntityLetsSayCamera [id=" + id + ", name=" + name + "]";
	}
	
}
