package com.twilio.guardrail
package terms.framework

import com.twilio.guardrail.generators.GeneratorSettings
import com.twilio.guardrail.languages

import scala.meta._

import cats.InjectK
import cats.free.Free

trait FrameworkAlgebra[Language <: languages.LanguageAbstraction] {

  sealed trait FrameworkTerm[T]
  case class GetFrameworkImports(tracing: Boolean) extends FrameworkTerm[List[Language#Import]]
  case class GetFrameworkImplicits()               extends FrameworkTerm[Defn.Object]
  case class GetGeneratorSettings()                extends FrameworkTerm[GeneratorSettings]

  class FrameworkTerms[F[_]](implicit I: InjectK[FrameworkTerm, F]) {
    def getFrameworkImports(tracing: Boolean): Free[F, List[Import]] =
      Free.inject(GetFrameworkImports(tracing))
    def getFrameworkImplicits(): Free[F, Defn.Object] =
      Free.inject(GetFrameworkImplicits())
    def getGeneratorSettings(): Free[F, GeneratorSettings] =
      Free.inject(GetGeneratorSettings())
  }

  implicit def serverTerms[F[_]](implicit I: InjectK[FrameworkTerm, F]): FrameworkTerms[F] =
    new FrameworkTerms[F]
}
