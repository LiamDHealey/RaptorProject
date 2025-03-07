#!/bin/bash

dotnet build > logs/build.log
wait
cp people.json ~/RaptorProject/bin/Debug/netcoreapp3.1
cd ~/RaptorProject/bin/Debug/netcoreapp3.1
spark-submit --class org.apache.spark.deploy.dotnet.DotnetRunner --master local microsoft-spark-3-1_2.12-2.1.1.jar dotnet HelloSpark.dll