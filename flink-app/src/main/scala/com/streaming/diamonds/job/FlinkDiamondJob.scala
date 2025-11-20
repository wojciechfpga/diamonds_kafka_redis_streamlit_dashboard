package com.streaming.diamonds.job

import org.apache.flink.streaming.api.scala._
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.connector.kafka.sink.{KafkaSink, KafkaRecordSerializationSchema}
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroSerializationSchema
import org.apache.flink.formats.avro.typeutils.GenericRecordAvroTypeInfo
import org.apache.avro.generic.{GenericRecord, GenericData}
import org.apache.flink.api.common.typeinfo.TypeInformation

import com.streaming.diamonds.model.Diamond
import com.streaming.diamonds.avro.{DiamondAvroDeserializer, OutputSchema}
import com.streaming.diamonds.preprocessing.DiamondPreprocessor
import com.streaming.diamonds.prediction.DiamondPriceTreePredictor

import com.streaming.diamonds.constants.JobConstants

object FlinkDiamondJob {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // env.setParallelism(1)

    val source = KafkaSource.builder[Diamond]()
      .setBootstrapServers(JobConstants.Kafka.BootstrapServers)
      .setTopics(JobConstants.Kafka.InputTopic)
      .setGroupId(JobConstants.Kafka.GroupId)
      .setStartingOffsets(OffsetsInitializer.latest())
      .setValueOnlyDeserializer(new DiamondAvroDeserializer(JobConstants.SchemaRegistry.Url))
      .build()

    val inputStream: DataStream[Diamond] = env.fromSource(
      source,
      WatermarkStrategy.noWatermarks[Diamond](),
      JobConstants.FlinkOps.SourceName
    )

    implicit val avroTypeInfo: TypeInformation[GenericRecord] = 
      new GenericRecordAvroTypeInfo(OutputSchema.DiamondScore)

    val outputStream: DataStream[GenericRecord] = inputStream
      .map { diamond =>
        val pre = DiamondPreprocessor.preprocess(diamond)
        val score = DiamondPriceTreePredictor.predict(pre)

        val record = new GenericData.Record(OutputSchema.DiamondScore)
        record.put(JobConstants.FieldNames.Id, diamond.id)
        record.put(JobConstants.FieldNames.Price, diamond.price)
        record.put(JobConstants.FieldNames.CalculatedScore, score)
        
        record.asInstanceOf[GenericRecord] 
      } 
      .name(JobConstants.FlinkOps.TransformationName)

    val sink: KafkaSink[GenericRecord] = KafkaSink.builder[GenericRecord]()
      .setBootstrapServers(JobConstants.Kafka.BootstrapServers)
      .setRecordSerializer(
        KafkaRecordSerializationSchema.builder[GenericRecord]()
          .setTopic(JobConstants.Kafka.OutputTopic)
          .setValueSerializationSchema(
            ConfluentRegistryAvroSerializationSchema.forGeneric(
              s"${JobConstants.Kafka.OutputTopic}-value", // Dynamiczne budowanie subject name
              OutputSchema.DiamondScore,
              JobConstants.SchemaRegistry.Url
            )
          )
          .build()
      )
      .build()

    outputStream
      .sinkTo(sink)
      .name(JobConstants.FlinkOps.SinkName)

    env.execute(JobConstants.FlinkOps.JobName)
  }
}