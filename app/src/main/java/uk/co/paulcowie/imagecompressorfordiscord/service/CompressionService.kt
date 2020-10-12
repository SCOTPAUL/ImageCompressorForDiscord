package uk.co.paulcowie.imagecompressorfordiscord.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.co.paulcowie.imagecompressorfordiscord.util.FileUtils
import java.io.File

object CompressionService {

    suspend fun compress(context: Context, uri: Uri): File = withContext(Dispatchers.IO) {
        val compressedImage = Compressor.compress(context, FileUtils.from(context, uri)) {
            size((7.5 * 1024 * 1024).toLong())
        }

        Log.i(
            javaClass.name,
            "Size of this lad ${compressedImage.length()}, location ${compressedImage.absolutePath}"
        )

        return@withContext compressedImage
    }

    fun startDiscordActivityForImage(context: Context, imageFile: File) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.setPackage("com.discord")
        sharingIntent.type = "image/*"

        val compressedFileUri =
            FileProvider.getUriForFile(context, "uk.co.paulcowie.fileprovider", imageFile)

        sharingIntent.putExtra(Intent.EXTRA_STREAM, compressedFileUri)
        context.startActivity(sharingIntent)
    }

}