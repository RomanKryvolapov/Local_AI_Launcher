/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.common.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ChatMessageModel(
    val id: UUID,
    val dialogID: UUID,
    val timeStamp: Long,
    val message: String,
    val messageData: String
) : Parcelable
