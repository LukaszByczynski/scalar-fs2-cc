import cats.Monoid
import cats.implicits._
import fs2._

object ex04_pull extends App {

  def sumOperator[F[_], A: Monoid](windowSize: Int): Pipe[F, A, A] = { in =>
    def go(stream: Stream[F, A]): Pull[F, A, Unit] = {
      stream.pull.unconsN(windowSize, allowFewer = true).flatMap {
        case Some((chunk, tailStream)) =>
          Pull.output1(chunk.foldLeft(Monoid[A].empty)(_ |+| _)) >> go(tailStream)
        case None =>
          Pull.done
      }
    }

    go(in).stream
  }

  Stream
    .range(1, 10)
    .through(sumOperator(4))
    .compile
    .toList
    .foreach(println)
}
