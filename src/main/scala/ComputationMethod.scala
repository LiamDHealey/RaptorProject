import org.apache.spark.sql._
import org.apache.spark.rdd.RDD

trait ComputationMethod {
  def compute(rasterData : Dataset[Pixel], vectorData : RDD[Int]): RDD[Int]
}
