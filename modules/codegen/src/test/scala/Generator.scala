package com.twilio.guardrail.test.generator

import cats._
import cats.data._
import cats.implicits._

import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import scala.collection.JavaConverters._
import scala.meta._

import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserTypeAdapter
import com.github.javaparser.ast.`type`.{ Type => JPType }

object Syntax {
  implicit class OptionalToOption[A](x: java.util.Optional[A]) {
    def asScala: Option[A] = {
      if (x.isPresent()) Option(x.get()) else Option.empty
    }
  }
}

import Syntax._

class OpenAPIVersion(val version: String)
class OpenAPIExtensions(val extensions: Map[String, Object])
case class PathSuffix(value: String)
case class MethodDecl(name: String, params: Vector[com.github.javaparser.ast.body.Parameter])
object MethodDecl {
  def fromMethod[A](method: com.github.javaparser.ast.body.MethodDeclaration, inject: MethodDecl => A): A = {
    val params = method.getParameters().asScala.toVector

    inject(MethodDecl(method.getNameAsString(), params))
  }
}

case class DownField(methods: Ior[MethodDecl, MethodDecl], dependencies: Vector[(com.github.javaparser.ast.`type`.Type, String, PathSuffix)]) {
  private[this] val addMethod = "^add(.*?)s?$".r
  private[this] val setMethod = "^set(.*?)s?$".r
  private[this] val getMethod = "^get(.*?)s?$".r
  private[this] val lowercase: String => String = s => (s.take(1).toLowerCase ++ s.drop(1))

  private[this] def guessTypeName(tpe: JPType): String = {
    tpe match {
      case tpe: com.github.javaparser.ast.`type`.ClassOrInterfaceType =>
        val lastTypeArg: Option[String] = tpe.getName().asString() match {
          case "List" => tpe.getTypeArguments().asScala.flatMap(_.asScala.toVector.lastOption).map(_.asString())
          case "Map" =>  tpe.getTypeArguments().asScala.flatMap(_.asScala.toVector.lastOption).map(_.asString())
          case _ => None
        }

        lastTypeArg.getOrElse(tpe.asString())
      case _ => tpe.getElementType().asString()
    }
  }
  private[this] def guessAll(method: MethodDecl): Vector[String] = {
    Vector(method.name).flatMap {
      case addMethod(capped) =>
        val (elem, typeName) = method.params match {
          case Vector(key, value) if !Set("Map", "List").contains(value.getType().getElementType().asString()) => (value, value.getType())
          case Vector(value) if !Set("Map", "List").contains(value.getType().getElementType().asString()) => (value, value.getType())
          case other => throw new Exception(s"Unexpected $other params for 'add' $method")
        }
        Vector(lowercase(capped), elem.getName().asString(), lowercase(guessTypeName(typeName)))
      case setMethod(capped) =>
        val (elem, typeName) = method.params match {
          case Vector(value) if !Set("Map", "List").contains(value.getType().getElementType().asString()) => (value, value.getType())
          case other => throw new Exception(s"Unexpected $other params for 'set' $method")
        }
        Vector(lowercase(capped), elem.getName().asString(), lowercase(guessTypeName(typeName)))
      case getMethod(capped) =>
        throw new Exception(s"Unexpected 'get' for ${method}")
    }
  }

  def guessBaseTerm: Option[String] = (methods match {
    case Ior.Left(a) => guessAll(a)
    case Ior.Right(b) => guessAll(b)
    case Ior.Both(a, b) => guessAll(a).intersect(guessAll(b))
  }).distinct.headOption.orElse {
    println(s"// Unable to come up with a decent base term for $methods")
    None
  }

  def baseTerm: String = {
    if (Term.Name(guessBaseTerm.get).toString.startsWith("`")) {
      s"_${guessBaseTerm.get}"
    } else guessBaseTerm.get
  }
}

