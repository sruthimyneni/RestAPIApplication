package com.example.demo.controller;

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
import com.example.demo.helper.Checksum;
import com.example.demo.service.FileManagementService;

@Controller
@RequestMapping("/testApplicationContext")
public class FileManagementController {

	@Autowired
	FileManagementService fileManagementService;

	@RequestMapping(value = "/uploadData/{key}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadData(@PathVariable("key") String key,
			@RequestHeader("checksum-type") String checksumType, @RequestParam("file") MultipartFile file)
			throws Exception {

		HttpHeaders responseHeader = new HttpHeaders();
		String checkSum = "";

		if (!key.isEmpty() && key.equals(FileManagementConstants.KEY)) {
			String filePath = FileManagementConstants.LOCAL_FILE_PATH.concat(file.getOriginalFilename());

			fileManagementService.uploadFile(filePath, file);
			checkSum = fileManagementService.calculateCheckSum(checksumType, filePath);
			fileManagementService.storeCheckSumInFile(filePath, checkSum, checksumType);
		} else {
			return ResponseEntity.notFound().build();
		}

		responseHeader.set("checksum", checkSum);
		return ResponseEntity.ok().headers(responseHeader).body("File uploaded successfully");
	}

	@RequestMapping(value = "downloadData/{key}", method = RequestMethod.GET)
	public void downloadData(@PathVariable("key") String key, @RequestParam String fileName,
			HttpServletResponse httpServletResponse) throws Exception {

		String filePath = FileManagementConstants.LOCAL_FILE_PATH.concat(fileName);

		fileManagementService.downloadFile(filePath, key, httpServletResponse);
	}

	@RequestMapping(value = "removeData/{key}", method = RequestMethod.GET)
	public ResponseEntity<String> removeData(@PathVariable("key") String key, @RequestParam String fileName)
			throws Exception {

		String filePath = FileManagementConstants.LOCAL_FILE_PATH.concat(fileName);
		if (!key.isEmpty() && key.equals(FileManagementConstants.KEY)) {
			fileManagementService.removeData(filePath);
		} else {
			return ResponseEntity.notFound().build();
		}

		HttpHeaders responseHeader = new HttpHeaders();

		Checksum checksum = fileManagementService.retrieveCheckSumOfAFile(filePath);

		responseHeader.set("checksum", checksum.getChecksum());
		return ResponseEntity.ok().headers(responseHeader).body("Data is deleted from the given file");
	}

}
