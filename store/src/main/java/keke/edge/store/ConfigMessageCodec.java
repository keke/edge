package keke.edge.store;

import io.vertx.core.json.JsonObject;
import keke.edge.util.JsonDataMessageCodec;

public class ConfigMessageCodec extends JsonDataMessageCodec<Config> {
    @Override
    protected Config newInstance(JsonObject jsonObject) {
        return new Config(jsonObject);
    }
}
