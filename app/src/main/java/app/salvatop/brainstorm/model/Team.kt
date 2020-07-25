package app.salvatop.brainstorm.model

import java.io.Serializable

class Team: Serializable {
    var teamName: String? = null
    var ideas: ArrayList<Idea>? = null
    var members: ArrayList<String>? = null

    constructor()

    constructor(teamName: String?, ideas: ArrayList<Idea>?, members: ArrayList<String>?) {
        this.teamName = teamName
        this.ideas = ideas
        this.members = members
    }
}