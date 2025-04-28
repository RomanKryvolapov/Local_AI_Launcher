/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.models.list

import android.os.Parcelable

interface CommonModelWithId : Parcelable {
    val elementId: Int?
    val elementEnum: CommonListElementIdentifier?
}