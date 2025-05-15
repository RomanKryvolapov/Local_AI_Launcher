package ai.onnxruntime.genai

var engine: SimpleGenAI? = null

fun clear() {
    engine?.close()
    engine = null
}