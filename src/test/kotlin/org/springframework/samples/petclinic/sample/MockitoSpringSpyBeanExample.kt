package org.springframework.samples.petclinic.sample

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.samples.petclinic.rest.controller.OwnerRestController
import org.springframework.samples.petclinic.service.ClinicService
import org.springframework.security.test.context.support.WithMockUser

@SpringBootTest
class MockitoSpringSpyBeanExample(
    @Autowired val ownerRestController: OwnerRestController,
    @Autowired @SpyBean val clinicService: ClinicService
) {
    @WithMockUser(roles = ["OWNER_ADMIN"])
    @Test
    fun listOwners() {
        // 별도의 stub 정의 불필요
        val actual = ownerRestController.listOwners("Black")

        actual.body.shouldNotBeNull().should {
            it.shouldHaveSize(1)
            it[0].lastName shouldBe "Black"
        }

        // 그러나 검증 가능
        verify(clinicService).findOwnerByLastName("Black")
    }
}




