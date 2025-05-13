package com.romankryvolapov.localailauncher.models.chat

import android.os.Parcelable
import com.romankryvolapov.localailauncher.extensions.equalTo
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ChatMessageErrorUi(
    val id: UUID,
    val timeStamp: Long,
    val message: String,
) : ChatMessageAdapterMarker, Parcelable {

    override fun isItemSame(other: Any?): Boolean {
        return equalTo(
            other,
            { id },
        )
    }

    override fun isContentSame(other: Any?): Boolean {
        return equalTo(
            other,
            { id },
            { message },
            { timeStamp },
        )
    }

}