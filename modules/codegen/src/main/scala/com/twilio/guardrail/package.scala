package com.twilio

import cats.Monad
import cats.data.EitherK
import com.twilio.guardrail.languages.LA
import com.twilio.guardrail.protocol.terms.client.{ ClientTerm, ClientTerms }
import com.twilio.guardrail.protocol.terms.protocol._
import com.twilio.guardrail.protocol.terms.server.{ ServerTerm, ServerTerms }
import com.twilio.guardrail.terms.framework.{ FrameworkTerm, FrameworkTerms }
import com.twilio.guardrail.terms.{ CoreTerms, LanguageTerm, LanguageTerms, SwaggerTerm, SwaggerTerms }

package guardrail {
  case class CodegenDefinitions[L <: LA](
      clients: List[Client[L]],
      servers: List[Server[L]],
      supportDefinitions: List[SupportDefinition[L]],
      frameworksImplicits: Option[(L#TermName, L#ObjectDefinition)]
  )
}

trait MonadChain11 {
  implicit def monadForProtocolSupportTerms[L <: LA, F[_]](implicit ev: ProtocolSupportTerms[L, F]): Monad[F] = ev.MonadF
}
trait MonadChain10 extends MonadChain11 {
  implicit def monadForServerTerms[L <: LA, F[_]](implicit ev: ServerTerms[L, F]): Monad[F] = ev.MonadF
}
trait MonadChain9 extends MonadChain10 {
  implicit def monadForFrameworkTerms[L <: LA, F[_]](implicit ev: FrameworkTerms[L, F]): Monad[F] = ev.MonadF
}
trait MonadChain8 extends MonadChain9 {
  implicit def monadForPolyProtocolTerms[L <: LA, F[_]](implicit ev: PolyProtocolTerms[L, F]): Monad[F] = ev.MonadF
}
trait MonadChain7 extends MonadChain8 {
  implicit def monadForModelProtocolTerms[L <: LA, F[_]](implicit ev: ModelProtocolTerms[L, F]): Monad[F] = ev.MonadF
}
trait MonadChain6 extends MonadChain7 {
  implicit def monadForEnumProtocolTerms[L <: LA, F[_]](implicit ev: EnumProtocolTerms[L, F]): Monad[F] = ev.MonadF
}
trait MonadChain5 extends MonadChain6 {
  implicit def monadForArray[L <: LA, F[_]](implicit ev: ArrayProtocolTerms[L, F]): Monad[F] = ev.MonadF
}
trait MonadChain4 extends MonadChain5 {
  implicit def monadForSwagger[L <: LA, F[_]](implicit ev: SwaggerTerms[L, F]): Monad[F] = ev.MonadF
}
trait MonadChain3 extends MonadChain4 {
  implicit def monadForLanguage[L <: LA, F[_]](implicit ev: LanguageTerms[L, F]): Monad[F] = ev.MonadF
}
trait MonadChain2 extends MonadChain3 {
  implicit def monadForCore[L <: LA, F[_]](implicit ev: CoreTerms[L, F]): Monad[F] = ev.MonadF
}
trait MonadChain1 extends MonadChain2 {
  implicit def monadForClient[L <: LA, F[_]](implicit ev: ClientTerms[L, F]): Monad[F] = ev.MonadF
}

package object guardrail extends MonadChain1 {
  type DefinitionPM[L <: LA, T]    = EitherK[ProtocolSupportTerm[L, ?], ModelProtocolTerm[L, ?], T]
  type DefinitionPME[L <: LA, T]   = EitherK[EnumProtocolTerm[L, ?], DefinitionPM[L, ?], T]
  type DefinitionPMEA[L <: LA, T]  = EitherK[ArrayProtocolTerm[L, ?], DefinitionPME[L, ?], T]
  type DefinitionPMEAP[L <: LA, T] = EitherK[PolyProtocolTerm[L, ?], DefinitionPMEA[L, ?], T]

  type ModelInterpreters[L <: LA, T] = DefinitionPMEAP[L, T]

  type FrameworkC[L <: LA, T]   = EitherK[ClientTerm[L, ?], ModelInterpreters[L, ?], T]
  type FrameworkCS[L <: LA, T]  = EitherK[ServerTerm[L, ?], FrameworkC[L, ?], T]
  type FrameworkCSF[L <: LA, T] = EitherK[FrameworkTerm[L, ?], FrameworkCS[L, ?], T]

  type ClientServerTerms[L <: LA, T] = FrameworkCSF[L, T]

  type Parser[L <: LA, T] = EitherK[SwaggerTerm[L, ?], ClientServerTerms[L, ?], T]

  type CodegenApplication[L <: LA, T] = EitherK[LanguageTerm[L, ?], Parser[L, ?], T]
}
