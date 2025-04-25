import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import Main.spark
import spark.implicits._

object PointInPolygon extends ComputationMethod {
  override def compute(rasterData: Dataset[Pixel], vectorData: Dataset[Region]): Dataset[Result] = {
    return spark.createDataset(vectorData.collect().map(region => {
        println(s"   PointInPoly: ${region.county}, ${region.state}")
        val averageElevation = rasterData.filter(pixel => {
            true
        }).agg("elevation" -> "avg")
        .collect()(0)
        .getAs[Double](0)
        

        new Result(region.state, region.county, averageElevation)
    }))
  }  
}