object DownField {
  implicit object DownFieldMonoid extends Semigroup[DownField] {
    def combine(a: DownField, b: DownField): DownField = (a, b) match {
      case (DownField(methodsA, dependenciesA), DownField(methodsB, dependenciesB)) =>
        DownField((methodsA, methodsB) match {
          case (Ior.Left(a), Ior.Right(b)) => Ior.both(a, b)
          case (Ior.Right(a), Ior.Left(b)) => Ior.both(b, a)
          case (a@Ior.Left(_), b) => Ior.both(a, b)
            println(s"// Warning, abandoning: $b")
            a
          case (a@Ior.Right(_), b) => Ior.both(b, a)
            println(s"// Warning, abandoning: $b")
            a
          case (a@Ior.Both(_, _), b) =>
            println(s"// Warning, abandoning: $b")
            a
          case (a, b@Ior.Both(_, _)) =>
            println(s"// Warning, abandoning: $a")
            b
        }, dependenciesA ++ dependenciesB)
    }
  }
}

object Generator {
  val lowercase: String => String = s => (s.take(1).toLowerCase ++ s.drop(1))
  val capitalize: String => String = s => (s.take(1).toUpperCase ++ s.drop(1))

  def guessPath(pkg: String, imports: Map[String, String])(tpe: JPType): (JPType, String, PathSuffix) = {
    val parts = tpe.getElementType().asString().split('.').toList match {
      case clsName :: Nil => imports.getOrElse(clsName, s"${pkg}.${clsName}").split('.').toList
      case fullyQualified => fullyQualified
    }
    val fullyQualified = parts.mkString(".")
    val suffix = parts.mkString("/") + ".java"

    (tpe, fullyQualified, new PathSuffix(suffix))
  }

  def getDeps(rootParsed: com.github.javaparser.ast.CompilationUnit): com.github.javaparser.ast.body.MethodDeclaration => Vector[(JPType, String, PathSuffix)] = { method =>
    val pkg = rootParsed.getPackageDeclaration().get().getNameAsString()
    val imports = rootParsed.getImports().asScala.toVector.map(_.getNameAsString()).map(i => i.split('.').last -> i).toMap

    val params = method.getParameters().asScala.toVector
    val dependencies = params.map(_.getType())
    dependencies.map(guessPath(pkg, imports) _)
  }

  def genFromDownField(base: Term.Name): DownField => (Term.Param, (Term.Name, Term.Name), Stat) = { case df@DownField(methods, _) =>
    def buildAdd(_addMethod: MethodDecl) = {
      val liftParams: Term => Term = {
        case term if _addMethod.params.length > 1 => q"$term tupled"
        case term => term
      }
      (_addMethod.params, q"${Term.Name(df.baseTerm)}.foreach(${liftParams(q"${base}.${Term.Name(_addMethod.name)} _")})")
    }
    def buildSet(_setMethod: MethodDecl) = {
      (_setMethod.params, q"${base}.${Term.Name(_setMethod.name)}(${Term.Name(df.baseTerm)}.orNull)")
    }

    val genField = Term.Name(s"gen${capitalize(df.baseTerm)}")

    val (params, addOrSet) = methods match {
      case Ior.Right(_addMethod) => buildAdd(_addMethod)
      case Ior.Both(_, _addMethod) => buildAdd(_addMethod)
      case Ior.Left(_setMethod) => buildSet(_setMethod)
    }

    val normalizedParams: Type = params match {
      case Vector(param) => Type.Name(param.getType().asString())
      case Vector(key, value) => t"(${Type.Name(key.getType().asString())}, ${Type.Name(value.getType().asString())})"
    }

    val genParam = param"${genField}: Gen[Option[${normalizedParams}]]"

    (genParam, (genField, Term.Name(df.baseTerm)), addOrSet)
  }

