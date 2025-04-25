import org.apache.spark.rdd.RDD
import javax.imageio.ImageIO
import java.io.File
import org.apache.spark.sql._
import Main.spark
import spark.implicits._

final case class Pixel(x: Int, y: Int, elevation: Byte)

object RasterLoader {
    def getRasterData(): Dataset[Pixel] = {
        var ds : Dataset[Pixel] = spark.emptyDataset[Pixel]

        new File("data/Raster").listFiles().foreach(file => {
            // val xOffset = Integer.parseInt(file.getName().split("n", 2)(1).split("w", 2)(0))
            // val yOffset = Integer.parseInt(file.getName().split("w", 2)(1).split("_", 2)(0))
			val fileName = file.getName
			val xOffset = """n(\d+)""".r.findFirstMatchIn(fileName).map(_.group(1).toInt).getOrElse(0)
			val yOffset = """w(\d+)""".r.findFirstMatchIn(fileName).map(_.group(1).toInt).map(v => -v).getOrElse(0)
            val imageDf = spark.read.format("image").option("dropInvalid", true).load(file.getPath())

            val imageDS = imageDf
                .flatMap(r => {
                    val image = r.getAs[Row]("image")
                    val data = image.getAs[Array[Byte]]("data")
                    val width = image.getAs[Int]("width")
                    val height = image.getAs[Int]("height")
                    data.zipWithIndex.map(bi => new Pixel(
                        xOffset * width + bi._2 % width,
                        yOffset * height + bi._2 / width,
                        bi._1
                    ))
                })


            ds = ds.union(imageDS)            
            
            println(s"   Loaded $file")
        })

        return ds
    }
}
