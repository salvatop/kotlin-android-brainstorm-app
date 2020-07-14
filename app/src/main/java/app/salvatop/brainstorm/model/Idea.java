package app.salvatop.brainstorm.model;

import java.util.ArrayList;

public class Idea {

    private String author;
    private String context;
    private String content;
    private String title;
    private Boolean isPublic;
    private ArrayList<String> forks;

    public Idea(String author, String context, String content, String title, Boolean isPublic, ArrayList<String> forks) {
        this.author = author;
        this.context = context;
        this.content = content;
        this.title = title;
        this.isPublic = isPublic;
        this.forks = forks;
    }

    public Idea(){};

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public ArrayList<String> getForks() {
        return forks;
    }

    public void setForks(ArrayList<String> forks) {
        this.forks = forks;
    }

    @Override
    public String toString() {
        return "Idea{" +
                "author='" + author + '\'' +
                ", contex='" + context + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", isPublic=" + isPublic +
                ", forks=" + forks +
                '}';
    }
}
