import webapi.HttpTaskServer;
import webapi.kv.KVServer;
import webapi.kv.KVTaskClient;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();
        KVTaskClient client = new KVTaskClient("http://localhost:8078");
        client.put("str", "str");
        System.out.println(client.load("str"));
        client.put("str", "another str");
        System.out.println(client.load("str"));
    }

}
