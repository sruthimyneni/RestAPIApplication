package com.example.demo.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.constants.FileManagementConstants;
import com.example.demo.service.FileManagementService;

@Controller
@RequestMapping("/testApplicationContext")
public class FileManagementController {

	@Autowired
	FileManagementService fileManagementService;

	@RequestMapping(value = "/uploadData/{key}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadData(@PathVariable("key") String key,
			@RequestHeader("checksum-type") String checksumType, @RequestParam("file") MultipartFile file)
			throws NoSuchAlgorithmException, IOException {

		HttpHeaders responseHeader = new HttpHeaders();
		String checkSum = "";

		if (!key.isEmpty() && key.equals(FileManagementConstants.KEY)) {
			String filePath = FileManagementConstants.LOCAL_FILE_PATH.concat(file.getOriginalFilename());
			
			fileManagementService.uploadFile(filePath, file);
			checkSum = fileManagementService.calculateCheckSum(checksumType, filePath);
		}else {
			return ResponseEntity.notFound().build();
		}
		
		responseHeader.set("checksum", checkSum);
		return ResponseEntity.ok().headers(responseHeader).body("File uploaded successfully");
	}

	@RequestMapping(value = "downloadData/{key}", method = RequestMethod.GET)
	public ResponseEntity<String> downloadData(@PathVariable("key") String key, @RequestParam String fileName, HttpServletResponse httpServletResponse) throws IOException {

		String filePath = FileManagementConstants.LOCAL_FILE_PATH.concat(fileName);
		if (key.isEmpty()) {
			return ResponseEntity.notFound().build();
		}else if(!key.equals(FileManagementConstants.KEY)) {
			return ResponseEntity.badRequest().body(FileManagementConstants.WRONG_KEY_400);
		}
		
		fileManagementService.downloadFile(filePath, httpServletResponse);

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
