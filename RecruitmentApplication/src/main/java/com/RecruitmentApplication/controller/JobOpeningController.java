package com.RecruitmentApplication.controller;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
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


import com.RecruitmentApplication.model.CandidateProfiles;
import com.RecruitmentApplication.model.JobOpening;
import com.RecruitmentApplication.model.LoginCredentials;
import com.RecruitmentApplication.model.Recentactivities;
import com.RecruitmentApplication.repository.JobOpeningRepository;
import com.RecruitmentApplication.repository.LoginCredentialsRepository;
import com.RecruitmentApplication.repository.RecentactivitiesRepository;
import com.RecruitmentApplication.repository.SessionTokenRepository;
import com.RecruitmentApplication.service.JobOpeningService;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(exposedHeaders = "Content-Disposition")
@RestController
@RequestMapping("/jobopening")
public class JobOpeningController {

   
	@Autowired
	private JobOpeningService JobopeningService;
	
	@Autowired
	private SessionTokenRepository SessionTokenRepository;
	
	@Autowired
	private JobOpeningRepository jobopeningrepository;
	
	@Autowired
	private RecentactivitiesRepository recentactivitiesRepository;
	
	@Autowired
	private LoginCredentialsRepository loginCredentialsRepository;
   // API for get(read) all details from database
	
	 // http://localhost:8080/jobopening/getdetails 
	 // http://192.168.1.2:8080/rz/jobopening/getdetails 
	
    @GetMapping("/getdetails")
	public ResponseEntity<?> getAlljobopening(@RequestHeader(name = "Authorization") String session) 
	{
    	try {	
    	//session token validation
    	
    	if(session.equals(null) || session.isEmpty()) {
			System.out.println("Session token is Empty!");
			return null;
		}
    	Integer userId = SessionTokenRepository.findByAllSessionToken(session);
		String stoken = SessionTokenRepository.getSessionTokenById(userId);
		if(!session.equals(stoken)) {
			System.out.println("Session token is Not equal!");
			 throw new RuntimeException();
		}
		Integer loginId = userId;
		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		String roles = user.getUserRole();
		
		if(roles.equals("admin") || roles.equals("zenuser"))
		{
			//function calling for get all details form the database 
			
		List<JobOpening> jobOpening = JobopeningService.getAlldetails();
		if(jobOpening.isEmpty())
		{
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-JOBOPENING-DETAILS " +"STATUS: "+HttpStatus.NO_CONTENT); 
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-JOBOPENING-DETAILS " +"STATUS: "+ HttpStatus.OK); 
		return ResponseEntity.status(HttpStatus.OK).body(jobOpening);
		}else {
			
			List<JobOpening> jobOpening = JobopeningService.getAlldetailsbyuser(userId);
			if(jobOpening.isEmpty())
			{
				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-JOBOPENING-DETAILS-BY-USER " +"STATUS: "+HttpStatus.NO_CONTENT); 
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
			}
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-JOBOPENING-DETAILS-BY-USER " +"STATUS: "+ HttpStatus.OK); 
			return ResponseEntity.status(HttpStatus.OK).body(jobOpening);
			
		}
    	}
		
		catch (RuntimeException e) {
			   System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-ALL-CANDIDATE-DETAILS "+"STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Session token is not equal:"+e);
		   }
		
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-ALL-JOBOPENING-DETAILS "+ "STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Somthing went wrong:"+e);
		}
	}
    
    // API for post(insert) the details in the database
    
    // http://localhost:8080/jobopening/postdetails 
    // http://192.168.1.2:8080/rz/jobopening/postdetails 
    
