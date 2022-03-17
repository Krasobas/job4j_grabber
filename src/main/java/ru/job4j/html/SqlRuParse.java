package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.lesjeudis.com/recherche?q=java&loc=").get();
        Elements row = doc.select(".job-info");
        for (Element td : row) {
            Element href = td.child(0);
            Element date = td.child(1).child(0);
            System.out.println(href.absUrl("href"));
            System.out.println(href.text());
            System.out.println(date.text());
            System.out.printf("%s%n", "=".repeat(50));
        }
    }
}
