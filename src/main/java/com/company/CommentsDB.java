package com.company;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentsDB {
    public static CommentsDB commentsDB;
    private final String url = "jdbc:postgresql://localhost:5432/comments";
    private final String user = "postgres";
    private final String password = "password";
    private Connection connection;

    private CommentsDB() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static CommentsDB getInstance() {
        if (commentsDB == null) {
            commentsDB = new CommentsDB();
        }
        return commentsDB;
    }

    public void putCommentToDB(Comment comment) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO comments(date, text, name) VALUES(?, ?, ?)");

            statement.setObject(1, comment.getDate());
            statement.setString(2, comment.getText());
            statement.setString(3, comment.getName());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Comment> getCommentsFromDB(Optional<String> keyWord) {
        PreparedStatement statement;
        try {
            if (!keyWord.isPresent()) {
                statement = connection.prepareStatement("SELECT * FROM comments");
            } else {
                statement = connection.prepareStatement("SELECT * FROM comments WHERE text LIKE '%"+keyWord.get()+"%'");
            }
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
