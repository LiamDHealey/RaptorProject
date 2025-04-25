import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.launcher.SparkLauncher

object Main {  
    val spark : SparkSession = SparkSession.builder.appName("Simple Application").getOrCreate()
    
    def main(args: Array[String]): Unit = {

        println("Start Loading")
        // Image data is a dataset where x & y are mesured in 1/3 arc-seconds
        val imageData = RasterLoader.getRasterData()
        val vectorData = VectorLoader.getVectorData()
        println("Done Loading")


        println("Point in Polygon")
        val PointInPolygonReult = PointInPolygon.compute(imageData, vectorData)
        PointInPolygonReult.show(10)
        println("Point in Polygon Done")


        //println("Clipping")
        //val ClippingResult = Clipping.compute(imageData, vectorData)
        //ClippingResult.show(10)
        //println("Clipping Done")


        //println("Aggregating QuadTree")
        //val AggregateQuadTreeResult = AggregateQuadTree.compute(imageData, vectorData)
        //AggregateQuadTreeResult.show(10)
        //println("QuadTree Done")




        // Stop Spark
        spark.stop()
    }
}