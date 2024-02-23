package com.RecruitmentApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name ="recent_activities")
public class Recentactivities {

	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	
	private Integer activityId;
	
	@Column(name="date_time")
	private String datetime;
	
	@Column(name="message")
	private String message;

	public Recentactivities() {
		super();
	}

	public Recentactivities(String datetime, String message) {
		super();
		this.datetime = datetime;
		this.message = message;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
}
