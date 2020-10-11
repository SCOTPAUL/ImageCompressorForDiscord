package uk.co.paulcowie.imagecompressorfordiscord

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
                withContext(Dispatchers.IO) {
                    val compressedImage = Compressor.compress(this@MainActivity, FileUtils.from(this@MainActivity, receivedUri!!)) {
                        size((7.5 * 1024 * 1024).toLong())
                    }

                    Log.i(javaClass.name,"Size of this lad ${compressedImage.length()}, location ${compressedImage.absolutePath}, cache at $cacheDir")

                    val sharingIntent = Intent(ACTION_SEND)
                    sharingIntent.setPackage("com.discord")
                    sharingIntent.type = "image/*"

                    val uri = FileProvider.getUriForFile(this@MainActivity, "uk.co.paulcowie.fileprovider", compressedImage)



                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(sharingIntent)
                }
            }
        }

        lifecycleScope.launch {
            Log.i(javaClass.name, "${packageManager.getPackageInfo("com.discord", PackageManager.GET_ACTIVITIES).activities.forEach { Log.i("Activity", it.name) }}")
        }


    }
}
