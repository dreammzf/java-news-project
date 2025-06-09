package com.dreammzf;

public interface IFileExporter {
    void exportToJson(String filename) throws Exception;
    void exportToHtml(String filename) throws Exception;
}