package com.RecruitmentApplication.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.RecruitmentApplication.model.*;
import com.RecruitmentApplication.repository.*;
import com.RecruitmentApplication.exception.*;



@Service
public class RecruitmentApplicationService {
	
	@Autowired
	private RecruitmentApplicationRepository recruitmentapplicationRepository;
	
	@Autowired
	private UploadService uploadService;
	
	public List<CandidateProfiles>getAllCandidateProfiles(){
	try {
		return recruitmentapplicationRepository.getcandidate(); 
		
	}
	
	catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to get candidate List Data from database:\t" + e.getMessage());
	}
	}
	public List<CandidateProfiles>getAllCandidateProfilesbyuser(Integer userId){
		try {
			return recruitmentapplicationRepository.getcandidatebyuser(userId); 
			
		}
		
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to get candidate by user List Data from database:\t" + e.getMessage());
		}
		}
	public void addCandidateProfiles(CandidateProfiles candidateProfiles,List<MultipartFile> multipartFiles,Integer userId){
		try {
			Integer email=recruitmentapplicationRepository.findByEmailId(candidateProfiles.getEmailId());
			Integer  mobile=recruitmentapplicationRepository.findByPhoneNumber(candidateProfiles.getPrimaryContact());
			//System.out.println(email);
			if(email == null && mobile==null)
			{
			   candidateProfiles.setUserId(userId);
		       recruitmentapplicationRepository.save(candidateProfiles);
		       candidateProfiles.getCandidateId();
		      // System.out.println(candidateProfiles.getCandidateId());
		      
		       uploadService.addResume(multipartFiles, candidateProfiles.getCandidateId());		      
		      
			}
			else
				{
				throw new RuntimeException("email or phone number is already exists");
				}
			
	} catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to post candidate List Data from database:\t" + e.getMessage());
	}
	}
	
	
//	private Exception RuntimeException(ResponseEntity<String> body) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	public CandidateProfiles getCandidateProfilesById( Integer candidateId){
		
		try {
		CandidateProfiles candidateProfiles = recruitmentapplicationRepository.findById(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("CandidateProfiles not exist with id :" + candidateId));
		 return candidateProfiles;
	} catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to get candidate List Data from database:\t" + e.getMessage());
	}}
	
