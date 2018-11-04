package com.twilio.guardrail.protocol.terms.protocol

import _root_.io.swagger.models.ArrayModel
import cats.InjectK
import cats.free.Free
import com.twilio.guardrail.generators.GeneratorSettings
import com.twilio.guardrail.languages

import scala.meta._

trait ArrayProtocolAlgebra[Language <: languages.LanguageAbstraction] {

  sealed trait ArrayProtocolTerm[T]
  case class ExtractArrayType(arr: ArrayModel, concreteTypes: List[PropMeta[Language]]) extends ArrayProtocolTerm[Type]

  class ArrayProtocolTerms[F[_]](implicit I: InjectK[ArrayProtocolTerm, F]) {
    def extractArrayType(arr: ArrayModel, concreteTypes: List[PropMeta[Language]]): Free[F, Type] =
      Free.inject[ArrayProtocolTerm, F](ExtractArrayType(arr, concreteTypes))
  }

  implicit def arrayProtocolTerms[F[_]](implicit I: InjectK[ArrayProtocolTerm, F]): ArrayProtocolTerms[F] =
    new ArrayProtocolTerms[F]
}
