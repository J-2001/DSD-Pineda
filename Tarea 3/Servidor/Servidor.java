import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.concurrent.Executors;

public class Servidor {
    
    private static final String GET_ENDPOINT = "/get";
    private static final String PUT_ENDPOINT = "/put";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String CREDENTIALS = "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes());
    private final int port;
    private HttpServer server;
    
    public Servidor(int port) {
        this.port = port;
    }
    
    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch(Exception e) {
            System.out.println("Error al iniciar el servidor: " + e.toString());
            return;
        }
        
        HttpContext getContext = this.server.createContext(GET_ENDPOINT);
        HttpContext putContext = this.server.createContext(PUT_ENDPOINT);
        HttpContext statusContext = this.server.createContext(STATUS_ENDPOINT);
        
        getContext.setHandler(this::handleGetRequest);
        putContext.setHandler(this::handlePutRequest);
        statusContext.setHandler(this::handleStatusRequest);
        
        this.server.setExecutor(Executors.newFixedThreadPool(8));
        this.server.start();
    }
    
    private void handleGetRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        
        Headers headers = exchange.getRequestHeaders();
        
        if (headers.containsKey("Authorization")) {
            if ( !headers.get("Authorization").get(0).equals(this.CREDENTIALS) ) {
                sendUnauthorizedResponse(exchange);
                return;
            }
        } else {
            sendUnauthorizedResponse(exchange);
            return;
        }
        
        try {
            if (headers.containsKey("fileName")) {
                String fileName = headers.get("fileName").get(0);
                File file = new File(fileName);
                byte[] response = Files.readAllBytes(file.toPath());
                
                System.out.println("Se ha enviado el archivo " + fileName + "...");
                sendResponse(response, exchange);
                return;
            }
        } catch (Exception e) {
            System.out.println("Error al recibir la peticion GET: " + e.toString());
        }
        
        String response = "Error!";
        sendResponse(response.getBytes(), exchange);
    }
    
    private void handlePutRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("put")) {
            exchange.close();
            return;
        }
        
        Headers headers = exchange.getRequestHeaders();
        
        if (headers.containsKey("Authorization")) {
            if ( !headers.get("Authorization").get(0).equals(this.CREDENTIALS) ) {
                sendUnauthorizedResponse(exchange);
                return;
            }
        } else {
            sendUnauthorizedResponse(exchange);
            return;
        }
        
        try {
            if (headers.containsKey("fileName")) {
                String fileName = headers.get("fileName").get(0);
                int fileSize = Integer.valueOf(headers.get("Content-length").get(0));
                byte[] requestBytes = exchange.getRequestBody().readAllBytes();
                File file = new File(fileName);
                Files.write(file.toPath(), requestBytes);
                
                System.out.println("Se ha recibido el archivo " + fileName + " - " + fileSize + " bytes");
                String response = "OK!";
                sendResponse(response.getBytes(), exchange);
                return;
            }
        } catch (Exception e) {
            System.out.println("Error al recibir la peticion PUT: " + e.toString());
        }
        
        String response = "Error!";
        sendResponse(response.getBytes(), exchange);
    }
    
    private void handleStatusRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        
        String response = "El servidor esta vivo!\n";
        sendResponse(response.getBytes(), exchange);
    }
    
    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }
    
    private void sendUnauthorizedResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(401, -1);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.flush();
        exchange.close();
    }
    
    public int getPort() {
        return this.port;
    }
    
    public static void main(String[] args) {
        int puerto = 8080;
        if (args.length == 1) {
            puerto = Integer.valueOf(args[0]);
        }
        
        Servidor servidor = new Servidor(puerto);
        servidor.startServer();
        
        System.out.println("Servidor iniciado en el puerto " + servidor.getPort() + "...");
    }
    
}
