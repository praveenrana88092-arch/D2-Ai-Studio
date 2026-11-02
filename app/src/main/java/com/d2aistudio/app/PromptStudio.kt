
package com.d2aistudio.app

object PromptStudio {

    fun createImagePrompt(
        subject: String,
        style: String,
        ratio: String
    ): String {
        return """
            Create a high-quality image.
            Subject: $subject
            Visual style: $style
            Aspect ratio: $ratio
            Detailed environment,
            cinematic lighting,
            professional composition,
            consistent characters,
            no text, no watermark.
        """.trimIndent()
    }

    fun createVideoPrompt(
        subject: String,
        camera: String,
        duration: String
    ): String {
        return """
            Create a cinematic video.
            Scene: $subject
            Camera movement: $camera
            Duration: $duration
            Natural movement,
            smooth transitions,
            detailed environment,
            professional lighting.
            No text or watermark.
        """.trimIndent()
    }

    fun createVoicePrompt(
        script: String,
        language: String,
        mood: String
    ): String {
        return """
            Voiceover language: $language
            Voice mood: $mood
            Script: $script
            Clear pronunciation,
            natural pacing,
            expressive narration.
        """.trimIndent()
    }
}
