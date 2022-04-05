package ru.job4j.grabber.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.StringJoiner;

public class Post {
    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime created;

    public Post() {
    }

    public Post(int id, String title, String link, String description, LocalDateTime created) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.created = created;
    }

    public Post(String title, String link, String description, LocalDateTime created) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String toHTML() {
        StringJoiner html = new StringJoiner(System.lineSeparator());
        html.add("</tr>");
        html.add("<tr>");
        html.add(String.format("<td><a href=\"%s\">%s</a></td>", this.getLink(), this.getTitle()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm EEEE, d MMMM");
        html.add(String.format("<td class=\"created\">%s</td>", this.getCreated().format(formatter)));
        html.add("<td class=\"description\">");
        boolean ul = false;
        for (String line : this.getDescription().split(System.lineSeparator())) {
            if (ul && !line.startsWith("\t– ")) {
                html.add("</ul>");
                ul = false;
            }
            if (line.startsWith("\t– ")) {
                ul = true;
                html.add(String.format("<li>%s</li>", line.replace("\t–", "")));
            } else {
                html.add(String.format("%s<br>", line));
            }
        }
        html.add("</td>");

        html.add("</tr>");
        return html.toString();
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", link='" + link + '\''
                + System.lineSeparator()
                + ", description='" + description + '\''
                + System.lineSeparator()
                + ", created=" + created + '}'
                + System.lineSeparator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return id == post.id && Objects.equals(title, post.title) && Objects.equals(link, post.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, link);
    }
}
