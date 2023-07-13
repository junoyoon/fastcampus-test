package org.springframework.samples.petclinic.sample

import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.samples.petclinic.model.Owner
import org.springframework.samples.petclinic.rest.controller.OwnerRestController
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest
class MockitoSpringMockBeanExample(
    @Autowired val ownerRestController: OwnerRestController,
    @Autowired @MockBean val clinicService: ClinicService
) {
    @WithMockUser(roles = ["OWNER_ADMIN"])
    @Test
    fun listOwners() {
        // mock stub 정의
        whenever(clinicService.findOwnerByLastName("Black"))
            .doReturn(listOf(Owner(lastName = "Black")))

        ownerRestController.listOwners("Black")
            .body.shouldNotBeNull().should {
                it.shouldHaveSize(1)
                it[0].lastName shouldBe "Black"
            }
    }
}





