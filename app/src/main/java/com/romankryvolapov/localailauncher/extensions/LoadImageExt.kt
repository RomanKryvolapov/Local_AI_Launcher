/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.models.common.ImageSource
import java.io.File

fun AppCompatImageView.loadImage(imageSource: ImageSource) {
    when (imageSource) {
        is ImageSource.Url -> {
            if (imageSource.placeholder != 0 && imageSource.error != 0) {
                loadImage(
                    imageUrl = imageSource.url,
                    placeholder = imageSource.placeholder,
                    error = imageSource.error,
                )
            } else {
                loadImageWithoutPlaceholder(
                    imageUrl = imageSource.url
                )
            }
        }
        is ImageSource.Res -> {
            setImageResource(imageSource.res)
        }
    }
}

fun AppCompatImageView.loadImage(
    imageUrl: String?,
    @DrawableRes placeholder: Int = R.drawable.bg_placeholder_rounded_icon,
    @DrawableRes error: Int = R.drawable.bg_placeholder_rounded_icon,
) {
    if (!isInEditMode) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(context.drawable(placeholder))
            .error(context.drawable(error))
            .into(this)
    }
}

fun AppCompatImageView.loadImageWithoutPlaceholder(imageUrl: String?) {
    if (!isInEditMode) {
        Glide.with(this)
            .load(imageUrl)
            .into(this)
    }
}


// Circle image

fun AppCompatImageView.loadCircleImage(imageSource: ImageSource) {
    when (imageSource) {
        is ImageSource.Url -> {
            if (imageSource.placeholder != 0 && imageSource.error != 0) {
                loadCircleImage(
                    imageUrl = imageSource.url,
                    placeholder = imageSource.placeholder,
                    error = imageSource.error,
                )
            } else {
                loadCircleImageWithoutPlaceHolder(
                    imageUrl = imageSource.url
                )
            }
        }
        is ImageSource.Res -> {
            loadCircleImage(
                imageRes = imageSource.res,
            )
        }
    }
}

fun AppCompatImageView.loadCircleImage(imageRes: Int) {
    if (!isInEditMode) {
        Glide.with(this)
            .load(imageRes)
            .circleCrop()
            .into(this)
    }
}

fun AppCompatImageView.loadCircleImage(
    imageUrl: String?,
    @DrawableRes placeholder: Int = R.drawable.bg_placeholder_oval_icon,
    @DrawableRes error: Int = R.drawable.bg_placeholder_oval_icon,
) {
    if (!isInEditMode) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(context.drawable(placeholder))
            .error(context.drawable(error))
            .circleCrop()
            .into(this)
    }
}

fun AppCompatImageView.loadCircleImageWithoutPlaceHolder(imageUrl: String?) {
    if (!isInEditMode) {
        Glide.with(this)
            .load(imageUrl)
            .circleCrop()
            .into(this)
    }
}

// Top round corners image

fun AppCompatImageView.loadImageWithTopRoundCorners(
    imageSource: ImageSource,
    roundSizeResource: Int = R.dimen.default_semi_corners_radius,
    @DrawableRes placeholder: Int = R.drawable.bg_placeholder_oval_icon,
    @DrawableRes error: Int = R.drawable.bg_placeholder_oval_icon,
) {
    when {
        imageSource is ImageSource.Url && imageSource.url != null -> {
            loadImageWithTopRoundCorners(
                imageUrl = imageSource.url,
                roundSizeResource = roundSizeResource,
                placeholder = placeholder,
                error = error,
            )
        }
        imageSource is ImageSource.Res -> {
            loadImageWithTopRoundCorners(
                imageRes = imageSource.res,
                roundSizeResource = roundSizeResource,
            )
        }
    }
}

fun AppCompatImageView.loadImageWithTopRoundCorners(
    imageUrl: String,
    roundSizeResource: Int = R.dimen.default_semi_corners_radius,
    @DrawableRes placeholder: Int = R.drawable.bg_placeholder_oval_icon,
    @DrawableRes error: Int = R.drawable.bg_placeholder_oval_icon,
) {
    val cornerRoundSize = this.context.pxDimen(roundSizeResource).toFloat()
    loadImageWithRoundCorners(
        imageUrl = imageUrl,
        granularRoundedCorners = GranularRoundedCorners(
            cornerRoundSize,
            cornerRoundSize,
            0f,
            0f,
        ),
        placeholder = placeholder,
        error = error
    )
}

fun AppCompatImageView.loadImageWithTopRoundCorners(
    imageRes: Int,
    roundSizeResource: Int = R.dimen.default_semi_corners_radius,
) {
    val cornerRoundSize = this.context.pxDimen(roundSizeResource).toFloat()
    loadImageWithRoundCorners(
        imageRes = imageRes,
        granularRoundedCorners = GranularRoundedCorners(
            cornerRoundSize,
            cornerRoundSize,
            0f,
            0f,
        ),
    )
}

// Round corners image

