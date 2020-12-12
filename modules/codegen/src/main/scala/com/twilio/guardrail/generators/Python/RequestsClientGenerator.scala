package com.twilio.guardrail.generators.Python

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits._
import com.twilio.guardrail.{ RenderedClientOperation, StaticDefns, StrictProtocolElems, SupportDefinition, Target }
import com.twilio.guardrail.core.Tracker
import com.twilio.guardrail.generators.{ LanguageParameter, LanguageParameters, RawParameterName }
import com.twilio.guardrail.generators.syntax.Scala._
import com.twilio.guardrail.generators.syntax._
import com.twilio.guardrail.protocol.terms.{ ApplicationJson, ContentType, Header, MultipartFormData, Responses, TextPlain }
import com.twilio.guardrail.protocol.terms.client._
import com.twilio.guardrail.shims._
import com.twilio.guardrail.terms.{ CollectionsLibTerms, RouteMeta, SecurityScheme }
import com.twilio.guardrail.languages.PythonLanguage
import com.twilio.guardrail.languages.python
import scala.meta._
import _root_.io.swagger.v3.oas.models.PathItem.HttpMethod
import java.net.URI

object RequestsClientGenerator {
  def ClientTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]): ClientTerms[PythonLanguage, Target] =
    new ClientTermInterp

  class ClientTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target])
      extends ClientTerms[PythonLanguage, Target] {
    implicit def MonadF: Monad[Target] = Target.targetInstances

    def buildClient(clientName: String,tracingName: Option[String],serverUrls: Option[cats.data.NonEmptyList[java.net.URI]],basePath: Option[String],ctorArgs: List[List[PythonLanguage#MethodParameter]],clientCalls: List[PythonLanguage#Definition],supportDefinitions: List[PythonLanguage#Definition],tracing: Boolean): Target[cats.data.NonEmptyList[Either[PythonLanguage#Trait,PythonLanguage#ClassDefinition]]] = {
      Target.pure(NonEmptyList.one(Right(python.ClassDefn(Nil, python.TermName(clientName), clientCalls.flatten))))
    }
    def buildStaticDefns(clientName: String,tracingName: Option[String],serverUrls: Option[cats.data.NonEmptyList[java.net.URI]],ctorArgs: List[List[PythonLanguage#MethodParameter]],tracing: Boolean): Target[Option[StaticDefns[PythonLanguage]]] = {
      Target.pure(None)
    }
    def clientClsArgs(tracingName: Option[String],serverUrls: Option[cats.data.NonEmptyList[java.net.URI]],tracing: Boolean): Target[List[List[PythonLanguage#MethodParameter]]] = {
      Target.pure(Nil)
    }
    def generateClientOperation(className: List[String],responseClsName: String,tracing: Boolean,securitySchemes: Map[String,SecurityScheme[PythonLanguage]],parameters: LanguageParameters[PythonLanguage])(route: RouteMeta,methodName: String,responses: Responses[PythonLanguage]): Target[RenderedClientOperation[PythonLanguage]] =
      Target.pure(RenderedClientOperation[PythonLanguage](
        s"""def ${methodName}(...{arglists}) -> responseTypeRef:
           |  # $className: List[String],
           |  # $responseClsName: String,
           |  # $tracing: Boolean,
           |  # $securitySchemes: Map[String,SecurityScheme[PythonLanguage]],
           |  # $parameters: LanguageParameters[PythonLanguage]",
           |  return methodBody
           |
           |""".stripMargin.split("\n").toList.map(python.Stat),
        Nil
      ))

    def generateResponseDefinitions(responseClsName: String,responses: Responses[PythonLanguage],protocolElems: List[StrictProtocolElems[PythonLanguage]]): Target[List[PythonLanguage#Definition]] =
      Target.pure(Nil)
    def generateSupportDefinitions(tracing: Boolean,securitySchemes: Map[String,SecurityScheme[PythonLanguage]]): Target[List[SupportDefinition[PythonLanguage]]] = Target.pure(List.empty)
    def getExtraImports(tracing: Boolean): Target[List[PythonLanguage#Import]] = Target.pure(List.empty)
    def getImports(tracing: Boolean): Target[List[PythonLanguage#Import]] = Target.pure(List(python.Import("import requests")))
  }
}
