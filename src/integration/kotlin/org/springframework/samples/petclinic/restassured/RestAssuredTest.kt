package org.springframework.samples.petclinic.restassured

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.ObjectMapperConfig
import io.restassured.http.ContentType
import io.restassured.mapper.ObjectMapperType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.parsing.Parser
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.samples.petclinic.fixtures.Fixtures
import org.springframework.samples.petclinic.restassured.extensions.extractBodyAs
import org.springframework.samples.petclinic.restassured.extensions.noOp
import org.springframework.samples.petclinic.rest.dto.OwnerDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestAssuredTest(
    @LocalServerPort port: Int,
    @Autowired objectMapper: ObjectMapper
) {
    init {
        RestAssured.baseURI = "http://127.0.0.1"
        RestAssured.port = port
        RestAssured.defaultParser = Parser.JSON
        RestAssured.requestSpecification = RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build()
        RestAssured.config = RestAssured.config().objectMapperConfig(
            ObjectMapperConfig()
                .defaultObjectMapperType(ObjectMapperType.JACKSON_2)
                .jackson2ObjectMapperFactory { _, _ -> objectMapper }
        )

    }

    @Test
    fun testOne() {
        Given {
            auth().basic("admin", "admin")
            headers("key1", "value1", "key2", "value2")
            log().all()
        } When  {
            get("/api/owners/1")
        } Then  {
            statusCode(HttpStatus.OK.value())
            body("firstName", not(nullValue()))
            body("pets[0].name",
                equalTo("Leo"))
            body("pets.size()", greaterThan(0))
            extractBodyAs<OwnerDto>().should { dto ->
                dto.shouldNotBeNull()
                dto.id shouldBe 1
            }
        }
    }

    // 생성 자체는 할 수 있으나, transaction rollback 되지 않음
    @Test
    fun testPost() {
        val newOwner = Fixtures.giveMeOneFreshOwner()
        Given {
            auth().basic("admin", "admin")
            headers("key1", "value1", "key2", "value2")
            body(newOwner)
            log().all()
        } When  {
            post("/api/owners")
        } Then  {
            log().all()
            statusCode(HttpStatus.CREATED.value())
            body("firstName", not(nullValue()))
            body("pets.size()", equalTo(0))

            extractBodyAs<OwnerDto>().should { dto ->
                dto.lastName shouldBe newOwner.lastName
                dto.firstName shouldBe newOwner.firstName
                dto.id shouldNotBe null
                dto.pets.shouldNotBeNull() shouldHaveSize 0
            }
        }
    }

    @Test
    fun testAll() {
        Given {
            auth().basic("admin", "admin")
            log().all()
        } When {
            get("/api/owners")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("[0].firstName", equalTo("George"))
            body("findAll { it.firstName == 'George' }.lastName", everyItem(equalTo("Franklin")))
            body("firstName", everyItem(not(nullValue())))
            body("firstName.size()", greaterThan(0))
            body("pets.size()", greaterThan(0))
            extractBodyAs<List<OwnerDto>>().should {
                it shouldNotHaveSize 0
                it.forAll { owner ->
                    owner.firstName shouldNotBe null
                    owner.pets shouldNotBe  null
                }
            }
            log().all()
        }
    }

    @Test
    fun testWebFlux() {
        Given {
            noOp() // 아무것도 기재할 것이 없으면 이런식으로 처리 가능
            auth().basic("admin", "admin")
            log().all()
        } When {
            get("/api/owners-webflux/1")
        } Then {
            statusCode(HttpStatus.OK.value())
            extractBodyAs<OwnerDto>().shouldNotBeNull()
            log().all()
        }
    }
}

