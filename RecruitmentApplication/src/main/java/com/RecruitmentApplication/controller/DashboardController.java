package com.RecruitmentApplication.controller;

import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RecruitmentApplication.model.LoginCredentials;
import com.RecruitmentApplication.repository.LoginCredentialsRepository;
import com.RecruitmentApplication.repository.SessionTokenRepository;
import com.RecruitmentApplication.service.DashboardService;


@CrossOrigin
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
	@Autowired
	 private DashboardService dashboardService;
	@Autowired
	private  SessionTokenRepository SessionTokenRepository;
	@Autowired
	private LoginCredentialsRepository loginCredentialsRepository;
	
	@GetMapping("/getalldetails")
	public ResponseEntity<?> getAllDetails(@RequestHeader(name = "Authorization") String session){
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
			
		 
		    Map<String, Object> status = dashboardService.dashboardService();
		
		
		
		System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-DASEBOARD-DETAILS "+"STATUS: " + HttpStatus.OK);

		return ResponseEntity.status(HttpStatus.OK).body(status);
			}else {
				
				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-DASEBOARD-DETAILS "+"STATUS: " + HttpStatus.OK);

				return ResponseEntity.status(HttpStatus.OK).body("User Access Denied");
				
			}
		
	}catch (Exception e) {
			// TODO: handle exception
			//String message="something went Wrong"+e.getMessage();
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-ALL-DASEBOARD-DETAILS "+"STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR +e);
			return ResponseEntity
					  .status(HttpStatus.INTERNAL_SERVER_ERROR)
					  .body("Something went wrong:"+e);
	 }
	

 }
}
