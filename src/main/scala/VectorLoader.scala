import org.apache.spark.sql.{Dataset}
import org.apache.spark.sql.functions._
import java.io.File
import Main.spark
import spark.implicits._


case class Region(
    state: String,
    county: String,
    population: Int,
    coordinates: Seq[Seq[Seq[Double]]]
)

object VectorLoader {
  def getVectorData(): Dataset[Region] = {
    println("Loading vector data…")

    val inputDir = new File("data/geojson_output")
    val files = Option(inputDir.listFiles())
      .getOrElse(Array.empty)
      .filter(_.getName.toLowerCase.endsWith(".geojson"))

    var ds = spark.emptyDataset[Region]

    files.foreach { file =>
      println(s"Processing ${file.getName}")
      try {
        val df = spark.read
          .option("multiline", "true")
          .json(file.getPath)

        if (df.columns.contains("features")) {
          val regionDS = df
            .select(explode(col("features")).as("feature"))
            .select(
              col("feature.properties.state_name").as("state"),
              col("feature.properties.county_nam").as("county"),
              col("feature.properties.population").cast("int").as("population"),
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
            .as[Region]

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
    ds.show(100)
    println(s"Total regions loaded: ${ds.count()}")
    ds
  }
}
