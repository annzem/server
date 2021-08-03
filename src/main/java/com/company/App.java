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
            public void processRequest(Request request, Response response) throws PageNotFoundException{
                if (request.getUrl().equals("/guestbook")) {
                    if (guestbook.parseNewComment(request) == null && guestbook.parseKeyword(request) == null) {
                        if (guestbook.getComments().size() == 0) {
                            guestbook.addStartComments();
                        }
                    }
                    if (guestbook.parseNewComment(request) != null && guestbook.checkComments(request) == true) {
                        guestbook.addNewComment(guestbook.parseNewComment(request));
                    }
                    try {
                        server.addContentFromFile(response, "/home/anna/IdeaProjects/server_connection/src/main/resources/guestbook.html");
                    } catch (IOException e) {
                        throw new PageNotFoundException(request.getUrl());
                    }
                    guestbook.drawComments(response, guestbook.parseKeyword(request));

                }


            }
        });

        server.start();
    }
}

