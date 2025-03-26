#!/bin/bash
git pull
wait
sbt package
wait
/opt/spark/bin/spark-submit \
  --class "SimpleApp" \
  --master local[4] \
  --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=custom-log4j.properties"\
  --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=custom-log4j.properties"\
  target/scala-2.12/simple-project_2.12-1.0.jar > spark.log
