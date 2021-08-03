package com.company;

public class PageNotFoundException extends Exception {
    public PageNotFoundException(String message) {
        super(message);
    }
}