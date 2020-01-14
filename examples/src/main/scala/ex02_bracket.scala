import java.nio.file.Files

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import fs2._
import cats.Monad

object ex02_bracket extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    Monad

    Stream
      .bracket(IO(Files.createTempFile("aaaa", "bbbb")))(f => IO(f.toFile.delete()))
      .evalMap(f => IO(println(f)))
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
