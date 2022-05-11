import webapi.HttpTaskServer;
import webapi.kvserver.KVServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        HttpTaskServer server = new HttpTaskServer();
    }
}
