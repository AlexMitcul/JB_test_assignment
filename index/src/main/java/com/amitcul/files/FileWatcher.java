package com.amitcul.files;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;


public class FileWatcher implements Runnable {
    private final static Logger LOG = Logger.getLogger(FileWatcher.class.getName());

    private final WatchService watchService;
    private final Map<WatchKey, Path> watchKeys;
    private final List<Path> filesToWatch;
    private final BlockingQueue<Event> eventsQueue;
    private final ExecutorService executorService;
    private Future<?> watcherFuture;

    public FileWatcher(List<Path> filesToWatch, BlockingQueue<Event> eventsQueue) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        watchKeys = new ConcurrentHashMap<>();
        this.filesToWatch = filesToWatch;
        this.eventsQueue = eventsQueue;
        this.executorService = Executors.newSingleThreadExecutor();
        registerFiles();
    }

    public void startWatching() {
        watcherFuture = executorService.submit(this);
    }

    public void stopWatching() {
        if (watcherFuture != null) {
            watcherFuture.cancel(true);
        }
        executorService.shutdown();
    }

    @Override
    public void run() {
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            for (WatchEvent<?> watchEvent : key.pollEvents()) {
                Event event = new Event(watchKeys.get(key), watchEvent);
                boolean added = eventsQueue.offer(event);
                if (!added) LOG.log(Level.WARNING, "Failed to add event to queue: {0}", event);
            }

            boolean valid = key.reset();
            if (!valid) {
                LOG.log(Level.WARNING, "WatchKey is no longer valid. Exiting.");
                break;
            }
        }
    }

    private void registerFiles() {
        filesToWatch.forEach( file -> {
            Path parentDir = file.getParent();
            if (parentDir != null) {
                try {
                    WatchKey key = parentDir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                    watchKeys.put(key, parentDir);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Failed to register file: " + file, e);
                }
            }
        });
    }
}