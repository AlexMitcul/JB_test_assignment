package com.amitcul.files;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class FileManager {
    private FileWatcher fileWatcher;
    private final List<Path> files; //TODO: Change type to File

    //region Constructors
    public FileManager() {
        files = new ArrayList<>();
    }
    //endregion

    //region Register Files & Dirs API
    public void addFile(String filePath) throws NoSuchFileException {
        Path path = Paths.get(filePath);
        checkFile(path);
        files.add(path);
    }

    public void addFile(Path filePath) {
        files.add(filePath);
    }

    public void addDirectory(String dirPath) throws DirectoryProcessingException {
        walkByDir(Paths.get(dirPath));
    }

    public void addDirectory(Path dirPath) throws DirectoryProcessingException {
        walkByDir(dirPath);
    }
    //endregion

    //region Getters
    public List<Path> getFiles() {
        return files;
    }

    public int getFilesCount() {
        return files.size();
    }
    //endregion

    //region Private Region
    private void checkFile(Path path) throws NoSuchFileException {
        if (path == null || !Files.exists(path)) {
            throw new NoSuchFileException(path != null ? path.toString() : "null", null, "Invalid path: " + path);
        }
    }

    private void walkByDir(Path dirPath) throws DirectoryProcessingException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, Files::isRegularFile)) {
            stream.forEach(this::addFile);
        } catch (IOException e) {
            throw new DirectoryProcessingException("Error processing directory: " + dirPath, e);
        }
    }

    //region FileWatcher
    public void setUpWatcher(BlockingQueue<Event> eventsQueue) throws IOException {
        fileWatcher = new FileWatcher(this.getFiles(), eventsQueue);
        fileWatcher.startWatching();
    }

    public void downFileWatcher() {
        if (fileWatcher != null) {
            fileWatcher.stopWatching();
        }
    }
    //endregion
}
