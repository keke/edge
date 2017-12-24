package keke.edge.monitoring.util;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WalkDir extends SimpleFileVisitor<Path> {
    private static final Logger LOG = LoggerFactory.getLogger(WalkDir.class);
    private static final Set<String> EXT_SET = new HashSet<>(Arrays.asList("pptx", "pdf", "ppt", "doc", "docx"));

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (Files.isHidden(dir)) {
            return FileVisitResult.SKIP_SUBTREE;
        } else {
            return FileVisitResult.CONTINUE;
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!Files.isHidden(file) && isAcceptable(file.toString())) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Visiting file {}", file);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Skip file {}", file);
            }
        }
        return super.visitFile(file, attrs);
    }

    private boolean isAcceptable(String name) {
        return EXT_SET.contains(FilenameUtils.getExtension(name).toLowerCase());
    }
}
