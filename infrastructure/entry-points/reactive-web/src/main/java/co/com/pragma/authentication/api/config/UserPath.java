package co.com.pragma.authentication.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths")
public class UserPath {
    private String users;
    private String usersByEmails;
    private String userByDocument;
}

