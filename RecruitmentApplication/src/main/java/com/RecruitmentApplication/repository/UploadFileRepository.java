package com.RecruitmentApplication.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.RecruitmentApplication.model.UploadFile;


@Repository
public interface UploadFileRepository  extends CrudRepository<UploadFile,Integer>{
	
	public UploadFile findByCandidateId(Integer candidateId);

	@Query(
			value = "SELECT * FROM upload_file WHERE candidate_id = ?1",
			nativeQuery = true			
			)	
	public UploadFile findAllByCandidateId(Integer candidateId); 

}
