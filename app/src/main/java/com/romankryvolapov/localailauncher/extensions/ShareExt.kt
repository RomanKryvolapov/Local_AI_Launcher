/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import java.io.File

fun Context.shareStringInfo(data: String) {
    val intent = ShareCompat.IntentBuilder(this)
        .setText(data)
        .setType("text/plain")
        .setChooserTitle("Data")
        .createChooserIntent()

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

fun Context.sharePdf(file: File) {
    val intent = ShareCompat.IntentBuilder(this)
        .setType("application/pdf")
        .setStream(FileProvider.getUriForFile(this, "${this.packageName}.provider", file))
        .setChooserTitle("Document")
        .createChooserIntent()
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

fun Context.sendEmailWithAttachment(email: String, subject: String, text: String, file: File?) {
    val builder = ShareCompat.IntentBuilder(this)
        .setType("text/plain")
        .setChooserTitle("Email")
        .setEmailTo(arrayOf(email))
        .setSubject(subject)
        .setText(text)

    // Attach file if needed
    if (file != null) {
        val fileUri = FileProvider
            .getUriForFile(this, "${this.packageName}.provider", file)

        builder
            .setType(contentResolver.getType(fileUri))
            .setStream(fileUri)
    }

    val intent = builder.intent
        .setAction(Intent.ACTION_SENDTO)
        .setData(Uri.parse("mailto:")) // only email apps should handle this
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}