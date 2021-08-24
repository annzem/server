package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {

    public Request(String rawReqLine,
                   String rawHeaders,
                   String method,
                   String url,
                   String version) {
        this.rawReqLine = rawReqLine;
        this.rawHeaders = rawHeaders;
        this.method = method;
        this.url = url;
        this.version = version;
    }

    private final String rawReqLine;
    private final String rawHeaders;
    private final String method;
    private final String url;
    private final String version;

    public String getRawBody() {
        return rawBody;
    }

    private String rawBody;
    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> paramsOfGet = new HashMap<String, String>();
    private Map<String, String> paramsOfPost = new HashMap<String, String>();

    public Map<String, String> getParamsOfPost() {
        return paramsOfPost;
    }

    public Map<String, String> getParamsOfGet() {
        return paramsOfGet;
    }

    public String getRawReqLine() {
        return rawReqLine;
    }

    public String getRawHeaders() {
        return rawHeaders;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public void readBody(InputStream inputStream) throws IOException {
        String bodyLength = headers.get("Content-Length");
        int bodyLengthInt = Integer.parseInt(bodyLength);
        byte[] bodyArray = new byte[bodyLengthInt];
        inputStream.read(bodyArray);
        rawBody = URLDecoder.decode(new String(bodyArray), StandardCharsets.UTF_8.toString());
    }

    public void parseHeaders() {
        String[] headersArr = rawHeaders.split("\r\n");
        for (int i = 0; i < headersArr.length; i++) {
            headers.put(headersArr[i].split(": ")[0], headersArr[i].split(": ")[1]);
        }
    }

    public void parseParamsOfGet() {
        if (getRawReqLine().contains("?")) {
            String paramsOfGetS = getRawReqLine().split(" ")[1].split("\\?")[1];
            String[] split = paramsOfGetS.split("&");
            putToMap(split, paramsOfGet);
        }
    }

    public void parseParamsOfPost() {
        String[] split = getRawBody().split("&");
        putToMap(split, paramsOfPost);
    }

    private void putToMap(String[] split, Map<String, String> params) {
        for (int i = 0; i < split.length; i++) {
            String[] splittedKV = split[i].split("=");
            if (splittedKV.length == 1) {
                params.put(splittedKV[0], "");
            } else {
                params.put(splittedKV[0], splittedKV[1]);
            }
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "rawReqLine='" + rawReqLine + '\'' +
                ", rawHeaders='" + rawHeaders + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", rawBody='" + rawBody + '\'' +
                ", headers=" + headers +
                ", paramsOfGet=" + paramsOfGet +
                ", paramsOfPost=" + paramsOfPost +
                '}';
    }
}
