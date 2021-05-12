package it.olly.springboot.googleauth2.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

@Service
public class GoogleAuthorizationService {

    private final static Logger logger = LoggerFactory.getLogger(GoogleAuthorizationService.class);
    private GoogleAuthorizationCodeFlow flow;
    private GoogleClientSecrets clientSecrets;

    @Autowired
    private ApplicationConfig config;

    @PostConstruct
    public void init() throws Exception {
        InputStreamReader reader = new InputStreamReader(config.getDriveSecretKeys()
                .getInputStream());

        clientSecrets = GoogleClientSecrets.load(ApplicationConfig.JSON_FACTORY, reader);
        flow = new GoogleAuthorizationCodeFlow.Builder(ApplicationConfig.HTTP_TRANSPORT, ApplicationConfig.JSON_FACTORY, clientSecrets, ApplicationConfig.SCOPES)
                .build();
    }

    private String getNewToken(String refreshToken) throws IOException {
        logger.debug("new access token request from refresh token");
        String clientId = clientSecrets.getDetails()
                .getClientId();
        String clientSecret = clientSecrets.getDetails()
                .getClientSecret();

        TokenResponse tokenResponse = new GoogleRefreshTokenRequest(ApplicationConfig.HTTP_TRANSPORT, ApplicationConfig.JSON_FACTORY, refreshToken, clientId, clientSecret)
                .setScopes(ApplicationConfig.SCOPES)
                .setGrantType("refresh_token")
                .execute();

        return tokenResponse.getAccessToken();
    }

    public String getAccessToken(HttpServletRequest request) {
        return (String) request.getSession()
                .getAttribute("at");
    }

    public boolean isTokenExpiredLowLevelCall(String accessToken) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(config.getCHECKTOKEN_URI() + "?accessToken=" + accessToken);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            return (con.getResponseCode() == 200);
        } catch (Exception e) {
            return false;
        } finally {
            if (con != null)
                con.disconnect();
        }
    }

    /**
     * also renew the access token if expired (using the refresh token)
     * 
     * @param request
     * @return
     * @throws Exception
     */
    public boolean isUserAuthenticated(HttpServletRequest request) throws Exception {
        String at = getAccessToken(request);
        if (at == null)
            return false;

        if (isTokenExpiredLowLevelCall(at)) {
            String rt = (String) request.getSession()
                    .getAttribute("rt");
            at = getNewToken(rt);
            if (at == null)
                return false;
            request.getSession()
                    .setAttribute("at", at);
        }

        return true;
    }

    public String authenticateUserViaGoogle() throws Exception {
        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        String redirectUrl = url.setRedirectUri(config.getCALLBACK_URI())
                .setAccessType("offline")
                .build();
        logger.debug("redirectUrl, " + redirectUrl);
        return redirectUrl;
    }

    public Credential exchangeCodeForTokens(String code) throws Exception {
        // exchange the code against the access token and refresh token
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(config.getCALLBACK_URI())
                .execute();
        return flow.createAndStoreCredential(tokenResponse, null);
    }

    public void removeUserSession(HttpServletRequest request) throws Exception {
        request.getSession()
                .removeAttribute("at");
        request.getSession()
                .removeAttribute("rt");

    }

}