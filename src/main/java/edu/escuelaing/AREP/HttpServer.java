package edu.escuelaing.AREP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.TreeMap;
import org.json.*;

import com.fasterxml.jackson.databind.ObjectMapper;


public class HttpServer {

    private static TreeMap<String, Integer> grades = new TreeMap<>();

    public static void main(String[] args) throws IOException {
        while (true) {
            ServerSocket serverSocket = new ServerSocket(35000);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String[] dataPage = readInput(in).split(" ");
            String types = dataPage[0];
            String name = dataPage[1];
            String request = dataPage[2];
            String outputLine = createOutput(types, name, clientSocket.getOutputStream(), request);
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
        }
    }

    private static String createOutput(String dataPage, String name, OutputStream output, String request) throws IOException {  
        String outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + dataPage + "\r\n"
                + "\r\n";
        File file = new File("target/classes/edu/escuelaing/AREP/files" + name);
        if (dataPage.contains("image")) {
            sendImage(outputLine, output, file);
            return null;
        }
        else if(dataPage.contains("json")){
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(grades);
            if(request.contains("POST")){
                return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n"
                + "{\"status\": \"success\", \"message\": \"Grade received successfully\"}";

            }
            return json;
        }
        else {
            outputLine += readHtmlFile("target/classes/edu/escuelaing/AREP/files" + name);
            return outputLine;
        }
    }

    private static void sendImage(String outputLine, OutputStream output, File file) throws IOException{
        output.write(outputLine.getBytes());
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
    }

    private static String readInput(BufferedReader in) throws IOException {
        String inputLine;
        String type = null;
        String name = null;
        int contentLength = 0;
        StringBuilder body = new StringBuilder();
        boolean isFirstLine = true;
        String request = "POST";
        while ((inputLine = in.readLine()) != null) {
            if (isFirstLine) {
                String[] data = inputLine.split(" ");
                name = data[1];
                request = data[0];
                if (name.split("\\.").length > 1) {
                    type = getType(name.split("\\.")[1]);
                }
                isFirstLine = false;
            }
            if (inputLine.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(inputLine.split(":")[1].trim());
            }
            if (inputLine.isEmpty()) {
                break;
            }
        }
        if (type == null) {
            name = "/index.html";
            type = "html";
        }
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            body.append(buffer); 
        }        
        if (body.length() > 0) {
            try {
                JSONObject jsonData = new JSONObject(body.toString());
                String key = jsonData.getString("nombre"); 
                int value = jsonData.getInt("nota");  
                putData(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            body.append(buffer);
        }
        return type + " " + name + " " + request;
    }

    private static String getType(String type) {
        HashMap<String, String> mimeTypes = new HashMap<>();
        mimeTypes.put("png", "image/png");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("app", "application/json");
        return mimeTypes.get(type);
    }

    private static String readHtmlFile(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }

    private static void putData(String name, Integer grade) {
        grades.put(name, grade);
    }

}
