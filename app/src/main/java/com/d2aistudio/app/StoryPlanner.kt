
package com.d2aistudio.app

data class Scene(
    val number: Int,
    val title: String,
    val narration: String,
    val imagePrompt: String,
    val videoPrompt: String
)

object StoryPlanner {

    fun createScenes(
        story: String,
        language: String = "Hindi"
    ): List<Scene> {

        val paragraphs = story
            .split(Regex("[.!?।\\n]+"))
            .map { it.trim() }
            .filter { it.length > 5 }

        return paragraphs.mapIndexed { index, line ->

            val number = index + 1

            Scene(
                number = number,
                title = "Scene $number",
                narration = line,
                imagePrompt = """
                    Cinematic 3D realistic scene.
                    Story: $line
                    Consistent characters,
                    detailed environment,
                    professional lighting,
                    vertical 9:16 composition,
                    no text, no watermark.
                """.trimIndent(),

                videoPrompt = """
                    Create a cinematic video scene.
                    Action: $line
                    Natural character movement,
                    smooth camera movement,
                    realistic lighting,
                    vertical 9:16 format.
                """.trimIndent()
            )
        }
    }

    fun createVoiceover(
        scenes: List<Scene>
    ): String {
        return scenes.joinToString("\n\n") {
            "${it.title}\n${it.narration}"
        }
    }
}
