import org.apache.spark.rdd.RDD
import javax.imageio.ImageIO
import java.io.File
import org.apache.spark.sql._
import Main._
import spark.implicits._

object DataLoader {
    def getRasterData(): Dataset[Pixel] = {
        val ds = spark.emptyDataset[Pixel]

        new File("data/Raster").listFiles().foreach(file => {
            val image = ImageIO.read(file)
            val xOffset = Integer.parseInt(file.getName().substring(9,  11)) * 10812
            val yOffset = Integer.parseInt(file.getName().substring(12, 15)) * 10812
            val data = (
                (0 until image.getWidth(), 0 until image.getHeight())
                .zipped.map((x, y) => Pixel(x + xOffset, y + yOffset, image.getRGB(x, y)))
            ).toDS()
            ds.union(data)
        })
        
        return ds
    }

    def getVectorData(): RDD[Int] = {
        return ???
    }
}
