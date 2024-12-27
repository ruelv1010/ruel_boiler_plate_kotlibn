package syntactics.boilerplate.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.WindowManager

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

fun getBitmapFromAsset(context: Context, imageName: String): Bitmap? {
    val assetManager = context.assets

    val istr: InputStream
    var bitmap: Bitmap? = null
    try {
        istr = assetManager.open(imageName)
        bitmap = BitmapFactory.decodeStream(istr)
    } catch (e: IOException) {
        // handle exception
    }

    return bitmap
}

/**
 * another way of converting image uri to file
 */
fun convertImageUriToFile(context: Context, uri: Uri?): File {
    // Get the input stream from the content URI
    val inputStream = uri?.let { context.contentResolver.openInputStream(it) }

    // Generate a file name for the image
    val fileName = "${System.currentTimeMillis()}.jpg"

    // Get the directory where you want to save the image
    val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    // Create a new file in the directory with the generated file name
    val file = File(directory, fileName)

    // Create an output stream for the file
    val outputStream = FileOutputStream(file)

    // Copy the contents of the input stream to the output stream
    inputStream?.copyTo(outputStream)

    // Close the streams
    inputStream?.close()
    outputStream.close()

    return file
}


/**
 * Custom method to get a File from a cropped Uri using library
  */
fun getFileFromCroppedUri(context: Context, uri: Uri): File? {
    try {
        val contentResolver = context.contentResolver

        // Attempt to open an input stream for the URI
        val inputStream = contentResolver.openInputStream(uri)

        if (inputStream != null) {
            // Create a temporary file to copy the content from the input stream
            val tempFile = File.createTempFile("cropped_image", ".jpg")
            val outputStream = FileOutputStream(tempFile)

            // Copy the data from the input stream to the temporary file
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            return tempFile
        }
    } catch (e: Exception) {
        Log.e("GetFileUri Exception:", e.message ?: "")
    }

    return null
}


fun getFileFromUri(context: Context, uri: Uri?): File? {
    uri ?: return null
    val newUriString = uri.toString()
        .replace("content://com.android.providers.downloads.documents/", "content://com.android.providers.media.documents/")
        .replace("/msf%3A", "/image%3A")
    val newUri = Uri.parse(newUriString)

    try {
        val realPath = getRealPathFromURI(uri, context)

        val path = realPath.ifEmpty {
            when {
                newUri.path?.contains("/document/raw:") == true -> newUri.path?.replace(
                    "/document/raw:",
                    ""
                )
                newUri.path?.contains("/document/primary:") == true -> newUri.path?.replace(
                    "/document/primary:",
                    "/storage/emulated/0/"
                )
                else -> return null
            }
        }

        return if (path.isNullOrEmpty()) null else File(path)
    } catch (e: Exception) {
        Log.i("GetFileUri Exception:", e.message ?: "")
    }

    return null
}

fun getRealPathFromURI(uri: Uri, context: Context): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val name = it.getString(nameIndex)
            val file = File(context.filesDir, name)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable: Int = inputStream.available() ?: 0
                //int bufferSize = 1024;
                val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
                val buffer = ByteArray(bufferSize)
                var read: Int

                while (input.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
            }

            return file.path
        }
    }

    return ""
}

fun setQR(context: Context, code: String?): Bitmap? {
    val bitmap: Bitmap
    val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    // initializing a variable for default display.
    val display = manager.defaultDisplay

    // creating a variable for point which
    // is to be displayed in QR Code.
    val point = Point()
    display.getSize(point)

    // getting width and
    // height of a point
    val width = point.x
    val height = point.y

    // generating dimension from width and height.
    var dimen = if (width < height) width else height
    dimen = dimen
    //val qrgEncoder = QRGEncoder(code, null, QRGContents.Type.TEXT, dimen)
   // bitmap = qrgEncoder.bitmap
    return null
}


