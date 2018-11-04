package com.twilio.guardrail.languages

import cats.data.EitherK

import com.twilio.guardrail.protocol.terms.client.ClientGeneratorAlgebra
import com.twilio.guardrail.protocol.terms.protocol._
import com.twilio.guardrail.protocol.terms.server.ServerAlgebra
import com.twilio.guardrail.terms.{ CoreAlgebra, ScalaAlgebra, SwaggerAlgebra }
import com.twilio.guardrail.terms.framework.FrameworkAlgebra

class Algebras[Language <: LanguageAbstraction](val Language: Language)
    extends AliasProtocolAlgebra[Language]
    with ArrayProtocolAlgebra[Language]
    with ClientGeneratorAlgebra[Language]
    with CoreAlgebra[Language]
    with EnumProtocolAlgebra[Language]
    with FrameworkAlgebra[Language]
    with ModelProtocolAlgebra[Language]
    with PolyProtocolAlgebra[Language]
    with ProtocolSuppportAlgebra[Language]
    with ScalaAlgebra[Language]
    with ServerAlgebra[Language]
    with SwaggerAlgebra[Language] {

  type DefinitionPM[T]     = EitherK[ProtocolSupportTerm, ModelProtocolTerm, T]
  type DefinitionPME[T]    = EitherK[EnumProtocolTerm, DefinitionPM, T]
  type DefinitionPMEA[T]   = EitherK[AliasProtocolTerm, DefinitionPME, T]
  type DefinitionPMEAA[T]  = EitherK[ArrayProtocolTerm, DefinitionPMEA, T]
  type DefinitionPMEAAP[T] = EitherK[PolyProtocolTerm, DefinitionPMEAA, T]

  type ModelInterpreters[T] = DefinitionPMEAAP[T]

  type FrameworkC[T]   = EitherK[ClientTerm, ModelInterpreters, T]
  type FrameworkCS[T]  = EitherK[ServerTerm, FrameworkC, T]
  type FrameworkCSF[T] = EitherK[FrameworkTerm, FrameworkCS, T]

  type ClientServerTerms[T] = FrameworkCSF[T]

  type Parser[T] = EitherK[SwaggerTerm, ClientServerTerms, T]

  type CodegenApplication[T] = EitherK[ScalaTerm, Parser, T]
}