	@PostMapping("/postdetails")
	public ResponseEntity<String> CreateJob(@RequestBody JobOpening jobopening,@RequestHeader(name = "Authorization") String session) {
	    
		try 
		{
		//session token validation
		
		if(session.equals(null) || session.isEmpty()) {
			System.out.println("Session token is Empty!");
		    return null;
		}
		Integer userId = SessionTokenRepository.findByAllSessionToken(session);
		String stoken = SessionTokenRepository.getSessionTokenById(userId);
		if(!session.equals(stoken)) {
			System.out.println("Session token is Not equal!");
			throw new RuntimeException();
		}
		
			// function calling for create JobOpening
			JobopeningService.CreateJob(jobopening,userId);
			
			Recentactivities recentactivities = new Recentactivities();
			Integer loginId = userId;
			LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
			String username = user.getUsername();
			recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
			recentactivities.setMessage( username+" added 1 job opening through the Add Jobopening");
			recentactivitiesRepository.save(recentactivities);
			
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tPOST-JOBOPENING-DETAIL "+"STATUS: " + HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body("JobOpening Inserted Successfully");
		} 
		
		//return ResponseEntity.ok("Added Sucessfully");
		//return new ResponseEntity<String>(JobopeningService.CreateJob(jobopening),HttpStatus.CREATED);
		 catch (RuntimeException e) {
		        System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPOST-CANDIDATE-DETAIL "+"STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("session token is not equal:"+e);
		    }
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPOST-JOBOPENING-DETAIL "+"STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Somthing went wrong:"+e);
		}
	}
	
	// API for delete the data in the database
	
	 // http://localhost:8080/jobopening/deletebyid/1 
	// http://192.168.1.2:8080/rz/jobopening/deletebyid/1 
	
	@DeleteMapping("/deletebyid/{jobOpeningId}")
	public ResponseEntity <?>  DeleteJob (@PathVariable(value="jobOpeningId")int jobOpeningId,@RequestHeader(name = "Authorization") String session)
	{
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
		Optional<JobOpening> jobop=jobopeningrepository.findById(jobOpeningId);
		
 		if (jobop.isEmpty()) {
             throw new RuntimeException("Candidate not found!");
         }
				//function calling for Delete JobOpening Details By Id 
			 
			    JobopeningService.DeleteJobOpeningById(jobOpeningId);
			    
			    Recentactivities recentactivities = new Recentactivities();
//				Integer loginId = userId;
//				LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
				String username = user.getUsername();
				recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
				recentactivities.setMessage( username+" deleted 1 job opening through the Delete");
				recentactivitiesRepository.save(recentactivities);
				
			    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tDELETE-JOBOPENING-DETAIL-BY-ID: "+jobOpeningId+" STATUS: " + HttpStatus.OK);
				return ResponseEntity.status(HttpStatus.OK).body("JobOpening Deleted Successfully "+jobOpeningId);
				}else {
					 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tDELETE-JOBOPENING-DETAIL-BY-ID: "+jobOpeningId+" STATUS: " + HttpStatus.OK);
						return ResponseEntity.status(HttpStatus.OK).body("User Delete Access Denied");
				}
					
				}
		        
		
				//return JobopeningService.getAlldetails();
		       catch (RuntimeException e) {
				// TODO: handle exception
		    	   System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tDELETE-JOBOPENING-DETAIL-BY-ID: " + jobOpeningId + " STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
				    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Jobopening id is not found/Session token is not equal:"+e);
			}
				catch (Exception e) {
					// TODO: handle exception
					//String message="something went Wrong"+e.getMessage();
					System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tDELETE-JOBOPENING-DETAIL-BY-ID: "+jobOpeningId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
					return ResponseEntity
							  .status(HttpStatus.INTERNAL_SERVER_ERROR)
							  .body("Somthing went wrong:"+e);
				}
	
		
		//return ResponseEntity.ok("deleted Successfully");
	}
	
	// API for put(update) the details in the database
	
	 // http://localhost:8080/jobopening/updatedetails/1
	 // http://192.168.1.2:8080/rz/jobopening/updatedetails/1
	
    @PutMapping("/updatedetails/{jobOpeningId}")
    public ResponseEntity<String>  UpdateJob(@PathVariable Integer jobOpeningId,@RequestBody JobOpening jobopeningdetails,@RequestHeader(name = "Authorization") String session)
	{      
    	try
    	{
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
	 Optional<JobOpening> jobop=jobopeningrepository.findById(jobOpeningId);
		
 		if (jobop.isEmpty()) {
             throw new RuntimeException("Candidate not found!");
         }
		//function calling for Update JobOpening Details By Id
	   JobopeningService.UpdateJob(jobOpeningId, jobopeningdetails);
	   
	    Recentactivities recentactivities = new Recentactivities();
		Integer loginId = userId;
		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
		String username = user.getUsername();
		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
		recentactivities.setMessage( username+" updated 1 job opening through the Edit");
		recentactivitiesRepository.save(recentactivities);
	   
	    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tPUT-JOBOPENING-DETAIL-BY-ID: "+jobOpeningId+" STATUS: " + HttpStatus.OK);
	   return ResponseEntity.status(HttpStatus.OK).body("JobOpening Updated Successfully");
	}
	 catch (RuntimeException e) 
		{
		    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPUT-JOBOPENING-DETAIL-BY-ID: " + jobOpeningId + " STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Jobopening id is not found/Session token is not equal:"+e);
		}
	catch (Exception e) {
		// TODO: handle exception
		//String message="something went Wrong"+e.getMessage();
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tPUT-JOBOPENING-DETAIL-BY-ID: "+jobOpeningId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		return ResponseEntity
				  .status(HttpStatus.INTERNAL_SERVER_ERROR)
				  .body("Somthing went wrong:"+e);
	}
	
	}
    
    // API for Get(read) the details by Id from the database
	
    // http://localhost:8080/jobopening/getdetails/1
    // http://192.168.1.2:8080/rz/jobopening/getdetails/1
    
    @GetMapping("/getdetails/{jobOpeningId}")
	public  ResponseEntity<?> GetById(@PathVariable int jobOpeningId,@RequestHeader(name = "Authorization") String session){

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
		
			
			//function calling for Get a JobOpenoing Details By Id
			
			Optional<JobOpening> jobOpening = JobopeningService.callId(jobOpeningId);
			if(jobOpening.isEmpty())
			{
			    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-JOBOPENING-DETAIL-BY-ID: "+jobOpeningId+" STATUS: " + HttpStatus.NO_CONTENT);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Id NotFound");
			}
			 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-JOBOPENING-DETAIL-BY-ID: "+jobOpeningId+" STATUS: " + HttpStatus.OK);
			//System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\tAPI\tADD-USER-POINT-LOG[USER ID: " + newUserPointLog.get("userId") + "\t TEST ID: " + newUserPointLog.get("testId") + "]\t" + httpStatus);
			return ResponseEntity.status(HttpStatus.OK).body(jobOpening);
			}
			//return JobopeningService.getAlldetails();
		     catch (RuntimeException e) 
		    {
		    System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-JOBOPENING-DETAIL-BY-ID: " + jobOpeningId + " STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Session token is not equal:"+e);
		    }
			catch (Exception e) {
				// TODO: handle exception
				//String message="something went Wrong"+e.getMessage();
				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI\tGET-JOBOPENING-DETAIL-BY-ID: "+jobOpeningId+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
				return ResponseEntity
						  .status(HttpStatus.INTERNAL_SERVER_ERROR)
						  .body("Somthing went wrong:"+e);
			}
		
		//return JobopeningService.callId(id);
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
 							
