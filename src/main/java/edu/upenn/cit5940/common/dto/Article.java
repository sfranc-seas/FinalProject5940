package edu.upenn.cit5940.common.dto;

import java.time.LocalDate;

public class Article {

    private String uri;
    private String date;  
    private String title;
    private String body;

    public Article(String uri, String date, String title, String body) {
        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null or empty.");
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }

        this.uri = uri;
        this.date = date;
        this.title = title;
        this.body = body;
    }

    // --- Core Getters ---

    public String getUri() {
        return uri;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    // --- Alias Getters (for project features) ---

    public String getId() {
        return uri;
    }

    public String getContent() {
        return body;
    }

    public LocalDate getPublishDate() {
        return LocalDate.parse(date); // assumes format YYYY-MM-DD
    }

    // --- Optional Setters (safe to keep) ---

    public void setUri(String uri) {
        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null or empty.");
        }
        this.uri = uri;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Article{id='" + uri + "', title='" + title + "', date='" + date + "'}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Article)) {
            return false;
        }
        Article other = (Article) obj;
        return uri != null && uri.equals(other.uri);
    }

    @Override
    public int hashCode() {
        return uri == null ? 0 : uri.hashCode();
    }
}