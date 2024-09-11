package com.amitcul.index;

import com.amitcul.analyze.Analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Index {
    private static final Logger LOG = Logger.getLogger(Index.class.getName());

    private final Analyzer analyzer;
    private final ExecutorService executor;
    private final Map<String, Set<Path>> invertedIndex = new ConcurrentHashMap<>();

    public Index(Analyzer analyzer) {
        this.executor = Executors.newFixedThreadPool(4);
        this.analyzer = analyzer;
    }

    public void buildIndex(List<Path> paths) throws ExecutionException, InterruptedException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Path path : paths) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> indexFile(path), executor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
    }

    public void indexFile(Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                List<String> analyzedTokens = analyzer.analyze(line);
                for (String token : analyzedTokens) {
                    invertedIndex
                            .computeIfAbsent(token.toLowerCase(), k -> ConcurrentHashMap.newKeySet())
                            .add(path);
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while indexing file: {0}", path);
        }
    }

    public void removeFileFromIndex(Path path) {
        invertedIndex.forEach((token, files) -> files.remove(path));
        invertedIndex.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public void updateFileInIndex(Path path) {
        removeFileFromIndex(path);
        indexFile(path);
    }

    public void close() {
        executor.shutdown();
    }

    public Set<Path> searchForToken(String token) {
        return invertedIndex.getOrDefault(token.toLowerCase(), Set.of());
    }
}
