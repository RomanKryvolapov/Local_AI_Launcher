package com.romankryvolapov.localailauncher.domain

enum class Models(val modelName: String, val modelLib: String) {
    GEMMA_3_1B_QAT("gemma-3-1b-it-q4f16_1-MLC", "gemma3_text_q4f16_1_15281663a194ab7a82d5f6c3b0d59432"),
    GEMMA_3_4B_QAT("gemma-3-4b-it-q4f16_1-MLC", "gemma3_q4f16_1_06b17d1fdaff9d482ee470497f45993e"),
}