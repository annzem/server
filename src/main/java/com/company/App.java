package com.company;

import java.io.IOException;

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
                    try {
                        response.getBody().append(Utils.readFile("/home/anna/IdeaProjects/server_connection/src/main/resources/getComments.html"));
                    } catch (IOException e) {
                        throw new PageNotFoundException(request.getUrl());
                    }
                } else if (urlSegments[2].equals("getComments")) {

                    response.getBody().append("[");
                    for (int i = 0; i < guestbook.getComments().size(); i++) {
                        response.getBody().append("\"");
                        response.getBody().append(guestbook.getComments().get(i));
                        response.getBody().append("\"");
                        if (i != guestbook.getComments().size() - 1) {
                            response.getBody().append(",");
                        }
                    }
                    response.getBody().append("]");
                }
            }
        });

        server.start();
    }
}

