package keke.edge.monitoring;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoringStoreVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringStoreVerticle.class);
    private JsonArray store;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("To start MonitoringStore Verticle");
        }
        store = config().getJsonArray("store", new JsonArray());
        getVertx().eventBus().consumer("store.monitoring", h -> {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Receiving a message {} - reply address:{}", h.body(), h.replyAddress());
            }
            if ("load".equals(h.body())) {
                h.reply(store);
            }
        });
        super.start(startFuture);
    }
}
