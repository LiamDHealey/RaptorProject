# Prerequisites
You must have Docker installed to run the code.
# Instructions
1. Open a terminal and navigate to this folder
2. Run the command `docker run --mount type=bind,src=.,dst=/opt/spark/work-dir/RaptorProject --rm -it $(docker build -q .) bash` to build the image and run the container
3. Then run `./run.sh`, which will start our program
