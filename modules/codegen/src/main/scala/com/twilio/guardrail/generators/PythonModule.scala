package com.twilio.guardrail
package generators

import cats.data.NonEmptyList
import com.twilio.guardrail.generators.Python._
import com.twilio.guardrail.generators.Python.DecoderProtocolGenerator
import com.twilio.guardrail.generators.collections.PythonCollectionsGenerator

import com.twilio.guardrail.languages.PythonLanguage
import com.twilio.guardrail.protocol.terms.client.ClientTerms
import com.twilio.guardrail.protocol.terms.protocol._
import com.twilio.guardrail.protocol.terms.server.ServerTerms
import com.twilio.guardrail.terms.framework.FrameworkTerms
import com.twilio.guardrail.terms.{ CollectionsLibTerms, LanguageTerms, SwaggerTerms }

object PythonModule extends AbstractModule[PythonLanguage] {
  def decoder(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]): (
      ProtocolSupportTerms[PythonLanguage, Target],
      ModelProtocolTerms[PythonLanguage, Target],
      EnumProtocolTerms[PythonLanguage, Target],
      ArrayProtocolTerms[PythonLanguage, Target],
      PolyProtocolTerms[PythonLanguage, Target]
  ) = (
    DecoderProtocolGenerator.ProtocolSupportTermInterp,
    DecoderProtocolGenerator.ModelProtocolTermInterp,
    DecoderProtocolGenerator.EnumProtocolTermInterp,
    DecoderProtocolGenerator.ArrayProtocolTermInterp,
    DecoderProtocolGenerator.PolyProtocolTermInterp
  )

  def requests(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]): (
      ClientTerms[PythonLanguage, Target],
      ServerTerms[PythonLanguage, Target],
      FrameworkTerms[PythonLanguage, Target]
  ) = (
    RequestsClientGenerator.ClientTermInterp,
    RequestsServerGenerator.ServerTermInterp,
    RequestsGenerator.FrameworkInterp
  )

  def extract(modules: NonEmptyList[String]): Target[Framework[PythonLanguage, Target]] = {
    implicit val collections = PythonCollectionsGenerator.PythonCollectionsInterp
    (for {
      (protocol, model, enum, array, poly) <- popModule[(
        ProtocolSupportTerms[PythonLanguage, Target],
        ModelProtocolTerms[PythonLanguage, Target],
        EnumProtocolTerms[PythonLanguage, Target],
        ArrayProtocolTerms[PythonLanguage, Target],
        PolyProtocolTerms[PythonLanguage, Target]
      )](
        "json",
        ("decoder", ???),
      )
      (client, server, framework) <- popModule(
        "framework",
        ("requests", requests),
      )
    } yield new Framework[PythonLanguage, Target] {
      def ArrayProtocolInterp: ArrayProtocolTerms[PythonLanguage, Target]     = array
      def ClientInterp: ClientTerms[PythonLanguage, Target]                   = client
      def EnumProtocolInterp: EnumProtocolTerms[PythonLanguage, Target]       = enum
      def FrameworkInterp: FrameworkTerms[PythonLanguage, Target]             = framework
      def ModelProtocolInterp: ModelProtocolTerms[PythonLanguage, Target]     = model
      def PolyProtocolInterp: PolyProtocolTerms[PythonLanguage, Target]       = poly
      def ProtocolSupportInterp: ProtocolSupportTerms[PythonLanguage, Target] = protocol
      def ServerInterp: ServerTerms[PythonLanguage, Target]                   = server
      def SwaggerInterp: SwaggerTerms[PythonLanguage, Target]                 = SwaggerGenerator[PythonLanguage]
      def LanguageInterp: LanguageTerms[PythonLanguage, Target]               = PythonGenerator.PythonInterp
      def CollectionsLibInterp: CollectionsLibTerms[PythonLanguage, Target]   = collections
    }).runA(modules.toList.toSet)
  }
}
