ThisBuild / scalaVersion     := "2.12.19"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "SparkRDDProject",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.3.0",
      "org.apache.spark" %% "spark-sql" % "3.3.0",
      "org.apache.spark" %% "spark-streaming" % "3.3.0",
      "org.apache.bahir" %% "spark-streaming-twitter" % "2.4.0",
      "org.apache.hadoop" % "hadoop-client-api" % "3.3.4",
      "org.apache.hadoop" % "hadoop-client-runtime" % "3.3.4",
      "org.apache.hadoop" % "hadoop-common" % "3.3.4",
      "org.apache.spark" %% "spark-mllib" % "3.3.0",
      "com.danielasfregola" %% "twitter4s" % "7.0",
      "com.johnsnowlabs.nlp" %% "spark-nlp" % "4.4.0"
    ),
    Compile / mainClass := Some("example.TwitterJSONAnalysisApp")
  )

dependencyOverrides ++= Seq(
  "org.apache.hadoop" % "hadoop-client-api" % "3.3.4",
  "org.apache.hadoop" % "hadoop-client-runtime" % "3.3.4"
)
