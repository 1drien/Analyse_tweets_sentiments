package example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import com.johnsnowlabs.nlp.base._
import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{RegexTokenizer, StopWordsRemover}

object TwitterJSONAnalysisApp {
  def main(args: Array[String]): Unit = {
    // Initialiser Spark
    val spark = SparkSession.builder()
      .appName("TwitterJSONAnalysis")
      .master("local[*]")
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

    // Tokeniser les textes
    val tokenizer = new RegexTokenizer()
      .setInputCol("clean_text")
      .setOutputCol("tokens")
      .setPattern("\\W") // Séparer sur les caractères non alphabétiques

    val tokenizedTweets = tokenizer.transform(cleanTweets)

    // Supprimer les mots vides (stop words)
    val remover = new StopWordsRemover()
      .setInputCol("tokens")
      .setOutputCol("filtered_tokens")

    val processedTweets = remover.transform(tokenizedTweets)

    // Afficher les résultats après traitement
    println("Tweets après prétraitement :")
    processedTweets.select("clean_text", "filtered_tokens").show(false)

    // Analyse de sentiments
    val positiveWords = Set("excellent", "bon", "super", "formidable", "positif", "heureux", "satisfait")
    val negativeWords = Set("mauvais", "terrible", "nul", "inadmissible", "négatif", "triste", "insatisfait")

    // Fonction pour attribuer un score de sentiment
    def analyzeSentiment(tokens: Seq[String]): String = {
      val positives = tokens.count(positiveWords.contains)
      val negatives = tokens.count(negativeWords.contains)

      if (positives > negatives) "positive"
      else if (negatives > positives) "negative"
      else "neutral"
    }

    // Ajouter une colonne "sentiment"
    val sentimentUdf = udf(analyzeSentiment(_: Seq[String]))
    val sentimentAnalysis = processedTweets.withColumn("sentiment", sentimentUdf($"filtered_tokens"))

    // Afficher les résultats de l'analyse de sentiments
    println("Résultats de l'analyse de sentiments :")
    sentimentAnalysis.select("clean_text", "sentiment").show(false)

    // Agréger les résultats par sentiment
    val sentimentStats = sentimentAnalysis.groupBy("sentiment")
      .count()
      .orderBy(desc("count"))

    // Afficher les statistiques
    println("Statistiques des sentiments :")
    sentimentStats.show(false)

    // Sauvegarder les résultats de sentimentAnalysis en excluant les colonnes non prises en charge
    sentimentAnalysis
      .select("clean_text", "sentiment") // Seules les colonnes supportées par CSV
      .write
      .option("header", "true")
      .csv("tweets_with_sentiments.csv")

    // Sauvegarder les statistiques
    sentimentStats.write
      .option("header", "true")
      .csv("sentiment_statistics.csv")

    // Stopper Spark
    spark.stop()
  }
}



