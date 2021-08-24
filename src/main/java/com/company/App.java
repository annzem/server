package com.company;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class App {

    private static boolean updateCache = false;

    public static void main(String[] args) {
        Server server = new Server();
        server.getHandlers().put("homepage", new ServerRequestHandler() {
            @Override
            public void processRequest(Request request, Response response, String[] urlSegments) {
                response.getBody().append("<html>homepage</html>");
            }
        });

        final Guestbook guestbook = new Guestbook();
//        guestbook.initStartComments();

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
                        response.getBody().append(Utils.readFile("/home/anna/IdeaProjects/server_connection/src/main/resources/guestbook.html", updateCache));
                    } catch (IOException e) {
                        throw new PageNotFoundException(request.getUrl());
                    }
                    guestbook.drawComments(response, guestbook.parseKeyword(request));
                }
            }
        });

        server.getHandlers().put("guestbook2", new ServerRequestHandler() {
            @Override
            public void processRequest(Request request, Response response, String[] urlSegments) throws PageNotFoundException {
                if (urlSegments.length == 2) {
                    try {
                        response.getBody().append(Utils.readFile("/home/anna/IdeaProjects/server_connection/src/main/resources/guestbook2.html", updateCache));
                    } catch (IOException e) {
                        throw new PageNotFoundException(request.getUrl());
                    }

                } else if (urlSegments[2].equals("getComments")) {
                    response.getResponseHeaders().put("Content-Type", "application/json");
                    if (request.getParamsOfGet().containsKey("word")) {
//                            List<Comment> filtered = guestbook.getComments().stream().filter(s -> s.getText().contains(request.getParamsOfGet().get("word"))).collect(Collectors.toList());
                        List<Comment> filtered = guestbook.getCommentsFromDB().stream().filter(s -> s.getText().contains(request.getParamsOfGet().get("word"))).collect(Collectors.toList());
                        String json = Utils.getGson().toJson(filtered);
                        response.getBody().append(json);
                    } else {
//                            String json = new ObjectMapper().writeValueAsString(guestbook.getComments());
                        String json = Utils.getGson().toJson(guestbook.getCommentsFromDB());
                        response.getBody().append(json);
                    }

                } else if (urlSegments[2].equals("postComment")) {
                    if (request.getMethod().equals("POST")) {
                        guestbook.parseNewComment(request).ifPresent((comment) -> {
                            Map<String, String> result = new HashMap<>();
                            if (comment.getText().trim().isEmpty()) {
                                result.put("status", "failed");
                                result.put("errormsg", "empty comment!");
                            } else {
//                                guestbook.addNewComment(comment);
                                guestbook.addNewCommentToDB(comment);

                                result.put("status", "success");
                            }

                            response.getResponseHeaders().put("Content-Type", "application/json");
                            String json = Utils.getGson().toJson(result);
                            response.getBody().append(json);

                        });
                    }

                }
            }
        });

        server.start();
    }
}

