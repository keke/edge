package keke.edge.store;

import io.vertx.core.json.JsonObject;
import keke.edge.util.JsonData;

public class Doc extends JsonData {
    public Doc(JsonObject jsonObject) {
        super(jsonObject);
    }

    public Doc(String path) {
        super(new JsonObject());
        getJsonObject().put("path", path);
    }

    public String getPath() {
        return getJsonObject().getString("path");
    }
}
