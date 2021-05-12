package it.olly.springboot.googleauth2.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GDriveUtils {
    private final static Logger logger = LoggerFactory.getLogger(GDriveUtils.class);
    private Drive driveService = null;

    public GDriveUtils(String accessToken) {
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setAccessToken(accessToken);

        driveService = new Drive.Builder(ApplicationConfig.HTTP_TRANSPORT, ApplicationConfig.JSON_FACTORY, credential)
                .setApplicationName(ApplicationConfig.APPLICATION_NAME)
                .build();

    }

    private void manageError(Exception e) throws SecurityException, InternalError {
        if (e instanceof GoogleJsonResponseException) {
            if (((GoogleJsonResponseException) e).getStatusCode() == 401)
                throw new SecurityException(e);
        } else throw new InternalError(e);
    }

    public List<File> listGDrive(int totResults) throws SecurityException, InternalError {
        logger.debug("listGDrive()");
        List<File> ret = new ArrayList<File>();

        String pageToken = null;
        int cnt = 0;
        try {
            do {
                FileList result = driveService.files()
                        .list()
                        // .setFields("nextPageToken, files(id, name)") // retrieve only specific fields
                        // .setQ("<query>") // add a query to select files
                        .setPageToken(pageToken)
                        .execute();
                for (File f : result.getFiles()) {
                    ret.add(f);
                    cnt++;
                    if (cnt >= totResults)
                        break;
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null && cnt < totResults);
        } catch (Exception e) {
            logger.warn("cannot listGDrive()", e);
            manageError(e);
        }
        return ret;
    }

    public byte[] download(String fileId) throws SecurityException, InternalError {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            driveService.files()
                    .get(fileId)
                    .executeMediaAndDownloadTo(outputStream);
            outputStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.warn("cannot download()", e);
            manageError(e);
        }
        return null;

    }

    public byte[] export(String fileId, String mimeType) throws SecurityException, InternalError {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            driveService.files()
                    .export(fileId, mimeType)
                    .executeMediaAndDownloadTo(outputStream);
            outputStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.warn("cannot download()", e);
            manageError(e);
        }
        return null;

    }

    public File getFile(String fileId) throws SecurityException, InternalError {
        try {
            logger.debug("getFile()");

            return driveService.files()
                    .get(fileId)
                    .execute();
        } catch (Exception e) {
            logger.warn("cannot listGDrive()", e);
            manageError(e);
        }
        return null;
    }

}
