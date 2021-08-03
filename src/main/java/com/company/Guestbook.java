package com.company;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Guestbook {
    private List<String> comments = new ArrayList<>();

    public List<String> getComments() {
        return comments;
    }

    public List<String> addStartComments() {
        getComments().add("The worst experience in my life! The color they gave me nothing to do with what I wanted. </br>Bad customer service, I had to go back so they could try to fix what they had done and the owner didn't even deign to ask me what had happened. </br>Marco R.<hr/>");
        getComments().add("Excellent work and attention, as always. Thank you <3</br> Janet Lisovsky<hr/>");
        return comments;
    }

    public String parseKeyword(Request request) {
        if (request.getParamsOfGet().containsKey("word")) {
            return request.getParamsOfGet().get("word");
        } else return null;
    }

    public String parseNewComment(Request request) {
        if (request.getParamsOfPost().containsKey("comment")) {
            String newCommentEnc = request.getParamsOfPost().get("comment");
            try {
                String newComment = URLDecoder.decode(newCommentEnc, StandardCharsets.UTF_8.toString());
                return newComment;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        } else return null;
    }

    public Response drawComments(Response response, String keyWord) {
        StringBuffer commentsStr = new StringBuffer();
        for (String comment : comments) {
            if (keyWord == null) {
                commentsStr.append("<p>");
                commentsStr.append(comment);
                commentsStr.append("</p>");
            } else {
                if (comment.contains(keyWord)) {
                    commentsStr.append("<p>");
                    commentsStr.append(comment);
                    commentsStr.append("</p>");
                }
            }
        }
        StringBuffer newBody = new StringBuffer();
        response.setBody(newBody.append(response.getBody().toString().replace("<comments/>", commentsStr)));
        return response;
    }

    public boolean checkComments (Request request) {
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).equals(parseNewComment(request) + "<hr/>")) {
                return false;
            }
        }
        return true;
    }

    public List<String> addNewComment(String newComment) {
        comments.add(newComment + "<hr/>");
        return comments;
    }
}

