/* SimpleApp.scala */
import org.apache.spark.sql.SparkSession

object SimpleApp {    
    def main(args: Array[String]): Unit = {
        val logFile = "/opt/spark/work-dir/RaptorProject/spark.log" // Should be some file on your system
        val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
        spark.sparkContext.setLogLevel("ERROR")
        val logData = spark.read.textFile(logFile).cache()
        val numAs = logData.filter(line => line.contains("a")).count()
        val numBs = logData.filter(line => line.contains("b")).count()
        println(s"Lines with a: $numAs, Lines with b: $numBs")
        spark.stop()
    }
}