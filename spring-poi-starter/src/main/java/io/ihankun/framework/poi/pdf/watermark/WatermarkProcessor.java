package io.ihankun.framework.poi.pdf.watermark;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;

import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WatermarkProcessor implements IWatermarkProcessor {

    public PDDocument document = null;

    private Set<String> watermarkWordSet = new HashSet<>();

    @Override
    public void init(PDDocument document) {
        this.document = document;

        // 扫描PDF文档，获取所有水印，如果超过3页，则启动多线程并行扫描
        int threadCount = getThreadCount();

        CompletableFuture<?>[] scancerTasks = new CompletableFuture<?>[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int pageStart = i * 3;
            scancerTasks[i] = CompletableFuture.runAsync(() -> {
                WatermarkScanner scanner = new WatermarkScanner(WatermarkProcessor.this, pageStart, 3);
                scanner.run();
            });
        }
        CompletableFuture.allOf(scancerTasks).join();
    }

    /**
     * 清除水印的实现 当超过3页时，本方法采用多线程执行，并行清除页面水印，以提高效率。
     */
    public void removeWatermark(PDDocument document) throws Exception {
        int threadCount = getThreadCount();
        CompletableFuture<?>[] removerTasks = new CompletableFuture<?>[threadCount];
        final Vector<RemoveResult> removeResults = new Vector<>();

        for (int i = 0; i < threadCount; i++) {
            final int pageStart = i * 3;
            removerTasks[i] = CompletableFuture.runAsync(() -> {
                WatermarkRemover remover = new WatermarkRemover(WatermarkProcessor.this, pageStart, 3, null);
                remover.removeWatermark();
                removeResults.addAll(remover.getPageTokens());
            });
        }
        CompletableFuture.allOf(removerTasks).join();

        // 对所有结果进行排序
        Collections.sort(removeResults, new Comparator<RemoveResult>() {
            @Override
            public int compare(RemoveResult o1, RemoveResult o2) {
                return o1.getPageNo() - o2.getPageNo();
            }
        });

        // 执行完毕后统一进行回写处理
        for (RemoveResult result : removeResults) {
            PDStream updatedStream = new PDStream(document);
            OutputStream out = updatedStream.createOutputStream(COSName.FLATE_DECODE);
            ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
            tokenWriter.writeTokens(result.getTokens());
            out.close();
            result.getPage().setContents(updatedStream);
        }
    }

    public void removeWatermark(List<String> watermarks) throws Exception {
        int threadCount = getThreadCount();
        CompletableFuture<?>[] removerTasks = new CompletableFuture<?>[threadCount];
        final Vector<RemoveResult> removeResults = new Vector<>();

        for (int i = 0; i < threadCount; i++) {
            final int pageStart = i * 3;
            removerTasks[i] = CompletableFuture.runAsync(() -> {
                WatermarkRemover remover = new WatermarkRemover(WatermarkProcessor.this, pageStart, 3, watermarks);
                remover.removeWatermark();
                removeResults.addAll(remover.getPageTokens());
            });
        }
        CompletableFuture.allOf(removerTasks).join();

        // 对所有结果进行排序
        Collections.sort(removeResults, new Comparator<RemoveResult>() {
            @Override
            public int compare(RemoveResult o1, RemoveResult o2) {
                return o1.getPageNo() - o2.getPageNo();
            }
        });

        // 执行完毕后统一进行回写处理
        for (RemoveResult result : removeResults) {
            PDStream updatedStream = new PDStream(document);
            OutputStream out = updatedStream.createOutputStream(COSName.FLATE_DECODE);
            ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
            tokenWriter.writeTokens(result.getTokens());
            out.close();
            result.getPage().setContents(updatedStream);
        }
    }

    @Override
    public PDDocument getDocument() {
        return document;
    }

    @Override
    public boolean isWatermarkWord(String str) {
        return watermarkWordSet.contains(str);
    }

    @Override
    public void addWatermarkWord(String str) {
        watermarkWordSet.add(str);
    }
}
