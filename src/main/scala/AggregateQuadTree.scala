import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

object AggregateQuadTree extends ComputationMethod {
  override def compute(rasterData: Dataset[Pixel], vectorData: RDD[Int]): RDD[Int] = {
    return ???
  }  
}
