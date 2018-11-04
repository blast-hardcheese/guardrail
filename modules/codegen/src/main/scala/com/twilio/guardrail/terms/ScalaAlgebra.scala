package com.twilio.guardrail
package terms

import cats.InjectK
import cats.free.Free
import com.twilio.guardrail.languages
import scala.meta._

trait ScalaAlgebra[Language <: languages.LanguageAbstraction] {
  implicit val Language: Language

  sealed trait ScalaTerm[T]
  case class RenderImplicits(pkgName: List[String],
                             frameworkImports: List[Language.Import],
                             jsonImports: List[Language.Import],
                             customImports: List[Language.Import])
      extends ScalaTerm[Source]
  case class RenderFrameworkImplicits(pkgName: List[String],
                                      frameworkImports: List[Language.Import],
                                      jsonImports: List[Language.Import],
                                      frameworkImplicits: Defn.Object)
      extends ScalaTerm[Source]

  class ScalaTerms[F[_]](implicit I: InjectK[ScalaTerm, F]) {
    def renderImplicits(pkgName: List[String],
                        frameworkImports: List[Language.Import],
                        jsonImports: List[Language.Import],
                        customImports: List[Language.Import]): Free[F, Source] =
      Free.inject[ScalaTerm, F](RenderImplicits(pkgName, frameworkImports, jsonImports, customImports))
    def renderFrameworkImplicits(pkgName: List[String],
                                 frameworkImports: List[Language.Import],
                                 jsonImports: List[Language.Import],
                                 frameworkImplicits: Defn.Object): Free[F, Source] =
      Free.inject[ScalaTerm, F](RenderFrameworkImplicits(pkgName, frameworkImports, jsonImports, frameworkImplicits))
  }
  object ScalaTerms {
    implicit def scalaTerm[F[_]](implicit I: InjectK[ScalaTerm, F]): ScalaTerms[F] = new ScalaTerms[F]
  }
}
