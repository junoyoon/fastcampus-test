package org.springframework.samples.petclinic.webtestclient

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.samples.petclinic.fixtures.Fixtures
import org.springframework.samples.petclinic.rest.dto.OwnerDto
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody


@WithMockUser(roles = ["OWNER_ADMIN"])
@AutoConfigureMockMvc
@SpringBootTest
class WebTestClientWithMockMvcTest(
    @Autowired webTestClient: WebTestClient,
    @Autowired objectMapper: ObjectMapper,
) {

    val webTestClient = webTestClient.mutate()
        // spring에서 디폴트로 사용하는 jackson 사용
        .codecs {
            it.defaultCodecs().apply {
                jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
                jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
            }
        }
        .defaultHeader("default_header", "default_value")
        // 이런식으로 해야지 로그가 남음
        .entityExchangeResultConsumer(System.out::println).build()

    @Test
    fun testOne() {
        webTestClient
            .get()
            .uri("/api/owners/1")
            .header("common_header_key1", "common_header_value1")
            .exchange()
            .should {
                it.expectStatus().isOk()
                it.expectBody() should { body ->
                    body.jsonPath("$.pets[*].name").isEqualTo("Leo")
                }
                it.expectBody<OwnerDto>().value { dto ->
                    dto.shouldNotBeNull()
                    dto.id shouldBe 1
                }
            }
    }

    @Test
    fun testPost() {
        val newOwner = Fixtures.giveMeOneFreshOwner()

        webTestClient.post()
            .uri("/api/owners").bodyValue(newOwner)
            .exchange()
            .should {
                it.expectStatus().isCreated()
                it.expectBody<OwnerDto>().value { dto ->
                    dto.shouldNotBeNull()
                    dto.lastName shouldBe newOwner.lastName
                    dto.firstName shouldBe newOwner.firstName
                    dto.id shouldNotBe null
                    dto.pets.shouldNotBeNull() shouldHaveSize 0
                }
            }
    }

    @Test
    fun testWebFlux() {
        webTestClient
            .get()
            .uri("/api/owners-webflux/1")
            .header("common_header_key1", "common_header_value1")
            .exchange()
            .should {
                it.expectStatus().isOk()
                it.expectBody() should { body ->
                    body.jsonPath("$.pets[*].name").isEqualTo("Leo")
                }
                it.expectBody<OwnerDto>().value { dto ->
                    dto.shouldNotBeNull()
                    dto.id shouldBe 1
                }
            }
    }
}

