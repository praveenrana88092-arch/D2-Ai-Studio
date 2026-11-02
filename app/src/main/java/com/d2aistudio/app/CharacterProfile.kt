
package com.d2aistudio.app

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

    private val characters = mutableListOf<CharacterProfile>()

    fun add(profile: CharacterProfile) {
        characters.removeAll { it.name == profile.name }
        characters.add(profile)
    }

    fun getAll(): List<CharacterProfile> {
        return characters.toList()
    }

    fun find(name: String): CharacterProfile? {
        return characters.find { it.name == name }
    }
}
