package com.example.demo.service.implementation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.constants.FileManagementConstants;
import com.example.demo.service.FileManagementService;
import com.example.demo.utility.FilesCheckSum;

@Service
public class FileManagementServiceImpl implements FileManagementService {

	// Calculates checksum for MD5 and SHA256 hashing algorithms
	@Override
	public String calculateCheckSum(String checkSumType, String filePath) throws NoSuchAlgorithmException, IOException {

		MessageDigest messageDigest = null;
		String checkSum = "";

		if (checkSumType.equals(FileManagementConstants.MD5)) {
			messageDigest = MessageDigest.getInstance(FileManagementConstants.MD5);
		} else if (checkSumType.equals(FileManagementConstants.SHA256)) {
			messageDigest = MessageDigest.getInstance(FileManagementConstants.SHA256);
		}

		// Calculating checksum of the uploaded file
		checkSum = FilesCheckSum.calculateCheckSum(filePath, messageDigest);

		return checkSum;
	}

	// File Upload Service
	@Override
	public void uploadFile(String filePath, MultipartFile file) throws IOException {

		File uploadedFile = new File(filePath);
		uploadedFile.createNewFile();
		FileOutputStream outStream = new FileOutputStream(uploadedFile);
		outStream.write(file.getBytes());
		outStream.close();
	}

	// File download service
	@Override
	public void downloadFile(String filePath, HttpServletResponse httpServletResponse) throws IOException {

		ServletOutputStream out = httpServletResponse.getOutputStream();
		File resource = new File(filePath);

		if (resource.exists()) {
			byte[] data = readFile(filePath);
			httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + filePath + "\"");
			//httpServletResponse.setHeader("checksum", calculateCheckSum(, filePath));
			httpServletResponse.setContentLength(data.length);
			out.write(data);
			out.flush();
		}

	}

	public byte[] readFile(String filePath) throws IOException {
		byte[] data = null;
		Path path = Paths.get(filePath).normalize();
		data = Files.readAllBytes(path);
		return data;
	}
}