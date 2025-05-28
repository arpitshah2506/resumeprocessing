package com.example.lambda;

import java.io.FileWriter;
import java.io.PrintWriter;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;


public class ResumeLambdaHandler implements RequestHandler<S3Event, String> {
    @Override
    public String handleRequest(S3Event event, Context context) {
        try {
            String bucket = event.getRecords().get(0).getS3().getBucket().getName();
            String key = event.getRecords().get(0).getS3().getObject().getKey();

            String logLine = "{\"bucket\":\"" + bucket + "\",\"key\":\"" + key + "\"}";

            // Write to a file in /tmp (the only writable dir in AWS Lambda)
            try (PrintWriter out = new PrintWriter(new FileWriter("/tmp/lambda-s3-log.txt", true))) {
                out.write(logLine);
            }

			/*
			 * AmazonSQS sqsClient = AmazonSQSClientBuilder.standard()
			 * .withEndpointConfiguration(new
			 * AwsClientBuilder.EndpointConfiguration("http://host.docker.internal:4566",
			 * "us-east-1")) .withCredentials(new AWSStaticCredentialsProvider(new
			 * BasicAWSCredentials("fake", "fake"))) .build();
			 */
            
            AmazonSQS sqsClient = AmazonSQSClientBuilder.standard()
            	    .withRegion("ap-south-1") // Your AWS region
            	    .build();
            String queueUrl = sqsClient.getQueueUrl("resume-queue").getQueueUrl();

            sqsClient.sendMessage(queueUrl, logLine);
            
            return "Message sent to SQS for: " + key;
        } catch (Exception e) {
        	    try (PrintWriter out = new PrintWriter(new FileWriter("/tmp/lambda-s3-log.txt", true))) {
        	        out.write("Exception: " + e.getMessage() + "\n");
        	    } catch (Exception ignored) {}
        	    context.getLogger().log("Exception in Lambda: " + e.getMessage());
        	    return "Error";
        	}
        }
    }
