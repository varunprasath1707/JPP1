package com.RecruitmentApplication.controller;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RecruitmentApplication.model.*;
import com.RecruitmentApplication.repository.*;
import com.RecruitmentApplication.service.*;

import jakarta.servlet.http.HttpServletResponse;



@CrossOrigin(exposedHeaders = "Content-Disposition")
@RestController
@RequestMapping("/interview")
public class InterviewController {
	
	@Autowired
	private InterviewService interviewService;
	
	@Autowired
	private  SessionTokenRepository SessionTokenRepository;
	
	@Autowired
	private InterviewRepository interviewrepository;
	
	@Autowired
	private RecentactivitiesRepository recentactivitiesRepository;
	
	@Autowired
	private LoginCredentialsRepository loginCredentialsRepository;
	
	
    
	
	// API for Get(read) all details from database
	
	// http://localhost:8080/interview/getinterview
	// http://192.168.1.2:8080/rz/interview/getinterview
	
	@GetMapping("/getinterview")
	public ResponseEntity<?> getAllInterview(@RequestHeader(name = "Authorization") String session){
	
	try {
		//session token validation
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
		Integer loginId = userId;
		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		String roles = user.getUserRole();
		if(roles.equals("admin") || roles.equals("zenuser"))
		{
			// function calling for Get All Interview Details form the Database
			
		List <Interview> interview = interviewService.getAllInterview();
		if(interview.isEmpty())
		{
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-INTERVIEW-DETAILS "+ "STATUS: " + HttpStatus.NO_CONTENT);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-INTERVIEW-DETAILS "+ "STATUS: " + HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(interview);
		}else {
			List <Interview> interview = interviewService.getAllInterviewbyuser(userId);
			if(interview.isEmpty())
			{
				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-INTERVIEW-DETAILS-BY-USER "+ "STATUS: " + HttpStatus.NO_CONTENT);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
			}
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-INTERVIEW-DETAILS-BY-USER "+ "STATUS: " + HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body(interview);
		
		}
	}
		catch (RuntimeException e) {
		
			 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-ALL-INTERVIEW-DETAILS "+"STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Session token is not equal:"+e);
		
		}
		catch (Exception e) {
			// TODO: handle exception
			//String message="something went Wrong"+e.getMessage();
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-ALL-INTERVIEW-DETAILS "+"STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Something went wrong:"+e);
		}
	}
	
	// API for Post(insert) the details in the database
	
	// http://localhost:8080/interview/postinterview
	// http://192.168.1.2:8080/rz/interview/postinterview
	
	@PostMapping("/postinterview")
	public ResponseEntity<?>addInterview(@RequestBody Map<String,Object> interview,@RequestHeader(name = "Authorization") String session ){
		
		try {
		//session token validation
		
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
		
	
			//function calling for Add Interview
		interviewService.addInterview(interview,userId);
		
		Recentactivities recentactivities = new Recentactivities();
		Integer loginId = userId;
		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		String username = user.getUsername();
		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
		recentactivities.setMessage( username+" added 1 interview through the Add Interview");
		recentactivitiesRepository.save(recentactivities);
	
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tPOST-INTERVIEW-DETAILS "+"STATUS: " + HttpStatus.OK);
		
		return ResponseEntity.status(HttpStatus.OK).body("interview inserted Successfully");
		}
		catch (RuntimeException e) {
			
			 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPOST-INTERVIEW-DETAILS "+"STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +" "+e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong:"+e);
		
		}
		catch(Exception e){
			
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPOST-INTERVIEW-DETAILS "+"STATUS:" + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
			        .body("Something went wrong:"+e);
		}
}
	@PostMapping("/postinterview-and-mail")
	public ResponseEntity<?>addInterviewAndSendMail(@RequestBody Map<String,Object> interview,@RequestHeader(name = "Authorization") String session ){
		
		try {
		//session token validation
		
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
		
	
			//function calling for Add Interview
		interviewService.addInterviewAndMail(interview);
		
		Recentactivities recentactivities = new Recentactivities();
		Integer loginId = userId;
		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		String username = user.getUsername();
		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
		recentactivities.setMessage( username+" added 1 interview through the Add Interview");
		recentactivitiesRepository.save(recentactivities);
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tPOST-INTERVIEW-DETAILS-Mail "+"STATUS: " + HttpStatus.OK);
	
		return ResponseEntity.status(HttpStatus.OK).body("Interview Inserted Successfully and Mail sent");
		}
		catch (RuntimeException e) {
			
			 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPOST-INTERVIEW-DETAILS-Mail "+"STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +" "+e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong:"+e);
		
		}
		catch(Exception e){
			
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPOST-INTERVIEW-DETAILS-Mail "+"STATUS:" + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
			        .body("Something went wrong:"+e);
		}
}

	       
	        // API for Get(read) the details by Id from the database
	
			// http://localhost:8080/interview/getinterview/1
	        // http://192.168.1.2:8080/rz/interview/getinterview/1
	
	@GetMapping("/getinterview/{interviewId}")
	public ResponseEntity<?> getInterviewById(@PathVariable Integer interviewId,@RequestHeader(name = "Authorization") String session) {
		
		try {
		//session token validation
		
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
			
			// function for Get Interview Detail By Id
			Interview interview = interviewService.getInterviewById(interviewId);
			
			if(interview==null)
			{
				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-INTERVIEW-DETAIL-BY-ID: "+interviewId+" STATUS:" + HttpStatus.NO_CONTENT);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Interview NotFound");
					
			}
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-INTERVIEW-DETAIL-BY-ID: "+interviewId+" STATUS: " + HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body(interview);		
			
	    }
		catch (RuntimeException e) 
		{
		    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-INTERVIEW-DETAIL-BY-ID: " + interviewId + " STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("interview Id is not found/Session token is not equal:"+e);
		}
		catch (Exception e) 
		{
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-INTERVIEW-DETAIL-BY-ID: "+interviewId+" STATUS:" + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Somthing went wrong:"+e);
		}
}
		
	    // API for Put(update) the details in the database
	
		// http://localhost:8080/interview/updateinterview/1
	    // http://192.168.1.2:8080/rz/interview/updateinterview/1
	
	@PutMapping("/updateinterview/{interviewId}")
	public ResponseEntity<?> updateInterview(@PathVariable Integer interviewId,@RequestBody Interview interviewDetails,@RequestHeader(name = "Authorization") String session) {
		try {
		//session token validation
		
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
		
			 Optional<Interview> Candidatepro=interviewrepository.findById(interviewId);
				
		 		if (Candidatepro.isEmpty()) {
		             throw new RuntimeException("Candidate not found!");
		         }
	     
			 //function calling for Update Interview Details By Id
         interviewService.updateInterview(interviewId, interviewDetails);
         
        Recentactivities recentactivities = new Recentactivities();
 		Integer loginId = userId;
 		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
 		String username = user.getUsername();
 		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
 		recentactivities.setMessage( username+" updated 1 interview through the Edit");
 		recentactivitiesRepository.save(recentactivities);
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tPUT-INTERVIEW-DETAIL-BY-ID: "+interviewId+" STATUS: " + HttpStatus.OK);
         return ResponseEntity.status(HttpStatus.OK).body("interview Updated Successfully");
		
	}
		catch (RuntimeException e) 
		{
		    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPUT-INTERVIEW-DETAIL-BY-ID: " + interviewId + " STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("interview id is not found/Session token is not equal:"+e);
		}
		catch(Exception e){
		
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPUT-INTERVIEW-DETAIL-BY-ID: "+interviewId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
		        .body("Something went wrong:"+e);
	}
}
	   
	    // API for delete the data in the database
	
		// http://localhost:8080/interview/deleteinterview/1
	    // http://192.168.1.2:8080/rz/interview/deleteinterview/1
	
	@DeleteMapping("/deleteinterview/{interviewId}")
	public ResponseEntity<?> deleteInterview(@PathVariable Integer interviewId,@RequestHeader(name = "Authorization") String session) {
		
		try {
		//session token validation
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
		Integer loginId = userId;
		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		String roles = user.getUserRole();
		if(userId == 1 || roles == "admin")
		{
		
		Optional<Interview> Candidatepro=interviewrepository.findById(interviewId);
		
 		if (Candidatepro.isEmpty()) {
             throw new RuntimeException("Candidate not found!");
         }
			//function calling for Delete Interview Detail By Id
		interviewService.deleteInterview(interviewId);
		
		Recentactivities recentactivities = new Recentactivities();
//		Integer loginId = userId;
//		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		String username = user.getUsername();
		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
		recentactivities.setMessage( username+" deleted 1 interview through the Delete");
		recentactivitiesRepository.save(recentactivities);
		
		
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tDELETE-INTERVIEW-DETAIL-BY-ID: "+interviewId+" STATUS: " + HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body("Interview Deleted Successfully");
	
	}else {
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tDELETE-INTERVIEW-DETAIL-BY-ID: "+interviewId+" STATUS: " + HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body("User Delete Access Denied");
	}
	}
		catch (RuntimeException e) 
		{
		    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tDELETE-INTERVIEW-DETAIL-BY-ID: " + interviewId + " STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("interview id is not found/Session token is not equal:"+e);
		}
		catch(Exception e){
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tDELETE-INTERVIEW-DETAIL-BY-ID: "+interviewId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
			        .body("Something went wrong:"+e);
		}
		
    }
	@DeleteMapping("/deleteInterviewWithEmail/{interviewId}")
	public ResponseEntity<?> deleteInterviewWithEmail(@PathVariable Integer interviewId,@RequestHeader(name = "Authorization") String session,@RequestBody Interview interviewDetails) {
		
		try {
		//session token validation
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
		
		Optional<Interview> Candidatepro=interviewrepository.findById(interviewId);
		
 		if (Candidatepro.isEmpty()) {
             throw new RuntimeException("Candidate not found!");
         }
			//function calling for Delete Interview Detail By Id
		interviewService.deleteInterviewWithEmail(interviewId,interviewDetails);
		
		Recentactivities recentactivities = new Recentactivities();
		Integer loginId = userId;
		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		String username = user.getUsername();
		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
		recentactivities.setMessage( username+" deleted 1 interview through the Delete");
		recentactivitiesRepository.save(recentactivities);
		
		
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tDELETE-INTERVIEW-DETAIL-BY-ID: "+interviewId+" STATUS: " + HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body("Interview Cancelled Successfully With the Mail");
	
	}
		catch (RuntimeException e) 
		{
		    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tDELETE-INTERVIEW-DETAIL-BY-ID: " + interviewId + " STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("interview id is not found/Session token is not equal:"+e);
		}
		catch(Exception e){
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tDELETE-INTERVIEW-DETAIL-BY-ID: "+interviewId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
			        .body("Something went wrong:"+e);
		}
		
    }
	
	@GetMapping("/history/{candidateId}")
	public ResponseEntity<?> getAllInterviewHistory(@PathVariable Integer candidateId,@RequestHeader(name = "Authorization") String session){
	
	try {
		//session token validation
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
		
			// function calling for Get All Interview Details form the Database
			
		List <Interview> interviewhistory = interviewService.getAllInterviewHistory(candidateId);
		if(interviewhistory.isEmpty())
		{
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-INTERVIEW-HISTORY-DETAILS-BY_ID "+ "STATUS: " + HttpStatus.NO_CONTENT);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-INTERVIEW-HISTORY-DETAILS-BY_ID "+ "STATUS: " + HttpStatus.OK);
		return ResponseEntity.status(HttpStatus.OK).body(interviewhistory);
		}
		catch (RuntimeException e) {
		
			 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-INTERVIEW-HISTORY-DETAILS-BY_ID "+"STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Session token is not equal:"+e);
		
		}
		catch (Exception e) {
			// TODO: handle exception
			//String message="something went Wrong"+e.getMessage();
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-INTERVIEW-HISTORY-DETAILS-BY_ID "+"STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Something went wrong:"+e);
		}
	}
	
	@GetMapping("/export/csv")
	   public ResponseEntity<?> exportCsv(@RequestHeader(name = "Authorization") String session){
			   
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
			                .withHeader("InterviewId","JobOpeningId","CandidateId","InterviewName","CandidateName","PostingTitle","Interviewer","InterviewFrom","InterviewTo","InterviewLocation","ClientName","ZenitusComments","ClientComments","InterviewStatus"));
			             
		 
