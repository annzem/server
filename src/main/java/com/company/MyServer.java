package com.company;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyServer {
    private static final int PORT = 8080;
    private static ServerSocket serverSocket;

    static String xmlPath = "/home/anna/IdeaProjects/server_connection/src/main/resources/xmlRequest.html";
    static String jQueryPath = "/home/anna/IdeaProjects/server_connection/src/main/resources/jQueryRequest.html";
    static String guestBookPath = "/home/anna/IdeaProjects/server_connection/src/main/resources/guestBook.html";
    private static List<String> comments = new ArrayList<>();

    public static void main(String[] args) {
        comments.add("The worst experience in my life! The color they gave me nothing to do with what I wanted. </br>Bad customer service, I had to go back so they could try to fix what they had done and the owner didn't even deign to ask me what had happened. </br>Marco R.<hr/>");
        comments.add("Excellent work and attention, as always. Thank you <3</br> Janet Lisovsky<hr/>");
        MyServer myServer = new MyServer();
        myServer.connectToServer();
    }

    public static String readFile(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            return IOUtils.toString(fis, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void connectToServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();

                OutputStream output = clientSocket.getOutputStream();
                InputStream input = clientSocket.getInputStream();


                StringBuffer headers = new StringBuffer();
                while (!headers.toString().endsWith("\r\n\r\n")) {
                    headers.append((char) input.read());
                }
                String[] startingLineSegments = headers.toString().split("\n")[0].split(" ");
                String method = startingLineSegments[0];

                String body = null;

                if (method.equals("POST")) {
                    StringBuffer bodyBuffer = new StringBuffer();

                    String bodyLength = headers.toString().split("\r\n")[3].split(" ")[1];

                    int bodyLengthInt = Integer.parseInt(bodyLength);
                    byte[] bodyArray = new byte[bodyLengthInt];
                    bodyBuffer.append((char) input.read(bodyArray, 0, bodyLengthInt));
                    body = new String(bodyArray);
                }
                String path = startingLineSegments[1];

                StringBuffer response;
                try {
                    response = processResponse(method, headers.toString(), startingLineSegments, body, path);
                } catch (Exception e) {
                    e.printStackTrace();
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    response = createResponse("<html><h1>internal server error</h1><p>"
                                    + errors.toString() + "</p></html>", 500,
                            "INTERNAL SERVER ERROR");
                }

                output.write(response.toString().getBytes());
                output.flush();
                input.close();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuffer processResponse(String method, String headers, String startingLineSegments[], String body, String path) throws UnsupportedEncodingException, InterruptedException {
        if (method.equals("GET")) {
            if (path.equals("/time")) {
                return createResponse(readFile(xmlPath));
            } else if (path.equals("/time1")) {
                Date date = Calendar.getInstance().getTime();
                return createResponse(date.toString());
            } else if (path.equals("/guestbook")) {
                String keyWord = null;
                if (headers.contains("word=")) {
                    try {
                        keyWord = startingLineSegments[1].split("word=")[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int n = startingLineSegments[1].indexOf("?");
                    path = startingLineSegments[1].substring(0, n);
                }
                return createResponse(drawComments(keyWord));
            }
        } else if (method.equals("POST")) {
            if (body.contains("comment=")) {
                String newCommentEnc = body.split("comment=")[1];
                String newComment = URLDecoder.decode(newCommentEnc, StandardCharsets.UTF_8.toString());
                comments.add(newComment + "<hr/>");
                return createResponseRedirect("http", "localhost:" + PORT, "/guestbook");
            }
        }
        return createResponse("<html><h1>404</h1></html>");
    }

    private StringBuffer createResponse(String body) {
        return createResponse(body, 200, "OK");
    }

    public String drawComments(String keyWord) {
        StringBuffer responseBody = new StringBuffer();
        String html = readFile(guestBookPath);
        StringBuffer commentsStr = new StringBuffer();
        for (String comment : comments) {
            if (keyWord == null) {
                commentsStr.append("<p>");
                commentsStr.append(comment);
                commentsStr.append("</p>");
            } else {
                if (comment.contains(keyWord)) {
                    commentsStr.append("<p>");
                    commentsStr.append(comment);
                    commentsStr.append("</p>");
                }
            }
        }
        html = html.replace("<comments/>", commentsStr);
        responseBody.append(html);
        return responseBody.toString();
    }

    private StringBuffer createResponse(String body, int code, String codeName) {
        StringBuffer response = new StringBuffer();
        response.append("HTTP/1.1 " + code + " " + codeName + "\n" +
                "Date: Mon, 23 May 2005 22:38:34 GMT\n" +
                "Content-Type: text/html; charset=UTF-8\n" +
                "Content-Length: " + body.length() + "\n" +
                "Accept-Ranges: bytes\n" +
                "Connection: close\n" +
                "\n");
        response.append(body.toString());
        return response;
    }

    private StringBuffer createResponseRedirect(String protocol, String hostname, String url) {
        StringBuffer result = new StringBuffer();
        result.append("HTTP/1.1 303 See Other\n");
        result.append("Location:");
        result.append(protocol + "://" + hostname + url);;
        result.append("\n");
        return result;
    }

}
