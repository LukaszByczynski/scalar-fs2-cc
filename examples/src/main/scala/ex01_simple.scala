import fs2._

object ex01_simple extends App {

  val composed = Stream.range(1, 4) ++ Stream("a", "b", "c")
  println(composed.compile.toList)
}