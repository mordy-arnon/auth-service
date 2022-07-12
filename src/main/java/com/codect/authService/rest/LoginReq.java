package com.codect.authService.rest;

public class LoginReq {
	private String username;
	private String password;
	private int expirationDuration=3600;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getExpirationDuration() {
		return expirationDuration;
	}
	public void setExpirationDuration(int expirationDuration) {
		this.expirationDuration = expirationDuration;
	}
}
