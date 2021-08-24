package com.company;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Guestbook {

 //   private List<Comment> comments = new ArrayList<>();

//    public List<Comment> getComments() {return comments;}

    public List<Comment> getCommentsFromDB() {
        return CommentsDB.getInstance().getCommentsFromDB();
    }

//    public void initStartComments() {
//        try {
//            comments.add(new Comment("The worst experience in my life! The color they gave me nothing to do with what I wanted. </br>Bad customer service, I had to go back so they could try to fix what they had done and the owner didn't even deign to ask me what had happened.",
//                    new SimpleDateFormat("dd.MM.yyyy").parse("16.04.2019"),
//                    "Marco R."));
//            comments.add(new Comment("Excellent work and attention, as always. Thank you <3",
//                    new SimpleDateFormat("dd.MM.yyyy").parse("02.11.2020"),
//                    "Janet Lisovsky"));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public Optional<String> parseKeyword(Request request) {

        if (request.getParamsOfGet().containsKey("word")) {
            return Optional.of(request.getParamsOfGet().get("word"));
        } else return Optional.empty();

    }

    public Optional<Comment> parseNewComment(Request request) {
        if (request.getParamsOfPost().containsKey("text")) {
            Comment comment = new Comment(request.getParamsOfPost().get("text"), request.getParamsOfPost().get("name"));
            return Optional.of(comment);
        } else return Optional.empty();
    }

    public StringBuffer renderComment(StringBuffer data, Comment comment) {
        data.append("<p>");
        data.append(new SimpleDateFormat("dd.MM.yyyy").format(comment.getDate()));
        data.append(comment.getText());
        data.append(comment.getName());
        data.append("<hr/>");
        data.append("</p>");
        return data;
    }

    public Response drawComments(Response response, Optional<String> keyWord) {
        StringBuffer commentsStr = new StringBuffer();
        for (int i = 0; i < CommentsDB.getInstance().getCommentsFromDB().size(); i++) {
            if (!keyWord.isPresent()) {
                renderComment(commentsStr, CommentsDB.getInstance().getCommentsFromDB().get(i));
            } else {
                if (CommentsDB.getInstance().getCommentsFromDB().get(i).getText().contains(keyWord.get())) {
                    renderComment(commentsStr, CommentsDB.getInstance().getCommentsFromDB().get(i));
                }
            }
        }
        StringBuffer newBody = new StringBuffer();
        response.setBody(newBody.append(response.getBody().toString().replace("<comments/>", commentsStr)));
        return response;
    }

    public List<Comment> addNewComment(Comment newComment) {
        CommentsDB.getInstance().getCommentsFromDB().add(newComment);
        return CommentsDB.getInstance().getCommentsFromDB();
    }

    public void addNewCommentToDB(Comment comment) {
        CommentsDB.getInstance().putCommentToDB(comment);
    }
}

