/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.models.chat

import android.os.Parcelable
import com.romankryvolapov.localailauncher.extensions.equalTo
import com.romankryvolapov.localailauncher.models.common.DiffEquals
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ChatMessageUi(
    val id: UUID,
    val timeStamp: Long,
    val isUserMessage: Boolean,
    val message: String,
    val messageData: String,
) : DiffEquals, Parcelable {

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
            { isUserMessage },
            { message },
            { messageData },)
    }

}