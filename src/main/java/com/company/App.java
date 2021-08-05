package com.company;

import java.io.IOException;

public class App {

    public static void main(String[] args) {
        Server server = new Server();

        server.getHandlers().put("homepage", new ServerRequestHandler() {
            @Override
            public void processRequest(Request request, Response response) {
                response.getBody().append("<html>homepage</html>");
            }
        });

        final Guestbook guestbook = new Guestbook();

        server.getHandlers().put("guestbook", new ServerRequestHandler() {
            @Override
            public void processRequest(Request request, Response response) throws PageNotFoundException {
                if (request.getUrl().equals("/guestbook")) {
                    if (guestbook.getComments().size() == 0) {
                        guestbook.addStartComments();
                    }

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

                } else if (request.getUrl().equals("/guestbook/get_comments")) {

//                    response.getBody().append("[\"comment1\", \"comment2\", \"comment3\"]");
//                    response.getBody().append("[");
//                    for (String comment : guestbook.getComments()) {
//                        response.getBody().append("\"");
//                        response.getBody().append(comment);
//                        response.getBody().append("\",");
//                    }
//                    response.getBody().append("]");
                }
            }
        });

        server.start();
    }
}

