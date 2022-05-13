package webapi;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpClient client;
    private HttpTaskServer server;

    @BeforeEach
    public void startServerAndConfigClient() throws IOException {
        server = new HttpTaskServer();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void stopServer() {
        server.stop(0);
    }


}