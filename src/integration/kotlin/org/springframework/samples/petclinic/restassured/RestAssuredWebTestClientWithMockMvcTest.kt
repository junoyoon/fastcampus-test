package org.springframework.samples.petclinic.restassured

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.restassured.config.ObjectMapperConfig
import io.restassured.http.ContentType
import io.restassured.mapper.ObjectMapperType
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import io.restassured.module.webtestclient.RestAssuredWebTestClient.given
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.samples.petclinic.fixtures.Fixtures
import org.springframework.samples.petclinic.restassured.extensions.extractBodyAs
import org.springframework.samples.petclinic.restassured.extensions.whenever
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication

@WithMockUser(roles = ["OWNER_ADMIN"])
@AutoConfigureMockMvc
@SpringBootTest
class RestAssuredWebTestClientWithMockMvcTest(
    @Autowired webTestClient: WebTestClient,
    @Autowired objectMapper: ObjectMapper
) {
    init {
        val webTestClientWithBasicAuth = webTestClient.mutate()
            .filter(basicAuthentication("admin", "admin"))
            .build()
        RestAssuredWebTestClient.webTestClient(webTestClientWithBasicAuth)
        RestAssuredWebTestClient.requestSpecification = given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .headers("common_header_key1", "common_header_value1")
        RestAssuredWebTestClient.config = RestAssuredWebTestClient.config().objectMapperConfig(
            ObjectMapperConfig()
                .defaultObjectMapperType(ObjectMapperType.JACKSON_2)
                .jackson2ObjectMapperFactory { _, _ -> objectMapper }
        )
    }

    @Test
    fun testOne() {
        given()
            .`when`()
            .get("/api/owners/1")
            .then()
            .should {
                it.body("firstName", not(nullValue()))
                it.body("pets[0].name", equalTo("Leo"))
                it.body("pets.size()", greaterThan(0))
                it.status(HttpStatus.OK)
                it.extractBodyAs<OwnerDto>().should { dto ->
                    dto.shouldNotBeNull()
                    dto.id shouldBe 1
                }
            }
    }

    @Test
    fun testPost() {
        val newOwner = Fixtures.giveMeOneFreshOwner()

        given()
            .headers("key1", "value1", "key2", "value2")
            .body(newOwner)
            .`when`()
            .post("/api/owners")
            .then()
            .should {
                it.status(HttpStatus.CREATED)
                it.body("firstName", not(nullValue()))
                it.body("pets.size()", equalTo(0))
                it.extractBodyAs<OwnerDto>().should { dto ->
                    dto.lastName shouldBe newOwner.lastName
                    dto.firstName shouldBe newOwner.firstName
                    dto.id shouldNotBe null
                    dto.pets.shouldNotBeNull() shouldHaveSize 0
                }
            }
    }


    @Test
    fun testAll() {
        given()
            .`when`()
            .get("/api/owners")
            .then()
            .should {
                it.status(HttpStatus.OK)
                it.body("[0].firstName", equalTo("George"))
                it.body("findAll { it.firstName == 'George' }.lastName", everyItem(equalTo("Franklin")))
                it.body("firstName", everyItem(not(nullValue())))
                it.body("firstName.size()", greaterThan(0))
                it.body("pets.size()", greaterThan(0))
                it.extractBodyAs<List<OwnerDto>>().should { list ->
                    list shouldNotHaveSize 0
                    list.forAll { owner ->
                        owner.firstName shouldNotBe null
                        owner.pets shouldNotBe  null
                    }
                }
            }
    }


    @Test
    fun testWebFlux() {
        given()
            .`when`()
            .get("/api/owners-webflux/1")
            .then()
            .should {
                it.status(HttpStatus.OK)
                it.extractBodyAs<OwnerDto>().should { dto ->
                    dto.shouldNotBeNull()
                    dto.id shouldBe 1
                }
            }

    }

    @Test
    fun testWebFluxWithExtension() {
        given()
            // 이 부분을 extension 으로 대체
            .whenever()
            .get("/api/owners-webflux/1")
            .then()
            .should {
                it.status(HttpStatus.OK)
                it.extractBodyAs<OwnerDto>().should { dto ->
                    dto.shouldNotBeNull()
                    dto.id shouldBe 1
                }
            }

    }
}
