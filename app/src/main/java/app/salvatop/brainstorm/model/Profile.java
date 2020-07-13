package app.salvatop.brainstorm.model;

import java.util.ArrayList;

public class Profile {

    private String displayName;
    private ArrayList<String> followed;
    private ArrayList<String> following;
    private ArrayList<String> teams;
    private ArrayList<Idea> ideas;
    private ArrayList<String> bookmarks;

    public Profile(String displayName, ArrayList<String> followed, ArrayList<String> following, ArrayList<String> teams, ArrayList<Idea> ideas, ArrayList<String> bookmarks) {
        this.displayName = displayName;
        this.followed = followed;
        this.following = following;
        this.teams = teams;
        this.ideas = ideas;
        this.bookmarks = bookmarks;
    }

    public Profile(){};

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<String> getFollowed() {
        return followed;
    }

    public void setFollowed(ArrayList<String> followed) {
        this.followed = followed;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<String> teams) {
        this.teams = teams;
    }

    public ArrayList<Idea> getIdeas() {
        return ideas;
    }

    public void setIdeas(ArrayList<Idea> ideas) {
        this.ideas = ideas;
    }

    public ArrayList<String> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(ArrayList<String> bookmarks) {
        this.bookmarks = bookmarks;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "displayName='" + displayName + '\'' +
                ", followed=" + followed +
                ", following=" + following +
                ", teams=" + teams +
                ", ideas=" + ideas +
                ", bookmarks=" + bookmarks +
                '}';
    }
}
