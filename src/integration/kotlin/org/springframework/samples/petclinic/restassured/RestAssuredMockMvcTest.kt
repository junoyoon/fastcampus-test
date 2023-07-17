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
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.fixtures.Fixtures
import org.springframework.samples.petclinic.restassured.extensions.extractBodyAs
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional

@Transactional
@WithMockUser(roles = ["OWNER_ADMIN"])
@AutoConfigureMockMvc
@SpringBootTest
class RestAssuredMockMvcTest(
    @Autowired mockMvc: MockMvc,
    @Autowired objectMapper: ObjectMapper
) {
    init {
        RestAssuredMockMvc.mockMvc(mockMvc)
        RestAssuredMockMvc.requestSpecification = Given {
            contentType(ContentType.JSON)
            accept(ContentType.JSON)
            headers("common_header_key1", "common_header_value1")
        }
        RestAssuredMockMvc.config = RestAssuredMockMvc.config().objectMapperConfig(
            ObjectMapperConfig()
                .defaultObjectMapperType(ObjectMapperType.JACKSON_2)
                .jackson2ObjectMapperFactory { _, _ -> objectMapper }
        )
    }

    @Test
    fun testOne() {
        Given {
            contentType(MediaType.APPLICATION_JSON)
            headers("key1", "value1", "key2", "value2")
        } When {
            get("/api/owners/1")
        } Then {
            status(HttpStatus.OK)
            body("pets[0].name", equalTo("Leo"))
            extractBodyAs<OwnerDto>().shouldNotBeNull()
            log().all()
        }
    }

    @Test
    fun testAll() {
        Given {
            contentType(MediaType.APPLICATION_JSON)
        } When {
            get("/api/owners")
        } Then {
            status(HttpStatus.OK)
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

    // mock mvc 는 트랜잭션 가능
    @Transactional
    @Test
    fun testPost() {
        val newOwner = Fixtures.giveMeOneFreshOwner()
        Given {
            headers("key1", "value1", "key2", "value2")
            body(newOwner)
        } When  {
            post("/api/owners")
        } Then  {
            status(HttpStatus.CREATED)
            extractBodyAs<OwnerDto>().should { dto ->
                dto.lastName shouldBe newOwner.lastName
                dto.firstName shouldBe newOwner.firstName
                dto.id shouldNotBe null
                dto.pets.shouldNotBeNull() shouldHaveSize 0
            }
            log().all()
        }
    }

    @Disabled("mockmvc 는 webflux 를 인식하지 못함")
    @Test
    fun testWebFlux() {
        Given {
            contentType(MediaType.APPLICATION_JSON)
        } When {
            get("/api/owners-webflux/1")
        } Then {
            status(HttpStatus.OK)
            body("$.pets[*].name", equalTo("Leo"))
            extractBodyAs<OwnerDto>().shouldNotBeNull()
            log().all()
        }
    }
}
