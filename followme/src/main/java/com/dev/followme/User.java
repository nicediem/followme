package com.dev.followme;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
	
	@Id
	@Column(length=50,nullable=false)
	private String id;
	
	@Column(length=100,nullable=false)
	private String pwd;
	
	@Column(length=50,nullable=false)
	private String name;
	
	@Column(length=100,nullable=false)
	private String email;
	
	public User() { super(); }
	public User(String id,String pwd,String name,String email) {
		this.id = id;
		this.pwd = pwd;
		this.name = name;
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
