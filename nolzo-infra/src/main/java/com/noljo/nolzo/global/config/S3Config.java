package com.noljo.nolzo.global.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3Client amazonS3Client() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().withRegion(region);

        if (StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey)) {
            AWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
            builder.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials));
        } else {
            builder.withCredentials(DefaultAWSCredentialsProviderChain.getInstance());
        }

        return (AmazonS3Client) builder.build();
    }
}
