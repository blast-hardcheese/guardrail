package com.twilio.guardrail.protocol.terms.protocol

import _root_.io.swagger.models.Model
import _root_.io.swagger.models.properties.Property
import _root_.io.swagger.models.{ ComposedModel, Model, ModelImpl }
import cats.InjectK
import cats.free.Free
import com.twilio.guardrail.generators.GeneratorSettings
import com.twilio.guardrail.languages
import com.twilio.guardrail.{ ProtocolParameter, SuperClass }
import scala.meta._

trait ModelProtocolAlgebra[Language <: languages.LanguageAbstraction] {

  sealed trait ModelProtocolTerm[T]
  case class ExtractProperties(swagger: Model) extends ModelProtocolTerm[List[(String, Property)]]
  case class TransformProperty(clsName: String,
                               name: String,
                               prop: Property,
                               needCamelSnakeConversion: Boolean,
                               concreteTypes: List[PropMeta[languages.ScalaLanguage]])
      extends ModelProtocolTerm[ProtocolParameter]
  case class RenderDTOClass(clsName: String, terms: List[Term.Param], parents: List[SuperClass] = Nil) extends ModelProtocolTerm[Defn.Class]
  case class EncodeModel(clsName: String, needCamelSnakeConversion: Boolean, params: List[ProtocolParameter], parents: List[SuperClass] = Nil)
      extends ModelProtocolTerm[Stat]
  case class DecodeModel(clsName: String, needCamelSnakeConversion: Boolean, params: List[ProtocolParameter], parents: List[SuperClass] = Nil)
      extends ModelProtocolTerm[Stat]
  case class RenderDTOCompanion(clsName: String, deps: List[Term.Name], encoder: Stat, decoder: Stat) extends ModelProtocolTerm[Defn.Object]

  class ModelProtocolTerms[F[_]](implicit I: InjectK[ModelProtocolTerm, F]) {
    def extractProperties(swagger: Model): Free[F, List[(String, Property)]] =
      Free.inject[ModelProtocolTerm, F](ExtractProperties(swagger))
    def transformProperty(clsName: String, needCamelSnakeConversion: Boolean, concreteTypes: List[PropMeta[languages.ScalaLanguage]])(
        name: String,
        prop: Property
    ): Free[F, ProtocolParameter] =
      Free.inject[ModelProtocolTerm, F](TransformProperty(clsName, name, prop, needCamelSnakeConversion, concreteTypes))
    def renderDTOClass(clsName: String, terms: List[Term.Param], parents: List[SuperClass] = Nil): Free[F, Defn.Class] =
      Free.inject[ModelProtocolTerm, F](RenderDTOClass(clsName, terms, parents))
    def encodeModel(clsName: String, needCamelSnakeConversion: Boolean, params: List[ProtocolParameter], parents: List[SuperClass] = Nil): Free[F, Stat] =
      Free.inject[ModelProtocolTerm, F](EncodeModel(clsName, needCamelSnakeConversion, params, parents))
    def decodeModel(clsName: String, needCamelSnakeConversion: Boolean, params: List[ProtocolParameter], parents: List[SuperClass] = Nil): Free[F, Stat] =
      Free.inject[ModelProtocolTerm, F](DecodeModel(clsName, needCamelSnakeConversion, params, parents))
    def renderDTOCompanion(clsName: String, deps: List[Term.Name], encoder: Stat, decoder: Stat): Free[F, Defn.Object] =
      Free.inject[ModelProtocolTerm, F](RenderDTOCompanion(clsName, deps, encoder, decoder))
  }

  implicit def modelProtocolTerm[F[_]](implicit I: InjectK[ModelProtocolTerm, F]): ModelProtocolTerms[F] =
    new ModelProtocolTerms[F]
}
