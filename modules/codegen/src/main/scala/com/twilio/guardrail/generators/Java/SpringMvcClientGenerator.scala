package com.twilio.guardrail.generators.Java

import com.twilio.guardrail.Target
import com.twilio.guardrail.languages.JavaLanguage
import com.twilio.guardrail.protocol.terms.client._
import cats.data.NonEmptyList
import com.twilio.guardrail.generators.LanguageParameters
import com.twilio.guardrail.protocol.terms.Responses
import com.twilio.guardrail.terms.{ RouteMeta, SecurityScheme }
import com.twilio.guardrail.StrictProtocolElems
import java.net.URI

object SpringMvcClientGenerator {

  object ClientTermInterp extends ClientTerms[JavaLanguage, Target] {
    def MonadF = Target.targetInstances
    def generateClientOperation(
        className: List[String],
        tracing: Boolean,
        securitySchemes: Map[String, SecurityScheme[JavaLanguage]],
        parameters: LanguageParameters[JavaLanguage]
    )(
        route: RouteMeta,
        methodName: String,
        responses: Responses[JavaLanguage]
    ) =
      Target.raiseUserError("spring client generation is not currently supported")
    def getImports(tracing: Boolean) =
      Target.raiseUserError("spring client generation is not currently supported")
    def getExtraImports(tracing: Boolean) =
      Target.raiseUserError("spring client generation is not currently supported")
    def clientClsArgs(
        tracingName: Option[String],
        serverUrls: Option[NonEmptyList[URI]],
        tracing: Boolean
    ) =
      Target.raiseUserError("spring client generation is not currently supported")
    def generateResponseDefinitions(
        operationId: String,
        responses: Responses[JavaLanguage],
        protocolElems: List[StrictProtocolElems[JavaLanguage]]
    ) =
      Target.raiseUserError("spring client generation is not currently supported")
    def generateSupportDefinitions(
        tracing: Boolean,
        securitySchemes: Map[String, SecurityScheme[JavaLanguage]]
    ) =
      Target.raiseUserError("spring client generation is not currently supported")
    def buildStaticDefns(
        clientName: String,
        tracingName: Option[String],
        serverUrls: Option[NonEmptyList[URI]],
        ctorArgs: List[List[com.github.javaparser.ast.body.Parameter]],
        tracing: Boolean
    ) =
      Target.raiseUserError("spring client generation is not currently supported")
    def buildClient(
        clientName: String,
        tracingName: Option[String],
        serverUrls: Option[NonEmptyList[URI]],
        basePath: Option[String],
        ctorArgs: List[List[com.github.javaparser.ast.body.Parameter]],
        clientCalls: List[com.github.javaparser.ast.body.BodyDeclaration[_ <: com.github.javaparser.ast.body.BodyDeclaration[_]]],
        supportDefinitions: List[com.github.javaparser.ast.body.BodyDeclaration[_ <: com.github.javaparser.ast.body.BodyDeclaration[_]]],
        tracing: Boolean
    ) =
      Target.raiseUserError("spring client generation is not currently supported")
  }
}