//		        String[]  headings = {"InterviewId","JobOpeningId","CandidateId","InterviewName","CandidateName","PostingTitle","Interviewer","InterviewFrom","InterviewTo","InterviewLocation","ClientName","ZenitusComments","ClientComments","InterviewStatus"};
//		        
//		        String[] interviewsrecord = {"interviewId","jobOpeningId","candidateId","interviewName","candidateName","postingTitle","interviewer","interviewFrom","interviewTo","interviewLocation","clientName","zenitusComments","clientComments","interviewStatus"};
//		              
		   
		        Integer loginId = userId;
				LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
				String roles = user.getUserRole();
				if(roles.equals("admin") || roles.equals("zenuser"))
				{
				
		        // write to csv file //
		        List <Interview> interviews = interviewService.getAllInterviewexportcsv();
		        if(null!=interviews && !interviews.isEmpty()){
		            for (Interview interview : interviews) {
		            	csvPrinter.printRecord(interview.getInterviewId(),interview.getJobOpeningId(),interview.getCandidateId(),interview.getInterviewName(),interview.getCandidateName(),interview.getPostingTitle(),interview.getInterviewer(),interview.getInterviewFrom(),interview.getInterviewTo(),interview.getInterviewLocation(),interview.getClientName(),interview.getZenitusComments(),interview.getClientComments(),interview.getInterviewStatus());
		               
		              }
		            }
		        csvPrinter.close();
				}else {
					List <Interview> interviews = interviewService.getAllInterviewexportcsvbyuser(userId);
			        if(null!=interviews && !interviews.isEmpty()){
			            for (Interview interview : interviews) {
			            	csvPrinter.printRecord(interview.getInterviewId(),interview.getJobOpeningId(),interview.getCandidateId(),interview.getInterviewName(),interview.getCandidateName(),interview.getPostingTitle(),interview.getInterviewer(),interview.getInterviewFrom(),interview.getInterviewTo(),interview.getInterviewLocation(),interview.getClientName(),interview.getZenitusComments(),interview.getClientComments(),interview.getInterviewStatus());
			               
			              }
			            }
			        csvPrinter.close();
					
				}
				
				String cdate = new String(new Timestamp(System.currentTimeMillis()).toString());
				HttpHeaders headers = new HttpHeaders();
	            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interviewreport"+cdate+".csv");
		          
		        Recentactivities recentactivities = new Recentactivities();