// 	        response.setContentType("text/csv");
// 	        response.setHeader("Content-Disposition", "attachment; filename=jobopeningreport"+cdate+".csv");
 			
 	        // write to csv file //
 	        
// 	        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),CsvPreference.STANDARD_PREFERENCE);
 	         
 				StringWriter writer = new StringWriter();
		        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
		                .withHeader("jobOpeningId","client Name","position Title","skillset","job Description","experience","japanese Level","country","salary","state Name","job Opening Status","assigned By","job Type","city","requirement Received Date"));
 	 
// 	        String[]  headings = {"jobOpeningId","client Name","position Title","skillset","job Description","experience","japanese Level","country","salary","state Name","job Opening Status","assigned By","job Type","city","requirement Received Date"};
 	        
// 	        String[] jobopeningrecord = {"jobOpeningId","clientName","positionTitle","skillset","jobDescription","experience","japaneseLevel","country","salary","stateName","jobOpeningStatus","assignedBy","jobType","city","requirementReceivedDate"};
 	              
 	 
 	        
 	        Integer loginId = userId;
 			LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
 			String roles = user.getUserRole();
 			
 			if(roles.equals("admin") || roles.equals("zenuser"))
 			{
 	        
 	        List<JobOpening> jobOpening = JobopeningService.exportcsv();
 	        
 	       if(null!=jobOpening && !jobOpening.isEmpty()){
	            for (JobOpening job : jobOpening) {
	            	csvPrinter.printRecord(job.getJobOpeningId(),job.getClientName(),job.getPositionTitle(),job.getSkillset(),job.getJobDescription(),job.getExperience(),job.getJapaneseLevel(),job.getCountry(),job.getSalary(),job.getStateName(),job.getJobOpeningStatus(),job.getAssignedBy(),job.getJobType(),job.getCity(),job.getRequirementReceivedDate());
	              }
	            }
 	      csvPrinter.close();
 			}
 			else {
 				List<JobOpening> jobOpening = JobopeningService.exportcsvbyuser(userId);
 	 	        
 	 	       if( null!=jobOpening && !jobOpening.isEmpty()){
 		            for (JobOpening job : jobOpening) {
 		            	csvPrinter.printRecord(job.getJobOpeningId(),job.getClientName(),job.getPositionTitle(),job.getSkillset(),job.getJobDescription(),job.getExperience(),job.getJapaneseLevel(),job.getCountry(),job.getSalary(),job.getStateName(),job.getJobOpeningStatus(),job.getAssignedBy(),job.getJobType(),job.getCity(),job.getRequirementReceivedDate());

 		              }
 		            }
 	 	     csvPrinter.close();
 			}
 			
 			String cdate = new String(new Timestamp(System.currentTimeMillis()).toString());
			HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=jobopeningreport"+cdate+".csv");
 	 
 	        
 	        Recentactivities recentactivities = new Recentactivities();
