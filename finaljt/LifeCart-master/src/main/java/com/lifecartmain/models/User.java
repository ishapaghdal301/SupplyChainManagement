package com.lifecartmain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class User {
	@Id
	String username;
	String password;
	String confirm_password;
	@Column(unique=true)
	String email;
	boolean isAdmin;
	public User() {
		super();
		this.confirm_password = null;
		this.username = null;
		this.password = null;
		this.email = null;
		this.isAdmin = false;
	}
	public User(String username, String password, String email,String confirm_password) {
		super();
		this.confirm_password = confirm_password;
		this.username = username;
		this.password = password;
		this.email = email;
		this.isAdmin = false;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public String getConfirm_password() {
		return confirm_password;
	}
	public void setConfirm_password(String confirm_password) {
		this.confirm_password = confirm_password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", email=" + email + "]";
	}
}
