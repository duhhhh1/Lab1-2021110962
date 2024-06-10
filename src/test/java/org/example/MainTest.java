package org.example;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class MainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Main main;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        main = new Main();
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testQueryBridgeWords_NoWord1AndWord2InGraph() throws IOException {
        String word1 = "djx";
        String word2 = "dcx";
        String expectedResult = "1";
        String expectedOutput = "No \"djx\" and \"dcx\" in the graph !";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        main.buildGraph(processedText);
        String result = main.queryBridgeWords(word1, word2);
        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, outContent.toString().trim());
    }

    @Test
    public void testQueryBridgeWords_NoWord1InGraph() throws IOException {
        String word1 = "djx";
        String word2 = "to";
        String expectedResult = "1";
        String expectedOutput = "No \"djx\" in the graph !";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        main.buildGraph(processedText);
        String result = main.queryBridgeWords(word1, word2);
        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, outContent.toString().trim());
    }

    @Test
    public void testQueryBridgeWords_NoWord2InGraph() throws IOException {
        String word1 = "to";
        String word2 = "djx";
        String expectedResult = "1";
        String expectedOutput = "No \"djx\" in the graph !";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        main.buildGraph(processedText);
        String result = main.queryBridgeWords(word1, word2);
        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, outContent.toString().trim());
    }

    @Test
    public void testQueryBridgeWords_Word1AndWord2InGraph() throws IOException {
        String word1 = "to";
        String word2 = "out";
        String expectedResult = "seek";
        String expectedOutput = "";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        main.buildGraph(processedText);
        String result = main.queryBridgeWords(word1, word2);
        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, outContent.toString().trim());
    }

    @Test
    public void testQueryBridgeWords_NoBridgeWords() throws IOException {
        String word1 = "to";
        String word2 = "new";
        String expectedResult = "";
        String expectedOutput = "";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        main.buildGraph(processedText);
        String result = main.queryBridgeWords(word1, word2);
        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, outContent.toString().trim());
    }
}
