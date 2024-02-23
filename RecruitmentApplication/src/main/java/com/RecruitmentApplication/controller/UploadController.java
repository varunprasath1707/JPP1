//package com.RecruitmentApplication.controller;


//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.List;


//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;

//import com.RecruitmentApplication.exception.ResourceNotFoundException;
//import com.RecruitmentApplication.model.CandidateProfiles;
//import com.RecruitmentApplication.model.UploadFile;
//import com.RecruitmentApplication.repository.RecruitmentApplicationRepository;
//import com.RecruitmentApplication.repository.SessionTokenRepository;
//import com.RecruitmentApplication.repository.UploadFileRepository;
//import com.RecruitmentApplication.service.UploadService;

//@CrossOrigin
//@RestController
//@RequestMapping
//public class UploadController {
	
	
//	@Autowired
	//private SessionTokenRepository SessionTokenRepository;
//	@Autowired
//	private UploadService uploadService;
	
//	@PostMapping("/uploadResume/{candidateId}")
//	public ResponseEntity<?> uploadFile(@RequestParam("files")List<MultipartFile> multipartFiles,@PathVariable Integer candidateId,@RequestHeader(name = "Authorization") String session ) throws IOException{
//		
//		if(session.equals(null) || session.isEmpty()) 
//		{
//			System.out.println("Session token is empty!");
//			return null;
//		}
//		
//		Integer userId = SessionTokenRepository.findByAllSessionToken(session);
//		String stoken = SessionTokenRepository.getSessionTokenById(userId);
//		//System.out.println("IN DB ="+stoken);
//		//System.out.println("Input = "+session );
//		if(!session.equals(stoken)) {
//			System.out.println("Session token is Not equal!");
//			return null;
//		}
		
	  //	if (multipartFiles.isEmpty()) {
            // Handle empty file case
		//	System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\tAPI\tupload:"+ HttpStatus.BAD_REQUEST );
         //   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Resume selected for upload.");
        //}

//		try{
//			
//			uploadService.addResume(multipartFiles, candidateId);
//			 
//	
//		  System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId +"\tAPI\tupload:"+ HttpStatus.OK);
//		return ResponseEntity.status(HttpStatus.OK).body("Resume uploaded successfully.");
//    } catch (IOException e) {
//        // Handle file processing error
//    	System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\t\tUSERID: "+userId + "\tAPI\tupload:"+ HttpStatus.INTERNAL_SERVER_ERROR );
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the file.");
//    }
//		
//	}
	
//	@GetMapping("/getResume/{resumeId}")
	//public ResponseEntity<?> getFile(@PathVariable Integer resumeId){
		
	//	try {
		
		
	//	UploadFile uploadFile = uploadFileRepository.findById(resumeId).orElseThrow(() -> new ResourceNotFoundException("UploadFile not exist with id :" + resumeId));
        
	 //  return ResponseEntity.status(HttpStatus.OK).body(uploadFile);
		
//	}catch (Exception e) {
        // Handle file processing error
  //  	System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\tAPI\tgetfile:"+ HttpStatus.INTERNAL_SERVER_ERROR );
   //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fileId not get.");
   // }
//	}	
//	@GetMapping("/downloadResume/{fileName}")
	//public ResponseEntity<?> downloadFile(){
		 
//		return null;
		
		
//	}

//}
