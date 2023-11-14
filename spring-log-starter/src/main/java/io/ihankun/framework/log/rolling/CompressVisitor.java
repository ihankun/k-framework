package io.ihankun.framework.log.rolling;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.action.PathCondition;
import org.apache.logging.log4j.status.StatusLogger;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

/**
 * @author hankun
 */
public class CompressVisitor extends SimpleFileVisitor<Path> {

    private static final Logger LOGGER = StatusLogger.getLogger();

    private static final int BUF_SIZE = 8192;

    private final Path basePath;
    private final List<? extends PathCondition> pathConditions;

    public CompressVisitor(final Path basePath, final List<? extends PathCondition> pathConditions) {
        this.basePath = Objects.requireNonNull(basePath, "basePath");
        this.pathConditions = Objects.requireNonNull(pathConditions, "pathConditions");
        for (final PathCondition condition : pathConditions) {
            condition.beforeFileTreeWalk();
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        for (final PathCondition pathFilter : pathConditions) {
            final Path relative = basePath.relativize(file);
            if (!pathFilter.accept(basePath, relative, attrs)) {
                LOGGER.trace("Not deleting base={}, relative={}", basePath, relative);
                return FileVisitResult.CONTINUE;
            }
        }
        compressFile(file);
        return FileVisitResult.CONTINUE;
    }

    protected void compressFile(final Path file) throws IOException {
        final String tmpCompressedName = file.toString() + ".gz";

        final File source = file.toFile();
        final File destination = target(tmpCompressedName);
        LOGGER.trace("Compressing {}", file);

        if (source.exists()) {
            try (final FileInputStream fis = new FileInputStream(source);
                 final OutputStream fos = new FileOutputStream(destination);
                 final OutputStream gzipOut = new ConfigurableLevelGzipOutputStream(
                         fos, BUF_SIZE, Deflater.DEFAULT_COMPRESSION);
                 // Reduce native invocations by buffering data into GZIPOutputStream
                 final OutputStream os = new BufferedOutputStream(gzipOut, BUF_SIZE)) {
                final byte[] inbuf = new byte[BUF_SIZE];
                int n;

                while ((n = fis.read(inbuf)) != -1) {
                    os.write(inbuf, 0, n);
                }
            }

            if (!source.delete()) {
                LOGGER.warn("Unable to delete {}.", source);
            }
        }
    }

    private static final class ConfigurableLevelGzipOutputStream extends GZIPOutputStream {

        ConfigurableLevelGzipOutputStream(OutputStream out, int bufSize, int level) throws IOException {
            super(out, bufSize);
            def.setLevel(level);
        }
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException ioException) throws IOException {
        if (ioException instanceof NoSuchFileException) {
            LOGGER.info("File {} could not be accessed, it has likely already been deleted", file, ioException);
            return FileVisitResult.CONTINUE;
        } else {
            return super.visitFileFailed(file, ioException);
        }
    }

    File target(final String fileName) {
        return new File(fileName);
    }
}
