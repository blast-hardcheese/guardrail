package com.twilio.guardrail.protocol.terms.protocol

import _root_.io.swagger.models.ModelImpl
import cats.InjectK
import cats.free.Free
import com.twilio.guardrail.generators.GeneratorSettings
import com.twilio.guardrail.languages
import scala.meta._

trait EnumProtocolAlgebra[Language <: languages.LanguageAbstraction] {

  sealed trait EnumProtocolTerm[T]
  case class ExtractEnum(swagger: ModelImpl)                                        extends EnumProtocolTerm[Either[String, List[String]]]
  case class ExtractType(swagger: ModelImpl)                                        extends EnumProtocolTerm[Either[String, Type]]
  case class RenderMembers(clsName: String, elems: List[(String, Term.Name, Term)]) extends EnumProtocolTerm[Defn.Object]
  case class EncodeEnum(clsName: String)                                            extends EnumProtocolTerm[Defn.Val]
  case class DecodeEnum(clsName: String)                                            extends EnumProtocolTerm[Defn.Val]
  case class RenderClass(clsName: String, tpe: Type)                                extends EnumProtocolTerm[Defn.Class]
  case class RenderCompanion(clsName: String, members: Defn.Object, accessors: List[meta.Defn.Val], values: meta.Defn.Val, encoder: Defn.Val, decoder: Defn.Val)
      extends EnumProtocolTerm[Defn.Object]

  class EnumProtocolTerms[F[_]](implicit I: InjectK[EnumProtocolTerm, F]) {
    def extractEnum(swagger: ModelImpl): Free[F, Either[String, List[String]]] =
      Free.inject[EnumProtocolTerm, F](ExtractEnum(swagger))
    def extractType(swagger: ModelImpl): Free[F, Either[String, Type]] =
      Free.inject[EnumProtocolTerm, F](ExtractType(swagger))
    def renderMembers(clsName: String, elems: List[(String, Term.Name, Term)]): Free[F, Defn.Object] =
      Free.inject[EnumProtocolTerm, F](RenderMembers(clsName, elems))
    def encodeEnum(clsName: String): Free[F, Defn.Val] =
      Free.inject[EnumProtocolTerm, F](EncodeEnum(clsName))
    def decodeEnum(clsName: String): Free[F, Defn.Val] =
      Free.inject[EnumProtocolTerm, F](DecodeEnum(clsName))
    def renderClass(clsName: String, tpe: Type): Free[F, Defn.Class] =
      Free.inject[EnumProtocolTerm, F](RenderClass(clsName, tpe))
    def renderCompanion(clsName: String,
                        members: Defn.Object,
                        accessors: List[Defn.Val],
                        values: Defn.Val,
                        encoder: Defn.Val,
                        decoder: Defn.Val): Free[F, Defn.Object] =
      Free.inject[EnumProtocolTerm, F](RenderCompanion(clsName, members, accessors, values, encoder, decoder))
  }

  implicit def enumProtocolTerms[F[_]](implicit I: InjectK[EnumProtocolTerm, F]): EnumProtocolTerms[F] =
    new EnumProtocolTerms[F]
}
