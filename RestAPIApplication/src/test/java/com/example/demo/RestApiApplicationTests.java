package com.example.demo;

import java.io.File;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.constants.FileManagementConstants;

import io.restassured.RestAssured;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RestApiApplicationTests {

	@Test
	public void test1uploadTestWhenKeyIsGivenMD5() {

		File testUploadFile = new File(FileManagementConstants.LOCAL_FILE_PATH_FOR_TEST);
		RestAssured.baseURI = "http://localhost:8080/testApplicationContext";

		RestAssured.given().header("checksum-type", FileManagementConstants.MD5).multiPart(testUploadFile).when()
				.post("/uploadData/" + FileManagementConstants.KEY).then().assertThat()
				.header(FileManagementConstants.CHECKSUM_RESPONSE_HEADER, "079f7410ce7a6491b8334364199ab317")
				.header(FileManagementConstants.STATUS_RESPONSE_HEADER, HttpStatus.OK.toString()).statusCode(200);
	}

	@Test
	public void test2uploadTestWhenKeyIsNotPresentMD5() {

		File testUploadFile = new File(FileManagementConstants.LOCAL_FILE_PATH_FOR_TEST);
		RestAssured.baseURI = "http://localhost:8080/testApplicationContext";

		RestAssured.given().header("checksum-type", FileManagementConstants.MD5).multiPart(testUploadFile).when()
				.post("/uploadData").then().statusCode(404);
	}

	@Test
	public void test3uploadTestWhenKeyIsGivenSHA256() {

		File testUploadFile = new File(FileManagementConstants.LOCAL_FILE_PATH_FOR_TEST_SHA256);
		RestAssured.baseURI = "http://localhost:8080/testApplicationContext";

		RestAssured.given().header("checksum-type", FileManagementConstants.SHA256).multiPart(testUploadFile).when()
				.post("/uploadData/" + FileManagementConstants.KEY).then().assertThat()
				.header(FileManagementConstants.CHECKSUM_RESPONSE_HEADER,
						"8beb9c9db1f2844ae6b1c40706275f5bc360601d1c68a04a8afeea4c13d9e9f1")
				.header(FileManagementConstants.STATUS_RESPONSE_HEADER, HttpStatus.OK.toString()).statusCode(200);
	}

	@Test
	public void test4uploadTestWhenKeyIsNotPresentSHA256() {

		File testUploadFile = new File(FileManagementConstants.LOCAL_FILE_PATH_FOR_TEST_SHA256);
		RestAssured.baseURI = "http://localhost:8080/testApplicationContext";

		RestAssured.given().header("checksum-type", FileManagementConstants.SHA256).multiPart(testUploadFile).when()
				.post("/uploadData").then().statusCode(404);
	}

	@Test
	public void test5downloadDataWithFileKey() {
		RestAssured.baseURI = "http://localhost:8080/testApplicationContext";

		RestAssured.given().when().get("/downloadData/" + FileManagementConstants.KEY + "?fileName=Read.txt").then()
				.assertThat()
				.header(FileManagementConstants.CHECKSUM_RESPONSE_HEADER, "079f7410ce7a6491b8334364199ab317")
				.header(FileManagementConstants.STATUS_RESPONSE_HEADER, HttpStatus.OK.toString())
				.statusCode(200);
	}

	@Test
	public void test6downloadDataWithoutKey() {
		RestAssured.baseURI = "http://localhost:8080/testApplicationContext";

		RestAssured.given().when().get("/downloadData?fileName=Read.txt").then().statusCode(404);
	}

	@Test
	public void test7removeDataWithKey() {
		RestAssured.baseURI = "http://localhost:8080/testApplicationContext";

		RestAssured.given().when().get("/removeData/" + FileManagementConstants.KEY + "?fileName=Read.txt").then()
				.assertThat()
				.header(FileManagementConstants.CHECKSUM_RESPONSE_HEADER, "d41d8cd98f00b204e9800998ecf8427e")
				.statusCode(200);
	}

	@Test
	public void test8removeDataWithoutKey() {
		RestAssured.baseURI = "http://localhost:8080/testApplicationContext";

		RestAssured.given().when().get("/removeData/?fileName=Read.txt").then()
				.statusCode(404);
	}

};