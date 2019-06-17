package core.issues

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshaller
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import org.scalatest.{ EitherValues, FunSuite, Matchers }
import scala.concurrent.{ ExecutionContext, Future }
import io.circe._
import _root_.jawn.IncompleteParseException
import cats.implicits._
import cats.data.OptionT

class Issue325Suite extends FunSuite with Matchers with EitherValues with ScalaFutures with ScalatestRouteTest {
  override implicit val patienceConfig = PatienceConfig(10 seconds, 1 second)

  test("Ensure that servers can be constructed") {
    import issues.issue325.server.akkaHttp.{ Handler, Resource }
    import issues.issue325.server.akkaHttp.definitions._
    val route = Resource.routes(new Handler {
      override def testMultipleContentTypes(
          respond: Resource.testMultipleContentTypesResponse.type
      )(foo: String, bar: Int, baz: Option[Int]): Future[Resource.testMultipleContentTypesResponse] =
        Future.successful(
          if (foo == foo && bar == 5 && baz.forall(_ == 10)) {
            respond.OK
          } else {
            respond.InternalServerError
          }
        )
    })

    Post("/test") ~> route ~> check {
      rejection match {
        case MissingFormFieldRejection("foo") => ()
      }
    }

    Post("/test")
      .withEntity(ContentType.apply(MediaTypes.`application/x-www-form-urlencoded`, () => HttpCharsets.`UTF-8`), "".getBytes) ~> route ~> check {
      rejection match {
        case MissingFormFieldRejection("foo") => ()
      }
    }

    Post("/test")
      .withEntity(ContentType.apply(MediaTypes.`application/x-www-form-urlencoded`, () => HttpCharsets.`UTF-8`), "foo=foo&bar=5".getBytes) ~> route ~> check {
      status should equal(StatusCodes.OK)
    }
  }

  test("Ensure that clients supply the correct arguments encoded in the expected way") {
    import akka.stream.scaladsl.Sink
    import issues.issue325.client.akkaHttp.{ Client, TestMultipleContentTypesResponse }
    import issues.issue325.client.akkaHttp.definitions._

    def expectResponse(p: (String, List[Multipart.FormData.BodyPart.Strict]) => Boolean)(implicit ec: ExecutionContext): HttpRequest => Future[HttpResponse] = {
      req =>
        import scala.concurrent.duration._
        for {
          sreq     <- req.toStrict(Duration(5, SECONDS))
          chunks   <- Unmarshaller.multipartFormDataUnmarshaller.apply(sreq.entity)
          elements <- chunks.parts.runFold(List.empty[Multipart.FormData.BodyPart])(_ :+ _)
          res <- elements.groupBy(_.name).toList.traverse {
            case (name, chunks) =>
              for {
                chunks <- chunks.traverse[Future, Multipart.FormData.BodyPart.Strict](_.toStrict(Duration(5, SECONDS)))
              } yield p(name, chunks)
          }
        } yield {
          if (res.forall(_ == true)) {
            HttpResponse(200)
          } else {
            HttpResponse(500)
          }
        }
    }

    Client
      .httpClient(
        expectResponse({
          case ("foo", chunks) if chunks.length == 1 && chunks.forall(_.entity.data.utf8String == "foo") => true
          case ("bar", chunks) if chunks.length == 1 && chunks.forall(_.entity.data.utf8String == "5")   => true
          case _                                                                                         => false
        }),
        "http://localhost:80"
      )
      .testMultipleContentTypes("foo", 5)
      .value
      .futureValue match {
      case Right(TestMultipleContentTypesResponse.OK) => ()
      case ex                                         => failTest(s"Unknown: ${ex}")
    }
  }
}