fun AppCompatImageView.loadImageWithRoundCorners(
    imageSource: ImageSource,
    roundSizeResource: Int = R.dimen.default_semi_corners_radius,
    @DrawableRes placeholder: Int = R.drawable.bg_placeholder_oval_icon,
    @DrawableRes error: Int = R.drawable.bg_placeholder_oval_icon,
) {
    when {
        imageSource is ImageSource.Url && !imageSource.url.isNullOrEmpty() -> {
            loadImageWithRoundCorners(
                imageUrl = imageSource.url,
                roundSizeResource = roundSizeResource,
                placeholder = placeholder,
                error = error,
            )
        }
        imageSource is ImageSource.Res -> {
            loadImageWithRoundCorners(
                imageRes = imageSource.res,
                roundSizeResource = roundSizeResource,
            )
        }
    }
}

fun AppCompatImageView.loadImageWithRoundCorners(
    imageUrl: String,
    roundSizeResource: Int = R.dimen.default_semi_corners_radius,
    @DrawableRes placeholder: Int = R.drawable.bg_placeholder_rounded_icon,
    @DrawableRes error: Int = R.drawable.bg_placeholder_rounded_icon,
) {
    val cornerRoundSize = this.context.pxDimen(roundSizeResource).toFloat()
    loadImageWithRoundCorners(
        imageUrl = imageUrl,
        granularRoundedCorners = GranularRoundedCorners(
            cornerRoundSize,
            cornerRoundSize,
            cornerRoundSize,
            cornerRoundSize
        ),
        placeholder = placeholder,
        error = error
    )
}

private fun AppCompatImageView.loadImageWithRoundCorners(
    imageUrl: String,
    granularRoundedCorners: GranularRoundedCorners,
    @DrawableRes placeholder: Int,
    @DrawableRes error: Int,
) {
    if (!isInEditMode) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(context.drawable(placeholder))
            .error(context.drawable(error))
            .transform(CenterCrop(), granularRoundedCorners)
            .into(this)
    }
}

fun AppCompatImageView.loadImageWithRoundCorners(
    imageRes: Int,
    roundSizeResource: Int = R.dimen.default_semi_corners_radius,
) {
    val cornerRoundSize = this.context.pxDimen(roundSizeResource).toFloat()
    loadImageWithRoundCorners(
        imageRes = imageRes,
        granularRoundedCorners = GranularRoundedCorners(
            cornerRoundSize,
            cornerRoundSize,
            cornerRoundSize,
            cornerRoundSize
        ),
    )
}

private fun AppCompatImageView.loadImageWithRoundCorners(
    imageRes: Int,
    granularRoundedCorners: GranularRoundedCorners,
) {
    if (!isInEditMode) {
        Glide.with(this)
            .load(imageRes)
            .transform(CenterCrop(), granularRoundedCorners)
            .into(this)
    }
}

fun AppCompatImageView.loadImageWithRoundCornersWithoutCache(
    imageFile: File,
    roundSizeResource: Int = R.dimen.default_semi_corners_radius,
    @DrawableRes placeholder: Int = R.drawable.bg_placeholder_rounded_icon,
    @DrawableRes error: Int = R.drawable.bg_placeholder_rounded_icon,
) {
    val cornerRoundSize = this.context.pxDimen(roundSizeResource).toFloat()
    loadImageWithRoundCornersWithoutCache(
        imageFile = imageFile,
        granularRoundedCorners = GranularRoundedCorners(
            cornerRoundSize,
            cornerRoundSize,
            cornerRoundSize,
            cornerRoundSize
        ),
        placeholder = placeholder,
        error = error
    )
}

private fun AppCompatImageView.loadImageWithRoundCornersWithoutCache(
    imageFile: File,
    granularRoundedCorners: GranularRoundedCorners,
    @DrawableRes placeholder: Int,
    @DrawableRes error: Int
) {
    if (!isInEditMode) {
        Glide.with(this)
            .load(imageFile)
            .placeholder(context.drawable(placeholder))
            .error(context.drawable(error))
            .transform(granularRoundedCorners)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(this)
    }
}

fun combineImages(
    context: Context,
    @DrawableRes iconImageRes: Int,
    @DrawableRes backgroundImageRes: Int,
    @DimenRes marginLeftRes: Int? = null,
    @DimenRes marginRightRes: Int? = null,
    @DimenRes marginTopRes: Int? = null,
    @DimenRes marginBottomRes: Int? = null,
): Bitmap {
    val backgroundBitmap = ContextCompat.getDrawable(context, backgroundImageRes)!!.toBitmap()
    val iconBitmap = ContextCompat.getDrawable(context, iconImageRes)!!.toBitmap()
    val marginLeft = if (marginLeftRes != null) context.pxDimen(marginLeftRes) else 0
    val marginRight = if (marginRightRes != null) context.pxDimen(marginRightRes) else 0
    val marginTop = if (marginTopRes != null) context.pxDimen(marginTopRes) else 0
    val marginBottom = if (marginBottomRes != null) context.pxDimen(marginBottomRes) else 0
    val iconHeight = backgroundBitmap.height - marginTop - marginBottom
    val iconWidth = backgroundBitmap.width - marginLeft - marginRight
    val rect = Rect(
        marginLeft,
        marginTop,
        iconWidth + marginLeft,
        iconHeight + marginTop,
    )
    Canvas(backgroundBitmap).apply {
        drawBitmap(iconBitmap, null, rect, null)
    }
    return backgroundBitmap
}