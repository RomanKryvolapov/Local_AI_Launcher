package com.romankryvolapov.localailauncher.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ChatMessageModel(
    val id: UUID,
    val timeStamp: Long,
    val message: String,
    val messageData: String
) : Parcelable
