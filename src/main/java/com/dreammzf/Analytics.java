package com.dreammzf;

import java.util.HashMap;
import java.util.Map;

public class Analytics implements IAnalytics{
    private int news = 0;
    private int newsWithKeywords = 0;
    private int newsWithImages = 0;
    private final Map<String, Integer> sourceCounts = new HashMap<>();
    private final Map<String, Integer> categoryCounts = new HashMap<>();

    @Override
    public void countNews() {
        news++;
    }

    @Override
    public void countNewsWithKeywords() {
        newsWithKeywords++;
    }

    @Override
    public void countNewsWithImages() {
        newsWithImages++;
    }

    @Override
    public void countSource(String source) {
        sourceCounts.put(source, sourceCounts.getOrDefault(source, 0) + 1);
    }

    @Override
    public void countCategory(String category) {
        categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
    }

    @Override
    public void printStatistics() {
        System.out.println("news parsed: " + news);
        System.out.println("news with keywords parsed: " + newsWithKeywords);
        System.out.println("news with images parsed: " + newsWithImages);
        System.out.println("sources parsed:");
        for (Map.Entry<String, Integer> entry : sourceCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("categories parsed:");
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    @Override
    public Map<String, Integer> getCategoryCounts() {
        return categoryCounts;
    }
}