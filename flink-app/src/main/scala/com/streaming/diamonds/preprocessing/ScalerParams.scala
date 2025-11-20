package com.streaming.diamonds.preprocessing

object ScalerParams {
  val features = Seq("carat", "depth", "table", "sold_copies")

  val means = Seq(
    0.8151423603564443,
    61.90004036389605,
    57.43722172198591,
    504.6796038128357
  )

  val stds = Seq(
    0.11074267564024241,
    1.1658709745263094,
    1.850780101521018,
    276.854822747068
  )
}