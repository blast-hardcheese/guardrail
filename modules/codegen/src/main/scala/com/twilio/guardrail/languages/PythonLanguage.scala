package com.twilio.guardrail.languages

import cats.Show
import cats.data.NonEmptyList
import cats.implicits._

package python {
  final case class Type(value: String)
  sealed abstract class Term(val asPython: String)
  final case class TermSelect(base: Term, last: TermName) extends Term(s"${base.asPython}.${last}")
  final case class TermName(value: String) extends Term(value)
  final case class TermExpr(value: String) extends Term(value)
  final case class StringLit(value: String) extends Term(s"""\"\"\"${value}\"\"\"""")
  final case class BoolLit(value: Boolean) extends Term(s"""${if (value) "True" else "False"}""")
  final case class IntLit(value: Int) extends Term(s"""${value}""")
  final case class Defn(name: String, params: List[Term], returns: Option[Type], body: List[Term])
  final case class Stat(line: String)
  final case class Import(value: String)
  final case class Param(name: TermName, tpe: Type, default: Option[Term])
  final case class ClassDefn(decorators: List[Term], name: TermName, body: List[Stat])

  package object implicits {
    def reflow(level: Int): String => String = s => (s.stripMargin.split("\n").toList match {
      case x :: y :: rest => x :: (y :: rest).map("|" + "  " * level + _)
      case other => other
    }).mkString("\n")

    implicit val ShowImport: Show[Import] = _.value
    implicit val ShowTerm: Show[Term] = _.asPython
    implicit val ShowStat: Show[Stat] = { case Stat(line) => line }
    implicit val ShowClassDefn: Show[ClassDefn] = { case ClassDefn(decorators, TermName(name), body) =>
      val prefix = NonEmptyList.fromList(decorators).fold("")(_.map(s => s"@${s.show}").toList.mkString("\n") + "\n")
      val renderedBody = NonEmptyList.fromList(body).fold("pass")(_.toList.map(_.show).mkString("\n|"))

      val defn =
        s"""|class ${name}:
            |  ${reflow(1)(renderedBody)}
            |""".stripMargin
      prefix + defn
    }
  }
}

class PythonLanguage extends LanguageAbstraction {

  type Statement = String

  type Import = python.Import

  // Terms

  type Term = python.Term
  type TermName = python.TermName
  type TermSelect = python.TermSelect

  // Declarations
  type MethodDeclaration = python.Defn

  // Definitions
  type Definition = List[python.Stat]
  type AbstractClass = String
  type ClassDefinition = python.ClassDefn
  type InterfaceDefinition = String
  type ObjectDefinition = String
  type Trait = Nothing

  // Functions
  type InstanceMethod = String
  type StaticMethod = String

  // Values
  type ValueDefinition = python.Stat
  type MethodParameter = python.Param
  type Type = python.Type
  type TypeName = python.Type
  type Annotation = String

  // Result
  type FileContents = String
}
