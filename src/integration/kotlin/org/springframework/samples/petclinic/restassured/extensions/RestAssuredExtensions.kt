package org.springframework.samples.petclinic.restassured.extensions

import io.restassured.common.mapper.TypeRef
import io.restassured.module.mockmvc.response.MockMvcResponse
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification
import io.restassured.module.webtestclient.response.ValidatableWebTestClientResponse
import io.restassured.module.webtestclient.specification.WebTestClientRequestSender
import io.restassured.module.webtestclient.specification.WebTestClientRequestSpecification
import io.restassured.response.ExtractableResponse
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification


inline fun <reified T : Any> ValidatableMockMvcResponse.extractBodyAs() : T {
    return this.extract().body().`as`(object: TypeRef<T>(){})
}

inline fun <reified T : Any> ValidatableResponse.extractBodyAs() : T {
    return this.extract().body().`as`(object: TypeRef<T>(){})
}

inline fun <reified T : Any> ValidatableWebTestClientResponse.extractBodyAs() : T {
    return this.extract().body().`as`(object: TypeRef<T>(){})
}

inline fun RequestSpecification.noOp() : RequestSpecification = this

inline fun MockMvcRequestSpecification.noOp() : MockMvcRequestSpecification = this

inline fun WebTestClientRequestSpecification.whenever() : WebTestClientRequestSender {
    return this.`when`()
}

inline  infix fun <reified T : Any> ValidatableMockMvcResponse.ExtractBodyAs(
    block: ExtractableResponse<MockMvcResponse>.() -> T
): T = extract().run(block)
