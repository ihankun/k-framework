package io.ihankun.framework.log.rolling;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.appender.rolling.action.*;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.Deflater;

/**
 * @author hankun
 */
@Plugin(name = "GzCompressAction", category = Core.CATEGORY_NAME, printObject = true)
public class GzCompressAction extends AbstractPathAction {

    /**
     * If true, attempt to delete file on completion.
     */
    private final boolean deleteSource;

    /**
     * GZIP compression level to use.
     *
     * @see Deflater#setLevel(int)
     */
    private final int compressionLevel;

    private final PathSorter pathSorter;

    /**
     * Create new instance of GzCompressAction.
     *
     * @param deleteSource     if true, attempt to delete file on completion.  Failure to delete
     *                         does not cause an exception to be thrown or affect return value.
     * @param compressionLevel Gzip deflater compression level.
     */
    public GzCompressAction(
            final String basePath, final boolean followSymbolicLinks, final int maxDepth,
            final PathCondition[] pathConditions,
            final boolean deleteSource, final int compressionLevel, final StrSubstitutor subst, final PathSorter pathSorter) {

        super(basePath, followSymbolicLinks, maxDepth, pathConditions, subst);
        this.deleteSource = deleteSource;
        this.compressionLevel = compressionLevel;
        this.pathSorter = pathSorter;
    }

    /**
     * Compress.
     *
     * @return true if successfully compressed.
     * @throws IOException on IO exception.
     */
    @Override
    public boolean execute(final FileVisitor<Path> visitor) throws IOException {
        final List<PathWithAttributes> sortedPaths = getSortedPaths();

        for (final PathWithAttributes element : sortedPaths) {
            try {
                visitor.visitFile(element.getPath(), element.getAttributes());
            } catch (final IOException ioex) {
                LOGGER.error("Error in post-rollover Delete when visiting {}", element.getPath(), ioex);
                visitor.visitFileFailed(element.getPath(), ioex);
            }
        }
        // TODO return (visitor.success || ignoreProcessingFailure)
        return true;
    }

    List<PathWithAttributes> getSortedPaths() throws IOException {
        final SortingVisitor sort = new SortingVisitor(pathSorter);
        super.execute(sort);
        final List<PathWithAttributes> sortedPaths = sort.getSortedPaths();
        return sortedPaths;
    }

    @Override
    protected FileVisitor<Path> createFileVisitor(Path visitorBaseDir, List<PathCondition> conditions) {
        return new CompressVisitor(visitorBaseDir, conditions);
    }

    @PluginFactory
    public static GzCompressAction createMsunGzCompressAction(
            @PluginAttribute("basePath") final String basePath,
            @PluginAttribute(value = "followLinks") final boolean followLinks,
            @PluginAttribute(value = "maxDepth", defaultInt = 1) final int maxDepth,
            @PluginAttribute(value = "DeleteSource", defaultBoolean = true) final boolean deleteSource,
            @PluginElement("PathConditions") final PathCondition[] pathConditions,
            @PluginElement("PathSorter") final PathSorter sorterParameter,
            @PluginConfiguration final Configuration config

    ) {
        final PathSorter sorter = sorterParameter == null ? new PathSortByModificationTime(true) : sorterParameter;
        return new GzCompressAction(basePath, followLinks, maxDepth, pathConditions, deleteSource, Deflater.DEFAULT_COMPRESSION, config.getStrSubstitutor(), sorter);
    }
}
