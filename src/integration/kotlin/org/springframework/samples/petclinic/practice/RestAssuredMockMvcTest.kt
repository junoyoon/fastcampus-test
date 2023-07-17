package org.springframework.samples.petclinic.practice

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
import io.restassured.module.mockmvc.kotlin.extensions.Extract
import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.fixtures.Fixtures
import org.springframework.samples.petclinic.restassured.extensions.extractBodyAs
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.samples.petclinic.restassured.extensions.ExtractBodyAs
import org.springframework.samples.petclinic.restassured.extensions.noOp
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
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

    // mock mvc 는 트랜잭션 가능
    @Order(1) // 이게 먼저 수행되어야 함으로 순서 지정
    @Transactional
    @Test
    fun testPostAndGet() {
        // lastName이 이 hello 인 사용자 생성
        val newOwner = Fixtures.giveMeOneFreshOwner()
        var id : Int = 0
        Given {
            body(newOwner)
            log().all()
        } When {
            post("/api/owners")
        } Then {
            status(HttpStatus.CREATED)
            extractBodyAs<OwnerDto>().should { dto ->
                id = dto.id!!
                // ...
            }
        }

        // id 로 가져올때 잘 가져와 지는지 확인
        Given {
            log().all()
        } When {
            get("/api/owners/$id")
        } Then {
            log().all()
            status(HttpStatus.OK)
            extractBodyAs<OwnerDto>().should { dto ->
                dto.lastName shouldBe newOwner.lastName
            }
        }
    }

    @Order(2)
    @Test
    fun testFindByLastNameHello() {
        // lastName이 이 hello 인 사용자 탐색해봄
        val lastName = "hello"
        Given {
            log().all()
        } When {
            get("/api/owners?lastName=$lastName")
        } Then {

        }
    }
}
