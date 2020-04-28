package com.twilio.guardrail.test.generator

import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import scala.collection.JavaConverters._

object GeneratedGenerators {
  import _root_.io.swagger.v3.oas.models.OpenAPI
  import _root_.io.swagger.v3.oas.models.security.SecurityRequirement
  import _root_.io.swagger.v3.oas.models.ExternalDocumentation
  import _root_.io.swagger.v3.oas.models.tags.Tag
  import _root_.io.swagger.v3.oas.models.servers.Server
  import _root_.io.swagger.v3.oas.models.info.Info
  import _root_.io.swagger.v3.oas.models.Components
  import _root_.io.swagger.v3.oas.models.Paths
  import _root_.io.swagger.v3.oas.models.servers.ServerVariables
  import _root_.io.swagger.v3.oas.models.servers.ServerVariable
  import _root_.io.swagger.v3.oas.models.info.License
  import _root_.io.swagger.v3.oas.models.info.Contact
  import _root_.io.swagger.v3.oas.models.responses.ApiResponse
  import _root_.io.swagger.v3.oas.models.parameters.RequestBody
  import _root_.io.swagger.v3.oas.models.media.Schema
  import _root_.io.swagger.v3.oas.models.examples.Example
  import _root_.io.swagger.v3.oas.models.links.Link
  import _root_.io.swagger.v3.oas.models.callbacks.Callback
  import _root_.io.swagger.v3.oas.models.security.SecurityScheme
  import _root_.io.swagger.v3.oas.models.parameters.Parameter
  import _root_.io.swagger.v3.oas.models.headers.Header
  import _root_.io.swagger.v3.oas.models.media.Content
  import _root_.io.swagger.v3.oas.models.media.MediaType
  import _root_.io.swagger.v3.oas.models.media.Encoding
  import _root_.io.swagger.v3.oas.models.media.Discriminator
  import _root_.io.swagger.v3.oas.models.media.XML
  import _root_.io.swagger.v3.oas.models.PathItem
  import _root_.io.swagger.v3.oas.models.Operation
  import _root_.io.swagger.v3.oas.models.responses.ApiResponses
  import _root_.io.swagger.v3.oas.models.security.OAuthFlows
  import _root_.io.swagger.v3.oas.models.security.OAuthFlow
  import _root_.io.swagger.v3.oas.models.security.Scopes

  def genServer(genString: Gen[Option[String]], gen_object: Gen[Option[(String, Object)]], genServerVariables: Gen[Option[ServerVariables]]): Gen[Server] = {
    val base = new Server
    Gen.zip(genString, gen_object, genServerVariables).map({
      case (string, _object, serverVariables) =>
        base.setUrl(string.orNull)
        _object.foreach((base.addExtension _).tupled)
        base.setVariables(serverVariables.orNull)
        base
    })
  }

  def genPathItem(gen_object: Gen[Option[(String, Object)]], genString: Gen[Option[String]], genServer: Gen[Option[Server]], genParameter: Gen[Option[Parameter]], genOperation: Gen[Option[Operation]]): Gen[PathItem] = {
    val base = new PathItem
    Gen.zip(gen_object, genString, genServer, genParameter, genOperation).map({
      case (_object, string, server, parameter, operation) =>
        _object.foreach((base.addExtension _).tupled)
        base.setSummary(string.orNull)
        server.foreach(base.addServersItem _)
        parameter.foreach(base.addParametersItem _)
        base.setGet(operation.orNull)
        base
    })
  }

