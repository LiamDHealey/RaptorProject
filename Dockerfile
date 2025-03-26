# Based on https://github.com/dotnet/spark/blob/main/docs/building/ubuntu-instructions.md
FROM apache/spark
USER root
RUN apt update
RUN apt install git -y
RUN apt install wget -y
RUN apt-get update
RUN apt-get install apt-transport-https curl gnupg -yqq
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
RUN chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
RUN apt-get update
RUN apt-get install sbt -y
RUN git clone https://github.com/LiamDHealey/RaptorProject
WORKDIR /opt/spark/work-dir/RaptorProject
RUN chmod +x run.sh
RUN touch spark.log
CMD ["tail", "-f", "/dev/null"]