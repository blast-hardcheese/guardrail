package com.twilio.guardrail.generators.Python

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits._
import cats.Traverse
import com.twilio.guardrail.{ CustomExtractionField, RenderedRoutes, StrictProtocolElems, SwaggerUtil, Target, TracingField, UserError }
import com.twilio.guardrail.core.Tracker
import com.twilio.guardrail.extract.{ ServerRawResponse, TracingLabel }
import com.twilio.guardrail.generators.{ LanguageParameter, LanguageParameters }
import com.twilio.guardrail.generators.syntax._
import com.twilio.guardrail.generators.operations.TracingLabelFormatter
import com.twilio.guardrail.generators.syntax.Scala._
import com.twilio.guardrail.languages.PythonLanguage
import com.twilio.guardrail.protocol.terms.{ ContentType, Header, Response, Responses }
import com.twilio.guardrail.protocol.terms.server._
import com.twilio.guardrail.shims._
import com.twilio.guardrail.terms.{ CollectionsLibTerms, RouteMeta, SecurityScheme }

import _root_.io.swagger.v3.oas.models.PathItem.HttpMethod
import _root_.io.swagger.v3.oas.models.Operation

object RequestsServerGenerator {
  def ServerTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]): ServerTerms[PythonLanguage, Target] =
    new ServerTermInterp
  class ServerTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]) extends ServerTerms[PythonLanguage, Target] {
    def MonadF: cats.Monad[com.twilio.guardrail.Target] = ???
    def buildCustomExtractionFields(operation: com.twilio.guardrail.core.Tracker[io.swagger.v3.oas.models.Operation],resourceName: List[String],customExtraction: Boolean): com.twilio.guardrail.Target[Option[com.twilio.guardrail.CustomExtractionField[com.twilio.guardrail.languages.PythonLanguage]]] = ???
    def buildTracingFields(operation: com.twilio.guardrail.core.Tracker[io.swagger.v3.oas.models.Operation],resourceName: List[String],tracing: Boolean): com.twilio.guardrail.Target[Option[com.twilio.guardrail.TracingField[com.twilio.guardrail.languages.PythonLanguage]]] = ???
    def generateResponseDefinitions(responseClsName: String,responses: com.twilio.guardrail.protocol.terms.Responses[com.twilio.guardrail.languages.PythonLanguage],protocolElems: List[com.twilio.guardrail.StrictProtocolElems[com.twilio.guardrail.languages.PythonLanguage]]): com.twilio.guardrail.Target[List[com.twilio.guardrail.languages.PythonLanguage#Definition]] = ???
    def generateRoutes(tracing: Boolean,resourceName: String,handlerName: String,basePath: Option[String],routes: List[com.twilio.guardrail.protocol.terms.server.GenerateRouteMeta[com.twilio.guardrail.languages.PythonLanguage]],protocolElems: List[com.twilio.guardrail.StrictProtocolElems[com.twilio.guardrail.languages.PythonLanguage]],securitySchemes: Map[String,com.twilio.guardrail.terms.SecurityScheme[com.twilio.guardrail.languages.PythonLanguage]]): com.twilio.guardrail.Target[com.twilio.guardrail.RenderedRoutes[com.twilio.guardrail.languages.PythonLanguage]] = ???
    def generateSupportDefinitions(tracing: Boolean,securitySchemes: Map[String,com.twilio.guardrail.terms.SecurityScheme[com.twilio.guardrail.languages.PythonLanguage]]): com.twilio.guardrail.Target[List[com.twilio.guardrail.SupportDefinition[com.twilio.guardrail.languages.PythonLanguage]]] = ???
    def getExtraImports(tracing: Boolean,supportPackage: List[String]): com.twilio.guardrail.Target[List[com.twilio.guardrail.languages.PythonLanguage#Import]] = ???
    def getExtraRouteParams(customExtraction: Boolean,tracing: Boolean): com.twilio.guardrail.Target[List[com.twilio.guardrail.languages.PythonLanguage#MethodParameter]] = ???
    def renderClass(resourceName: String,handlerName: String,annotations: List[com.twilio.guardrail.languages.PythonLanguage#Annotation],combinedRouteTerms: List[com.twilio.guardrail.languages.PythonLanguage#Statement],extraRouteParams: List[com.twilio.guardrail.languages.PythonLanguage#MethodParameter],responseDefinitions: List[com.twilio.guardrail.languages.PythonLanguage#Definition],supportDefinitions: List[com.twilio.guardrail.languages.PythonLanguage#Definition],customExtraction: Boolean): com.twilio.guardrail.Target[List[com.twilio.guardrail.languages.PythonLanguage#Definition]] = ???
    def renderHandler(handlerName: String,methodSigs: List[com.twilio.guardrail.languages.PythonLanguage#MethodDeclaration],handlerDefinitions: List[com.twilio.guardrail.languages.PythonLanguage#Statement],responseDefinitions: List[com.twilio.guardrail.languages.PythonLanguage#Definition],customExtraction: Boolean): com.twilio.guardrail.Target[com.twilio.guardrail.languages.PythonLanguage#Definition] = ???
  }
}
