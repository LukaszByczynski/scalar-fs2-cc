import cats.Monoid
import cats.implicits._
import fs2._

object ex04_pull extends App {

  def sumOperator[F[_], A: Monoid](range: Int): Pipe[F, A, A] = { in =>

    def go(stream: Stream[F, A], counter: Int, sum: A): Pull[F, A, Unit] = {
      stream.pull.unconsLimit(100).flatMap {
        case Some((chunk, tailStream)) =>
          val newCounter = counter + chunk.size
          if (newCounter < range)
            go(tailStream, newCounter, chunk.foldLeft(sum)(_ |+| _))
          else {
            val n = newCounter - range
            val outputSum = chunk.take(n).foldLeft(sum)(_ |+| _)

            Pull.output1(outputSum) >> go(Stream.chunk(chunk.drop(n)) ++ tailStream, 0, Monoid[A].empty)
          }

        case None =>
          Pull.output1(sum) >> Pull.done
      }
    }

    go(in, 0, Monoid[A].empty).streamNoScope
  }

  Stream
    .range(1, 100000)
    .through(sumOperator(4))
    .compile
    .toList
    .foreach(println)
}
