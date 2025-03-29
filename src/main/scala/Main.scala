import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.launcher.SparkLauncher

object Main {  
    val spark : SparkSession = SparkSession.builder.appName("Simple Application").getOrCreate()
    
    def main(args: Array[String]): Unit = {
        println("Start Loading")
        // Image data is a dataset where x & y are mesured in 1/3 arc-seconds
        val imageData = DataLoader.getRasterData()
        //val vectorData = DataLoader.getVectorData()
        println("Done Loading")
        

        //PointInPolygon.compute(imageData, vectorData).foreach(println)

        // Stop Spark
        //spark.stop()
    }
}