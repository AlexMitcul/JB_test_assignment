package com.amitcul.app;

import com.amitcul.index.IndexCreationException;
import com.amitcul.index.IndexManager;
import com.amitcul.analyze.Analyzer;
import com.amitcul.analyze.AnalyzerBuilder;
import com.amitcul.analyze.LowerCaseFilter;
import com.amitcul.analyze.SimpleTokenizer;
import com.amitcul.files.DirectoryProcessingException;
import com.amitcul.files.FileManager;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger LOG = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        FileManager fileManager = new FileManager();

        try {
            fileManager.addDirectory("/root/jb_test_assessment/app/src/main/resources");
        } catch (DirectoryProcessingException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
        }

        try {
            fileManager.addFile("/root/jb_test_assessment/app/src/main/resources/dirNotToInclude/from_not_to_include_dir.txt");
        } catch (NoSuchFileException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
        }

        fileManager.getFiles().forEach(System.out::println);

        Analyzer analyzer = new AnalyzerBuilder()
                .setTokenizer(new SimpleTokenizer())
                .addFilter(new LowerCaseFilter())
                .build();

        IndexManager indexManager = new IndexManager(fileManager, analyzer);
        try {
            indexManager.index();
        } catch (IndexCreationException e) {
            throw new RuntimeException(e);
        }


        System.out.print("Found at: ");
        indexManager.query("hello").forEach(System.out::println);

        System.out.println("woke");

        System.out.print("Found at: ");
        indexManager.query("hello").forEach(System.out::println);

        while (true) {}

//        indexManager.close();
    }

}
