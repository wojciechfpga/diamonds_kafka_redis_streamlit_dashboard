package com.streaming.diamonds.model

case class Diamond(
  id: String,
  carat: Double,
  cut: String,
  color: String,
  clarity: String,
  depth: Double,
  table: Double,
  price: Double,
  x: Double,
  y: Double,
  z: Double,
  soldCopies: Int,
  timestamp: Long
)