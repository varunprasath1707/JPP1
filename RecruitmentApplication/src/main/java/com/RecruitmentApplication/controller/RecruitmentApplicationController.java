package com.RecruitmentApplication.controller;

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.catalina.mapper.Mapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.RecruitmentApplication.model.*;
import com.RecruitmentApplication.repository.*;
import com.RecruitmentApplication.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;

import jakarta.servlet.http.HttpServletResponse;


@CrossOrigin(exposedHeaders = "Content-Disposition")
@RestController
// Base URL for All APIs in Candidate profiles page
@RequestMapping("/api/v1/")
public class RecruitmentApplicationController {
	
	@Autowired
	private RecruitmentApplicationService recruitmentapplicationService;
	
	@Autowired
	private SessionTokenRepository SessionTokenRepository;
	
	@Autowired
	private RecruitmentApplicationRepository recruitmentapplicationrepository;
	
	@Autowired
	private UploadService uploadService;
	
	@Autowired
	private RecentactivitiesRepository recentactivitiesRepository;
	
	@Autowired
	private LoginCredentialsRepository loginCredentialsRepository;
	
	
	@Value("${upload.directory}")
	private String uploadDirectory;
	
	private ObjectMapper mapper =  new ObjectMapper();


	 // API for Get(read) all details from database
	
	 // http://localhost:8080/api/v1/candidateProfiles
	
	@GetMapping("/candidateProfiles")  //URL for Get API
	public ResponseEntity<?> getAllCandidateProfiles (@RequestHeader(name = "Authorization") String session){

		try
		{
		if(session.equals(null) || session.isEmpty())
		{
			System.out.println("Session token is empty!");
			return null;
		}
		
		Integer userId = SessionTokenRepository.findByAllSessionToken(session);
	    String stoken = SessionTokenRepository.getSessionTokenById(userId);
		if(!session.equals(stoken)) {
          throw new RuntimeException();
		}
		Integer loginId = userId;
		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		String roles = user.getUserRole();
		
		if(roles.equals("admin") || roles.equals("zenuser"))
		{
		
			List<CandidateProfiles> candidate = recruitmentapplicationService.getAllCandidateProfiles();
			if(candidate.isEmpty())
			{
				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-CANDIDATE-DETAILS   STATUS: "+HttpStatus.NO_CONTENT);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No Candidate Profile Found");
			}
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-CANDIDATE-DETAILS   STATUS: "+ HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body(candidate);
			}else {
				List<CandidateProfiles> candidate = recruitmentapplicationService.getAllCandidateProfilesbyuser(userId);
				if(candidate.isEmpty())
				{
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-CANDIDATE-DETAILS-BY-USER  STATUS: "+HttpStatus.NO_CONTENT);
					return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No Candidate Profile Found");
				}
				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-CANDIDATE-DETAILS   STATUS: "+ HttpStatus.OK);
				return ResponseEntity.status(HttpStatus.OK).body(candidate);
				
			}
			}
			//return JobopeningService.getAlldetails();
		
		   catch (RuntimeException e) {
			   System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-ALL-CANDIDATE-DETAILS   STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Somthing went wrong :"+e);
			   
		}
			catch (Exception e) {
				// TODO: handle exception
				//String message="something went Wrong"+e.getMessage();
				System.out.println(new Timestamp(System.currentTimeMillis()).toString() +"\t\tUSERID: "+"\tAPI:\tGET-ALL-CANDIDATE-DETAILS   STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
				return ResponseEntity
						  .status(HttpStatus.INTERNAL_SERVER_ERROR)
						  .body("Somthing went wrong :"+e);
			}
		
		
	}
	
	// API for Post(insert) the details in the database
	
	// http://localhost:8080/api/v1/candidateProfiles
	
