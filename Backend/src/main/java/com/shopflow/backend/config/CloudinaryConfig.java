package com.shopflow.backend.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        String cleanedCloudName = cloudName != null ? cloudName.trim() : "";
        String cleanedApiKey = apiKey != null ? apiKey.trim() : "";
        String cleanedApiSecret = apiSecret != null ? apiSecret.trim() : "";

        return new Cloudinary(Map.of(
                "cloud_name", cleanedCloudName,
                "api_key", cleanedApiKey,
                "api_secret", cleanedApiSecret,
                "secure", true
        ));
    }
}
