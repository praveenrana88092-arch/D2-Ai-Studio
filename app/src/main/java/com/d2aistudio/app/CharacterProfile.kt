package com.d2aistudio.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class CharacterProfile(
    val name: String,
    val age: String,
    val appearance: String,
    val clothes: String,
    val personality: String
) {
    fun createCharacterPrompt(): String {
        return """
            CHARACTER IDENTITY:
            Name: $name
            Age: $age
            Appearance: $appearance
            Clothes: $clothes
            Personality: $personality

            CONSISTENCY RULE:
            Keep the same face, hairstyle,
            clothing, age and appearance
            in every scene.
            Do not redesign the character.
        """.trimIndent()
    }
}

object CharacterManager {
    private lateinit var prefs: android.content.SharedPreferences
    private val characters = mutableListOf<CharacterProfile>()

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(
            "d2_characters", Context.MODE_PRIVATE
        )
        load()
    }

    private fun load() {
        characters.clear()
        val data = prefs.getString("characters", "[]") ?: "[]"
        val array = JSONArray(data)

        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            characters.add(
                CharacterProfile(
                    item.getString("name"),
                    item.getString("age"),
                    item.getString("appearance"),
                    item.getString("clothes"),
                    item.getString("personality")
                )
            )
        }
    }

    private fun save() {
        val array = JSONArray()
        characters.forEach {
            array.put(JSONObject().apply {
                put("name", it.name)
                put("age", it.age)
                put("appearance", it.appearance)
                put("clothes", it.clothes)
                put("personality", it.personality)
            })
        }
        prefs.edit().putString("characters", array.toString()).apply()
    }

    fun add(profile: CharacterProfile) {
        characters.removeAll { it.name == profile.name }
        characters.add(profile)
        save()
    }

    fun getAll(): List<CharacterProfile> = characters.toList()

    fun find(name: String): CharacterProfile? =
        characters.find { it.name == name }
}
