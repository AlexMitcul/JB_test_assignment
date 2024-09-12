package com.amitcul.index;

import com.amitcul.files.Event;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IndexMonitor implements Runnable {
    private static final Logger LOG = Logger.getLogger(IndexMonitor.class.getName());

    private final BlockingQueue<Event> eventQueue;
    private final Index index;
    private final ExecutorService executorService;
    private Future<?> monitorFuture;


    public IndexMonitor(Index index, BlockingQueue<Event> eventQueue) {
        this.index = index;
        this.eventQueue = eventQueue;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void startMonitoring() {
        monitorFuture = executorService.submit(this);
    }

    public void stopMonitoring() {
        if (monitorFuture != null) {
            monitorFuture.cancel(true);
        }
        executorService.shutdown();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (!eventQueue.isEmpty()) {
                    Event event = eventQueue.take();
                    handleEvent(event);
                }
            } catch (InterruptedException e) {
                LOG.log(Level.INFO, "IndexMonitor Stopped", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void handleEvent(Event eventInstance) {
        WatchEvent.Kind<?> kind = eventInstance.event().kind();
        WatchEvent<Path> ev = (WatchEvent<Path>) eventInstance.event();
        Path parentDir = eventInstance.path();
        Path fileName = ev.context().getFileName();
        Path filePath = parentDir.resolve(fileName);

        if (isTemproraryFile(fileName)) return;
        switch (kind.name()) {
            case "ENTRY_CREATE":
                LOG.log(Level.INFO, "File created: {0}", filePath);
                index.indexFile(filePath);
                break;
            case "ENTRY_DELETE":
                LOG.log(Level.INFO, "File deleted: {0}", filePath);
                index.removeFileFromIndex(filePath);
                break;
            case "ENTRY_MODIFY":
                LOG.log(Level.INFO, "File modified: {0}", filePath);
                index.updateFileInIndex(filePath);
                break;
            default:
                LOG.log(Level.INFO, "Unmatched action with file: {0}", filePath);
        }
    }

    private boolean isTemproraryFile(Path fileName) {
        String fileNameStr = fileName.toString();
        return fileNameStr.endsWith("~") || fileNameStr.endsWith(".tmp") || fileNameStr.startsWith(".");
    }

}