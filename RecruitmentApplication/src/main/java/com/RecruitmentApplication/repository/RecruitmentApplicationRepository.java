package com.RecruitmentApplication.repository;



import java.util.ArrayList;
import java.util.List;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.RecruitmentApplication.model.CandidateProfiles;




@Repository
public interface RecruitmentApplicationRepository extends CrudRepository<CandidateProfiles,Integer>{

//	@Query(
//		value ="",
//		nativeQuery =true)
//   public boolean existByCandidateId(Integer sessionToken);
	
	 @Query(
			 value="SELECT * FROM candidate_profiles  WHERE deleted = false",
			 nativeQuery= true
			 )
	  public  List<CandidateProfiles> getcandidate();
	 @Query(
			 value="SELECT * FROM candidate_profiles  WHERE deleted = false AND user_id=?1",
			 nativeQuery= true
			 )
	  public  List<CandidateProfiles> getcandidatebyuser(Integer userId);
	
	 @Query(
			value = "SELECT candidate_id FROM candidate_profiles WHERE first_name = ?1",
			nativeQuery = true			
			)	
		public Integer findCandidateIdByFirstName(String firstName);
	
	    //public CandidateProfiles findByEmailId(String emailId);

		//public CandidateProfiles findByPhoneNumber(String primaryContact);
		
		@Query(
				value = "SELECT first_name FROM candidate_profiles WHERE candidate_id = ?1",
				nativeQuery = true)
		
		public String findCandiateNameByCandiateId(Integer candidateId);

	
//		@Modifying
//		@Query(
//				value="UPDATE candidate_profiles SET deleted = true WHERE candidate_id = ?1",
//				nativeQuery = true)
//	 
//	    void deleteById(Integer candidateId);
		@Query(
				value = "SELECT candidate_id FROM candidate_profiles WHERE  email_id= ?1",
				nativeQuery = true			
				)	  
	    public Integer findByEmailId(String emailId);
	    @Query(
				value = "SELECT candidate_id FROM candidate_profiles WHERE primary_contact = ?1",
				nativeQuery = true			
				)	
		public Integer findByPhoneNumber(Long primaryContact);
	    @Query(
				value = "SELECT candidate_id FROM candidate_profiles WHERE  candidate_name= ?1",
				nativeQuery = true			
				)	
	    public Integer findByName(String candidateName);
	    @Query(
				value = "SELECT email_id FROM candidate_profiles WHERE  candidate_id= ?1",
				nativeQuery = true			
				)	
	    public String findByEmail(Integer candidateIdinInterview);
	    
	    @Query(
				value = "SELECT COUNT(candidate_id) FROM candidate_profiles WHERE candidate_status='New' AND deleted = false",
				nativeQuery = true			
				)
	   public Integer countofNewStatus();
	   @Query(
				value = "SELECT COUNT(candidate_id) FROM candidate_profiles WHERE candidate_status='In Review'AND deleted = false",
				nativeQuery = true			
				)
	   public Integer countofInReviewStatus();
	   @Query(
				value = "SELECT COUNT(candidate_id) FROM candidate_profiles WHERE candidate_status='Available'AND deleted = false",
				nativeQuery = true			
				)
	   public Integer countofAvailableStatus();
	   @Query(
				value = "SELECT COUNT(candidate_id) FROM candidate_profiles WHERE candidate_status='Interview'AND deleted = false",
				nativeQuery = true			
				)
	   public Integer countofInterviewStatus();
	   @Query(
				value = "SELECT COUNT(candidate_id) FROM candidate_profiles WHERE candidate_status='Not Available'AND deleted = false",
				nativeQuery = true			
				)
	   public Integer countofNotAvailableStatus();
	   @Query(
				value = "SELECT COUNT(candidate_id) FROM candidate_profiles WHERE candidate_status='On Hold'AND deleted = false",
				nativeQuery = true			
				)
	   public Integer countofOnHoldStatus();
	   @Query(
				value = "SELECT COUNT(candidate_id) FROM candidate_profiles WHERE candidate_status='Offered'AND deleted = false",
				nativeQuery = true			
				)
	   public Integer countofOfferedStatus();
	   @Query(
				value = "SELECT COUNT(candidate_id) FROM candidate_profiles WHERE candidate_status='Hired'AND deleted = false",
				nativeQuery = true			
				)
	   public Integer countofHiredStatus();
	   @Query(
				value = "SELECT COUNT(candidate_id) FROM candidate_profiles WHERE candidate_status='Blocklist/Trash'AND deleted = false",
				nativeQuery = true			
				)
	   public Integer countofBlocklistTrashStatus();
	   @Query(
				value = "SELECT COUNT(candidate_id) FROM candidate_profiles WHERE  deleted = false",
				nativeQuery = true			
				)
	   public Integer countoftotalcandidateStatus();
	   @Query(
				value = "SELECT distinct(position_title) as Positions, count(position_title)  as members FROM candidate_profiles  where deleted='false' group by position_title order by position_title",
				nativeQuery = true			
				)
	   public List positiontitle();
	   @Query(
				value = "SELECT distinct(position_title) as Positions FROM candidate_profiles where deleted='false' group by position_title order by position_title",
				nativeQuery = true			
				)
	   public List<String> positions();
	   @Query(
				value = "SELECT  count(position_title)  as members FROM candidate_profiles where deleted='false' group by position_title order by position_title",
				nativeQuery = true			
				)
	   public List<Integer> members();
	   @Query(
				value = "SELECT distinct(primary_skill) as Skills, count(primary_skill)  as members FROM candidate_profiles  where deleted='false' group by primary_skill  order by primary_skill ",
				nativeQuery = true			
				)
	   public List primaryskill();
	   @Query(
				value = "SELECT distinct(primary_skill) as Positions FROM candidate_profiles where deleted='false' group by primary_skill order by primary_skill",
				nativeQuery = true			
				)
	   public List<String> skills();
	   @Query(
				value = "SELECT  count(primary_skill)  as members FROM candidate_profiles where deleted='false' group by primary_skill order by primary_skill",
				nativeQuery = true			
				)
	   public List<Integer> skillcount();
	   @Query(
				 value="SELECT * FROM candidate_profiles  WHERE deleted = false order by candidate_id",
				 nativeQuery= true
				 )
	  public  List<CandidateProfiles> getcandidatecsv();
	  @Query(
				 value="SELECT * FROM candidate_profiles  WHERE user_id=?1 AND deleted = false  order by candidate_id",
				 nativeQuery= true
				 )
	  public  List<CandidateProfiles> getcandidatecsvbyuser(Integer userId); 

}




