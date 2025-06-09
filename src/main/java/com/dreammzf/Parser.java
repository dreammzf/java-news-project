package com.dreammzf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser implements IParser {
    final private ArrayList<String> keywords;
    final private int amount;
    final private Analytics analytics;
    final private Database db;

    Parser(int amount, Database db, ArrayList<String> keywords, Analytics analytics) {
        this.amount = amount;
        this.db = db;
        this.keywords = keywords;
        this.analytics = analytics;
        if (!keywords.isEmpty()) {
            System.out.print("Parsing news with keywords: ");
            String kw = keywords.stream().collect(Collectors.joining(", "));
            System.out.print(kw);
            System.out.print("\n");
        }
    };

    @Override
    public boolean foundKeywords(String title, String description) {
        boolean foundKeys = false;
        for (String keyword : keywords) {
            if (title.contains(keyword) || description.contains(keyword)) {
                foundKeys = true;
            }
        }
        return foundKeys;
    }

    @Override
    public void parseRiaNews(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select("div.list-item");
            int count = 0;
            for (Element item : items) {
                String title = TextCleaner.clean(item.select("a.list-item__title").text());
                String newsUrl = item.select("a.list-item__title").attr("abs:href");
                String[] data = item.select("div.list-item__info-item").text().split(" ");
                String categoryName = item.select("span.list-tag__text").text();
                String imageUrl = item.select("img.responsive_img").attr("src");
                Document news = Jsoup.connect(newsUrl).ignoreContentType(true).get();
                String descriptionTitle = TextCleaner.clean(news.select("strong").text());
                String description = TextCleaner.clean(news.select(".article__text").text().replace(descriptionTitle, "").trim());
                if (!keywords.isEmpty() && !foundKeywords(title, description)) {
                    continue;
                }
                analytics.countNews();
                if (!imageUrl.isEmpty()) analytics.countNewsWithImages();
                analytics.countSource("РИА Новости");
                analytics.countCategory(categoryName.split(" ")[0]);
                String date;
                String views;
                System.out.println("Заголовок: " + title);
                System.out.println("Категория: " + categoryName.split(" ")[0]);
                switch (data.length) {
                    case 4 -> {
                        date = data[0] + " " + data[1] + " " + data[2];
                        views = data[3];
                    }
                    case 3 -> {
                        date = data[0] + " " + data[1];
                        views = data[2];
                    }
                    default -> {
                        date = data[0] + " " + data[1];
                        views = data[1];
                    }
                }
                System.out.println("Дата публикации: " + date);
                System.out.println("Просмотры: " + views); 
                db.insertNews(title, description, url, categoryName.split(" ")[0], date, imageUrl, "РИА Новости");
                System.out.println("Ссылка: " + newsUrl);
                if (!imageUrl.isEmpty()) {
                    System.out.println("Изображение: " + imageUrl);
                } else {
                    System.out.println("Изображение отсутствует");
                }
                if (!description.isEmpty()) {
                    System.out.println("Краткое описание: " + description.substring(0, description.indexOf('.')));
                } else {
                    System.out.println("Краткое описание отсутствует");
                }
                System.out.println("-------------------------");
                count++;
                if (count == amount) {
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при парсинге РИА Новости: " + e.getMessage());
        }
    }

    @Override
    public void parseLentaNews(String category) {
        String requiredCategory;
        switch (category) {
            case "economy" -> requiredCategory = "Экономика";
            case "politics" -> requiredCategory = "Мир";
            case "science" -> requiredCategory = "Наука и техника";
            case "culture" -> requiredCategory = "Культура";
            case "sports" -> requiredCategory = "Спорт";
            case "tourism" -> requiredCategory = "Путешествия";
            case "society" -> requiredCategory = "Интернет и СМИ";
            default -> requiredCategory = "";
        }
        final String url = "https://lenta.ru/rss/news";
        try {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            Elements items = doc.select("item");
            int count = 0;
            for (Element item : items) {
                String title = TextCleaner.clean(item.select("title").text());
                String newsUrl = item.select("link").text();
                String date = item.select("pubDate").text();
                String categoryName = item.select("category").text();
                if (!requiredCategory.equals(categoryName)) {
                    continue;
                }
                String imageUrl = item.select("enclosure").attr("url");
                Document news = Jsoup.connect(newsUrl).ignoreContentType(true).get();
                String description = TextCleaner.clean(news.select(".topic-body__content-text").text());
                if (!keywords.isEmpty() && !foundKeywords(title, description)) {
                    return;
                }
                analytics.countNews();
                if (!imageUrl.isEmpty()) analytics.countNewsWithImages();
                analytics.countSource("Lenta.ru");
                analytics.countCategory(categoryName);
                db.insertNews(title, description, url, categoryName, date, imageUrl, "РИА Новости");
                System.out.println("Заголовок: " + title);
                System.out.println("Категория: " + categoryName);
                System.out.println("Дата публикации: " + date);
                System.out.println("Ссылка: " + newsUrl);
                if (!imageUrl.isEmpty()) {
                    System.out.println("Изображение: " + imageUrl);
                } else {
                    System.out.println("Изображение отсутствует");
                }
                if (!description.isEmpty()) {
                    System.out.println("Краткое описание: " + description.substring(0, description.indexOf('.')));
                } else {
                    System.out.println("Краткое описание отсутствует");
                }
                System.out.println("-------------------------");
                count++;
                if (count == amount) {
                    return;
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка при парсинге Lenta.ru: " + e.getMessage());
        }
    }

    @Override
    public void parseGuardianNews(String category) {
        String requiredCategory;
        switch (category) {
            case "economy" -> requiredCategory = "business";
            case "politics" -> requiredCategory = "politics";
            case "science" -> requiredCategory = "science";
            case "culture" -> requiredCategory = "culture";
            case "sports" -> requiredCategory = "sport";
            case "tourism" -> requiredCategory = "travel";
            case "society" -> requiredCategory = "world";
            default -> requiredCategory = "";
        }
        final String url = "https://www.theguardian.com/"+requiredCategory;
        try {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            int count = 0;
            Elements items = doc.select("div.dcr-199p3eh");
            for (Element item : items) {
                String title = TextCleaner.clean(item.select("h3.card-headline span.dcr-uyefka").text());
                String newsUrl = "https://www.theguardian.com" + item.select("a").attr("href");
                String categoryName = requiredCategory;            
                String imageUrl = item.select("picture source").first().attr("srcset").split(" ")[0];
                Document news = Jsoup.connect(newsUrl).ignoreContentType(true).get();
                String date = news.select(".dcr-lp0nif").text();
                date = date.substring(0, date.indexOf(" BST"));
                String description = TextCleaner.clean(news.select(".dcr-16w5gq9").text());
                if (!keywords.isEmpty() && !foundKeywords(title, description)) {
                    continue;
                }
                analytics.countNews();
                if (!imageUrl.isEmpty()) analytics.countNewsWithImages();
                analytics.countSource("The Guardian");
                analytics.countCategory(categoryName);
                db.insertNews(title, description, url, categoryName, date, imageUrl, "РИА Новости");
                System.out.println("Заголовок: " + title);
                System.out.println("Категория: " + categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1));
                System.out.println("Дата публикации: " + date);
                System.out.println("Ссылка: " + newsUrl);
                if (!imageUrl.isEmpty()) {
                    System.out.println("Изображение: " + imageUrl);
                } else {
                    System.out.println("Изображение отсутствует");
                }
                if (!description.isEmpty()) {
                    System.out.println("Краткое описание: " + description.substring(0, description.indexOf('.')));
                } else {
                    System.out.println("Краткое описание отсутствует");
                }
                System.out.println("-------------------------");
                count++;
                if (count == amount) {
                    return;
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка при парсинге CNN: " + e.getMessage());
        }
    }

    @Override
    public void parseVestiNews(String category) {
        String requiredCategory;
        switch (category) {
            case "economy" -> requiredCategory = "ekonomika";
            case "politics" -> requiredCategory = "politika";
            case "science" -> requiredCategory = "nauka";
            case "culture" -> requiredCategory = "kultura";
            case "sports" -> requiredCategory = "sport";
            case "tourism" -> requiredCategory = "tag/путешествия";
            case "society" -> requiredCategory = "obshchestvo";
            default -> requiredCategory = "";
        }
        final String url = "https://www.vesti.ru/" + requiredCategory;
        try {
            Document doc = Jsoup.connect(url).get();
            int count = 0;
            Elements items = doc.select(".list__item");
            for (Element item : items) {
                String title = TextCleaner.clean(item.select("h3.list__title").text());
                String categoryName = item.select(".list__src").text();
                String newsUrl = "https://www.vesti.ru" + item.select("a").attr("href");
                String imageUrl = item.select("img").attr("data-src");
                Document news = Jsoup.connect(newsUrl).ignoreContentType(true).get();
                String description = TextCleaner.clean(news.select(".article__anons").text());
                if (!keywords.isEmpty() && !foundKeywords(title, description)) {
                    continue;
                }
                analytics.countNews();
                if (!imageUrl.isEmpty()) analytics.countNewsWithImages();
                analytics.countSource("Вести");
                analytics.countCategory(categoryName);
                String date = item.select(".list__date").text() + " " + item.select(".list__time").text();
                db.insertNews(title, description, url, categoryName, date, imageUrl, "РИА Новости");
                System.out.println("Заголовок: " + title);
                System.out.println("Категория: " + categoryName.split(" ")[0].substring(0, 1).toUpperCase()+categoryName.split(" ")[0].substring(1));
                System.out.println("Дата публикации: " + date);
                System.out.println("Ссылка: " + newsUrl);
                if (!imageUrl.isEmpty()) {
                    System.out.println("Изображение: " + imageUrl);
                } else {
                    System.out.println("Изображение отсутствует");
                }
                if (!description.isEmpty()) {
                    System.out.println("Краткое описание: " + description.substring(0, description.indexOf('.')));
                } else {
                    System.out.println("Краткое описание отсутствует");
                }
                count++;
                if (count == amount) {
                    return;
                }
                System.out.println("-------------------------");
            }
        } catch (IOException e) {
                System.err.println("Ошибка при парсинге Вести (" + requiredCategory + "): " + e.getMessage());
        }
    }

    @Override
    public void parseGazetaNews(String category) {
        String requiredCategory;
        switch (category) {
            case "economy" -> requiredCategory = "business";
            case "politics" -> requiredCategory = "politics";
            case "science" -> requiredCategory = "science";
            case "culture" -> requiredCategory = "culture";
            case "sports" -> requiredCategory = "sport";
            case "tourism" -> requiredCategory = "lifestyle";
            case "society" -> requiredCategory = "social";
            default -> requiredCategory = "";
        }
        final String url = "https://www.gazeta.ru/" + requiredCategory + "/news";
        try {
            Document doc = Jsoup.connect(url).get();
            int count = 0;
            Elements items = doc.select("a.b_ear");
            for (Element item : items) {
                String title = TextCleaner.clean(item.select("div.b_ear-title").text());
                String newsUrl = "https://www.gazeta.ru" + item.attr("href");
                String imageUrl = "";
                Document news = Jsoup.connect(newsUrl).ignoreContentType(true).get();
                String description = TextCleaner.clean(news.select(".b_article-text").text());
                if (!keywords.isEmpty() && !foundKeywords(title, description)) {
                    continue;
                }
                analytics.countNews();
                if (!imageUrl.isEmpty()) analytics.countNewsWithImages();
                analytics.countSource("Газета.ру");
                analytics.countCategory(requiredCategory.substring(0, 1).toUpperCase() + requiredCategory.substring(1));
                if (item.select("div.b_ear-image img").first() != null) {
                    imageUrl = item.select("div.b_ear-image img").first().attr("src");
                }
                String dateTimeSource = item.select("time").attr("datetime");
                String[] dateTime = dateTimeSource.split("T");
                String date = dateTime[0] + " " + dateTime[1].split("\\+")[0];
                db.insertNews(title, "", url, requiredCategory.substring(0, 1).toUpperCase() + requiredCategory.substring(1), date, imageUrl, "РИА Новости");
                System.out.println("Заголовок: " + title);
                System.out.println("Категория: " + requiredCategory.substring(0, 1).toUpperCase() + requiredCategory.substring(1));
                System.out.println("Дата публикации: " + date);
                System.out.println("Ссылка: " + newsUrl);
                if (!imageUrl.isEmpty()) {
                    System.out.println("Изображение: " + imageUrl);
                } else {
                    System.out.println("Изображение отсутствует");
                }
                if (!description.isEmpty()) {
                    System.out.println("Краткое описание: " + description.substring(0, description.indexOf('.')));
                } else {
                    System.out.println("Краткое описание отсутствует");
                }
                System.out.println("-------------------------");
                count++;
                if (count == amount) {
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при парсинге Газета.Ру (" + requiredCategory + "): " + e.getMessage());
        }
    }
}
