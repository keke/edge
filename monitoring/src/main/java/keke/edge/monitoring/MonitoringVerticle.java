package keke.edge.monitoring;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import keke.edge.monitoring.util.WalkDir;
import keke.edge.store.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author keke
 */
public class MonitoringVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringVerticle.class);
    private JsonArray configData;


    @Override
    public void start(Future<Void> startFuture) throws Exception {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Starting Monitoring Verticle");
        }

        super.start(startFuture);
        getVertx().eventBus().consumer("vertx.system", h -> {
            if (h.body().equals("started")) {
                loadStoreConfig();
            }
        });

    }

    private void loadStoreConfig() {
        getVertx().eventBus().send("store.config.load", null, r -> {
            if (r.succeeded()) {
                Config config = (Config) r.result().body();
                if (LOG.isInfoEnabled()) {
                    LOG.info("Monitoring configuration loaded {}", config.getJsonObject());
                }
                doFirstScan(config);
            }
        });
    }

    private void doFirstScan(Config config) {
        getVertx().executeBlocking(f -> {
            config.getFolders().forEach(e -> {
                String path = e.getPath();
                try {
                    walkDir(path);
                } catch (IOException e1) {
                    LOG.error("Unable to work dir {}", path);
                }
            });
            f.complete();
        }, rh -> {
            if (rh.succeeded()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Finished initial scan");
                }
            } else {
                LOG.warn("Find errors when doing initial scan");
            }
        });
    }

    private void walkDir(String path) throws IOException {
        WalkDir walkDir = new WalkDir(getVertx().eventBus());
        Files.walkFileTree(Paths.get(path), walkDir);
    }
}
