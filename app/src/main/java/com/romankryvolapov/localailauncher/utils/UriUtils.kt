/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File

object UriUtils {

    fun getRealFilePathFromUri(context: Context, uri: Uri): String? {
        return when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                fetchDocumentFilePath(context, uri)
            }

            "content".equals(uri.scheme!!, ignoreCase = true) -> {
                if (isGooglePhotosUri(uri)) {
                    return uri.lastPathSegment
                }
                getDataColumn(context, uri, null, null)
            }

            "file".equals(uri.scheme!!, ignoreCase = true) -> {
                uri.path
            }

            else -> null
        }
    }

    private fun fetchDocumentFilePath(context: Context, uri: Uri): String? {
        return when {
            isExternalStorageDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    if (split.size > 1) {
                        context.getExternalFilesDir(null)!!.toString() + "/" + split[1]
                    } else {
                        context.getExternalFilesDir(null)!!.toString() + "/"
                    }
                } else {
                    "storage" + "/" + docId.replace(":", "/")
                }
            }

            isRawDownloadsDocument(uri) -> {
                val fileName = getFilePath(context, uri)
                val subFolderName = getSubFolders(uri)

                if (fileName != null) {
                    return context.getExternalFilesDir(null)!!
                        .toString() + "/Download/" + subFolderName + fileName
                }
                val id = DocumentsContract.getDocumentId(uri)

                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
                getDataColumn(context, contentUri, null, null)
            }

            isDownloadsDocument(uri) -> {
                val fileName = getFilePath(context, uri)

                if (fileName != null) {
                    return context.getExternalFilesDir(null)!!.toString() + "/Download/" + fileName
                }
                var id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:".toRegex(), "")
                    val file = File(id)
                    if (file.exists())
                        return id
                }
                if (id.startsWith("raw%3A%2F")) {
                    id = id.replaceFirst("raw%3A%2F".toRegex(), "")
                    val file = File(id)
                    if (file.exists())
                        return id
                }
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
                getDataColumn(context, contentUri, null, null)
            }

            isMediaDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val contentUri: Uri? = when (split[0]) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> null
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                getDataColumn(context, contentUri, selection, selectionArgs)
            }

            else -> null
        }
    }

    private fun getSubFolders(uri: Uri): String {
        val replaceChars = uri.toString()
            .replace("%2F", "/")
            .replace("%20", " ")
            .replace("%3A", ":")
        val bits = replaceChars.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val sub5 = bits[bits.size - 2]
        val sub4 = bits[bits.size - 3]
        val sub3 = bits[bits.size - 4]
        val sub2 = bits[bits.size - 5]
        val sub1 = bits[bits.size - 6]
        return if (sub1 == "Download") {
            "$sub2/$sub3/$sub4/$sub5/"
        } else if (sub2 == "Download") {
            "$sub3/$sub4/$sub5/"
        } else if (sub3 == "Download") {
            "$sub4/$sub5/"
        } else if (sub4 == "Download") {
            "$sub5/"
        } else {
            ""
        }
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        return try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                cursor.getString(index)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            cursor?.close()
        }
    }

    private fun getFilePath(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME)
        return try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                cursor.getString(index)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            cursor?.close()
        }
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isRawDownloadsDocument(uri: Uri): Boolean {
        val uriToString = uri.toString()
        return uriToString.contains("com.android.providers.downloads.documents/document/raw")
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}