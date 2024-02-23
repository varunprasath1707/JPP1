package com.RecruitmentApplication.model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="interview")
public class Interview {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private Integer interviewId;
	
	@Column(name="job_opening_id")
	private Integer jobOpeningId;
	
	@Column(name="candidate_id")
	private Integer candidateId;
	
	@Column(name="interview_name")
	private String interviewName;
	
	@Column(name="candidate_name")
	private String candidateName;
	
	@Column(name="posting_title")
	private String postingTitle;
	
	@Column(name="interview_from")
	private String interviewFrom;
	
	@Column(name="interview_to")
	private String interviewTo;
	
	@Column(name="interview_location")
	private String interviewLocation;
	
	@Column(name="client_name")
	private String clientName;
	
	@Column(name="zenitus_comments")
	private String zenitusComments;
	
	@Column(name="client_comments")
	private String clientComments;
	
	@Column(name="interview_status")
	private String interviewStatus;
	
	@Column(name="deleted")
	private Boolean deleted =false;
	
	@Column(name="interviewer")
	private String interviewer;
	
	@Column(name="meeting_link")
	private String meetingLink;
	
	@Column(name="reason_of_canclation")
	private String reasonOfCancelation;
	
	 @Column(name="user_id")
		private Integer userId;
	
	

	public Interview() {
		super();
	}

	public Interview(Integer jobOpeningId, Integer candidateId, String interviewName, String candidateName,
			String postingTitle, String interviewFrom, String interviewTo, String interviewLocation, String clientName,
			String zenitusComments, String clientComments, String interviewStatus, Boolean deleted, String interviewer,
			String meetingLink, String reasonOfCancelation,Integer userId) {
		super();
		this.jobOpeningId = jobOpeningId;
		this.candidateId = candidateId;
		this.interviewName = interviewName;
		this.candidateName = candidateName;
		this.postingTitle = postingTitle;
		this.interviewFrom = interviewFrom;
		this.interviewTo = interviewTo;
		this.interviewLocation = interviewLocation;
		this.clientName = clientName;
		this.zenitusComments = zenitusComments;
		this.clientComments = clientComments;
		this.interviewStatus = interviewStatus;
		this.deleted = deleted;
		this.interviewer = interviewer;
		this.meetingLink = meetingLink;
		this.reasonOfCancelation = reasonOfCancelation;
		this.userId=userId;
	}

	public Integer getInterviewId() {
		return interviewId;
	}

	public void setInterviewId(Integer interviewId) {
		this.interviewId = interviewId;
	}

	public Integer getJobOpeningId() {
		return jobOpeningId;
	}

	public void setJobOpeningId(Integer jobOpeningId) {
		this.jobOpeningId = jobOpeningId;
	}

	public Integer getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(Integer candidateId) {
		this.candidateId = candidateId;
	}

	public String getInterviewName() {
		return interviewName;
	}

	public void setInterviewName(String interviewName) {
		this.interviewName = interviewName;
	}

	public String getCandidateName() {
		return candidateName;
	}

	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}

	public String getPostingTitle() {
		return postingTitle;
	}

	public void setPostingTitle(String postingTitle) {
		this.postingTitle = postingTitle;
	}

	public String getInterviewFrom() {
		return interviewFrom;
	}

	public void setInterviewFrom(String interviewFrom) {
		this.interviewFrom = interviewFrom;
	}

	public String getInterviewTo() {
		return interviewTo;
	}

	public void setInterviewTo(String interviewTo) {
		this.interviewTo = interviewTo;
	}

	public String getInterviewLocation() {
		return interviewLocation;
	}

	public void setInterviewLocation(String interviewLocation) {
		this.interviewLocation = interviewLocation;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getZenitusComments() {
		return zenitusComments;
	}

	public void setZenitusComments(String zenitusComments) {
		this.zenitusComments = zenitusComments;
	}

	public String getClientComments() {
		return clientComments;
	}

	public void setClientComments(String clientComments) {
		this.clientComments = clientComments;
	}

	public String getInterviewStatus() {
		return interviewStatus;
	}

	public void setInterviewStatus(String interviewStatus) {
		this.interviewStatus = interviewStatus;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getInterviewer() {
		return interviewer;
	}

	public void setInterviewer(String interviewer) {
		this.interviewer = interviewer;
	}

	public String getMeetingLink() {
		return meetingLink;
	}

	public void setMeetingLink(String meetingLink) {
		this.meetingLink = meetingLink;
	}

	public String getReasonOfCancelation() {
		return reasonOfCancelation;
	}

	public void setReasonOfCancelation(String reasonOfCancelation) {
		this.reasonOfCancelation = reasonOfCancelation;
	}
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	
	
	

}
