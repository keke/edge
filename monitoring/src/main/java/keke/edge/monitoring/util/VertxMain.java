package keke.edge.monitoring.util;

import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author keke
 */
public class VertxMain {
    private static final Logger LOG = LoggerFactory.getLogger(VertxMain.class);
    private Vertx vertx;
    private VertxOptions options = new VertxOptions();
    private JsonObject vertxConfig = new JsonObject();

    /**
     * Build a default VertxMain
     *
     * @throws IOException
     */
    public VertxMain() throws IOException {
        this("/vertx.json");
    }

    /**
     * @param configFile
     * @throws IOException
     */
    public VertxMain(String configFile) throws IOException {
        if (StringUtils.isBlank(configFile)) {
            throw new IllegalArgumentException("ConfigFile should not be blank");
        }
        options = new VertxOptions();

        String configStr = getConfig(configFile);
        if (configStr != null) {
            vertxConfig = new JsonObject(configStr);
            options = new VertxOptions(vertxConfig.getJsonObject("options", new JsonObject()));
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

    static String getConfig(String configPath) throws IOException {
        Objects.requireNonNull(configPath);
        //try file first
        File file = new File(configPath);
        if (file.exists() && file.isFile()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Load config from file {}", file);
            }
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        }
        //try stream
        if(LOG.isDebugEnabled()){
            LOG.debug("URL {}", VertxMain.class.getResource(configPath));
        }
        return IOUtils.toString(VertxMain.class.getResourceAsStream(configPath), StandardCharsets.UTF_8);
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
}
