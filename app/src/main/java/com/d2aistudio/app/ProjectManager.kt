package com.d2aistudio.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class VideoProject(
    val id: String,
    val name: String,
    val description: String,
    val type: String,
    val createdAt: Long = System.currentTimeMillis()
)

object ProjectManager {
    private lateinit var prefs: android.content.SharedPreferences
    private val projects = mutableListOf<VideoProject>()

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(
            "d2_projects", Context.MODE_PRIVATE
        )
        load()
    }

    private fun load() {
        projects.clear()
        val data = prefs.getString("projects", "[]") ?: "[]"
        val array = JSONArray(data)

        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            projects.add(
                VideoProject(
                    item.getString("id"),
                    item.getString("name"),
                    item.getString("description"),
                    item.getString("type"),
                    item.getLong("createdAt")
                )
            )
        }
    }

    private fun save() {
        val array = JSONArray()
        projects.forEach {
            array.put(JSONObject().apply {
                put("id", it.id)
                put("name", it.name)
                put("description", it.description)
                put("type", it.type)
                put("createdAt", it.createdAt)
            })
        }
        prefs.edit().putString("projects", array.toString()).apply()
    }

    fun create(name: String, description: String, type: String): VideoProject {
        val project = VideoProject(
            System.currentTimeMillis().toString(),
            name, description, type
        )
        projects.add(project)
        save()
        return project
    }

    fun getAll(): List<VideoProject> = projects.toList()

    fun delete(id: String) {
        projects.removeAll { it.id == id }
        save()
    }

    fun find(id: String): VideoProject? =
        projects.find { it.id == id }
}
