package com.company;

import org.apache.commons.io.IOUtils;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

public class MyServer {
    private static ServerSocket serverSocket;
    private static InputStream input;
    private static OutputStream output;

    public static void main(String[] args) {
        connectToServer();
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

                StringBuffer stringBuffer = new StringBuffer();
                while (!stringBuffer.toString().endsWith("\r\n\r\n")) {
                    stringBuffer.append((char) input.read());
                }

                String path = stringBuffer.toString().split("\n")[0].split(" ")[1];

//                FileInputStream fis = new FileInputStream("/home/anna/IdeaProjects/server_connection/src/main/resources/xmlRequest.html");
                FileInputStream fis = new FileInputStream("/home/anna/IdeaProjects/server_connection/src/main/resources/jQueryRequest.html");
                String html = IOUtils.toString(fis, "UTF-8");

                String response = "HTTP/1.1 200 OK\n" +
                        "Date: Mon, 23 May 2005 22:38:34 GMT\n" +
                        "Content-Type: text/html; charset=UTF-8\n" +
                        "Content-Length: " + html.length() + "\n" +
                        "Accept-Ranges: bytes\n" +
                        "Connection: close\n" +
                        "\n" + html;

                String response1 = "HTTP/1.1 200 OK\n" +
                        "Content-Type: text/html; charset=UTF-8\n" +
                        "Content-Length: " + lengthDate + "\n" +
                        "Accept-Ranges: bytes\n" +
                        "Connection: close\n" +
                        "\n" +
                        date;

                if (path.equals("/")) {
                    output.write(response.getBytes());
                }
                if (path.equals("/first")) {
                    output.write(response1.getBytes());
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
