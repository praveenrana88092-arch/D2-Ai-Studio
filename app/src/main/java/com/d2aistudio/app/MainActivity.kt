package com.d2aistudio.app
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.view.ViewGroup

class MainActivity : Activity() {
private val MEDIA_REQUEST = 501

private fun openMediaPicker() {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
        putExtra(
            Intent.EXTRA_MIME_TYPES,
            arrayOf("image/*", "video/*", "audio/*")
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
    }
    startActivityForResult(intent, MEDIA_REQUEST)
}
    private lateinit var root: LinearLayout
    private val bg = Color.rgb(13, 17, 30)
    private val card = Color.rgb(28, 35, 53)
    private val accent = Color.rgb(105, 94, 255)

    private var currentStory = ""
    private var currentScenes = emptyList<Scene>()

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    ProjectManager.init(this)
    CharacterManager.init(this)
    MediaLibrary.init(this)

    dashboard()
    }
    private fun setup(title: String) {
        val scroll = ScrollView(this)
        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(22, 25, 22, 25)
        root.setBackgroundColor(bg)
        scroll.addView(root)
        setContentView(scroll)

        text("D2 AI STUDIO", 25, Color.WHITE, true)
        text(title, 17, Color.LTGRAY)
    }

    private fun text(
        value: String,
        size: Int = 16,
        color: Int = Color.WHITE,
        bold: Boolean = false
    ) {
        val t = TextView(this)
        t.text = value
        t.textSize = size.toFloat()
        t.setTextColor(color)
        if (bold) t.setTypeface(null, 1)
        t.setPadding(0, 12, 0, 12)
        root.addView(t)
    }

    private fun button(label: String, action: () -> Unit) {
        val b = Button(this)
        b.text = label
        b.setTextColor(Color.WHITE)
        b.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(accent)
        )
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 6, 0, 6)
        root.addView(b, params)
        b.setOnClickListener { action() }
    }

    private fun input(hint: String): EditText {
        val e = EditText(this)
        e.hint = hint
        e.setTextColor(Color.WHITE)
        e.setHintTextColor(Color.LTGRAY)
        e.setPadding(14, 12, 14, 12)
        e.setBackgroundColor(card)
        root.addView(e)
        return e
    }

    private fun dashboard() {
        setup("Your Private AI Creation Studio")

        text("Create something amazing", 23, Color.WHITE, true)
        text("Your personal creative workspace", 14, Color.LTGRAY)

        button("AI AGENT") { agent() }
        button("STORY TO VIDEO") { storyPlanner() }
        button("CHARACTER STUDIO") { characterStudio() }
        button("IMAGE PROMPT STUDIO") { imageStudio() }
        button("VIDEO PROMPT STUDIO") { videoStudio() }
        button("VOICEOVER STUDIO") { voiceStudio() }
        button("MY PROJECTS") { projects() }
        button("MEDIA LIBRARY") { media() }

        text("D2 AI Studio | Private Workspace", 12, Color.LTGRAY)
    }

    private fun agent() {
        setup("AI Agent")

        text("What do you want to create?", 21, Color.WHITE, true)

        val idea = input("Describe your video idea...")

        button("Plan My Story") {
            val value = idea.text.toString().trim()
            if (value.isEmpty()) {
                idea.error = "Enter your idea"
            } else {
                currentStory = value
                currentScenes = StoryPlanner.createScenes(value)
                showScenes()
            }
        }

        button("Back to Dashboard") { dashboard() }
    }

    private fun storyPlanner() {
        setup("Story to Video")

        text("Enter your complete story", 20, Color.WHITE, true)

        val story = input("Write your story here...")

        button("Generate Scenes") {
            val value = story.text.toString().trim()

            if (value.isEmpty()) {
                story.error = "Please enter a story"
            } else {
                currentStory = value
                currentScenes = StoryPlanner.createScenes(value)
                showScenes()
            }
        }

        button("Back") { dashboard() }
    }

    private fun showScenes() {
        setup("Your Story Scenes")

        text("Total Scenes: ${currentScenes.size}", 20, Color.WHITE, true)

        currentScenes.forEach { scene ->
            text(
                "${scene.title}\n${scene.narration}",
                16, Color.WHITE, true
            )

            text("IMAGE PROMPT", 14, Color.YELLOW, true)
            text(scene.imagePrompt, 13, Color.LTGRAY)

            text("VIDEO PROMPT", 14, Color.YELLOW, true)
            text(scene.videoPrompt, 13, Color.LTGRAY)
        }

        button("Create Voiceover Script") {
            voiceResult(StoryPlanner.createVoiceover(currentScenes))
        }

        button("Save Project") {
            val project = ProjectManager.create(
                "My Story ${System.currentTimeMillis()}",
                currentStory,
                "Story to Video"
            )
            Toast.makeText(
                this,
                "Project saved: ${project.name}",
                Toast.LENGTH_LONG
            ).show()
        }

        button("Back to Dashboard") { dashboard() }
    }

    private fun characterStudio() {
        setup("Character Studio")

        val name = input("Character name")
        val age = input("Character age")
        val appearance = input("Face and appearance")
        val clothes = input("Clothing")
        val personality = input("Personality")

        button("Save Character") {
            val n = name.text.toString().trim()

            if (n.isEmpty()) {
                name.error = "Enter character name"
            } else {
                val profile = CharacterProfile(
                    n,
                    age.text.toString(),
                    appearance.text.toString(),
                    clothes.text.toString(),
                    personality.text.toString()
                )

                CharacterManager.add(profile)

                Toast.makeText(
                    this,
                    "Character profile saved",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        button("View Characters") {
            setup("Saved Characters")
            CharacterManager.getAll().forEach {
                text(it.createCharacterPrompt(), 14, Color.WHITE)
            }
            button("Back") { characterStudio() }
        }

        button("Back") { dashboard() }
    }

    private fun imageStudio() {
        setup("Image Prompt Studio")

        val subject = input("Describe the image")
        val style = input("Style: 3D, realistic, cartoon...")
        val ratio = input("Aspect ratio: 9:16, 16:9, 1:1")

        button("Create Image Prompt") {
            val result = PromptStudio.createImagePrompt(
                subject.text.toString(),
                style.text.toString(),
                ratio.text.toString()
            )
            promptResult("Image Prompt", result)
        }

        button("Back") { dashboard() }
    }

    private fun videoStudio() {
        setup("Video Prompt Studio")

        val subject = input("Describe the video scene")
        val camera = input("Camera movement")
        val duration = input("Video duration")

        button("Create Video Prompt") {
            val result = PromptStudio.createVideoPrompt(
                subject.text.toString(),
                camera.text.toString(),
                duration.text.toString()
            )
            promptResult("Video Prompt", result)
        }

        button("Back") { dashboard() }
    }

    private fun voiceStudio() {
        setup("Voiceover Studio")

        val script = input("Enter narration script")
        val language = input("Language: Hindi / English")
        val mood = input("Mood: Cinematic / Emotional / Normal")

        button("Prepare Voice Prompt") {
            val result = PromptStudio.createVoicePrompt(
                script.text.toString(),
                language.text.toString(),
                mood.text.toString()
            )
            voiceResult(result)
        }

        button("Back") { dashboard() }
    }

    private fun voiceResult(value: String) {
        promptResult("Voiceover Script", value)
    }

    private fun promptResult(title: String, value: String) {
        setup(title)

        val output = TextView(this)
        output.text = value
        output.textSize = 14f
        output.setTextColor(Color.WHITE)
        output.setPadding(12, 15, 12, 15)
        output.setBackgroundColor(card)
        root.addView(output)

        button("Copy Text") {
            val clipboard = getSystemService(CLIPBOARD_SERVICE)
                    as android.content.ClipboardManager
            clipboard.setPrimaryClip(
                android.content.ClipData.newPlainText("D2 Prompt", value)
            )
            Toast.makeText(
                this,
                "Copied",
                Toast.LENGTH_SHORT
            ).show()
        }

        button("Back") { dashboard() }
    }

    private fun projects() {
        setup("My Projects")

        val all = ProjectManager.getAll()

        if (all.isEmpty()) {
            text("No projects saved yet.", 15, Color.LTGRAY)
        }

        all.forEach { project ->
            text(project.name, 18, Color.WHITE, true)
            text(project.description, 14, Color.LTGRAY)
            text("Type: ${project.type}", 13, Color.YELLOW)
            button("Delete Project") {
                ProjectManager.delete(project.id)
                projects()
            }
        }

        button("Back") { dashboard() }
    }

    private fun media() {
        setup("Media Library")

        val items = MediaLibrary.getAll()

        if (items.isEmpty()) {
            text("No media added yet.", 15, Color.LTGRAY)
        }

        items.forEach {
            text("${it.name} - ${it.type}", 16, Color.WHITE)
            text(it.path, 12, Color.LTGRAY)
        }

        button("Back") { dashboard() }
    }
}
