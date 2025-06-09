package com.dreammzf;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

public class FileExporter implements IFileExporter{
    final private Database db;

    public FileExporter(Database db) {
        this.db = db;
    }

    @Override
    public void exportToJson(String filename) throws Exception {
        Connection conn = db.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM news");
             FileWriter file = new FileWriter(filename)) {
            JSONArray jsonArray = new JSONArray();
            while (rs.next()) {
                jsonArray.put(new JSONObject()
                    .put("title", rs.getString("title"))
                    .put("description", rs.getString("description"))
                    .put("url", rs.getString("url"))
                    .put("date", rs.getString("publish_date"))
                    .put("source", rs.getString("source")));
            }
            file.write(jsonArray.toString(2));
        }
    }

    @Override
    public void exportToHtml(String filename) throws Exception {
        Connection conn = db.getConnection();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM news"); FileWriter file = new FileWriter(filename)) {
            file.write("<html>\n<body>\n");
            file.write("<pre>\n");
            while (rs.next()) {
                file.write(String.format("""
                                         Title: %s
                                         Description: %s
                                         URL: %s
                                         Date: %s
                                         Source: %s
                                         ----------------------------------------
                                         
                                         """,
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("url"),
                    rs.getString("publish_date"),
                    rs.getString("source")
                ));
            }
            file.write("</pre>\n");
            file.write("</body>\n</html>");
        }
    }
}