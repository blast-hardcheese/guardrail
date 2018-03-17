package com.twilio.swagger

import _root_.io.swagger.parser.SwaggerParser
import cats.instances.all._
import com.twilio.swagger.codegen.generators.AkkaHttp
import com.twilio.swagger.codegen.{ClassDefinition,                            EnumDefinition, ProtocolGenerator, CodegenApplication, Target}
import com.twilio.swagger.codegen.{ClassDefinition, Client, Clients, Context, ClientGenerator, ProtocolGenerator, ProtocolDefinitions, RandomType, CodegenApplication, Target, Server, Servers}
import org.scalatest.{FunSuite, Matchers}
import scala.meta._

class ValueWrapperSpec extends FunSuite with Matchers {

  val swagger = s"""
    |swagger: "2.0"
    |info:
    |  title: Whatever
    |  version: 1.0.0
    |host: localhost:1234
    |schemes:
    |  - http
    |paths:
    |  /foo:
    |    get:
    |      operationId: getFoo
    |      responses:
    |        200:
    |          description: Success
    |          type: integer
    |          format: int64
    |          x-scala-wrapper: Example1
    |definitions:
    |  Example2:
    |    type: object
    |    properties:
    |      foo:
    |        type: integer
    |        format: int64
    |        x-scala-wrapper: Ex2Val
    |  Example3:
    |    type: integer
    |    format: int64
    |    x-scala-wrapper: Example3
    |  Example4:
    |    enum:
    |      - val1
    |      - val2
    |      - val3
    |    x-scala-wrapper: Example4
    |""".stripMargin

  test("Value properties should be represented in protocols") {
    val (
      ProtocolDefinitions(ClassDefinition(_, _, cls, cmp) :: RandomType(_, _) :: (_ : EnumDefinition) :: Nil, _, _, _),
      _,
      _
    ) = runSwaggerSpec(swagger)(Context.empty, AkkaHttp)

    val definition = q"""
      case class Example2(foo: Option[Ex2Val] = None)
    """
    val companion = q"""
      object Example2 {
        implicit val encodeExample2 = {
          val readOnlyKeys = Set[String]()
          Encoder.forProduct1("foo")((o: Example2) => o.foo.value).mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
        }
        implicit val decodeExample2 = Decoder.forProduct1("foo")(v0 => Example2(Ex2Val(v0)))
      }
    """

    println(cls)
    cls.structure should equal(definition.structure)
    cmp.structure should equal(companion.structure)
  }

//  test("Value properties should be represented in protocols") {
//    val (
//      ProtocolDefinitions(ClassDefinition(_, _, cls, cmp) :: RandomType(_, _) :: (_ : EnumDefinition) :: Nil, _, _, _),
//      Clients(Client(_, _, clientLines) :: Nil),
//      Servers(Server(_, _, serverLines) :: Nil)
//    ) = runSwaggerSpec(swagger)(Context.empty, AkkaHttp)
//
//    val definition = q"""
//      case class Example2(foo: Option[Long] = None)
//    """
//    val companion = q"""
//      object Example2 {
//        implicit val encodeExample2 = {
//          val readOnlyKeys = Set[String]()
//          Encoder.forProduct1("foo")( (o: Example2) => o.foo ).mapJsonObject(_.filterKeys(key => !(readOnlyKeys contains key)))
//        }
//        implicit val decodeExample2 = Decoder.forProduct1("foo")(Example2.apply _)
//      }
//    """
//
//    cls.structure should equal(definition.structure)
//    cmp.structure should equal(companion.structure)
//  }
}
