package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Guestbook {
    private List<String> comments = new ArrayList<>();

    public List<String> getComments() {
        return comments;
    }

    public List<String> addStartComments() {
        getComments().add("The worst experience in my life! The color they gave me nothing to do with what I wanted. </br>Bad customer service, I had to go back so they could try to fix what they had done and the owner didn't even deign to ask me what had happened. </br>Marco R.");
        getComments().add("Excellent work and attention, as always. Thank you <3</br> Janet Lisovsky");
        return comments;
    }

    public Optional <String> parseKeyword(Request request) {
        if (request.getParamsOfGet().containsKey("word")) {
            return Optional.of(request.getParamsOfGet().get("word"));
        } else return Optional.empty();
    }

    public Optional<String> parseNewComment(Request request) {
        if (request.getParamsOfPost().containsKey("comment")) {
                return Optional.of(request.getParamsOfPost().get("comment"));
        } else return Optional.empty();
    }

    public StringBuffer renderComment (StringBuffer data, String comment) {

        data.append("<p>");
        data.append(comment);
        data.append("<hr/>");
        data.append("</p>");
        return data;
    }

    public Response drawComments(Response response, Optional<String> keyWord) {
        StringBuffer commentsStr = new StringBuffer();
        for (String comment : comments) {
            if (!keyWord.isPresent()) {
                renderComment(commentsStr, comment);
            } else {
                if (comment.contains(keyWord.get())) {
                    renderComment(commentsStr, comment);
                }
            }
        }
        StringBuffer newBody = new StringBuffer();
        response.setBody(newBody.append(response.getBody().toString().replace("<comments/>", commentsStr)));
        return response;
    }

    public List<String> addNewComment(String newComment) {
        comments.add(newComment);
        return comments;
    }
}

