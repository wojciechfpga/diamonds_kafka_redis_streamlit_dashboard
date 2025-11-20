package com.streaming.diamonds.constants

object JobConstants {

  object Kafka {
    val BootstrapServers = "broker-1:19092"
    val InputTopic = "diamonds"
    val OutputTopic = "diamonds-output-full"
    val GroupId = "flink-diamonds-group"
  }

  object SchemaRegistry {
    val Url = "http://schema-registry:8081"
  }

  object FieldNames {
    val Id = "id"
    val Price = "price"
    val CalculatedScore = "calculated_score"
  }

  object FlinkOps {
    val SourceName = "kafka-source-diamonds"
    val TransformationName = "preprocess-and-predict"
    val SinkName = "kafka-sink-output"
    val JobName = "Flink Diamond Price Prediction Job"
  }
}