package keke.edge.monitoring.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class DirWatcher {
    private static final Logger LOG = LoggerFactory.getLogger(DirWatcher.class);
    private Map<WatchKey, Path> watchKeys = new HashMap<>();
    private WatchService watcher;

    public DirWatcher() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
    }

    public void addPath(String path) throws IOException {
        Path p = Paths.get(path);

        WatchKey watchKey = p.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        watchKeys.put(watchKey, p);
        LOG.info("Watching path {}", p);
    }

    public void check() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Checking watchservice...");
        }
        WatchKey key = watcher.poll();
        if (key != null) {
            for (WatchEvent<?> e : key.pollEvents()) {
                WatchEvent.Kind kind = e.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }
                WatchEvent<Path> ev = (WatchEvent<Path>) e;
                Path filename = ev.context();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} was {}", filename, e.kind());
                }

            }
            boolean valid = key.reset();
            if (!valid) {
                LOG.warn("Watch key {} is invalid", key.watchable());
            }
            check();
        }
    }

    public void close() {
        watchKeys.keySet().forEach(k -> k.cancel());
    }
}
