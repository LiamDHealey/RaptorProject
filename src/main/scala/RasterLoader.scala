import org.apache.spark.rdd.RDD
import javax.imageio.ImageIO
import java.io.File
import org.apache.spark.sql._
import Main.spark
import spark.implicits._

object RasterLoader {
    def getRasterData(): DataFrame = {
        var df : DataFrame = null

        new File("data/Test").listFiles().foreach(file => {
            val imageDf = spark.read.format("image").option("dropInvalid", true).load(file.getPath())
            if (df == null)
                df = imageDf
            else
                df = df.union(imageDf)
            
            // println(file)
            // val image = ImageIO.read(file).getRaster()
            // println("   read")
            // val width = image.getWidth()
            // val hieght = image.getHeight()
            // println(s"   W: $width H: $hieght")
            // val xOffset = Integer.parseInt(file.getName().split("n", 2)(1).split("w", 2)(0)) * width
            // val yOffset = Integer.parseInt(file.getName().split("w", 2)(1).split("_", 2)(0)) * hieght
            // println(s"   x: $xOffset y: $yOffset")


            // for ( x <- 0 until width; y <- 0 until hieght ) 
            // {
            //     ds = ds.union(Seq(new Pixel(x + xOffset, y + yOffset, image.getPixel(x, y, null.asInstanceOf[Array[Int]])(0))).toDS())
            // }

            println(s"   Loaded $file")
        })

        return df
    }
}
