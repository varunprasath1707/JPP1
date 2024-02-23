package com.RecruitmentApplication.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RecruitmentApplication.repository.InterviewRepository;
import com.RecruitmentApplication.repository.RecentactivitiesRepository;
import com.RecruitmentApplication.repository.RecruitmentApplicationRepository;

@Service
public class DashboardService {
	
	@Autowired
	 private InterviewRepository interviewRepository;
	
	@Autowired
	private RecruitmentApplicationRepository recruitmentapplicationRepository;
	
	@Autowired
	private RecentactivitiesRepository recentactivitiesRepository;
	
	
	public Map<String, Object> dashboardService(){
		
		try {
		
		Map<String, Object> content =  new HashMap<>();
		List<String> positions = new ArrayList<String>();
		List<Integer> members = new ArrayList<Integer>();
		
		positions = recruitmentapplicationRepository.positions();
		members = recruitmentapplicationRepository.members();
		//ArrayList positiontitle[] = recruitmentapplicationRepository.positiontitle();
				 
	    ArrayList<Map<String, Object>> designationCounts = new ArrayList<Map<String, Object>>();
	    
	    Map<String, Object> designation =  new HashMap<>();
	    
	    
	    
	    for (int i = 0; i < positions.size(); i++) {
	    	designation.put(positions.get(i), members.get(i));
	    }
	    
	    designationCounts.add(designation);  
	    
	    ArrayList<Map<String, Object>> candidateStatusCounts = new ArrayList<Map<String, Object>>();
	    
	    Map<String,Object> candidateStatus =  new HashMap<>();
	    
	    Integer newCandidateStatus = recruitmentapplicationRepository.countofNewStatus();
		Integer availableCandidateStatus =recruitmentapplicationRepository.countofAvailableStatus();
		Integer InReviewCandidateStatus =recruitmentapplicationRepository.countofInReviewStatus();
		Integer NotAvailableCandidateStatus =recruitmentapplicationRepository.countofNotAvailableStatus();
		Integer InterviewCandidateStatus =recruitmentapplicationRepository.countofInterviewStatus();
		Integer OnHoldCandidateStatus =recruitmentapplicationRepository.countofOnHoldStatus();
		Integer OfferedCandidateStatus =recruitmentapplicationRepository.countofOfferedStatus();
		Integer HiredCandidateStatus =recruitmentapplicationRepository.countofHiredStatus();
		Integer BlocklistTrashCandidateStatus =recruitmentapplicationRepository.countofBlocklistTrashStatus();
		Integer totalCandidateStatus = recruitmentapplicationRepository.countoftotalcandidateStatus();
		
		candidateStatus.put("New",newCandidateStatus);
		candidateStatus.put("InReview",InReviewCandidateStatus);
		candidateStatus.put("Available",availableCandidateStatus);
		candidateStatus.put("Interview",InterviewCandidateStatus);
		candidateStatus.put("NotAvailable",NotAvailableCandidateStatus);
		candidateStatus.put("OnHold",OnHoldCandidateStatus);
		candidateStatus.put("Offered",OfferedCandidateStatus);
		candidateStatus.put("Hired",HiredCandidateStatus);
		candidateStatus.put("Blocklist/Trash",BlocklistTrashCandidateStatus);
		candidateStatus.put("TotalCandidateStatus",totalCandidateStatus);
		
		candidateStatusCounts.add(candidateStatus);
		
		List<String> skillset = new ArrayList<String>();
		List<Integer> skillcounts = new ArrayList<Integer>();
		
		skillset = recruitmentapplicationRepository.skills();
		skillcounts = recruitmentapplicationRepository.skillcount();
		
		 ArrayList<Map<String, Object>> primarySkillCounts = new ArrayList<Map<String, Object>>();
		    
		    Map<String, Object> skills =  new HashMap<>();
		     
		    for (int i = 0; i < skillset.size(); i++) {
		    	skills.put(skillset.get(i), skillcounts.get(i));
		    }
		    
		    primarySkillCounts.add(skills); 
		 
		 ArrayList<Map<String, Object>> interviewStatusCounts = new ArrayList<Map<String, Object>>();
			
		 Map<String,Object> interviewStatus =  new HashMap<>();
		 
		 
		 Integer candidateMissinginterviewStatus = interviewRepository.countofinterviewcandidateMissingStatus();		 
		 Integer ScheduledinterviewStatus = interviewRepository.countofinterviewScheduledStatus();
		 Integer completeinterviewStatus = interviewRepository.countofinterviewCompleteStatus();
		 Integer totalinterviewStatus = interviewRepository.countoftotalinterviewStatus();
		 
		 
		 interviewStatus.put("Scheduled", ScheduledinterviewStatus);
		 interviewStatus.put("Complete", completeinterviewStatus);
		 interviewStatus.put("Candidate Missing", candidateMissinginterviewStatus);
		 interviewStatus.put("total interview Status", totalinterviewStatus);
		 
		 interviewStatusCounts.add(interviewStatus);
		 
		 ArrayList<Map<String, Object>> scheduledinterviewrviews  = new ArrayList<Map<String, Object>>(); 
		 
		 List<List> scheduledinterviewrview = new ArrayList();
				 
		 scheduledinterviewrview = interviewRepository.getScheduledinterview();
		 
		 ArrayList<Object> scheduledinterviewrviewsData = new ArrayList<Object>();
		 
		 
		  for(int rows=0;rows< scheduledinterviewrview.size();rows++)
		  {
			  List interview1 = scheduledinterviewrview.get(rows);
			  
			  int count =0;
			 
			  Map<String,Object> interviews =  new HashMap<>();
			  
			  for(int record=0;record<interview1.size();record++)
			  {
				  if (count ==0)interviews.put("Interviewname",interview1.get(record));
				  if (count ==1)interviews.put("Interviewfrom",interview1.get(record));
				  if (count ==2)interviews.put("Interviewto",interview1.get(record));
				  if (count ==3)interviews.put("Candidatename",interview1.get(record));
				  if (count ==4)interviews.put("Clientname",interview1.get(record));
				  if (count ==5)interviews.put("Postingtitle",interview1.get(record));
				  if (count ==6)interviews.put("Interviewstatus",interview1.get(record));
				   count++;
				   
				   scheduledinterviewrviews.add(interviews);
			  }
			  scheduledinterviewrviewsData.add(interviews); 
			}
		  
		 ArrayList<Map<String, Object>> recentactivities = new ArrayList<Map<String, Object>>();
		 
		 List<List> recentactivity = new ArrayList();
		 
		 recentactivity = recentactivitiesRepository.getAllRecentActivities();
		 
		 ArrayList<Object> recentactivitiesData = new ArrayList<Object>();

		 
		  for(int rows=0;rows< recentactivity.size();rows++)
		  {
			  List activity = recentactivity.get(rows);
			  
			  int count1 =0;
			  
			  Map<String,Object> activities =  new HashMap<>();
			 
			  for(int record=0;record<activity.size();record++)
			  {

				  if (count1 ==0)activities.put("ActivityId",activity.get(record));
				  if (count1 ==1)activities.put("Datetime",activity.get(record));
				  if (count1 ==2)activities.put("message",activity.get(record));
				  count1++;
				   
				  recentactivities.add(activities);
			  }
			  recentactivitiesData.add(activities);
			  }
		  
		content.put("DesignationCounts",designationCounts);
		content.put("CandidateStatusCounts",candidateStatusCounts);
		content.put("PrimarySkillCounts",primarySkillCounts);
		content.put("InterviewStatusCounts", interviewStatusCounts);
		content.put("ScheduledinterviewrviewsData", scheduledinterviewrviewsData);
		content.put("RecentActivities", recentactivitiesData);
	
        
		return content;
		
		}catch (Exception e) {
			//  handle exception
			throw new RuntimeException("Failed to get  daseboard List Data from database:\t" + e.getMessage());
			
		}
}
	}

