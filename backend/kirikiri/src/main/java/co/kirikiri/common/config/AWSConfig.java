package co.kirikiri.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    private final Regions region;
    private final String accessKey;
    private final String secretKey;

    public AWSConfig(@Value("${cloud.aws.region.static}") final String region,
                     @Value("${cloud.aws.credentials.access-key}") final String accessKey,
                     @Value("${cloud.aws.credentials.secret-key}") final String secretKey) {
        this.region = Regions.fromName(region);
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Bean
    public AmazonS3 amazonS3() {
        final BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
