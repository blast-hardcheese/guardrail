package com.twilio.guardrail
package terms

import cats.InjectK
import cats.arrow.FunctionK
import cats.data.NonEmptyList
import cats.free.Free
import com.twilio.guardrail.generators.GeneratorSettings
import com.twilio.guardrail.languages
import scala.language.existentials
import scala.meta._

trait CoreAlgebra[Language <: languages.LanguageAbstraction] {
  implicit val A: languages.Algebras[Language]

  sealed trait CoreTerm[T]
  case object GetDefaultFramework                                                                 extends CoreTerm[String]
  case class ExtractGenerator(context: Context)                                                   extends CoreTerm[FunctionK[A.CodegenApplication, Target]]
  case class ExtractGeneratorSettings(context: Context)                                           extends CoreTerm[GeneratorSettings]
  case class ParseArgs(args: Array[String], defaultFramework: String)                             extends CoreTerm[List[Args]]
  case class ValidateArgs(parsed: List[Args])                                                     extends CoreTerm[NonEmptyList[Args]]
  case class ProcessArgSet(targetInterpreter: FunctionK[A.CodegenApplication, Target], arg: Args) extends CoreTerm[ReadSwagger[Target[List[WriteTree]]]]

  class CoreTerms[F[_]](implicit I: InjectK[CoreTerm, F]) {
    def getDefaultFramework: Free[F, String] =
      Free.inject[CoreTerm, F](GetDefaultFramework)
    def extractGenerator(context: Context): Free[F, FunctionK[A.CodegenApplication, Target]] =
      Free.inject[CoreTerm, F](ExtractGenerator(context))
    def extractGeneratorSettings(context: Context): Free[F, GeneratorSettings] =
      Free.inject[CoreTerm, F](ExtractGeneratorSettings(context))
    def parseArgs(args: Array[String], defaultFramework: String): Free[F, List[Args]] =
      Free.inject[CoreTerm, F](ParseArgs(args, defaultFramework))
    def validateArgs(parsed: List[Args]): Free[F, NonEmptyList[Args]] =
      Free.inject[CoreTerm, F](ValidateArgs(parsed))
    def processArgSet(
        targetInterpreter: FunctionK[A.CodegenApplication, Target]
    )(args: Args): Free[F, ReadSwagger[Target[List[WriteTree]]]] =
      Free.inject[CoreTerm, F](ProcessArgSet(targetInterpreter, args))
  }

  implicit def coreTerm[F[_]](implicit I: InjectK[CoreTerm, F]): CoreTerms[CoreTerm] = new CoreTerms[CoreTerm]
}
