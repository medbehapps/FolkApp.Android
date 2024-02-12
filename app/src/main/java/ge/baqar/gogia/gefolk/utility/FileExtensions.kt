package ge.baqar.gogia.gefolk.utility

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.net.toFile
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class FileExtensions(private val context: Context) {

    @SuppressLint("Recycle")
    fun read(uri: Uri?): ByteArray? {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri?.let {
                    val fileDescriptor =
                        context.contentResolver.openFile(uri, "r", null)?.fileDescriptor
                    return FileInputStream(fileDescriptor!!).readBytes()
                }
            } else {
                uri?.let {
                    try {
                        val file = uri.toFile()
                        var bytes = byteArrayOf()
                        try {
                            FileInputStream(file).use { inputStream ->
                                bytes = ByteArray(inputStream.available())
                                inputStream.read(bytes)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        return bytes
                    } catch (ignored: FileNotFoundException) {
                        null
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }
}