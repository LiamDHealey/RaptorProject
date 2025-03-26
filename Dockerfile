# Based on https://github.com/dotnet/spark/blob/main/docs/building/ubuntu-instructions.md
FROM apache/spark
USER root
RUN apt update
RUN apt install git -y
RUN git clone https://github.com/LiamDHealey/RaptorProject
CMD ["tail", "-f", "/dev/null"]