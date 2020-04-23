package com.twilio.guardrail.test.generator

import cats.data._
import cats.implicits._

import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import scala.collection.JavaConverters._
import scala.meta._

import _root_.io.swagger.v3.oas.models.Components
import _root_.io.swagger.v3.oas.models.ExternalDocumentation
import _root_.io.swagger.v3.oas.models.OpenAPI
import _root_.io.swagger.v3.oas.models.Operation
import _root_.io.swagger.v3.oas.models.PathItem
import _root_.io.swagger.v3.oas.models.Paths
import _root_.io.swagger.v3.oas.models.info.Info
import _root_.io.swagger.v3.oas.models.security.SecurityRequirement
import _root_.io.swagger.v3.oas.models.servers.Server
import _root_.io.swagger.v3.oas.models.tags.Tag

class OpenAPIVersion(val version: String)
class OpenAPIExtensions(val extensions: Map[String, Object])
class DownField(val params: Vector[com.github.javaparser.ast.body.Parameter], val dependencies: Vector[(com.github.javaparser.ast.`type`.Type, java.io.File)]) {
  override def toString() = s"DownField($params, $dependencies)"
}

object Generator {
  val genOpenAPIVersion: Gen[OpenAPIVersion] = Gen.const(new OpenAPIVersion("3.0.2"))
  val genOperation: Gen[Operation] = Gen.const(new Operation)
  val genPathItem: Gen[PathItem] = Gen.const(new PathItem)
  def genPaths(
    genPathItem: Gen[Option[List[(String, PathItem)]]],
    genExtensions: Gen[Option[OpenAPIExtensions]]
  ): Gen[Paths] = {
    val elem = new Paths
    Gen.zip(genPathItem, genExtensions).map({ case (pathItems, extensions) =>
pathItems.foreach(_.foreach((elem.addPathItem _).tupled))
extensions.map(_.extensions).foreach(_.foreach((elem.addExtension _).tupled))
      elem
    })
  }

  def genOpenAPI(
    genComponents: Gen[Option[Components]],
    genExtensions: Gen[Option[OpenAPIExtensions]],
    genExternalDocs: Gen[Option[ExternalDocumentation]],
    genInfo: Gen[Option[Info]],
    genOpenapi: Gen[OpenAPIVersion],
    genPaths: Gen[Option[Paths]],
    genSecurity: Gen[Option[List[SecurityRequirement]]],
    genServers: Gen[Option[List[Server]]],
    genTags: Gen[Option[List[Tag]]]
  ): Gen[OpenAPI] = {
    val openAPI = new OpenAPI
    Gen.zip(genComponents, genExtensions, genExternalDocs, genInfo, genOpenapi, genPaths, genSecurity, genServers, genTags)
      .map({ case (components, extensions, externalDocs, info, openapi, paths, security, servers, tags) =>
        openAPI.setComponents(components.orNull)
        openAPI.setExtensions(extensions.map(_.extensions.asJava).orNull)
        openAPI.setExternalDocs(externalDocs.orNull)
        openAPI.setInfo(info.orNull)
        openAPI.setOpenapi(openapi.version)
        openAPI.setPaths(paths.orNull)
        openAPI.setSecurity(security.map(_.asJava).orNull)
        openAPI.setServers(servers.map(_.asJava).orNull)
        openAPI.setTags(tags.map(_.asJava).orNull)
        openAPI
      })
  }

  val lowercase: String => String = s => (s.take(1).toLowerCase ++ s.drop(1))
  val capitalize: String => String = s => (s.take(1).toUpperCase ++ s.drop(1))

  def handleAddMethod(
    dirname: String,
    rootParsed: com.github.javaparser.ast.CompilationUnit,
    method: com.github.javaparser.ast.body.MethodDeclaration
  ): ((List[String], List[String]), List[DownField]) = {
    val pkg = rootParsed.getPackageDeclaration().get().getNameAsString()
    val imports = rootParsed.getImports().asScala.toVector.map(_.getNameAsString()).map(i => i.split('.').last -> i).toMap

    val params = method.getParameters().asScala.toVector
    val dependencies = params.map(_.getType())
    def guessPath(tpe: com.github.javaparser.ast.`type`.Type): List[(com.github.javaparser.ast.`type`.Type, java.io.File)] = {
      val suffix = (tpe.getElementType().asString().split('.').toList match {
        case clsName :: Nil => imports.getOrElse(clsName, s"${pkg}.${clsName}").split('.').toList
        case fullyQualified => fullyQualified
      }).mkString("/") + ".java"

      List((tpe, new java.io.File(dirname, suffix))).filter(_._2.isFile())
    }
    val downField = new DownField(params, dependencies.flatMap(guessPath))
    ((Nil, Nil), List(downField))
  }


  def walkNode(dirname: String, file: java.io.File): State[Set[com.github.javaparser.ast.`type`.Type], Unit] = {
    println(s"walkNode(..., $file)")
    import com.github.javaparser.JavaParser

    val javaParser = new JavaParser()

    val addMethod = "^add(.*)$".r
    val setMethod = "^set(.*)$".r
    val getMethod = "^get(.*)$".r

    val rootParsed = javaParser.parse(file).getResult().get
    rootParsed.getTypes.asScala.toVector.flatTraverse({
      case typeDecl: com.github.javaparser.ast.body.ClassOrInterfaceDeclaration =>
        val ((others, getterNames), downFields) = typeDecl.getMembers().asScala.toList.flatTraverse({
          case ctor: com.github.javaparser.ast.body.ConstructorDeclaration =>
            ((Nil, Nil), Nil)
          case field: com.github.javaparser.ast.body.FieldDeclaration =>
            ((Nil, Nil), Nil)
          case method: com.github.javaparser.ast.body.MethodDeclaration =>
            method.getNameAsString() match {
              case addMethod(properPropertyName) =>
                handleAddMethod(dirname, rootParsed, method)
              case addMethod(properPropertyName) =>
                handleAddMethod(dirname, rootParsed, method)
              case setMethod(properPropertyName) =>
                handleAddMethod(dirname, rootParsed, method)
              case getMethod(properPropertyName) =>
                ((Nil, List(lowercase(properPropertyName))), Nil)
              case "equals" | "hashCode" | "toString" | "toIndentedString" =>
                ((Nil, Nil), Nil)
              case other =>
                // println(s"  Unexpected method name: ${other}")
                ((List(other), List.empty), List.empty)
            }
          case enum: com.github.javaparser.ast.body.EnumDeclaration =>
            println(s"TODO: Not handling ${enum.getNameAsString()} yet")
            ((Nil, Nil), Nil)
          case unknown =>
            // println(unknown.getClass)
            // println(unknown)
            ((Nil, Nil), Nil)
        })

        for {
          nextFiles <- downFields.toVector.flatTraverse { downField =>
            downField.dependencies.flatTraverse { case (tpe, file) =>
              for {
                seen <- State.get[Set[com.github.javaparser.ast.`type`.Type]]
                res = if (seen contains tpe) Vector.empty else Vector(file)
                _ <- State.set[Set[com.github.javaparser.ast.`type`.Type]](seen + tpe)
              } yield res
            }
          }
          res <- nextFiles.traverse(walkNode(dirname, _))
        } yield res
    }).map(_ => ())
  }

  def main(args: Array[String]): Unit = {
    val (dirname, root) = args.toList match {
      case dirname :: root :: Nil => (dirname, root)
      case _ => throw new Exception(s"args: <dirname> <root>")
    }

    val (allTpes, ()) = walkNode(dirname, new java.io.File(dirname, root)).runEmpty.value
  }
}
