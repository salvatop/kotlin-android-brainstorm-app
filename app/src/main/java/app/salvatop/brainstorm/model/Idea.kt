package app.salvatop.brainstorm.model

import java.io.Serializable

class Idea : Serializable {
        lateinit var author: String
        lateinit var ideaContext: String
        lateinit var content: String
        lateinit var title: String
        lateinit var isPublic: String
        lateinit var forks: ArrayList<String>

    constructor()

    constructor(author: String, ideaContext: String, content: String, title: String, isPublic: String, forks: ArrayList<String>) {
        this.author = author
        this.ideaContext = ideaContext
        this.content = content
        this.title = title
        this.isPublic = isPublic
        this.forks = forks
    }
}