package keke.edge.store;

import io.vertx.core.json.JsonObject;
import keke.edge.util.JsonDataMessageCodec;

public class DocMessageCodec extends JsonDataMessageCodec<Doc> {
    @Override
    protected Doc newInstance(JsonObject jsonObject) {
        return new Doc(jsonObject);
    }
}
