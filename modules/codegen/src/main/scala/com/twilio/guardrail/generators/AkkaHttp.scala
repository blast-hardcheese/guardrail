package com.twilio.guardrail
package generators

import cats.~>
import cats.arrow.FunctionK

import AkkaHttpClientGenerator._
import AkkaHttpServerGenerator._
import ScalaGenerator._
import SwaggerGenerator._
import AkkaHttpGenerator._

class AkkaHttp(val A: languages.Algebras[languages.ScalaLanguage]) {
  val circeProtocolGenerator = new CirceProtocolGenerator(A)

  import circeProtocolGenerator._

  val interpDefinitionPM: A.DefinitionPM ~> Target         = ProtocolSupportTermInterp or ModelProtocolTermInterp
  val interpDefinitionPME: A.DefinitionPME ~> Target       = EnumProtocolTermInterp or interpDefinitionPM
  val interpDefinitionPMEA: A.DefinitionPMEA ~> Target     = AliasProtocolTermInterp or interpDefinitionPME
  val interpDefinitionPMEAA: A.DefinitionPMEAA ~> Target   = ArrayProtocolTermInterp or interpDefinitionPMEA
  val interpDefinitionPMEAAP: A.DefinitionPMEAAP ~> Target = PolyProtocolTermInterp or interpDefinitionPMEAA

  val interpModel: A.ModelInterpreters ~> Target = interpDefinitionPMEAAP

  val interpFrameworkC: A.FrameworkC ~> Target     = ClientTermInterp or interpModel
  val interpFrameworkCS: A.FrameworkCS ~> Target   = ServerTermInterp or interpFrameworkC
  val interpFrameworkCSF: A.FrameworkCSF ~> Target = FrameworkInterp or interpFrameworkCS

  val interpFramework: A.ClientServerTerms ~> Target = interpFrameworkCSF

  val parser: A.Parser ~> Target = SwaggerInterp or interpFramework

  val codegenApplication: A.CodegenApplication ~> Target = ScalaInterp or parser

  def interp: FunctionK[A.CodegenApplication, Target] = codegenApplication
}
