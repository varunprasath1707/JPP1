package com.RecruitmentApplication.controller;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.RecruitmentApplication.model.CandidateProfiles;
import com.RecruitmentApplication.model.CountryCodeAndCurrencyCode;
import com.RecruitmentApplication.repository.SessionTokenRepository;
import com.RecruitmentApplication.service.CountryCodeAndCurrencyCodeService;

@CrossOrigin
@RestController
// Base URL for All APIs in Candidate profiles page
@RequestMapping("/api/v1")
public class CountryCodeAndCurrencyCodeController {
	
	@Autowired
	private SessionTokenRepository SessionTokenRepository;
	
	@Autowired
	private CountryCodeAndCurrencyCodeService countrycodeandcurrencycode;
	
	@GetMapping("/candidateProfiles/countrycode")  //URL for Get API
	public ResponseEntity<?> getAllContryCodeAndCurrencyCode(@RequestHeader(name = "Authorization") String session){
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
          throw new RuntimeException();
		}
		
			List<CountryCodeAndCurrencyCode> country = countrycodeandcurrencycode.getAllCountryCode();
			if(country.isEmpty())
			{
				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-COUNTRYCODE-DETAILS "+" STATUS: " + HttpStatus.NO_CONTENT);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
			}
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId+"\tAPI:\tGET-ALL-COUNTRYCODE-DETAILS " +" STATUS: " + HttpStatus.OK);
			return ResponseEntity.status(HttpStatus.OK).body(country);
			}
			//return JobopeningService.getAlldetails();
		
		   catch (RuntimeException e) {
			   System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-ALL-COUNTRYCODE-DETAILS "+" STATUS: " + HttpStatus.NO_CONTENT);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Session token is not equal");
			   
		}
			catch (Exception e) {
				// TODO: handle exception
				//String message="something went Wrong"+e.getMessage();
				System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+"\tAPI:\tGET-ALL-COUNTRYCODE-DETAILS "+" STATUS: " + HttpStatus.INTERNAL_SERVER_ERROR);
				return ResponseEntity
						  .status(HttpStatus.INTERNAL_SERVER_ERROR)
						  .body("Somthing went wrong");
			}
		
		
	}

}
