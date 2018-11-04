package com.twilio.guardrail
package terms

import _root_.io.swagger.models.{ ModelImpl, Operation, Path }
import cats.InjectK
import cats.free.Free
import com.twilio.guardrail.languages
import scala.meta._

case class RouteMeta(path: String, method: HttpMethod, operation: Operation)

trait SwaggerAlgebra[Language <: languages.LanguageAbstraction] {
  sealed trait SwaggerTerm[T]
  case class ExtractOperations(paths: List[(String, Path)]) extends SwaggerTerm[List[RouteMeta]]
  case class GetClassName(operation: Operation)             extends SwaggerTerm[List[String]]

  class SwaggerTerms[F[_]](implicit I: InjectK[SwaggerTerm, F]) {
    def extractOperations(paths: List[(String, Path)]): Free[F, List[RouteMeta]] =
      Free.inject[SwaggerTerm, F](ExtractOperations(paths))
    def getClassName(operation: Operation): Free[F, List[String]] =
      Free.inject[SwaggerTerm, F](GetClassName(operation))
  }
  object SwaggerTerms {
    implicit def swaggerTerm[F[_]](implicit I: InjectK[SwaggerTerm, F]): SwaggerTerms[F] =
      new SwaggerTerms[F]
  }
}
