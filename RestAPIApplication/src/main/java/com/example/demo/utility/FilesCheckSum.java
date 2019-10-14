package com.example.demo.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class FilesCheckSum {

	public static String calculateCheckSum(String filePath, MessageDigest messageDigest) throws IOException {
		try (DigestInputStream digestInputStream = new DigestInputStream(new FileInputStream(filePath),
				messageDigest)) {
			while (digestInputStream.read() != -1); 
			messageDigest = digestInputStream.getMessageDigest();
		}

		// bytes to hex
		StringBuilder result = new StringBuilder();
		for (byte b : messageDigest.digest()) {
			result.append(String.format("%02x", b));
		}
		return result.toString();
	}
}
