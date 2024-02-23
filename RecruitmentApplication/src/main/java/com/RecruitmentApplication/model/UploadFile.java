package com.RecruitmentApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name ="upload_file")
public class UploadFile {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	
	private Integer resumeId;
	
	@Column(name="candidate_id")
	private Integer candidateId;
	@Column(name="resume_1")
	private String resume1;
	@Column(name ="resume_2")
	private String resume2;
	@Column(name="resume_3")
	private String resume3;
	@Column(name="resume_4")
	private String resume4;
	@Column(name="resume_5")
	private String resume5;
	@Column(name="resume_6")
	private String resume6;
	
	public UploadFile() {
		super();
	}

	public UploadFile(Integer candidateId, String resume1, String resume2, String resume3, String resume4,
			String resume5, String resume6) {
		super();
		this.candidateId = candidateId;
		this.resume1 = resume1;
		this.resume2 = resume2;
		this.resume3 = resume3;
		this.resume4 = resume4;
		this.resume5 = resume5;
		this.resume6 = resume6;
	}

	public Integer getResumeId() {
		return resumeId;
	}

	public void setResumeId(Integer resumeId) {
		this.resumeId = resumeId;
	}

	public Integer getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(Integer candidateId) {
		this.candidateId = candidateId;
	}

	public String getResume1() {
		return resume1;
	}

	public void setResume1(String resume1) {
		this.resume1 = resume1;
	}

	public String getResume2() {
		return resume2;
	}

	public void setResume2(String resume2) {
		this.resume2 = resume2;
	}

	public String getResume3() {
		return resume3;
	}

	public void setResume3(String resume3) {
		this.resume3 = resume3;
	}

	public String getResume4() {
		return resume4;
	}

	public void setResume4(String resume4) {
		this.resume4 = resume4;
	}

	public String getResume5() {
		return resume5;
	}

	public void setResume5(String resume5) {
		this.resume5 = resume5;
	}

	public String getResume6() {
		return resume6;
	}

	public void setResume6(String resume6) {
		this.resume6 = resume6;
	}

	
		
  
}
