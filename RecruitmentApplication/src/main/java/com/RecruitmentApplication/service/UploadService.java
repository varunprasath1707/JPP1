package com.RecruitmentApplication.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.RecruitmentApplication.exception.ResourceNotFoundException;
import com.RecruitmentApplication.model.CandidateProfiles;
import com.RecruitmentApplication.model.UploadFile;
import com.RecruitmentApplication.repository.RecruitmentApplicationRepository;
import com.RecruitmentApplication.repository.UploadFileRepository;

@Service
public class UploadService {

	@Autowired
	private UploadFileRepository uploadFileRepository;
	@Autowired
	private RecruitmentApplicationRepository recruitmentapplicationRepository;
	
	@Value("${upload.directory}")
	private String uploadDirectory;
	
	public void addResume(List<MultipartFile> multipartFiles,Integer candidateId) throws IOException {
		
		UploadFile uf = uploadFileRepository.findByCandidateId(candidateId);
		
				 
		if(uf == null) {
			
	        List<String> fileNames = new ArrayList();
			
			UploadFile uploadFile = new UploadFile();
		
			uploadFile.setCandidateId(candidateId);
			int count=0;
			for(MultipartFile file : multipartFiles) {
	            
				byte[] data = file.getBytes();
				
				String fileName = StringUtils.cleanPath(file.getOriginalFilename());
				
//				Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName);
				
				Path fileStroage = Paths.get(uploadDirectory+""+fileName);
				Files.write(fileStroage, data);
				//System.out.println("Path : " +fileStroage);
				
				if (count ==0) uploadFile.setResume1(fileName);
				if (count ==1) uploadFile.setResume2(fileName);
				if (count ==2) uploadFile.setResume3(fileName);
				if (count ==3) uploadFile.setResume4(fileName);
				if (count ==4) uploadFile.setResume5(fileName);
				if (count ==5) uploadFile.setResume6(fileName);
				//Files.write(fileStroage, data1);
				count++;
			}
			 //store filename in database
			  uploadFileRepository.save(uploadFile);
			  CandidateProfiles candidateProfiles = recruitmentapplicationRepository.findById(candidateId)
						.orElseThrow(() -> new ResourceNotFoundException("CandidateProfiles not exist with id :" + candidateId));
			  candidateProfiles.setResume(uploadFile.getResumeId().toString());
			  recruitmentapplicationRepository.save(candidateProfiles);			
		}
		else {
			 List<String> fileNames = new ArrayList();
			 
			 int count=0;
			 for(MultipartFile file : multipartFiles) {
		            
					byte[] data = file.getBytes();
					
					String fileName = StringUtils.cleanPath(file.getOriginalFilename());
					//Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName);
					Path fileStroage = Paths.get(uploadDirectory+""+fileName);
					
					Files.write(fileStroage, data);
					//System.out.println("Path : " +fileStroage);
					
					if (count ==0) uf.setResume1(fileName);
					if (count ==1) uf.setResume2(fileName);
					if (count ==2) uf.setResume3(fileName);
					if (count ==3) uf.setResume4(fileName);
					if (count ==4) uf.setResume5(fileName);
					if (count ==5) uf.setResume6(fileName);
					//Files.write(fileStroage, data1);
					count++;
				}
				 //store filename in database
				  uploadFileRepository.save(uf);
//				  CandidateProfiles candidateProfiles = recruitmentapplicationRepository.findById(candidateId)
//							.orElseThrow(() -> new ResourceNotFoundException("CandidateProfiles not exist with id :" + candidateId));
//				  candidateProfiles.setResume(uploadFile.getResumeId().toString());
//				  recruitmentapplicationRepository.save(candidateProfiles);			
			
		}
	}
	public UploadFile getResumebycandidateId(Integer candidateId){ 
		 
		 UploadFile uploadfile = uploadFileRepository.findAllByCandidateId(candidateId);
		      
   	 return uploadfile; 
}
	public  void updateResume(MultipartFile multipartFile,Integer candidateId,String fileName)throws IOException {
		   
		   UploadFile uploadfile = uploadFileRepository.findByCandidateId(candidateId);
		   
		  
		  if(fileName.equals(uploadfile.getResume1())) {
		  
	     		String fileName1 = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				//Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName1);
				Path fileStroage = Paths.get(uploadDirectory+""+fileName1);
				
	            //file store in a server
			   multipartFile.transferTo(fileStroage);
			   //fileName store in a database
			   uploadfile.setResume1(fileName1);
			   uploadFileRepository.save(uploadfile);
			   
	      }
		  if(fileName.equals(uploadfile.getResume2())){
			  String fileName1 = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			   // Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName1);
				Path fileStroage = Paths.get(uploadDirectory+""+fileName1);
				
	           //file store in a server
			   multipartFile.transferTo(fileStroage);
			   //fileName store in a database
			   uploadfile.setResume2(fileName1);
			   uploadFileRepository.save(uploadfile); 
			  
		  }
		  if(fileName.equals(uploadfile.getResume3())){
			  String fileName1 = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			  // Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName1);
				Path fileStroage = Paths.get(uploadDirectory+""+fileName1);
				
	        //file store in a server
			   multipartFile.transferTo(fileStroage);
			   //fileName store in a database
			   uploadfile.setResume3(fileName1);
			   uploadFileRepository.save(uploadfile);
		  }
		  if(fileName.equals(uploadfile.getResume4())){
			  String fileName1 = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			  //Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName1);
				Path fileStroage = Paths.get(uploadDirectory+""+fileName1);
				
	            //file store in a server
			   multipartFile.transferTo(fileStroage);
			   //fileName store in a database
			   uploadfile.setResume4(fileName1);
			   uploadFileRepository.save(uploadfile);
			   
		  }
		  if(fileName.equals(uploadfile.getResume5())){
			  String fileName1 = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			  //Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName1);
				Path fileStroage = Paths.get(uploadDirectory+""+fileName1);
				
	            //file store in a server
			   multipartFile.transferTo(fileStroage);
			   //fileName store in a database
			   uploadfile.setResume5(fileName1);
			   uploadFileRepository.save(uploadfile);
			   
		  }
		  if(fileName.equals(uploadfile.getResume6())){
			  
			  String fileName1 = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			 // Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName1);
				Path fileStroage = Paths.get(uploadDirectory+""+fileName1);
				
	            //file store in a server
			   multipartFile.transferTo(fileStroage);
			   //fileName store in a database
			   uploadfile.setResume6(fileName1);
			   uploadFileRepository.save(uploadfile);
			   
		  }
	   
	}
	
