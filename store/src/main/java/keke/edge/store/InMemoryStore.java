package keke.edge.store;

import io.vertx.core.json.JsonObject;

public class InMemoryStore implements Store {
    private Config config;

    public InMemoryStore(JsonObject data) {
        config = new Config(data.getJsonObject("config"));
    }

    @Override
    public void saveConfig(Config config) {
        this.config = config;
    }

    @Override
    public Config loadConfig() {
        return config;
    }
}
