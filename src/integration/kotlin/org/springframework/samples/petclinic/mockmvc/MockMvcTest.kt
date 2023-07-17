package org.springframework.samples.petclinic.mockmvc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.samples.petclinic.mockmvc.extentions.andReturnBodyAs
import org.springframework.samples.petclinic.fixtures.Fixtures
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Transactional
@WithMockUser(roles = ["OWNER_ADMIN"])
@AutoConfigureMockMvc
@SpringBootTest
class MockMvcTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
) {
    @Test
    fun test() {
        mockMvc.get("/api/owners/1") {
            accept(MediaType.APPLICATION_JSON)
            header("common_header_key1", "common_header_value1")
            //cookie(Cookie("hello", "World"))
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.pets[*].name", everyItem(equalTo("Leo"))) }
        }
    }


    @Test
    fun testPost() {
        val newOwner = Fixtures.giveMeOneFreshOwner()

        mockMvc.post("/api/owners") {
            content = objectMapper.writeValueAsBytes(newOwner)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content {
                jsonPath("$.lastName", equalTo(newOwner.lastName))
            }
            content { jsonPath("$.pets", empty<Any>())}
        }.andReturn().response.contentAsString.should {
            val dto =
                objectMapper.readValue(it, OwnerDto::class.java)
            dto.lastName shouldBe newOwner.lastName
            dto.firstName shouldBe newOwner.firstName
            dto.id shouldNotBe null
            dto.pets.shouldNotBeNull() shouldHaveSize 0
        }
    }


    @Test
    fun testAll() {
        mockMvc.get("/api/owners") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[*].lastName", everyItem(not(nullValue()))) }
            content { jsonPath("$[0].firstName", not(nullValue())) }
        }.andReturn().response.contentAsString.should {
            val list = objectMapper.readValue(it, object: TypeReference<List<OwnerDto>>() {})
            list shouldNotHaveSize 0
            list.forAll { owner ->
                owner.firstName shouldNotBe null
                owner.pets shouldNotBe  null
            }
        }
    }

    @Test
    fun testAllWithExtensionMethod() {
        mockMvc.get("/api/owners") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[*].lastName", everyItem(not(nullValue()))) }
            content { jsonPath("$[0].firstName", not(nullValue())) }
        }.andReturnBodyAs<List<OwnerDto>>(objectMapper).let {
            it shouldNotHaveSize 0
            it.forAll { owner ->
                owner.firstName shouldNotBe null
                owner.pets shouldNotBe  null
            }
        }
    }
}
