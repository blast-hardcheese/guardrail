package com.twilio.guardrail.protocol.terms.protocol

import io.swagger.v3.oas.models.media.Schema
import com.twilio.guardrail.languages.LA
import com.twilio.guardrail.{ ProtocolElems, StrictProtocolElems }

case class PropMeta[L <: LA](clsName: String, tpe: L#Type)
sealed trait ProtocolSupportTerm[L <: LA, T]
case class ExtractConcreteTypes[L <: LA](models: Either[String, List[PropMeta[L]]]) extends ProtocolSupportTerm[L, List[PropMeta[L]]]
case class ProtocolImports[L <: LA]()                                               extends ProtocolSupportTerm[L, List[L#Import]]
case class PackageObjectImports[L <: LA]()                                          extends ProtocolSupportTerm[L, List[L#Import]]
case class PackageObjectContents[L <: LA]()                                         extends ProtocolSupportTerm[L, List[L#ValueDefinition]]
