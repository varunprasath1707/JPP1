package com.RecruitmentApplication.controller;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.RecruitmentApplication.exception.ResourceNotFoundException;
import com.RecruitmentApplication.model.*;
import com.RecruitmentApplication.service.*;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.RecruitmentApplication.repository.*;

@CrossOrigin
@RestController
@RequestMapping("/loginCredentials")
public class LoginCredentialsController {
	
	@Autowired
	private LoginCredentialsService loginCredentialsService;
	@Autowired
	private SessionTokenService sessionTokenService;
	@Autowired
	private SessionTokenRepository SessionTokenRepository;
	@Autowired
	private RecentactivitiesRepository recentactivitiesRepository;
	@Autowired
	private LoginCredentialsRepository loginCredentialsRepository;
	
	
	
	
	    // API for Post(login) the details in the database
	
		// http://localhost:8080/loginCredentials/login
	
	  @PostMapping("/login")
	  public ResponseEntity<?>login(@RequestBody LoginCredentials loginCredentials){
		  
		    //BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			//String hashedPassword = bCryptPasswordEncoder.encode(loginCredentials.getPassword()).toString();
			//loginCredentials.setPassword(hashedPassword);
		  LoginCredentials user = loginCredentialsService.findByusername(loginCredentials.getUsername());
		  if (user == null || !user.getPassword().equals(loginCredentials.getPassword())) {
			  
	            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			  return  ResponseEntity.ok("InvalidUser");
	        }
	 	  try {
		  
		// Create session token and store it in session	
		  Integer userid = user.getLoginId();
		  String token = sessionTokenService.generateSessionToken(userid);
		  
		    Recentactivities recentactivities = new Recentactivities();
	 		String username = user.getUsername();
	 		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
	 		recentactivities.setMessage( username+" Login 1 user through the Login");
	 		recentactivitiesRepository.save(recentactivities);
		  
		  System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userid+"\tAPI:\tlOGIN "+" STATUS: "+HttpStatus.OK);
		  return ResponseEntity.status(HttpStatus.OK).body("Login Successfully '"+ token+"'");
	    		  
	    }catch (Exception e) {
			
	    	 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tlOGIN "+" STATUS: "+ HttpStatus.INTERNAL_SERVER_ERROR );
	    	return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Somthing went wrong:"+e);
		  
	  }
	  }
	  
	     // API for Delete(logout) the details in the database
		
		// http://localhost:8080/loginCredentials/logout/userId
	   //sessiontoken delete and logout
	
//	  @DeleteMapping("/logout/{userId}")
//	  public ResponseEntity<?>logout(@PathVariable Integer userId){
//		try {
//		 sessionTokenService.deleteSessionToken(userId);
//		 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\tAPI\tlogout:"+ HttpStatus.OK);
//		 return ResponseEntity.status(HttpStatus.OK).body("logout Suceessfully");
//		  
//	  }catch (Exception e) {
//			
//	    	 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\tAPI\tlogin:"+ HttpStatus.INTERNAL_SERVER_ERROR );
//	    	return ResponseEntity
//					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
//					  .body("Somthing went wrong");
//		  
//	  }
//	 }
	  @PostMapping("/logout")
	  public ResponseEntity<?>logout(@RequestHeader(name = "Authorization") String session){
		try {
			//System.out.println(session);
		 Integer userId = SessionTokenRepository.findByAllSessionToken(session);
		 sessionTokenService.deleteSessionToken(userId);
		 
		    Recentactivities recentactivities = new Recentactivities();
		    Integer loginId = userId;
		    LoginCredentials user =loginCredentialsRepository.findloginid(loginId);
	 		String username = user.getUsername();
	 		recentactivities.setDatetime(new Timestamp(System.currentTimeMillis()).toString());
	 		recentactivities.setMessage( username+" Logout 1 user through the Logout");
	 		recentactivitiesRepository.save(recentactivities);
		 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tlOGOUT "+" STATUS: "+ HttpStatus.OK);
		 return ResponseEntity.status(HttpStatus.OK).body("logout Suceessfully");
		 
	  }catch (Exception e) {
			
	    	 System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tlOGOUT "+" STATUS: "+ HttpStatus.INTERNAL_SERVER_ERROR );
	    	return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Somthing went wrong:"+e);
		 
	  }
	}

}

	  



		   
		   

	

	 


	
	
	
	


