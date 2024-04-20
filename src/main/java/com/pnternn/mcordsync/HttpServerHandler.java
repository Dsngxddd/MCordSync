package com.pnternn.mcordsync;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class HttpServerHandler implements HttpHandler{

    private String response = "oh no";
    private long responseLength;
    private String[] headers;
    private HttpExchange exchange;
    private OutputStream outputStream;
    private Scanner scanner;
    private InputStream inputStream;
    private String id;

    public HttpServerHandler(String id) {
        super();
        this.id = id;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.responseLength = 0;
        this.response = "404";
        this.exchange = exchange;
        this.outputStream = exchange.getResponseBody();
        this.inputStream = exchange.getRequestBody();
        this.scanner = new Scanner(this.inputStream);

        URI dataUrl = this.exchange.getRequestURI();
        Headers dataHeaders = this.exchange.getResponseHeaders();
        String dataMethod = this.exchange.getRequestMethod();
        String dataId = this.id;
        String dataBody = null;
        String body = null;
        while (this.scanner.hasNextLine()) {
            body = this.scanner.nextLine();
        }
        if(dataMethod.equals("POST")) {
            dataBody = body;
        }else if(dataMethod.equals("GET")) {
        }
    }
}
