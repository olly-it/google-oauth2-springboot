package it.olly.springboot.googleauth2.test;

import java.io.FileOutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.drive.model.File;

import it.olly.springboot.googleauth2.service.GDriveUtils;

public class GDriveListDownloadTest {
    private final static Logger logger = LoggerFactory.getLogger(GDriveListDownloadTest.class);

    public static void main(String[] args) {
        try {
            String accessToken = "ya29.a0AfH6SMBQGeXCWq0pfKVqhnxpcOWa27ZgmeeLp2mrdNGacGIaY_Zp8fPwRHuvm6ks8iyUeTjf--nMkj7tDyxa18BCB2HIOiY3afysP42PqOkrDvtNpFdDuCKLOWItrpA85_Wj-chLzT6vWJkS1aeIdOx1l3Js";
            GDriveUtils gdu = new GDriveUtils(accessToken);
            logger.debug("******************** LIST FILES ********************");
            // list gdrive files
            List<File> files = gdu.listGDrive(10);
            for (File f : files) {
                logger.debug("> " + f);
            }

            logger.debug("******************** GET gdoc FILE ********************");
            String gdocFileId = "1VRxOEUGXME5tqqBeLS1yRlU9WYk2dGxKC4FiTZvJGqo"; // gdoc

            File f = gdu.getFile(gdocFileId);
            logger.debug("FILE> " + f);

            logger.debug("******************** EXPORT to pdf ********************");
            try {
                String pathTo = "/tmp/gd/gdoc_to_pdf.pdf";
                byte[] content = gdu.export(gdocFileId, "application/pdf");
                FileOutputStream fout = new FileOutputStream(pathTo);
                fout.write(content);
                fout.close();
                logger.debug("file written to " + pathTo);
            } catch (Exception e) {
                logger.warn("problems on donwloading/converting pdf");
            }

            logger.debug("******************** EXPORT to txt ********************");
            try {
                byte[] content = gdu.export(gdocFileId, "text/plain");
                logger.debug("TXT CONTENT:");
                logger.debug(new String(content));
            } catch (Exception e) {
                logger.warn("problems on donwloading/converting pdf");
            }

            logger.debug("******************** GET docx FILE ********************");
            String docxFileId = "1erraN9FMvKyl4D8ATXeEgu5mp6jXro66"; // docx

            File fx = gdu.getFile(docxFileId);
            logger.debug("FILE> " + fx);

            logger.debug("******************** DOWNLOAD ********************");
            try {
                String pathTo = "/tmp/gd/docx_file.docx";
                /* direct conversion from docx to pdf generates exception:
                com.google.api.client.http.HttpResponseException: 403 Forbidden
                GET https://www.googleapis.com/drive/v3/files/1erraN9FMvKyl4D8ATXeEgu5mp6jXro66/export?mimeType=application/pdf&alt=media
                {"error": {"errors": [{"domain": "global","reason": "fileNotExportable",
                "message": "Export only supports Docs Editors files."
                }],"code": 403,"message": "Export only supports Docs Editors files."}}
                */

                byte[] content = gdu.download(docxFileId);
                FileOutputStream fout = new FileOutputStream(pathTo);
                fout.write(content);
                fout.close();
                logger.debug("file written to " + pathTo);
            } catch (Exception e) {
                logger.warn("problems on donwloading/converting pdf");
            }

        } catch (SecurityException se) {
            logger.error("cannot execute request for bad credentials", se);
        } catch (InternalError ie) {
            logger.error("cannot execute request for internal error", ie);
        }
    }

}
