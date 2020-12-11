package com.twilio.guardrail.generators.Python

import com.twilio.guardrail.Target
import com.twilio.guardrail.generators
import com.twilio.guardrail.generators.Python.{ RequestsClientGenerator, RequestsGenerator }
import com.twilio.guardrail.generators.collections.PythonCollectionsGenerator.PythonCollectionsInterp
import com.twilio.guardrail.generators.collections.ScalaCollectionsGenerator
import com.twilio.guardrail.generators.{ Framework, PythonGenerator, SwaggerGenerator }
import com.twilio.guardrail.languages.PythonLanguage
import com.twilio.guardrail.protocol.terms.client.ClientTerms
import com.twilio.guardrail.protocol.terms.protocol._
import com.twilio.guardrail.protocol.terms.server.ServerTerms
import com.twilio.guardrail.terms.framework.FrameworkTerms
import com.twilio.guardrail.terms.{ CollectionsLibTerms, LanguageTerms, SwaggerTerms }

object Requests extends Framework[PythonLanguage, Target] {
  override implicit def ArrayProtocolInterp: ArrayProtocolTerms[PythonLanguage, Target]     = DecoderProtocolGenerator.ArrayProtocolTermInterp
  override implicit def ClientInterp: ClientTerms[PythonLanguage, Target]                   = RequestsClientGenerator.ClientTermInterp
  override implicit def EnumProtocolInterp: EnumProtocolTerms[PythonLanguage, Target]       = DecoderProtocolGenerator.EnumProtocolTermInterp
  override implicit def FrameworkInterp: FrameworkTerms[PythonLanguage, Target]             = RequestsGenerator.FrameworkInterp
  override implicit def ModelProtocolInterp: ModelProtocolTerms[PythonLanguage, Target]     = DecoderProtocolGenerator.ModelProtocolTermInterp
  override implicit def PolyProtocolInterp: PolyProtocolTerms[PythonLanguage, Target]       = DecoderProtocolGenerator.PolyProtocolTermInterp
  override implicit def ProtocolSupportInterp: ProtocolSupportTerms[PythonLanguage, Target] = DecoderProtocolGenerator.ProtocolSupportTermInterp
  override implicit def ServerInterp: ServerTerms[PythonLanguage, Target]                   = RequestsServerGenerator.ServerTermInterp
  override implicit def SwaggerInterp: SwaggerTerms[PythonLanguage, Target]                 = SwaggerGenerator[PythonLanguage]()
  override implicit def LanguageInterp: LanguageTerms[PythonLanguage, Target]               = PythonGenerator.PythonInterp
  override implicit def CollectionsLibInterp: CollectionsLibTerms[PythonLanguage, Target]   = PythonCollectionsInterp
}
