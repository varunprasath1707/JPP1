 package com.RecruitmentApplication.model;

import java.sql.Date;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="candidate_profiles")
public class CandidateProfiles {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer candidateId;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="gender")
	private String gender;
	
	@Column(unique=true,name="email_id")
	private String emailId;
	
	@Column(name="primary_contact")
	private Long primaryContact;
	
	@Column(name="nationality")
	private String nationality;
	
	@Column(name="current_company")
	private String currentCompany;

	
	@Column(name="it_experience")
	private String itExperience;
	
	@Column(name="japanese_level")
	private String japaneseLevel;
	
	@Column(name="current_location")
	private String currentLocation;
	
	@Column(name="position_title")
	private String positionTitle;
	
	@Column(name="source_by")
	private String sourceBy;
	
	@Column(name="resume_received_date")
	private Date resumeReceivedDate;
	
	@Column(name="notice_period")
	private String noticePeriod;

	@Column(name="additional_info")
	private String additionalInfo;
	
	@Column(name="date_of_birth")
	private Date dateOfBirth;
	
	@Column(name="qualification")
	private String qualification;
	
	@Column(name="institution")
	private String institution;
	
	@Column(name="city")
	private String city;
	
	@Column(name="country")
	private String country;
	
	@Column(name="state_name")
	private String stateName;
	
	@Column(name="zip_code")
	private String zipCode;
	
	@Column(name="expect_salary")
	private String expectSalary;
	
	@Column(name="current_salary")
	private String currentSalary;
	
	@Column(name="resume")
	private String resume;
	
	@Column(name="candidate_status")
	private String candidateStatus;
	
	@Column(name="ratings")
	private Double ratings;
	
	@Column(name="visa_status")
	private String visaStatus;
	
	@Column(name="marital_status")
	private String maritalStatus;
	
	@Column(name="ready_to_relocate")
	private String readyToRelocate;
	
	@Column(name="offer_in_hand")
	private String offerInHand;
	
	@Column(name="primary_skill")
	private String primarySkill;
	
	@Column(name="secondary_skill")
	private String secondarySkill;
	
	@Column(name="no_of_years_japan")
	private String noOfYearsJapan;
	
	@Column(name="relevant_experience_technology")
	private Integer relevantExperienceTechnology;

	@Column(name="mr_or_ms")
	private String mrOrMs;
	
	@Column(name="country_code")
	private String countryCode;
	
	@Column(name="interview_status")
	private String interviewStatus;
	
	@Column(name="secondary_contact")
	private Long secondaryContact;
	
	@Column(name="current_address")
	private String currentAddress;
	
	@Column(name="permanent_address")
	private String permanentAddress;
	
	@Column(name="job_type")
	private String jobType;
	
	@Column(name="linkedin")
	private String linkedin;

	@Column(name="user_id")
	private Integer userId;
	
	@Column(name="deleted")
	private Boolean deleted = false;
	
	
	
	

	public CandidateProfiles() {
		super();
	}

	public CandidateProfiles(String firstName, String lastName, String gender, String emailId, Long primaryContact,
			String nationality, String currentCompany, String itExperience, String japaneseLevel,
			String currentLocation, String positionTitle, String sourceBy, Date resumeReceivedDate, String noticePeriod,
			String additionalInfo, Date dateOfBirth, String qualification, String institution, String city,
			String country, String stateName, String zipCode, String expectSalary, String currentSalary, String resume,
			String candidateStatus, Double ratings, String visaStatus, String maritalStatus, String readyToRelocate,
			String offerInHand, String primarySkill, String secondarySkill, String noOfYearsJapan,
			Integer relevantExperienceTechnology, String mrOrMs, String countryCode, String interviewStatus,
			Long secondaryContact, String currentAddress, String permanentAddress,String jobType,String linkedin,Integer userId, Boolean deleted) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.emailId = emailId;
		this.primaryContact = primaryContact;
		this.nationality = nationality;
		this.currentCompany = currentCompany;
		this.itExperience = itExperience;
		this.japaneseLevel = japaneseLevel;
		this.currentLocation = currentLocation;
		this.positionTitle = positionTitle;
		this.sourceBy = sourceBy;
		this.resumeReceivedDate = resumeReceivedDate;
		this.noticePeriod = noticePeriod;
		this.additionalInfo = additionalInfo;
		this.dateOfBirth = dateOfBirth;
		this.qualification = qualification;
		this.institution = institution;
		this.city = city;
		this.country = country;
		this.stateName = stateName;
		this.zipCode = zipCode;
		this.expectSalary = expectSalary;
		this.currentSalary = currentSalary;
		this.resume = resume;
		this.candidateStatus = candidateStatus;
		this.ratings = ratings;
		this.visaStatus = visaStatus;
		this.maritalStatus = maritalStatus;
		this.readyToRelocate = readyToRelocate;
		this.offerInHand = offerInHand;
		this.primarySkill = primarySkill;
		this.secondarySkill = secondarySkill;
		this.noOfYearsJapan = noOfYearsJapan;
		this.relevantExperienceTechnology = relevantExperienceTechnology;
		this.mrOrMs = mrOrMs;
		this.countryCode = countryCode;
		this.interviewStatus = interviewStatus;
		this.secondaryContact = secondaryContact;
		this.currentAddress = currentAddress;
		this.permanentAddress = permanentAddress;
		this.jobType=jobType;
		this.linkedin=linkedin;
		this.userId=userId;
		this.deleted = deleted;
	}

	

	public Integer getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(Integer candidateId) {
		this.candidateId = candidateId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Long getPrimaryContact() {
		return primaryContact;
	}

	public void setPrimaryContact(Long primaryContact) {
		this.primaryContact = primaryContact;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getCurrentCompany() {
		return currentCompany;
	}

	public void setCurrentCompany(String currentCompany) {
		this.currentCompany = currentCompany;
	}

	public String getItExperience() {
		return itExperience;
	}

	public void setItExperience(String itExperience) {
		this.itExperience = itExperience;
	}

	public String getJapaneseLevel() {
		return japaneseLevel;
	}

	public void setJapaneseLevel(String japaneseLevel) {
		this.japaneseLevel = japaneseLevel;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	public String getPositionTitle() {
		return positionTitle;
	}

	public void setPositionTitle(String positionTitle) {
		this.positionTitle = positionTitle;
	}

	public String getSourceBy() {
		return sourceBy;
	}

	public void setSourceBy(String sourceBy) {
		this.sourceBy = sourceBy;
	}

	public Date getResumeReceivedDate() {
		return resumeReceivedDate;
	}

	public void setResumeReceivedDate(Date resumeReceivedDate) {
		this.resumeReceivedDate = resumeReceivedDate;
	}

	public String getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getExpectSalary() {
		return expectSalary;
	}

	public void setExpectSalary(String expectSalary) {
		this.expectSalary = expectSalary;
	}

	public String getCurrentSalary() {
		return currentSalary;
	}

	public void setCurrentSalary(String currentSalary) {
		this.currentSalary = currentSalary;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public String getCandidateStatus() {
		return candidateStatus;
	}

	public void setCandidateStatus(String candidateStatus) {
		this.candidateStatus = candidateStatus;
	}

	public Double getRatings() {
		return ratings;
	}

	public void setRatings(Double ratings) {
		this.ratings = ratings;
	}

	public String getVisaStatus() {
		return visaStatus;
	}

	public void setVisaStatus(String visaStatus) {
		this.visaStatus = visaStatus;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getReadyToRelocate() {
		return readyToRelocate;
	}

	public void setReadyToRelocate(String readyToRelocate) {
		this.readyToRelocate = readyToRelocate;
	}

	public String getOfferInHand() {
		return offerInHand;
	}

	public void setOfferInHand(String offerInHand) {
		this.offerInHand = offerInHand;
	}

	public String getPrimarySkill() {
		return primarySkill;
	}

	public void setPrimarySkill(String primarySkill) {
		this.primarySkill = primarySkill;
	}

	public String getSecondarySkill() {
		return secondarySkill;
	}

	public void setSecondarySkill(String secondarySkill) {
		this.secondarySkill = secondarySkill;
	}

	public String getNoOfYearsJapan() {
		return noOfYearsJapan;
	}

	public void setNoOfYearsJapan(String noOfYearsJapan) {
		this.noOfYearsJapan = noOfYearsJapan;
	}

	public Integer getRelevantExperienceTechnology() {
		return relevantExperienceTechnology;
	}

	public void setRelevantExperienceTechnology(Integer relevantExperienceTechnology) {
		this.relevantExperienceTechnology = relevantExperienceTechnology;
	}

	public String getMrOrMs() {
		return mrOrMs;
	}

	public void setMrOrMs(String mrOrMs) {
		this.mrOrMs = mrOrMs;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getInterviewStatus() {
		return interviewStatus;
	}

	public void setInterviewStatus(String interviewStatus) {
		this.interviewStatus = interviewStatus;
	}

	public Long getSecondaryContact() {
		return secondaryContact;
	}

	public void setSecondaryContact(Long secondaryContact) {
		this.secondaryContact = secondaryContact;
	}

	public String getCurrentAddress() {
		return currentAddress;
	}

	public void setCurrentAddress(String currentAddress) {
		this.currentAddress = currentAddress;
	}

	public String getPermanentAddress() {
		return permanentAddress;
	}

	public void setPermanentAddress(String permanentAddress) {
		this.permanentAddress = permanentAddress;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
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
