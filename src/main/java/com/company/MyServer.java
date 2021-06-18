package com.company;

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

                StringBuffer stringBuffer = new StringBuffer();
                while (!stringBuffer.toString().endsWith("\r\n\r\n")) {
                    stringBuffer.append((char) input.read());
                }

                String html = "<html>\n" +
                        "  <head>\n" +
                        "    <title>An Example Page </title>\n" +
                        "  </head>\n" +
                        "  <body>\n" +
                        "    <p>Hello World, this is a very simple HTML document.</p>\n" +
                        date + "\n" +
                        "  </body>\n" +
                        "</html>";

                String response = "HTTP/1.1 200 OK\n" +
                        "Date: Mon, 23 May 2005 22:38:34 GMT\n" +
                        "Content-Type: text/html; charset=UTF-8\n" +
                        "Content-Length: " + html.getBytes().length + "\n" +
                        "Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n" +
                        "Server: Apache/1.3.3.7 (Unix) (Red-Hat/Linux)\n" +
                        "ETag: \"3f80f-1b6-3e1cb03b\"\n" +
                        "Accept-Ranges: bytes\n" +
                        "Connection: close\n" +
                        "\n" + html;

                stringBuffer.toString();

                output.write(response.getBytes());

                output.flush();
                input.close();
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
