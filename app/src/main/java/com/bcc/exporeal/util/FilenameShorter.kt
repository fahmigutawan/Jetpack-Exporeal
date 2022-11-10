package com.bcc.exporeal.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

fun Context.getFileName(uri: Uri): String = when(uri.scheme) {
    ContentResolver.SCHEME_CONTENT -> {
        var name = getContentFileName(uri) ?: "File Name Not Detected"

        name.let {
            val fileFormat = it.split(".").last()
            val newName = it.replace(fileFormat, "")

            if(newName.length >= 25){
                var beginningName = ""
                for(i in 0..10){
                    beginningName += newName[i]
                }

                var lastName = ""
                for(i in (newName.length-11)..(newName.length-1)){
                    lastName += newName[i]
                }

                name = "$beginningName.....$lastName.$fileFormat"
            }
        }

        name
    }
    else -> {
        var name = uri.path?.let(::File)?.name ?: "File Name Not Detected"

        name.let {
            val fileFormat = it.split(".").last()
            val newName = it.replace(fileFormat, "")

            if(newName.length >= 15){
                var beginningName = ""
                for(i in 0..3){
                    beginningName += newName[i]
                }

                var lastName = ""
                for(i in (newName.length-4)..(newName.length-1)){
                    lastName += newName[i]
                }

                name = "$beginningName.....$lastName.$fileFormat"
            }
        }

        name
    }
}

private fun Context.getContentFileName(uri: Uri): String? = runCatching {
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        cursor.moveToFirst()
        return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
    }
}.getOrNull()