import org.apache.spark.sql.{Dataset, functions => F}
import org.apache.spark.sql.functions._
import Main.spark
import spark.implicits._

// ----- QuadTree Cell -----
case class QuadCell(xmin: Double, xmax: Double, ymin: Double, ymax: Double, elevationSum: Double, count: Long)

object AggregateQuadTree extends ComputationMethod {

  val RESOLUTION_LEVELS = 6  

  override def compute(
      rasterData: Dataset[Pixel],
      vectorData: Dataset[Region]
  ): Dataset[Result] = {
    
    import rasterData.sparkSession.implicits._

    println("Building QuadTree...")

    // Raster Pixels to Degree scale 
    val scaledRaster = rasterData.map { p =>
      val lon = p.y * 0.000277777778
      val lat = p.x * 0.000277777778
      (lon, lat, p.elevation.toDouble)
    }.toDF("lon", "lat", "elevation")

    // Create quad cells
    val quadCells = scaledRaster.map { row =>
      val lon = row.getAs[Double]("lon")
      val lat = row.getAs[Double]("lat")
      val elevation = row.getAs[Double]("elevation")

      // Find which quad cell this pixel falls into
      val cellSize = 1.0 / math.pow(2, RESOLUTION_LEVELS)  // Degree size of one cell
      val xmin = (lon / cellSize).floor * cellSize
      val ymin = (lat / cellSize).floor * cellSize
      val xmax = xmin + cellSize
      val ymax = ymin + cellSize

      QuadCell(xmin, xmax, ymin, ymax, elevation, 1)
    }

    val aggregatedQuads = quadCells
      .groupBy("xmin", "xmax", "ymin", "ymax")
      .agg(
        sum("elevationSum").as("totalElevation"),
        sum("count").as("pixelCount")
      )
      .as[(Double, Double, Double, Double, Double, Long)]

    println("QuadTree Built.")

    val vectorWithBounds = vectorData.map { region =>
      val xs = region.points.map(_.x)
      val ys = region.points.map(_.y)
      val xmin = xs.min
      val xmax = xs.max
      val ymin = ys.min
      val ymax = ys.max
      (region.state, region.county, xmin, xmax, ymin, ymax)
    }.toDF("state", "county", "xmin", "xmax", "ymin", "ymax")

    val quadsWithAlias = aggregatedQuads.as("quad")
    val vectorsWithAlias = vectorWithBounds.as("vec")

    val joined = quadsWithAlias.crossJoin(vectorsWithAlias)
    .filter(
        $"quad.xmin" >= $"vec.xmin" && $"quad.xmax" <= $"vec.xmax" &&
        $"quad.ymin" >= $"vec.ymin" && $"quad.ymax" <= $"vec.ymax"
    )

    val aggregated = joined.groupBy($"vec.state", $"vec.county")
    .agg(
        ((sum($"quad.totalElevation") / sum($"quad.pixelCount"))).as("averageElevation")
    )
    .as[Result]

    // val bumpedResult = aggregated.map(r => 
    // Result(r.state, r.county, r.averageElevation + 128)
    // )

    aggregated
  }
}
