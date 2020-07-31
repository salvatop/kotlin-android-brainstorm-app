package app.salvatop.brainstorm.model

import java.io.Serializable

class Idea : Serializable {
        lateinit var author: String
        lateinit var ideaContext: String
        lateinit var content: String
        lateinit var title: String
        lateinit var visibility: String
        lateinit var forks: HashMap<String, String>

    constructor()

    constructor(author: String, ideaContext: String, content: String, title: String, visibility: String, forks: HashMap<String, String>) {
        this.author = author
        this.ideaContext = ideaContext
        this.content = content
        this.title = title
        this.visibility = visibility
        this.forks = forks
    }
}