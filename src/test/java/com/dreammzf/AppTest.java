package com.dreammzf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppTest {
    private Database database;
    private Analytics analytics;
    private ArrayList<String> testSources;
    private ArrayList<String> testCategories;
    private ArrayList<String> testKeywords;

    @BeforeEach
    public void setUp() {
        database = new Database();
        analytics = new Analytics();
        testSources = new ArrayList<>();
        testCategories = new ArrayList<>();
        testKeywords = new ArrayList<>();
    }

    @Test
    public void testDatabaseConnection() {
        assertNotNull(database.getConnection());
    }

    @Test
    public void testDatabaseInsertAndClear() {
        database.insertNews("title", "desc", "http://smthn.com", "test", "2023-01-01", "", "Test Source");
        database.clearTable();
    }

    @Test
    public void testAnalyticsCounting() {
        analytics.countNews();
        analytics.countNewsWithKeywords();
        analytics.countNewsWithImages();
        analytics.countSource("Test Source");
        analytics.countCategory("Test Category");
        
        Map<String, Integer> expectedCategories = new HashMap<>();
        expectedCategories.put("Test Category", 1);
        
        assertEquals(1, analytics.getCategoryCounts().get("Test Category"));
    }

    @Test
    public void testTextCleaner() {
        String text = "Test? text with?question marks?";
        String cleanText = TextCleaner.clean(text);
        assertEquals("Test? text withquestion marks?", cleanText);
    }

    @Test
    public void testCategoryCommands() {
        App.categoryAddCommand("ct add economy", testCategories);
        assertTrue(testCategories.contains("economy"));
        assertEquals(1, testCategories.size());
        
        App.categoryRemoveCommand("ct remove economy", testCategories);
        assertFalse(testCategories.contains("economy"));
        assertEquals(0, testCategories.size());
    }

    @Test
    public void testSourceCommands() {
        App.sourceAddCommand("sc add ria", testSources);
        assertTrue(testSources.contains("ria"));
        assertEquals(1, testSources.size());
        App.sourceRemoveCommand("sc remove ria", testSources);
        assertFalse(testSources.contains("ria"));
        assertEquals(0, testSources.size());
    }

    @Test
    public void testKeywordCommands() {
        App.keywordsAddCommand("kw add test", testKeywords);
        assertTrue(testKeywords.contains("test"));
        assertEquals(1, testKeywords.size());
        App.keywordsRemoveCommand("kw remove test", testKeywords);
        assertFalse(testKeywords.contains("test"));
        assertEquals(0, testKeywords.size());
    }

    @Test
    public void testNewsMaxCommand() {
        int currentAmount = 10;
        int newAmount = App.newsMaxCommand(currentAmount, "nw max 15");
        assertEquals(15, newAmount);
        newAmount = App.newsMaxCommand(currentAmount, "nw max invalid");
        assertEquals(currentAmount, newAmount);
    }

    @Test
    public void testParserKeywordMatching() {
        Parser parser = new Parser(10, database, testKeywords, analytics);
        testKeywords.add("test");
        assertTrue(parser.foundKeywords("test", "This is a test description"));
        assertFalse(parser.foundKeywords("Regular title", "Regular description"));
    }
}