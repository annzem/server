package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final int PORT = 8080;
    private static final String HTTP_VERSION = "HTTP/1.1";
    private Map<String, ServerRequestHandler> handlers = new HashMap<>();
    private static boolean updateCache = false;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public Map<String, ServerRequestHandler> getHandlers() {
        return handlers;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {

                Socket clientSocket = serverSocket.accept();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processRequest(clientSocket);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(Socket socket) {
        InputStream input = null;
        OutputStream output = null;
        Request request = null;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
            request = readAndParseRequest(input);

            Response response;
            try {
                response = route(request);
                sendResponse(response.getStatusCode(), response.getStatusText(), response, output);
            } catch (PageNotFoundException e) {
                response = createErrorResponse("page not found error: " + e.getMessage());
                sendResponse(404, "Page not found", response, output);
            } catch (Exception e) {
                e.printStackTrace();
                response = createErrorResponse("Internal server error happened");
                sendResponse(500, "Internal server error", response, output);
            }
        } catch (Exception e) {
            if (output != null) {
                try {
                    sendResponse(400, "Wrong request", new Response(), output);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
            if (request != null) {
                System.err.println(request);
                e.printStackTrace();
            }
        } finally {
            close(input);
            close(output);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Response createErrorResponse(String errorText) {
        Response response = new Response();
        response.getBody().append("<html><h1>");
        response.getBody().append(errorText);
        response.getBody().append("</h1></html>");
        return response;
    }

    private void sendResponse(int statusCode, String statusText, Response response, OutputStream output) throws IOException {
        StringBuffer firstLine = new StringBuffer();
        firstLine.append(HTTP_VERSION);
        firstLine.append(" ");
        firstLine.append(statusCode);
        firstLine.append(" ");
        firstLine.append(statusText);
        firstLine.append("\n");
        output.write(firstLine.toString().getBytes());
        output.write(response.getData().toString().getBytes());
        output.flush();
    }

    private Request readAndParseRequest(InputStream inputStream) throws IOException {
        StringBuffer rawHeadersAndReqLine = new StringBuffer();

        while (!rawHeadersAndReqLine.toString().endsWith("\r\n\r\n") && (rawHeadersAndReqLine.toString().getBytes().length < 1500)) {
            rawHeadersAndReqLine.append((char) inputStream.read());
        }
        String reqLine = rawHeadersAndReqLine.toString().split("\n")[0];
        String method = reqLine.split(" ")[0];
        String url = reqLine.split(" ")[1].split("\\?")[0];
        String version = reqLine.split(" ")[2];
        String headers = rawHeadersAndReqLine.toString().split(version + "\n")[1];

        Request request = new Request(reqLine, headers, method, url, version);
        request.parseHeaders();
        if (method.equals("POST")) {
            request.readBody(inputStream);
            request.parseParamsOfPost();
        }
        if (method.equals("GET")) {
            request.parseParamsOfGet();
        }
        return request;
    }


    private Response route(Request request) throws PageNotFoundException {
        Response response = new Response();
        try {
            String[] urlSegments = request.getUrl().split("/");
            if (urlSegments.length > 1) {
                String firstUrlSegment = urlSegments[1];

                ServerRequestHandler handler = handlers.get(firstUrlSegment);
                if (handler != null) {
                    handler.processRequest(request, response, urlSegments);
                } else {
                    response.getBody().append(Utils.readFile("/home/anna/IdeaProjects/server_connection/src/main/resources" + request.getUrl() + ".html", updateCache));
                }
            } else {
                response.getBody().append("empty");
            }
        } catch (IOException e) {
            throw new PageNotFoundException(request.getUrl());
        }
        return response;
    }
}
