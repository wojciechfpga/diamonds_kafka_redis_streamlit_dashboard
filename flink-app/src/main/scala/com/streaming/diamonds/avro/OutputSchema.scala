package com.streaming.diamonds.avro

import org.apache.avro.Schema

object OutputSchema {
  val DiamondScore: Schema = new Schema.Parser().parse("""
    {
      "type": "record",
      "name": "DiamondScore",
      "fields": [
        {"name": "id", "type": "string"},
        {"name": "price", "type": "double"},
        {"name": "calculated_score", "type": "double"}
      ]
    }
  """)
}