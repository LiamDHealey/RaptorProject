import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

//Takes vector data and converts it to raster
object Clipping extends ComputationMethod {
  override def compute(rasterData: Dataset[Pixel], vectorData: RDD[Int]): RDD[Int] = {
    return ???
  }  
}