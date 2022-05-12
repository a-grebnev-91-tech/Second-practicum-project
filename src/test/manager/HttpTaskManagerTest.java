package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import util.Managers;
import webapi.kv.KVServer;

import java.io.IOException;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{
    KVServer server;
    @BeforeEach
    public void init() throws IOException {
        server = new KVServer();
        server.start();
        setManager((HttpTaskManager) Managers.getDefault());
    }

    @AfterEach
    public void stopKVServer() {
        server.stop();
    }
}