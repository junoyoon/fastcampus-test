package org.springframework.samples.petclinic.practice.optimization_using_annotation

import io.kotest.matchers.collections.shouldNotHaveSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.samples.petclinic.repository.PetRepository
import org.springframework.transaction.annotation.Transactional

// @DataJpaTest 로 변경


@DataJpaTest
class PetRepositoryJpaTest(@Autowired val petRepository: PetRepository) {
    @Test
    fun findAll() {
        petRepository.findAll() shouldNotHaveSize 0
    }
}

