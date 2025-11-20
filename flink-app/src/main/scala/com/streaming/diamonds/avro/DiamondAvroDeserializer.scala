package com.streaming.diamonds.avro
import scala.collection.JavaConverters._
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import com.streaming.diamonds.model.Diamond
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.schemaregistry.client.{CachedSchemaRegistryClient, SchemaRegistryClient}
import org.apache.avro.generic.GenericRecord

class DiamondAvroDeserializer(schemaRegistryUrl: String) extends DeserializationSchema[Diamond] {

  @transient lazy val deserializer: KafkaAvroDeserializer = {
    val client: SchemaRegistryClient = new CachedSchemaRegistryClient(schemaRegistryUrl, 1000)
    val d = new KafkaAvroDeserializer(client)
    d.configure(
      Map(
        "schema.registry.url" -> schemaRegistryUrl,
        "specific.avro.reader" -> "false"
      ).asJava,
      false
    )
    d
  }

  override def deserialize(message: Array[Byte]): Diamond = {
    val record = deserializer.deserialize("diamonds", message).asInstanceOf[GenericRecord]

    def toDouble(field: String): Double = record.get(field).asInstanceOf[Number].doubleValue()
    def toInt(field: String): Int     = record.get(field).asInstanceOf[Number].intValue()
    def toLong(field: String): Long   = record.get(field).asInstanceOf[Number].longValue()

    Diamond(
      id          = record.get("id").toString,
      carat       = toDouble("carat"),
      cut         = record.get("cut").toString,
      color       = record.get("color").toString,
      clarity     = record.get("clarity").toString,
      depth       = toDouble("depth"),
      table       = toDouble("table"),
      price       = toDouble("price"),
      x           = toDouble("x"),
      y           = toDouble("y"),
      z           = toDouble("z"),
      soldCopies  = toInt("sold_copies"),
      timestamp   = toLong("timestamp")
    )
  }

  override def isEndOfStream(nextElement: Diamond): Boolean = false

  override def getProducedType: TypeInformation[Diamond] =
    TypeInformation.of(classOf[Diamond])
}