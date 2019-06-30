package com.revolut.hm.task;

import spark.utils.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.fail;


public class ApiTestUtils {

    public static TestResponse request(String method, String path, String requestBody) {
        try {
            URL url = new URL("http://localhost:8080" + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            if (requestBody != null) {
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(requestBody);
                osw.flush();
                osw.close();
                os.close();
            }
            connection.connect();
            String body = "";
            try {
                body = IOUtils.toString(connection.getInputStream());
            } catch (Exception e) {
            }
            return new TestResponse(connection.getResponseCode(), body);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
        }
    }

    public static class TestResponse {

        public final String body;
        public final int status;

        public TestResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }
    }
}
