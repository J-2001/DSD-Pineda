import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Servidor {
    
    private static final String GET_ENDPOINT = "/get";
    private static final String PUT_ENDPOINT = "/put";
    private static final String STATUS_ENDPOINT = "/status";
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
        
        try {
            Headers headers = exchange.getRequestHeaders();
            if (headers.containsKey("fileName")) {
                String fileName = headers.get("fileName").get(0);
                System.out.println("Archivo solicitado: " + fileName);
                String response = "OK!";
                sendResponse(response.getBytes(), exchange);
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
        
        try {
            Headers headers = exchange.getRequestHeaders();
            if (headers.containsKey("fileName")) {
                String fileName = headers.get("fileName").get(0);
                int fileSize = Integer.valueOf(headers.get("Content-length").get(0));
                System.out.println("Archivo enviado: " + fileName + " - " + fileSize + " bytes");
                // byte[] requestBytes = exchange.getRequestBody().readAllBytes();
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
