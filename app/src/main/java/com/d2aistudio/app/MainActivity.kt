
package com.d2aistudio.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.text.InputType

class MainActivity : Activity() {

    private lateinit var root: LinearLayout
    private val bg = Color.rgb(13, 17, 30)
    private val panel = Color.rgb(28, 35, 53)
    private val accent = Color.rgb(105, 94, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDashboard()
    }

    private fun setup(title: String) {
        val scroll = ScrollView(this)
        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(22, 28, 22, 28)
        root.setBackgroundColor(bg)
        scroll.addView(root)
        setContentView(scroll)

        addText("D2 AI STUDIO", 25, Color.WHITE, true)
        addText(title, 17, Color.LTGRAY)
    }

    private fun addText(
        value: String,
        size: Int,
        color: Int,
        bold: Boolean = false
    ) {
        val t = TextView(this)
        t.text = value
        t.textSize = size.toFloat()
        t.setTextColor(color)
        if (bold) t.setTypeface(null, 1)
        t.setPadding(0, 14, 0, 14)
        root.addView(t)
    }

    private fun addButton(label: String, action: () -> Unit) {
        val b = Button(this)
        b.text = label
        b.setTextColor(Color.WHITE)
        b.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(accent)
        )
        val p = LinearLayout.LayoutParams(
            -1, -2
        )
        p.setMargins(0, 8, 0, 8)
        root.addView(b, p)
        b.setOnClickListener { action() }
    }

    private fun addInput(hint: String): EditText {
        val e = EditText(this)
        e.hint = hint
        e.setHintTextColor(Color.LTGRAY)
        e.setTextColor(Color.WHITE)
        e.setPadding(14, 12, 14, 12)
        e.setBackgroundColor(panel)
        root.addView(e)
        return e
    }

    private fun showDashboard() {
        setup("Your Personal AI Creator")
        addText("Create anything with AI", 22, Color.WHITE, true)
        addText("Choose your creative studio", 15, Color.LTGRAY)

        addButton("🤖 AI AGENT") { showAgent() }
        addButton("🎬 TEXT TO VIDEO") { showGenerator("Text to Video") }
        addButton("🖼 IMAGE TO VIDEO") { showGenerator("Image to Video") }
        addButton("📖 STORY TO VIDEO") { showGenerator("Story to Video") }
        addButton("🎨 AI IMAGE GENERATOR") { showGenerator("AI Image Generator") }
        addButton("🎙 VOICEOVER STUDIO") { showGenerator("Voiceover Studio") }
        addButton("✂ VIDEO EDITOR") { showGenerator("Video Editor") }

        addText("D2 AI Studio • Private Creator Workspace", 12, Color.LTGRAY)
    }

    private fun showAgent() {
        setup("AI Agent")
        addText("What would you like to create?", 21, Color.WHITE, true)

        val prompt = addInput("Describe your video or story...")
        addButton("Create Plan") {
            val value = prompt.text.toString().trim()
            if (value.isEmpty()) {
                prompt.error = "Please enter your idea"
            } else {
                showPlan(value)
            }
        }

        addButton("← Back to Dashboard") { showDashboard() }
    }

    private fun showPlan(value: String) {
        setup("Your Creation Plan")
        addText("Your idea:", 18, Color.WHITE, true)
        addText(value, 16, Color.LTGRAY)
        addText("Suggested workflow", 20, Color.WHITE, true)
        addText("1. Prepare the story", 16, Color.LTGRAY)
        addText("2. Plan the scenes", 16, Color.LTGRAY)
        addText("3. Create images for scenes", 16, Color.LTGRAY)
        addText("4. Add voiceover", 16, Color.LTGRAY)
        addText("5. Edit and export your video", 16, Color.LTGRAY)
        addText("This is a local planning preview. AI generation is not connected yet.", 14, Color.YELLOW)
        addButton("← Back") { showAgent() }
    }

    private fun showGenerator(type: String) {
        setup(type)
        addText("Create with $type", 21, Color.WHITE, true)

        val prompt = addInput("Enter your prompt or idea...")

        addButton("Prepare Project") {
            val value = prompt.text.toString().trim()
            if (value.isEmpty()) {
                prompt.error = "Enter your idea first"
            } else {
                showPlan(value)
            }
        }

        addText(
            "Your project can be planned here. A free AI model must be connected for actual image or video generation.",
            14, Color.YELLOW
        )

        addButton("← Back to Dashboard") { showDashboard() }
    }
}
