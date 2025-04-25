import org.apache.spark.sql.Dataset

trait ComputationMethod {
  def compute(rasterData: Dataset[Pixel], vectorData: Dataset[Region]): Dataset[_]
}
