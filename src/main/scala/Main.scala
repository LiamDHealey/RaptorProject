import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.launcher.SparkLauncher

object Main {  
    val spark : SparkSession = SparkSession.builder.appName("Simple Application").getOrCreate()
    
    def main(args: Array[String]): Unit = {
        spark.sparkContext.setLogLevel("ERROR")
        println("")
        println("")
        println("")
        println("")
        println("")
        println(" ======================================  Starting Computation  ======================================")
        println("Start Loading")
        // Image data is a dataset where x & y are mesured in arc-seconds
        val imageData = RasterLoader.getRasterData()
        imageData.show(5, truncate = false)
        val total = imageData.count()
        println(s"Total number of pixels: $total")

        val vectorData = VectorLoader.getVectorData()
        vectorData.show(10)
        
        println("Done Loading")
        var startTime = 0L
        var duration = 0.0
        

        println("Aggregating QuadTree")
        startTime = System.nanoTime
        val AggregateQuadTreeResult = AggregateQuadTree.compute(imageData, vectorData)
        AggregateQuadTreeResult.show(29)
        duration = (System.nanoTime - startTime) / 1e9d
        println(s"QuadTree Done | Took $duration")
        



        println("Clipping")
        //Need specific lat and lon values for clipping
        //LLRasterLoader incluides those values when parsing
        val LLimageData = LLRasterLoader.getRasterData()
        LLimageData.show(5, truncate = false)

        startTime = System.nanoTime
        val clippingSampleFraction = 0.00001
        val sampledRasterData = LLimageData.sample(withReplacement = false, fraction = 0.00001) // 0.001% of pixels
        val ClippingResult = Clipping.clipCompute(sampledRasterData, vectorData)
        ClippingResult.show(50)
        duration = (System.nanoTime - startTime) / 1e9d / clippingSampleFraction
        println(s"Clipping Done | Took $duration")




        println("Point in Polygon")
        startTime = System.nanoTime
        val pointInPolySampleFraction = 0.0000001
        val pointInPolygonReult = PointInPolygon.compute(imageData.sample(pointInPolySampleFraction), vectorData)
        pointInPolygonReult.show(50)
        duration = (System.nanoTime - startTime) / 1e9d / pointInPolySampleFraction
        println(s"Point in Polygon Done | Took $duration")


        println(" ======================================  Finished Computation  ======================================")
        // Stop Spark
        spark.stop()
    }
}