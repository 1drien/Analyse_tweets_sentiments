package example

import org.apache.spark.sql.{SparkSession, functions => F}

object HelloSQL {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("DataFrame Example")
      .master("local[*]") // Utilise tous les cœurs disponibles
      .getOrCreate()

    import spark.implicits._ // Nécessaire pour utiliser les DataFrames

    // Données initiales
    val data = Seq(
      ("Adrien", 34, "New York"),
      ("Romain", 45, "San Francisco"),
      ("francois", 29, "Los Angeles")
    )

    // Création d'un DataFrame
    val df = data.toDF("name", "age", "city") // Scala déduit automatiquement que data est une séquence de tuples de type (String, Int, String).

    // Afficher le DataFrame
    println("=== Données initiales ===")
    df.show()

    // Transformation : filtrer les personnes âgées de plus de 30 ans
    val filteredDF = df.filter($"age" > 30)

    // Afficher le DataFrame filtré
    println("=== Données filtrées (âge > 30) ===")
    filteredDF.show()

    // Ajouter une colonne "age_next_year" : âge + 1
    val transformedDF = df.withColumn("age_next_year", $"age" + 1)

    // Afficher le DataFrame avec la nouvelle colonne
    println("=== Données avec transformation (âge l'année prochaine) ===")
    transformedDF.show()

    spark.stop()
  }
}

object FruitSQL {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Spark SQL Example")
      .master("local[*]") // Utilise tous les cœurs disponibles
      .getOrCreate()

    import spark.implicits._

    // Données initiales
    val data = Seq(
      ("apple", 3),
      ("banana", 6),
      ("orange", 2)
    )

    // Création d'un DataFrame
    val df = data.toDF("fruit", "quantity")

    // Enregistrer le DataFrame comme vue SQL
    df.createOrReplaceTempView("fruits")

    // Requête SQL : sélectionner les fruits avec quantité > 3
    val sqlResult = spark.sql("SELECT fruit, quantity FROM fruits WHERE quantity > 3")

    // Afficher le résultat de la requête SQL
    println("=== Résultat de la requête SQL (quantité > 3) ===")
    sqlResult.show()

    // Ajouter une transformation DataFrame : augmenter les quantités
    val transformedDF = df.withColumn("double_quantity", $"quantity" * 2)

    // Afficher les données transformées
    println("=== Données transformées (quantité doublée) ===")
    transformedDF.show()

    spark.stop()
  }
}


/* 1. HelloSQL (DataFrames)
Cet exemple montre comment :

Créer un DataFrame à partir d'une collection locale.
Appliquer des transformations comme filter pour filtrer les données.
Ajouter une nouvelle colonne calculée avec withColumn.
2. FruitSQL (Spark SQL)
Cet exemple montre comment :

Convertir des données en DataFrame.
Utiliser Spark SQL pour effectuer des requêtes SQL classiques.
Combiner Spark SQL avec des transformations DataFrame pour enrichir les données.*/