import mill._, scalalib._, scalafmt._

object examples extends SbtModule with ScalafmtModule {
  def scalaVersion = "2.12.8"

  def ivyDependencies = Agg(
    ivy"co.fs2::fs2-core:1.0.4"
  )
}
