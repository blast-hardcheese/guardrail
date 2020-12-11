package com.twilio.guardrail.generators.Python

import _root_.io.swagger.v3.oas.models.media.{ Discriminator => _, _ }
import cats.Monad
import cats.data.{ NonEmptyList, NonEmptyVector }
import cats.implicits._
import com.twilio.guardrail.{
  DataVisible,
  Discriminator,
  EmptyIsEmpty,
  EmptyIsNull,
  ProtocolParameter,
  StaticDefns,
  SuperClass,
  SupportDefinition,
  SwaggerUtil,
  Target,
  UserError
}
import com.twilio.guardrail.SwaggerUtil.ResolvedType
import com.twilio.guardrail.core.Tracker
import com.twilio.guardrail.core.implicits._
import com.twilio.guardrail.extract.{ DataRedaction, EmptyValueIsNull }
import com.twilio.guardrail.generators.Scala.model.CirceModelGenerator
import com.twilio.guardrail.generators.{ RawParameterName, RawParameterType, PythonGenerator }
import com.twilio.guardrail.languages.{ PythonLanguage, python }
import com.twilio.guardrail.protocol.terms.protocol._
import com.twilio.guardrail.terms.CollectionsLibTerms
import scala.collection.JavaConverters._

object DecoderProtocolGenerator {
  def EnumProtocolTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]): EnumProtocolTerms[PythonLanguage, Target] = new EnumProtocolTermInterp
  class EnumProtocolTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]) extends EnumProtocolTerms[PythonLanguage, Target] {
    def MonadF: cats.Monad[Target] = Target.targetInstances
    def buildAccessor(clsName: String,termName: String): Target[PythonLanguage#TermSelect] = ???
    def decodeEnum(clsName: String): Target[Option[PythonLanguage#Definition]] = ???
    def encodeEnum(clsName: String): Target[Option[PythonLanguage#Definition]] = ???
    def renderClass(clsName: String,tpe: PythonLanguage#Type,elems: List[(String, PythonLanguage#TermName, PythonLanguage#TermSelect)]): Target[PythonLanguage#ClassDefinition] = ???
    def renderMembers(clsName: String,elems: List[(String, PythonLanguage#TermName, PythonLanguage#TermSelect)]): Target[Option[PythonLanguage#ObjectDefinition]] = ???
    def renderStaticDefns(clsName: String,members: Option[PythonLanguage#ObjectDefinition],accessors: List[PythonLanguage#TermName],encoder: Option[PythonLanguage#Definition],decoder: Option[PythonLanguage#Definition]): Target[com.twilio.guardrail.StaticDefns[PythonLanguage]] = ???
  }


  def ModelProtocolTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]): ModelProtocolTerms[PythonLanguage, Target] =
    new ModelProtocolTermInterp
  class ModelProtocolTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target])
      extends ModelProtocolTerms[PythonLanguage, Target] {
    def MonadF: cats.Monad[Target] = Target.targetInstances

    def lookupTypeName(tpeName: String, concreteTypes: List[PropMeta[PythonLanguage]])(f: python.Type => python.Type): Option[python.Type] =
      concreteTypes
        .find(_.clsName == tpeName)
        .map(_.tpe)
        .map(f)
    def raiseUserError[A](s: String) = Target.raiseUserError[A](s)
    def decodeModel(clsName: String,dtoPackage: List[String],supportPackage: List[String],selfParams: List[ProtocolParameter[PythonLanguage]],parents: List[SuperClass[PythonLanguage]]): Target[Option[PythonLanguage#ValueDefinition]] = {
      Target.pure(None)
    }
    def encodeModel(clsName: String, dtoPackage: List[String], selfParams: List[ProtocolParameter[PythonLanguage]], parents: List[SuperClass[PythonLanguage]]): Target[Option[PythonLanguage#ValueDefinition]] = Target.pure(None)
    def renderDTOClass(clsName: String,supportPackage: List[String],terms: List[ProtocolParameter[PythonLanguage]],parents: List[SuperClass[PythonLanguage]]): Target[PythonLanguage#ClassDefinition] = {
      Target.pure(
        python.ClassDefn(List(python.TermName("dataclass")), python.TermName(clsName),
          terms.map(param => python.Stat(s"${param.term.name.value}: ${param.term.tpe.value}"))
        )
      )
    }
    def renderDTOStaticDefns(clsName: String,deps: List[PythonLanguage#TermName],encoder: Option[PythonLanguage#ValueDefinition],decoder: Option[PythonLanguage#ValueDefinition]): Target[com.twilio.guardrail.StaticDefns[PythonLanguage]] =
      Target.pure(
        StaticDefns[PythonLanguage](
          className = clsName,
          extraImports = Nil,
          definitions = List(encoder, decoder).flatten
        )
      )
    def transformProperty(
        clsName: String,
        dtoPackage: List[String],
        supportPackage: List[String],
        concreteTypes: List[PropMeta[PythonLanguage]]
    )(
        name: String,
        fieldName: String,
        prop: Schema[_],
        meta: ResolvedType[PythonLanguage],
        requirement: PropertyRequirement,
        isCustomType: Boolean,
        defaultValue: Option[PythonLanguage#Term]
    ): Target[ProtocolParameter[PythonLanguage]] =
      Target.log.function(s"transformProperty") {
        for {
          _ <- Target.log.debug(s"Args: (${clsName}, ${name}, ...)")

          rawType = RawParameterType(Option(prop.getType), Option(prop.getFormat))

          readOnlyKey = Option(name).filter(_ => Option(prop.getReadOnly).contains(true))
          emptyToNull = (prop match {
            case d: DateSchema      => EmptyValueIsNull(d)
            case dt: DateTimeSchema => EmptyValueIsNull(dt)
            case s: StringSchema    => EmptyValueIsNull(s)
            case _                  => None
          }).getOrElse(EmptyIsEmpty)
          dataRedaction = DataRedaction(prop).getOrElse(DataVisible)

          (tpe, classDep) = meta match {
            case SwaggerUtil.Resolved(declType, classDep, _, Some(rawType), rawFormat) if SwaggerUtil.isFile(rawType, rawFormat) && !isCustomType =>
              // assume that binary data are represented as a string. allow users to override.
              (python.Type("str"), classDep)
            case SwaggerUtil.Resolved(declType, classDep, _, _, _) =>
              (declType, classDep)
            case SwaggerUtil.Deferred(tpeName) =>
              val concreteType = lookupTypeName(tpeName, concreteTypes)(identity)
              val tpe    = concreteType.getOrElse {
                println(s"Unable to find definition for ${tpeName}, just inlining")
                python.Type(tpeName)
              }
              (tpe, Option.empty)
            case SwaggerUtil.DeferredArray(tpeName, containerTpe) =>
              val concreteType = lookupTypeName(tpeName, concreteTypes)(identity)
              val innerType    = concreteType.getOrElse(python.Type(tpeName))
              (python.Type(s"${containerTpe.getOrElse(python.Type("List")).value}[$innerType]"), Option.empty)
            case SwaggerUtil.DeferredMap(tpeName, customTpe) =>
              val concreteType = lookupTypeName(tpeName, concreteTypes)(identity)
              val innerType    = concreteType.getOrElse(python.Type(tpeName))
              (python.Type(s"${customTpe.getOrElse(python.Type("Map")).value}[str, $innerType]"), Option.empty)
          }
          presence     <- PythonGenerator.PythonInterp.selectTerm(NonEmptyList.ofInitLast(supportPackage, "Presence"))
          presenceType <- PythonGenerator.PythonInterp.selectType(NonEmptyList.ofInitLast(supportPackage, "Presence"))
          (finalDeclType, finalDefaultValue) = tpe -> defaultValue
          term = python.Param(python.TermName(fieldName), finalDeclType, finalDefaultValue)
          dep  = classDep.filterNot(_.value == clsName) // Filter out our own class name
        } yield ProtocolParameter[PythonLanguage](
          term,
          tpe,
          RawParameterName(name),
          dep,
          rawType,
          readOnlyKey,
          emptyToNull,
          dataRedaction,
          requirement,
          finalDefaultValue
        )
      }
  }

  def ArrayProtocolTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]): ArrayProtocolTerms[PythonLanguage, Target] = new ArrayProtocolTermInterp
  class ArrayProtocolTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]) extends ArrayProtocolTerms[PythonLanguage, Target] {
    def MonadF: cats.Monad[Target] = ???
    def extractArrayType(arr: com.twilio.guardrail.SwaggerUtil.ResolvedType[PythonLanguage],concreteTypes: List[com.twilio.guardrail.protocol.terms.protocol.PropMeta[PythonLanguage]]): Target[PythonLanguage#Type] = ???
  }

  def ProtocolSupportTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]): ProtocolSupportTerms[PythonLanguage, Target] =
    new ProtocolSupportTermInterp
  class ProtocolSupportTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]) extends ProtocolSupportTerms[PythonLanguage, Target] {
    def MonadF: cats.Monad[Target] = ???
    def extractConcreteTypes(models: Either[String,List[com.twilio.guardrail.protocol.terms.protocol.PropMeta[PythonLanguage]]]): Target[List[com.twilio.guardrail.protocol.terms.protocol.PropMeta[PythonLanguage]]] = ???
    def generateSupportDefinitions(): Target[List[com.twilio.guardrail.SupportDefinition[PythonLanguage]]] = Target.pure(Nil)
    def implicitsObject(): Target[Option[(PythonLanguage#TermName, PythonLanguage#ObjectDefinition)]] = Target.pure(None)
    def packageObjectContents(): Target[List[PythonLanguage#Statement]] = Target.pure(Nil)
    def packageObjectImports(): Target[List[PythonLanguage#Import]] = Target.pure(Nil)
    def protocolImports(): Target[List[PythonLanguage#Import]] = Target.pure(List(python.Import("from dataclasses import dataclass")))
    def staticProtocolImports(pkgName: List[String]): Target[List[PythonLanguage#Import]] = Target.pure(Nil)
  }

  def PolyProtocolTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]): PolyProtocolTerms[PythonLanguage, Target] =
    new PolyProtocolTermInterp
  class PolyProtocolTermInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]) extends PolyProtocolTerms[PythonLanguage, Target] {
    def MonadF: cats.Monad[Target] = ???
    def decodeADT(clsName: String,discriminator: com.twilio.guardrail.Discriminator[PythonLanguage],children: List[String]): Target[Option[PythonLanguage#ValueDefinition]] = ???
    def encodeADT(clsName: String,discriminator: com.twilio.guardrail.Discriminator[PythonLanguage],children: List[String]): Target[Option[PythonLanguage#ValueDefinition]] = ???
    def extractSuperClass(swagger: com.twilio.guardrail.core.Tracker[io.swagger.v3.oas.models.media.ComposedSchema],definitions: List[(String, com.twilio.guardrail.core.Tracker[io.swagger.v3.oas.models.media.Schema[_]])]): Target[List[(String, com.twilio.guardrail.core.Tracker[io.swagger.v3.oas.models.media.Schema[_]], List[com.twilio.guardrail.core.Tracker[io.swagger.v3.oas.models.media.Schema[_]]])]] = ???
    def renderADTStaticDefns(clsName: String,discriminator: com.twilio.guardrail.Discriminator[PythonLanguage],encoder: Option[PythonLanguage#ValueDefinition],decoder: Option[PythonLanguage#ValueDefinition]): Target[com.twilio.guardrail.StaticDefns[PythonLanguage]] = ???
    def renderSealedTrait(className: String,params: List[com.twilio.guardrail.ProtocolParameter[PythonLanguage]],discriminator: com.twilio.guardrail.Discriminator[PythonLanguage],parents: List[com.twilio.guardrail.SuperClass[PythonLanguage]],children: List[String]): Target[PythonLanguage#Trait] = ???
  }
}
