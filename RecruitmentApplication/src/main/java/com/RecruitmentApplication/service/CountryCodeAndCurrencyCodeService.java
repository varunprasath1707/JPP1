package com.RecruitmentApplication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RecruitmentApplication.model.CandidateProfiles;
import com.RecruitmentApplication.model.CountryCodeAndCurrencyCode;
import com.RecruitmentApplication.repository.CountryCodeAndCurrencyCodeRepository;

@Service
public class CountryCodeAndCurrencyCodeService {
	
	@Autowired
	private CountryCodeAndCurrencyCodeRepository countrycodeandcurrencycode;
	
	public List<CountryCodeAndCurrencyCode> getAllCountryCode(){
		try {
			return (List<CountryCodeAndCurrencyCode>)countrycodeandcurrencycode.findAll(); 
			
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException("Failed to get countrycode List Data from database:\t" + e.getMessage());
		}
		}

}
