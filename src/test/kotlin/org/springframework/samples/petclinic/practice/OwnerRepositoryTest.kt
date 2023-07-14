package org.springframework.samples.petclinic.practice

import io.kotest.inspectors.forAll
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.samples.petclinic.repository.OwnerRepository

// FIXME : DataJpaTest 로 변경
@SpringBootTest
class OwnerRepositoryTest(
    @Autowired val ownerRepository: OwnerRepository
) {
    @Test
    fun test() {
        val all = ownerRepository.findAll()
        val lastName = all[0].lastName!!
        val actual = ownerRepository.findByLastName(lastName)
        actual.shouldNotBeNull().forAll {
            it.lastName shouldBe lastName
        }
    }
}
