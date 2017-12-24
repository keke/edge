package keke.edge.monitoring.util;

import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author keke
 */
public class VertxMain {
    private static final Logger LOG = LoggerFactory.getLogger(VertxMain.class);
    private Vertx vertx;
    private VertxOptions options;
    private JsonObject vertxConfig = new JsonObject();

    public VertxMain(String configFile) throws IOException {
        options = new VertxOptions();
        if (configFile != null) {
            String configStr = getConfig(configFile);
            if (configStr != null) {
                vertxConfig = new JsonObject(configStr);
                options = new VertxOptions(vertxConfig.getJsonObject("options", new JsonObject()));
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
        vertx = Vertx.vertx(options);
        List<Future> futureList = new ArrayList<>();
        vertxConfig.getJsonArray("verticles", new JsonArray()).forEach(e -> {
            JsonObject obj = (JsonObject) e;

            String className = obj.getString("class");
            if (LOG.isDebugEnabled()) {
                LOG.debug("To deploy-{}", className);
            }
            Future<String> future = Future.future();
            futureList.add(future);
            vertx.deployVerticle(className, new DeploymentOptions(obj), future.completer());
        });
        CompositeFuture.all(futureList).setHandler(c -> {
            if (c.succeeded()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Vertx started successfully");
                }
                vertx.eventBus().publish("vertx.system", "started");
            } else {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to start vertx : {}", c.result());
                }
                vertx.close();
            }
        });

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
