package keke.edge.extract;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TikaVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(TikaVerticle.class);

    private AutoDetectParser parser;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        parser = new AutoDetectParser();

    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("To start Tika Verticle");
        }
        getVertx().eventBus().consumer("process.file", h -> {
            String path = (String) h.body();
            if (LOG.isTraceEnabled()) {
                LOG.trace("To process {}", path);
            }
            try {
                processFile(path);
            } catch (IOException | TikaException | SAXException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Unable to process file {}", path, e);
                }
            }
        });
        super.start(startFuture);
    }

    private void processFile(String path) throws IOException, TikaException, SAXException {

        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        try (InputStream is = Files.newInputStream(Paths.get(path))) {
            parser.parse(is, handler, metadata);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Parse Metadata {}", metadata.toString());
                LOG.debug("Result {}", handler.toString());
            }
        }

    }
}
