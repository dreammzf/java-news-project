package com.dreammzf;

import java.util.ArrayList;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Database database = new Database();
        ArrayList<String> mySources = new ArrayList<>();
        ArrayList<String> myCategories = new ArrayList<>();
        ArrayList<String> myKeywords = new ArrayList<>();
        Analytics analytics = new Analytics();
        int newsAmount = 10;
        System.out.println("News Aggregator");
        helpCommand();
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            String cmd;
            do {
                System.out.print("> ");
                cmd = scanner.nextLine().trim();
                if (cmd.startsWith("ct add")) {
                    categoryAddCommand(cmd, myCategories); 
                } else if (cmd.startsWith("ct remove")) {
                    categoryRemoveCommand(cmd, myCategories);
                } else if (cmd.startsWith("kw add")) {
                    keywordsAddCommand(cmd, myKeywords);
                } else if (cmd.startsWith("kw remove")) {
                    keywordsRemoveCommand(cmd, myKeywords);
                } else if (cmd.startsWith("nw max")) {
                    newsAmount = newsMaxCommand(newsAmount, cmd);
                } else if (cmd.startsWith("sc add")) {
                    sourceAddCommand(cmd, mySources);
                } else if (cmd.startsWith("sc remove")) {
                    sourceRemoveCommand(cmd, mySources); 
                } else {
                    switch (cmd) {
                        case "" -> {}
                        case "help" -> helpCommand();
                        case "ct" -> categoryHelpCommand();
                        case "ct help" -> categoryHelpCommand();
                        case "ct list" -> categoryListCommand();
                        case "ct mylist" -> categoryMylistCommand(myCategories);
                        case "db" -> dbHelpCommand();
                        case "db help" -> dbHelpCommand();
                        case "db get" -> dbGetCommand(database);
                        case "db clear" -> dbClearCommand(database);
                        case "fl" -> flHelpCommand();
                        case "fl help" -> flHelpCommand();
                        case "fl html" -> flHtmlCommand(database);
                        case "fl json" -> flJsonCommand(database);
                        case "kw" -> keywordsHelpCommand();
                        case "kw help" -> keywordsHelpCommand();
                        case "kw mylist" -> keywordsMylistCommand(myKeywords);
                        case "sc" -> sourceHelpCommand();
                        case "sc help" -> sourceHelpCommand();
                        case "sc list" -> sourceListCommand();
                        case "sc mylist" -> sourceMylistCommand(mySources);
                        case "stats" -> analytics.printStatistics();
                        case "nw help" -> newsHelpCommand();
                        case "nw" -> newsHelpCommand();
                        case "nw parse" -> newsParseCommand(mySources, myCategories, myKeywords, newsAmount, database, analytics);
                        case "nw stop" -> newsStopCommand();
                        default -> System.out.println("Unknown command. \"help\" for help");
                    }
                }
            } while (!"sc stop".equals(cmd));
        }
    }

    private static void helpCommand() {
        System.out.println("db - Database commands");
        System.out.println("ct - Category settings");
        System.out.println("fl - File savers");
        System.out.println("kw - Keyword settings");
        System.out.println("nw - News commands");
        System.out.println("sc - Source settings");
        System.out.println("stats - Statistics");
    }

    public static void categoryAddCommand(String cmd, ArrayList<String> myCategories) {
        if ("ct add".equals(cmd)) {
            System.out.println("Usage: ct add <category>");
            return;
        }
        String[] cmdSplit = cmd.split(" ");
        String source = cmdSplit[2];
        if (myCategories.contains(source)) {
            System.out.println(source + " is already in your categories list.");
            return;
        }
        if ("economy".equals(source) || "politics".equals(source) || "science".equals(source) || "culture".equals(source) || "sports".equals(source) || "tourism".equals(source) || "society".equals(source)) {
            myCategories.add(source);
            System.out.println("Added " + source + " to the categories list");
        }
        else {
            System.out.println("Unknown category. \"ct list\" for available categories");
        }
    }

    private static void categoryHelpCommand() {
        System.out.println("ct add");
        System.out.println("ct help");
        System.out.println("ct mylist");
        System.out.println("ct list");
        System.out.println("ct remove");
    }

    private static void categoryMylistCommand(ArrayList<String> myCategories) {
        for (String category : myCategories) {
            System.out.println(category);
        }
    }

    private static void categoryListCommand() {
        System.out.println("Available categories to parse:");
        System.out.println("economy");
        System.out.println("politics");
        System.out.println("science");
        System.out.println("culture");
        System.out.println("sports");
        System.out.println("tourism");
        System.out.println("society");
    }

    public static void categoryRemoveCommand(String cmd, ArrayList<String> myCategories) {
        if ("ct remove".equals(cmd)) {
            System.out.println("Usage: ct remove <category>");
            return;
        }
        String[] cmdSplit = cmd.split(" ");
        String source = cmdSplit[2];
        if (!myCategories.contains(source)) {
            System.out.println(source + " is not in your categories list yet.");
            return;
        }
        if ("economy".equals(source) || "politics".equals(source) || "science".equals(source) || "culture".equals(source) || "sports".equals(source) || "tourism".equals(source) || "society".equals(source)) {
            myCategories.remove(source);
            System.out.println("Removed " + source + " from the categories list");
        }
        else {
            System.out.println("Unknown category. \"sc list\" for available categories");
        }
    }

    private static void dbHelpCommand() {
        System.out.println("db clear");
        System.out.println("db get");
        System.out.println("db help");
    }

    private static void dbGetCommand(Database database) {
        database.getTable();
    }

    private static void dbClearCommand(Database database) {
        database.clearTable();
    }
    
    private static void flHelpCommand() {
        System.out.println("fl help");
        System.out.println("fl html");
        System.out.println("fl json");
    }

    private static void flJsonCommand(Database database) {
        try {
            FileExporter exporter = new FileExporter(database);
            exporter.exportToJson("news.json");
            System.out.println("Exported news to news.json");
        } catch (Exception e) {
            System.err.println("Failed to export news to json: " + e.getMessage());
        }
    }
    private static void flHtmlCommand(Database database) {
        try {
            FileExporter exporter = new FileExporter(database);
            exporter.exportToHtml("news.html");
            System.out.println("Exported news to news.html");
        } catch (Exception e) {
            System.err.println("Failed to export news to html: " + e.getMessage());
        }
    }

    private static void keywordsHelpCommand() {
        System.out.println("kw help");
        System.out.println("kw add");
        System.out.println("kw mylist");
        System.out.println("kw remove");
    }

    public static void keywordsAddCommand(String cmd, ArrayList<String> myKeywords) {
        if ("kw add".equals(cmd)) {
            System.out.println("Usage: kw add <keyword>");
            return;
        }
        String[] cmdSplit = cmd.split(" ");
        String keyword = cmdSplit[2];
        myKeywords.add(keyword);
        System.out.println("Added " + keyword + " to the keywords list");
    }

    private static void keywordsMylistCommand(ArrayList<String> myKeywords) {
        for (String keyword : myKeywords) {
            System.out.println(keyword);
        }
    }

    public static void keywordsRemoveCommand(String cmd, ArrayList<String> myKeywords) {
        if ("kw remove".equals(cmd)) {
            System.out.println("Usage: kw remove <keyword>");
            return;
        }
        String[] cmdSplit = cmd.split(" ");
        String keyword = cmdSplit[2];
        if (!myKeywords.contains(keyword)) {
            System.out.println(keyword + " is not in your keywords list yet.");
            return;
        }
        myKeywords.remove(keyword);
        System.out.println("Removed " + keyword + " from the sources list");
    }

    private static void newsHelpCommand() {
        System.out.println("nw help");
        System.out.println("nw max");
        System.out.println("nw parse");
        System.out.println("nw stop");
    }

    public static int newsMaxCommand(int newsAmount, String cmd) {
        if ("nw max".equals(cmd)) {
            System.out.println("Usage: nw max <maximum amount of news to parse from each source>");
            return newsAmount;
        }
        String[] cmdSplit = cmd.split(" ");
        try {
            newsAmount = Integer.parseInt(cmdSplit[2]);
        } catch (NumberFormatException e) {
            System.out.println("Usage: nw max <maximum amount of news to parse from each source>");
            return newsAmount;
        }
        System.out.println("Set maximum amount of news to parse to " +newsAmount);
        return newsAmount;
    }

    private static void newsParseCommand(ArrayList<String> mySources, ArrayList<String> myCategories, ArrayList<String> myKeywords, int newsAmount, Database database, Analytics analytics) {
        if (mySources.isEmpty()) {
            System.out.println("Choose at least one source to parse.");
            return;
        }
        if (myCategories.isEmpty()) {
            System.out.println("Choose at least one category to parse.");
            return;
        }
        Parser parser = new Parser(newsAmount, database, myKeywords, analytics);
        for (String category : myCategories) {
            if (mySources.contains("ria")) {
                if ("sports".equals(category)) {
                    parser.parseRiaNews("https://rsport.ria.ru/football/");
                    parser.parseRiaNews("https://rsport.ria.ru/hockey/");
                    parser.parseRiaNews("https://rsport.ria.ru/tennis/");
                } else {
                    parser.parseRiaNews("https://ria.ru/"+category+"/");
                }
            }
            if (mySources.contains("lenta")) {
                parser.parseLentaNews(category);
            }
            if (mySources.contains("guardian")) {
                parser.parseGuardianNews(category);
            }
            if (mySources.contains("vesti")) {
                parser.parseVestiNews(category);
            }
            if (mySources.contains("gazeta")) {
                parser.parseGazetaNews(category);
            }
        }
    }

    private static void newsStopCommand() {
        System.exit(0);
    }

    public static void sourceAddCommand(String cmd, ArrayList<String> mySources) {
        if ("sc add".equals(cmd)) {
            System.out.println("Usage: sc add <source>");
            return;
        }
        String[] cmdSplit = cmd.split(" ");
        String source = cmdSplit[2];
        if (mySources.contains(source)) {
            System.out.println(source + " is already in your sources list.");
            return;
        }
        if ("ria".equals(source) || "lenta".equals(source) || "guardian".equals(source) || "vesti".equals(source) || "gazeta".equals(source)) {
            mySources.add(source);
            System.out.println("Added " + source + " to the sources list");
        }
        else {
            System.out.println("Unknown source. \"sc list\" for available sources");
        }
    }

    private static void sourceHelpCommand() {
        System.out.println("sc add");
        System.out.println("sc help");
        System.out.println("sc mylist");
        System.out.println("sc list");
        System.out.println("sc remove");
    }

    private static void sourceMylistCommand(ArrayList<String> mySources) {
        for (String source : mySources) {
            System.out.println(source);
        }
    }

    private static void sourceListCommand() {
        System.out.println("Available sources to parse:");
        System.out.println("ria (ria.ru)");
        System.out.println("lenta (lenta.ru)");
        System.out.println("guardian (theguardian.com)");
        System.out.println("vesti (vesti.ru)");
        System.out.println("gazeta (gazeta.ru)");
    }

    public static void sourceRemoveCommand(String cmd, ArrayList<String> mySources) {
        if ("sc remove".equals(cmd)) {
            System.out.println("Usage: sc remove <source>");
            return;
        }
        String[] cmdSplit = cmd.split(" ");
        String source = cmdSplit[2];
        if (!mySources.contains(source)) {
            System.out.println(source + " is not in your sources list yet.");
            return;
        }
        if ("ria".equals(source) || "lenta".equals(source) || "guardian".equals(source) || "vesti".equals(source) || "gazeta".equals(source)) {
            mySources.remove(source);
            System.out.println("Removed " + source + " from the sources list");
        }
        else {
            System.out.println("Unknown source. \"sc list\" for available sources");
        }
    }
}
