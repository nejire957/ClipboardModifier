package com.example.clipboardmodifier

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.clipboardmodifier.databinding.ActivityMainBinding
import android.provider.Settings
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.button.setOnClickListener{
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clippedText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
            val modifiedText = clippedText.replace(Regex("\r\n|\n"), " ")
            val clipItem = ClipData.newPlainText("modified text", modifiedText)
            clipboard?.setPrimaryClip(clipItem)
            binding.textView.text = modifiedText
        }

        //val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //clipboard.addPrimaryClipChangedListener(MyOnPrimaryClipChangedListener(clipboard))
    }

    var isOverlayOn = false
    private fun startOverlay(){
        isOverlayOn = true
        OverlayService.start(this@MainActivity)
    }

    private fun stopOverlay(){
        OverlayService.stop(this@MainActivity)
        isOverlayOn = false
    }

    companion object {
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }
    private fun requestOverlayPermission() {
        if (isOverlayGranted()) return
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        val startForResult =
            registerForActivityResult(StartActivityForResult()) { result: ActivityResult? ->
                if (result?.resultCode == OVERLAY_PERMISSION_REQUEST_CODE) {
                    if (!isOverlayGranted()) {
                        finish()  // Cannot continue if not granted
                    }
                }
            }
        startForResult.launch(intent)
    }

    private fun isOverlayGranted(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)
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