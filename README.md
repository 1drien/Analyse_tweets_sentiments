# 🐦🔍 Analyse de Sentiments de Tweets avec Scala et Apache Spark

Ce projet implémente un système d’analyse de sentiments basé sur **Scala** et **Apache Spark**, utilisant l’**API de X (Twitter)** pour collecter des tweets en temps réel ou par mot-clé, les nettoyer, les transformer et appliquer des techniques de NLP afin de déterminer leur **polarité émotionnelle** (positive, négative, neutre).

---

## 🧱 Architecture

API X (Twitter) ─▶ Ingestion Scala ─▶ Spark RDD/DataFrame ─▶ NLP & Sentiment Analysis ─▶ Résultats agrégés/exportés

---

## 📦 Fonctionnalités

- 📡 Connexion à l’API Twitter v2 (flux ou recherche)
- 🧹 Nettoyage et normalisation des tweets
- 🧠 Analyse de sentiments via Spark NLP ou lexique
- 📊 Agrégation et export des résultats
- ⚡ Traitement distribué avec Apache Spark

---

## 🧰 Prérequis

- Scala 2.12+
- Apache Spark 3.x
- sbt (Scala Build Tool)
- Clé API Twitter (v2) avec accès au tweet stream ou à la recherche

---

## ⚙️ Installation

```bash
git clone https://github.com/1drien/Analyse_tweets_sentiments.git
cd sentiment-analysis-scala-spark
sbt compile
