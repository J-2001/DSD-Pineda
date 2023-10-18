import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;

public class ClienteGet {
    
    private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10)).build();
    
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://" + args[0] + ":" + args[1] + "/get")).setHeader("fileName", args[2]).build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        HttpHeaders headers = response.headers();
        
        System.out.println("Longitud del archivo: " + headers.firstValue("content-length").get());
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
    }
    
}