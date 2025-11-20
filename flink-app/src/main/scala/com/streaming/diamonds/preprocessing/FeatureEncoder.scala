package com.streaming.diamonds.preprocessing

object FeatureEncoder {
  val categories = Map(
    "cut"     -> Seq("Fair", "Good", "Ideal", "Premium", "Very Good"),
    "color"   -> Seq("D", "E", "F", "G", "H", "I", "J"),
    "clarity" -> Seq("I1", "IF", "SI1", "SI2", "VS1", "VS2", "VVS1", "VVS2")
  )

  def oneHot(value: String, feature: String): Seq[Double] =
    categories(feature).map(cat => if (cat == value) 1.0 else 0.0)
}