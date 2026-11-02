
package com.d2aistudio.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

enum class MediaType { IMAGE, VIDEO, AUDIO }

data class MediaItem(
    val id: String,
    val name: String,
    val path: String,
    val type: MediaType,
    val createdAt: Long = System.currentTimeMillis()
)

object MediaLibrary {
    private lateinit var prefs: android.content.SharedPreferences
    private val items = mutableListOf<MediaItem>()

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(
            "d2_media", Context.MODE_PRIVATE
        )
        load()
    }

    private fun load() {
        items.clear()
        val data = prefs.getString("media", "[]") ?: "[]"
        val array = JSONArray(data)

        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            items.add(
                MediaItem(
                    item.getString("id"),
                    item.getString("name"),
                    item.getString("path"),
                    MediaType.valueOf(item.getString("type")),
                    item.getLong("createdAt")
                )
            )
        }
    }

    private fun save() {
        val array = JSONArray()
        items.forEach {
            array.put(JSONObject().apply {
                put("id", it.id)
                put("name", it.name)
                put("path", it.path)
                put("type", it.type.name)
                put("createdAt", it.createdAt)
            })
        }
        prefs.edit().putString("media", array.toString()).apply()
    }

    fun add(item: MediaItem) {
        items.removeAll { it.id == item.id }
        items.add(item)
        save()
    }

    fun getAll(): List<MediaItem> = items.toList()

    fun getByType(type: MediaType): List<MediaItem> =
        items.filter { it.type == type }

    fun delete(id: String) {
        items.removeAll { it.id == id }
        save()
    }

    fun find(id: String): MediaItem? =
        items.find { it.id == id }
}