	@PostMapping("/candidateProfiles")
    public ResponseEntity<?> addCandidateprofiles(@RequestPart("files")List<MultipartFile> multipartFiles,@RequestPart("candidateProfiles") String candidateProfiles,@RequestHeader(name = "Authorization") String session){
					
		 
		if(session.equals(null) || session.isEmpty()) 
		{
			System.out.println("Session token is empty!");
			return null;
		}
		
		Integer userId = SessionTokenRepository.findByAllSessionToken(session);
		String stoken = SessionTokenRepository.getSessionTokenById(userId);
		//System.out.println("IN DB ="+stoken);
		//System.out.println("Input = "+session );
		if(!session.equals(stoken)) {
			System.out.println("Session token is Not equal!");
			return null;
		}
		try
	    {
			CandidateProfiles candidateProfile = mapper.readValue(candidateProfiles, CandidateProfiles.class);
	       	recruitmentapplicationService.addCandidateProfiles(candidateProfile,multipartFiles,userId);
	       	
			Recentactivities recentactivities = new Recentactivities();
	 		Integer loginId = userId;
	 		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
	 		String username = user.getUsername();
	 		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
	 		recentactivities.setMessage( username+" added 1 candidate through the Add Candidate");
	 		recentactivitiesRepository.save(recentactivities);
	       	
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tPOST-CANDIDATE-DETAIL  STATUS: " + HttpStatus.OK);
	    	return ResponseEntity.status(HttpStatus.OK).body("Candidate inserted Successfully" );
	    	
		} 
		 catch (JsonProcessingException e) {
			    e.printStackTrace();
			    return ResponseEntity
						  .status(HttpStatus.INTERNAL_SERVER_ERROR)
						  .body("Somthing went wrong:"+e);
			    // you can also return a specific error response here
		}
	    catch (Exception e) {
	    	//System.out.println("--------");
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tPOST-CANDIDATE-DETAIL STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Somthing went wrong:"+e);
		}		
}
	
	// API for Get(read) the details by Id from the database
	
	// http://localhost:8080/api/v1/candidateProfiles/1
	
	@GetMapping("/candidateProfiles/{candidateId}")
	public  ResponseEntity<?> getCandidateProfilesById(@PathVariable Integer candidateId,@RequestHeader(name = "Authorization") String session){
		try 
		{
			
		//if(!recruitmentapplicationService.isCandiIdExists(candidateId)) {
//			return ResponseEntity
//				.status(HttpStatus.BAD_REQUEST)
//					.body("Invalid session token.");
//		}
		
		if(session.equals(null) || session.isEmpty())
		{
			System.out.println("Session token is empty!");
			return null;
		}
		Integer userId = SessionTokenRepository.findByAllSessionToken(session);
		String stoken = SessionTokenRepository.getSessionTokenById(userId);
		//System.out.println("IN DB ="+stoken);
		//System.out.println("Input = "+session );
		if(!session.equals(stoken)) {

			throw new RuntimeException();
		}
		
		
			CandidateProfiles candidate = recruitmentapplicationService.getCandidateProfilesById(candidateId) ;	
			//System.out.println(candidate);
			if(candidate==null)
			{
				throw new RuntimeException("candidate details not found"); 
				//return ResponseEntity.noContent().build();
				//return ResponseEntity.status(HttpStatus.OK).body(candidate);
				
			}
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body(candidate);
			
			
	    }
		catch (RuntimeException e) {
			// TODO: handle exception
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-CANDIDATE-DETAIL-BY-ID:"+candidateId+" STATUS: " + HttpStatus.NO_CONTENT);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("candidate id is not found/Session token is not equal:"+e);
		}
		catch (Exception e) 
		{
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-CANDIDATE-DETAIL-BY-ID:"+candidateId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Somthing went wrong:"+e);
		}
		
	}
	

	// API for Put(update) the details in the database
	
	// http://localhost:8080/api/v1/candidateProfiles/1
	
	@PutMapping("/candidateProfiles/{candidateId}")
	public ResponseEntity<?> updateCandidateProfiles(@PathVariable Integer candidateId,@RequestBody CandidateProfiles candidateProfilesDetails,@RequestHeader(name = "Authorization") String session) {
		try 
		{
		if(session.equals(null) || session.isEmpty()) 
		{
			System.out.println("Session token is empty!");
			return null;
		}
		Integer userId = SessionTokenRepository.findByAllSessionToken(session);
		String stoken = SessionTokenRepository.getSessionTokenById(userId);
		//System.out.println("IN DB ="+stoken);
		//System.out.println("Input = "+session );
		
		if(!session.equals(stoken)) {
//			System.out.println("Session token is Not equal!");
//			return null;
			throw new RuntimeException();
		}
		 Optional<CandidateProfiles> Candidatepro=recruitmentapplicationrepository.findById(candidateId);
			
 		if (Candidatepro.isEmpty()) {
             throw new RuntimeException("Candidate not found!");
         }
		
			recruitmentapplicationService.updateCandidateProfiles(candidateId, candidateProfilesDetails);
			
			Recentactivities recentactivities = new Recentactivities();
     		Integer loginId = userId;
     		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
     		String username = user.getUsername();
     		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
     		recentactivities.setMessage( username+" updated 1 candidate through the Edit");
     		recentactivitiesRepository.save(recentactivities);
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tPUT-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body("Candidate Updated Successfully");
			
		} 
		 catch (RuntimeException e) 
		{
		    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPUT-CANDIDATE-DETAIL-BY-ID: " + candidateId + " STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("candidate id is not found/Session token is not equal:"+e);
		}
         
	    catch (Exception e) 
		{
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPUT-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		  return ResponseEntity
				  .status(HttpStatus.INTERNAL_SERVER_ERROR)
				  .body("Somthing went wrong:"+e);
		}
		
		
	}
	
	
	
	