	public void addExtraResumes(List<MultipartFile> multipartFiles,Integer candidateId)throws IOException {
		
		UploadFile uploadfile = uploadFileRepository.findByCandidateId(candidateId);
		  
		    List<String> fileNames = new ArrayList();
		  
		   for(MultipartFile multipartFile : multipartFiles)
		   {
			// System.out.println("Resume1");
			 //System.out.println(multipartFile.getOriginalFilename());
		   if(uploadfile.getResume1() == null)
		   {
		   String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		   // Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName);
			Path fileStroage = Paths.get(uploadDirectory+""+fileName);
			
          //file store in a server
		   multipartFile.transferTo(fileStroage);
		   //fileName store in a database
		   uploadfile.setResume1(fileName);
		   uploadFileRepository.save(uploadfile);
		    
		   continue;
		   }
		   //System.out.println("Resume2");
		   //System.out.println(multipartFile.getOriginalFilename());
		   if(uploadfile.getResume2() == null)
		   {
		    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		   // Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName);
			Path fileStroage = Paths.get(uploadDirectory+""+fileName);
			
          //file store in a server
		   multipartFile.transferTo(fileStroage);
		   //fileName store in a database
		   uploadfile.setResume2(fileName);
		   uploadFileRepository.save(uploadfile);
		   continue;
		   
		   }	
		   //System.out.println("Resume3"); 
		   //System.out.println(multipartFile.getOriginalFilename());
		   if(uploadfile.getResume3() == null)
		   {
		   String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		   // Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName);
			Path fileStroage = Paths.get(uploadDirectory+""+fileName);
			
          //file store in a server
		   multipartFile.transferTo(fileStroage);
		   //fileName store in a database
		   uploadfile.setResume3(fileName);
		   uploadFileRepository.save(uploadfile);
		   continue;
		   }
		  // System.out.println("Resume4"); 
		   //System.out.println(multipartFile.getOriginalFilename());
		   if(uploadfile.getResume4() == null)
		   {
		   String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		   // Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName);
			Path fileStroage = Paths.get(uploadDirectory+""+fileName);
			
          //file store in a server
		   multipartFile.transferTo(fileStroage);
		   //fileName store in a database
		   uploadfile.setResume4(fileName);
		   uploadFileRepository.save(uploadfile);
		   continue;
		   }
		   //System.out.println("Resume5"); 
		   //System.out.println(multipartFile.getOriginalFilename());
		   if(uploadfile.getResume5() == null)
		   {
		   String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		   // Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName);
			Path fileStroage = Paths.get(uploadDirectory+""+fileName);
			
          //file store in a server
		   multipartFile.transferTo(fileStroage);
		   //fileName store in a database
		   uploadfile.setResume5(fileName);
		   uploadFileRepository.save(uploadfile);
		   continue;
		   }
		   //System.out.println("Resume6"); 
		   //System.out.println(multipartFile.getOriginalFilename());
		   if(uploadfile.getResume6() == null)
		   {
		   String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		   // Path fileStroage = Paths.get("src\\main\\resources\\static\\"+fileName);
			Path fileStroage = Paths.get(uploadDirectory+""+fileName);
			
          //file store in a server
		   multipartFile.transferTo(fileStroage);
		   //fileName store in a database
		   uploadfile.setResume6(fileName);
		   uploadFileRepository.save(uploadfile);	   
		   }
	
	}
	
}
   public void deleteResume(Integer candidateId,String fileName) {
		
		   UploadFile uploadfile = uploadFileRepository.findByCandidateId(candidateId);
		   
			  
			  if(fileName.equals(uploadfile.getResume1())) {
			  
		     		
				   //fileName store in a database
				   uploadfile.setResume1(null);
				   uploadFileRepository.save(uploadfile);
				   
		      }
			  if(fileName.equals(uploadfile.getResume2())){
				 
				   //fileName store in a database
				   uploadfile.setResume2(null);
				   uploadFileRepository.save(uploadfile); 
				  
			  }
			  if(fileName.equals(uploadfile.getResume3())){
				 
				   //fileName store in a database
				   uploadfile.setResume3(null);
				   uploadFileRepository.save(uploadfile);
			  }
			  if(fileName.equals(uploadfile.getResume4())){
				  
				   //fileName store in a database
				   uploadfile.setResume4(null);
				   uploadFileRepository.save(uploadfile);
				   
			  }
			  if(fileName.equals(uploadfile.getResume5())){
				 
				   //fileName store in a database
				   uploadfile.setResume5(null);
				   uploadFileRepository.save(uploadfile);
				   
			  }
			  if(fileName.equals(uploadfile.getResume6())){
				 
				   //fileName store in a database
				   uploadfile.setResume6(null);
				   uploadFileRepository.save(uploadfile);
				   
			  }
		
	}
}


