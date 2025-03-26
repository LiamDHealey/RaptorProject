/* SimpleApp.scala */
import org.apache.spark.sql.SparkSession
import javax.imageio
import java.io

object SimpleApp {    
    def main(args: Array[String]): Unit = {
        // Setup
        val logFile = "/opt/spark/work-dir/RaptorProject/test.txt" // Should be some file on your system
        val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
        spark.sparkContext.setLogLevel("ERROR")

        val image = javax.imageio.ImageIO.read(new io.File("data/Test/Test1.png"))
        val imageData = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth())
        val logData = spark.sparkContext.parallelize(imageData)
        imageData.foreach(println)
        spark.stop()
    }
}