package me.zedaster.articleservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "s3")
@ConfigurationPropertiesScan
@Data
public class S3Configuration {
    /**
     * Access key for the S3 storage.
     */
    private String accessKey;

    /**
     * Secret key for the S3 storage.
     */
    private String secretKey;

    /**
     * Endpoint URL for the S3 storage.
     */
    private String endpointUrl;

    /**
     * Bucket name for storing content.
     * @see me.zedaster.articleservice.service.ContentService
     */
    private String contentBucketName;
}
