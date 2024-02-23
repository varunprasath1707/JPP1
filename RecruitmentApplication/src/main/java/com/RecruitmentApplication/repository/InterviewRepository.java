package com.RecruitmentApplication.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.RecruitmentApplication.model.CandidateProfiles;
import com.RecruitmentApplication.model.Interview;

@Repository
public interface InterviewRepository extends CrudRepository<Interview,Integer> {

	
	@Query(
			value = "SELECT * FROM Interview WHERE candidate_id = ?1 AND deleted = false",
			nativeQuery = true			
			)	
	public List<Interview> findAllByCandidateId(Integer candidateId);
	
	@Query(
			value = "SELECT candidate_id FROM Interview WHERE interview_id = ?1 AND deleted = false",
			nativeQuery = true			
			)	
	public Integer findByCandidateId(Integer interviewId);

	@Query(
			 value="SELECT * FROM interview  WHERE deleted = false",
			 nativeQuery= true
			 )
	  public  List<Interview> getinterview();
	@Query(
			 value="SELECT COUNT(interview_id) FROM interview WHERE interview_status='Scheduled' AND deleted = false",
			 nativeQuery= true
			 )
	 public Integer countofinterviewScheduledStatus();
	 @Query(
			 value="SELECT COUNT(interview_id) FROM interview WHERE interview_status='Complete' AND deleted = false",
			 nativeQuery= true
			 )
	 public Integer countofinterviewCompleteStatus();
	@Query(
			 value="SELECT COUNT(interview_id) FROM interview WHERE interview_status='Candidate Missing' AND deleted = false",
			 nativeQuery= true
			 )
	 public Integer countofinterviewcandidateMissingStatus();
	@Query(
			 value="SELECT COUNT(interview_id) FROM interview WHERE  deleted = false",
			 nativeQuery= true
			 )
	 public Integer countoftotalinterviewStatus();
	@Query(
			 value="SELECT  interview_name,interview_from,interview_to,candidate_name,client_name,posting_title,interview_status FROM interview where interview_status ='Scheduled' AND deleted='false'",
			 nativeQuery= true
			 )
	  public List<List>  getScheduledinterview();
	@Query(
			 value="SELECT * FROM interview  WHERE deleted = false order by interview_id",
			 nativeQuery= true
			 )
	  public  List<Interview> getinterviewexportcsv();
	@Query(
			 value="SELECT * FROM interview  WHERE user_id=?1  AND deleted = false",
			 nativeQuery= true
			 )
	  public  List<Interview> getinterviewbyuser(Integer userId);
	@Query(
			 value="SELECT * FROM interview  WHERE user_id=?1  AND deleted = false order by interview_id",
			 nativeQuery= true
			 )
	  public  List<Interview> getinterviewexportcsvbyuser(Integer userId);

}
