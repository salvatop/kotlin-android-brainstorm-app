package app.salvatop.brainstorm.model

import java.io.Serializable

class Profile(var city: String?, var motto: String?, var occupation: String?, var displayName: String?, var followed: ArrayList<String>?, var following: ArrayList<String>?, var teams: ArrayList<String>?, var ideas: ArrayList<Idea>?, var bookmarks: ArrayList<String>?) : Serializable {

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