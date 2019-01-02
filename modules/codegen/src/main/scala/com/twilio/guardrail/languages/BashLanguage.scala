package com.twilio.guardrail.languages

class BashLanguage extends LanguageAbstraction {

  type Statement = String

  type Import = String

  // Terms

  type Term       = String
  type TermName   = String
  type TermSelect = String

  // Declarations
  type MethodDeclaration = String

  // Definitions
  type Definition          = String
  type AbstractClass       = String
  type ClassDefinition     = String
  type InterfaceDefinition = String
  type ObjectDefinition    = String
  type Trait               = String

  // Functions
  type InstanceMethod = String
  type StaticMethod   = String

  // Values
  type ValueDefinition = String
  type MethodParameter = String
  type Type            = String
  type TypeName        = String

  // Result
  type FileContents = String
}
