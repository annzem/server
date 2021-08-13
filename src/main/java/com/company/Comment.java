package com.company;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Comment {
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private final Date date;
    private String name;

    public Comment(String text, String name) {
        this.text = text;
        this.date = new Date();
        this.name = name;
    }

    Comment(String text, Date date, String name) {
        this.text = text;
        this.date = date;
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }
}
