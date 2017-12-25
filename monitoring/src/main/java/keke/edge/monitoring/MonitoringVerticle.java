package keke.edge.monitoring;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import keke.edge.monitoring.util.DirWatcher;
import keke.edge.monitoring.util.WalkDir;
import keke.edge.store.Config;
import keke.edge.store.Doc;
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
    private DirWatcher watcher;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Starting Monitoring Verticle");
        }
        watcher = new DirWatcher();
        super.start(startFuture);
        getVertx().eventBus().consumer("vertx.system", h -> {
            if (h.body().equals("started")) {
                loadStoreConfig();
            }
        });

    }

    private void loadStoreConfig() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("to load store config");
        }
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("To do first round scanning...");
        }
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
                LOG.warn("Find errors when doing initial scan", rh.cause());
            }
            //start watching
            config.getFolders().forEach(e -> {
                try {
                    watcher.addPath(e.getPath());
                } catch (IOException e1) {
                    LOG.warn("Unable to watch {}", e.getPath(), e1);
                }
            });
            getVertx().setPeriodic(5000, this::checkWatcher);
        });
    }

    private void checkWatcher(Long id) {
        watcher.check();
    }

    private void walkDir(String path) throws IOException {
        WalkDir walkDir = new WalkDir() {
            @Override
            public void findDoc(Doc doc) {
                MonitoringVerticle.this.findDoc(doc);
            }
        };
        Files.walkFileTree(Paths.get(path), walkDir);
    }

    private void findDoc(Doc doc) {
    }

    @Override
    public void stop() throws Exception {
        watcher.close();
        super.stop();
    }
}
