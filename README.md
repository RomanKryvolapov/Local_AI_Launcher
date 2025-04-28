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

Edit mlc-package-config.json

You can use models from https://huggingface.co/

Instructions:

https://llm.mlc.ai/docs/deploy/cli.html
https://llm.mlc.ai/docs/deploy/android.html
https://llm.mlc.ai/docs/compilation/compile_models.html

Copy the huggingface repository
Make sure all the necessary files are present in config.json
Run mlc_llm package

also this commands can help:

$Env:TVM_NDK_CC="C:\Users\Roman\AppData\Local\Android\Sdk\ndk\27.0.11718014\toolchains\llvm\prebuilt\windows-x86_64\bin\aarch64-linux-android24-clang"

$Env:MLC_LLM_SOURCE_DIR="C:\ExampleProjects\mlc-llm"

$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
$env:Path="$env:JAVA_HOME\bin;$env:Path"

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