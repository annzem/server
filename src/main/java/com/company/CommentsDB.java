package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentsDB {
    public static CommentsDB commentsDB;
    private final String url = "jdbc:postgresql://localhost:5432/comments";
    private final String user = "postgres";
    private final String password = "password";

    private CommentsDB() {
    }

    public static CommentsDB getInstance() {
        if (commentsDB == null) {
            commentsDB = new CommentsDB();
        }
        return commentsDB;
    }

    public void putCommentToDB(Comment comment) {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO comments(date, text, name) VALUES(?, ?, ?)");

            statement.setObject(1,  comment.getDate());
            statement.setString(2, comment.getText());
            statement.setString(3, comment.getName());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Comment> getCommentsFromDB() {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM comments");
            List<Comment> comments = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                OffsetDateTime date = resultSet.getObject(2, OffsetDateTime.class);
                String text = resultSet.getString(3);
                String name = resultSet.getString(4);
                comments.add(new Comment(text, date, name));
            }
            return comments;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
