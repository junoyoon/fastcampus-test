package org.springframework.samples.petclinic.practice.mocking

import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.rest.controller.OwnerRestController
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser

/**
 * test double 실습
 * - 테스트가 정상 동작 하도록 clinicService stubbing 작성
 * - listOwners 메소드는 null 전달시 clinicService.findAllOwner 호출
 * - 스트링 전달시에는 findOwnerByLastName 호출. 여기서는 noname 으로 호출하였으므로 empty owner list 리턴
 * - 행위 검증 코드도 작성할 것
 */
@WebMvcTest(OwnerRestController::class)
class OwnerRestControllerTest(
    @Autowired val ownerRestController: OwnerRestController,
    @Autowired @MockBean val clinicService: ClinicService,
) {

    @Test
    fun listOwners() {
        // FIXME : 아래 두 조건에 맞도록 mock 작성
        whenever(clinicService.findAllOwners()).thenReturn(listOf(Owner()))
        ownerRestController.listOwners(null).body
            .shouldNotBeNull() shouldNotHaveSize 0

        whenever(clinicService.findOwnerByLastName("noname")).thenReturn(emptyList())
        ownerRestController.listOwners("noname")
            .shouldNotBeNull()
            .should {
                it.statusCode shouldBe HttpStatus.NOT_FOUND
                it.body.shouldBeNull()
            }

        ownerRestController.listOwners("noname")
            .shouldNotBeNull()
            .should {
                it.statusCode shouldBe HttpStatus.NOT_FOUND
                it.body.shouldBeNull()
            }
        // FIXME : 행위 검증 코드 작성
        verify(clinicService).findAllOwners()
        verify(clinicService, times(2)).findOwnerByLastName("noname")
    }
}

