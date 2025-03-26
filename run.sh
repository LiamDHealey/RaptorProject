#!/bin/bash
git pull
wait
sbt package
wait
/opt/spark/bin/spark-submit \
  --class "SimpleApp" \
  --master local[4] \
  target/scala-2.12/simple-project_2.12-1.0.jar
