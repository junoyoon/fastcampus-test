//package org.springframework.samples.petclinic.restassured
//
//import io.restassured.internal.ResponseSpecificationImpl
//import io.restassured.module.webtestclient.RestAssuredWebTestClient
//import io.restassured.module.webtestclient.internal.ValidatableWebTestClientResponseImpl
//import io.restassured.module.webtestclient.internal.WebTestClientRestAssuredResponseImpl
//import io.restassured.module.webtestclient.response.ValidatableWebTestClientResponse
//import io.restassured.module.webtestclient.response.WebTestClientResponse
//import io.restassured.module.webtestclient.specification.WebTestClientRequestSender
//import io.restassured.module.webtestclient.specification.WebTestClientRequestSpecification
//import io.restassured.response.ExtractableResponse
//import io.restassured.response.Response
//import io.restassured.response.ValidatableResponseOptions
//import io.restassured.specification.RequestSpecification
//
///**
// * A wrapper around [given] that starts building the request part of the test.
// * @see given
// */
//fun Given(block: WebTestClientRequestSpecification.() -> WebTestClientRequestSender): WebTestClientRequestSpecification =
//    RestAssuredWebTestClient.given().run(block)
//
///**
// * A wrapper around [ MockMvcRequestSpecification.when ] that configures how the request is dispatched.
// * @see MockMvcRequestSpecification.when
// */
//infix fun WebTestClientRequestSpecification.When(
//    block: WebTestClientRequestSender.() ->
//    WebTestClientResponse
//): WebTestClientResponse =
//    `when`().run(block)
//
//
///**
// * A wrapper around [then] that allow configuration of response expectations.
// * @see then
// */
//infix fun WebTestClientResponse.Then(block: ValidatableWebTestClientResponse.() -> Unit): ValidatableWebTestClientResponse = then()
//    .also(doIfValidatableResponseImpl {
//        forceDisableEagerAssert()
//    })
//    .apply(block)
//    .also(doIfValidatableResponseImpl {
//        forceValidateResponse()
//    })
//
///**
// * A wrapper around [ExtractableResponse] that allow for extract data out of the response
// * @see ExtractableResponse
// */
//infix fun <T> WebTestClientResponse.Extract(block: ExtractableResponse<WebTestClientResponse>.() -> T): T =
//    then().extract().run(block)
//
///**
// * A wrapper around [ExtractableResponse] that allow for extract data out of the response
// * @see ExtractableResponse
// */
//infix fun <T> ValidatableWebTestClientResponse.Extract(
//    block: ExtractableResponse<WebTestClientResponse>.() -> T
//): T = extract().run(block)
//
////  End main wrappers
//
//private fun doIfValidatableResponseImpl(
//    fn: ResponseSpecificationImpl.() -> Unit
//): (ValidatableWebTestClientResponse) -> Unit = { resp ->
//    if (resp is ValidatableWebTestClientResponseImpl) {
//        fn(resp.responseSpec)
//    }
//}
