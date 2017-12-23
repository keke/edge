package keke.edge.monitoring.util;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author keke
 */
public class VertxMain {
    private static final Logger LOG = LoggerFactory.getLogger(VertxMain.class);
    private Vertx vertx;
    private VertxOptions options;

    public VertxMain(String configFile) throws IOException {
        options = new VertxOptions();
        if (configFile != null) {
            String configStr = getConfig(configFile);
            if (configFile != null) {
                options = new VertxOptions(new JsonObject(configStr));
            }
        } else {
            LOG.debug("Using default configuration");
        }
    }

    public static void main(String... args) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Starting VertxMain...");
        }
        String configFile = args.length > 0 ? args[0] : null;
        try {
            new VertxMain(configFile).start();
        } catch (IOException e) {
            LOG.error("Unable to start VertxMain", e);
        }
    }

    private void start() {
        vertx = Vertx.vertx();
//        vertx.
    }

    private String getConfig(String configPath) throws IOException {
        //try file first
        File file = new File(configPath);
        if (file.exists() && file.isFile()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Load config from file {}", file);
            }
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        }
        //try stream
        return IOUtils.toString(VertxMain.class.getResourceAsStream(configPath), StandardCharsets.UTF_8);
    }
}
