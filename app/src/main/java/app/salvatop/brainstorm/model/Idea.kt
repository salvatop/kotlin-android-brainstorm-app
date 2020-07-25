package app.salvatop.brainstorm.model

import java.io.Serializable

class Idea : Serializable {
        var author: String? = null
        var ideaContext: String? = null
        var content: String? = null
        var title: String? = null
        var isPublic: Boolean? = null
        var forks: ArrayList<String>? = null

    constructor()

    constructor(author: String?, ideaContext: String?, content: String?, title: String?, isPublic: Boolean?, forks: ArrayList<String>?) {
        this.author = author
        this.ideaContext = ideaContext
        this.content = content
        this.title = title
        this.isPublic = isPublic
        this.forks = forks
    }

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