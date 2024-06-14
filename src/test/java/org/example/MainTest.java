package org.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


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
    public void testShortestPath_1() throws IOException {
        String word1 = "to";
        String word2 = "new";
        Set<String> expectedResults = new HashSet<>(Arrays.asList("to seek out new ", "to explore strange new "));
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        Main.buildGraph(processedText);
        String result = Main.shortestPath(word1, word2);
        //assertTrue(result.equals(expectedResult1) || result.equals(expectedResult2));
        assertTrue(expectedResults.contains(result));
    }

    @Test
    public void testShortestPath_2() throws IOException {
        String word1 = "to";
        String word2 = ",@";
        String expectedResult = "No path found";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        Main.buildGraph(processedText);
        String result = Main.shortestPath(word1, word2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testShortestPath_3() throws IOException {
        String word1 = "to";
        String word2 = "strange new";
        String expectedResult = "No path found";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        Main.buildGraph(processedText);
        String result = Main.shortestPath(word1, word2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testShortestPath_4() throws IOException {
        String word1 = "to";
        String word2 = "exercise";
        String expectedResult = "No path found";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        Main.buildGraph(processedText);
        String result = Main.shortestPath(word1, word2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testShortestPath_5() throws IOException {
        String word1 = "to";
        String word2 = "New";
        String expectedResult = "No path found";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        Main.buildGraph(processedText);
        String result = Main.shortestPath(word1, word2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testShortestPath_6() throws IOException {
        String word1 = "#)";
        String word2 = "new";
        String expectedResult = "No path found";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        Main.buildGraph(processedText);
        String result = Main.shortestPath(word1, word2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testShortestPath_7() throws IOException {
        String word1 = "to explore";
        String word2 = "new";
        String expectedResult = "No path found";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        Main.buildGraph(processedText);
        String result = Main.shortestPath(word1, word2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testShortestPath_8() throws IOException {
        String word1 = "first";
        String word2 = "new";
        String expectedResult = "No path found";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        Main.buildGraph(processedText);
        String result = Main.shortestPath(word1, word2);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testShortestPath_9() throws IOException {
        String word1 = "To";
        String word2 = "new";
        String expectedResult = "No path found";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        Main.buildGraph(processedText);
        String result = Main.shortestPath(word1, word2);
        assertEquals(expectedResult, result);
    }



    @Test
    public void testQueryBridgeWords_NoWord1AndWord2InGraph() throws IOException {
        String word1 = "djx";
        String word2 = "dcx";
        String expectedResult = "1";
        String expectedOutput = "No \"djx\" and \"dcx\" in the graph !";
        String filePath = "./file.txt";
        String processedText = Main.processText(filePath);
        Main.buildGraph(processedText);
        String result = Main.queryBridgeWords(word1, word2);
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
        Main.buildGraph(processedText);
        String result = Main.queryBridgeWords(word1, word2);
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
        Main.buildGraph(processedText);
        String result = Main.queryBridgeWords(word1, word2);
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
        Main.buildGraph(processedText);
        String result = Main.queryBridgeWords(word1, word2);
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
        Main.buildGraph(processedText);
        String result = Main.queryBridgeWords(word1, word2);
        assertEquals(expectedResult, result);
        assertEquals(expectedOutput, outContent.toString().trim());
    }
}
