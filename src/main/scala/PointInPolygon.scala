import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import Main.spark
import spark.implicits._

object PointInPolygon extends ComputationMethod {
  override def compute(rasterData: Dataset[Pixel], vectorData: Dataset[Region]): Dataset[Result] = {
    val utahOnly = vectorData.filter(r => r.state == "Utah")
    return spark.createDataset(utahOnly.collect().map(region => {
        println(s"   Starting PointInPoly: ${region.county}, ${region.state}")
        val averageElevation = rasterData.filter(pixel => {
            var numIntersections = 0;

            for (i <- 0 until region.points.length)
            {
                val start = region.points(i)
                val end = region.points((i + 1) % region.points.length)
                val current = new Point(pixel.x * 0.000277777778, pixel.y * 0.000277777778) // Convert arcseconds to degrees

                val betweenPointsY = current.y > Math.min(start.y, end.y) && current.y <= Math.max(start.y, end.y)
                val pastPointX = current.x <= Math.min(start.x, end.x)
                
                if (betweenPointsY && pastPointX)
                {
                    val verticleLine = start.x == end.x
                    val intersection = (current.y - start.y) * (end.x - start.x) / (end.y - start.y) + start.x;
                    if (verticleLine || current.x <= intersection) 
                        numIntersections += 1
                }
            }

            numIntersections % 2 == 1
        }).agg("elevation" -> "avg")
        .collect()(0)
        .getAs[Double](0)

        
        println(s"   Finished PointInPoly: ${region.county}, ${region.state}: $averageElevation")
        new Result(region.state, region.county, averageElevation)
    }))
  }  
}
