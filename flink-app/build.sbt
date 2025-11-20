name := "flink-app"

version := "0.1"

scalaVersion := "2.12.18"

// --- Flink version ---
val flinkVersion = "1.15.3"

// --- Confluent repo (wymagane) ---
resolvers ++= Seq(
  "Confluent" at "https://packages.confluent.io/maven/"
)

// --- Projektowe zależności ---
// --- Projektowe zależności ---
libraryDependencies ++= Seq(
  // Główne API Flinka (wymagają %% dla Scala)
  "org.apache.flink" %% "flink-scala" % flinkVersion,
  "org.apache.flink" %% "flink-streaming-scala" % flinkVersion,

  // Konektory i formaty (to są biblioteki Javy, wymagają jednego %)
  "org.apache.flink" % "flink-connector-kafka" % flinkVersion,
  "org.apache.flink" % "flink-avro" % flinkVersion,
  "org.apache.flink" % "flink-avro-confluent-registry" % flinkVersion,

  // Zależność Confluent
  "io.confluent" % "kafka-avro-serializer" % "7.4.0"
)

// --- Assembly ---
assembly / assemblyJarName := "flink-app-assembly-0.1.jar"

assembly / mainClass := Some("com.streaming.diamonds.job.FlinkDiamondJob")

assembly / assemblyShadeRules := Seq(
  ShadeRule.rename("com.google.**" -> "shaded.google.@1").inAll,
  ShadeRule.rename("org.apache.commons.**" -> "shaded.commons.@1").inAll
)

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", xs @ _*) =>
    MergeStrategy.concat
  case PathList("META-INF", xs @ _*) =>
    MergeStrategy.discard
  case PathList("module-info.class") =>
    MergeStrategy.discard
  case PathList("META-INF", "MANIFEST.MF") =>
    MergeStrategy.discard
  case _ =>
    MergeStrategy.first
}