public void updateCandidateProfiles(Integer candidateId,CandidateProfiles candidateProfilesDetails){
		
		try {
		CandidateProfiles candidateProfiles = recruitmentapplicationRepository.findById(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("CandidateProfiles not exist with id :" + candidateId));
		
		candidateProfiles.setFirstName(candidateProfilesDetails.getFirstName());
		candidateProfiles.setLastName(candidateProfilesDetails.getLastName());
		candidateProfiles.setGender(candidateProfilesDetails.getGender());
		candidateProfiles.setEmailId(candidateProfilesDetails.getEmailId());
		candidateProfiles.setPrimaryContact(candidateProfilesDetails.getPrimaryContact());
		candidateProfiles.setNationality(candidateProfilesDetails.getNationality());
		candidateProfiles.setCurrentCompany(candidateProfilesDetails.getCurrentCompany());
		candidateProfiles.setItExperience(candidateProfilesDetails.getItExperience());
		candidateProfiles.setJapaneseLevel(candidateProfilesDetails.getJapaneseLevel());
		candidateProfiles.setCurrentLocation(candidateProfilesDetails.getCurrentLocation());
		candidateProfiles.setPositionTitle(candidateProfilesDetails.getPositionTitle());
		candidateProfiles.setSourceBy(candidateProfilesDetails.getSourceBy());
		candidateProfiles.setResumeReceivedDate(candidateProfilesDetails.getResumeReceivedDate());
		candidateProfiles.setNoticePeriod(candidateProfilesDetails.getNoticePeriod());
		candidateProfiles.setAdditionalInfo(candidateProfilesDetails.getAdditionalInfo());
		candidateProfiles.setDateOfBirth(candidateProfilesDetails.getDateOfBirth());
		candidateProfiles.setQualification(candidateProfilesDetails.getQualification());
		candidateProfiles.setInstitution(candidateProfilesDetails.getInstitution());
		candidateProfiles.setCity(candidateProfilesDetails.getCity());
		candidateProfiles.setCountry(candidateProfilesDetails.getCountry());
		candidateProfiles.setStateName(candidateProfilesDetails.getStateName());
		candidateProfiles.setZipCode(candidateProfilesDetails.getZipCode());
		candidateProfiles.setExpectSalary(candidateProfilesDetails.getExpectSalary());
		candidateProfiles.setCurrentSalary(candidateProfilesDetails.getCurrentSalary());
		candidateProfiles.setResume(candidateProfilesDetails.getResume());
		candidateProfiles.setCandidateStatus(candidateProfilesDetails.getCandidateStatus());
		candidateProfiles.setRatings(candidateProfilesDetails.getRatings());
		candidateProfiles.setVisaStatus(candidateProfilesDetails.getVisaStatus());
		candidateProfiles.setMaritalStatus(candidateProfilesDetails.getMaritalStatus());
		candidateProfiles.setReadyToRelocate(candidateProfilesDetails.getReadyToRelocate());
		candidateProfiles.setOfferInHand(candidateProfilesDetails.getOfferInHand());
		candidateProfiles.setPrimarySkill(candidateProfilesDetails.getPrimarySkill());
		candidateProfiles.setSecondarySkill(candidateProfilesDetails.getSecondarySkill());
		candidateProfiles.setNoOfYearsJapan(candidateProfilesDetails.getNoOfYearsJapan());
		candidateProfiles.setRelevantExperienceTechnology(candidateProfilesDetails.getRelevantExperienceTechnology());
		candidateProfiles.setCountryCode(candidateProfilesDetails.getCountryCode());
		candidateProfiles.setMrOrMs(candidateProfilesDetails.getMrOrMs());
		candidateProfiles.setInterviewStatus(candidateProfilesDetails.getInterviewStatus());
		candidateProfiles.setSecondaryContact(candidateProfilesDetails.getSecondaryContact());
		candidateProfiles.setCurrentAddress(candidateProfilesDetails.getCurrentAddress());
		candidateProfiles.setPermanentAddress(candidateProfilesDetails.getPermanentAddress());
		candidateProfiles.setJobType(candidateProfilesDetails.getJobType());
		candidateProfiles.setLinkedin(candidateProfilesDetails.getLinkedin());
		
		
		CandidateProfiles updatedCandidateProfiles = recruitmentapplicationRepository.save(candidateProfiles);
	
	    
		
	}catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to update candidate List Data from database:\t" + e.getMessage());
	}
		}
	
	
//	public void updateCandidateProfiles( Integer candidateId, CandidateProfiles candidateProfilesDetails,List<MultipartFile> multipartFiles){
//		
//		try {
//		CandidateProfiles candidateProfiles = recruitmentapplicationRepository.findById(candidateId)
//				.orElseThrow(() -> new ResourceNotFoundException("CandidateProfiles not exist with id :" + candidateId));
//		
//		candidateProfiles.setFirstName(candidateProfilesDetails.getFirstName());
//		candidateProfiles.setLastName(candidateProfilesDetails.getLastName());
//		candidateProfiles.setGender(candidateProfilesDetails.getGender());
//		candidateProfiles.setEmailId(candidateProfilesDetails.getEmailId());
//		candidateProfiles.setPhoneNumber(candidateProfilesDetails.getPhoneNumber());
//		candidateProfiles.setNationality(candidateProfilesDetails.getNationality());
//		candidateProfiles.setCurrentCompany(candidateProfilesDetails.getCurrentCompany());
//		candidateProfiles.setItExperience(candidateProfilesDetails.getItExperience());
//		candidateProfiles.setJapaneseLevel(candidateProfilesDetails.getJapaneseLevel());
//		candidateProfiles.setCurrentLocation(candidateProfilesDetails.getCurrentLocation());
//		candidateProfiles.setPositionTitle(candidateProfilesDetails.getPositionTitle());
//		candidateProfiles.setSourceBy(candidateProfilesDetails.getSourceBy());
//		candidateProfiles.setResumeReceivedDate(candidateProfilesDetails.getResumeReceivedDate());
//		candidateProfiles.setNoticePeriod(candidateProfilesDetails.getNoticePeriod());
//		candidateProfiles.setAdditionalInfo(candidateProfilesDetails.getAdditionalInfo());
//		candidateProfiles.setDateOfBirth(candidateProfilesDetails.getDateOfBirth());
//		candidateProfiles.setQualification(candidateProfilesDetails.getQualification());
//		candidateProfiles.setInstitution(candidateProfilesDetails.getInstitution());
//		candidateProfiles.setCity(candidateProfilesDetails.getCity());
//		candidateProfiles.setCountry(candidateProfilesDetails.getCountry());
//		candidateProfiles.setStateName(candidateProfilesDetails.getStateName());
//		candidateProfiles.setZipCode(candidateProfilesDetails.getZipCode());
//		candidateProfiles.setExpectSalary(candidateProfilesDetails.getExpectSalary());
//		candidateProfiles.setCurrentSalary(candidateProfilesDetails.getCurrentSalary());
//		candidateProfiles.setResume(candidateProfilesDetails.getResume());
//		candidateProfiles.setCandidateStatus(candidateProfilesDetails.getCandidateStatus());
//		candidateProfiles.setRatings(candidateProfilesDetails.getRatings());
//		candidateProfiles.setVisaStatus(candidateProfilesDetails.getVisaStatus());
//		candidateProfiles.setMaritalStatus(candidateProfilesDetails.getMaritalStatus());
//		candidateProfiles.setReadyToRelocate(candidateProfilesDetails.getReadyToRelocate());
//		candidateProfiles.setOfferInHand(candidateProfilesDetails.getOfferInHand());
//		candidateProfiles.setPrimarySkill(candidateProfilesDetails.getPrimarySkill());
//		candidateProfiles.setSecondarySkill(candidateProfilesDetails.getSecondarySkill());
//		candidateProfiles.setNoOfYearsJapan(candidateProfilesDetails.getNoOfYearsJapan());
//		candidateProfiles.setRelevantExperienceTechnology(candidateProfilesDetails.getRelevantExperienceTechnology());
//		candidateProfiles.setCountryCode(candidateProfilesDetails.getCountryCode());
//		candidateProfiles.setMrOrMs(candidateProfilesDetails.getMrOrMs());
//		
//		
//		CandidateProfiles updatedCandidateProfiles = recruitmentapplicationRepository.save(candidateProfiles);
//		uploadService.addResume(multipartFiles,candidateId);	
//	    
//		
//	}catch (Exception e) {
//		// TODO: handle exception
//		throw new RuntimeException("Failed to update candidate List Data from database:\t" + e.getMessage());
//	}
//		}

