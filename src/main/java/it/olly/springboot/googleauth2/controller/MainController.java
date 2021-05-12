package it.olly.springboot.googleauth2.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.model.File;

import it.olly.springboot.googleauth2.service.GDriveUtils;
import it.olly.springboot.googleauth2.service.GoogleAuthorizationService;

@Controller
public class MainController {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    GoogleAuthorizationService authorizationService;

    /**
     * Handles the root request. Checks if user is already authenticated via SSO.
     * 
     * @return
     * @throws Exception
     */
    @GetMapping("/")
    public void showHomePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("at: " + request.getSession()
                .getAttribute("at"));
        logger.debug("rt: " + request.getSession()
                .getAttribute("rt"));

        if (authorizationService.isUserAuthenticated(request)) {
            logger.debug("User is authenticated");

            String accessToken = authorizationService.getAccessToken(request);

            response.setContentType("text/html");
            response.getWriter()
                    .println("<html><body>");
            response.getWriter()
                    .println("USER AUTHENTICATED!<br>");
            response.getWriter()
                    .println(accessToken);
            response.getWriter()
                    .println("<br><br>");
            response.getWriter()
                    .println("<ul>");
            GDriveUtils gdu = new GDriveUtils(accessToken);
            List<File> files = gdu.listGDrive(10);
            for (File f : files) {
                response.getWriter()
                        .println("<li>" + f + "</li>");
            }
            response.getWriter()
                    .println("</ul>");
            response.getWriter()
                    .println("<br><a href='/logout'>Log Out</a>");
            response.getWriter()
                    .println("</body></html>");
        } else {
            logger.debug("User is not authenticated. Redirecting to login page...");
            response.sendRedirect("/login");
        }
    }

    /**
     * Directs to login
     * 
     * @return
     */
    @GetMapping("/login")
    public String displayLogin() {
        logger.debug("display login");
        return "login.html"; // containing <a> href link to "/googlesignin"
    }

    /**
     * Calls the Google OAuth service to authorize the app
     * 
     * @param response
     * @throws Exception
     */
    @GetMapping("/googlesignin")
    public void doGoogleSignIn(HttpServletResponse response) throws Exception {
        logger.debug("SSO Called...");
        response.sendRedirect(authorizationService.authenticateUserViaGoogle());
    }

    /**
     * Applications Callback URI for redirection from Google auth server after user approval/consent
     * 
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/oauth/callback") // registered on application.properties
                                   // google.oauth.callback.uri=http://localhost:8080/oauth/callback
    public String saveAuthorizationCode(HttpServletRequest request) throws Exception {
        logger.debug("SSO Callback invoked...");
        String code = request.getParameter("code");
        logger.debug("SSO Callback Code Value: " + code);

        if (code != null) {
            Credential c = authorizationService.exchangeCodeForTokens(code);
            request.getSession()
                    .setAttribute("at", c.getAccessToken());
            request.getSession()
                    .setAttribute("rt", c.getRefreshToken());
        }
        return "redirect:/";
    }

    /**
     * Handles logout
     * 
     * @return
     * @throws Exception
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws Exception {
        logger.debug("Logout invoked...");
        authorizationService.removeUserSession(request);
        return "redirect:/login";
    }

}