package org.springframework.samples.petclinic.practice

import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.samples.petclinic.rest.controller.OwnerRestController
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser


// FIXME: WebMvcTest 도 제거
@WebMvcTest(OwnerRestController::class)
class OwnerRestControllerTest(
    @Autowired val ownerRestController: OwnerRestController,
    // FIXME : 정상 동작하도록 clinicService Mock 추가
) {

    @Disabled("수정시 이 어노테이션을 지우시오")
    @Test
    fun listOwners() {
        // FIXME : 아래 두 조건에 맞도록 mock 작성
        ownerRestController.listOwners(null).body
            .shouldNotBeNull() shouldNotHaveSize 0

        ownerRestController.listOwners("noname")
            .shouldNotBeNull()
            .should {
                it.statusCode shouldBe HttpStatus.NOT_FOUND
                it.body.shouldBeNull()
            }
        // FIXME : 행위 검증 코드 작성
    }
}

