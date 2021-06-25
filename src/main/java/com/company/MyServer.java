package com.company;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyServer {
    private static ServerSocket serverSocket;
    private static InputStream input;
    private static OutputStream output;
    static String xmlPath = "/home/anna/IdeaProjects/server_connection/src/main/resources/xmlRequest.html";
    static String jQueryPath = "/home/anna/IdeaProjects/server_connection/src/main/resources/jQueryRequest.html";
    static String guestBookPath = "/home/anna/IdeaProjects/server_connection/src/main/resources/guestBook.html";
    private static List<String> comments = new ArrayList<>();

    public static void main(String[] args) {
        comments.add("The worst experience in my life! The color they gave me nothing to do with what I wanted. </br>Bad customer service, I had to go back so they could try to fix what they had done and the owner didn't even deign to ask me what had happened. </br>Marco R.<hr/>");
        comments.add("Excellent work and attention, as always. Thank you <3</br> Janet Lisovsky<hr/>");
        connectToServer();
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

    public static void connectToServer() {
        try {
            serverSocket = new ServerSocket(8080);
            while (true) {
                Socket clientSocket = serverSocket.accept();

                output = clientSocket.getOutputStream();
                input = clientSocket.getInputStream();
                Date date = Calendar.getInstance().getTime();
                int lengthDate = date.toString().length();

                StringBuffer headers = new StringBuffer();
                while (!headers.toString().endsWith("\r\n\r\n")) {
                    headers.append((char) input.read());
                }

                String method = headers.toString().split("\n")[0].split(" ")[0];
                String body;
                String newComment;

                if (method.equals("POST")) {
                    StringBuffer bodyBuffer = new StringBuffer();

                    String bodyLength = headers.toString().split("\r\n")[3].split(" ")[1];

                    int bodyLengthInt = Integer.parseInt(bodyLength);
                    byte[] bodyArray = new byte[bodyLengthInt];
                    bodyBuffer.append((char) input.read(bodyArray, 0, bodyLengthInt));
                    body = new String(bodyArray);
                    String newCommentEnc = body.split("comment=")[1];

                    newComment = URLDecoder.decode(newCommentEnc, StandardCharsets.UTF_8.toString());
                    comments.add(newComment+"<hr/>");
                }

                String path = headers.toString().split("\n")[0].split(" ")[1];
                if (path.equals("/")) {
                    String html = readFile(xmlPath);
                    String response = "HTTP/1.1 200 OK\n" +
                            "Date: Mon, 23 May 2005 22:38:34 GMT\n" +
                            "Content-Type: text/html; charset=UTF-8\n" +
                            "Content-Length: " + html.length() + "\n" +
                            "Accept-Ranges: bytes\n" +
                            "Connection: close\n" +
                            "\n" + html;

                    output.write(response.getBytes());
                }
                if (path.equals("/first")) {
                    String response1 = "HTTP/1.1 200 OK\n" +
                            "Content-Type: text/html; charset=UTF-8\n" +
                            "Content-Length: " + lengthDate + "\n" +
                            "Accept-Ranges: bytes\n" +
                            "Connection: close\n" +
                            "\n" +
                            date;

                    output.write(response1.getBytes());
                }
                if (path.equals("/guestbook")) {
                    String html = readFile(guestBookPath);

                    StringBuffer commentsStr = new StringBuffer();
                    for (String comment : comments) {
                        commentsStr.append("<p>");
                        commentsStr.append(comment);
                        commentsStr.append("</p>");
                    }
                    html = html.replace("<comments/>", commentsStr);

                    String responseGuestbook = "HTTP/1.1 200 OK\n" +
                            "Content-Type: text/html; charset=utf-8\n" +
                            "Content-Length: " + html.length() + "\n" +
                            "Accept-Ranges: bytes\n" +
                            "Connection: close\n" +
                            "\n" + html;

                    output.write(responseGuestbook.getBytes());
                }

                output.flush();
                input.close();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
