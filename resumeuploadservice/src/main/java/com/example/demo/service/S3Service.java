package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import jakarta.annotation.PostConstruct;

@Service
public class S3Service {

    @Value("${aws.accessKey}") private String accessKey;
    @Value("${aws.secretKey}") private String secretKey;
    @Value("${aws.region}") private String region;
    @Value("${aws.s3.bucket}") private String bucket;
    @Value("${aws.s3.endpoint}") private String endpoint;
    private AmazonS3 s3Client;

    @PostConstruct
    private void init() {
    	AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region);

        if (endpoint != null && !endpoint.isBlank()) {
            builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region));
            builder.withPathStyleAccessEnabled(true);
        }

        s3Client = builder.build();
    }

    public String upload(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        s3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
        return s3Client.getUrl(bucket, fileName).toString();
    }
    
    public InputStream getObjectStream(String bucket, String key) {
        S3Object object = s3Client.getObject(bucket, key);
        return object.getObjectContent();
    }
}
