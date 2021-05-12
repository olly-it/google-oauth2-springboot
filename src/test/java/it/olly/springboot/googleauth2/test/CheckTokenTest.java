package it.olly.springboot.googleauth2.test;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.IOUtils;

public class CheckTokenTest {
    private static final Logger logger = LoggerFactory.getLogger(CheckTokenTest.class);

    public static class TokenRet {
        public String issued_to;
        public String audience;
        public String scope;
        public Integer expires_in;
        public String access_type;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("TokenRet {issued_to=");
            builder.append(issued_to);
            builder.append(", audience=");
            builder.append(audience);
            builder.append(", scope=");
            builder.append(scope);
            builder.append(", expires_in=");
            builder.append(expires_in);
            builder.append(", access_type=");
            builder.append(access_type);
            builder.append("}");
            return builder.toString();
        }

    }

    public static void main(String[] args) throws Exception {
        // EXPIRED
        // String accessToken =
        // "ya29.a0AfH6SMBidDAdhQaVoyFix-q-stiaFr_XnfUOvJGEWZXI7-VlCCP8vqmtlAwVGjyVP-AeECESxuL7BUkcMfm5RA29zi8JA8cJfLxQwBUCxBA5hs-yOX5FTqcgdIp4qJbAoAfrv-oXTrmVXQIIgHDetnptXeC9";

        String accessToken = "ya29.a0AfH6SMAXtBxbe8pG0Pas1kVsbbCKHEYZbdWepVoJmq-1SF6GE4ECX6QWweqNVrDkLS1017ERSTj_sKrl5i7M08HdTaVQI13USjXSxPDpF-0BPTroNUK-mMj0ftcM99PZ7RTU73aTP1zVBwfh02KBTkvh3ejN";
        // check token info
        URL url = new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int resp = con.getResponseCode();
        if (resp == 200) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            IOUtils.copy(con.getInputStream(), bout);
            bout.close();

            // convert to object
            ObjectMapper om = new ObjectMapper();
            TokenRet readValue = om.readValue(bout.toByteArray(), TokenRet.class);

            logger.debug("> " + readValue);
        } else {
            logger.debug("EXPIRED!");
        }

    }

}
