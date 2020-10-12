package uk.co.paulcowie.imagecompressorfordiscord.activity

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uk.co.paulcowie.imagecompressorfordiscord.R
import uk.co.paulcowie.imagecompressorfordiscord.service.CompressionService
import java.io.FileOutputStream
import java.nio.file.Files


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(intent.action == ACTION_SEND){
            Log.i(javaClass.name,"Got send intent")
            Log.i(javaClass.name, "Type was ${intent.type}")

            val receivedUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)

            Log.i(javaClass.name, "$receivedUri")


            lifecycleScope.launch {
                val compressedImageFile = CompressionService.compress(this@MainActivity, receivedUri!!)
                CompressionService.startDiscordActivityForImage(this@MainActivity, compressedImageFile)
            }
        }

    }
}
