package com.company;

import java.time.OffsetDateTime;


public class Comment {

    private String text;

    private final OffsetDateTime date;

    private String name;

    public Comment(String text, String name) {
        this.text = text;
        this.date = OffsetDateTime.now();
        this.name = name;
    }

    Comment(String text, OffsetDateTime date, String name) {
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

    public OffsetDateTime getDate() {
        return date;
    }
}
