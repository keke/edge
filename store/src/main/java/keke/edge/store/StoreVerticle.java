package keke.edge.store;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(StoreVerticle.class);
    private Store store;

    @Override
    public void init(Vertx vertx, Context context) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("To initiate Store Verticle with config: {}", context.config());
        }
        vertx.eventBus().registerDefaultCodec(Config.class, new ConfigMessageCodec()).registerDefaultCodec(Doc.class, new DocMessageCodec());
        super.init(vertx, context);

    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        if(LOG.isDebugEnabled()){
            LOG.debug("To start Store verticle");
        }
        String storeType = context.config().getString("store");
        if ("InMem".equals(storeType)) {
            store = new InMemoryStore(context.config().getJsonObject("data"));
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Unknown store type: [{}]. Using InMemory store", storeType);
                store = new InMemoryStore(new JsonObject());
            }
        }
        prepareEvents();
        super.start(startFuture);
    }

    private void prepareEvents() {
        getVertx().eventBus().consumer("store.config.load", this::loadConfig);
        getVertx().eventBus().consumer("store.config.save", this::saveConfig);
    }

    private void loadConfig(Message message) {
        message.reply(store.loadConfig());
    }

    private void saveConfig(Message<Config> message) {
        store.saveConfig(message.body());
    }

}
