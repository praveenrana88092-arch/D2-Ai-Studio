package com.d2aistudio.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 30, 24, 30)
        layout.setBackgroundColor(Color.rgb(12, 15, 25))

        scroll.addView(layout)

        val title = TextView(this)
        title.text = "D2 AI STUDIO"
        title.textSize = 28f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER
        layout.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "Your Personal AI Video Creator"
        subtitle.textSize = 14f
        subtitle.gravity = Gravity.CENTER
        subtitle.setTextColor(Color.LTGRAY)
        layout.addView(subtitle)

        val heading = TextView(this)
        heading.text = "\nWhat do you want to create?"
        heading.textSize = 21f
        heading.setTextColor(Color.WHITE)
        layout.addView(heading)

        val features = listOf(
            "AI Agent",
            "Text to Video",
            "Image to Video",
            "AI Image Generator",
            "Story to Video",
            "Voiceover Studio",
            "Video Editor"
        )

        for (feature in features) {
            val button = Button(this)
            button.text = feature
            button.textSize = 16f
            button.setOnClickListener {
                Toast.makeText(
                    this,
                    "$feature - Coming Soon",
                    Toast.LENGTH_SHORT
                ).show()
            }
            layout.addView(button)
        }

        setContentView(scroll)
    }
}
