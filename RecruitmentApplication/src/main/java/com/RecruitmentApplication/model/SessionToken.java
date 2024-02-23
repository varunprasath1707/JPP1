package com.RecruitmentApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="session_token")
public class SessionToken {
	
	@Id
	private Integer userId;
	
	@Column(name="session_token")
	private String sessionToken;
	
	@Column(name="created_on")
	private String createdOn;
	
	public SessionToken() {
		super();
	}
	
	public SessionToken(int userId, String sessionToken, String createdOn) {
		super();
		this.userId = userId;
		this.sessionToken = sessionToken;
		this.createdOn = createdOn;
	}

	public SessionToken(String sessionToken, String createdOn) {
		super();
		this.sessionToken = sessionToken;
		this.createdOn = createdOn;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "SessionToken [userId=" + userId + ", sessionToken=" + sessionToken + ", createdOn=" + createdOn + "]";
	}

	

}
