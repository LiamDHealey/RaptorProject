#!/bin/bash
echo " ======================================  Starting Comilation  ======================================"
sbt package
wait
echo " ======================================  Finished Comilation  ======================================"
echo ""
echo " ======================================  Starting Session  ======================================"
/opt/spark/bin/spark-submit \
  --class "Main" \
  --master local[4] \
  --driver-memory 5g \
  --conf "spark.driver.extraJavaOptions=-Dlog4j.configuration=custom-log4j.properties"\
  --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=custom-log4j.properties"\
  target/scala-2.12/raptor-project_2.12-1.0.jar > output.log &
echo " ======================================  Finished Session  ======================================"
less +F output.log

