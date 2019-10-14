package com.example.demo.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.helper.Checksum;

public interface FileManagementService {

	String calculateCheckSum(String checkSumType, String filePath) throws Exception;

	void uploadFile(String filePath, MultipartFile file) throws IOException;

	void downloadFile(String fileName, String key, HttpServletResponse httpServletResponse) throws Exception;

	void storeCheckSumInFile(String filePath, String checkSum, String checksumType) throws Exception;

	void removeData(String filePath) throws FileNotFoundException, Exception;

	Checksum retrieveCheckSumOfAFile(String filePath) throws Exception;

}
