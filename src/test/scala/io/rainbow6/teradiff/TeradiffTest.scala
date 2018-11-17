package io.rainbow6.teradiff

import org.junit.Test
import junit.framework.TestCase
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.junit.Assert._
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.Row
import wpy.graphlinker.core.TeraCompare;

class TeradiffTest extends TestCase {

  @Test
  def test(): Unit = {
    println("start")
    val conf = new SparkConf().setAppName("TeraDiff").setMaster("local")
    val spark = SparkSession.builder.config(conf).getOrCreate()

    val schema = new StructType()
      .add(StructField("id", StringType, true))
      .add(StructField("col1", StringType, true))
      .add(StructField("col2", StringType, true))
      .add(StructField("col3", StringType, true))

    val df1 = spark.read.format("com.databricks.spark.csv")
      .option("header", false)
      .schema(schema)
      .load("src/test/resources/data1.txt")

    val df2 = spark.read.format("com.databricks.spark.csv")
      .option("header", false)
      .schema(schema)
      .load("src/test/resources/data2.txt")

    val keyExpr1 = "id as key"
    val valueExpr1 = "concat_ws(',', col1, col2, col3) as value"

    val compare = new TeraCompare(spark, df1, (keyExpr1, valueExpr1), df2, (keyExpr1, valueExpr1))

    val output = compare.compare()

    output.collect().foreach(println)
  }
}