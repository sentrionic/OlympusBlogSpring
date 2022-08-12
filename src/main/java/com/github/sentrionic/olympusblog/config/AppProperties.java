package com.github.sentrionic.olympusblog.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppProperties {
    @Value("${spring.redis.host}")
    private String redisUrl;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretAccessKey;

    @Value("${cloud.aws.storageBucketName}")
    private String awsStorageBucketName;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Value("${spring.mail.username}")
    private String gmailUser;

    @Value("${spring.mail.password}")
    private String gmailPassword;
}
