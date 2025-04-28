package com.romankryvolapov.localailauncher.ui.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.LinearLayoutCompat
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.databinding.LayoutFullscreenLoaderBinding
import com.romankryvolapov.localailauncher.models.common.StringSource

class FullscreenLoaderView(context: Context): Dialog(context)  {

    private val binding = LayoutFullscreenLoaderBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setBackground()
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    fun setMessage(message: StringSource) {
        binding.tvMessage.text = message.getString(context)
    }

    private fun setBackground() {
        window?.setLayout(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(R.color.color_EFF6FF)
    }

}