package za.tomvuma.logisticshub.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data // generates getters, setters, toString, equals, hashCode
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long expiration;
}

