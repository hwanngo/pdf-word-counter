import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import vn.hus.nlp.tokenizer.VietTokenizer;

class Model {
    private String originalText;
    private String tokenizedText;
    private HashSet<String> pdfStopWords = new HashSet<>();
    private final String resourcePath;
    private final String resultPath;
    private final String stopWordsFile;
    private HashSet<String> stopWords = new HashSet<>();
    private final String originalTextFile;
    private final String tokenizedTextFile;
    private final String cleanedTextFile;
    private int result = 0;

    Model() {
        this("resources/", "results/");
    }

    Model(String resourcePath, String resultPath) {
        this.resourcePath = resourcePath;
        this.resultPath = resultPath;
        this.stopWordsFile = new File(resourcePath, "StopWords.txt").getPath();
        this.originalTextFile = new File(resultPath, "1.OriginalText.txt").getPath();
        this.tokenizedTextFile = new File(resultPath, "2.TokenizedText.txt").getPath();
        this.cleanedTextFile = new File(resultPath, "3.CleanedText.txt").getPath();
    }

    private String getFileExtension(String filePath) {
        String extension="";

        int i = filePath.lastIndexOf('.');
        int p = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));

        if (i > p) {
            extension = filePath.substring(i+1);
        }

        return extension;
    }

    private boolean isPdf(String filePath) {
        String extension = getFileExtension(filePath);
        return extension.equals("pdf");
    }

    void validateSelectedFile(File file) throws IOException {
        if (file == null) {
            throw new IOException("No PDF file selected");
        }
        if (!file.exists()) {
            throw new IOException("Selected PDF does not exist: " + file.getAbsolutePath());
        }
        if (!isPdf(file.getAbsolutePath().toLowerCase())) {
            throw new IOException("Selected file is not a PDF: " + file.getName());
        }
    }

    void ensureRuntimePaths() throws IOException {
        File resourcesDir = new File(resourcePath);
        File resultsDir = new File(resultPath);
        File stopWords = new File(stopWordsFile);

        if (!resourcesDir.isDirectory()) {
            throw new IOException("Missing resources directory: " + resourcesDir.getAbsolutePath());
        }
        if (!stopWords.isFile()) {
            throw new IOException("Missing stop words file: " + stopWords.getAbsolutePath());
        }
        if (!resultsDir.exists() && !resultsDir.mkdirs()) {
            throw new IOException("Unable to create results directory: " + resultsDir.getAbsolutePath());
        }
    }

    private String loadPdf(File file) throws IOException {
        validateSelectedFile(file);
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private HashSet<String> loadStopWords() throws IOException {
        stopWords.clear();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(stopWordsFile))) {
            String strCurrentLine;
            while ((strCurrentLine = bufferedReader.readLine()) != null) {
                stopWords.add(strCurrentLine);
            }
        }

        return stopWords;
    }

    private void writeToFile(String filename, String text) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(text);
        }
    }

    String loadTxt() throws IOException {
        StringBuilder text = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(tokenizedTextFile))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (text.length() > 0) {
                    text.append(' ');
                }
                text.append(currentLine);
            }
        }

        return text.toString();
    }

    private static ArrayList<String> convertTextToWordList(String string) {
        ArrayList<String> wordList = new ArrayList<>();

        if (string == null || string.trim().isEmpty()) {
            return wordList;
        }

        String[] wordArray = string.trim().split("\\s+");

        Collections.addAll(wordList, wordArray);

        return wordList;
    }

    private int countWord(ArrayList<String> wordList, HashSet<String> stopWords) {
        int count = 0;
        pdfStopWords.clear();

        for (String word : wordList) {
            if (!stopWords.contains(word)) {
                count++;
            } else {
                pdfStopWords.add(word);
            }
        }

        return count;
    }

    private String cleanString(String string) {
        return string.replaceAll("\\r\\n", " ").replaceAll("[0-9$&+,:;=?@#|'<>.^*()%!-/\\\\{}\\[\\]`~’]", " ").replaceAll("\\s\\s+", " ").toLowerCase();
    }

    void clearProcessingState() {
        originalText = "";
        tokenizedText = "";
        pdfStopWords.clear();
        result = 0;
    }


    void runCountWord(File file) throws IOException {
        ensureRuntimePaths();
        originalText = loadPdf(file);
        writeToFile(originalTextFile, originalText);

        VietTokenizer tokenizer = new VietTokenizer();
        tokenizer.tokenize(originalTextFile, tokenizedTextFile);

        tokenizedText = loadTxt();
        String cleanedText = cleanString(tokenizedText).trim();
        writeToFile(cleanedTextFile, cleanedText);
        ArrayList<String> wordList = convertTextToWordList(cleanedText);

        HashSet<String> stopWords = loadStopWords();

        result = countWord(wordList, stopWords);
    }

    String getOriginalText() {
        return originalText;
    }

    String getTokenizedText() {
        return cleanString(tokenizedText);
    }

    String getPdfStopWords() {
        return pdfStopWords.toString();
    }

    String getStopWords() {
        return stopWords.toString();
    }

    int getResult() {
        return result;
    }
}