//	      		Integer loginId = userId;
//	      		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
	      		String username = user.getUsername();
	      		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
	      		recentactivities.setMessage( username+" downloaded 1 csv through the interview export");
	      		recentactivitiesRepository.save(recentactivities);
		       
	      		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-INTERVIEW-EXPORT-CSV:  STATUS: " + HttpStatus.OK);
		         
	      		return ResponseEntity.status(HttpStatus.OK)
   		             .headers(headers)
   		             .contentType(MediaType.parseMediaType("text/csv"))
   		             .body(writer.toString().getBytes());
		        
		       
		        		             
				}catch(Exception e) {
					e.printStackTrace();
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-INTERVIEW-EXPORT-CSV:  STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
					 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	   		              .body("interviewscsv reportcsv failed"+e.getMessage());
			    }
}
	@PostMapping("/filterbyexport/csv")
	   public ResponseEntity<?> filterbyexportCsv(@RequestBody List<Interview> interviewe,@RequestHeader(name = "Authorization") String session){
			   
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
			                .withHeader("InterviewId","JobOpeningId","CandidateId","InterviewName","CandidateName","PostingTitle","Interviewer","InterviewFrom","InterviewTo","InterviewLocation","ClientName","ZenitusComments","ClientComments","InterviewStatus"));
			             
		 
				
		        // write to csv file //
		        List <Interview> interviews = interviewe;
		        if(null!=interviews && !interviews.isEmpty()){
		            for (Interview interview : interviews) {
		            	csvPrinter.printRecord(interview.getInterviewId(),interview.getJobOpeningId(),interview.getCandidateId(),interview.getInterviewName(),interview.getCandidateName(),interview.getPostingTitle(),interview.getInterviewer(),interview.getInterviewFrom(),interview.getInterviewTo(),interview.getInterviewLocation(),interview.getClientName(),interview.getZenitusComments(),interview.getClientComments(),interview.getInterviewStatus());
		               
		              }
		            }
		        csvPrinter.close();
		        				
				String cdate = new String(new Timestamp(System.currentTimeMillis()).toString());
				HttpHeaders headers = new HttpHeaders();
	            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interviewreport"+cdate+".csv");
		          
		        Recentactivities recentactivities = new Recentactivities();
	      		Integer loginId = userId;
	      		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
	      		String username = user.getUsername();
	      		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
	      		recentactivities.setMessage( username+" downloaded 1 csv through the interview export");
	      		recentactivitiesRepository.save(recentactivities);
		       
	      		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-INTERVIEW-FILTERBY-EXPORT-CSV:  STATUS: " + HttpStatus.OK);
		         
	      		return ResponseEntity.status(HttpStatus.OK)
		             .headers(headers)
		             .contentType(MediaType.parseMediaType("text/csv"))
		             .body(writer.toString().getBytes());
		        
		       
		        		             
				}catch(Exception e) {
					e.printStackTrace();
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-INTERVIEW-FILTERBY-EXPORT-CSV:  STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
					 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	   		              .body("interviewscsv reportcsv failed"+e.getMessage());
			    }
}


}