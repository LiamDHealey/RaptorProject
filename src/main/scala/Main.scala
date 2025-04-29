import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.launcher.SparkLauncher

object Main {  
    val spark : SparkSession = SparkSession.builder.appName("Simple Application").getOrCreate()
    
    def main(args: Array[String]): Unit = {

        println("Start Loading")
        // Image data is a dataset where x & y are mesured in arc-seconds
        val imageData = RasterLoader.getRasterData()
        imageData.show(5, truncate = false)
        
        val total = imageData.count()
        println(s"Total number of pixels: $total")
        val vectorData = VectorLoader.getVectorData()
        vectorData.show(10)
        
        println("Done Loading")
        
        
        println("Point in Polygon")
        val PointInPolygonReult = PointInPolygon.compute(imageData.sample(0.0000001), vectorData)
        PointInPolygonReult.show(10)
        println("Point in Polygon Done")
        
        
        // println("Clipping")
        // //Need specific lat and lon values for clipping
        // //LLRasterLoader incluides those values when parsing
        // val LLimageData = LLRasterLoader.getRasterData()
        // LLimageData.show(5, truncate = false)
        // val sampledRasterData = LLimageData.sample(withReplacement = false, fraction = 0.00001) // 0.001% of pixels
        // val ClippingResult = Clipping.clipCompute(sampledRasterData, vectorData)
        // ClippingResult.show(10)
        // println("Clipping Done")


        // println("Aggregating QuadTree")
        // val AggregateQuadTreeResult = AggregateQuadTree.compute(imageData, vectorData)
        // AggregateQuadTreeResult.show(29)
        // println("QuadTree Done")




        // Stop Spark
        spark.stop()
    }
}