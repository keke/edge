package keke.edge.extract;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import keke.edge.store.Doc;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Office;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExtractVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(ExtractVerticle.class);

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
        getVertx().eventBus().consumer("extract.all", h -> {
            Doc doc = (Doc) h.body();
            if (LOG.isTraceEnabled()) {
                LOG.trace("To extract all {}", doc.getPath());
            }
            try {
                extractAll(doc);
            } catch (IOException | TikaException | SAXException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Unable to process file {}", doc.getPath(), e);
                }
            }
        });
        super.start(startFuture);
    }

    private void extractAll(Doc doc) throws IOException, TikaException, SAXException {

        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        try (InputStream is = Files.newInputStream(Paths.get(doc.getPath()))) {
            parser.parse(is, handler, metadata);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Parse Metadata {}", metadata.toString());
                LOG.debug("Result {}", handler.toString());
            }

        }

    }
}
