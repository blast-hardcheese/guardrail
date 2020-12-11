package com.twilio.guardrail.generators.collections

import cats.Monad
import com.twilio.guardrail.SwaggerUtil.LazyResolvedType
import com.twilio.guardrail.languages.PythonLanguage
import com.twilio.guardrail.languages.python._
import com.twilio.guardrail.terms.CollectionsLibTerms
import com.twilio.guardrail.{ SwaggerUtil, Target }

object PythonCollectionsGenerator {
  object PythonCollectionsInterp extends CollectionsLibTerms[PythonLanguage, Target] {
    implicit def MonadF: Monad[Target] = Target.targetInstances

    def vendorPrefixes(): Target[List[String]] = Target.pure(List("x-scala", "x-jvm"))

    def liftOptionalType(value: Type): Target[Type] = Target.pure(Type(s"Optional[${value.value}]"))
    def liftOptionalTerm(value: Term): Target[Term] = Target.pure(value)
    def liftSomeTerm(value: Term): Target[Term]     = Target.pure(value)
    def emptyOptionalTerm(): Target[Term]           = Target.pure(TermName("None"))

    def arrayType(format: Option[String]): Target[Type] = Target.pure(Type("Iterable[String]"))
    def liftVectorType(value: Type, customTpe: Option[Type]): Target[Type] =
      Target.pure(Type(s"${customTpe.getOrElse(Type("List")).value}[${value.value}]"))
    def liftVectorTerm(value: Term): Target[Term] = Target.pure(TermExpr(s"[${value.asPython}]"))
    def emptyArray(): Target[Term]                = Target.pure(TermExpr("[]"))
    def embedArray(tpe: LazyResolvedType[PythonLanguage], containerTpe: Option[Type]): Target[LazyResolvedType[PythonLanguage]] = tpe match {
      case SwaggerUtil.Deferred(tpe) =>
        Target.pure(SwaggerUtil.DeferredArray[PythonLanguage](tpe, containerTpe))
      case SwaggerUtil.DeferredArray(_, _) =>
        Target.raiseUserError("FIXME: Got an Array of Arrays, currently not supported")
      case SwaggerUtil.DeferredMap(_, _) =>
        Target.raiseUserError("FIXME: Got an Array of Maps, currently not supported")
    }

    def liftMapType(value: Type, customTpe: Option[Type]): Target[Type] =
      Target.pure(Type(s"${customTpe.getOrElse(Type("Dict")).value}[str, ${value.value}]"))
    def emptyMap(): Target[Term] = Target.pure(TermExpr("dict()"))
    def embedMap(tpe: LazyResolvedType[PythonLanguage], containerTpe: Option[Type]): Target[LazyResolvedType[PythonLanguage]] = tpe match {
      case SwaggerUtil.Deferred(inner) =>
        Target.pure(SwaggerUtil.DeferredMap[PythonLanguage](inner, containerTpe))
      case SwaggerUtil.DeferredMap(_, _) =>
        Target.raiseUserError("FIXME: Got a map of maps, currently not supported")
      case SwaggerUtil.DeferredArray(_, _) =>
        Target.raiseUserError("FIXME: Got a map of arrays, currently not supported")
    }
  }
}
