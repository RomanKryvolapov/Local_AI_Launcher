---
library_name: mlc-llm
base_model: google/gemma-3-4b-it
tags:
- mlc-llm
- web-llm
---

# gemma-3-4b-it-q4f16_1-MLC

This is the [gemma-3-4b-it](https://huggingface.co/google/gemma-3-4b-it) model in MLC format `q4f16_1`.
The model can be used for projects [MLC-LLM](https://github.com/mlc-ai/mlc-llm) and [WebLLM](https://github.com/mlc-ai/web-llm).

## Example Usage

Here are some examples of using this model in MLC LLM.
Before running the examples, please install MLC LLM by following the [installation documentation](https://llm.mlc.ai/docs/install/mlc_llm.html#install-mlc-packages).

### Chat

In command line, run
```bash
mlc_llm chat HF://mlc-ai/gemma-3-4b-it-q4f16_1-MLC
```

### REST Server

In command line, run
```bash
mlc_llm serve HF://mlc-ai/gemma-3-4b-it-q4f16_1-MLC
```

### Python API

```python
from mlc_llm import MLCEngine

# Create engine
model = "HF://mlc-ai/gemma-3-4b-it-q4f16_1-MLC"
engine = MLCEngine(model)

# Run chat completion in OpenAI API.
for response in engine.chat.completions.create(
    messages=[{"role": "user", "content": "What is the meaning of life?"}],
    model=model,
    stream=True,
):
    for choice in response.choices:
        print(choice.delta.content, end="", flush=True)
print("\n")

engine.terminate()
```

## Documentation

For more information on MLC LLM project, please visit our [documentation](https://llm.mlc.ai/docs/) and [GitHub repo](http://github.com/mlc-ai/mlc-llm).
