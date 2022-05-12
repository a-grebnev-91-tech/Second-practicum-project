package manager;

import util.web.json.HttpTaskManagerConverter;
import webapi.kv.KVTaskClient;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTaskManager {

    private final String key = this.getClass().getName();
    private final KVTaskClient client;
    private final HttpTaskManagerConverter converter;

    public HttpTaskManager(String url) {
        super(null);
        client = new KVTaskClient(url);
        this.converter = new HttpTaskManagerConverter(this);
    }

    @Override
    public void save() {
        String jsonState = converter.convertStateToJson();
        try {
            client.put(key, jsonState);
        } catch (IOException | InterruptedException e) {
            throw new SaveException("Cannot save state on server"); //TODO change exception
        }
    }

    public void load() {
        String jsonState = null;
        try {
            jsonState = client.load(key);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        converter.updateManager(jsonState);
    }

    private static class SaveException extends RuntimeException {
        public SaveException(String message) {
            super(message);
        }
    }
}
