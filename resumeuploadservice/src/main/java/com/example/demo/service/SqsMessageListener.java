package com.example.demo.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.example.demo.entity.Resume;
import com.example.demo.model.ResumeData;
import com.example.demo.model.ResumeS3Event;
import com.example.demo.repository.ResumeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@EnableScheduling
public class SqsMessageListener {

	@Autowired
    private AmazonSQS amazonSQS;
	
	@Autowired private S3Service s3Service;
	
	@Autowired private ResumeRepository repo;
	
	@Autowired private ResumeParserService resumeParserService;

	@Value("${aws.sqs.queue-url}")
	private String queueUrl;

	@Value("${aws.s3.base-url}")
	private String s3BaseUrl;
	
	@Value("${spring.profiles.active}")
	private String activeProfile;
    
    @Scheduled(fixedDelay = 60000) // runs every 5 seconds
    @Async
    public void pollMessages() {
        ReceiveMessageRequest request = new ReceiveMessageRequest()
                .withQueueUrl(queueUrl)
                .withMaxNumberOfMessages(10)
                .withWaitTimeSeconds(1);

        List<Message> messages = amazonSQS.receiveMessage(request).getMessages();

        ObjectMapper objectMapper = new ObjectMapper();

        for (Message msg : messages) {
            try {
                // Parse the custom S3 event JSON from message body
                ResumeS3Event event = objectMapper.readValue(msg.getBody(), ResumeS3Event.class);
                String bucket = event.getBucket();
                String key = event.getKey();

                // Fetch resume file from S3 and parse
                try (InputStream resumeStream = s3Service.getObjectStream(bucket, key)) {
                    ResumeData data = resumeParserService.parse(resumeStream);
                    String fullFileUrl;
                    
                    // Update database
                    if ("prod".equalsIgnoreCase(activeProfile)) {
                    	fullFileUrl = s3BaseUrl + "/" + key;
                    } else {
                    	fullFileUrl = s3BaseUrl + "/" + bucket + "/" + key;
                    }
                    
                    Resume resume = repo.findByFileUrl(fullFileUrl);
                   
                    if (resume != null) {
                        resume.setStatus("Processed");
                        resume.setApplicantName(data.getName());
                        repo.save(resume);
                    } else {
                        System.err.println("Resume not found for fileUrl: " + key);
                    }
                }

                // Delete message from SQS
                amazonSQS.deleteMessage(queueUrl, msg.getReceiptHandle());
                System.out.println("Processed and deleted message: " + msg.getBody());

            } catch (Exception e) {
                System.err.println("Error processing message: " + msg.getBody());
                e.printStackTrace();
            }
        }
    }
}
