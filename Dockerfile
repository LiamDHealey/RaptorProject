# Based on https://github.com/dotnet/spark/blob/main/docs/building/ubuntu-instructions.md
FROM ubuntu:latest
USER root

# Tools
RUN apt-get update
RUN apt-get install -y wget
RUN apt-get install -y git

# Dotnet
RUN apt-get install -y dotnet-sdk-8.0
# Java
RUN apt install -y openjdk-8-jdk
# Apache Maven
RUN mkdir -p /root/bin/maven
WORKDIR /root/bin/maven
RUN wget https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz 
RUN tar -xvzf apache-maven-3.6.3-bin.tar.gz
RUN ln -s apache-maven-3.6.3 current
ENV M2_HOME=/root/bin/maven/current
ENV PATH=${M2_HOME}/bin:${PATH}
# Spark
RUN mkdir -p /root/bin/spark-3.5.5-bin-hadoop3.7
WORKDIR /root/bin/spark-3.5.5-bin-hadoop3.7
RUN wget https://dlcdn.apache.org/spark/spark-3.5.5/spark-3.5.5-bin-hadoop3.tgz
RUN tar -xvzf spark-3.5.5-bin-hadoop3.tgz
ENV SPARK_HOME=/root/bin/spark-3.5.5-bin-hadoop3.7
ENV PATH="$SPARK_HOME/bin:$PATH"



# # Enviroment
WORKDIR /root/RaptorProject
CMD ["tail", "-f", "/dev/null"]