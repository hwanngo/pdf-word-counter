import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class ModelBehaviorTest {

    @Test
    public void validateSelectedFileRejectsNull() throws Exception {
        Model model = new Model("resources/", "results/");

        try {
            model.validateSelectedFile(null);
            fail("Expected IOException for null file");
        } catch (java.io.IOException ex) {
            assertTrue(ex.getMessage().contains("No PDF file selected"));
        }
    }

    @Test
    public void ensureRuntimePathsCreatesResultsDirectory() throws Exception {
        Path root = Files.createTempDirectory("wordcount-model-");
        Path resources = Files.createDirectories(root.resolve("resources"));
        Files.writeString(resources.resolve("StopWords.txt"), "va\nla\n");
        Path results = root.resolve("results");

        Model model = new Model(resources.toString() + File.separator, results.toString() + File.separator);
        model.ensureRuntimePaths();

        assertTrue(Files.isDirectory(results));
    }

    @Test
    public void loadTxtPreservesBoundariesBetweenLines() throws Exception {
        Path root = Files.createTempDirectory("wordcount-model-");
        Path resources = Files.createDirectories(root.resolve("resources"));
        Path results = Files.createDirectories(root.resolve("results"));
        Files.writeString(resources.resolve("StopWords.txt"), "va\nla\n");
        Files.writeString(results.resolve("2.TokenizedText.txt"), "xin_chao\nban_toi");

        Model model = new Model(resources.toString() + File.separator, results.toString() + File.separator);

        assertEquals("xin_chao ban_toi", model.loadTxt().trim());
    }

    @Test
    public void ensureRuntimePathsAcceptsPathsWithoutTrailingSeparator() throws Exception {
        Path root = Files.createTempDirectory("wordcount-model-");
        Path resources = Files.createDirectories(root.resolve("resources"));
        Files.writeString(resources.resolve("StopWords.txt"), "va\nla\n");
        Path results = root.resolve("results");

        Model model = new Model(resources.toString(), results.toString());
        model.ensureRuntimePaths();

        assertTrue(Files.isDirectory(results));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void emptyCleanedTextCountsAsZeroWords() throws Exception {
        Method convertTextToWordList = Model.class.getDeclaredMethod("convertTextToWordList", String.class);
        convertTextToWordList.setAccessible(true);
        ArrayList<String> wordList = (ArrayList<String>) convertTextToWordList.invoke(null, "");

        Method countWord = Model.class.getDeclaredMethod("countWord", ArrayList.class, HashSet.class);
        countWord.setAccessible(true);
        int count = (Integer) countWord.invoke(new Model(), wordList, new HashSet<String>());

        assertEquals(0, count);
    }
}
