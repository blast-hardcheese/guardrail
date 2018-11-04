package com.twilio.guardrail.protocol.terms.protocol

import _root_.io.swagger.models.Model
import cats.InjectK
import cats.free.Free
import com.twilio.guardrail.languages
import com.twilio.guardrail.{ ProtocolElems, StrictProtocolElems }
import scala.meta._

trait ProtocolSuppportAlgebra[Language <: languages.LanguageAbstraction] {
  implicit val Language: Language

  case class PropMeta(clsName: String, tpe: Language#Type)
  sealed trait ProtocolSupportTerm[T]
  case class ExtractConcreteTypes(models: List[(String, Model)]) extends ProtocolSupportTerm[List[PropMeta[Language]]]
  case class ProtocolImports()                                   extends ProtocolSupportTerm[List[Language#Import]]
  case class PackageObjectImports()                              extends ProtocolSupportTerm[List[Language#Import]]
  case class PackageObjectContents()                             extends ProtocolSupportTerm[List[Language#Statement]]
  case class ResolveProtocolElems(elems: List[ProtocolElems])    extends ProtocolSupportTerm[List[StrictProtocolElems]]

  class ProtocolSupportTerms[F[_]](implicit I: InjectK[ProtocolSupportTerm, F]) {
    def extractConcreteTypes(models: List[(String, Model)]): Free[F, List[PropMeta[languages.ScalaLanguage]]] =
      Free.inject[ProtocolSupportTerm, F](ExtractConcreteTypes(models))
    def protocolImports(): Free[F, List[Import]] =
      Free.inject[ProtocolSupportTerm, F](ProtocolImports())
    def packageObjectImports(): Free[F, List[Import]] =
      Free.inject[ProtocolSupportTerm, F](PackageObjectImports())
    def packageObjectContents(): Free[F, List[Stat]] =
      Free.inject[ProtocolSupportTerm, F](PackageObjectContents())
    def resolveProtocolElems(elems: List[ProtocolElems]): Free[F, List[StrictProtocolElems]] =
      Free.inject[ProtocolSupportTerm, F](ResolveProtocolElems(elems))
  }
  object ProtocolSupportTerms {
    implicit def protocolSupportTerms[F[_]](implicit I: InjectK[ProtocolSupportTerm, F]): ProtocolSupportTerms[F] =
      new ProtocolSupportTerms[F]
  }
}
