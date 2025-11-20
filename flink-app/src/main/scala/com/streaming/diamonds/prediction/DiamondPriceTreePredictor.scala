package com.streaming.diamonds.prediction

import com.streaming.diamonds.preprocessing.{PreprocessedFeatures, FeatureEncoder}

object DiamondPriceTreePredictor {

  def predict(pre: PreprocessedFeatures): Double = {
    val carat = pre.carat
    val sold  = pre.soldCopies

    val cut_Ideal   = pre.cutVec(FeatureEncoder.categories("cut").indexOf("Ideal"))
    val clarity_SI1 = pre.clarityVec(FeatureEncoder.categories("clarity").indexOf("SI1"))
    val clarity_I1  = pre.clarityVec(FeatureEncoder.categories("clarity").indexOf("I1"))

    if (sold <= -0.0512) {
      if (sold <= -0.8748) {
        if (carat <= 0.7211) {
          if (sold <= -1.2721) {
            if (clarity_SI1 <= 0.5) 3329.7663 else 3181.0926
          } else {
            if (cut_Ideal <= 0.5) 3432.6642 else 3549.1506
          }
        } else {
          if (sold <= -1.3696) {
            if (clarity_I1 <= 0.5) 3610.8922 else 3325.8097
          } else {
            if (clarity_I1 <= 0.5) 3847.9526 else 3604.7039
          }
        }
      } else {
        if (carat <= 0.7211) {
          if (sold <= -0.4810) {
            if (cut_Ideal <= 0.5) 3590.6170 else 3727.7835
          } else {
            if (carat <= -0.9043) 3764.8108 else 3905.1511
          }
        } else {
          if (sold <= -0.4052) {
            if (clarity_I1 <= 0.5) 4064.4810 else 3758.8693
          } else {
            if (clarity_I1 <= 0.5) 4266.4083 else 3968.6400
          }
        }
      }
    } else {
      if (sold <= 0.8373) {
        if (carat <= 0.7211) {
          if (sold <= 0.4183) {
            if (cut_Ideal <= 0.5) 3959.9623 else 4107.9529
          } else {
            if (cut_Ideal <= 0.5) 4144.3822 else 4298.7660
          }
        } else {
          if (sold <= 0.3786) {
            if (clarity_I1 <= 0.5) 4433.8769 else 4155.2033
          } else {
            if (clarity_I1 <= 0.5) 4662.4385 else 4359.7508
          }
        }
      } else { 
        if (carat <= 0.5405) {
          if (sold <= 1.3575) {
            if (clarity_SI1 <= 0.5) 4482.4836 else 4295.4623
          } else {
            if (cut_Ideal <= 0.5) 4551.2304 else 4727.5881
          }
        } else {
          if (sold <= 1.3827) {
            if (clarity_I1 <= 0.5) 4907.7736 else 4565.2808
          } else {
            if (clarity_I1 <= 0.5) 5119.9876 else 4660.5740
          }
        }
      }
    }
  }
}