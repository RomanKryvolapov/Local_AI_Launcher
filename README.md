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



init of git with lfs help:

git init  
git lfs install
git lfs track "*.bin"  
git lfs track "*.so"
git lfs track "*.jar"
cat .gitattributes
git add .
git commit -m "Initial commit with LFS support"
git remote add origin https://github.com/repo.git
git push -u origin master