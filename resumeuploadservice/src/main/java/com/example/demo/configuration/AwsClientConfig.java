package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

@Configuration
public class AwsClientConfig {

	@Value("${aws.region}")
    private String region;

    @Value("${aws.sqs.endpoint:}") // blank if not provided
    private String sqsEndpoint;

	
    @Bean
    AmazonSQS amazonSQS() {
    	AmazonSQSClientBuilder builder = AmazonSQSClientBuilder.standard()
                .withRegion(region);

        if (sqsEndpoint != null && !sqsEndpoint.isBlank()) {
            builder.setEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(sqsEndpoint, region));
        }

        return builder.build();
    }
}
