package keke.edge.store;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class StoreVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(StoreVerticle.class);
    private Store store;

    @Override
    public void init(Vertx vertx, Context context) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("To initiate Store Verticle with config: {}", context.config());
        }
        vertx.eventBus().registerDefaultCodec(Config.class, new ConfigMessageCodec());
        super.init(vertx, context);

    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        String storeClass = context.config().getString("store");
        try {
            store = (Store) Class.forName(storeClass).getConstructor(null).newInstance();
            prepareEvents();
            super.start(startFuture);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            startFuture.fail(e);
        }
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
