package keke.edge.util;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class JsonDataTest {
    @Test
    void getJsonObject() {
        JsonObject obj = new TestData().getJsonObject();
        assertNotNull(obj);
    }

    @Test
    void setJsonObject() {
        TestData data = new TestData();
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            data.setJsonObject(null);
        });
        assertNotNull(e);
    }

    public static class TestData extends JsonData {

    }
}