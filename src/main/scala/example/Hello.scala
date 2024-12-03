package example

import org.apache.spark.sql.SparkSession

object Hello {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("RDD Example")
      .master("local[*]") // Utilise tous les cœurs disponibles en local
      .getOrCreate()

    val data = Seq(1, 2, 3, 4, 5)
    val rdd = spark.sparkContext.parallelize(data)
    val transformedRDD = rdd.map(x => x * 2)
    val result = transformedRDD.collect()

    println("Résultats transformés : " + result.mkString(", "))

    spark.stop()

  // Méthode greeting pour satisfaire le test
  def greeting: String = "hello"
  }
}

object Fruit {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder // Initialise session Spark
      .appName("Fruit Example") // Nom de l'application
      .master("local[*]") // Utilise tous les cœurs disponibles
      .getOrCreate() // Récupère ou crée une session Spark

    val data = Seq(("apple", 3), ("banana", 2), ("orange", 1)) // Données initiales
    val rdd = spark.sparkContext.parallelize(data) // Conversion en RDD

    // Transformation : multiplier les quantités par 2
    val transformedRDD = rdd.map { case (fruit, count) => (fruit, count * 2) }

    // Filtrage : garder les fruits avec une quantité > 4
    val filteredRDD = transformedRDD.filter { case (fruit, count) => count > 4 }

    // Affichage des statistiques
    println("Nombre de partitions : " + rdd.getNumPartitions) // Nombre de partitions
    println("Nombre total d'éléments : " + rdd.count()) // Nombre total d'éléments

    // Résultats après transformation
    val transformedResult = transformedRDD.collect()
    println("Résultats transformés : " + transformedResult.mkString(", "))

    // Résultats après filtrage
    val filteredResult = filteredRDD.collect()
    println("Résultats filtrés : " + filteredResult.mkString(", "))

    spark.stop()
  }
}
