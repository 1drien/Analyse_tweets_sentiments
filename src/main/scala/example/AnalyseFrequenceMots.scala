import org.apache.spark.sql.SparkSession

object AnalyseFrequenceMots {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Analyse de Fréquence des Mots")
      .master("local[*]")
      .getOrCreate()

    // Code de l'exercice ici...

    spark.stop()
  }
}

object AutreExercice {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Autre Exercice")
      .master("local[*]")
      .getOrCreate()

    // Code de l'autre exercice ici...

    spark.stop()
  }
}
