package com.twilio.guardrail.generators

import java.util.Locale
import io.swagger.v3.oas.models.media.Schema

package object syntax {
  private val toPascalRegexes = List(
    "[\\._-]([a-z])".r, // dotted, snake, or dashed case
    "\\s+([a-zA-Z])".r, // spaces
    "^([a-z])".r // initial letter
  )

  implicit class RichString(val s: String) extends AnyVal {
    def toPascalCase: String =
      toPascalRegexes.foldLeft(s)(
        (accum, regex) => regex.replaceAllIn(accum, m => m.group(1).toUpperCase(Locale.US))
      )

    def toCamelCase: String = {
      val fromSnakeOrDashed =
        "[_-]([a-z])".r.replaceAllIn(s, m => m.group(1).toUpperCase(Locale.US))
      "^([A-Z])".r
        .replaceAllIn(fromSnakeOrDashed, m => m.group(1).toLowerCase(Locale.US))
    }

    def toSnakeCase: String = {
      val noPascal  = "^[A-Z]".r.replaceAllIn(s, _.group(0).toLowerCase(Locale.US))
      val fromCamel = "[A-Z]".r.replaceAllIn(noPascal, "_" + _.group(0))
      fromCamel.replaceAllLiterally("-", "_")
    }

    def toDashedCase: String = {
      val lowercased =
        "^([A-Z])".r.replaceAllIn(s, m => m.group(1).toLowerCase(Locale.US))
      "([A-Z])".r
        .replaceAllIn(lowercased, m => '-' +: m.group(1).toLowerCase(Locale.US))
    }
  }

  implicit class RichSchema(value: Schema[_]) {
    def showNotNull: String = showNotNullIndented(0)
    def showNotNullIndented(indent: Int): String = ("  " * indent) + value.toString().lines.filterNot(_.contains(": null")).mkString("\n" + ("  " * indent))
  }
}
