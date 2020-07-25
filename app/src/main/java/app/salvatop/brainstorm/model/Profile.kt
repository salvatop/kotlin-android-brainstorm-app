package app.salvatop.brainstorm.model

import java.io.Serializable

class Profile : Serializable {
        var city: String? = null
        var motto: String?= null
        var occupation: String? = null
        var displayName: String? = null
        var followed: ArrayList<String>? = null
        var following: ArrayList<String>? = null
        var teams: ArrayList<String>? = null
        var ideas: ArrayList<Idea>? = null
        var bookmarks: ArrayList<String>? = null

    constructor()

    constructor(city: String?, motto: String?, occupation: String?, displayName: String?, followed: ArrayList<String>?, following: ArrayList<String>?, teams: ArrayList<String>?, ideas: ArrayList<Idea>?, bookmarks: ArrayList<String>?) {
        this.city = city
        this.motto = motto
        this.occupation = occupation
        this.displayName = displayName
        this.followed = followed
        this.following = following
        this.teams = teams
        this.ideas = ideas
        this.bookmarks = bookmarks
    }

    override fun toString(): String {
        return "Profile{" +
                "displayName='" + displayName + '\'' +
                ", followed=" + followed +
                ", following=" + following +
                ", teams=" + teams +
                ", ideas=" + ideas +
                ", bookmarks=" + bookmarks +
                '}'
    }
}