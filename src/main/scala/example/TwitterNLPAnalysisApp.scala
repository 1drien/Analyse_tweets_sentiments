package example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import com.johnsnowlabs.nlp.base._
import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.nlp.embeddings.BertSentenceEmbeddings // Import direct pour éviter l'ambiguïté
import org.apache.spark.ml.Pipeline

object TwitterNLPAnalysisApp {
  def main(args: Array[String]): Unit = {

     System.setProperty("spark.jsl.cache.path", "/mnt/h/spark_nlp_cache")
    // Initialiser Spark

    val spark = SparkSession.builder()
      .appName("TwitterNLPAnalysis")
      .master("local[*]")
      .config("spark.jars.packages", "com.johnsnowlabs.nlp:spark-nlp_2.12:4.4.0")
      .getOrCreate()

    import spark.implicits._

    // Charger le fichier JSON
    val tweetsDF = spark.read.json("tweets.json")

    // Extraire le texte des tweets
    val tweetsText = tweetsDF.selectExpr("explode(data) as tweet")
      .selectExpr("tweet.text as text")

    // Nettoyer les tweets : supprimer URLs, mentions, hashtags, etc.
    val cleanTweets = tweetsText.withColumn(
      "clean_text",
      regexp_replace($"text", "(http\\S+|@\\w+|#[\\w]+)", "").as("clean_text")
    )

    // Configurer Spark NLP pour l'analyse des sentiments
    val documentAssembler = new DocumentAssembler()
      .setInputCol("clean_text")
      .setOutputCol("document")

    val sentenceEmbeddings = BertSentenceEmbeddings
      .pretrained("labse", "xx") // Multilingue BERT
      .setInputCols(Array("document"))
      .setOutputCol("sentence_embeddings")

    val sentimentClassifier = ClassifierDLModel
      .pretrained("classifierdl_bert_sentiment", "fr") // Modèle pour le français
      .setInputCols(Array("document", "sentence_embeddings"))
      .setOutputCol("class")

    val sentimentPipeline = new Pipeline()
      .setStages(Array(documentAssembler, sentenceEmbeddings, sentimentClassifier))

    // Appliquer le pipeline NLP
    val sentimentModel = sentimentPipeline.fit(cleanTweets)
    val sentimentResult = sentimentModel.transform(cleanTweets)

    // Afficher les résultats de l'analyse de sentiments
    println("Résultats de l'analyse de sentiments :")
    sentimentResult.select("clean_text", "class.result").show(false)

    // Agréger les résultats par sentiment
    val sentimentStats = sentimentResult.selectExpr("clean_text", "class.result[0] as sentiment")
      .groupBy("sentiment")
      .count()
      .orderBy(desc("count"))

    // Afficher les statistiques des sentiments
    println("Statistiques des sentiments :")
    sentimentStats.show(false)

    // Sauvegarder les résultats
    sentimentResult.selectExpr("clean_text", "class.result[0] as sentiment")
      .write
      .option("header", "true")
      .csv("tweets_with_sentiments_classifier.csv")

    sentimentStats.write
      .option("header", "true")
      .csv("sentiment_statistics_classifier.csv")

    // Stopper Spark
    spark.stop()
  }
}

