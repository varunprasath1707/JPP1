package com.RecruitmentApplication.repository;

import org.springframework.data.repository.CrudRepository;

import com.RecruitmentApplication.model.CandidateProfiles;
import com.RecruitmentApplication.model.CountryCodeAndCurrencyCode;

public interface CountryCodeAndCurrencyCodeRepository extends CrudRepository<CountryCodeAndCurrencyCode,Integer>{

}
