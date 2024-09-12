package com.amitcul.app;

import com.amitcul.index.IndexCreationException;
import com.amitcul.index.IndexManager;
import com.amitcul.analyze.Analyzer;
import com.amitcul.analyze.AnalyzerBuilder;
import com.amitcul.analyze.LowerCaseFilter;
import com.amitcul.analyze.SimpleTokenizer;
import com.amitcul.files.FileManager;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger LOG = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws IOException {

        // Setup analyzer
        Analyzer analyzer = new AnalyzerBuilder()
                .setTokenizer(new SimpleTokenizer())
                .addFilter(new LowerCaseFilter())
                .build();

        FileManager fileManager = new FileManager();
        Scanner scanner = new Scanner(System.in);

        // Add files to index
        while (true) {
            System.out.println("Enter the absolute path of directory or file (enter empty line to proceed): ");
            String path = scanner.nextLine();
            if (path.isBlank()) break;
            try {
                fileManager.addInstance(path);
            } catch (NoSuchFileException e) {
                LOG.log(Level.SEVERE, "No such file or directory: {0}", e.getLocalizedMessage());
            }
        }

        // Create an index manager
        IndexManager indexManager = new IndexManager(fileManager, analyzer);
        try {
            indexManager.index();
        } catch (IndexCreationException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return;
        }

        while (true) {
            System.out.println("Enter word to query (enter empty line to proceed): ");
            String token = scanner.nextLine();
            if (token.isBlank()) break;

            Set<Path> result = indexManager.query(token);
            if (result.isEmpty()) {
                System.out.println("There is no matches");
            } else {
                System.out.println("There is matches in following files:");
                result.forEach(System.out::println);
            }
        }

        scanner.close();
        indexManager.close();
    }

}
