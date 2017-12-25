package keke.edge.util;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JsonDataMessageCodecTest {
    @Test
    void encodeToWire() {
        Buffer mockBuffer = mock(Buffer.class);
        new TestCodec().encodeToWire(mockBuffer, new TestData());
        verify(mockBuffer, times(1)).appendInt(anyInt());
        verify(mockBuffer, times(1)).appendBuffer(any(Buffer.class));
    }

    @Test
    void name() {
        assertEquals(TestCodec.class.getName(), new TestCodec().name());
    }

    @Test
    void systemCodecID() {
        assertEquals(-1, new TestCodec().systemCodecID());
    }

    public static class TestData extends JsonData {
        public TestData() {
            super();
        }
    }

    public static class TestCodec extends JsonDataMessageCodec<TestData> {
        @Override
        protected TestData newInstance(JsonObject jsonObject) {
            return new TestData();
        }
    }
}