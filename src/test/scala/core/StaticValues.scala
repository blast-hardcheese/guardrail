package tests.core

import _root_.io.swagger.parser.SwaggerParser
import cats.instances.all._
import com.twilio.swagger._
import com.twilio.guardrail.generators.AkkaHttp
import com.twilio.guardrail.{Context, ClassDefinition, ProtocolDefinitions, ProtocolGenerator, CodegenApplication, Target}
import org.scalatest.{FunSuite, Matchers}
import scala.meta._

class StaticValuesTest extends FunSuite with Matchers {

  val swagger = s"""
    |swagger: "2.0"
    |info:
    |  title: Whatever
    |  version: 1.0.0
    |host: localhost:1234
    |definitions:
    |  Types:
    |    type: object
    |    required:
    |      - int
    |    properties:
    |      array:
    |        type: array
    |        items:
    |          type: boolean
    |      map:
    |        type: objet
    |        additionalProperties:
    |          type: boolean
    |      obj:
    |        type: object
    |      bool:
    |        type: boolean
    |        default: false
    |        x-scala-static: true
    |      string:
    |        type: string
    |        default: "foo"
    |        x-scala-static: true
    |      long:
    |        type: integer
    |        format: int64
    |        default: 5
    |        x-scala-static: true
    |      int:
    |        type: integer
    |        format: int32
    |        default: 5
    |        x-scala-static: true
    |      float:
    |        type: number
    |        format: float
    |        default: 5.231
    |        x-scala-static: true
    |      double:
    |        type: number
    |        format: double
    |        default: 5.231
    |        x-scala-static: true
    |""".stripMargin

  test("Generate no definitions") {
    val (
      ProtocolDefinitions(ClassDefinition(_, _, cls, cmp) :: Nil, _, _, _),
      _,
      _
    ) = runSwaggerSpec(swagger)(Context.empty, AkkaHttp)

    val definition = q"""
      case class Types(array: Option[IndexedSeq[Boolean]] = Option(IndexedSeq.empty), obj: Option[io.circe.Json] = None) {
        val bool: Boolean = false
        val string: String = "foo"
        val long: Long = 5L
        val int: Int = 5
        val float: Float = 5.231f
        val double: Double = 5.231d
      }
    """

    val companion = q"""
      object Types {
        implicit val encodeTypes = {
          val readOnlyKeys = Set[String]()
          Encoder.forProduct8("array", "obj", "bool", "string", "long", "int", "float", "double")( (o: Types) =>
            (o.array, o.obj, o.bool, o.string, o.long, o.int, o.float, o.double)
          ).mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
        }
        implicit val decodeTypes = Decoder.forProduct2("array", "obj")(Types.apply _)
      }
    """

    cls.structure shouldEqual definition.structure
    cmp.structure shouldEqual companion.structure
  }
}
