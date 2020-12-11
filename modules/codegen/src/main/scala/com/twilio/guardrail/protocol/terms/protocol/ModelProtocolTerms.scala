package com.twilio.guardrail.protocol.terms.protocol

import cats.Monad
import cats.implicits._
import com.twilio.guardrail.SwaggerUtil.ResolvedType
import com.twilio.guardrail.core.Tracker
import com.twilio.guardrail.languages.LA
import com.twilio.guardrail.terms.CollectionsLibTerms
import com.twilio.guardrail.{ ProtocolParameter, StaticDefns, SuperClass }
import io.swagger.v3.oas.models.media.{ Schema, ObjectSchema, ComposedSchema }

abstract class ModelProtocolTerms[L <: LA, F[_]](implicit Cl: CollectionsLibTerms[L, F]) {
  private val _this = this
  def MonadF: Monad[F]
  def raiseUserError[A](str: String): F[A]

  def transformProperty(
      clsName: String,
      dtoPackage: List[String],
      supportPackage: List[String],
      concreteTypes: List[PropMeta[L]]
  )(
      name: String,
      fieldName: String,
      prop: Schema[_],
      meta: ResolvedType[L],
      requirement: PropertyRequirement,
      isCustomType: Boolean,
      defaultValue: Option[L#Term]
  ): F[ProtocolParameter[L]]
  def renderDTOClass(clsName: String, supportPackage: List[String], terms: List[ProtocolParameter[L]], parents: List[SuperClass[L]] = Nil): F[L#ClassDefinition]
  def encodeModel(
      clsName: String,
      dtoPackage: List[String],
      params: List[ProtocolParameter[L]],
      parents: List[SuperClass[L]] = Nil
  ): F[Option[L#ValueDefinition]]
  def decodeModel(
      clsName: String,
      dtoPackage: List[String],
      supportPackage: List[String],
      params: List[ProtocolParameter[L]],
      parents: List[SuperClass[L]] = Nil
  ): F[Option[L#ValueDefinition]]
  def renderDTOStaticDefns(clsName: String, deps: List[L#TermName], encoder: Option[L#ValueDefinition], decoder: Option[L#ValueDefinition]): F[StaticDefns[L]]

  def extractProperties(swagger: Tracker[Schema[_]]) =
    MonadF.map(
      swagger
        .refine[F[List[(String, Tracker[Schema[_]])]]]({ case o: ObjectSchema => o })(
          m => MonadF.pure(m.downField("properties", _.getProperties).indexedCosequence.value)
        )
        .orRefine({ case c: ComposedSchema => c })({ comp =>
          val extractedProps =
            comp.downField("allOf", _.getAllOf()).indexedDistribute.flatMap(_.downField("properties", _.getProperties).indexedCosequence.value)
          MonadF.pure(extractedProps)
        })
        .orRefine({ case x: Schema[_] if Option(x.get$ref()).isDefined => x })(
          comp => raiseUserError(s"Attempted to extractProperties for a ${comp.get.getClass()}, unsure what to do here (${comp.showHistory})")
        )
        .getOrElse(MonadF.pure(List.empty))
    )(_.toList)

  def copy(
      newMonadF: Monad[F] = MonadF,
      newExtractProperties: Tracker[Schema[_]] => F[List[(String, Tracker[Schema[_]])]] = extractProperties _,
      newTransformProperty: (
          String,
          List[String],
          List[String],
          List[PropMeta[L]]
      ) => (String, String, Schema[_], ResolvedType[L], PropertyRequirement, Boolean, Option[L#Term]) => F[ProtocolParameter[L]] = transformProperty _,
      newRenderDTOClass: (String, List[String], List[ProtocolParameter[L]], List[SuperClass[L]]) => F[L#ClassDefinition] = renderDTOClass _,
      newDecodeModel: (String, List[String], List[String], List[ProtocolParameter[L]], List[SuperClass[L]]) => F[Option[L#ValueDefinition]] = decodeModel _,
      newEncodeModel: (String, List[String], List[ProtocolParameter[L]], List[SuperClass[L]]) => F[Option[L#ValueDefinition]] = encodeModel _,
      newRenderDTOStaticDefns: (String, List[L#TermName], Option[L#ValueDefinition], Option[L#ValueDefinition]) => F[StaticDefns[L]] = renderDTOStaticDefns _
  ) = new ModelProtocolTerms[L, F] {
    def MonadF                                         = newMonadF
    def raiseUserError[A](str: String): F[A]           = _this.raiseUserError(str)
    override def extractProperties(swagger: Tracker[Schema[_]]) = newExtractProperties(swagger)
    def transformProperty(
        clsName: String,
        dtoPackage: List[String],
        supportPackage: List[String],
        concreteTypes: List[PropMeta[L]]
    )(
        name: String,
        fieldName: String,
        prop: Schema[_],
        meta: ResolvedType[L],
        requirement: PropertyRequirement,
        isCustomType: Boolean,
        defaultValue: Option[L#Term]
    ) =
      newTransformProperty(clsName, dtoPackage, supportPackage, concreteTypes)(
        name,
        fieldName,
        prop,
        meta,
        requirement,
        isCustomType,
        defaultValue
      )
    def renderDTOClass(clsName: String, supportPackage: List[String], terms: List[ProtocolParameter[L]], parents: List[SuperClass[L]] = Nil) =
      newRenderDTOClass(clsName, supportPackage, terms, parents)
    def encodeModel(
        clsName: String,
        dtoPackage: List[String],
        params: List[ProtocolParameter[L]],
        parents: List[SuperClass[L]] = Nil
    ) =
      newEncodeModel(clsName, dtoPackage, params, parents)
    def decodeModel(
        clsName: String,
        dtoPackage: List[String],
        supportPackage: List[String],
        params: List[ProtocolParameter[L]],
        parents: List[SuperClass[L]] = Nil
    ) =
      newDecodeModel(clsName, dtoPackage, supportPackage, params, parents)
    def renderDTOStaticDefns(clsName: String, deps: List[L#TermName], encoder: Option[L#ValueDefinition], decoder: Option[L#ValueDefinition]) =
      newRenderDTOStaticDefns(clsName, deps, encoder, decoder)
  }
}
