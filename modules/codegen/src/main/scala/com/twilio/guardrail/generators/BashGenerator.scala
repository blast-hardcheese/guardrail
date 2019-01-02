package com.twilio.guardrail
package generators

import cats.syntax.either._
import cats.~>
import com.twilio.guardrail.languages.BashLanguage
import com.twilio.guardrail.terms._
import scala.meta._
import java.nio.file.{ Path, Paths }

object BashGenerator {
  object BashInterp extends (ScalaTerm[BashLanguage, ?] ~> Target) {
    def apply[T](term: ScalaTerm[BashLanguage, T]): Target[T] = term match {
      case LitString(value)                                                                                                                           => ???
      case LitFloat(value)                                                                                                                            => ???
      case LitDouble(value)                                                                                                                           => ???
      case LitInt(value)                                                                                                                              => ???
      case LitLong(value)                                                                                                                             => ???
      case LitBoolean(value)                                                                                                                          => ???
      case LiftOptionalType(value)                                                                                                                    => ???
      case LiftOptionalTerm(value)                                                                                                                    => ???
      case EmptyOptionalTerm()                                                                                                                        => ???
      case LiftVectorType(value)                                                                                                                      => ???
      case LiftVectorTerm(value)                                                                                                                      => ???
      case LiftMapType(value)                                                                                                                         => ???
      case LookupEnumDefaultValue(tpe, defaultValue, values)                                                                                          => ???
      case EmbedArray(tpe)                                                                                                                            => ???
      case EmbedMap(tpe)                                                                                                                              => ???
      case ParseType(tpe)                                                                                                                             => ???
      case ParseTypeName(tpe)                                                                                                                         => ???
      case PureTermName(tpe)                                                                                                                          => ???
      case PureTypeName(tpe)                                                                                                                          => ???
      case PureMethodParameter(name, tpe, default)                                                                                                    => ???
      case TypeNamesEqual(a, b)                                                                                                                       => ???
      case TypesEqual(a, b)                                                                                                                           => ???
      case ExtractTypeName(tpe)                                                                                                                       => ???
      case ExtractTermName(term)                                                                                                                      => ???
      case AlterMethodParameterName(param, name)                                                                                                      => ???
      case DateType()                                                                                                                                 => ???
      case DateTimeType()                                                                                                                             => ???
      case StringType(format)                                                                                                                         => ???
      case FloatType()                                                                                                                                => ???
      case DoubleType()                                                                                                                               => ???
      case NumberType(format)                                                                                                                         => ???
      case IntType()                                                                                                                                  => ???
      case LongType()                                                                                                                                 => ???
      case IntegerType(format)                                                                                                                        => ???
      case BooleanType(format)                                                                                                                        => ???
      case ArrayType(format)                                                                                                                          => ???
      case FallbackType(tpe, format)                                                                                                                  => ???
      case WidenTypeName(tpe)                                                                                                                         => ???
      case WidenTermSelect(value)                                                                                                                     => ???
      case RenderImplicits(pkgPath, pkgName, frameworkImports, jsonImports, customImports)                                                            => ???
      case RenderFrameworkImplicits(pkgPath, pkgName, frameworkImports, jsonImports, frameworkImplicits, frameworkImplicitName)                       => ???
      case WritePackageObject(dtoPackagePath, dtoComponents, customImports, packageObjectImports, protocolImports, packageObjectContents, extraTypes) => ???
      case WriteProtocolDefinition(outputPath, pkgName, definitions, dtoComponents, imports, elem)                                                    => ???
      case WriteClient(pkgPath,
                       pkgName,
                       customImports,
                       frameworkImplicitName,
                       dtoComponents,
                       Client(pkg, clientName, imports, companion, client, responseDefinitions)) =>
        ???
      case WriteServer(pkgPath, pkgName, customImports, frameworkImplicitName, dtoComponents, Server(pkg, extraImports, src)) => ???
    }
  }
}
