# Local AI Launcher

## llama.cpp Project Structure

An overview of the key directories and files in the **ggml-org/llama.cpp** repository:

- **Root Directory**
  - `README.md`, `LICENSE`, `CONTRIBUTING.md`, `SECURITY.md` – project documentation, license and contribution guidelines
  - `CMakeLists.txt`, `Makefile`, `CMakePresets.json` – build scripts and presets for CMake/Make
  - Configuration files: `.clang-format`, `.clang-tidy`, `.editorconfig`, `.flake8`, `.gitignore`, `.pre-commit-config.yaml`

- **.github/** and **ci/**
  - GitHub Actions workflows, issue/PR templates, and CI/CD scripts

- **cmake/**
  - Custom CMake modules and functions for multi-platform build configuration

- **common/**
  - Shared utilities (logging, model format converters, helper functions)

- **include/**
  - Public C API headers (`llama.h`, `common.h`, tokenization interfaces)

- **src/**
  - Core inference implementation: model loading/parsing (`gguf`, `ggml`), tensor operations, memory management, tokenization, and post-processing

- **ggml/**
  - Vendored GGML library for low-level tensor operations and quantization optimizations

- **examples/**
  - Sample applications:
    - CLI tools (`llama-cli`, quantize utilities, embedding extractors)
    - OpenAI-API-compatible HTTP server

- **tools/**
  - Standalone utilities and scripts (e.g. `convert_hf_to_gguf.py`, test tools)

- **tests/**
  - Unit and integration tests covering model loading, tokenization, and inference

- **docs/**
  - Extended documentation, HOWTO guides, and FAQs

- **scripts/**
  - Build and automation scripts (e.g. Xcode framework packaging, model download/preparation)

- **gguf-py/**
  - Python library for working with GGUF format (conversion, validation)

- **prompts/**
  - Prompt templates and configuration files for various model flavors (Alpaca, Vicuna, etc.)

- **models/**
  - Metadata and manifests for supported models

- **pocs/**
  - Proof-of-Concept experiments and prototype code

- **grammars/**
  - DSL grammars and parsers for specialized use cases

- **licenses/** and **media/**
  - Third-party license documents and images used in documentation

- **llama.cpp** 
  - the main C++ file with implementations of all APIs.

- **llama.h** 
  - the header where functions are declared and structures are described.

- **llama-model-loader.*** 
  - the logic of loading the model (reading GGUF files, mmap, etc.).

- **llama-model.*** 
  - the classes llama_model, llama_context themselves.


# llama-android.cpp Parameters Reference

This document explains the model and context configuration options used in `llama.cpp` via JNI on Android. Each parameter is listed with its meaning, impact, and typical values.

---

## `llama_model_params`

Defined via `llama_model_default_params()` and optionally configured before calling `llama_model_load_from_file(...)`.

* **n\_gpu\_layers**: Specifies how many transformer layers to offload to GPU. Set to `0` for CPU-only mode. If set to `1` or more, GPU acceleration is used (if available and enabled at build time).

* **main\_gpu**: The index of the GPU to use when multiple GPUs are present. Typically `0` for the default GPU.

* **tensor\_split**: Used for multi-GPU inference. An array of float values to determine how to split tensor data across available GPUs. `nullptr` disables this behavior.

* **vocab\_only**: If set to `true`, loads only the model vocabulary without weights. Useful for tokenization tools.

* **use\_mmap**: Enables memory-mapped loading of the model file. When `true`, significantly reduces memory consumption — ideal for Android.

* **use\_mlock**: Locks the model into RAM to prevent swapping. Only meaningful on Linux. Typically `false` on Android.

* **lora\_adapter**: Path to a LoRA adapter file for fine-tuned weights. Enables usage of parameter-efficient adapters.

* **lora\_base**: Path to the original base model, needed if merging with a LoRA adapter at runtime.

* **dtype**: Reserved for future use to explicitly define weight data types (e.g., `F32`, `Q8_0`, etc.). Currently not used in standard builds.

---

## `llama_context_params`

Used when creating a new context with `llama_new_context_with_model(model, ctx_params)`.

* **n\_ctx**: Defines the size of the context window, i.e., how many tokens the model can consider at once. Typical values are `512`, `2048`, `4096`, `8192` or more.

* **n\_batch**: The number of tokens that can be processed in one forward pass. Normally equal to `n_ctx`, but can be tuned for performance.

* **n\_threads**: Number of CPU threads used for inference. Set according to device capability (e.g., `cores - 2`).

* **n\_threads\_batch**: Number of threads used for processing batches. Often the same as `n_threads`, unless tuning for background workloads.

* **seed**: The random seed for sampling. Use `-1` for a random seed. Set to a fixed value for reproducible outputs.

* **f16\_kv**: Enables float16 precision for KV cache to reduce memory usage. May slightly impact accuracy.

* **logits\_all**: If `true`, returns logits for every token, not just the final one. Useful for advanced use cases like training or token-level analysis.

* **embedding**: If `true`, runs the model only to extract embeddings instead of generating tokens.

* **offload\_kqv**: Deprecated. Previously used to offload key/query/value projection.

* **mul\_mat\_q**: Uses optimized matrix multiplication routines. Can improve performance depending on hardware.

* **flash\_attn**: Enables FlashAttention, an efficient GPU-based attention mechanism. Available only if supported in the build.

* **rope\_freq\_base**: Base frequency used in RoPE (rotary position embeddings). Only modify when experimenting with long context adaptations.

* **rope\_freq\_scale**: Scales the rotary embedding frequency. Can be used to fine-tune model performance over longer sequences.

* **pooling\_type**: Specifies pooling strategy when `embedding` is enabled. For example, mean pooling.

---

## `llama_backend_init()`

This function initializes runtime dependencies like SYCL/OpenCL/CUDA/Metal backends, depending on how `llama.cpp` was compiled.

You can view the active backend and supported hardware by calling:

```cpp
llama_print_system_info();
```

---

## Benchmarks & Tokens

In `completion_loop()` and `bench_model()`, the following operations are key:

* `llama_kv_self_clear(...)`: Resets the KV cache, useful before or between forward passes.
* `llama_decode(...)`: Runs the model inference for a batch of tokens.
* `llama_batch`: A structure used to provide token data and receive logits.
* `llama_sampler_sample(...)`: Chooses the next token based on logits and sampling strategy.

---

## Notes

* All parameter structs are initialized with `*_default_params()`. Any field not explicitly modified retains its default.
* Runtime logic (e.g., backend selection) is fixed at **compile time**, based on `CMake` flags like `GGML_CUDA`, `GGML_SYCL`, etc.
* GPU support must be enabled at build time — you cannot enable it dynamically on Android.

---



