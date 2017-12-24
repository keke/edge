package keke.edge.monitoring.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VertxMainTests {
    @Test
    void testGetConfigByClassPath() throws IOException {
        String str = VertxMain.getConfig("/vertx.json");
        assertNotNull(str);
    }

    @Test
    void testGetConfigByPath() throws IOException {
        System.out.println(Paths.get(".").toAbsolutePath());
        String str = VertxMain.getConfig("./src/test/resources/vertx.json");
        assertNotNull(str);
    }
}
