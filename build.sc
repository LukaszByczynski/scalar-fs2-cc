import mill._, scalalib._, scalafmt._

object examples extends SbtModule with ScalafmtModule {
  def scalaVersion = "2.12.10"

  override def scalacOptions = Seq(
    "-deprecation",
    "-encoding",
    "utf-8",
    "-explaintypes",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-Ypartial-unification"
  )

  override def ivyDeps = {
    val fs2Version = "1.0.4"
    val http4sVersion = "0.19.0"
    val circeVersion = "0.11.1"
    val doobieVersion = "0.6.0"

    Agg(
      ivy"co.fs2::fs2-core:$fs2Version",
      ivy"co.fs2::fs2-io:$fs2Version",
      ivy"org.tpolecat::doobie-core:$doobieVersion",
      ivy"org.tpolecat::doobie-h2:$doobieVersion",
      ivy"org.tpolecat::doobie-hikari:$doobieVersion",
      ivy"com.h2database:h2:1.4.197",
      ivy"org.http4s::http4s-blaze-server:$http4sVersion",
      ivy"org.http4s::http4s-circe:$http4sVersion",
      ivy"org.http4s::http4s-dsl:$http4sVersion",
      ivy"io.circe::circe-generic:$circeVersion",
      ivy"io.circe::circe-literal:$circeVersion",
      ivy"ch.qos.logback:logback-classic:1.2.3"
    )
  }
}
