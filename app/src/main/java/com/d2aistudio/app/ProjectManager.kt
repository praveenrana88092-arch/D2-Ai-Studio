
package com.d2aistudio.app

data class VideoProject(
    val id: String,
    val name: String,
    val description: String,
    val type: String,
    val createdAt: Long = System.currentTimeMillis()
)

object ProjectManager {

    private val projects = mutableListOf<VideoProject>()

    fun create(
        name: String,
        description: String,
        type: String
    ): VideoProject {
        val project = VideoProject(
            id = System.currentTimeMillis().toString(),
            name = name,
            description = description,
            type = type
        )

        projects.add(project)
        return project
    }

    fun getAll(): List<VideoProject> {
        return projects.toList()
    }

    fun delete(id: String) {
        projects.removeAll { it.id == id }
    }

    fun find(id: String): VideoProject? {
        return projects.find { it.id == id }
    }
}
