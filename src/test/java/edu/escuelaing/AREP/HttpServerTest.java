package edu.escuelaing.AREP;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.net.Socket;

public class HttpServerTest extends TestCase {

    public HttpServerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(HttpServerTest.class);
    }

    public void testCreateOutputForHtml() throws IOException {
        String output = HttpServer.createOutput("text/html", "/index.html", new ByteArrayOutputStream(), "GET");
        assertNotNull(output);
    }

    public void testCreateOutputForJson() throws IOException {
        String output = HttpServer.createOutput("application/json", "/grades.json", new ByteArrayOutputStream(), "GET");
        assertNotNull(output);
        assertTrue(output.contains("{"));
    }

    public void testCreateOutputForPostJson() throws IOException {
        String output = HttpServer.createOutput("application/json", "/grades.json", new ByteArrayOutputStream(), "POST");
        assertNotNull(output);
        assertTrue(output.contains("Grade received successfully"));
    }

    public void testReadInputWithGetRequest() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader("GET /index.html HTTP/1.1"));
        String input = HttpServer.readInput(reader);
        assertNotNull(input);
        assertTrue(input.contains("index.html"));
    }

    public void testReadInputWithPostRequest() throws IOException {
        String jsonBody = "{\"nombre\": \"Carlos\", \"nota\": 75}";
        BufferedReader reader = new BufferedReader(new StringReader("POST /grades.json HTTP/1.1\nContent-Length: 29\n\n" + jsonBody));
        String input = HttpServer.readInput(reader);
        assertNotNull(input);
        assertTrue(input.contains("POST"));
    }

    public void testPutData() {
        HttpServer.putData("Carlos", 90);
        assertTrue(HttpServer.grades.containsKey("Carlos"));
        assertEquals((Integer) 90, HttpServer.grades.get("Carlos"));
    }

    public void testSocketInteraction() throws IOException {
        Socket socketMock = new Socket();
        OutputStream outputStreamMock = new ByteArrayOutputStream();
        BufferedReader readerMock = new BufferedReader(new StringReader("GET /index.html HTTP/1.1"));

        PrintWriter writer = new PrintWriter(outputStreamMock, true);
        BufferedReader in = new BufferedReader(new StringReader("GET /index.html HTTP/1.1"));

        String input = HttpServer.readInput(in);
        assertNotNull(input);
        assertTrue(input.contains("/index.html"));

        String response = HttpServer.createOutput("text/html", "/index.html", new ByteArrayOutputStream(), "GET");
        writer.println(response);
        assertNotNull(response);
    }
}
