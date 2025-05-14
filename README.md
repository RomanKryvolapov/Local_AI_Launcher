# Local AI Launcher

Launching the LLM models on an Android smartphone using various AI engines.

## Current Implementations

### MLC LLM

* **Repository**: [github.com/mlc-ai/mlc-llm](https://github.com/mlc-ai/mlc-llm)
* **CLI Deployment**: [llm.mlc.ai/docs/deploy/cli.html](https://llm.mlc.ai/docs/deploy/cli.html)
* **Android Deployment**: [llm.mlc.ai/docs/deploy/android.html](https://llm.mlc.ai/docs/deploy/android.html)
* **Model Compilation**: [llm.mlc.ai/docs/compilation/compile\_models.html](https://llm.mlc.ai/docs/compilation/compile_models.html)
* **Hugging Face**: [huggingface.co/mlc-ai](https://huggingface.co/mlc-ai)
* **Example Model**: [huggingface.co/google/gemma-3-1b-it-qat-q4\_0-unquantized](https://huggingface.co/google/gemma-3-1b-it-qat-q4_0-unquantized)

### MediaPipe

* **Guide**: [ai.google.dev/edge/mediapipe/solutions/guide](https://ai.google.dev/edge/mediapipe/solutions/guide)
* **Repository**: [github.com/google-ai-edge/mediapipe](https://github.com/google-ai-edge/mediapipe)
* **Hugging Face**: [huggingface.co/litert-community](https://huggingface.co/litert-community)
* **Kaggle Model**: [kaggle.com/models/google/gemma-3/tfLite](https://www.kaggle.com/models/google/gemma-3/tfLite)
* **Maven Artifact**: [mvnrepository.com/artifact/com.google.mediapipe](https://mvnrepository.com/artifact/com.google.mediapipe)

### ONNX

* **Wikipedia**: [en.wikipedia.org/wiki/Open\_Neural\_Network\_Exchange](https://en.wikipedia.org/wiki/Open_Neural_Network_Exchange)
* **ONNX Runtime for GenAI (Java)**: [github.com/microsoft/onnxruntime-genai/tree/main/src/java](https://github.com/microsoft/onnxruntime-genai/tree/main/src/java)
* **API Documentation**: [onnxruntime.ai/docs/genai/api/java.html](https://onnxruntime.ai/docs/genai/api/java.html)
* **Hugging Face**: [huggingface.co/onnx-community](https://huggingface.co/onnx-community)
* **Maven Artifact**: [mvnrepository.com/artifact/com.microsoft.onnxruntime](https://mvnrepository.com/artifact/com.microsoft.onnxruntime)

### llama.cpp

* **Wikipedia**: [en.wikipedia.org/wiki/Llama.cpp](https://en.wikipedia.org/wiki/Llama.cpp)
* **Repository**: [github.com/ggml-org/llama.cpp](https://github.com/ggml-org/llama.cpp)
* **Android Example**: [github.com/ggml-org/llama.cpp/tree/master/examples/llama.android](https://github.com/ggml-org/llama.cpp/tree/master/examples/llama.android)

## Future Integrations

### PyTorch

* **Wikipedia**: [en.wikipedia.org/wiki/PyTorch](https://en.wikipedia.org/wiki/PyTorch)
* **Official Site**: [pytorch.org](https://pytorch.org/)
* **Repository**: [github.com/pytorch/pytorch](https://github.com/pytorch/pytorch)

## Planned Features

* Model downloads from **Hugging Face**
* Query history tracking
* Model configuration settings and additional utilities

## Additional Resources

* [Large language model (Wikipedia)](https://en.wikipedia.org/wiki/Large_language_model)
* [List of large language models (Wikipedia)](https://en.wikipedia.org/wiki/List_of_large_language_models)
* [Llama (language model) (Wikipedia)](https://en.wikipedia.org/wiki/Llama_%28language_model%29)
* [Language model benchmark (Wikipedia)](https://en.wikipedia.org/wiki/Language_model_benchmark)
* [Model evaluation paper (arXiv)](https://arxiv.org/html/2410.03613v1)

## Architecture

### Modular Clean Architecture

* **app**: Android application module containing UI (Jetpack Compose), ViewModels, and orchestrating engine modules.
* **domain**: Core business logic with use case definitions and the `IAIEngine` interface.
* **data**: Data layer responsible for model loading, history persistence, and repository implementations.
* **Engine modules**:
    * **mlc-llm-engine**: JNI integration with MLC LLM ([github.com/mlc-ai/mlc-llm](https://github.com/mlc-ai/mlc-llm)).
    * **mediapipe-engine**: MediaPipe graph integration for TensorFlow Lite inference ([ai.google.dev/edge/mediapipe/solutions/guide](https://ai.google.dev/edge/mediapipe/solutions/guide)).
    * **onnx-engine**: ONNX Runtime GenAI Java API integration ([onnxruntime.ai/docs/genai/api/java.html](https://onnxruntime.ai/docs/genai/api/java.html)).
    * **llama-cpp-engine**: JNI wrapper for llama.cpp C++ inference ([github.com/ggml-org/llama.cpp](https://github.com/ggml-org/llama.cpp)).
* **gradle**: Common Gradle scripts for model compilation and dependency management.

This modular setup follows **Clean Architecture** principles and employs the **MVVM** pattern with Android Architecture Components.

## Tech Stack

* **Language**: Kotlin
* **Build**: Gradle (Groovy DSL)
* **Android SDK**: API 21+
* **C++**: JNI wrappers for native inference libraries
* **Libraries**:
    * Jetpack Compose (UI)
    * AndroidX Lifecycle (ViewModel, LiveData / Kotlin Flow)
    * Dependency Injection (Hilt or Koin)
    * MLC LLM, MediaPipe, ONNX Runtime, llama.cpp
* **Supported ABIs**: arm64-v8a, armeabi-v7a, x86, x86\_64

## Directory Structure

```
Local_AI_Launcher/
├── app/                 # Android application module
├── domain/              # Core business logic and interfaces
├── data/                # Data layer and repositories
├── mlc-llm-engine/      # MLC LLM integration
├── mediapipe-engine/    # MediaPipe integration
├── onnx-engine/         # ONNX Runtime integration
├── llama-cpp-engine/    # llama.cpp integration
├── gradle/              # Custom Gradle scripts
├── settings.gradle      # Project settings
└── build.gradle         # Top-level build script
```