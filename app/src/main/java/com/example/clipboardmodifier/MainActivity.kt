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
    companion object {
        /** ID for the runtime permission dialog */
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        requestOverlayPermission()

        binding.button.setOnClickListener {
            if(isOverlayOn) startOverlay()
            else stopOverlay()
        }

        binding.button2.setOnClickListener{
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clippedText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
            val modifiedText = clippedText.replace(Regex("\r\n|\n"), " ")
            val clipItem = ClipData.newPlainText("modified text", modifiedText)
            clipboard?.setPrimaryClip(clipItem)
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

    /** Requests an overlay permission to the user if needed. */
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

    /** Checks if the overlay is permitted. */
    private fun isOverlayGranted() =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)

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