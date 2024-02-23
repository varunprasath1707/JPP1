package com.RecruitmentApplication.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="job_opening")
public class JobOpening {
	
		   @Id
		   @GeneratedValue(strategy = GenerationType.IDENTITY)
		   private Integer jobOpeningId;
		   
		   @Column(name="client_name")
		   private String clientName;
		   
		   @Column(name="position_title")
		   private String positionTitle;
		   
		   @Column(name="skillset")
		   private String skillset;
		   
		   @Column(name="job_description")
		   private String jobDescription;
		   
		   @Column(name="experience")
		   private String experience;
		   
		   @Column(name="japanese_level")
		   private String japaneseLevel;
		   
		   @Column(name="no_of_openings")
		   private Integer noOfOpenings;
		   
		   @Column(name="country")
		   private String country;
		   
		   @Column(name="salary")
		   private String salary;
		   
		   @Column(name="state_name")
		   private String stateName;
		   
		   @Column(name="job_opening_status")
		   private String jobOpeningStatus;
		   
		   @Column(name="assigned_by")
		   private String assignedBy;
		   
		   @Column(name="job_type")
		   private String jobType;
		   
		   @Column(name="city")
		   private String city;
		   
		   @Column(name="requirement_received_date")
			private Date requirementReceivedDate;
		   
		   @Column(name="user_id")
			private Integer userId;
		   
		   @Column(name="deleted")
		   private Boolean deleted=false;

		   
		   
		public JobOpening() {
			super();
		}

		public JobOpening(String clientName, String positionTitle, String skillset, String jobDescription,
				String experience, String japaneseLevel, Integer noOfOpenings, String country, String salary,
				String stateName, String jobOpeningStatus, String assignedBy, String jobType, String city,
				Date requirementReceivedDate, Integer userId,Boolean deleted) {
			super();
			this.clientName = clientName;
			this.positionTitle = positionTitle;
			this.skillset = skillset;
			this.jobDescription = jobDescription;
			this.experience = experience;
			this.japaneseLevel = japaneseLevel;
			this.noOfOpenings = noOfOpenings;
			this.country = country;
			this.salary = salary;
			this.stateName = stateName;
			this.jobOpeningStatus = jobOpeningStatus;
			this.assignedBy = assignedBy;
			this.jobType = jobType;
			this.city = city;
			this.requirementReceivedDate = requirementReceivedDate;
			this.userId=userId;
			this.deleted = deleted;
		}

		public Integer getJobOpeningId() {
			return jobOpeningId;
		}

		public void setJobOpeningId(Integer jobOpeningId) {
			this.jobOpeningId = jobOpeningId;
		}

		public String getClientName() {
			return clientName;
		}

		public void setClientName(String clientName) {
			this.clientName = clientName;
		}

		public String getPositionTitle() {
			return positionTitle;
		}

		public void setPositionTitle(String positionTitle) {
			this.positionTitle = positionTitle;
		}

		public String getSkillset() {
			return skillset;
		}

		public void setSkillset(String skillset) {
			this.skillset = skillset;
		}

		public String getJobDescription() {
			return jobDescription;
		}

		public void setJobDescription(String jobDescription) {
			this.jobDescription = jobDescription;
		}

		public String getExperience() {
			return experience;
		}

		public void setExperience(String experience) {
			this.experience = experience;
		}

		public String getJapaneseLevel() {
			return japaneseLevel;
		}

		public void setJapaneseLevel(String japaneseLevel) {
			this.japaneseLevel = japaneseLevel;
		}

		public Integer getNoOfOpenings() {
			return noOfOpenings;
		}

		public void setNoOfOpenings(Integer noOfOpenings) {
			this.noOfOpenings = noOfOpenings;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getSalary() {
			return salary;
		}

		public void setSalary(String salary) {
			this.salary = salary;
		}

		public String getStateName() {
			return stateName;
		}

		public void setStateName(String stateName) {
			this.stateName = stateName;
		}

		public String getJobOpeningStatus() {
			return jobOpeningStatus;
		}

		public void setJobOpeningStatus(String jobOpeningStatus) {
			this.jobOpeningStatus = jobOpeningStatus;
		}

		public String getAssignedBy() {
			return assignedBy;
		}

		public void setAssignedBy(String assignedBy) {
			this.assignedBy = assignedBy;
		}

		public String getJobType() {
			return jobType;
		}

		public void setJobType(String jobType) {
			this.jobType = jobType;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public Date getRequirementReceivedDate() {
			return requirementReceivedDate;
		}

		public void setRequirementReceivedDate(Date requirementReceivedDate) {
			this.requirementReceivedDate = requirementReceivedDate;
		}
		
		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}


		public Boolean getDeleted() {
			return deleted;
		}

		public void setDeleted(Boolean deleted) {
			this.deleted = deleted;
		}
		   
		   
		   

		   
		   
	   
		   
}