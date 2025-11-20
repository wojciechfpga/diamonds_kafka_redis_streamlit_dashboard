package com.streaming.diamonds.preprocessing

import com.streaming.diamonds.model.Diamond

case class PreprocessedFeatures(
  carat: Double,
  depth: Double,
  table: Double,
  soldCopies: Double,
  cutVec: Seq[Double],
  colorVec: Seq[Double],
  clarityVec: Seq[Double]
)

object DiamondPreprocessor {
  private def scale(value: Double, mean: Double, std: Double): Double =
    (value - mean) / std

  def preprocess(d: Diamond): PreprocessedFeatures = {
    import ScalerParams._
    import FeatureEncoder._

    PreprocessedFeatures(
      carat      = scale(d.carat,       means(0), stds(0)),
      depth      = scale(d.depth,       means(1), stds(1)),
      table      = scale(d.table,       means(2), stds(2)),
      soldCopies = scale(d.soldCopies.toDouble, means(3), stds(3)),
      cutVec     = oneHot(d.cut,     "cut"),
      colorVec   = oneHot(d.color,   "color"),
      clarityVec = oneHot(d.clarity, "clarity")
    )
  }
}