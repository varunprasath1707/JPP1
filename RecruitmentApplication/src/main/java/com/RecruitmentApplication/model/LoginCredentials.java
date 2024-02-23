package com.RecruitmentApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="login_credentials")
public class LoginCredentials {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	 
	private Integer loginId;
	
	@Column(name="username")
	private String username;
	
	@Column(name="email_id")
	private String emailId;
	
	@Column(name="password")
	private String password;
	
	@Column(name="user_role")
	private String userRole;
	
	@Column(name="is_active")
	private Boolean isActive;

	@Column(name="is_verified")
	private Boolean isVerified;
	
	
	public LoginCredentials() {
		
	}
    public LoginCredentials(String username, String emailId, String password, String userRole, Boolean isActive,
			Boolean isVerified) {
		super();
		this.username = username;
		this.emailId = emailId;
		this.password = password;
		this.userRole = userRole;
		this.isActive = isActive;
		this.isVerified = isVerified;
	}
	public Integer getLoginId() {
		return loginId;
	}
	public void setLoginId(Integer loginId) {
		this.loginId = loginId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Boolean getIsVerified() {
		return isVerified;
	}
	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}
	
    
	
	
	

}
