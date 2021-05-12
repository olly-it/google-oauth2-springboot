package it.olly.springboot.googleauth2.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;

@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties
public class ApplicationConfig {

    // Constants
    public static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    public static final String USER_IDENTIFIER_KEY = "MY_TEST_USER"; // it must be a unique id for the user
    public static final String APPLICATION_NAME = "SSD OAuth Spring App";
    public static final String PARENT_FOLDER_NAME = "OAuth Demo App Uploaded";

    // config params
    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    @Value("${google.secret.key.path}")
    private Resource driveSecretKeys;

    @Value("${google.oauth.checktoken.uri}")
    private String CHECKTOKEN_URI;

    public String getCALLBACK_URI() {
        return CALLBACK_URI;
    }

    public Resource getDriveSecretKeys() {
        return driveSecretKeys;
    }

    public String getCHECKTOKEN_URI() {
        return CHECKTOKEN_URI;
    }

}