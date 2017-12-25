package keke.edge.util;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class VertxMainTests {
    @Test
    void testGetConfigByClassPath() throws IOException {
        String str = VertxMain.getConfig("/vertx-test.json");
        assertNotNull(str);
    }

    @Test
    void testGetConfigByPath() throws IOException {
        System.out.println(Paths.get(".").toAbsolutePath());
        String str = VertxMain.getConfig("./src/test/resources/vertx-test.json");
        assertNotNull(str);
    }

    @Test
    void testConstructorDefault() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new VertxMain();
        });
        assertNotNull(e);
    }

    @Test
    void testConstructorBlankParameter() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new VertxMain("");
        });
        assertNotNull(e);
    }

    @Test
    void testConstructorNullParameter() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new VertxMain(null);
        });
        assertNotNull(e);
    }

    @Test
    void testConstructureWithParameter() throws IOException {
        new VertxMain("./src/test/resources/vertx-test.json");
    }

    @Test
    void testStartSuccess() throws IOException {
        VertxMain main = new VertxMain("./src/test/resources/vertx-test.json");
        Vertx mockVertx = mock(Vertx.class);
        EventBus mockEventBus = mock(EventBus.class);
        when(mockVertx.eventBus()).thenReturn(mockEventBus);
        doAnswer(invocation -> {
            Handler<AsyncResult<String>> handler = invocation.getArgument(2);
            handler.handle(Future.succeededFuture());
            return null;
        }).when(mockVertx).deployVerticle(anyString(), any(DeploymentOptions.class), any(Handler.class));
        main.setVertx(mockVertx);
        main.start();
        verify(mockVertx, never()).close();
        verify(mockEventBus, times(1)).publish(eq(VertxMain.ADDR_VERTX_SYSTEM), eq(VertxMain.EVENT_STARTED));
    }

    @Test
    void testStartFailed() throws IOException {
        VertxMain main = new VertxMain("./src/test/resources/vertx-test.json");
        Vertx mockVertx = mock(Vertx.class);
        EventBus mockEventBus = mock(EventBus.class);
        when(mockVertx.eventBus()).thenReturn(mockEventBus);
        doAnswer(invocation -> {
            Handler<AsyncResult<String>> handler = invocation.getArgument(2);
            handler.handle(Future.failedFuture("error"));
            return null;
        }).when(mockVertx).deployVerticle(anyString(), any(DeploymentOptions.class), any(Handler.class));
        main.setVertx(mockVertx);
        main.start();
        verify(mockVertx, times(1)).close();
        verify(mockEventBus, never()).publish(eq(VertxMain.ADDR_VERTX_SYSTEM), eq(VertxMain.EVENT_STARTED));
    }
}
