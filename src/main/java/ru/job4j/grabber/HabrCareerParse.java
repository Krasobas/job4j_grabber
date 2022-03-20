package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final int MAX_PAGE_NUMBER = 1;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }
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

    private Post createPost(Element el) throws ParseException, IOException {
        Element timeElement = el.select(".vacancy-card__date").first();
        String time = timeElement.child(0).attr("datetime");
        LocalDateTime created = dateTimeParser.parse(time);
        Element titleElement = el.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String title = titleElement.text();
        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String description = retrieveDescription(link);
        return new Post(title, link, description, created);
    }

    @Override
    public List<Post> list(String link) {
        List<Post> rsl = new ArrayList<>();
        for (int page = 1; page < MAX_PAGE_NUMBER + 1; page++) {
            Connection connection = Jsoup.connect(String.format("%s?page=%d", link, page));
            try {
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    try {
                        rsl.add(createPost(row));
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rsl;
    }

    public static void main(String[] args) {
        HabrCareerParse app = new HabrCareerParse(new HabrCareerDateTimeParser());
        System.out.println(app.list(PAGE_LINK));
    }
}
