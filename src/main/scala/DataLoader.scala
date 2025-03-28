import org.apache.spark.rdd.RDD
import javax.imageio.ImageIO
import java.io.File
import org.apache.spark.sql._
import Main.spark
import spark.implicits._

object DataLoader {
    def getRasterData(): Dataset[Pixel] = {
        var ds = spark.emptyDataset[Pixel]

        new File("data/Test").listFiles().foreach(file => {
            val image = ImageIO.read(file)

            val width = image.getWidth()
            val hieght = image.getHeight()
            val xOffset = Integer.parseInt(file.getName().substring(9,  11)) * width
            val yOffset = Integer.parseInt(file.getName().substring(12, 15)) * hieght
            
            val data = (
                for ( x <- 0 until width; y <- 0 until hieght ) 
                yield new Pixel(x + xOffset, y + yOffset, image.getRGB(x, y))
            )

            ds = ds.union(data.toDS())
        })
        
        return ds
    }

    def getVectorData(): RDD[Int] = {
        return ???
    }
}
