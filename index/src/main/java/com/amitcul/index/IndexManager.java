package com.amitcul.index;

import com.amitcul.analyze.Analyzer;
import com.amitcul.files.Event;
import com.amitcul.files.FileManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class IndexManager {
    private final FileManager fileManager;
    private final Analyzer analyzer;
    private final BlockingQueue<Event> eventsQueue;
    private Index index;
    private IndexMonitor indexMonitor = null;

    public IndexManager(FileManager fileManager, Analyzer analyzer) throws IOException {
        this.fileManager = fileManager;
        this.analyzer = analyzer;

        eventsQueue = new LinkedBlockingQueue<>();
        fileManager.setUpWatcher(eventsQueue);
    }

    public void index() throws IndexCreationException {
        this.index = new Index(analyzer);
        try {
            index.buildIndex(fileManager.getFiles());
            indexMonitor = new IndexMonitor(index, eventsQueue);
            indexMonitor.startMonitoring();
        } catch (Exception e) {
            throw new IndexCreationException("Failed to create index", e);
        }
    }

    public Set<Path> query(String token) {
        return index.searchForToken(token);
    }

    public void close() {
        if (fileManager != null) fileManager.downFileWatcher();
        if (indexMonitor != null) indexMonitor.stopMonitoring();
        index.close();
    }

}