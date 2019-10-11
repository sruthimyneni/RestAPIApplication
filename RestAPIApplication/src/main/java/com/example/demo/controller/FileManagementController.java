package com.example.demo.controller;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.constants.FileManagementConstants;
import com.example.demo.utility.FilesCheckSum;

@Controller
@RequestMapping("/testApplicationContext")
public class FileManagementController {

	@RequestMapping(value = "/uploadData/{key}")
	public ResponseEntity<String> uploadData(@PathVariable("key") String key,
			@RequestHeader("checksum-type") String checksumType) throws NoSuchAlgorithmException, IOException {

		HttpHeaders responseHeader = new HttpHeaders();
		MessageDigest messageDigest = null;
		String checkSum = "", filePath = "";

		// Setting the instance to MD5/SHA256
		if (checksumType.equals(FileManagementConstants.MD5)) {
			messageDigest = MessageDigest.getInstance(FileManagementConstants.MD5);
		} else if (checksumType.equals(FileManagementConstants.SHA256)) {
			messageDigest = MessageDigest.getInstance(FileManagementConstants.SHA256);
		}

		// Calculating checksum of the uploaded file
		checkSum = FilesCheckSum.calculateCheckSum(filePath, messageDigest);

		// Add code for file upload

		responseHeader.set("checksum", checkSum);
		return ResponseEntity.ok().headers(responseHeader).body("File uploaded successfully");
	}

	@RequestMapping(value = "downloadData/{key}")
	public ResponseEntity<String> downloadData(@PathVariable("key") String key, String fileName) {

		if (key.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		// Add code for download

		return ResponseEntity.ok().body("File downloaded successfully");
	}

	/*
	 * @RequestMapping(value = "removeData/{key}") public ResponseEntity<String>
	 * removeData(@PathVariable("key") String key, String fileName){
	 * 
	 * }
	 */

}
