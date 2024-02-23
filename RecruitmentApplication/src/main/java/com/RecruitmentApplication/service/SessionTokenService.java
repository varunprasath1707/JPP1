package com.RecruitmentApplication.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.RecruitmentApplication.exception.ResourceNotFoundException;
import com.RecruitmentApplication.model.*;
import com.RecruitmentApplication.repository.*;


@Service
public class SessionTokenService {
	
	
	public static SessionToken findbyuserId;
	@Autowired
	private SessionTokenRepository sessionTokenRepository;
	
	
	public String generateSessionToken(int loginId){
	
		try {
	 String token = UUID.randomUUID().toString();
	 String createdOn = new Timestamp(System.currentTimeMillis()).toString();
	 
	 SessionToken sessionToken = new SessionToken(
			 loginId,
			 token,
			 createdOn
			 );
	 
	 sessionTokenRepository.save(sessionToken);
	 return token;
	
	 
	}catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to sessiontoken List  Data from database:\t" + e.getMessage());
	
	}
	}

    public void deleteSessionToken(@PathVariable Integer userId ){
	   try {
    	SessionToken sessionToken = sessionTokenRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("SessionToken not exist with id :" + userId));
		sessionTokenRepository.delete(sessionToken);
		Map<String,Boolean> response = new HashMap<>();
		response.put("deleted",Boolean.TRUE);
  }catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to sessiontoken List  Data from database:\t" + e.getMessage());
	
	}
	}

	public static SessionToken findbysessiontoken(String session) {
		// TODO Auto-generated method stub
		
		return null;
	}
	

}