  def genScopes(genString: Gen[Option[(String, String)]], gen_object: Gen[Option[(String, Object)]]): Gen[Scopes] = {
    val base = new Scopes
    Gen.zip(genString, gen_object).map({
      case (string, _object) =>
        string.foreach((base.addString _).tupled)
        _object.foreach((base.addExtension _).tupled)
        base
    })
  }
  def genOAuthFlow(genString: Gen[Option[String]], gen_object: Gen[Option[(String, Object)]], genScopes: Gen[Option[Scopes]]): Gen[OAuthFlow] = {
    val base = new OAuthFlow
    Gen.zip(genString, gen_object, genScopes).map({
      case (string, _object, scopes) =>
        base.setAuthorizationUrl(string.orNull)
        _object.foreach((base.addExtension _).tupled)
        base.setScopes(scopes.orNull)
        base
    })
  }
  def genOAuthFlows(gen_object: Gen[Option[(String, Object)]], genOAuthFlow: Gen[Option[OAuthFlow]]): Gen[OAuthFlows] = {
    val base = new OAuthFlows
    Gen.zip(gen_object, genOAuthFlow).map({
      case (_object, oAuthFlow) =>
        _object.foreach((base.addExtension _).tupled)
        base.setImplicit(oAuthFlow.orNull)
        base
    })
  }
  def genSecurityScheme(genIn: Gen[Option[SecurityScheme.In]], gen_object: Gen[Option[(String, Object)]], genString: Gen[Option[String]], genOAuthFlows: Gen[Option[OAuthFlows]], gen_type: Gen[Option[SecurityScheme.Type]]): Gen[SecurityScheme] = {
    val base = new SecurityScheme
    Gen.zip(genIn, gen_object, genString, genOAuthFlows, gen_type).map({
      case (in, _object, string, oAuthFlows, _type) =>
        base.setIn(in.orNull)
        _object.foreach((base.addExtension _).tupled)
        base.setDescription(string.orNull)
        base.setFlows(oAuthFlows.orNull)
        base.setType(_type.orNull)
        base
    })
  }
  def genParameter(genSchema: Gen[Option[Schema[Any]]], genExample: Gen[Option[(String, Example)]], gen_object: Gen[Option[(String, Object)]], genContent: Gen[Option[Content]], genString: Gen[Option[String]], genStyleEnum: Gen[Option[Parameter.StyleEnum]], genBoolean: Gen[Option[java.lang.Boolean]]): Gen[Parameter] = {
    val base = new Parameter
    Gen.zip(genSchema, genExample, gen_object, genContent, genString, genStyleEnum, genBoolean).map({
      case (schema, example, _object, content, string, styleEnum, boolean) =>
        base.setSchema(schema.orNull)
        example.foreach((base.addExample _).tupled)
        _object.foreach((base.addExtension _).tupled)
        base.setContent(content.orNull)
        base.setName(string.orNull)
        styleEnum.foreach(base.setStyle _)
        boolean.foreach(base.setRequired _)
        base
    })
  }
  def genHeader(genSchema: Gen[Option[Schema[Any]]], genExample: Gen[Option[(String, Example)]], gen_object: Gen[Option[(String, Object)]], genContent: Gen[Option[Content]], genString: Gen[Option[String]], genStyleEnum: Gen[Option[Header.StyleEnum]], genBoolean: Gen[Option[java.lang.Boolean]]): Gen[Header] = {
    val base = new Header
    Gen.zip(genSchema, genExample, gen_object, genContent, genString, genStyleEnum, genBoolean).map({
      case (schema, example, _object, content, string, styleEnum, boolean) =>
        base.setSchema(schema.orNull)
        example.foreach((base.addExample _).tupled)
        _object.foreach((base.addExtension _).tupled)
        base.setContent(content.orNull)
        base.setDescription(string.orNull)
        styleEnum.foreach(base.setStyle _)
        boolean.foreach(base.setRequired _)
        base
    })
  }
  def genComponents(genApiResponse: Gen[Option[(String, ApiResponse)]], genRequestBody: Gen[Option[(String, RequestBody)]], genSchema: Gen[Option[(String, Schema[Any])]], genExample: Gen[Option[(String, Example)]], gen_object: Gen[Option[(String, Object)]], genLink: Gen[Option[(String, Link)]], genCallback: Gen[Option[(String, Callback)]], genSecurityScheme: Gen[Option[(String, SecurityScheme)]], genParameter: Gen[Option[(String, Parameter)]], genHeader: Gen[Option[(String, Header)]]): Gen[Components] = {
    val base = new Components
    Gen.zip(genApiResponse, genRequestBody, genSchema, genExample, gen_object, genLink, genCallback, genSecurityScheme, genParameter, genHeader).map({
      case (apiResponse, requestBody, schema, example, _object, link, callback, securityScheme, parameter, header) =>
        apiResponse.foreach((base.addResponses _).tupled)
        requestBody.foreach((base.addRequestBodies _).tupled)
        schema.foreach((base.addSchemas _).tupled)
        example.foreach((base.addExamples _).tupled)
        _object.foreach((base.addExtension _).tupled)
        link.foreach((base.addLinks _).tupled)
        callback.foreach((base.addCallbacks _).tupled)
        securityScheme.foreach((base.addSecuritySchemes _).tupled)
        parameter.foreach((base.addParameters _).tupled)
        header.foreach((base.addHeaders _).tupled)
        base
    })
  }
  def genPaths(genPathItem: Gen[Option[(String, PathItem)]], gen_object: Gen[Option[(String, Object)]]): Gen[Paths] = {
    val base = new Paths
    Gen.zip(genPathItem, gen_object).map({
      case (pathItem, _object) =>
        pathItem.foreach((base.addPathItem _).tupled)
        _object.foreach((base.addExtension _).tupled)
        base
    })
  }
  def genOpenAPI(genSecurityRequirement: Gen[Option[SecurityRequirement]], gen_object: Gen[Option[(String, Object)]], genExternalDocumentation: Gen[Option[ExternalDocumentation]], genString: Gen[Option[String]], genTag: Gen[Option[Tag]], genServer: Gen[Option[Server]], genInfo: Gen[Option[Info]], genComponents: Gen[Option[Components]], genPaths: Gen[Option[Paths]]): Gen[OpenAPI] = {
    val base = new OpenAPI
    Gen.zip(genSecurityRequirement, gen_object, genExternalDocumentation, genString, genTag, genServer, genInfo, genComponents, genPaths).map({
      case (securityRequirement, _object, externalDocumentation, string, tag, server, info, components, paths) =>
        securityRequirement.foreach(base.addSecurityItem _)
        _object.foreach((base.addExtension _).tupled)
        base.setExternalDocs(externalDocumentation.orNull)
        base.setOpenapi(string.orNull)
        tag.foreach(base.addTagsItem _)
        server.foreach(base.addServersItem _)
        base.setInfo(info.orNull)
        base.setComponents(components.orNull)
        base.setPaths(paths.orNull)
        base
    })
  }
}

object Example extends App {
  import GeneratedGenerators._
  def stub[A] = Gen.const(Option.empty[A])
  implicit class Syntax[A](gen: Gen[A]) {
    def o: Gen[Option[A]] = gen.map(Some(_))
  }
  genOpenAPI(
    stub,
    stub,
    stub,
    stub,
    stub,
    stub,
    stub,
    stub,
    genPaths(
      genPathItem(
        stub,
        stub,
        genServer(
          Gen.const(Option("http://guardrail.dev/example/api")),
          stub,
          stub
        ).o,
        genParameter(
          stub,
          stub,
          stub,
          stub,
          Gen.const(Option("hello")),
          stub,
          stub
        ).o,
        stub
      ).map(("/foo", _)).o,
      stub
    ).o
  ) .sample
    .foreach(println)
}
