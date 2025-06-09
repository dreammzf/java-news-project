package com.dreammzf;

public interface IParser {
    boolean foundKeywords(String title, String description);
    void parseRiaNews(String url);
    void parseLentaNews(String category);
    void parseGuardianNews(String category);
    void parseVestiNews(String category);
    void parseGazetaNews(String category);
}