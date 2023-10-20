import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.util.Base64;

public class ClientePut {
    
    private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10)).build();
    
    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File(args[2]);
        byte[] requestPayload = Files.readAllBytes(file.toPath());
                
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + args[0] + ":" + args[1] + "/put")).PUT(BodyPublishers.ofByteArray(requestPayload)).setHeader("fileName", args[2]).setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString("username:password".getBytes())).build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }
    
}
