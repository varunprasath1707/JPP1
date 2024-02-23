package com.RecruitmentApplication.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.RecruitmentApplication.model.*;
import com.RecruitmentApplication.repository.*;

@Service
public class LoginCredentialsService {
	
	@Autowired
	 private LoginCredentialsRepository loginCredentialsRepository;
	
	public LoginCredentials findByusername(String username) {
		try {
		
		return loginCredentialsRepository.findByusername(username);
	}catch (Exception e) {
		// TODO: handle exception
		throw new RuntimeException("Failed to get  jobopening List Data from database:\t" + e.getMessage());
	}
	}
}
