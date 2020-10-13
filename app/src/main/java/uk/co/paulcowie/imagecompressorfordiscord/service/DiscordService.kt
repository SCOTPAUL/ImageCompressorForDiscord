package uk.co.paulcowie.imagecompressorfordiscord.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

object DiscordService {
    private const val DISCORD_PACKAGE = "com.discord"

    suspend fun compress(context: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri)).let { if(it == "") "tmp" else it }

        val file = context.contentResolver.openInputStream(uri).use { inputStream ->
            inputStream?.let {
                val file = Files.createTempFile("IMG", ".$fileExtension").toFile()

                FileOutputStream(file).use { it.write(inputStream.readBytes()) }

                file
            }
        }

        file?.let {
            Compressor.compress(context, it) {
                size((7.5 * 1024 * 1024).toLong())
            }
        }
    }

    fun isDiscordInstalled(context: Context): Boolean {
        return context.packageManager.getInstalledApplications(0).find { info -> info.packageName == DISCORD_PACKAGE } != null
    }

    fun startDiscordActivityForImage(context: Context, imageFile: File) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.setPackage(DISCORD_PACKAGE)
        sharingIntent.type = "image/*"

        val compressedFileUri =
            FileProvider.getUriForFile(context, "uk.co.paulcowie.fileprovider", imageFile)

        sharingIntent.putExtra(Intent.EXTRA_STREAM, compressedFileUri)
        context.startActivity(sharingIntent)
    }

}