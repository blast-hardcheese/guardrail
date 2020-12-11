package com.twilio.guardrail.generators.Python

import com.twilio.guardrail.Target
import com.twilio.guardrail.languages.{ PythonLanguage, python }
import com.twilio.guardrail.terms.CollectionsLibTerms
import com.twilio.guardrail.terms.framework._
import scala.meta._


object RequestsGenerator {
  def FrameworkInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]): FrameworkTerms[PythonLanguage, Target] =
    new FrameworkInterp

  class FrameworkInterp(implicit Cl: CollectionsLibTerms[PythonLanguage, Target]) extends FrameworkTerms[PythonLanguage, Target] {
    implicit def MonadF = Target.targetInstances
    def fileType(format: Option[String]) =
      Target.pure(python.Type("file types are not supported at this time, so this string is designed to never match anything"))

    def objectType(format: Option[String]) =
      Target.pure(python.Type("Any"))

    def getFrameworkImports(tracing: Boolean) =
      Target.pure(
        List.empty
      )
    def getFrameworkImplicits() =
      Target.pure(None)

    def getFrameworkDefinitions(tracing: Boolean) =
      Target.pure(List.empty)

    def lookupStatusCode(key: String): Target[(Int, python.TermName)] =
      key match {
        case "100" => Target.pure((100, python.TermName("Continue")))
        case "101" => Target.pure((101, python.TermName("SwitchingProtocols")))
        case "102" => Target.pure((102, python.TermName("Processing")))
        case "200" => Target.pure((200, python.TermName("Ok")))
        case "201" => Target.pure((201, python.TermName("Created")))
        case "202" => Target.pure((202, python.TermName("Accepted")))
        case "203" => Target.pure((203, python.TermName("NonAuthoritativeInformation")))
        case "204" => Target.pure((204, python.TermName("NoContent")))
        case "205" => Target.pure((205, python.TermName("ResetContent")))
        case "206" => Target.pure((206, python.TermName("PartialContent")))
        case "207" => Target.pure((207, python.TermName("MultiStatus")))
        case "208" => Target.pure((208, python.TermName("AlreadyReported")))
        case "226" => Target.pure((226, python.TermName("IMUsed")))
        case "300" => Target.pure((300, python.TermName("MultipleChoices")))
        case "301" => Target.pure((301, python.TermName("MovedPermanently")))
        case "302" => Target.pure((302, python.TermName("Found")))
        case "303" => Target.pure((303, python.TermName("SeeOther")))
        case "304" => Target.pure((304, python.TermName("NotModified")))
        case "305" => Target.pure((305, python.TermName("UseProxy")))
        case "307" => Target.pure((307, python.TermName("TemporaryRedirect")))
        case "308" => Target.pure((308, python.TermName("PermanentRedirect")))
        case "400" => Target.pure((400, python.TermName("BadRequest")))
        case "401" => Target.pure((401, python.TermName("Unauthorized")))
        case "402" => Target.pure((402, python.TermName("PaymentRequired")))
        case "403" => Target.pure((403, python.TermName("Forbidden")))
        case "404" => Target.pure((404, python.TermName("NotFound")))
        case "405" => Target.pure((405, python.TermName("MethodNotAllowed")))
        case "406" => Target.pure((406, python.TermName("NotAcceptable")))
        case "407" => Target.pure((407, python.TermName("ProxyAuthenticationRequired")))
        case "408" => Target.pure((408, python.TermName("RequestTimeout")))
        case "409" => Target.pure((409, python.TermName("Conflict")))
        case "410" => Target.pure((410, python.TermName("Gone")))
        case "411" => Target.pure((411, python.TermName("LengthRequired")))
        case "412" => Target.pure((412, python.TermName("PreconditionFailed")))
        case "413" => Target.pure((413, python.TermName("PayloadTooLarge")))
        case "414" => Target.pure((414, python.TermName("UriTooLong")))
        case "415" => Target.pure((415, python.TermName("UnsupportedMediaType")))
        case "416" => Target.pure((416, python.TermName("RangeNotSatisfiable")))
        case "417" => Target.pure((417, python.TermName("ExpectationFailed")))
        case "422" => Target.pure((422, python.TermName("UnprocessableEntity")))
        case "423" => Target.pure((423, python.TermName("Locked")))
        case "424" => Target.pure((424, python.TermName("FailedDependency")))
        case "426" => Target.pure((426, python.TermName("UpgradeRequired")))
        case "428" => Target.pure((428, python.TermName("PreconditionRequired")))
        case "429" => Target.pure((429, python.TermName("TooManyRequests")))
        case "431" => Target.pure((431, python.TermName("RequestHeaderFieldsTooLarge")))
        case "451" => Target.pure((451, python.TermName("UnavailableForLegalReasons")))
        case "500" => Target.pure((500, python.TermName("InternalServerError")))
        case "501" => Target.pure((501, python.TermName("NotImplemented")))
        case "502" => Target.pure((502, python.TermName("BadGateway")))
        case "503" => Target.pure((503, python.TermName("ServiceUnavailable")))
        case "504" => Target.pure((504, python.TermName("GatewayTimeout")))
        case "505" => Target.pure((505, python.TermName("HttpVersionNotSupported")))
        case "506" => Target.pure((506, python.TermName("VariantAlsoNegotiates")))
        case "507" => Target.pure((507, python.TermName("InsufficientStorage")))
        case "508" => Target.pure((508, python.TermName("LoopDetected")))
        case "510" => Target.pure((510, python.TermName("NotExtended")))
        case "511" => Target.pure((511, python.TermName("NetworkAuthenticationRequired")))
        case _     => Target.raiseUserError(s"Unknown HTTP status code: ${key}")

      }
  }
}
