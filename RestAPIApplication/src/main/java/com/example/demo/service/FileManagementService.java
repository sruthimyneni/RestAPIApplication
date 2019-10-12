package com.example.demo.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

public interface FileManagementService {

	String calculateCheckSum(String checkSumType, String filePath) throws NoSuchAlgorithmException, IOException, NoSuchAlgorithmException, IOException;

	void uploadFile(String filePath, MultipartFile file) throws IOException;

	void downloadFile(String fileName, HttpServletResponse httpServletResponse) throws IOException;

}
