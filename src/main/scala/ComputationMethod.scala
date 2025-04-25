import org.apache.spark.sql.Dataset


final case class Result(state: String, county: String, averageElevation: Double)

trait ComputationMethod {
  def compute(rasterData: Dataset[Pixel], vectorData: Dataset[Region]): Dataset[Result]
}
