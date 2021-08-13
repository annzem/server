package com.company;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    private String text;
    private String date;
    private String name;

    public Comment(String text, String name) {
        this.text = text;
        this.date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }
}
