import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.apache.spark.broadcast.Broadcast
import Main.spark
import spark.implicits._

object Clipping {

  def isInsidePolygon(point: Point, polygon: Seq[Point]): Boolean = 
  {
    var crossings = 0
    for (i <- polygon.indices) 
    {
      val a = polygon(i)
      val b = polygon((i + 1) % polygon.length)

      if (((a.y > point.y) != (b.y > point.y)) &&
          (point.x < (b.x - a.x) * (point.y - a.y) / (b.y - a.y + 1e-10) + a.x)) 
      {
        crossings += 1
      }
    }

    crossings % 2 == 1
  }

  def clipCompute(rasterData: Dataset[LLPixel], vectorData: Dataset[Region]): Dataset[Result] = 
  {
    println("Start of Clipping Compute")

    val utahOnly = vectorData.filter(r => r.state == "Utah").collect()

    val results = utahOnly.flatMap { region =>
      println(s"Starting Clipping for ${region.county}, ${region.state}")

      // Compute the bounding box for the region
      val minX = region.points.map(_.x).min
      val maxX = region.points.map(_.x).max
      val minY = region.points.map(_.y).min
      val maxY = region.points.map(_.y).max

      println(s"Region bounding box: [($minX, $minY) -> ($maxX, $maxY)]")

      // Broadcast the region's points ONCE
      val regionPoints: Broadcast[Seq[Point]] = spark.sparkContext.broadcast(region.points)

      val filteredPixels = rasterData.filter(pixel =>
        pixel.lon >= minX && pixel.lon <= maxX &&
        pixel.lat >= minY && pixel.lat <= maxY
      )

      // Map over rasterData (pixels) and check inside
      val clippedRaster = filteredPixels.map { pixel =>
        val pixelCenter = new Point(pixel.lon, pixel.lat)
        val inside = isInsidePolygon(pixelCenter, regionPoints.value)
        if (inside) 
        {
          val elevationMeters = (pixel.elevation & 0xFF) * 15.7
          elevationMeters
        } 
        else 
        {
          -1.0
        }}.filter(_ != -1.0).collect()

      if (clippedRaster.isEmpty) 
      {
        println(s"No valid pixels found for ${region.county}")
        Some(new Result(region.state, region.county, 0.0))
      } 
      else 
      {
        val sum = clippedRaster.map(_.toDouble).sum
        val avg = sum / clippedRaster.length
        println(f"Finished Clipping for ${region.county}: Average Elevation = $avg%.2f")
        Some(new Result(region.state, region.county, avg))
      }
    }

    println("Finished Clipping Compute")
    spark.createDataset(results)
  }
}

