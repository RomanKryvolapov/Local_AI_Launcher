# Local AI Launcher

Launch the Gemma 3 AI model
https://huggingface.co/google/gemma-3-1b-it-qat-q4_0-unquantized
locally on the device using
https://github.com/mlc-ai/mlc-llm

You can launch any other model

Check if you have it installed:

rustup --version
cargo --version
ninja --version
cmake --version
javac -version
jar --help
javadoc --help

Instructions:

https://llm.mlc.ai/docs/deploy/cli.html
https://llm.mlc.ai/docs/deploy/android.html
https://llm.mlc.ai/docs/compilation/compile_models.html

Option 1: Clone MLC model from https://huggingface.co/mlc-ai:

git clone https://huggingface.co/mlc-ai/gemma-3-4b-it-q4f16_1-MLC

Option 2: Compile original model:

git clone https://huggingface.co/google/gemma-3-4b-it-qat-q4_0-unquantized

Make sure all the necessary files are present in config.json

run in console from model folder:

mlc_llm convert_weight . --quantization q4f16_1 -o ..\MLC

mlc_llm gen_config . --quantization q4f16_1 --conv-template redpajama_chat --context-window-size 768 -o ..\MLC

mlc_llm compile ..\MLC/mlc-chat-config.json --device android -o ..\android.tar

Generate Android code:

create file "mlc-package-config.json" with data

{
 "device": "android",
 "model_list": [
  {
  "model": "HF://mlc-ai/gemma-3-4b-it-q4f16_1-MLC",
  "model_id": "gemma-3-4b-it-q4f16_1-MLC",
  "estimated_vram_bytes": 3000000000
  }
 ]
}

run in console from model folder:

$env:TVM_NDK_CC="C:\Users\Roman\AppData\Local\Android\Sdk\ndk\27.0.11718014\toolchains\llvm\prebuilt\windows-x86_64\bin\aarch64-linux-android24-clang"

$env:MLC_LLM_SOURCE_DIR="C:\ExampleProjects\mlc-llm"

$env:TVM_SOURCE_DIR = "C:\ExampleProjects\mlc-llm\3rdparty\tvm"

$env:ANDROID_NDK="C:\Users\Roman\AppData\Local\Android\Sdk\ndk\27.0.11718014"

$env:JAVA_HOME="C:\Program Files\Java\jdk-17"

$env:Path="$env:JAVA_HOME\bin;$env:Path"

$env:PATH += ";C:\Users\Roman\.cargo\bin"

mlc_llm package

Also this commands can help:

pip install --upgrade huggingface_hub

huggingface-cli login

conda activate your-environment

conda install -c conda-forge cmake -y

conda install -c conda-forge ninja

init of git with lfs help:

git init

git lfs install

git lfs track "*.bin"  

git lfs track "*.so"

git lfs track "*.jar"

cat .gitattributes

git add .

git commit -m "Initial commit with LFS support"

git remote add origin https://github.com/RomanKryvolapov/Local_AI_Launcher.git

git push -u origin master