//public void updateResume(List<MultipartFile>multipartFiles,Integer candidateId) {
//	 
//	 try {
//		  
//		 uploadService.addResume(multipartFiles,candidateId);
//		 
//	 }catch (Exception e) {
//		// TODO: handle exception
//		throw new RuntimeException("Failed to update resume List Data from database:\t" + e.getMessage());
//}
//}
   public void updateResume(MultipartFile multipartFile,Integer candidateId,String fileName) {
	try {
	
	uploadService.updateResume(multipartFile, candidateId, fileName);
	
    }catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to update resume List Data from database:\t" + e.getMessage());
   }
  }
   public void addUpdateResume( List<MultipartFile> multipartFiles,Integer candidateId) {
		try {
		
		uploadService.addExtraResumes(multipartFiles, candidateId);
		
	    }catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to update resume List Data from database:\t" + e.getMessage());
	   }
	  }
   
   
	public void deleteCandidateProfiles(Integer candidateId){
		try {	
		CandidateProfiles candidateProfiles = recruitmentapplicationRepository.findById(candidateId)
				.orElseThrow(() -> new ResourceNotFoundException("CandidateProfiles not exist with id :" + candidateId));
		
		candidateProfiles.setDeleted(true);
        recruitmentapplicationRepository.save(candidateProfiles);  //set the deleted row is true it means the data deleted from the front end 
//		recruitmentapplicationRepository.delete(candidateProfiles);
//		Map<String,Boolean> response = new HashMap<>();
//		response.put("deleted",Boolean.TRUE);
//		
		
		} 
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to delete candidate List Data from database:\t" + e.getMessage());
		}
		
	}
	
	public void resumeRemove(Integer candidateId,String fileName) {
		try {
			
			uploadService.deleteResume(candidateId, fileName);
		
	 }catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to remove resume List Data from database:\t" + e.getMessage());
   }
}
	public List<CandidateProfiles>exportcsv(){
		try {
			return recruitmentapplicationRepository.getcandidatecsv(); 
			
		}
		
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to get candidate List Data from database:\t" + e.getMessage());
		}
		}
	public List<CandidateProfiles>exportcsvbyuser(Integer userId){
		try {
			return recruitmentapplicationRepository.getcandidatecsvbyuser(userId); 
			
		}
		
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to get candidate List Data from database:\t" + e.getMessage());
		}
		}
}
