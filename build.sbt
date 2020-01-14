val examples = project
  .settings(
    name := "scalar-fs2-cc",
    version := "1.0.0",
    scalaVersion := "2.12.10",
    scalacOptions := Seq(
      "-deprecation",
      "-encoding",
      "utf-8",
      "-explaintypes",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-Ypartial-unification"
    ),
    libraryDependencies ++= {
      val fs2Version = "2.1.0"
      val http4sVersion = "0.20.15"
      val circeVersion = "0.12.3"
      val doobieVersion = "0.8.6"

      Seq(
        "co.fs2" %% "fs2-core" % fs2Version,
        "co.fs2" %% "fs2-io" % fs2Version,
        "org.tpolecat" %% "doobie-core" % doobieVersion,
        "org.tpolecat" %% "doobie-h2" % doobieVersion,
        "org.tpolecat" %% "doobie-hikari" % doobieVersion,
        "com.h2database" % "h2" % "1.4.200",
        "org.http4s" %% "http4s-blaze-server" % http4sVersion,
        "org.http4s" %% "http4s-circe" % http4sVersion,
        "org.http4s" %% "http4s-dsl" % http4sVersion,
        "io.circe" %% "circe-generic" % circeVersion,
        "io.circe" %% "circe-literal" % circeVersion,
        "ch.qos.logback" % "logback-classic" % "1.2.3"
      )
    }
  )
