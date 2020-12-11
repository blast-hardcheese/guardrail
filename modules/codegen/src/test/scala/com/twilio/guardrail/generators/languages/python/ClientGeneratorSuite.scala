package com.twilio.guardrail.generators.languages.python

import cats.implicits._

import com.twilio.guardrail._
import com.twilio.guardrail.core._
import com.twilio.guardrail.generators.{ PythonGenerator, SwaggerGenerator }
import com.twilio.guardrail.generators.Python.{ RequestsClientGenerator, RequestsGenerator }
import com.twilio.guardrail.languages.PythonLanguage
import com.twilio.guardrail.generators.collections.PythonCollectionsGenerator.PythonCollectionsInterp
import com.twilio.guardrail.terms.{ CollectionsLibTerms, LanguageTerms, SwaggerTerms }

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers


object ClientGeneratorSuite extends App {
  val z: Long = Target.unsafeExtract((new ClientGeneratorSuite).build)
}

class ClientGeneratorSuite extends AnyFunSuite with Matchers {
  implicit val collections: CollectionsLibTerms[PythonLanguage, Target] = PythonCollectionsInterp
  implicit val language: LanguageTerms[PythonLanguage, Target] = PythonGenerator.PythonInterp
  implicit val clientInterp = RequestsClientGenerator.ClientTermInterp
  implicit val framworkInterp = RequestsGenerator.FrameworkInterp
  implicit val swaggerTerms = SwaggerGenerator[PythonLanguage]()
  def build = ClientGenerator.fromSwagger[PythonLanguage, Target](Context.empty, List.empty)(None, None, List.empty)(List.empty, Map.empty)
}
