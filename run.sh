#!/bin/bash

dotnet build > logs/build.log
wait
spark-submit --class org.apache.spark.deploy.dotnet.DotnetRunner --master local microsoft-spark-3-1_2.12-2.1.1.jar dotnet RaptorProject.dll