//	// API for Put(update) the details in the database
//	
//	// http://localhost:8080/api/v1/candidateProfiles/1
//	
//	@PutMapping("/candidateProfiles/{candidateId}")
//	public ResponseEntity<?> updateCandidateProfiles(@PathVariable Integer candidateId,@RequestParam("files")List<MultipartFile> multipartFiles,@ModelAttribute CandidateProfiles candidateProfilesDetails,@RequestHeader(name = "Authorization") String session) {
//		try 
//		{
//		if(session.equals(null) || session.isEmpty()) 
//		{
//			System.out.println("Session token is empty!");
//			return null;
//		}
//		Integer userId = SessionTokenRepository.findByAllSessionToken(session);
//		String stoken = SessionTokenRepository.getSessionTokenById(userId);
//		//System.out.println("IN DB ="+stoken);
//		//System.out.println("Input = "+session );
//		
//		if(!session.equals(stoken)) {
////			System.out.println("Session token is Not equal!");
////			return null;
//			throw new RuntimeException();
//		}
//		 Optional<CandidateProfiles> Candidatepro=recruitmentapplicationrepository.findById(candidateId);
//			
// 		if (Candidatepro.isEmpty()) {
//             throw new RuntimeException("Candidate not found!");
//         }
//		
//			recruitmentapplicationService.updateCandidateProfiles(candidateId, candidateProfilesDetails,multipartFiles);
//			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tPUT-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.OK);
//			return ResponseEntity.status(HttpStatus.OK).body("Candidate Updated Successfully");
//			
//		} 
//		 catch (RuntimeException e) 
//		{
//		    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPUT-CANDIDATE-DETAIL-BY-ID: " + candidateId + " STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
//		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("candidate id is not found/Session token is not equal");
//		}
//         
//	    catch (Exception e) 
//		{
//			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPUT-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
//		  return ResponseEntity
//				  .status(HttpStatus.INTERNAL_SERVER_ERROR)
//				  .body("Somthing went wrong");
//		}
//		
//		
//	}
	
	// API for delete the data in the database
	
	// http://localhost:8080/api/v1/candidateProfiles/1
	
	@DeleteMapping("/candidateProfiles/{candidateId}")
	public ResponseEntity<?> deleteCandidateProfiles(@PathVariable Integer candidateId,@RequestHeader(name = "Authorization") String session) {
		try {
		    
			if(session.equals(null) || session.isEmpty()) {
			System.out.println("Session token is empty!");
			
		  return null;
		}
		Integer userId = SessionTokenRepository.findByAllSessionToken(session);
		String stoken = SessionTokenRepository.getSessionTokenById(userId);
		
	
		if(!session.equals(stoken)) {

//			System.out.println("Session token is Not equal!");
//			return null;
			throw new RuntimeException();
			
		}
		Integer loginId = userId;
		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		String roles = user.getUserRole();
		if(userId == 1 || roles == "admin")
		{
		    Optional<CandidateProfiles> Candidatepro=recruitmentapplicationrepository.findById(candidateId);
		
		    		if (Candidatepro.isEmpty()) {
		                throw new RuntimeException("Candidate not found!");
		            }
		    		
		                recruitmentapplicationService.deleteCandidateProfiles(candidateId);
		                //return ResponseEntity.ok("User soft deleted successfully");
		                
		                Recentactivities recentactivities = new Recentactivities();
//		        		Integer loginId = userId;
//		        		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		        		String username = user.getUsername();
		        		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
		        		recentactivities.setMessage( username+" deleted 1 candidate through the Delete");
		        		recentactivitiesRepository.save(recentactivities);           
		            
		    		
			//recruitmentapplicationService.deleteCandidateProfiles(candidateId);
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tDELETE-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body("Candidate Deleted Successfully");
		}
		else {
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tDELETE-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body("User Delete Access denied");
			
		}
		
		}
	
		catch(RuntimeException e)
		{
		
		        System.out.println(new Timestamp(System.currentTimeMillis()).toString() +  "\t\tUSERID: "+"\tAPI:\tDELETE-CANDIDATE-DETAIL-BY-ID: " + candidateId + " STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("candidate id is not found/Session token is not equal:"+e);
		}
		
		catch (Exception e) {
			
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tDELETE-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Somthing went wrong:"+e);
		}
	}
	

//	@PutMapping("/updateresume/{candidateId}")
//	public ResponseEntity<?> updateResume(@RequestPart("files")List<MultipartFile> multipartFiles,@PathVariable Integer candidateId,@RequestHeader(name = "Authorization") String session){
//		
//		try {
//			if(session.equals(null) || session.isEmpty()) 
//			{
//				System.out.println("Session token is empty!");
//				return null;
//			}
//			
//			Integer userId = SessionTokenRepository.findByAllSessionToken(session);
//			String stoken = SessionTokenRepository.getSessionTokenById(userId);
//			//System.out.println("IN DB ="+stoken);
//			//System.out.println("Input = "+session );
//			if(!session.equals(stoken)) {
//				System.out.println("Session token is Not equal!");
//				return null;
//			}
//		
//		recruitmentapplicationService.updateResume(multipartFiles,candidateId);
//		return ResponseEntity.status(HttpStatus.OK)
//		                     .body("Resume Updated Successfully");
//	}catch (Exception e) 
//		{
//	     return ResponseEntity
//				  .status(HttpStatus.INTERNAL_SERVER_ERROR)
//				  .body("Somthing went wrong" + e);
//	
//		}
//	}
	@PutMapping("/candidateProfiles/updateresume/{candidateId}/resumename/{fileName}")
	public ResponseEntity<?> updateresume(@PathVariable( name = "candidateId" ) Integer candidateId,@PathVariable( name = "fileName" ) String fileName,@RequestPart("files")MultipartFile multipartFile,@RequestHeader(name = "Authorization") String session ){
		 try {
			 if(session.equals(null) || session.isEmpty()) 
				{
					System.out.println("Session token is empty!");
					return null;
				}
				
				Integer userId = SessionTokenRepository.findByAllSessionToken(session);
				String stoken = SessionTokenRepository.getSessionTokenById(userId);
				//System.out.println("IN DB ="+stoken);
				//System.out.println("Input = "+session );
				if(!session.equals(stoken)) {
					System.out.println("Session token is Not equal!");
					return null;
				}
		recruitmentapplicationService.updateResume(multipartFile, candidateId, fileName);
		
	    Recentactivities recentactivities = new Recentactivities();
 		Integer loginId = userId;
 		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
 		String username = user.getUsername();
 		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
 		recentactivities.setMessage( username+" updated resumes through the Update Resume");
 		recentactivitiesRepository.save(recentactivities);
		
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tUPDATERESUME-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK)
				             .body("Resume Updated Sucessfully");
	} catch (Exception e) 
			{
		    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tUPDATERESUME-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Somthing went wrong" +e);	
		}
   }
	
	@PostMapping("/candidateProfiles/addupdateresume/{candidateId}")
	public ResponseEntity <?> addUpdateResume(@PathVariable Integer candidateId,@RequestPart("files")List<MultipartFile> multipartFiles,@RequestHeader(name = "Authorization") String session){
		try {
			if(session.equals(null) || session.isEmpty()) 
			{
				System.out.println("Session token is empty!");
				return null;
			}
			
			Integer userId = SessionTokenRepository.findByAllSessionToken(session);
			String stoken = SessionTokenRepository.getSessionTokenById(userId);
			//System.out.println("IN DB ="+stoken);
			//System.out.println("Input = "+session );
			if(!session.equals(stoken)) {
				System.out.println("Session token is Not equal!");
				return null;
			}
		
		recruitmentapplicationService.addUpdateResume(multipartFiles, candidateId);
		Recentactivities recentactivities = new Recentactivities();
 		Integer loginId = userId;
 		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
 		String username = user.getUsername();
 		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
 		recentactivities.setMessage( username+" added resumes through the Add Resume");
 		recentactivitiesRepository.save(recentactivities);
		
 		 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tUPDATERESUME-CANDIDATE-DETAIL-BY-ID: "+userId+" STATUS: " + HttpStatus.OK);
		
		return ResponseEntity.status(HttpStatus.OK)
	             .body("Resume Add Updated Sucessfully");	
	}catch (Exception e) 
	{
	    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tUPDATERESUME-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		return ResponseEntity
				  .status(HttpStatus.INTERNAL_SERVER_ERROR)
				  .body("Somthing went wrong" +e);	
	}
	}
 	
		
		
     @GetMapping("/candidateProfiles/getresume/{candidateId}")
	public ResponseEntity <?> getResumebyCandidateId(@PathVariable Integer candidateId,@RequestHeader(name = "Authorization") String session){
		try {
			if(session.equals(null) || session.isEmpty()) 
			{
				System.out.println("Session token is empty!");
				return null;
			}
			
			Integer userId = SessionTokenRepository.findByAllSessionToken(session);
			String stoken = SessionTokenRepository.getSessionTokenById(userId);
			//System.out.println("IN DB ="+stoken);
			//System.out.println("Input = "+session );
			if(!session.equals(stoken)) {
				System.out.println("Session token is Not equal!");
				return null;
			}
		UploadFile uploadFile = uploadService.getResumebycandidateId(candidateId);
		
		 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGETRESUME-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.OK);
		 return  ResponseEntity.status(HttpStatus.OK)
				               .body(uploadFile);
		
	}catch (Exception e) 
		{
	    
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGETRESUME-CANDIDATE-DETAIL-BY-ID: "+candidateId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		return ResponseEntity
				  .status(HttpStatus.INTERNAL_SERVER_ERROR)
				  .body("Somthing went wrong");
		
	}
	}
	
	@GetMapping("/candidateProfiles/downloadresume/{fileName}")
	public ResponseEntity<?> downloadResume(@PathVariable String fileName,@RequestHeader(name = "Authorization") String session){
		
		try {
			if(session.equals(null) || session.isEmpty()) 
			{
				System.out.println("Session token is empty!");
				return null;
			}
			
			Integer userId = SessionTokenRepository.findByAllSessionToken(session);
			String stoken = SessionTokenRepository.getSessionTokenById(userId);
			//System.out.println("IN DB ="+stoken);
			//System.out.println("Input = "+session );
			if(!session.equals(stoken)) {
				System.out.println("Session token is Not equal!");
				return null;
			}
		//String filePath = "D:\\eclipse-workspace\\RecruitmentApplication\\src\\main\\resources\\static/" + fileName;
		 // String filePath = "uploadDirectory"+ fileName;
			Path filePath = Paths.get(uploadDirectory +fileName);

         // Load file as a resource
         Resource resource = new FileSystemResource(filePath);

         if (resource.exists()) {
             // Set content type to force download
             HttpHeaders headers = new HttpHeaders();
             headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
             
            
     		Recentactivities recentactivities = new Recentactivities();
      		Integer loginId = userId;
      		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
      		String username = user.getUsername();
      		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
      		recentactivities.setMessage( username+" downloaded 1 resume through the download");
      		recentactivitiesRepository.save(recentactivities);
             
             
             System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tDOWNLOADRESUME-BY-FILENAME: "+" STATUS: " + HttpStatus.OK);
             return ResponseEntity.status(HttpStatus.OK)
                     .headers(headers)
                     .contentType(MediaType.APPLICATION_OCTET_STREAM)
                     .body(resource);
         } else {
             // File not found
        	 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tDOWNLOADRESUME-BY-FILENAME: "+" STATUS: " + HttpStatus.NOT_FOUND);
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
            		              .body("File Not Found");
         }
     } 
	catch (Exception e) {
         // Handle exceptions here (e.g., file not found, access denied)
		 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tDOWNLOADRESUME-BY-FILENAME: "+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        		              .body("file does not exist in Server /Access Denied ");
     }
 
	}
	
	@DeleteMapping("/candidateProfiles/removeresume/{candidateId}/resumename/{fileName}")
	public ResponseEntity<?>  removeResume(@PathVariable( name = "candidateId" ) Integer candidateId,@PathVariable( name = "fileName" ) String fileName,@RequestHeader(name = "Authorization") String session){
		try {
			if(session.equals(null) || session.isEmpty()) 
			{
				System.out.println("Session token is empty!");
				return null;
			}
			
			Integer userId = SessionTokenRepository.findByAllSessionToken(session);
			String stoken = SessionTokenRepository.getSessionTokenById(userId);
			//System.out.println("IN DB ="+stoken);
			//System.out.println("Input = "+session );
			if(!session.equals(stoken)) {
				System.out.println("Session token is Not equal!");
				return null;
			}	
		recruitmentapplicationService.resumeRemove(candidateId, fileName);
		
		Recentactivities recentactivities = new Recentactivities();
  		Integer loginId = userId;
  		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
  		String username = user.getUsername();
  		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
  		recentactivities.setMessage( username+" removed 1 resume through the remove");
  		recentactivitiesRepository.save(recentactivities);
		
  		 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tREMOVERESUMECANDIDATE-DETAIL-BY-ID-BY-FILENAME: "+candidateId+" STATUS: " + HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK)
				             .body("Removed a Resume sucessfully");
		
	}catch (Exception e) 
	{
	    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tREMOVERESUME-CANDIDATE-DETAIL-BY-ID-BY-FILENAME: "+candidateId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		return ResponseEntity
				  .status(HttpStatus.INTERNAL_SERVER_ERROR)
				  .body("Somthing went wrong" +e);	
	}
}
	 @GetMapping("/candidateProfiles/export/csv")
	   public ResponseEntity<?> exportCsv(@RequestHeader(name = "Authorization") String session)throws Exception{
		  
		   if(session.equals(null) || session.isEmpty()) {
					System.out.println("Session token is empty!");
					return null;
				}
		    	Integer userId = SessionTokenRepository.findByAllSessionToken(session);
				String stoken = SessionTokenRepository.getSessionTokenById(userId);
				//System.out.println("IN DB ="+stoken);
				//System.out.println("Input = "+session );
				if(!session.equals(stoken)) {
					System.out.println("Session token is Not equal!");
					throw new RuntimeException();
				}
				
				try {
					
//		        response.setContentType("text/csv");
//		        response.setHeader("Content-Disposition", "attachment; filename=candidatereport"+ cdate+".csv");
		        
		        StringWriter writer = new StringWriter();
		        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
		                .withHeader("CandidateId","FirstName","LastName","Gender","EmailId","PrimaryContact","Nationality","CurrentCompany","ItExperience","JapaneseLevel","CurrentLocation","PositionTitle","SourceBy","ResumeReceivedDate","NoticePeriod","AdditionalInfo","DateOfBirth","Qualification","Institution","City","Country","StateName","ZipCode","ExpectSalary","CurrentSalary","CandidateStatus","Ratings","VisaStatus","MaritalStatus","ReadyToRelocate","OfferInHand","PrimarySkill","SecondarySkill","NoOfYearsJapan","CountryCode","InterviewStatus","SecondaryContact","CurrentAddress","PermanentAddress","JobType","Linkedin"));
		        
				
		        // write to csv file //
		       
		     //   ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),CsvPreference.STANDARD_PREFERENCE);
		        
//		    String[]  headings = {"CandidateId","FirstName","LastName","Gender","EmailId","PrimaryContact","Nationality","CurrentCompany","ItExperience","JapaneseLevel","CurrentLocation","PositionTitle","SourceBy","ResumeReceivedDate","NoticePeriod","AdditionalInfo","DateOfBirth","Qualification","Institution","City","Country","StateName","ZipCode","ExpectSalary","CurrentSalary","CandidateStatus","Ratings","VisaStatus","MaritalStatus","ReadyToRelocate","OfferInHand","PrimarySkill","SecondarySkill","NoOfYearsJapan","CountryCode","InterviewStatus","SecondaryContact","CurrentAddress","PermanentAddress","JobType","Linkedin"};
		        
	       
		              
		      
		      //  csvWriter.writeHeader(headings);
		        Integer loginId = userId;
				LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
				String roles = user.getUserRole();
				
				if(roles.equals("admin") || roles.equals("zenuser"))
				{
		        
		        List<CandidateProfiles> candidates = recruitmentapplicationService.exportcsv();
		       
		 
		        if(null!=candidates && !candidates.isEmpty()){
		            for (CandidateProfiles candidate : candidates) {
		            	csvPrinter.printRecord(candidate.getCandidateId(),candidate.getFirstName(),candidate.getLastName(),candidate.getGender(),candidate.getEmailId(),candidate.getPrimaryContact(),candidate.getNationality(),candidate.getCurrentCompany(),candidate.getItExperience(),candidate.getJapaneseLevel(),candidate.getCurrentLocation(),candidate.getPositionTitle(),candidate.getSourceBy(),candidate.getResumeReceivedDate(),candidate.getNoticePeriod(),candidate.getAdditionalInfo(),
		            			candidate.getDateOfBirth(),candidate.getQualification(),candidate.getInstitution(),candidate.getCity(),candidate.getCountry(),candidate.getStateName(),candidate.getZipCode(),candidate.getExpectSalary(),candidate.getCurrentSalary(),candidate.getCandidateStatus(),candidate.getRatings(),candidate.getVisaStatus(),candidate.getMaritalStatus(),candidate.getReadyToRelocate(),candidate.getOfferInHand(),candidate.getPrimarySkill(),candidate.getSecondarySkill(),
		            			candidate.getNoOfYearsJapan(),candidate.getCountryCode(),candidate.getInterviewStatus(),candidate.getSecondaryContact(),candidate.getCurrentAddress(),candidate.getPermanentAddress(),candidate.getJobType(),candidate.getLinkedin());
		              }
		            }
		       
		        
		        csvPrinter.close(); 
				}else {
					List<CandidateProfiles> candidates = recruitmentapplicationService.exportcsvbyuser(userId);
				       
					 
			        if(null!=candidates && !candidates.isEmpty()){
			            for (CandidateProfiles candidate : candidates) {
			            	csvPrinter.printRecord(candidate.getCandidateId(),candidate.getFirstName(),candidate.getLastName(),candidate.getGender(),candidate.getEmailId(),candidate.getPrimaryContact(),candidate.getNationality(),candidate.getCurrentCompany(),candidate.getItExperience(),candidate.getJapaneseLevel(),candidate.getCurrentLocation(),candidate.getPositionTitle(),candidate.getSourceBy(),candidate.getResumeReceivedDate(),candidate.getNoticePeriod(),candidate.getAdditionalInfo(),
			            			candidate.getDateOfBirth(),candidate.getQualification(),candidate.getInstitution(),candidate.getCity(),candidate.getCountry(),candidate.getStateName(),candidate.getZipCode(),candidate.getExpectSalary(),candidate.getCurrentSalary(),candidate.getCandidateStatus(),candidate.getRatings(),candidate.getVisaStatus(),candidate.getMaritalStatus(),candidate.getReadyToRelocate(),candidate.getOfferInHand(),candidate.getPrimarySkill(),candidate.getSecondarySkill(),
			            			candidate.getNoOfYearsJapan(),candidate.getCountryCode(),candidate.getInterviewStatus(),candidate.getSecondaryContact(),candidate.getCurrentAddress(),candidate.getPermanentAddress(),candidate.getJobType(),candidate.getLinkedin());			              }
			            }
		            

			        csvPrinter.close(); 
				}
				
				String cdate = new String(new Timestamp(System.currentTimeMillis()).toString());
				HttpHeaders headers = new HttpHeaders();
	            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=candidatereport"+cdate+".csv");
	            
		        Recentactivities recentactivities = new Recentactivities();
//	      		Integer loginId = userId;
//	      		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
	      		String username = user.getUsername();
	      		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
	      		recentactivities.setMessage( username+" downloaded 1 csv through the candidateprofiles export");
	      		recentactivitiesRepository.save(recentactivities);
		        
		        
	      		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-CANDIDATEPROFILES-EXPORT-CSV:  STATUS: " + HttpStatus.OK);	      		
		       
	      		return ResponseEntity.status(HttpStatus.OK)
		        		             .headers(headers)
		        		             .contentType(MediaType.parseMediaType("text/csv"))
		        		             .body(writer.toString().getBytes());
		      
	                        
				}
			    catch(Exception e) {
					e.printStackTrace();
					
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-CANDIDATEPROFILES-EXPORT-CSV:  STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
					 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	   		              .body("candidatescsv reportcsv failed"+e);
			    }
		      
	   }
	 @PostMapping("/candidateProfiles/filterbyexport/csv")
	   public ResponseEntity<?> filterbyexportCsv(@RequestBody List<CandidateProfiles> candidateprofiles,@RequestHeader(name = "Authorization") String session)throws Exception{
		  
		   if(session.equals(null) || session.isEmpty()) {
					System.out.println("Session token is empty!");
					return null;
				}
		    	Integer userId = SessionTokenRepository.findByAllSessionToken(session);
				String stoken = SessionTokenRepository.getSessionTokenById(userId);
				//System.out.println("IN DB ="+stoken);
				//System.out.println("Input = "+session );
				if(!session.equals(stoken)) {
					System.out.println("Session token is Not equal!");
					throw new RuntimeException();
				}
				
				try {

		        
		        StringWriter writer = new StringWriter();
		        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
		                .withHeader("CandidateId","FirstName","LastName","Gender","EmailId","PrimaryContact","Nationality","CurrentCompany","ItExperience","JapaneseLevel","CurrentLocation","PositionTitle","SourceBy","ResumeReceivedDate","NoticePeriod","AdditionalInfo","DateOfBirth","Qualification","Institution","City","Country","StateName","ZipCode","ExpectSalary","CurrentSalary","CandidateStatus","Ratings","VisaStatus","MaritalStatus","ReadyToRelocate","OfferInHand","PrimarySkill","SecondarySkill","NoOfYearsJapan","CountryCode","InterviewStatus","SecondaryContact","CurrentAddress","PermanentAddress","JobType","Linkedin"));
		        
				
		        
		        List<CandidateProfiles> candidates = candidateprofiles;
		         
		 
		        if(null!=candidates && !candidates.isEmpty()){
		            for (CandidateProfiles candidate : candidates) {
		            	csvPrinter.printRecord(candidate.getCandidateId(),candidate.getFirstName(),candidate.getLastName(),candidate.getGender(),candidate.getEmailId(),candidate.getPrimaryContact(),candidate.getNationality(),candidate.getCurrentCompany(),candidate.getItExperience(),candidate.getJapaneseLevel(),candidate.getCurrentLocation(),candidate.getPositionTitle(),candidate.getSourceBy(),candidate.getResumeReceivedDate(),candidate.getNoticePeriod(),candidate.getAdditionalInfo(),
		            			candidate.getDateOfBirth(),candidate.getQualification(),candidate.getInstitution(),candidate.getCity(),candidate.getCountry(),candidate.getStateName(),candidate.getZipCode(),candidate.getExpectSalary(),candidate.getCurrentSalary(),candidate.getCandidateStatus(),candidate.getRatings(),candidate.getVisaStatus(),candidate.getMaritalStatus(),candidate.getReadyToRelocate(),candidate.getOfferInHand(),candidate.getPrimarySkill(),candidate.getSecondarySkill(),
		            			candidate.getNoOfYearsJapan(),candidate.getCountryCode(),candidate.getInterviewStatus(),candidate.getSecondaryContact(),candidate.getCurrentAddress(),candidate.getPermanentAddress(),candidate.getJobType(),candidate.getLinkedin());
		               }
		            
		            }
		       
		        csvPrinter.close(); 
		        
				
				
				String cdate = new String(new Timestamp(System.currentTimeMillis()).toString());
				HttpHeaders headers = new HttpHeaders();
	            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=candidatereport"+cdate+".csv");
	            
	           
	            
		        Recentactivities recentactivities = new Recentactivities();
	      		Integer loginId = userId;
	      		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
	      		String username = user.getUsername();
	      		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
	      		recentactivities.setMessage( username+" downloaded 1 csv through the candidateprofiles export");
	      		recentactivitiesRepository.save(recentactivities);
		        
		        
	      		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-CANDIDATEPROFILES-FILTERBY-EXPORT-CSV:  STATUS: " + HttpStatus.OK);	      		
		       
	      		return ResponseEntity.status(HttpStatus.OK)
		        		             .headers(headers)
		        		             .contentType(MediaType.parseMediaType("text/csv"))
		        		             .body(writer.toString().getBytes());
		      
	                        
				}
			    catch(Exception e) {
					e.printStackTrace();
					
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-CANDIDATEPROFILES-FILTERBY-EXPORT-CSV:  STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
					 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	   		              .body("candidatescsv reportcsv failed"+e);
			    }
		      
	   }
}

	


