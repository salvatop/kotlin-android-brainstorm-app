package app.salvatop.brainstorm.model;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class Idea  {

    private String author;
    private String ideaContext;
    private String content;
    private String title;
    private Boolean isPublic;
    private ArrayList<String> forks;

    public Idea(String author, String ideaContext, String content, String title, Boolean isPublic, ArrayList<String> forks) {
        this.author = author;
        this.ideaContext = ideaContext;
        this.content = content;
        this.title = title;
        this.isPublic = isPublic;
        this.forks = forks;
    }

    public Idea(){ }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIdeaContext() {
        return ideaContext;
    }

    public void setIdeaContext(String ideaContext) {
        this.ideaContext = ideaContext;
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
                ", contex='" + ideaContext + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", isPublic=" + isPublic +
                ", forks=" + forks +
                '}';
    }
}
