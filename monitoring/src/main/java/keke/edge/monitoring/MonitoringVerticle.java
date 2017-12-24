package keke.edge.monitoring;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoringVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Starting Monitoring Verticle");
        }
        super.start(startFuture);
    }
}
