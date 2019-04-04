import scala.util.Random

import fs2._
import cats.effect.IO

import scala.concurrent.duration._

object ex01_simple extends App {

  val list = Stream(1, 2, 3).compile.toList
  println(list)

  val range = Stream.eval(IO(Random.nextInt())).compile.lastOrError
  println(range.unsafeRunSync())

  val rangeRepeted = Stream.repeatEval(IO(Random.nextInt())).take(4).compile.toList
  println(rangeRepeted.unsafeRunSync())

  val composed = Stream.range(1, 4) ++ Stream("a", "b", "c")
  println(composed.compile.toList)

  val zipped = Stream("Chicago", "Berlin", "Warsaw")
    .zip(Stream("USA", "Germany", "Poland"))
    .map { case (city, country) => s"$city - $country" }
    .compile
    .toList
  println(zipped)

  val unfoldWitEvalMap = Stream
    .duration[IO]
    .take(4)
    .evalMap(i => IO(println(i)))
    // .flatMap(i => Stream.eval(IO(println(i))))
    .compile
    .drain
  unfoldWitEvalMap.unsafeRunSync()

  val concat = (Stream.range(1, 4) ++ Stream(4, 5)).compile.toList
  println(concat)
}
