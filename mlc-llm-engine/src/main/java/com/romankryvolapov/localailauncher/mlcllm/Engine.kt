package com.romankryvolapov.localailauncher.mlcllm

var engine: MLCEngine? = null

fun clear() {
    engine?.reset()
    engine?.unload()
    engine = null
}