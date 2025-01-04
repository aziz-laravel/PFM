package ma.ensaj.pets

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val backArrow = findViewById<ImageView>(R.id.imageView6)
        backArrow.setOnClickListener {
            onBackPressed()
        }
    }
}