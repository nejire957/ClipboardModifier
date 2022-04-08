package com.example.clipboardmodifier

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clipboardmodifier.databinding.ActivityMainBinding
import android.content.ClipboardManager
import android.content.ClipData
import com.google.android.material.snackbar.Snackbar
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener(MyOnPrimaryClipChangedListener(clipboard))

        binding.button.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clippedText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
            val modifiedText = clippedText.replace(Regex("\r\n|\n"), "_")
            binding.plainText.setText(modifiedText)
        }

        binding.button2.setOnClickListener{
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clippedText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
            val modifiedText = clippedText.replace(Regex("\r\n|\n"), " ")
            val clipItem = ClipData.newPlainText("modified text", modifiedText)
            clipboard?.setPrimaryClip(clipItem)
        }
    }
}

class MyOnPrimaryClipChangedListener(val clipboard: ClipboardManager): ClipboardManager.OnPrimaryClipChangedListener{
    private var modifiedText = ""
    override fun onPrimaryClipChanged() {
        val clippedText = clipboard.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
        if(clippedText != modifiedText) {
            modifiedText = clippedText.replace(Regex("\r\n|\n"), " ")
            val clipItem = ClipData.newPlainText("modified text", modifiedText)
            clipboard?.setPrimaryClip(clipItem)
        }
    }
}