//       		Integer loginId = userId;
//       		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
       		String username = user.getUsername();
       		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
       		recentactivities.setMessage( username+" downloaded 1 csv through the job opening export");
       		recentactivitiesRepository.save(recentactivities);
       		
       		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-JOBOPENING-EXPORT-CSV:  STATUS: " + HttpStatus.OK);
       		
       		
       		return ResponseEntity.status(HttpStatus.OK)
		             .headers(headers)
		             .contentType(MediaType.parseMediaType("text/csv"))
		             .body(writer.toString().getBytes());
 			}catch(Exception e) {
 				e.printStackTrace();
 				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-JOBOPENING-EXPORT-CSV:  STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
 				 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    		              .body("jobopeningcsv reportcsv failed"+e);
 		     }
 	      
    }
    @PostMapping("/filterbyexport/csv")
    public ResponseEntity<?> filterbyexportCsv(@RequestBody List<JobOpening> jobopenings,@RequestHeader(name = "Authorization") String session){
 		   
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
		                .withHeader("jobOpeningId","client Name","position Title","skillset","job Description","experience","japanese Level","country","salary","state Name","job Opening Status","assigned By","job Type","city","requirement Received Date"));
 	 

 	        List<JobOpening> jobOpening = jobopenings ;
 	        
 	       if(null!=jobOpening && !jobOpening.isEmpty()){
	            for (JobOpening job : jobOpening) {
	            	csvPrinter.printRecord(job.getJobOpeningId(),job.getClientName(),job.getPositionTitle(),job.getSkillset(),job.getJobDescription(),job.getExperience(),job.getJapaneseLevel(),job.getCountry(),job.getSalary(),job.getStateName(),job.getJobOpeningStatus(),job.getAssignedBy(),job.getJobType(),job.getCity(),job.getRequirementReceivedDate());
	              }
	            }
 	      csvPrinter.close();
 			
 			String cdate = new String(new Timestamp(System.currentTimeMillis()).toString());
			HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=jobopeningreport"+cdate+".csv");
 	 
 	        
 	        Recentactivities recentactivities = new Recentactivities();
       		Integer loginId = userId;
       		LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
       		String username = user.getUsername();
       		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
       		recentactivities.setMessage( username+" downloaded 1 csv through the job opening export");
       		recentactivitiesRepository.save(recentactivities);
       		
       		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-JOBOPENING-FILTERBY-EXPORT-CSV:  STATUS: " + HttpStatus.OK);
       		
       		
       		return ResponseEntity.status(HttpStatus.OK)
		             .headers(headers)
		             .contentType(MediaType.parseMediaType("text/csv"))
		             .body(writer.toString().getBytes());
 			}catch(Exception e) {
 				e.printStackTrace();
 				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-JOBOPENING-FILTERBY-EXPORT-CSV:  STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
 				 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    		              .body("jobopeningcsv reportcsv failed"+e);
 		     }
 	      
    }

}
