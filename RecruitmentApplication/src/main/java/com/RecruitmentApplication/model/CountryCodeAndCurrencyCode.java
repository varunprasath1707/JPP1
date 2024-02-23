package com.RecruitmentApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="country_code_and_currency")
public class CountryCodeAndCurrencyCode {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer countryId;
	
	@Column(name="country_code")
	private String countryCode;
	
	@Column(name="currency_code")
	private String currencyCode;
	
	@Column(name="country")
	private String country;
	
	@Column(name="country_abbreviation")
	private String countryabbreviation;
	

	public CountryCodeAndCurrencyCode() {
		super();
	}


	public CountryCodeAndCurrencyCode(String countryCode, String currencyCode, String country,
			String countryabbreviation) {
		super();
		this.countryCode = countryCode;
		this.currencyCode = currencyCode;
		this.country = country;
		this.countryabbreviation = countryabbreviation;
	}


	public Integer getCountryId() {
		return countryId;
	}


	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}


	public String getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}


	public String getCurrencyCode() {
		return currencyCode;
	}


	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getCountryabbreviation() {
		return countryabbreviation;
	}


	public void setCountryabbreviation(String countryabbreviation) {
		this.countryabbreviation = countryabbreviation;
	}
	
	

	
	
	
	

}
