package com.example.demo.service.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.constants.FileManagementConstants;
import com.example.demo.helper.Checksum;
import com.example.demo.service.FileManagementService;
import com.example.demo.utility.FilesCheckSum;

@Service
public class FileManagementServiceImpl implements FileManagementService {

	// Calculates checksum for MD5 and SHA256 hashing algorithms
	@Override
	public String calculateCheckSum(String checkSumType, String filePath) throws Exception {

		MessageDigest messageDigest = null;
		String checkSum = "";

		if (checkSumType.equals(FileManagementConstants.MD5) || checkSumType.equals(FileManagementConstants.SHA256)) {
			messageDigest = MessageDigest.getInstance(checkSumType);
		} else {
			throw new Exception("Checksum type in request header can be either MD5 or SHA256");
		}

		// Calculating checksum of the uploaded file
		checkSum = FilesCheckSum.calculateCheckSum(filePath, messageDigest);

		return checkSum;
	}

	// Stores checksums in text file
	@Override
	public void storeCheckSumInFile(String filePath, String checkSum, String checkSumType) throws Exception {

		try {
			BufferedReader file = new BufferedReader(new FileReader(FileManagementConstants.CHECKSUM_LOCAL_FILE_PATH));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			// Checking if the file and checksum already exists
			while ((line = file.readLine()) != null) {
				if (line.contains(filePath)) {
					String[] fileAndCheckSum = line.split("\\+");
					line = line.replace(fileAndCheckSum[1], checkSum);
				}
				inputBuffer.append(line);
				inputBuffer.append('\n');
			}
			file.close();
			String inputStr = inputBuffer.toString();

			if (!inputStr.isEmpty()) {
				inputStr = inputStr.concat("\n");
			}

			if (!inputStr.contains(filePath)) {
				inputStr = inputStr.concat(filePath).concat("+").concat(checkSum).concat("+").concat(checkSumType);
			}

			FileOutputStream fileOut = new FileOutputStream(FileManagementConstants.CHECKSUM_LOCAL_FILE_PATH);
			fileOut.write(inputStr.getBytes());
			fileOut.close();
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
	}

	// Retrieve checksum of a file
	@Override
	public Checksum retrieveCheckSumOfAFile(String filePath) throws Exception {
		Checksum checkSum = new Checksum();
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(FileManagementConstants.CHECKSUM_LOCAL_FILE_PATH));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] fileAndCheckSum = line.split("\\+");
				if (filePath.equals(fileAndCheckSum[0])) {
					checkSum.setChecksum(fileAndCheckSum[1]);
					checkSum.setChecksumType(fileAndCheckSum[2]);
				}
			}
			reader.close();
		} catch (Exception e) {
			throw new Exception("Check Checksum file path");
		}
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
	public void downloadFile(String filePath, String key, HttpServletResponse httpServletResponse) throws Exception {

		ServletOutputStream out = httpServletResponse.getOutputStream();
		File resource = new File(filePath);
		Checksum checksum = retrieveCheckSumOfAFile(filePath);

		if (resource.exists()) {
			byte[] data = readFile(filePath);
			httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + resource.getName() + "\"");
			httpServletResponse.setHeader("checksum", checksum.getChecksum());
			httpServletResponse.setHeader(FileManagementConstants.STATUS_RESPONSE_HEADER, HttpStatus.OK.toString());
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

	// Removes data from file
	@Override
	public void removeData(String filePath) throws Exception {

		File resource = new File(filePath);
		if (resource.exists()) {
			PrintWriter writer = new PrintWriter(resource);
			writer.close();
		} else {
			throw new Exception("File doesnot exist");
		}
		Checksum checksum = retrieveCheckSumOfAFile(filePath);
		String checksumValue = calculateCheckSum(checksum.getChecksumType(), filePath);
		storeCheckSumInFile(filePath, checksumValue, checksum.getChecksumType());
	}
}
