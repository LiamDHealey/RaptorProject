import org.apache.spark.sql.{Dataset, functions => F}
import Main.spark

object AggregateQuadTree extends ComputationMethod {
  override def compute(
      rasterData: Dataset[Pixel],
      vectorData: Dataset[Region]
  ) = {
    import rasterData.sparkSession.implicits._

    //bounding box for each region
    val vectorWithBounds = vectorData
      .map { region =>
        val allCoords = region.coordinates.flatten.flatten
        // val xs = allCoords.grouped(2).map(_(0)).toSeq
        // val ys = allCoords.grouped(2).map(_(1)).toSeq
        val coordPairs = allCoords.grouped(2).filter(_.length == 2).toSeq
        val xs = coordPairs.map(_(0))
        val ys = coordPairs.map(_(1))
        val xmin = xs.min
        val xmax = xs.max
        val ymin = ys.min
        val ymax = ys.max
        (region.state, region.county, xmin, xmax, ymin, ymax)
      }
      .toDF("state", "county", "xmin", "xmax", "ymin", "ymax")

    //raster pixels whose (x, y) fall within region bounding box
    val joined = rasterData
      .crossJoin(vectorWithBounds)
      .filter(
        $"x" >= $"xmin" && $"x" <= $"xmax" && $"y" >= $"ymin" && $"y" <= $"ymax"
      )

    //Group by region and compute average elevation
    val result = joined
      .groupBy("state", "county")
      .agg(
        F.avg("elevation").alias("avg_elevation"),
        F.count("elevation").alias("pixel_count")
      )
      .orderBy("state", "county")

    // result.show(100, truncate = false)
    //result
    spark.emptyDataset[Result]
  }
}
