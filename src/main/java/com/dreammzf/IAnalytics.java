package com.dreammzf;

import java.util.Map;

public interface IAnalytics {
    void countNews();
    void countNewsWithKeywords();
    void countNewsWithImages();
    void countSource(String source);
    void countCategory(String category);
    void printStatistics();
    Map<String, Integer> getCategoryCounts();
}