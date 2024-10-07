package io.github.cleverton.heusner.fixture;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment {

    private long id;
    private String text;
    private Date datePublished;
    private Author author;
    private List<Comment> replies;

    public Comment() {
        author = new Author();
        replies = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", datePublished=" + datePublished +
                ", author=" + author +
                ", replies=" + replies +
                '}';
    }
}