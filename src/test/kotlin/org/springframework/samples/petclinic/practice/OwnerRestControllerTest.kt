package org.springframework.samples.petclinic.practice

import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.rest.controller.OwnerRestController
import org.springframework.samples.petclinic.service.ClinicService


// FIXME: WebMvcTest 도 제거
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

