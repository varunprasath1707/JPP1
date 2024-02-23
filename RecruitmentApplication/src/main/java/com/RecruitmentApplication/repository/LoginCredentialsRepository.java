package com.RecruitmentApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.RecruitmentApplication.model.LoginCredentials;

@Repository

public interface LoginCredentialsRepository extends CrudRepository< LoginCredentials,Integer> {
	

	public LoginCredentials findByusername(String username);
	
	@Query(
			 value="SELECT *from login_credentials WHERE login_id =?1",
			 nativeQuery= true
			 )
	  public LoginCredentials findloginid(Integer loginId);
}
