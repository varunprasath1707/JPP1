package com.RecruitmentApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.RecruitmentApplication.model.Interview;
import com.RecruitmentApplication.model.JobOpening;

@Repository
public interface JobOpeningRepository extends CrudRepository<JobOpening, Integer>{

	@Query(
			value = "SELECT job_opening_id FROM job_opening WHERE client_name = ?1",
			nativeQuery = true			
			)	
		public Integer findJobOpeningIdByClientName(String clientName);
	
	@Query(
			value = "SELECT client_name FROM job_opening WHERE job_opening_id = ?1",
			nativeQuery = true			
			)	
		public String findClientNameByJobOpeningId(Integer clientId);
	
	@Query(
			 value="SELECT * FROM job_opening  WHERE deleted = false",
			 nativeQuery= true
			 )
	  public  List<JobOpening> getjobopening();
	@Query(
			 value="SELECT * FROM job_opening  WHERE deleted = false order by job_opening_id",
			 nativeQuery= true
			 )
	  public  List<JobOpening> getexportcsv();
	@Query(
			 value="SELECT * FROM job_opening  WHERE user_id= ?1 AND deleted = false",
			 nativeQuery= true
			 )
	  public  List<JobOpening> getjobopeningbyuser(Integer userId);
	@Query(
			 value="SELECT * FROM job_opening  WHERE user_id= ?1 AND deleted = false order by job_opening_id ",
			 nativeQuery= true
			 )
	  public  List<JobOpening> getexportcsvbyuser(Integer userId);

}
