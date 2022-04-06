package com.example.clipboardmodifier

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clipboardmodifier.databinding.ActivityMainBinding
import android.content.ClipboardManager
import android.content.ClipData

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        val clipboardManager = applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.addPrimaryClipChangedListener {
            val item = clipboardManager.primaryClip?.getItemAt(0)
            if(item != null){
                val text = item.coerceToText(applicationContext).toString()
                clipboardManager.setPrimaryClip(ClipData.newPlainText("", text + "a"))
            }
        }
        */


        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.button.setOnClickListener {
            binding.label.text = "Clicked"
        }
    }
}