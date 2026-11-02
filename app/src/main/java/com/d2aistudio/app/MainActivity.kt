package com.d2aistudio.app

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private val mediaRequest = 501
    private lateinit var root: LinearLayout
    private val bg = Color.rgb(13, 17, 30)
    private val card = Color.rgb(28, 35, 53)
    private val accent = Color.rgb(105, 94, 255)
    private var currentStory = ""
    private var currentScenes: List<Scene> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProjectManager.init(this)
        CharacterManager.init(this)
        MediaLibrary.init(this)
        dashboard()
    }

    private fun setup(title: String) {
        val scroll = ScrollView(this)
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 24, 22, 28)
            setBackgroundColor(bg)
        }
        scroll.addView(root)
        setContentView(scroll)
        text("D2 AI STUDIO", 25, Color.WHITE, true)
        text(title, 17, Color.LTGRAY)
    }

    private fun text(value: String, size: Int = 16, color: Int = Color.WHITE, bold: Boolean = false) {
        val view = TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 9, 0, 9)
        }
        root.addView(view)
    }

    private fun button(label: String, action: () -> Unit) {
        val view = Button(this).apply {
            text = label
            setTextColor(Color.WHITE)
            backgroundTintList = ColorStateList.valueOf(accent)
            isAllCaps = false
            setOnClickListener { action() }
        }
        root.addView(view, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 5, 0, 5) })
    }

    private fun input(hint: String, multiline: Boolean = false): EditText {
        val view = EditText(this).apply {
            this.hint = hint
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
            setPadding(14, 12, 14, 12)
            setBackgroundColor(card)
            if (multiline) {
                minLines = 4
                gravity = android.view.Gravity.TOP
                inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            }
        }
        root.addView(view, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 5, 0, 9) })
        return view
    }

    private fun dashboard() {
        setup("Your Private AI Creation Studio")
        text("Create something amazing", 23, Color.WHITE, true)
        text("Free tools for stories, prompts and media organization.", 14, Color.LTGRAY)
        button("AI AGENT") { agent() }
        button("STORY TO VIDEO") { storyPlanner() }
        button("CHARACTER STUDIO") { characterStudio() }
        button("IMAGE PROMPT STUDIO") { imageStudio() }
        button("VIDEO PROMPT STUDIO") { videoStudio() }
        button("VOICEOVER STUDIO") { voiceStudio() }
        button("MY PROJECTS") { projects() }
        button("MEDIA LIBRARY") { media() }
        text("Prompts and projects are stored on this device.", 12, Color.LTGRAY)
    }

    private fun agent() {
        setup("AI Agent")
        text("Describe your idea. The app will turn it into scenes and prompts.", 15, Color.LTGRAY)
        val idea = input("Example: A mysterious village adventure...", true)
        button("Create Story Plan") {
            val value = idea.text.toString().trim()
            if (value.isBlank()) idea.error = "Please enter an idea"
            else {
                currentStory = value
                currentScenes = StoryPlanner.createScenes(value)
                showScenes()
            }
        }
        button("Back to Dashboard") { dashboard() }
    }

    private fun storyPlanner() {
        setup("Story to Video")
        val story = input("Paste or write your complete story...", true)
        button("Generate Scenes") {
            val value = story.text.toString().trim()
            if (value.isBlank()) story.error = "Please enter a story"
            else {
                currentStory = value
                currentScenes = StoryPlanner.createScenes(value)
                showScenes()
            }
        }
        button("Back to Dashboard") { dashboard() }
    }

    private fun showScenes() {
        setup("Story Scenes")
        text("Scenes created: ${currentScenes.size}", 18, Color.WHITE, true)
        currentScenes.forEach { scene ->
            text("${scene.title}\n${scene.narration}", 16, Color.WHITE, true)
            text("IMAGE PROMPT", 14, Color.YELLOW, true)
            text(scene.imagePrompt, 13, Color.LTGRAY)
            text("VIDEO PROMPT", 14, Color.YELLOW, true)
            text(scene.videoPrompt, 13, Color.LTGRAY)
            button("Copy Scene ${scene.number} Prompts") {
                copyText("${scene.imagePrompt}\n\n${scene.videoPrompt}")
            }
        }
        button("Copy All Voiceover") { copyText(StoryPlanner.createVoiceover(currentScenes)) }
        button("Save Project") {
            val saved = ProjectManager.create(
                "Story ${System.currentTimeMillis()}",
                currentStory,
                "Story to Video"
            )
            Toast.makeText(this, "Project saved: ${saved.name}", Toast.LENGTH_SHORT).show()
        }
        button("Back to Dashboard") { dashboard() }
    }

    private fun characterStudio() {
        setup("Character Studio")
        text("Create a repeatable character identity prompt.", 14, Color.LTGRAY)
        val name = input("Character name")
        val age = input("Character age")
        val appearance = input("Face, hair and appearance", true)
        val clothes = input("Clothing")
        val personality = input("Personality")
        button("Save Character") {
            val n = name.text.toString().trim()
            if (n.isBlank()) name.error = "Enter a character name"
            else {
                CharacterManager.add(CharacterProfile(
                    n, age.text.toString(), appearance.text.toString(),
                    clothes.text.toString(), personality.text.toString()
                ))
                Toast.makeText(this, "Character saved", Toast.LENGTH_SHORT).show()
            }
        }
        button("View Saved Characters") { savedCharacters() }
        button("Back to Dashboard") { dashboard() }
    }

    private fun savedCharacters() {
        setup("Saved Characters")
        val all = CharacterManager.getAll()
        if (all.isEmpty()) text("No characters saved yet.", 15, Color.LTGRAY)
        all.forEach { character ->
            text(character.name, 18, Color.WHITE, true)
            text(character.createCharacterPrompt(), 13, Color.LTGRAY)
            button("Copy Consistency Prompt") { copyText(character.createCharacterPrompt()) }
            button("Delete Character") {
                CharacterManager.delete(character.name)
                Toast.makeText(this, "Character deleted", Toast.LENGTH_SHORT).show()
                savedCharacters()
            }
        }
        button("Back") { characterStudio() }
    }

    private fun imageStudio() {
        setup("Image Prompt Studio")
        val subject = input("Image subject or scene", true)
        val style = input("Style: cinematic, 3D, realistic, cartoon...")
        val ratio = input("Aspect ratio: 9:16, 16:9, 1:1")
        button("Generate Image Prompt") {
            val s = subject.text.toString().trim()
            if (s.isBlank()) subject.error = "Describe the image"
            else promptResult("Image Prompt", PromptStudio.createImagePrompt(
                s, style.text.toString(), ratio.text.toString()
            ))
        }
        button("Back to Dashboard") { dashboard() }
    }

    private fun videoStudio() {
        setup("Video Prompt Studio")
        val subject = input("Describe the video scene", true)
        val camera = input("Camera movement")
        val duration = input("Duration, e.g. 5 seconds")
        button("Generate Video Prompt") {
            val s = subject.text.toString().trim()
            if (s.isBlank()) subject.error = "Describe the video"
            else promptResult("Video Prompt", PromptStudio.createVideoPrompt(
                s, camera.text.toString(), duration.text.toString()
            ))
        }
        button("Back to Dashboard") { dashboard() }
    }

    private fun voiceStudio() {
        setup("Voiceover Studio")
        val script = input("Enter narration script", true)
        val language = input("Language: Hindi / English / Tamil")
        val mood = input("Mood: cinematic / emotional / friendly")
        button("Prepare Voiceover Prompt") {
            val s = script.text.toString().trim()
            if (s.isBlank()) script.error = "Enter a script"
            else promptResult("Voiceover Prompt", PromptStudio.createVoicePrompt(
                s, language.text.toString(), mood.text.toString()
            ))
        }
        button("Back to Dashboard") { dashboard() }
    }

    private fun promptResult(title: String, value: String) {
        setup(title)
        text(value, 14, Color.WHITE)
        button("Copy Text") { copyText(value) }
        button("Back to Dashboard") { dashboard() }
    }

    private fun copyText(value: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("D2 AI Studio", value))
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun projects() {
        setup("My Projects")
        val all = ProjectManager.getAll()
        if (all.isEmpty()) text("No saved projects yet.", 15, Color.LTGRAY)
        all.forEach { project ->
            text(project.name, 18, Color.WHITE, true)
            text("Type: ${project.type}\n${project.description}", 14, Color.LTGRAY)
            button("Open Project") {
                currentStory = project.description
                currentScenes = StoryPlanner.createScenes(project.description)
                showScenes()
            }
            button("Delete Project") {
                ProjectManager.delete(project.id)
                projects()
            }
        }
        button("Back to Dashboard") { dashboard() }
    }

    private fun openMediaPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*", "audio/*"))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, mediaRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != mediaRequest || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Exception) { }
        val mime = contentResolver.getType(uri) ?: ""
        val type = when {
            mime.startsWith("image/") -> MediaType.IMAGE
            mime.startsWith("video/") -> MediaType.VIDEO
            mime.startsWith("audio/") -> MediaType.AUDIO
            else -> {
                Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show()
                return
            }
        }
        var name = "Imported Media"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) name = cursor.getString(index) ?: name
            }
        }
        MediaLibrary.add(MediaItem(uri.toString(), name, uri.toString(), type))
        Toast.makeText(this, "Media added", Toast.LENGTH_SHORT).show()
        media()
    }

    private fun openMedia(item: MediaItem) {
        try {
            val uri = Uri.parse(item.path)
            val mime = contentResolver.getType(uri) ?: when (item.type) {
                MediaType.IMAGE -> "image/*"
                MediaType.VIDEO -> "video/*"
                MediaType.AUDIO -> "audio/*"
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Open media with"))
        } catch (_: Exception) {
            Toast.makeText(this, "No compatible app found to open this file", Toast.LENGTH_LONG).show()
        }
    }

    private fun media() {
        setup("Media Library")
        button("Import Photo / Video / Audio") { openMediaPicker() }
        val items = MediaLibrary.getAll()
        text("Saved files: ${items.size}", 15, Color.LTGRAY)
        if (items.isEmpty()) text("No media added yet.", 15, Color.LTGRAY)
        items.forEach { item ->
            text(item.name, 17, Color.WHITE, true)
            text("Type: ${item.type}", 13, Color.LTGRAY)
            button("Open / Play") { openMedia(item) }
            button("Remove from Library") {
                MediaLibrary.delete(item.id)
                try {
                    contentResolver.releasePersistableUriPermission(
                        Uri.parse(item.path), Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) { }
                media()
            }
        }
        text("Removing a file here does not delete the original from your phone.", 12, Color.LTGRAY)
        button("Back to Dashboard") { dashboard() }
    }
}
