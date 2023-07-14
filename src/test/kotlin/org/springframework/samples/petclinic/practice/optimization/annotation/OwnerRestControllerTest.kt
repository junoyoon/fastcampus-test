package org.springframework.samples.petclinic.practice.optimization.annotation

import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.rest.controller.OwnerRestController
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser


// FIXME:
//  1. @SpringBootTest 를 @WebMvcTest 로 변경
//  2. @WebMvcTets 까지도 제거해 보기
//
@WithMockUser(roles = ["OWNER_ADMIN"])
@SpringBootTest
class OwnerRestControllerTest(
    @Autowired val ownerRestController: OwnerRestController,
    @Autowired @MockBean val clinicService: ClinicService,
) {
    @Test
    fun listOwners() {
        whenever(clinicService.findAllOwners())
            .thenReturn(listOf(Owner()))
        ownerRestController.listOwners(null).body
            .shouldNotBeNull() shouldNotHaveSize 0
    }
}
