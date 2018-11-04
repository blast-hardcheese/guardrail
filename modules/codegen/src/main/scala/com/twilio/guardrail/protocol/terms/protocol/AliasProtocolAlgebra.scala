package com.twilio.guardrail.protocol.terms.protocol

import cats.InjectK
import com.twilio.guardrail.languages

sealed trait AliasProtocolTerm[T]

trait AliasProtocolAlgebra[Language <: languages.LanguageAbstraction] {
  class AliasProtocolTerms[F[_]](implicit I: InjectK[AliasProtocolTerm, F]) {}

  implicit def aliasProtocolTerm[F[_]](implicit I: InjectK[AliasProtocolTerm, F]): AliasProtocolTerms[F] =
    new AliasProtocolTerms[F]
}
