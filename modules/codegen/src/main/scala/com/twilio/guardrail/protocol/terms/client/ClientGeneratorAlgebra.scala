package com.twilio.guardrail.protocol.terms.client

import _root_.io.swagger.models.Operation
import cats.InjectK
import cats.free.Free
import com.twilio.guardrail.generators.GeneratorSettings
import com.twilio.guardrail.terms.RouteMeta
import com.twilio.guardrail.{ RenderedClientOperation, StrictProtocolElems }
import scala.meta._

trait ClientGeneratorAlgebra[Language <: languages.LanguageAbstraction] {

  sealed trait ClientTerm[T]
  case class GenerateClientOperation(className: List[String], route: RouteMeta, tracing: Boolean, protocolElems: List[StrictProtocolElems])
      extends ClientTerm[RenderedClientOperation]
  case class GetImports(tracing: Boolean)      extends ClientTerm[List[Import]]
  case class GetExtraImports(tracing: Boolean) extends ClientTerm[List[Import]]
  case class ClientClsArgs(tracingName: Option[String], schemes: List[String], host: Option[String], tracing: Boolean)
      extends ClientTerm[List[List[Term.Param]]]
  case class GenerateResponseDefinitions(operation: Operation, protocolElems: List[StrictProtocolElems]) extends ClientTerm[List[Defn]]
  case class BuildCompanion(clientName: String,
                            tracingName: Option[String],
                            schemes: List[String],
                            host: Option[String],
                            ctorArgs: List[List[Term.Param]],
                            tracing: Boolean)
      extends ClientTerm[Defn.Object]
  case class BuildClient(clientName: String,
                         tracingName: Option[String],
                         schemes: List[String],
                         host: Option[String],
                         basePath: Option[String],
                         ctorArgs: List[List[Term.Param]],
                         clientCalls: List[Defn],
                         supportDefinitions: List[Defn],
                         tracing: Boolean)
      extends ClientTerm[Defn.Class]

  class ClientTerms[F[_]](implicit I: InjectK[ClientTerm, F]) {
    def generateClientOperation(className: List[String], tracing: Boolean, protocolElems: List[StrictProtocolElems])(
        route: RouteMeta
    ): Free[F, RenderedClientOperation] =
      Free.inject[ClientTerm, F](GenerateClientOperation(className, route, tracing, protocolElems))
    def getImports(tracing: Boolean): Free[F, List[Import]] =
      Free.inject[ClientTerm, F](GetImports(tracing))
    def getExtraImports(tracing: Boolean): Free[F, List[Import]] =
      Free.inject[ClientTerm, F](GetExtraImports(tracing))
    def clientClsArgs(tracingName: Option[String], schemes: List[String], host: Option[String], tracing: Boolean): Free[F, List[List[Term.Param]]] =
      Free.inject[ClientTerm, F](ClientClsArgs(tracingName, schemes, host, tracing))
    def generateResponseDefinitions(operation: Operation, protocolElems: List[StrictProtocolElems]): Free[F, List[Defn]] =
      Free.inject[ClientTerm, F](GenerateResponseDefinitions(operation, protocolElems))
    def buildCompanion(clientName: String,
                       tracingName: Option[String],
                       schemes: List[String],
                       host: Option[String],
                       ctorArgs: List[List[Term.Param]],
                       tracing: Boolean): Free[F, Defn.Object] =
      Free.inject[ClientTerm, F](BuildCompanion(clientName, tracingName, schemes, host, ctorArgs, tracing))
    def buildClient(clientName: String,
                    tracingName: Option[String],
                    schemes: List[String],
                    host: Option[String],
                    basePath: Option[String],
                    ctorArgs: List[List[Term.Param]],
                    clientCalls: List[Defn],
                    supportDefinitions: List[Defn],
                    tracing: Boolean): Free[F, Defn.Class] =
      Free.inject[ClientTerm, F](
        BuildClient(clientName, tracingName, schemes, host, basePath, ctorArgs, clientCalls, supportDefinitions, tracing)
      )
  }

  implicit def clientTerms[F[_]](implicit I: InjectK[ClientTerm, F]): ClientTerms[F] =
    new ClientTerms[F]
}
