package com.twilio.guardrail.generators

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits._
import com.twilio.guardrail.Common.resolveFile
import com.twilio.guardrail._
import com.twilio.guardrail.generators.syntax.RichString
import com.twilio.guardrail.languages.{ PythonLanguage, python }
import com.twilio.guardrail.languages.python.implicits._
import com.twilio.guardrail.terms._
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PythonGenerator {
  object PythonInterp extends LanguageTerms[PythonLanguage, Target] {
    def MonadF: cats.Monad[Target] = Target.targetInstances
    def alterMethodParameterName(param: PythonLanguage#MethodParameter,name: PythonLanguage#TermName): Target[PythonLanguage#MethodParameter] = ???
    def booleanType(format: Option[String]): Target[PythonLanguage#Type] = Target.pure(python.Type("bool"))
    def bytesType(): Target[PythonLanguage#Type] = ???
    def dateTimeType(): Target[PythonLanguage#Type] = ???
    def dateType(): Target[PythonLanguage#Type] = ???
    def doubleType(): Target[PythonLanguage#Type] = ???
    def extractTermName(term: PythonLanguage#TermName): Target[String] = Target.pure(term.value)
    def extractTermNameFromParam(param: PythonLanguage#MethodParameter): Target[String] = Target.pure(param.name.value)
    def extractTypeName(tpe: PythonLanguage#Type): Target[Option[PythonLanguage#TypeName]] = Target.pure(Option(tpe))
    def fallbackType(tpe: Option[String],format: Option[String]): Target[PythonLanguage#Type] = ???
    def findCommonDefaultValue(history: String,a: Option[PythonLanguage#Term],b: Option[PythonLanguage#Term]): Target[Option[PythonLanguage#Term]] = ???
    def findCommonRawType(history: String,a: RawParameterType,b: RawParameterType): Target[RawParameterType] = ???
    def floatType(): Target[PythonLanguage#Type] = ???
    def formatEnumName(enumValue: String): Target[String] = ???
    def formatFieldName(fieldName: String): Target[String] = Target.pure(fieldName.toSnakeCase)
    def formatMethodArgName(methodArgName: String): Target[String] = Target.pure(methodArgName.toSnakeCase)
    def formatMethodName(methodName: String): Target[String] = Target.pure(methodName.toSnakeCase)
    def formatPackageName(packageName: List[String]): Target[List[String]] = Target.pure(packageName)
    def formatTypeName(typeName: String,suffix: Option[String]): Target[String] = Target.pure(typeName.toPascalCase + suffix.fold("")(_.toPascalCase))
    def fullyQualifyPackageName(rawPkgName: List[String]): Target[List[String]] = Target.pure(rawPkgName)
    def intType(): Target[PythonLanguage#Type] = Target.pure(python.Type("int"))
    def integerType(format: Option[String]): Target[PythonLanguage#Type] = Target.pure(python.Type("int"))
    def litBoolean(value: Boolean): Target[PythonLanguage#Term] = Target.pure(python.BoolLit(value))
    def litDouble(value: Double): Target[PythonLanguage#Term] = ???
    def litFloat(value: Float): Target[PythonLanguage#Term] = ???
    def litInt(value: Int): Target[PythonLanguage#Term] = Target.pure(python.IntLit(value))
    def litLong(value: Long): Target[PythonLanguage#Term] = ???
    def litString(value: String): Target[PythonLanguage#Term] = Target.pure(python.StringLit(value))
    def longType(): Target[PythonLanguage#Type] = ???
    def lookupEnumDefaultValue(tpe: PythonLanguage#TypeName,defaultValue: PythonLanguage#Term,values: List[(String, PythonLanguage#TermName, PythonLanguage#TermSelect)]): Target[PythonLanguage#TermSelect] = 
      defaultValue match {
        case python.StringLit(s) =>
          values
            .find(_._1 == s)
            .fold(Target.raiseUserError[PythonLanguage#TermSelect](s"Enumeration $tpe is not defined for default value ${s}"))(value => Target.pure(value._3))
        case _ =>
          Target.raiseUserError(s"Enumeration $tpe somehow has a default value that isn't a string")
      }
    def numberType(format: Option[String]): Target[PythonLanguage#Type] = ???
    def parseType(value: String): Target[Option[PythonLanguage#Type]] = ???
    def parseTypeName(value: String): Target[Option[PythonLanguage#TypeName]] = Target.pure(Some(python.Type(value)))
    def pureMethodParameter(name: PythonLanguage#TermName,tpe: PythonLanguage#Type,default: Option[PythonLanguage#Term]): Target[PythonLanguage#MethodParameter] =
      Target.pure(python.Param(name, tpe, default))
    def pureTermName(value: String): Target[PythonLanguage#TermName] = Target.pure(python.TermName(value))
    def pureTypeName(value: String): Target[PythonLanguage#TypeName] = Target.pure(python.Type(value))
    def renderFrameworkDefinitions(pkgPath: java.nio.file.Path,pkgName: List[String],frameworkImports: List[PythonLanguage#Import],frameworkDefinitions: List[PythonLanguage#Definition],frameworkDefinitionsName: PythonLanguage#TermName): Target[WriteTree] = ???
    def renderFrameworkImplicits(pkgPath: java.nio.file.Path,pkgName: List[String],frameworkImports: List[PythonLanguage#Import],frameworkImplicitImportNames: List[PythonLanguage#TermName],jsonImports: List[PythonLanguage#Import],frameworkImplicits: PythonLanguage#ObjectDefinition,frameworkImplicitName: PythonLanguage#TermName): Target[WriteTree] = ???
    def renderImplicits(pkgPath: java.nio.file.Path,pkgName: List[String],frameworkImports: List[PythonLanguage#Import],jsonImports: List[PythonLanguage#Import],customImports: List[PythonLanguage#Import]): Target[Option[WriteTree]] = Target.pure(None)
    def selectTerm(termNames: cats.data.NonEmptyList[String]): Target[PythonLanguage#Term] = {
      val xs = termNames.map(python.TermName(_))
      Target.pure(xs.tail.foldLeft[python.Term](xs.head)(python.TermSelect.apply _))
    }
    def selectType(typeNames: cats.data.NonEmptyList[String]): Target[PythonLanguage#Type] =
      Target.pure(typeNames.tail.foldLeft(python.Type(typeNames.head)) {
        case (python.Type(acc), next) =>
          python.Type(s"${acc}.${next}")
      })
    def stringType(format: Option[String]): Target[PythonLanguage#Type] = Target.pure(python.Type("str"))
    def typeNamesEqual(a: PythonLanguage#TypeName,b: PythonLanguage#TypeName): Target[Boolean] = ???
    def typesEqual(a: PythonLanguage#Type,b: PythonLanguage#Type): Target[Boolean] = Target.pure(a == b)
    def uuidType(): Target[PythonLanguage#Type] = ???
    def widenClassDefinition(value: PythonLanguage#ClassDefinition): Target[PythonLanguage#Definition] = ???
    def widenObjectDefinition(value: PythonLanguage#ObjectDefinition): Target[PythonLanguage#Definition] = ???
    def widenTermSelect(value: PythonLanguage#TermSelect): Target[PythonLanguage#Term] = ???
    def widenTypeName(tpe: PythonLanguage#TypeName): Target[PythonLanguage#Type] = Target.pure(tpe)
    def wrapToObject(name: PythonLanguage#TermName,imports: List[PythonLanguage#Import],definitions: List[PythonLanguage#Definition]): Target[Option[PythonLanguage#ObjectDefinition]] = ???
    def writeClient(pkgPath: java.nio.file.Path,pkgName: List[String],customImports: List[PythonLanguage#Import],frameworkImplicitNames: List[PythonLanguage#TermName],dtoComponents: Option[List[String]],_client: Client[PythonLanguage]): Target[List[WriteTree]] = {
      val Client(pkg, clientName, imports, staticDefns, client, responseDefinitions) = _client
      Target.pure(
        List(
          WriteTree(
            resolveFile(pkgPath)(pkg :+ (s"$clientName.py")),
            Future.successful(
              s"""${imports.map(_.show).mkString("\n")}
                 |
                 |${client.toList.map(_.merge.show).mkString("\n\n")}
                 |""".stripMargin.getBytes(StandardCharsets.UTF_8))
          )
        )
      )
    }
    def writePackageObject(dtoPackagePath: java.nio.file.Path,pkgComponents: List[String],dtoComponents: Option[cats.data.NonEmptyList[String]],customImports: List[PythonLanguage#Import],packageObjectImports: List[PythonLanguage#Import],protocolImports: List[PythonLanguage#Import],packageObjectContents: List[PythonLanguage#Statement],extraTypes: List[PythonLanguage#Statement]): Target[Option[WriteTree]] = Target.pure(None)
    // Put all elems into the same file, hopefully the order is OK out of the box
    def groupProtocolDefinitions(
        elems: List[StrictProtocolElems[PythonLanguage]]
    ) = Target.pure(List(elems))
    def writeProtocolDefinitions(outputPath: java.nio.file.Path,pkgName: List[String],definitions: List[String],dtoComponents: List[String],imports: List[PythonLanguage#Import],protoImplicitName: Option[PythonLanguage#TermName],elems: List[StrictProtocolElems[PythonLanguage]]): Target[(List[WriteTree], List[PythonLanguage#Statement])] = {
        Target.pure(
          elems.traverse({
            case EnumDefinition(_, _, _, _, cls, staticDefns) =>
              (
                List(
                    Future.successful(
                      s"""${imports.map(_.show).mkString("\n")}
                         |
                         |${cls.show}
                         |""".stripMargin.getBytes(StandardCharsets.UTF_8)
                    )
                  ),
                List.empty
              )
            case ClassDefinition(_, _, _, cls, staticDefns, _) =>
              (
                List(
                  Future.successful(
                    s"""${cls.show}
                       |""".stripMargin.getBytes(StandardCharsets.UTF_8)
                  )
                ),
                List.empty
              )
            case ADT(name, tpe, _, trt, staticDefns) =>
              (List.empty, List.empty)
            case RandomType(_, _) =>
              (List.empty, List.empty)
          }).bimap({ fx =>
            val importBytes =
              s"""${imports.map(_.show).mkString("\n")}
                 |
                 |""".stripMargin.getBytes(StandardCharsets.UTF_8)
            List(WriteTree(
              resolveFile(outputPath)(dtoComponents).resolve("__init__.py"),
                fx.sequence.map(_.flatMap(_.toList).toArray).map(importBytes ++ _)
            ))
          }, {
            _.flatten
          })
        )
    }
    def writeServer(pkgPath: java.nio.file.Path,pkgName: List[String],customImports: List[PythonLanguage#Import],frameworkImplicitNames: List[PythonLanguage#TermName],dtoComponents: Option[List[String]],server: Server[PythonLanguage]): Target[List[WriteTree]] = Target.raiseUserError("Python requests server generation is not supported")
  }
}
