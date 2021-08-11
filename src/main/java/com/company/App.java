package com.company;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) {
        Server server = new Server();

        server.getHandlers().put("homepage", new ServerRequestHandler() {
            @Override
            public void processRequest(Request request, Response response, String[] urlSegments) {
                response.getBody().append("<html>homepage</html>");
            }
        });

        final Guestbook guestbook = new Guestbook();
        guestbook.addStartComments();

        server.getHandlers().put("guestbook", new ServerRequestHandler() {
            @Override
            public void processRequest(Request request, Response response, String[] urlSegments) throws PageNotFoundException {
                if (request.getUrl().equals("/guestbook")) {

                    if (request.getMethod().equals("POST")) {
                        guestbook.parseNewComment(request).ifPresent((comment) -> {
                            guestbook.addNewComment(comment);
                        });

                        response.setStatusCode(301);
                        response.setStatusText("Moved permanently");
                        response.getResponseHeaders().put("Location", "/guestbook");
                    }

                    try {
                        response.getBody().append(Utils.readFile("/home/anna/IdeaProjects/server_connection/src/main/resources/guestbook.html"));
                    } catch (IOException e) {
                        throw new PageNotFoundException(request.getUrl());
                    }
                    guestbook.drawComments(response, guestbook.parseKeyword(request));

                } else if (request.getUrl().equals("/guest")) {
                    //TODO
                }
            }
        });

        server.getHandlers().put("guestbook2", new ServerRequestHandler() {
            @Override
            public void processRequest(Request request, Response response, String[] urlSegments) throws PageNotFoundException {
                if (urlSegments.length == 2) {
                    if (request.getMethod().equals("POST")) {
                        guestbook.parseNewComment(request).ifPresent((comment) -> {
                            guestbook.addNewComment(comment);
                        });

                        response.setStatusCode(301);
                        response.setStatusText("Moved permanently");
                        response.getResponseHeaders().put("Location", "/guestbook2");
                    }

                    try {
                        response.getBody().append(Utils.readFile("/home/anna/IdeaProjects/server_connection/src/main/resources/guestbook2.html"));
                    } catch (IOException e) {
                        throw new PageNotFoundException(request.getUrl());
                    }

                } else if (urlSegments[2].equals("getComments")) {
                    response.getResponseHeaders().put("Content-Type", "application/json");
                    try {
                        if (request.getParamsOfGet().containsKey("word")) {
                            List<String> filtered = guestbook.getComments().stream().filter(s -> s.contains(request.getParamsOfGet().get("word"))).collect(Collectors.toList());
                            String json = new ObjectMapper().writeValueAsString(filtered);
                            response.getBody().append(json);
                        } else {
                            String json = new ObjectMapper().writeValueAsString(guestbook.getComments());
                            response.getBody().append(json);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        server.start();
    }
}