  def walkNode(dirname: String, file: java.io.File): State[Set[com.github.javaparser.ast.`type`.Type], Unit] = {
    println(s"// walkNode(..., $file)")
    import com.github.javaparser.JavaParser

    val javaParser = new JavaParser()

    val addMethod = "^add(.*?)s?$".r
    val setMethod = "^set(.*?)s?$".r
    val getMethod = "^get(.*?)s?$".r

    val rootParsed = javaParser.parse(file).getResult().get
    rootParsed.getTypes.asScala.toVector.traverse({
      case typeDecl: com.github.javaparser.ast.body.ClassOrInterfaceDeclaration =>
        for {
          (fields, nextFiles) <- 
            typeDecl.getMembers().asScala.toList.flatMap({
              case ctor: com.github.javaparser.ast.body.ConstructorDeclaration =>
                Nil
              case field: com.github.javaparser.ast.body.FieldDeclaration =>
                Nil
              case method: com.github.javaparser.ast.body.MethodDeclaration =>
                method.getNameAsString() match {
                  case addMethod(properPropertyName) =>
                    List(DownField(MethodDecl.fromMethod(method, Ior.right), getDeps(rootParsed)(method)))
                  case setMethod(properPropertyName) =>
                    List(DownField(MethodDecl.fromMethod(method, Ior.left), getDeps(rootParsed)(method)))
                  case getMethod(properPropertyName) =>
                    Nil
                  case "equals" | "hashCode" | "toString" | "toIndentedString" =>
                    Nil
                  case other =>
                    // println(s"  Unexpected method name: ${other}")
                    List.empty
                }
              case enum: com.github.javaparser.ast.body.EnumDeclaration =>
                println(s"// TODO: Not handling ${enum.getNameAsString()} yet")
                Nil
              case unknown =>
                // println(unknown.getClass)
                // println(unknown)
                Nil
            })
            .groupBy(_.guessBaseTerm)
            .mapValues(_.reduceLeft(Semigroup[DownField].combine _))
            .values
            .toVector
            .flatTraverse({ case df@DownField(methods, dependencies) =>

              val field = genFromDownField(q"base")(df)

              Nested(dependencies.flatTraverse({ case (tpe, fullyQualified, PathSuffix(suffix)) =>
                for {
                  seen <- State.get[Set[com.github.javaparser.ast.`type`.Type]]
                  path = Option(new java.io.File(dirname, suffix)).filter(_.isFile())
                  res <- path.filterNot(_ => seen.contains(tpe)).toVector.flatTraverse { file =>
                    State.set[Set[com.github.javaparser.ast.`type`.Type]](seen + tpe)
                      .map { _ =>
                        println(s"import _root_.${fullyQualified}")
                        Vector(file)
                      }
                    }
                } yield res
              }).map((Vector(field), _)))
            }).value
          _ <- nextFiles.traverse(walkNode(dirname, _)) // Emits a vector of unit
          baseDefn = q"val base = ${Term.New(Init(Type.Name(typeDecl.getNameAsString), Name(""), Nil))}"
        } yield (typeDecl.getNameAsString, baseDefn, fields)
    }).map({ case xs =>
      xs.map { case (clsName, baseDefn, fields) =>
        val (params, genAndTerms, setters) = fields.toList.unzip3
        val result =
        genAndTerms.unzip match {
          case (Nil, Nil) =>
            println(s"// WARNING: Not generating gen for ${clsName}")
          case (gen :: Nil, term :: Nil) =>
            q"""
            def ${Term.Name(s"gen${clsName}")}(..${params}): Gen[${Type.Name(clsName)}] = {
              ${baseDefn}
              ${gen}
                .map({ ${Term.Param(Nil, term, None, None)} =>
                  ..${setters};
                  base
                })
            }
            """
          case (gens, terms) =>
            q"""
            def ${Term.Name(s"gen${clsName}")}(..${params}): Gen[${Type.Name(clsName)}] = {
              ${baseDefn}
              Gen.zip(..${gens})
                .map({ case (..${terms.map(Pat.Var(_))}) =>
                  ..${setters};
                  base
                })
            }
            """
        }

        println(result)
      }
    })
  }

  def main(args: Array[String]): Unit = {
    val (dirname, root) = args.toList match {
      case dirname :: root :: Nil => (dirname, root)
      case _ => throw new Exception(s"args: <dirname> <root>")
    }

    val (allTpes, ()) = walkNode(dirname, new java.io.File(dirname, root)).runEmpty.value
  }
}
