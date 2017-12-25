package keke.edge.util;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

public abstract class JsonData<T extends JsonData> {
    private JsonObject jsonObject;

    public JsonData() {
        this(new JsonObject());
    }

    public JsonData(JsonObject jsonObject) {
        Objects.requireNonNull(jsonObject);
        this.jsonObject = jsonObject;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

}
