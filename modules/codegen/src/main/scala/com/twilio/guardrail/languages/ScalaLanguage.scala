package com.twilio.guardrail.languages

class ScalaLanguage extends LanguageAbstraction {
  type Statement = scala.meta.Stat

  type Import = scala.meta.Import

  // Definitions
  type AbstractClass       = Nothing
  type ClassDefinition     = Nothing
  type InterfaceDefinition = Nothing
  type ObjectDefinition    = scala.meta.Defn.Object
  type Trait               = Nothing

  // Functions
  type InstanceMethod = Nothing
  type StaticMethod   = Nothing

  // Values
  type ValueDefinition = Nothing
  type MethodParameter = Nothing
  type Type            = scala.meta.Type

  // Result
  type FileContents = Nothing
}
