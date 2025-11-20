package com.streaming.diamonds.job

import org.apache.flink.streaming.api.scala._
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.connector.kafka.sink.{KafkaSink, KafkaRecordSerializationSchema}
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroSerializationSchema

import org.apache.avro.generic.{GenericRecord, GenericData}
import org.apache.flink.api.common.typeinfo.TypeInformation
import com.streaming.diamonds.model.Diamond
import com.streaming.diamonds.avro.{DiamondAvroDeserializer, OutputSchema}
import com.streaming.diamonds.preprocessing.DiamondPreprocessor
import com.streaming.diamonds.prediction.DiamondPriceTreePredictor

import org.apache.flink.formats.avro.typeutils.GenericRecordAvroTypeInfo

object FlinkDiamondJob {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // env.setParallelism(1) 

    val schemaRegistryUrl = "http://schema-registry:8081"
    val outputTopic = "diamonds-output-full"

    val source = KafkaSource.builder[Diamond]()
      .setBootstrapServers("broker-1:19092")
      .setTopics("diamonds")
      .setGroupId("flink-diamonds-group")
      .setStartingOffsets(OffsetsInitializer.latest())
      .setValueOnlyDeserializer(new DiamondAvroDeserializer(schemaRegistryUrl))
      .build()

    val inputStream: DataStream[Diamond] = env.fromSource(
      source,
      WatermarkStrategy.noWatermarks[Diamond](),
      "kafka-source-diamonds"
    )

    implicit val avroTypeInfo: TypeInformation[GenericRecord] = 
      new GenericRecordAvroTypeInfo(OutputSchema.DiamondScore)

    val outputStream: DataStream[GenericRecord] = inputStream
      .map { diamond =>
        val pre = DiamondPreprocessor.preprocess(diamond)
        val score = DiamondPriceTreePredictor.predict(pre)

        val record = new GenericData.Record(OutputSchema.DiamondScore)
        record.put("id", diamond.id)
        record.put("price", diamond.price)
        record.put("calculated_score", score)
        
        record.asInstanceOf[GenericRecord] 
      } 
      .name("preprocess-and-predict")

    val sink: KafkaSink[GenericRecord] = KafkaSink.builder[GenericRecord]()
      .setBootstrapServers("broker-1:19092")
      .setRecordSerializer(
        KafkaRecordSerializationSchema.builder[GenericRecord]()
          .setTopic(outputTopic)
          .setValueSerializationSchema(
            ConfluentRegistryAvroSerializationSchema.forGeneric(
              s"$outputTopic-value",
              OutputSchema.DiamondScore,
              schemaRegistryUrl
            )
          )
          .build()
      )
      .build()

    outputStream
      .sinkTo(sink)
      .name("kafka-sink-output")

    env.execute("Flink Diamond Price Prediction Job")
  }
}