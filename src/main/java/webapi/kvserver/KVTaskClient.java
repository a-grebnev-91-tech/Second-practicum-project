package webapi.kvserver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private  URI uri;
    private String API_TOKEN;

    public KVTaskClient(String url) {
        client = HttpClient.newHttpClient();
        try {
            uri = URI.create(url);
            URI registrationUri = uri.resolve("/register");
            HttpRequest registerRequest = HttpRequest.newBuilder().GET().uri(uri).build();
            HttpResponse<String> response = client.send(registerRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Unexpected status code has been received");
                return;
            }
            API_TOKEN = response.body();
        } catch (IOException | InterruptedException ex) {
            System.out.println(" An error occurred while executing the request. Please check the URL and try again.");
        } catch (IllegalArgumentException ex) {
            System.out.println("The address you entered does not match the URL format. Please try again.");
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        URI loadUri = uri.resolve("/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest loadRequest = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .GET()
                .uri(loadUri)
                .build();
        HttpResponse<String> response = client.send(loadRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            return null;
        }
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI saveUri = uri.resolve("/save/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest putRequest = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(saveUri)
                .build();
        client.send(putRequest, HttpResponse.BodyHandlers.ofString());
    }
}
