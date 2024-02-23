package com.RecruitmentApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.RecruitmentApplication.model.Interview;
import com.RecruitmentApplication.model.Recentactivities;

@Repository
public interface RecentactivitiesRepository  extends CrudRepository<Recentactivities,Integer>{
	
	@Query(
			value = "SELECT * FROM recent_activities WHERE TO_DATE(date_time, 'YYYY-MM-DD') >= CURRENT_DATE - INTERVAL '7 days'AND TO_DATE(date_time, 'YYYY-MM-DD') <= CURRENT_DATE;",
			nativeQuery = true			
			)	
	public List<List> getAllRecentActivities() ;
	

}