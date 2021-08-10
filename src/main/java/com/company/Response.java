package com.company;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Response {

    private Map<String, String> responseHeaders = new HashMap<>();
    private StringBuffer body = new StringBuffer();

    private int statusCode = 200;
    private String statusText = "OK";

    public Response() {
        responseHeaders.put("Date", String.valueOf(getTime()));
        responseHeaders.put("Content-Type", "text/html; charset=UTF-8");
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setBody(StringBuffer body) {
        this.body = body;
    }

    public StringBuffer getBody() {
        return body;
    }

    public StringBuffer getData() {
        StringBuffer data = new StringBuffer();
        for (String headerName : responseHeaders.keySet()) {
            data.append(headerName);
            data.append(": ");
            data.append(responseHeaders.get(headerName));
            data.append("\n");
        }

        data.append("Content-Length: ");
        data.append(body.toString().getBytes().length);
        data.append("\n");
        data.append("\n");
        data.append(body);
        return data;
    }

    private DateTimeFormatter getTime() {
        return DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z",
                Locale.ENGLISH).withZone(ZoneId.of("GMT"));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}
