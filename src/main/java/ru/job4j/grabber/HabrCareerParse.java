package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.StringJoiner;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private String retrieveDescription(String link) throws IOException {
        StringJoiner rsl = new StringJoiner(System.lineSeparator());
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".job_show_description__vacancy_description");
        rows.forEach(row -> {
            Element descriptionElement = row.select(".style-ugc").first();
            descriptionElement.children().forEach(child -> {
                if ("ul".equals(child.tagName())) {
                    child.children().forEach(li -> rsl.add("\t– " + li.text()));
                } else {
                    rsl.add(child.text());
                }
            });
        });
        return rsl.toString();
    }

    public void init() throws IOException {
        StringBuilder url = new StringBuilder().append(PAGE_LINK).append("?page=");
        int start = PAGE_LINK.length() + 6;
        for (int page = 1; page < 2; page++) {
            url.append(page);
            Connection connection = Jsoup.connect(url.toString());
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element timeElement = row.select(".vacancy-card__date").first();
                String time = timeElement.child(0).attr("datetime");
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s %s%n", time, vacancyName, link);
                try {
                    System.out.println(retrieveDescription(link));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.printf("%n%s%n%n", "=".repeat(30));
            });
            url.delete(start, url.length());
        }
    }

    public static void main(String[] args) {
        try {
            new HabrCareerParse().init();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
