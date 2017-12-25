package keke.edge.store;

/**
 * @author keke
 */
public interface Store {
    void saveConfig(Config config);

    Config loadConfig();
}
