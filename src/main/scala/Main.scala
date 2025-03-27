import org.apache.spark.sql._
import org.apache.spark.sql.types._

object Main {  
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
    
    def main(args: Array[String]): Unit = {
        println("Start Loading")

        // Load Data
        val imageData = DataLoader.getRasterData()
        println("Done Loading")
        
        imageData.show()
        //val vectorData = DataLoader.getVectorData()

        // Stop Spark
        spark.stop()
    }
}