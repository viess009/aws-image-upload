package com.amigoscode.awsimageupload.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AmazonConfig {

	@Bean
	public AmazonS3 s3() {
		AWSCredentials awsCredentials = null;
		try {
			awsCredentials = new BasicAWSCredentials(getKey("AWSAccessKeyId"), getKey("AWSSecretKey"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
	}
	
	private String getKey(String keyType) throws Exception {
		File file = new File("C:\\Users\\James\\.aws\\credentials\\rootkey.csv");
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith(keyType)) {
				br.close();
				return line.split("=")[1]; // returns value of key value pair
			}
			line = br.readLine();
		}
		br.close();
		return null;
	}
	
}
