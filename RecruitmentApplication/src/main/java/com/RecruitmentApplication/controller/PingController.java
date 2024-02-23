package com.RecruitmentApplication.controller;

import java.sql.Timestamp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@CrossOrigin
@Controller
public class PingController {
	@GetMapping("/ping")
	public ResponseEntity<?> pingRequest() {
		try {
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\tAPI:\tPING\t" +" STATUS: "+ HttpStatus.OK);
			return ResponseEntity
					.status(HttpStatus.OK)
					.body("Pong!");
		} catch (Exception e) {
			System.out.println(new Timestamp(System.currentTimeMillis()).toString() + "\tAPI:\tPING\t" +" STATUS: "+ HttpStatus.INTERNAL_SERVER_ERROR);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong.");
		}
	}

}
