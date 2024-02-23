package com.RecruitmentApplication.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.RecruitmentApplication.model.SessionToken;

@Repository
public interface SessionTokenRepository extends CrudRepository<SessionToken, Integer> {

	@Query(
		value = "select session_token from session_token where user_id = ?1",
		nativeQuery = true			
		)	
	public String getSessionTokenById(int userId);

	@Query(
			value = "select user_id from session_token where session_token = ?1",
			nativeQuery = true			
			)	
		public Integer findByAllSessionToken(String sessionToken);
		

	
}
