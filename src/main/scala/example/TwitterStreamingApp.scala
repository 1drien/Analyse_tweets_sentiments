package example
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.conf.ConfigurationBuilder
import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.streaming.StreamingMessage



object TwitterStreamingApp {
  def main(args: Array[String]): Unit = {

    // Récupération des clés API depuis des variables d'environnement
    val apiKey = sys.env.getOrElse("TWITTER_API_KEY", "")
    val apiSecret = sys.env.getOrElse("TWITTER_API_SECRET", "")
    val accessToken = sys.env.getOrElse("TWITTER_ACCESS_TOKEN", "")
    val accessTokenSecret = sys.env.getOrElse("TWITTER_ACCESS_TOKEN_SECRET", "")

    // Ajouter les clés API Twitter dans les propriétés système
    System.setProperty("twitter4j.oauth.consumerKey", apiKey)
    System.setProperty("twitter4j.oauth.consumerSecret", apiSecret)
    System.setProperty("twitter4j.oauth.accessToken", accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)


    // Configuration Spark
    val conf = new SparkConf().setAppName("TwitterStreaming").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(10))

    // Créer un flux Twitter
    val stream = TwitterUtils.createStream(ssc, None)

    // Traiter les tweets (par exemple, afficher les textes des tweets)
    val tweets = stream.map(status => status.getText)
    tweets.print()

    // Démarrer le contexte Spark Streaming
    ssc.start()
    ssc.awaitTermination()
  }
}
