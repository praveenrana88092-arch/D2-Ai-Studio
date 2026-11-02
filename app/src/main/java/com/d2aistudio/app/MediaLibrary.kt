
package com.d2aistudio.app

enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO
}

data class MediaItem(
    val id: String,
    val name: String,
    val path: String,
    val type: MediaType,
    val createdAt: Long = System.currentTimeMillis()
)

object MediaLibrary {

    private val items = mutableListOf<MediaItem>()

    fun add(item: MediaItem) {
        items.removeAll { it.id == item.id }
        items.add(item)
    }

    fun getAll(): List<MediaItem> {
        return items.toList()
    }

    fun getByType(type: MediaType): List<MediaItem> {
        return items.filter { it.type == type }
    }

    fun delete(id: String) {
        items.removeAll { it.id == id }
    }

    fun find(id: String): MediaItem? {
        return items.find { it.id == id }
    }
}
