package keke.edge.util;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

/**
 * @param <T>
 */
public abstract class JsonDataMessageCodec<T extends JsonData> implements MessageCodec<T, T> {
    private String codecName = getClass().getName();

    @Override
    public void encodeToWire(Buffer buffer, T t) {
        Buffer encoded = t.getJsonObject().toBuffer();
        buffer.appendInt(encoded.length());
        buffer.appendBuffer(encoded);
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        pos += 4;
        return newInstance(new JsonObject(buffer.slice(pos, pos + length)));
    }

    protected abstract T newInstance(JsonObject jsonObject);

    @Override
    public T transform(T t) {
        return newInstance(t.getJsonObject().copy());
    }

    @Override
    public String name() {
        return codecName;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
