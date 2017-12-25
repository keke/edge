package keke.edge.store;

public class InMemoryStore implements Store {
    private Config config;

    @Override
    public void saveConfig(Config config) {
        this.config = config;
    }

    @Override
    public Config loadConfig() {
        return config;
    }
}
