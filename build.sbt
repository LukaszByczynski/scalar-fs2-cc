val examples = project
  .settings(
    name := "scalar-fs2-cc",
    version := "1.0.0",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % "1.0.4"
    )
  )
