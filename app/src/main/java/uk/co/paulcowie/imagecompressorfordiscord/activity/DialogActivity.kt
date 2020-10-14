package uk.co.paulcowie.imagecompressorfordiscord.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uk.co.paulcowie.imagecompressorfordiscord.R
import uk.co.paulcowie.imagecompressorfordiscord.service.DiscordService

class DialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        if(intent.action == Intent.ACTION_SEND){
            Log.i(javaClass.name,"Got send intent")
            Log.i(javaClass.name, "Type was ${intent.type}")

            val receivedUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)

            Log.i(javaClass.name, "$receivedUri")


            lifecycleScope.launch {
                val compressedImageFile = DiscordService.compress(this@DialogActivity, receivedUri!!)

                compressedImageFile?.let {
                    if(DiscordService.isDiscordInstalled(this@DialogActivity)) {
                        DiscordService.startDiscordActivityForImage(this@DialogActivity, compressedImageFile)
                    }
                }

                this@DialogActivity.finish()
            }
        }
    }
}
