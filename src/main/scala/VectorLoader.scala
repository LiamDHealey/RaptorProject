import org.apache.spark.sql.{Dataset}
import org.apache.spark.sql.functions._
import java.io.File
import Main.spark
import spark.implicits._


case class Region(
    state: String,
    county: String,
    // population: Int,
    points: Seq[Point]
)

case class Point(x: Double, y: Double)

object VectorLoader {
  def getVectorData(): Dataset[Region] = {
    println("Loading vector data…")

    val inputDir = new File("data/geojson_output")
    val files = Option(inputDir.listFiles())
      .getOrElse(Array.empty)
      .filter(_.getName.toLowerCase.endsWith(".geojson"))

    var ds = spark.emptyDataset[Region]

    files.foreach { file =>
      // println(s"Processing ${file.getName}")
      try {
        val df = spark.read
          .option("multiline", "true")
          .json(file.getPath)

        if (df.columns.contains("features")) {
          val rawDF = df
            .select(explode(col("features")).as("feature"))
            .select(
              col("feature.properties.state_name").as("state"),
              col("feature.properties.county_nam").as("county"),
              // col("feature.properties.population").cast("int").as("population"),
              // three nested transforms to cast every element to double easy peasy baby
              expr("""
                transform(
                  feature.geometry.coordinates,
                  arr1 -> transform(
                    arr1,
                    arr2 -> transform(
                      arr2,
                      x -> cast(x as double)
                    )
                  )
                )
              """).as("coordinates")
            )
            // .as[Region]
            
            // this will basically expand the 3d array of coordinates into a sequence of points
            val regionDS = rawDF.map { row =>
            val state = row.getAs[String]("state")
            val county = row.getAs[String]("county")
            val rawCoords = row.getAs[Seq[Seq[Seq[Any]]]]("coordinates")

            val points: Seq[Point] = rawCoords match {
              case null => Seq.empty
              case coords =>
                coords.flatMap { ring =>
                  ring.flatMap {
                    case Seq(x: java.lang.Number, y: java.lang.Number) =>
                      Some(Point(x.doubleValue(), y.doubleValue()))
                    case _ => None
                  }
                }
            }

            Region(state, county, points)
          }

          //   regionDS.show(2, truncate = false)
          ds = ds.union(regionDS)
        } else {
          println(s"No 'features' in ${file.getName}, skipping")
        }
      } catch {
        case e: Exception =>
          println(s"Failed on ${file.getName}: ${e.getMessage}")
      }
    }
    ds.show(10)
    println(s"Total regions loaded: ${ds.count()}")
    ds
  }
}
