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
    public void addFile(Path filePath) {
        files.add(filePath);
    }

    public void addInstance(String pathStr) throws NoSuchFileException {
        Path path = Path.of(pathStr);
        if (!Files.exists(path)) {
            throw new NoSuchFileException(pathStr);
        }

        if (Files.isRegularFile(path)) {
            addFile(path);
        } else if (Files.isDirectory(path)) {
            try {
                addDirectory(path);
            } catch (DirectoryProcessingException e) {
                throw new NoSuchFileException(e.getLocalizedMessage());
            }
        }
    }

    private void addDirectory(Path dirPath) throws DirectoryProcessingException {
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
