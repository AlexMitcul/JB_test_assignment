# README

## Overview
This Java application creates an inverted index of text files or directories and allows querying of specific words within them. It uses custom tokenization and filtering techniques, such as converting text to lowercase, to process and index the contents of files.

## Features
- Supports indexing individual files or entire directories.
- Tokenizes and normalizes text using a customizable `Analyzer` (lowercasing, simple tokenization).
- Efficiently searches for a word and lists the files where the word appears.
- Handles file input errors gracefully, logging any issues related to missing files or directories.

## Dependencies
- Java 21 or higher
- `java.util.Scanner` for user input
- `java.util.logging.Logger` for logging
- Custom classes for indexing and analyzing:
    - `IndexManager`
    - `Analyzer`, `AnalyzerBuilder`, `LowerCaseFilter`, `SimpleTokenizer`
    - `FileManager`

## How to Run

1. **Compile the Program**:
   Run the following command in the root directory of your project:

    ```bash
    ./gradlew build
    ```

2. **Run the Program**:
   Once compiled, you can run the program with:

    ```bash
    java -cp "app/build/libs/app.jar:index/build/libs/index-1.0-SNAPSHOT.jar" com.amitcul.app.App
    ```

3. **Usage**:
    - **Indexing Files**: Enter the absolute path of files or directories that you want to index. After inputting all files, press Enter on an empty line.
    - **Querying Words**: Enter a word to search for its occurrences in the indexed files. Press Enter on an empty line to exit the query loop.

## Example

```text
Enter the absolute path of directory or file (enter empty line to proceed): 
/root/jb_test_assessment/app/src/main/resources
Enter the absolute path of directory or file (enter empty line to proceed): 
/root/jb_test_assessment/app/src/main/resources/dirNotToInclude/from_not_to_include_dir.txt
Indexing completed.

Enter word to query (enter empty line to proceed): 
Hello

There are matches in the following files:
/root/jb_test_assessment/app/src/main/resources/dirNotToInclude/from_not_to_include_dir.txt
```

## Error Handling
- **NoSuchFileException**: If the entered path does not exist, a message will be logged, and the application will continue running.

## Customization
You can extend the functionality by modifying the `Analyzer`:
- Add new filters or tokenizers.
- Modify the existing lowercasing behavior or tokenizer logic.
