package com.company;

public interface ServerRequestHandler {
    void processRequest(Request request, Response response) throws PageNotFoundException;
}
