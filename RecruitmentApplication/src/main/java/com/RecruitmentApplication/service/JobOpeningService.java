package com.RecruitmentApplication.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.RecruitmentApplication.exception.ResourceNotFoundException;
import com.RecruitmentApplication.model.CandidateProfiles;
import com.RecruitmentApplication.model.JobOpening;
import com.RecruitmentApplication.repository.JobOpeningRepository;


@Service
public class JobOpeningService {
	
	@Autowired
	private JobOpeningRepository jobopeningrepository;
	
	// To Get all Job Opening Details
	
	public List<JobOpening> getAlldetails() 
	{
		try {
			
		//System.out.println(jobopeningrepository.findAll());
		return (List<JobOpening>) jobopeningrepository.getjobopening();
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to Get All JobOpening Details from database:\t" + e.getMessage());
		}
		
	}
	public List<JobOpening> getAlldetailsbyuser(Integer userId) 
	{
		try {
			
		//System.out.println(jobopeningrepository.findAll());
		return (List<JobOpening>) jobopeningrepository.getjobopeningbyuser(userId);
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to Get All JobOpening Details by user from database:\t" + e.getMessage());
		}
		
	}
	
	// To Create a Job Opening Details
	
	public void CreateJob(JobOpening jobopening,Integer userId) {
		try {
			
			jobopening.setUserId(userId);
			jobopeningrepository.save(jobopening);
		} 
	     
	     catch (Exception e) {
				// TODO: handle exception
	    	 throw new RuntimeException("Failed to Create a JobOpening :\t" + e.getMessage());
			}
	}
	
	// To Delete a Job Opening Detail by ID
	
	public void DeleteJobOpeningById(Integer JobOpeningId) {
		
		try {
			JobOpening jobopening = jobopeningrepository.findById(JobOpeningId)
					.orElseThrow(() -> new ResourceNotFoundException("Job opening not exist with id :" + JobOpeningId));
			
			jobopening.setDeleted(true);
	        jobopeningrepository.save(jobopening);
//			jobopeningrepository.deleteById(JobOpeningId);	
//			Map<String,Boolean> response = new HashMap<>();
//			response.put("deleted",Boolean.TRUE);
//	
		} 
	     
	     catch (Exception e) {
				// TODO: handle exception
	    	 throw new RuntimeException("Failed to Delete the JobOpening Detail:\t" + e.getMessage());
			}
		
	}
    
	// To Get a Job Opening Detail by ID
	
     public Optional<JobOpening> callId(Integer j) {
    	 try
    	 {
    		 return jobopeningrepository.findById(j);	
		 } 
		
		catch (Exception e) {

			throw new RuntimeException("Failed to Get the JobOpening Detail by ID :\t" + e.getMessage());
		}
     }
     
     // To Update Job Opening Detail by ID
     
     public ResponseEntity<JobOpening>  UpdateJob(Integer Job_Opening_Id,JobOpening jobopeningdetails)
     {
    	 try {
			
        JobOpening jobopening=jobopeningrepository.findById(Job_Opening_Id).orElseThrow(() -> new ResourceNotFoundException("CandidateProfiles not exist with id :" + Job_Opening_Id));
        jobopening.setClientName(jobopeningdetails.getClientName());
        jobopening.setPositionTitle(jobopeningdetails.getPositionTitle());
	    jobopening.setSkillset(jobopeningdetails.getSkillset()); 		
	    jobopening.setExperience(jobopeningdetails.getExperience());
	    jobopening.setJapaneseLevel(jobopeningdetails.getJapaneseLevel());
		jobopening.setNoOfOpenings(jobopeningdetails.getNoOfOpenings());
		jobopening.setCountry(jobopeningdetails.getCountry());
		jobopening.setSalary(jobopeningdetails.getSalary());
		jobopening.setStateName(jobopeningdetails.getStateName());
		jobopening.setJobOpeningStatus(jobopeningdetails.getJobOpeningStatus());
		jobopening.setAssignedBy(jobopeningdetails.getAssignedBy());
		jobopening.setJobType(jobopeningdetails.getJobType());
		jobopening.setCity(jobopeningdetails.getCity());
		jobopening.setJobDescription(jobopeningdetails.getJobDescription());
		jobopening.setRequirementReceivedDate(jobopeningdetails.getRequirementReceivedDate());
 		
 	  JobOpening updatedjob = jobopeningrepository.save(jobopening);
 	  return ResponseEntity.ok(updatedjob);
    	 }
 	 catch (Exception e) {
			// TODO: handle exception
 		throw new RuntimeException("Failed to Update the JobOpening Detail by ID :\t" + e.getMessage());
		}
	}
     public List<JobOpening> exportcsv() 
 	{
 		try {
 			
 		//System.out.println(jobopeningrepository.findAll());
 		return (List<JobOpening>) jobopeningrepository.getexportcsv();
 		}
 		catch (Exception e) {
 			// TODO: handle exception
 			throw new RuntimeException("Failed to Get All JobOpening Details from database:\t" + e.getMessage());
 		}
 	}
 	public List<JobOpening> exportcsvbyuser(Integer userId) 
 	 	{
 	 		try {
 	 			
 	 		//System.out.println(jobopeningrepository.findAll());
 	 		return (List<JobOpening>) jobopeningrepository.getexportcsvbyuser(userId);
 	 		}
 	 		catch (Exception e) {
 	 			// TODO: handle exception
 	 			throw new RuntimeException("Failed to Get All JobOpening Details from database:\t" + e.getMessage());
 	 		}
 	 		
 	 	}
 	
 	
}
