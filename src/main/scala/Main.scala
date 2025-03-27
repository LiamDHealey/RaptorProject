import org.apache.spark.sql._
import org.apache.spark.sql.types._

object Main {  
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()

    val rasterDataScema = StructType(List(
        StructField("x", IntegerType, false),
        StructField("y", IntegerType, false),
        StructField("elevation", IntegerType, true),
    ))
    val vectorDataScema = StructType(List(
        StructField("x", IntegerType, false),
        StructField("y", IntegerType, false),
        StructField("elevation", IntegerType, true),
    ))
    
    def main(args: Array[String]): Unit = {
        // Load Data
        val imageData = DataLoader.getRasterData()
        val vectorData = DataLoader.getVectorData()
        
        // Stop Spark
        spark.stop()
    }
}