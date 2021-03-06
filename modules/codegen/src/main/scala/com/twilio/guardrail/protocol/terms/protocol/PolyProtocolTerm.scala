package com.twilio.guardrail.protocol.terms.protocol

import cats.{ InjectK, Monad }
import cats.arrow.FunctionK
import cats.free.Free
import com.twilio.guardrail.{ Discriminator, ProtocolParameter, StaticDefns, SuperClass }
import com.twilio.guardrail.core.Tracker
import com.twilio.guardrail.languages.LA
import io.swagger.v3.oas.models.media.{ ComposedSchema, Schema }

/**
  * Protocol for Polymorphic models
  */
sealed trait PolyProtocolTerm[L <: LA, T]

case class ExtractSuperClass[L <: LA](swagger: Tracker[ComposedSchema], definitions: List[(String, Tracker[Schema[_]])])
    extends PolyProtocolTerm[L, List[(String, Tracker[Schema[_]], List[Tracker[Schema[_]]])]]

case class RenderSealedTrait[L <: LA](
    className: String,
    params: List[ProtocolParameter[L]],
    discriminator: Discriminator[L],
    parents: List[SuperClass[L]] = Nil,
    children: List[String] = Nil
) extends PolyProtocolTerm[L, L#Trait]

case class EncodeADT[L <: LA](clsName: String, discriminator: Discriminator[L], children: List[String] = Nil)
    extends PolyProtocolTerm[L, Option[L#ValueDefinition]]

case class DecodeADT[L <: LA](clsName: String, discriminator: Discriminator[L], children: List[String] = Nil)
    extends PolyProtocolTerm[L, Option[L#ValueDefinition]]

case class RenderADTStaticDefns[L <: LA](
    clsName: String,
    discriminator: Discriminator[L],
    encoder: Option[L#ValueDefinition],
    decoder: Option[L#ValueDefinition]
) extends PolyProtocolTerm[L, StaticDefns[L]]

abstract class PolyProtocolTerms[L <: LA, F[_]] extends FunctionK[PolyProtocolTerm[L, ?], F] {
  def MonadF: Monad[F]
  def extractSuperClass(
      swagger: Tracker[ComposedSchema],
      definitions: List[(String, Tracker[Schema[_]])]
  ): F[List[(String, Tracker[Schema[_]], List[Tracker[Schema[_]]])]]
  def renderSealedTrait(
      className: String,
      params: List[ProtocolParameter[L]],
      discriminator: Discriminator[L],
      parents: List[SuperClass[L]] = Nil,
      children: List[String] = Nil
  ): F[L#Trait]
  def encodeADT(clsName: String, discriminator: Discriminator[L], children: List[String] = Nil): F[Option[L#ValueDefinition]]
  def decodeADT(clsName: String, discriminator: Discriminator[L], children: List[String] = Nil): F[Option[L#ValueDefinition]]
  def renderADTStaticDefns(
      clsName: String,
      discriminator: Discriminator[L],
      encoder: Option[L#ValueDefinition],
      decoder: Option[L#ValueDefinition]
  ): F[StaticDefns[L]]

  def copy(
      newMonadF: Monad[F] = MonadF,
      newExtractSuperClass: (Tracker[ComposedSchema], List[(String, Tracker[Schema[_]])]) => F[List[(String, Tracker[Schema[_]], List[Tracker[Schema[_]]])]] =
        extractSuperClass _,
      newRenderSealedTrait: (String, List[ProtocolParameter[L]], Discriminator[L], List[SuperClass[L]], List[String]) => F[L#Trait] = renderSealedTrait _,
      newEncodeADT: (String, Discriminator[L], List[String]) => F[Option[L#ValueDefinition]] = encodeADT _,
      newDecodeADT: (String, Discriminator[L], List[String]) => F[Option[L#ValueDefinition]] = decodeADT _,
      newRenderADTStaticDefns: (String, Discriminator[L], Option[L#ValueDefinition], Option[L#ValueDefinition]) => F[StaticDefns[L]] = renderADTStaticDefns _
  ) = new PolyProtocolTerms[L, F] {
    def MonadF                                                                                               = newMonadF
    def extractSuperClass(swagger: Tracker[ComposedSchema], definitions: List[(String, Tracker[Schema[_]])]) = newExtractSuperClass(swagger, definitions)
    def renderSealedTrait(
        className: String,
        params: List[ProtocolParameter[L]],
        discriminator: Discriminator[L],
        parents: List[SuperClass[L]] = Nil,
        children: List[String] = Nil
    )                                                                                             = newRenderSealedTrait(className, params, discriminator, parents, children)
    def encodeADT(clsName: String, discriminator: Discriminator[L], children: List[String] = Nil) = newEncodeADT(clsName, discriminator, children)
    def decodeADT(clsName: String, discriminator: Discriminator[L], children: List[String] = Nil) = newDecodeADT(clsName, discriminator, children)
    def renderADTStaticDefns(clsName: String, discriminator: Discriminator[L], encoder: Option[L#ValueDefinition], decoder: Option[L#ValueDefinition]) =
      newRenderADTStaticDefns(clsName, discriminator, encoder, decoder)
  }

  def apply[A](fa: PolyProtocolTerm[L, A]): F[A] = fa match {
    case ExtractSuperClass(swagger, definitions)                                => extractSuperClass(swagger, definitions)
    case RenderADTStaticDefns(clsName, discriminator, encoder, decoder)         => renderADTStaticDefns(clsName, discriminator, encoder, decoder)
    case DecodeADT(clsName, discriminator, children)                            => decodeADT(clsName, discriminator, children)
    case EncodeADT(clsName, discriminator, children)                            => encodeADT(clsName, discriminator, children)
    case RenderSealedTrait(className, params, discriminator, parents, children) => renderSealedTrait(className, params, discriminator, parents, children)
  }
}

object PolyProtocolTerms {
  implicit def polyProtocolTerms[L <: LA, F[_]](implicit I: InjectK[PolyProtocolTerm[L, ?], F]): PolyProtocolTerms[L, Free[F, ?]] =
    new PolyProtocolTerms[L, Free[F, ?]]() {
      def MonadF = Free.catsFreeMonadForFree
      def extractSuperClass(
          swagger: Tracker[ComposedSchema],
          definitions: List[(String, Tracker[Schema[_]])]
      ): Free[F, List[(String, Tracker[Schema[_]], List[Tracker[Schema[_]]])]] = Free.inject[PolyProtocolTerm[L, ?], F](ExtractSuperClass(swagger, definitions))
      def renderSealedTrait(
          className: String,
          params: List[ProtocolParameter[L]],
          discriminator: Discriminator[L],
          parents: List[SuperClass[L]] = Nil,
          children: List[String] = Nil
      ): Free[F, L#Trait] = Free.inject[PolyProtocolTerm[L, ?], F](RenderSealedTrait(className, params, discriminator, parents, children))
      def encodeADT(clsName: String, discriminator: Discriminator[L], children: List[String] = Nil): Free[F, Option[L#ValueDefinition]] =
        Free.inject[PolyProtocolTerm[L, ?], F](EncodeADT(clsName, discriminator, children))
      def decodeADT(clsName: String, discriminator: Discriminator[L], children: List[String] = Nil): Free[F, Option[L#ValueDefinition]] =
        Free.inject[PolyProtocolTerm[L, ?], F](DecodeADT(clsName, discriminator, children))
      def renderADTStaticDefns(
          clsName: String,
          discriminator: Discriminator[L],
          encoder: Option[L#ValueDefinition],
          decoder: Option[L#ValueDefinition]
      ): Free[F, StaticDefns[L]] = Free.inject[PolyProtocolTerm[L, ?], F](RenderADTStaticDefns(clsName, discriminator, encoder, decoder))
    }
}
