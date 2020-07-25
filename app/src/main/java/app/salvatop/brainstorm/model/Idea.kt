package app.salvatop.brainstorm.model

import java.io.Serializable

class Idea(var author: String?, var ideaContext: String?, var content: String?, var title: String?, var isPublic: Boolean?, var forks: ArrayList<String>?) : Serializable {

    override fun toString(): String {
        return "Idea{" +
                "author='" + author + '\'' +
                ", contex='" + ideaContext + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", isPublic=" + isPublic +
                ", forks=" + forks +
                '}'
    }
}