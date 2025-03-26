using Microsoft.Spark.Sql;
using System.Linq;
using System.IO;

class Program
{
    static void Main(string[] args)
    {
        SparkSession spark = SparkSession.Builder().GetOrCreate();
        var rows = Directory.GetFiles("data/Test")
            .Select(ImageMagick.)
        DataFrame df = spark.CreateDataFrame();
    }
}
