import java.util.UUID

import cats.Monad
import cats.effect._
import cats.implicits._
import org.http4s.dsl.Http4sDsl

object domain {
  case class Customer(id: Int, email: String)
}

object doobie_infrastructure {
  import domain._
  import doobie._
  import doobie.h2.H2Transactor
  import doobie.implicits._

  def findAll[F[_]: Monad](xa: Transactor[F]): fs2.Stream[F, Customer] = {
    sql"select * from customer".query[Customer].stream.transact(xa)
  }

  def createDatabase[F[_]: Sync](xa: Transactor[F]): F[Int] = {
    val createQuery =
      sql"create table CUSTOMER (id INTEGER NOT NULL, email VARCHAR)".update.run

    val customers = List.range(0, 100000).map(n => (n, s"${UUID.randomUUID().toString.replace("-", "")}@test.com"))

    val insertQuery = {
      val sql = "insert into CUSTOMER (id, email) values (?, ?)"
      Update[(Int, String)](sql).updateMany(customers)
    }

    createQuery.transact(xa) >> insertQuery.transact(xa)
  }

  def createTransactor[F[_]: Async: ContextShift]: F[Resource[F, H2Transactor[F]]] = {
    val transactor = for {
      blocker <- Blocker[F]
      ce      <- ExecutionContexts.cachedThreadPool[F] // our transaction EC
      xa <- H2Transactor.newH2Transactor[F](
        "jdbc:h2:mem:test1;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "test",
        "test",
        ce,
        blocker
      )
    } yield xa

    transactor.use(createDatabase[F]) >> Async[F].delay(transactor)
  }
}

object json {
  import domain._
  import io.circe.Encoder
  import io.circe.generic.semiauto._

  implicit val customerJsonEncoder: Encoder[Customer] = deriveEncoder[Customer]
}

object ex03_json extends IOApp with Http4sDsl[IO] {

  import json._
  import doobie_infrastructure._
  import io.circe.syntax._
  import org.http4s.HttpRoutes
  import org.http4s.server.blaze.BlazeBuilder

  override def run(args: List[String]): IO[ExitCode] =
    createTransactor[IO] >>= { xa =>
      val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
        case _ @GET -> Root =>
          Ok(
            fs2.Stream.emit("[") ++
              fs2.Stream
                .resource(xa)
                .flatMap(findAll[IO])
                .map(_.asJson.noSpaces)
                .intersperse(",") ++
              fs2.Stream.emit("]")
          )
      }

      BlazeBuilder[IO]
        .bindHttp(8080, "127.0.0.1")
        .mountService(service, "/")
        .serve
        .compile
        .last
        .map {
          case Some(ExitCode.Success) => ExitCode.Success
          case _                      => ExitCode.Error
        }
    }
}
