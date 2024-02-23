package com.RecruitmentApplication.service;


import java.io.IOException;
import java.io.OutputStream;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RecruitmentApplication.controller.InterviewController;
import com.RecruitmentApplication.exception.ResourceNotFoundException;
import com.RecruitmentApplication.model.*;
import com.RecruitmentApplication.repository.*;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;


@Service
public class InterviewService {
		
	@Autowired
	 private InterviewRepository interviewRepository;
	@Autowired
	private RecruitmentApplicationRepository recruitmentapplicationRepository;
	@Autowired 
	private JobOpeningRepository jobopeningrepository;
	@Autowired 
	private GoogleCalendarService googleCalendarService;
	@Autowired
	private InterviewRepository interviewrepository;
	

	
	public List<Interview>getAllInterview(){
		try { 
		return (List<Interview>)interviewRepository.getinterview();
		
	}catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to get  interview List Data from database:\t" + e.getMessage());
		
	}
	}
	public List<Interview>getAllInterviewbyuser(Integer userId){
		try { 
		return (List<Interview>)interviewRepository.getinterviewbyuser(userId);
		
	}catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to get  interview by user List Data from database:\t" + e.getMessage());
		
	}
	}
	public void addInterview(Map<String,Object> interviewDetails,Integer userId) {
		try {
		
			Interview interview=new Interview();
			//System.out.println(interviewDetails);
			Integer candidateId = Integer.parseInt(interviewDetails.get("candidateId").toString());
			Integer jobOpeningId = Integer.parseInt(interviewDetails.get("jobOpeningId").toString());
			String candidateName =  recruitmentapplicationRepository.findCandiateNameByCandiateId(candidateId);
			String clientName = jobopeningrepository.findClientNameByJobOpeningId(jobOpeningId);
			
			CandidateProfiles cp = recruitmentapplicationRepository.findById(candidateId).
					orElseThrow(() -> new ResourceNotFoundException("Candidate not exist with id :" + candidateId));
			interview.setCandidateId(candidateId);
			interview.setJobOpeningId(jobOpeningId);
			interview.setCandidateName(candidateName);
			interview.setInterviewName(interviewDetails.get("interviewName").toString());
			interview.setClientName(clientName);
			interview.setInterviewer(interviewDetails.get("interviewer").toString());	
			interview.setInterviewFrom(interviewDetails.get("interviewFrom").toString());
			interview.setInterviewTo(interviewDetails.get("interviewTo").toString());
			interview.setInterviewLocation(interviewDetails.get("interviewLocation").toString());
			interview.setPostingTitle(interviewDetails.get("postingTitle").toString());
			interview.setInterviewStatus(interviewDetails.get("interviewStatus").toString());
			interview.setClientComments(interviewDetails.get("clientComments").toString());
			interview.setZenitusComments(interviewDetails.get("zenitusComments").toString());
			interview.setMeetingLink(interviewDetails.get("meetingLink").toString());
			interview.setUserId(userId);
			cp.setInterviewStatus(interviewDetails.get("interviewStatus").toString());
			interviewRepository.save(interview);
			recruitmentapplicationRepository.save(cp);
			
		
	}catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to post  interview List Data from database:\t" + e.getMessage());
	}
	}
	
	public void addInterviewAndMail(Map<String,Object> interviewDetails) {
		try {
		
			Interview interview=new Interview();
			//System.out.println(interviewDetails);
			Integer candidateId = Integer.parseInt(interviewDetails.get("candidateId").toString());
			Integer jobOpeningId = Integer.parseInt(interviewDetails.get("jobOpeningId").toString());
			String candidateName =  recruitmentapplicationRepository.findCandiateNameByCandiateId(candidateId);
			String clientName = jobopeningrepository.findClientNameByJobOpeningId(jobOpeningId);
			
			CandidateProfiles cp = recruitmentapplicationRepository.findById(candidateId).
					orElseThrow(() -> new ResourceNotFoundException("Candidate not exist with id :" + candidateId));
			interview.setCandidateId(candidateId);
			interview.setJobOpeningId(jobOpeningId);
			interview.setCandidateName(candidateName);
			interview.setInterviewName(interviewDetails.get("interviewName").toString());
			interview.setClientName(clientName);
			interview.setInterviewer(interviewDetails.get("interviewer").toString());	
			interview.setInterviewFrom(interviewDetails.get("interviewFrom").toString());
			interview.setInterviewTo(interviewDetails.get("interviewTo").toString());
			interview.setInterviewLocation(interviewDetails.get("interviewLocation").toString());
			interview.setPostingTitle(interviewDetails.get("postingTitle").toString());
			interview.setInterviewStatus(interviewDetails.get("interviewStatus").toString());
			interview.setClientComments(interviewDetails.get("clientComments").toString());
			interview.setZenitusComments(interviewDetails.get("zenitusComments").toString());
			interview.setMeetingLink(interviewDetails.get("meetingLink").toString());
			cp.setInterviewStatus(interviewDetails.get("interviewStatus").toString());
			interviewRepository.save(interview);
			recruitmentapplicationRepository.save(cp);
			String  interviewfrom = interview.getInterviewFrom();
			String  interviewto =interview.getInterviewTo();
			String  meetingLink =interview.getMeetingLink();
			String interviewFrom=interviewfrom+"Z";
			String interviewTo=interviewto+"Z";
			String candidateemail =cp.getEmailId();
			String candidatename=cp.getFirstName();
			//System.out.println("formate for from: "+interviewDetails.get("interviewFrom").toString());
			//System.out.println("formate for TO: "+interviewDetails.get("interviewTo").toString());
			//String intervieweremail=interview.getInterviewer();
			googleCalendarService.googleCalenderService(candidateemail,interviewFrom,interviewTo,candidatename,meetingLink);
			
			
		
	}catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to post  interview List Data from database:\t" + e.getMessage());
	}
	}

	
	public Interview getInterviewById(Integer interviewId) {
		try {
		Interview interview = interviewRepository.findById(interviewId).
				orElseThrow(() -> new ResourceNotFoundException("interview not exist with id :" + interviewId));
		return interview;
		
	}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to get  interview List Data from database:\t" + e.getMessage());
	
		}
		}
	
	public void updateInterview(Integer interviewId,Interview interviewDetails ){
		try {
			Interview interview = interviewRepository.findById(interviewId).
					  orElseThrow(() -> new ResourceNotFoundException("Interview not exist with id :" + interviewId));
			
			Integer candidateId = interviewDetails.getCandidateId();
			
			CandidateProfiles cp = recruitmentapplicationRepository.findById(candidateId).
					orElseThrow(() -> new ResourceNotFoundException("Candidate not exist with id :" + candidateId));
			
			interview.setCandidateId(interviewDetails.getCandidateId());
			interview.setJobOpeningId(interviewDetails.getJobOpeningId());
			interview.setCandidateName(interviewDetails.getCandidateName());
			interview.setInterviewName(interviewDetails.getInterviewName());
			interview.setClientName(interviewDetails.getClientName());
			interview.setInterviewer(interviewDetails.getInterviewer());
			interview.setInterviewFrom(interviewDetails.getInterviewFrom());
			interview.setInterviewTo(interviewDetails.getInterviewTo());
			interview.setInterviewLocation(interviewDetails.getInterviewLocation());
			interview.setPostingTitle(interviewDetails.getPostingTitle());
			interview.setInterviewStatus(interviewDetails.getInterviewStatus());
			interview.setClientComments(interviewDetails.getClientComments());
			interview.setZenitusComments(interviewDetails.getZenitusComments());
			interview.setMeetingLink(interviewDetails.getMeetingLink());
			cp.setInterviewStatus(interviewDetails.getInterviewStatus());
			
			Interview updatedInterview = interviewRepository.save(interview);
			recruitmentapplicationRepository.save(cp);
			
	}catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to delete candidate List Data from database:\t" + e.getMessage());
	}
}
	
	public void deleteInterview( Integer interviewId){
		try {
		Interview interview = interviewRepository.findById(interviewId)
				.orElseThrow(() -> new ResourceNotFoundException("Interview not exist with id :" + interviewId));
		
		interview.setDeleted(true);
        interviewRepository.save(interview);
//		interviewRepository.delete(interview);
//		Map<String,Boolean> response = new HashMap<>();
//		response.put("deleted",Boolean.TRUE);
		
		
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to delete candidate List Data from database:\t" + e.getMessage());
		}
		
	}
	public void deleteInterviewWithEmail( Integer interviewId,Interview interviewDetails){
		try {
		
		Interview interview = interviewRepository.findById(interviewId)
				.orElseThrow(() -> new ResourceNotFoundException("Interview not exist with id :" + interviewId));
		
		interview.setDeleted(true);
		String  interviewfrom = interview.getInterviewFrom();
		String  interviewto =interview.getInterviewTo();
		String  meetingLink= interview.getMeetingLink();
		String  ReasonforCancelation=interviewDetails.getReasonOfCancelation();
		Integer candidateIdinInterview=interviewRepository.findByCandidateId(interviewId);
		//Integer candidateId = recruitmentapplicationRepository.findByName(candidateIdinInterview);
	    String  candidateEmail=recruitmentapplicationRepository.findByEmail(candidateIdinInterview);
		
		googleCalendarService.googleCalenderDeleteService(candidateEmail,interviewfrom,interviewto,meetingLink,ReasonforCancelation);
        interviewRepository.save(interview);
//		interviewRepository.delete(interview);
//		Map<String,Boolean> response = new HashMap<>();
//		response.put("deleted",Boolean.TRUE);
		
		
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to delete candidate List Data from database:\t" + e.getMessage());
		}
		
	}
	
	public List<Interview> getAllInterviewHistory(Integer candidateId) {
	 return interviewRepository.findAllByCandidateId(candidateId);
	 
	}
	
	public List<Interview>getAllInterviewexportcsv(){
		try {
		return (List<Interview>)interviewRepository.getinterviewexportcsv();
		
	}catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to get  interview List Data from database:\t" + e.getMessage());
		
	}
	}
	public List<Interview>getAllInterviewexportcsvbyuser(Integer userId){
		try {
		return (List<Interview>)interviewRepository.getinterviewexportcsvbyuser(userId);
		
	}catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to get  interview List Data from database:\t" + e.getMessage());
		
	}
	}
}
