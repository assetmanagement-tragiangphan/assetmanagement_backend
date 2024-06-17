package com.nashtech.rookies.assetmanagement.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.cookie")
@Getter
@Setter
public class CookieProperties {
    private String name;
    private Long maxAge;
    private String path;
    private String sameSite;
    private Boolean httpOnly;
    private Boolean secure;
}
